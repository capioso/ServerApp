package networksTwo.adapter.in;

import com.fasterxml.jackson.databind.JsonNode;
import networksTwo.domain.model.Chat;
import networksTwo.domain.model.Message;
import networksTwo.domain.model.User;
import networksTwo.domain.service.ChatService;
import networksTwo.domain.service.MessageService;
import networksTwo.domain.service.UserService;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import static networksTwo.adapter.in.ChatHandler.getUserFromToken;
import static networksTwo.domain.service.SessionService.getOutByUserId;
import static networksTwo.utils.SerializerUtils.handleString;

public class MessageHandler {

    public static String handleSendMessage(UserService userService, ChatService chatService, JsonNode node) throws Exception {
        String token = node.path("token").asText();
        User owner = getUserFromToken(token, userService);

        UUID chatId = UUID.fromString(node.path("chatId").asText());
        Chat chat = chatService.getChatById(chatId);

        String content = node.path("content").asText();
        Message message = new Message();
        message.setSender(owner.getId());
        message.setContent(content);
        message.setChat(chat);

        chat.getMessages().add(message);
        chatService.updateChat(chat);

        List<UUID> users = chatService.getReceptorsByChat(chat, owner.getId());
        users.forEach(uuid -> {
            PrintWriter out = getOutByUserId(uuid);
            if (out != null) {
                out.println(handleString("messageUpdate", chat.getId() + "," + owner.getUsername() + "," + content));
            }
        });

        return handleString("message", "Message sent successfully");
    }

    public static String handleGetMessagesByChat(UserService userService, ChatService chatService, JsonNode node) throws Exception {
        String token = node.path("token").asText();
        User owner = getUserFromToken(token, userService);

        UUID chatId = UUID.fromString(node.path("chatId").asText());
        Chat chat = chatService.getChatById(chatId);

        List<String> messagesCleaned = new ArrayList<>();
        chat.getMessages().forEach(message -> {
            String contentInBase64 = Base64.getEncoder().encodeToString(message.getContent().getBytes());
            try {
                User user = userService.getById(message.getSender());
                messagesCleaned.add(message.getId() + "," + user.getUsername() + "," + contentInBase64);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        });
        return handleString("message", messagesCleaned.toString());
    }
}
