package networksTwo.domain.repository;

import networksTwo.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    Optional<User> findByUsername(String username);
    Optional<User> findById(UUID id);
    Optional<Boolean> save(User user);
    Optional<Boolean> delete(User user);
}