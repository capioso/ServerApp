package networksTwo.application.handler;

import com.fasterxml.jackson.databind.JsonNode;
import networksTwo.application.service.UserService;
import networksTwo.domain.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

import static networksTwo.application.service.SessionService.setUserIdBySessionId;
import static networksTwo.utils.JwtUtils.generateToken;
import static networksTwo.utils.JwtUtils.validateToken;
import static networksTwo.utils.PasswordUtils.checkPassword;
import static networksTwo.utils.PasswordUtils.hashPassword;

@Service
public class UserHandler {
    private final UserService userService;

    @Autowired
    public UserHandler(UserService userService) {
        this.userService = userService;
    }

    public Optional<UserService> getUserService() {
        return Optional.ofNullable(userService);
    }

    public String handleCreateUser(JsonNode node) throws Exception {
        String username = node.path("username").asText();
        String email = node.path("email").asText();
        String password = node.path("password").asText();

        String hashedPassword = hashPassword(password)
                .orElseThrow(() -> new RuntimeException("Password hashing failed"));

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setPassword(hashedPassword);

        userService.createUser(newUser)
                .filter(created -> created)
                .orElseThrow(() -> new RuntimeException("User creation failed"));

        return "User created successfully";
    }

    public String handleLogInUser(JsonNode node, UUID sessionId) throws Exception {
        String username = node.path("username").asText();
        String password = node.path("password").asText();

        User user = userService.getByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        checkPassword(password, user.getPassword())
                .filter(valid -> valid)
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        String token = generateToken(user.getId())
                .orElseThrow(() -> new RuntimeException("Token not generated"));

        setUserIdBySessionId(sessionId, user.getId());
        return token;
    }

    public String handleGetUser(JsonNode node) throws Exception {
        String token = node.path("token").asText();
        String username = node.path("username").asText();

        validateToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        User user = userService.getByUsername(username)
                .orElseThrow(() -> new Exception("User not found"));

        return "User " + user.getUsername() + " found";
    }
}
