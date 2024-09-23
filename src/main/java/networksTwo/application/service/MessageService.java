package networksTwo.application.service;

import jakarta.transaction.Transactional;
import networksTwo.domain.persistence.MessageRepository;
import networksTwo.domain.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageService {
    @Autowired
    private MessageRepository messageRepository;

    @Transactional
    public void createMessage(Message message) {
        messageRepository.save(message);
    }
}
