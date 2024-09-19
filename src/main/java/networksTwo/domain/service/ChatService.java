package networksTwo.domain.service;

import networksTwo.adapter.out.ChatRepository;
import networksTwo.domain.model.Chat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public Chat getChat(String title) throws Exception {
        try {
            return chatRepository.getByTitle(title);
        }catch (Exception e) {
            throw new Exception("Chat not found: " + e.getMessage());
        }
    }


}
