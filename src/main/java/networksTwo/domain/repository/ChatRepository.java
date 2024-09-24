package networksTwo.domain.repository;

import networksTwo.domain.model.Chat;

import java.util.Optional;
import java.util.UUID;


public interface ChatRepository {
    Optional<Chat> findById(UUID id);
    void save(Chat chat);
}
