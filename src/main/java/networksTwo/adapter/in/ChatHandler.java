package networksTwo.adapter.in;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.JsonNode;
import networksTwo.domain.model.Chat;
import networksTwo.domain.model.User;
import networksTwo.domain.service.ChatService;
import networksTwo.domain.service.UserService;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static networksTwo.domain.service.SessionService.getOutByUserId;
import static networksTwo.utils.JwtUtils.validateToken;
import static networksTwo.utils.SerializerUtils.handleString;

public class ChatHandler {

    public static User getUserFromToken(String token, UserService userService) throws Exception {
        DecodedJWT decodedJWT = validateToken(token);
        UUID id = UUID.fromString(decodedJWT.getSubject());
        return userService.getById(id);
    }

    public static String handleCreateChat(UserService userService, ChatService chatService, JsonNode node) throws Exception {
        String token = node.path("token").asText();
        User owner = getUserFromToken(token, userService);

        String username = node.path("username").asText();
        if (username.equals(owner.getUsername())) {
            throw new Exception("You can not create a chat with yourself.");
        }

        UUID chatId = UUID.fromString(node.path("chatId").asText());
        Chat existentChat = chatService.getChatById(chatId);

        User user = userService.getByUsername(username);
        PrintWriter out = getOutByUserId(user.getId());

        if (existentChat == null) {
            Chat chat = new Chat();
            chat.setId(chatId);
            chat.setOwner(owner);
            chat.getUsers().add(user);
            chat.getUsers().add(owner);
            chatService.createChat(chat);
        }else{
            if (existentChat.getUsers().contains(user)) {
                throw new Exception("User already in chat.");
            }
            existentChat.getUsers().add(user);
            chatService.updateChat(existentChat);
        }

        if (out != null) {
            out.println(handleString("chatUpdate", chatId.toString()));
        }

        return handleString("message", "Chat created successfully");
    }

    public static String handleGetChats(UserService userService, JsonNode node) throws Exception {
        String token = node.path("token").asText();
        User owner = getUserFromToken(token, userService);

        List<Chat> chats = owner.getChats();
        List<UUID> chatIds = chats.stream()
                .map(Chat::getId)
                .toList();

        return handleString("message", chatIds.toString());
    }

    public static String handleGetSingleChat(UserService userService, ChatService chatService, JsonNode node) throws Exception {
        String token = node.path("token").asText();
        User owner = getUserFromToken(token, userService);

        UUID chatId = UUID.fromString(node.path("chatId").asText());
        Chat chat = chatService.getChatById(chatId);

        List<String> filteredUsers = chat.getUsers().stream()
                .map(User::getUsername)
                .filter(username -> !username.equals(owner.getUsername()))
                .toList();

        String title;
        if (filteredUsers.size() == 1){
            title = filteredUsers.getFirst();
        }else{
            title = "Group: " + String.join(", ", filteredUsers);
        }
        return handleString("message", title);
    }
}
