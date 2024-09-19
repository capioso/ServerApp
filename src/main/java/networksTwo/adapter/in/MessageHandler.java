package networksTwo.adapter.in;

import com.fasterxml.jackson.databind.JsonNode;
import networksTwo.domain.model.Chat;
import networksTwo.domain.model.Message;
import networksTwo.domain.model.User;
import networksTwo.domain.service.ChatService;
import networksTwo.domain.service.MessageService;
import networksTwo.domain.service.UserService;

import static networksTwo.adapter.in.ChatHandler.getUserFromToken;
import static networksTwo.utils.SerializerUtils.handleString;

public class MessageHandler {

    public static String handleSendMessage(UserService userService, ChatService chatService, MessageService messageService, JsonNode node) throws Exception {
        String token = node.path("token").asText();
        User owner = getUserFromToken(token, userService);

        String chatTitle = node.path("chatTitle").asText();
        Chat chat = chatService.getChat(chatTitle);

        Message message = new Message();
        message.setSender(owner.getId());
        message.setContent(node.path("content").asText());
        message.setChat(chat);

        chat.getMessages().add(message);
        chatService.updateChat(chat);

        return handleString("message", "Message sent successfully");
    }
}
