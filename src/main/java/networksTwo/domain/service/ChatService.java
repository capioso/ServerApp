package networksTwo.domain.service;

import networksTwo.adapter.out.ChatRepository;
import networksTwo.domain.model.database.Chat;
import networksTwo.domain.model.database.User;
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
    public void createChat(Chat chat) {
        chatRepository.save(chat);
    }

    @Transactional
    public void updateChat(Chat chat) {
        chatRepository.save(chat);
    }

    @Transactional
    public Chat getChatById(UUID chatId) {
        return chatRepository.getChatById(chatId);
    }

    @Transactional
    public List<UUID> getReceptorsByChat(Chat chat, UUID sender) {
        return chat.getUsers().stream()
                .map(User::getId)
                .filter(id -> !id.equals(sender))
                .toList();
    }
}
