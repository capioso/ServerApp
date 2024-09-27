package networksTwo.application.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DatabaseService {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseService.class);

    @PersistenceContext
    private EntityManager entityManager;

    public Optional<Boolean> checkDatabaseConnection() {
        try {
            entityManager.createNativeQuery("SELECT 1").getSingleResult();
            return Optional.of(true);
        } catch (Exception e) {
            logger.error("Database connection error: {}", e.getMessage());
            return Optional.empty();
        }
    }
}
