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
        try {
            return springUserRepository.findByUsername(username);
        }catch (Exception e){
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> findById(UUID id) {
        try {
            return springUserRepository.findById(id);
        }catch (Exception e){
            return Optional.empty();
        }
    }

    @Override
    public Optional<Boolean> save(User user) {
        try {
            springUserRepository.save(user);
            return Optional.of(true);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Boolean> delete(User user) {
        try {
            springUserRepository.delete(user);
            return Optional.of(true);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
