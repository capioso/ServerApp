package networksTwo;

import jakarta.annotation.PostConstruct;
import networksTwo.domain.service.DatabaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AppInitializer {

    private static final Logger logger = LoggerFactory.getLogger(AppInitializer.class);
    private final DatabaseService databaseService;

    @Autowired
    public AppInitializer(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    @PostConstruct
    public void onInit() {
        try {
            if (!databaseService.checkDatabaseConnection()) {
                throw new Exception("Could not connect to database.");
            }
            logger.info("Connected to database.");
        } catch (Exception e) {
            logger.error("Error connecting to DB: " + e.getMessage());
        }
    }
}