package networksTwo.application.handler;

import com.fasterxml.jackson.databind.JsonNode;
import networksTwo.application.service.ChatService;
import networksTwo.domain.dto.ChatDto;
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
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

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
        User owner = getOwner(node);

        String username = node.path("username").asText();
        if (username.equals(owner.getUsername())) {
            throw new ExpressionException("You can not create a chat with yourself.");
        }

        User user = userService.getByUsername(username).orElseThrow(() -> new ExpressionException("User not found."));

        UUID chatId = UUID.fromString(node.path("chatId").asText());

        Optional<Chat> existentChatOptional = chatService.getById(chatId);
        AtomicReference<String> messageResponse = new AtomicReference<>("");

        existentChatOptional.ifPresentOrElse(
                existentChat -> {
                    if (existentChat.getUsers().contains(user)) {
                        throw new IllegalArgumentException("User already in chat.");
                    }

                    String newTitle = node.path("title").asText();
                    existentChat.setTitle(newTitle);
                    existentChat.getUsers().add(user);

                    chatService.updateChat(existentChat)
                            .orElseThrow(() -> new RuntimeException("Chat not updated."));

                    List<UUID> users = chatService.getReceptorsByChatWithSender(existentChat)
                            .orElseThrow(() -> new RuntimeException("Users not found"));

                    users.forEach(userInChat -> getOutByUserId(userInChat)
                            .ifPresent(outputStream -> {
                                        try {
                                            Response otherClient = new Response(
                                                    "groupUpdate",
                                                    new ChatDto(chatId, getTitleFiltered(existentChat, user.getUsername()), true)
                                            );
                                            byte[] responseBytes = MessagePackUtils.getInstance().writeValueAsBytes(otherClient);
                                            outputStream.write(responseBytes);
                                            outputStream.flush();
                                        } catch (Exception e) {
                                            throw new RuntimeException("Failed to send response to user: {}", e.getCause());
                                        }
                                    }
                            ));

                    messageResponse.set("Chat updated successfully");
                },
                () -> {
                    Chat chat = new Chat();
                    chat.setId(chatId);
                    chat.setOwner(owner);
                    chat.getUsers().add(user);
                    chat.getUsers().add(owner);
                    chatService.createChat(chat)
                            .orElseThrow(() -> new RuntimeException("Chat not created."));

                    getOutByUserId(user.getId())
                            .ifPresent(outputStream -> {
                                        try {
                                            Chat registeredChat = chatService.getById(chatId)
                                                    .orElseThrow(() -> new RuntimeException("Chat not found."));
                                            Response otherClient = new Response(
                                                    "chatUpdate",
                                                    new ChatDto(chatId, getTitleFiltered(registeredChat, user.getUsername()), false)
                                            );
                                            byte[] responseBytes = MessagePackUtils.getInstance().writeValueAsBytes(otherClient);
                                            outputStream.write(responseBytes);
                                            outputStream.flush();
                                        } catch (Exception e) {
                                            throw new RuntimeException("Failed to send response to user: {}", e.getCause());
                                        }
                                    }
                            );
                    messageResponse.set("Chat created successfully");
                }
        );
        return messageResponse.get();
    }

    public List<ChatDto> handleGetChats(JsonNode node) throws Exception {
        User owner = getOwner(node);

        return owner.getChats().stream()
                .map(chat -> {
                    String title = getTitleFiltered(chat, owner.getUsername());
                    return new ChatDto(chat.getId(), title, chatService.isGroupChat(chat, owner.getUsername()));
                })
                .collect(Collectors.toList());
    }

    public String handleGetSingleChat(JsonNode node) throws Exception {
        User owner = getOwner(node);

        UUID chatId = UUID.fromString(node.path("chatId").asText());

        Chat chat = chatService.getById(chatId)
                .orElseThrow(() -> new RuntimeException("Chat not found with id: " + chatId));

        return getTitleFiltered(chat, owner.getUsername());
    }

    private User getOwner(JsonNode node) {
        String token = node.path("token").asText();
        return getUserFromToken(token, userService)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private String getTitleFiltered(Chat chat, String ownerUsername) {
        List<String> filteredUsers = chatService.getTitlesByChatWithoutOwner(chat, ownerUsername)
                .orElseThrow(() -> new RuntimeException("Filtered Users by chat not executed."));

        String title;
        if (filteredUsers.size() == 1) {
            title = filteredUsers.getFirst();
        } else {
            title = chat.getTitle();
        }

        return title;
    }

    private String getTitle(Chat chat) {
        List<String> filteredUsers = chatService.getTitlesByChat(chat)
                .orElseThrow(() -> new RuntimeException("Get Users by chat not executed."));

        String title;
        if (filteredUsers.size() == 1) {
            title = filteredUsers.getFirst();
        } else {
            title = chat.getTitle();
        }

        return title;
    }
}
