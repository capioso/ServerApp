package networksTwo.domain.service;

import networksTwo.domain.model.User;
import networksTwo.adapter.out.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public void createUser(User user) throws Exception {
        try {
            userRepository.save(user);
        } catch (Exception e) {
            throw new Exception("User not created: " + e.getMessage());
        }
    }

    @Transactional
    public User getById(UUID id) throws Exception {
        try {
            User user = userRepository.findById(id);
            if (user == null) {
                throw new Exception("User with id " + id + " not found");
            }
            user.getChats().size();
            return user;
        } catch (Exception e) {
            throw new Exception("User not found by id: " + e.getMessage());
        }
    }

    @Transactional
    public User getByUsername(String username) throws Exception {
        try {
            User user = userRepository.findByUsername(username);
            if (user == null) {
                throw new Exception("User with username " + username + " not found");
            }
            user.getChats().size();
            return user;
        } catch (Exception e) {
            throw new Exception("User not found by username: " + e.getMessage());
        }
    }

    @Transactional
    public void deleteByUsername(String username) throws Exception {
        try {
            User existent = userRepository.findByUsername(username);
            if (existent != null) {
                userRepository.delete(existent);
            } else {
                throw new Exception("User with username >" + username + "<, do not exist.");
            }
        } catch (Exception e) {
            throw new Exception("User deletion failed: " + e.getMessage());
        }
    }
}
