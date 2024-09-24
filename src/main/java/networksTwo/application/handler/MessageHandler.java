package networksTwo.application.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import networksTwo.domain.dto.MessageDto;
import networksTwo.domain.model.Chat;
import networksTwo.domain.model.Message;
import networksTwo.domain.model.Response;
import networksTwo.domain.model.User;
import networksTwo.application.service.ChatService;
import networksTwo.application.service.UserService;
import networksTwo.utils.MessagePackUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static networksTwo.application.service.SessionService.getOutByUserId;
import static networksTwo.utils.JwtUtils.getUserFromToken;

@Service
public class MessageHandler {

    private final UserService userService;
    private final ChatService chatService;

    @Autowired
    public MessageHandler(UserService userService, ChatService chatService) {
        this.userService = userService;
        this.chatService = chatService;
    }

    public UUID handleSendMessage(JsonNode node) throws Exception {
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
        chatService.updateChat(chat)
                .orElseThrow(() -> new RuntimeException("Chat not updated"));

        List<UUID> users = chatService.getReceptorsByChat(chat, owner.getId())
                .orElseThrow(() -> new RuntimeException("No receivers found for the chat."));

        users.forEach(uuid -> getOutByUserId(uuid).ifPresent(
                out -> {
                    ObjectNode updateNode = MessagePackUtils.getInstance().createObjectNode();

                    updateNode.put("chatId", chat.getId().toString());
                    updateNode.put("messageId", messageId.toString());
                    updateNode.put("username", owner.getUsername());
                    updateNode.put("content", content);

                    try {
                        Response otherClient = new Response("messageUpdate", updateNode.toString());
                        byte[] responseBytes = MessagePackUtils.getInstance().writeValueAsBytes(otherClient);
                        out.write(responseBytes);
                        out.flush();
                    }catch (Exception e) {
                        throw new RuntimeException("Failed to send response to user: {}", e.getCause());
                    }
                }
        ));

        return messageId;
    }

    public List<MessageDto> handleGetMessagesByChat(JsonNode node) throws Exception {
        String token = node.path("token").asText();
        getUserFromToken(token, userService)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UUID chatId = UUID.fromString(node.path("chatId").asText());

        Chat chat = chatService.getById(chatId).orElseThrow(() ->
                new RuntimeException("Chat not found with id: " + chatId)
        );

        return chat.getMessages().stream()
                .map(this::convertToMessageDto)
                .collect(Collectors.toList());
    }

    private MessageDto convertToMessageDto(Message message) {
        try {
            User user = userService.getById(message.getSender())
                    .orElseThrow(() -> new RuntimeException("No user found with given id"));

            return new MessageDto(
                    message.getId(),
                    user.getUsername(),
                    message.getContent()
            );
        } catch (Exception e) {
            throw new RuntimeException("Error processing message with id " + message.getId() + ": " + e.getMessage(), e);
        }
    }
}
