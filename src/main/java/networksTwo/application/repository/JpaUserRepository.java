package networksTwo.application.repository;

import networksTwo.adapter.repository.SpringUserRepository;
import networksTwo.domain.model.User;
import networksTwo.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaUserRepository implements UserRepository {
    private final SpringUserRepository springUserRepository;

    @Autowired
    public JpaUserRepository(SpringUserRepository springUserRepository) {
        this.springUserRepository = springUserRepository;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return springUserRepository.findByUsername(username);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return springUserRepository.findById(id);
    }

    @Override
    public void save(User user) {
        springUserRepository.save(user);
    }

    @Override
    public void delete(User user) {
        springUserRepository.delete(user);
    }
}
