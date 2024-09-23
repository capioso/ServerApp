package networksTwo.domain.persistence;

import networksTwo.domain.model.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
    Chat getChatById(UUID chatId);
}
