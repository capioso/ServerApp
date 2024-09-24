package networksTwo.application.handler;

import com.fasterxml.jackson.databind.JsonNode;
import networksTwo.application.service.ChatService;
import networksTwo.domain.model.Chat;
import networksTwo.domain.model.User;
import networksTwo.application.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ExpressionException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static networksTwo.application.service.SessionService.getOutByUserId;
import static networksTwo.utils.JwtUtils.getUserFromToken;
import static networksTwo.utils.SerializerUtils.handleString;

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
                .ifPresent(printWriter ->
                        printWriter.println(handleString("chatUpdate", chatId.toString()))
                );

        return handleString("message", "Chat created successfully");
    }

    public String handleGetChats(JsonNode node) throws Exception {
        String token = node.path("token").asText();
        User owner = getUserFromToken(token, userService)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<UUID> chatIds = owner.getChats().stream()
                .map(Chat::getId)
                .toList();

        return handleString("message", chatIds.toString());
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

        return handleString("message", title);
    }
}
