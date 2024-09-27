package networksTwo;

import jakarta.annotation.PostConstruct;
import networksTwo.application.service.DatabaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AppInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppInitializer.class);
    private final DatabaseService databaseService;

    @Autowired
    public AppInitializer(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    @PostConstruct
    public void onInit() {
        try {
            Optional<Boolean> isConnected = databaseService.checkDatabaseConnection();
            if (isConnected.isEmpty() || !isConnected.get()) {
                throw new Exception("Could not connect to database.");
            }
            LOGGER.info("Connected to database.");
        } catch (Exception e) {
            LOGGER.error("Error connecting to DB: {}", e.getMessage());
        }
    }
}