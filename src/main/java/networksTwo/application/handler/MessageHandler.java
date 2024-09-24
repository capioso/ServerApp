package networksTwo.application.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import networksTwo.domain.model.Chat;
import networksTwo.domain.model.Message;
import networksTwo.domain.model.User;
import networksTwo.application.service.ChatService;
import networksTwo.application.service.UserService;
import networksTwo.utils.ObjectMapperUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static networksTwo.application.service.SessionService.getOutByUserId;
import static networksTwo.utils.JwtUtils.getUserFromToken;
import static networksTwo.utils.SerializerUtils.handleString;

@Service
public class MessageHandler {

    private final UserService userService;
    private final ChatService chatService;

    @Autowired
    public MessageHandler(UserService userService, ChatService chatService) {
        this.userService = userService;
        this.chatService = chatService;
    }

    public String handleSendMessage(JsonNode node) throws Exception {
        String token = node.path("token").asText();
        User owner = getUserFromToken(token, userService)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UUID chatId = UUID.fromString(node.path("chatId").asText());

        Chat chat = chatService.getById(chatId).orElseThrow(() ->
                new RuntimeException("Chat not found with id: " + chatId)
        );

        String content = node.path("content").asText();
        UUID messageId = UUID.randomUUID();
        Message message = new Message();
        message.setId(messageId);
        message.setSender(owner.getId());
        message.setContent(content);
        message.setChat(chat);

        chat.getMessages().add(message);
        chatService.updateChat(chat);

        List<UUID> users = chatService.getReceptorsByChat(chat, owner.getId())
                .orElseThrow(() -> new RuntimeException("No receivers found for the chat."));

        users.forEach(uuid -> getOutByUserId(uuid)
                .ifPresentOrElse(
                        out -> {
                            ObjectNode updateNode = ObjectMapperUtils.getInstance().createObjectNode();

                            updateNode.put("chatId", chat.getId().toString());
                            updateNode.put("messageId", messageId.toString());
                            updateNode.put("username", owner.getUsername());
                            updateNode.put("content", content);

                            out.println(handleString("messageUpdate", updateNode.toString()));
                        },
                        () -> System.out.println("No PrintWriter found for userId: " + uuid)
                ));


        return handleString("message", String.valueOf(messageId));
    }

    public String handleGetMessagesByChat(JsonNode node) throws Exception {
        String token = node.path("token").asText();
        getUserFromToken(token, userService);

        UUID chatId = UUID.fromString(node.path("chatId").asText());
        Optional<Chat> optionalChat = chatService.getById(chatId);

        Chat chat = optionalChat.orElseThrow(() ->
                new RuntimeException("Chat not found with id: " + chatId)
        );

        ArrayNode messagesArray = ObjectMapperUtils.getInstance().createArrayNode();

        for (Message message : chat.getMessages()) {
            try {
                User user = userService.getById(message.getSender())
                        .orElseThrow(() -> new RuntimeException("No user found with given id"));

                ObjectNode messageNode = ObjectMapperUtils.getInstance().createObjectNode();
                messageNode.put("id", message.getId().toString());
                messageNode.put("sender", user.getUsername());
                messageNode.put("content", message.getContent());

                messagesArray.add(messageNode);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        ObjectNode resultNode = ObjectMapperUtils.getInstance().createObjectNode();
        resultNode.set("messages", messagesArray);

        return handleString("message", resultNode.toString());
    }
}
