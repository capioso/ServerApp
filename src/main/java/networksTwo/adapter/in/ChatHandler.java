package networksTwo.adapter.in;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.JsonNode;
import networksTwo.domain.model.Chat;
import networksTwo.domain.model.User;
import networksTwo.domain.service.ChatService;
import networksTwo.domain.service.UserService;

import java.util.List;
import java.util.UUID;

import static networksTwo.utils.JwtUtils.validateToken;
import static networksTwo.utils.SerializerUtils.handleString;

public class ChatHandler {
    public static String handleCreateChat(UserService userService, ChatService chatService, JsonNode node) throws Exception {
        String token = node.path("token").asText();
        DecodedJWT decodedJWT = validateToken(token);
        UUID id = UUID.fromString(decodedJWT.getSubject());
        String username = node.path("username").asText();
        User user = userService.getByUsername(username);
        User owner = userService.getById(id);
        String title = node.path("title").asText();
        Chat existentChat = chatService.getChat(title);
        if (existentChat == null) {
            Chat chat = new Chat();
            chat.setOwner(owner);
            chat.setTitle(title);
            chat.getUsers().add(user);
            chat.getUsers().add(owner);
            chatService.createChat(chat);
        }else{
            existentChat.getUsers().add(user);
            chatService.updateChat(existentChat);
        }
        return handleString("message", "Chat created successfully");
    }

    public static String handleGetChat(UserService userService, JsonNode node) throws Exception {
        String token = node.path("token").asText();
        DecodedJWT decodedJWT = validateToken(token);
        UUID id = UUID.fromString(decodedJWT.getSubject());
        User owner = userService.getById(id);

        return owner.getChats().toString();
    }
}
