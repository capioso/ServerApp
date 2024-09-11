package networksTwo;

import jakarta.annotation.PostConstruct;
import networksTwo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AppInitializer {
    @Autowired
    private UserService userService;

    @PostConstruct
    public void onInit() {
        try {
            if (userService.checkDatabaseConnection()) {
                System.out.println("User service ready");
            } else {
                System.out.println("User service not ready");
            }
        } catch (Exception e) {
            System.out.println("Error connecting to DB: " + e.getMessage());
        }
    }
}
