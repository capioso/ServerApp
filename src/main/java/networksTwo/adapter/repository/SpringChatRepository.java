package networksTwo.adapter.repository;

import networksTwo.domain.model.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpringChatRepository extends JpaRepository<Chat, UUID> {
}
