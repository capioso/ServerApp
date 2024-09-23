package networksTwo.adapter.in;

import com.fasterxml.jackson.databind.JsonNode;
import networksTwo.domain.model.database.Chat;
import networksTwo.domain.model.database.User;
import networksTwo.domain.service.ChatService;
import networksTwo.domain.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.util.List;
import java.util.UUID;

import static networksTwo.domain.service.SessionService.getOutByUserId;
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

    public String handleGetChats(JsonNode node) throws Exception {
        String token = node.path("token").asText();
        User owner = getUserFromToken(token, userService);

        List<Chat> chats = owner.getChats();
        List<UUID> chatIds = chats.stream()
                .map(Chat::getId)
                .toList();

        return handleString("message", chatIds.toString());
    }

    public String handleGetSingleChat(JsonNode node) throws Exception {
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
