package networksTwo.adapter.in;

import com.fasterxml.jackson.databind.JsonNode;
import networksTwo.domain.model.User;
import networksTwo.domain.service.UserService;

import static networksTwo.utils.PasswordUtils.checkPassword;
import static networksTwo.utils.PasswordUtils.hashPassword;

public class UserHandler {
    public static String handleGetUser(UserService userService, JsonNode node) {
        try {
            String username = node.path("username").asText();
            User user = userService.getByUsername(username);
            return "User with username " + user.getUsername() + " exists";
        } catch (Exception e) {
            return e.getMessage();
        }
    }


    public static String handleLogInUser(UserService userService, JsonNode node) {
        try {
            String username = node.path("username").asText();
            String password = node.path("password").asText();

            User user = userService.getByUsername(username);
            if (!checkPassword(password, user.getPassword())) {
                throw new Exception("Bad credentials");
            }
            return "Login successful! Welcome " + user.getUsername();
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public static String handleCreateUser(UserService userService, JsonNode node) {
        try {
            String username = node.path("username").asText();
            String email = node.path("email").asText();
            String password = node.path("password").asText();
            User newUser = new User();
            newUser.setUsername(username);
            newUser.setEmail(email);
            newUser.setPassword(hashPassword(password));
            userService.createUser(newUser);
            return "User created successfully";
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}
