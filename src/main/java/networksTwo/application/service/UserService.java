package networksTwo.application.service;

import networksTwo.domain.model.User;
import networksTwo.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public Optional<Boolean> createUser(User user) {
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public Optional<User> getById(UUID id) {
        return userRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<User> getByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Transactional
    public Optional<Boolean> deleteByUsername(String username) {
        Optional<User> existent = userRepository.findByUsername(username);
        if (existent.isPresent()) {
            return userRepository.delete(existent.get());
        }
        return Optional.empty();
    }
}
