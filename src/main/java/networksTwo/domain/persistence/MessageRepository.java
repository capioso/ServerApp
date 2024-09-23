package networksTwo.domain.persistence;

import networksTwo.domain.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> getAllByChatId(UUID chatId);
}
