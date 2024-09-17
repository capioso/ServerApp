package networksTwo.domain.service;

import networksTwo.adapter.out.ChatRepository;
import networksTwo.domain.model.Chat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChatService {

    @Autowired
    private ChatRepository chatRepository;

    public void createChat(Chat chat) throws Exception {
        try {
            chatRepository.save(chat);
        } catch (Exception e) {
            throw new Exception("Chat not created: " + e.getMessage());
        }
    }

    public void updateChat(Chat chat) throws Exception {
        try {
            chatRepository.save(chat);
        } catch (Exception e) {
            throw new Exception("Chat not updated: " + e.getMessage());
        }
    }

    public Chat getChat(String title) throws Exception {
        try {
            return chatRepository.getByTitle(title);
        }catch (Exception e) {
            throw new Exception("Chat not found: " + e.getMessage());
        }
    }
}
