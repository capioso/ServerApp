package networksTwo.domain.service;

import networksTwo.domain.model.User;
import networksTwo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public void createUser(User user) throws Exception {
        try {
            userRepository.save(user);
        }catch(Exception e) {
            throw new Exception("User not created: " + e.getMessage());
        }
    }

    public void deleteByUsername(String username) throws Exception {
        try {
            User existent = userRepository.findByUsername(username);
            if (existent != null) {
                userRepository.delete(existent);
            }else {
                throw new Exception("User with username >" + username + "<, do not exist.");
            }
        } catch (Exception e) {
            throw new Exception("User deletion failed: " + e.getMessage());
        }
    }
}
