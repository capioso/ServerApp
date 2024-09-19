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
        User user = userService.getByUsername(username);

        if (user.getUsername().equals(owner.getUsername())) {
            throw new Exception("You can not add yourself.");
        }

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
            if (existentChat.getUsers().contains(user)) {
                throw new Exception("User already in chat.");
            }
            existentChat.getUsers().add(user);
            chatService.updateChat(existentChat);
        }
        PrintWriter out = getOutByUserId(user.getId());
        if (out != null) {
            out.println(handleString("update", "Chat created"));
        }
        return handleString("message", "Chat created successfully");
    }

    public static String handleGetChat(UserService userService, JsonNode node) throws Exception {
        String token = node.path("token").asText();
        User owner = getUserFromToken(token, userService);

        List<Chat> chats = owner.getChats();
        List<String> titles = new ArrayList<>();
        for (Chat chat : chats) {
            List<User> usersInChat = chat.getUsers();
            if (usersInChat.size() == 2) {
                for (User user : usersInChat) {
                    if (!user.getUsername().equals(owner.getUsername())) {
                        titles.add(user.getUsername());
                    }
                }
            }else{
                titles.add(chat.getTitle());
            }
        }

        return handleString("message", titles.toString());
    }
}
