package networksTwo.domain.service;

import jakarta.transaction.Transactional;
import networksTwo.adapter.out.MessageRepository;
import networksTwo.domain.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageService {
    @Autowired
    private MessageRepository messageRepository;

    @Transactional
    public void createMessage(Message message) throws Exception {
        try {
            messageRepository.save(message);
        }catch (Exception e) {
            throw new Exception("Message not created: " + e.getMessage());
        }
    }
}
