package networksTwo.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import networksTwo.model.User;
import networksTwo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public void createUser(User user) {
        userRepository.save(user);
    }

    public boolean checkDatabaseConnection() {
        try {
            entityManager.createNativeQuery("SELECT 1").getSingleResult();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
