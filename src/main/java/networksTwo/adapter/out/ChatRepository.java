package networksTwo.adapter.out;

import networksTwo.domain.model.database.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
    Chat getChatById(UUID chatId);
}
