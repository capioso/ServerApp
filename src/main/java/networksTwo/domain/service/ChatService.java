package networksTwo.domain.service;

import networksTwo.adapter.out.ChatRepository;
import networksTwo.domain.model.Chat;
import networksTwo.domain.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ChatService {

    @Autowired
    private ChatRepository chatRepository;

    @Transactional
    public void createChat(Chat chat) throws Exception {
        try {
            chatRepository.save(chat);
        } catch (Exception e) {
            throw new Exception("Chat not created: " + e.getMessage());
        }
    }

    @Transactional
    public void updateChat(Chat chat) throws Exception {
        try {
            chatRepository.save(chat);
        } catch (Exception e) {
            throw new Exception("Chat not updated: " + e.getMessage());
        }
    }

    @Transactional
    public Chat getChatById(UUID chatId) throws Exception {
        try {
            return chatRepository.getChatById(chatId);
        }catch (Exception e) {
            throw new Exception("Chat not found: " + e.getMessage());
        }
    }

    @Transactional
    public List<UUID> getReceptorsByChat(Chat chat, UUID sender) throws Exception {
        try {
            return chat.getUsers().stream()
                    .map(User::getId)
                    .filter(id -> !id.equals(sender))
                    .toList();
        }catch (Exception e) {
            throw new Exception("Users by chat not retrieved: " + e.getMessage());
        }
    }


}
