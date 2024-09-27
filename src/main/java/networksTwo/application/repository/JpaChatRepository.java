package networksTwo.application.repository;

import networksTwo.adapter.repository.SpringChatRepository;
import networksTwo.domain.model.Chat;
import networksTwo.domain.repository.ChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaChatRepository implements ChatRepository {

    private final SpringChatRepository springChatRepository;

    @Autowired
    public JpaChatRepository(SpringChatRepository springChatRepository) {
        this.springChatRepository = springChatRepository;
    }

    @Override
    public Optional<Chat> findById(UUID id) {
        try {
            return springChatRepository.findById(id);
        }catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Boolean> save(Chat chat) {
        try {
            springChatRepository.save(chat);
            return Optional.of(true);
        }catch (Exception e) {
            return Optional.empty();
        }
    }
}

