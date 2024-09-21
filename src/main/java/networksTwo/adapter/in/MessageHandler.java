package networksTwo.adapter.in;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
        UUID messageId = UUID.randomUUID();
        Message message = new Message();
        message.setId(messageId);
        message.setSender(owner.getId());
        message.setContent(content);
        message.setChat(chat);

        chat.getMessages().add(message);
        chatService.updateChat(chat);

        List<UUID> users = chatService.getReceptorsByChat(chat, owner.getId());
        users.forEach(uuid -> {
            PrintWriter out = getOutByUserId(uuid);
            if (out != null) {
                ObjectMapper objectMapper = new ObjectMapper();
                ObjectNode updateNode = objectMapper.createObjectNode();

                updateNode.put("chatId", chat.getId().toString());
                updateNode.put("messageId", messageId.toString());
                updateNode.put("username", owner.getUsername());
                updateNode.put("content", content);
                out.println(handleString("messageUpdate", updateNode.toString()));
            }
        });

        return handleString("message", String.valueOf(messageId));
    }

    public static String handleGetMessagesByChat(UserService userService, ChatService chatService, JsonNode node) throws Exception {
        String token = node.path("token").asText();
        User owner = getUserFromToken(token, userService);

        UUID chatId = UUID.fromString(node.path("chatId").asText());
        Chat chat = chatService.getChatById(chatId);

        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode messagesArray = objectMapper.createArrayNode();

        for (Message message : chat.getMessages()) {
            try {
                User user = userService.getById(message.getSender());

                ObjectNode messageNode = objectMapper.createObjectNode();
                messageNode.put("id", message.getId().toString());
                messageNode.put("sender", user.getUsername());
                messageNode.put("content", message.getContent());

                messagesArray.add(messageNode);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        ObjectNode resultNode = objectMapper.createObjectNode();
        resultNode.set("messages", messagesArray);

        return handleString("message", resultNode.toString());
    }
}
