package networksTwo.application.handler;

import com.fasterxml.jackson.databind.JsonNode;
import networksTwo.application.service.ChatService;
import networksTwo.domain.model.Chat;
import networksTwo.domain.model.Response;
import networksTwo.domain.model.User;
import networksTwo.application.service.UserService;
import networksTwo.utils.MessagePackUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ExpressionException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static networksTwo.application.service.SessionService.getOutByUserId;
import static networksTwo.utils.JwtUtils.getUserFromToken;

@Service
public class ChatHandler {

    private final UserService userService;
    private final ChatService chatService;

    @Autowired
    public ChatHandler(UserService userService, ChatService chatService) {
        this.userService = userService;
        this.chatService = chatService;
    }

    public String handleCreateChat(JsonNode node) throws Exception {
        String token = node.path("token").asText();
        User owner = getUserFromToken(token, userService)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String username = node.path("username").asText();
        if (username.equals(owner.getUsername())) {
            throw new ExpressionException("You can not create a chat with yourself.");
        }

        User user = userService.getByUsername(username).orElseThrow(() -> new ExpressionException("User not found."));

        UUID chatId = UUID.fromString(node.path("chatId").asText());

        Optional<Chat> existentChatOptional = chatService.getById(chatId);

        existentChatOptional.ifPresentOrElse(
                existentChat -> {
                    if (existentChat.getUsers().contains(user)) {
                        throw new IllegalArgumentException("User already in chat.");
                    }
                    existentChat.getUsers().add(user);
                    chatService.updateChat(existentChat)
                            .orElseThrow(() -> new RuntimeException("Chat not updated."));
                },
                () -> {
                    Chat chat = new Chat();
                    chat.setId(chatId);
                    chat.setOwner(owner);
                    chat.getUsers().add(user);
                    chat.getUsers().add(owner);
                    chatService.createChat(chat)
                            .orElseThrow(() -> new RuntimeException("Chat not created."));
                }
        );

        getOutByUserId(user.getId())
                .ifPresent(outputStream -> {
                            try {
                                Response otherClient = new Response("chatUpdate", chatId.toString());
                                byte[] responseBytes = MessagePackUtils.getInstance().writeValueAsBytes(otherClient);
                                outputStream.write(responseBytes);
                                outputStream.flush();
                            }catch (Exception e) {
                                throw new RuntimeException("Failed to send response to user: {}", e.getCause());
                            }
                        }
                );

        return "Chat created successfully";
    }

    public List<UUID> handleGetChats(JsonNode node) throws Exception {
        String token = node.path("token").asText();
        User owner = getUserFromToken(token, userService)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return owner.getChats().stream()
                .map(Chat::getId)
                .toList();
    }

    public String handleGetSingleChat(JsonNode node) throws Exception {
        String token = node.path("token").asText();
        User owner = getUserFromToken(token, userService)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UUID chatId = UUID.fromString(node.path("chatId").asText());

        Chat chat = chatService.getById(chatId)
                .orElseThrow(() -> new RuntimeException("Chat not found with id: " + chatId));

        List<String> filteredUsers = chat.getUsers().stream()
                .map(User::getUsername)
                .filter(username -> !username.equals(owner.getUsername()))
                .toList();

        String title;
        if (filteredUsers.size() == 1) {
            title = filteredUsers.getFirst();
        } else {
            title = "Group: " + String.join(", ", filteredUsers);
        }

        return title;
    }
}
