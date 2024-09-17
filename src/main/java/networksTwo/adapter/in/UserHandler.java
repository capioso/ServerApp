package networksTwo.adapter.in;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.JsonNode;
import networksTwo.domain.model.User;
import networksTwo.domain.service.UserService;

import static networksTwo.utils.JwtUtils.generateToken;
import static networksTwo.utils.JwtUtils.validateToken;
import static networksTwo.utils.PasswordUtils.checkPassword;
import static networksTwo.utils.PasswordUtils.hashPassword;
import static networksTwo.utils.SerializerUtils.handleString;

public class UserHandler {

    public static String handleGetUser(UserService userService, JsonNode node) throws Exception {
        String token = node.path("token").asText();
        DecodedJWT decodedJWT = validateToken(token);
        String id = decodedJWT.getSubject();
        System.out.println("UUID: " + id);
        String username = node.path("username").asText();
        User user = userService.getByUsername(username);
        return handleString("message", "User " + user.getUsername() + " found");
    }

    public static String handleLogInUser(UserService userService, JsonNode node) throws Exception {
        String username = node.path("username").asText();
        String password = node.path("password").asText();
        User user = userService.getByUsername(username);
        if (!checkPassword(password, user.getPassword())) {
            throw new Exception("Bad credentials");
        }
        String token = generateToken(user.getId());
        return handleString("token", token);
    }

    public static String handleCreateUser(UserService userService, JsonNode node) throws Exception {
        String username = node.path("username").asText();
        String email = node.path("email").asText();
        String password = node.path("password").asText();
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setPassword(hashPassword(password));
        userService.createUser(newUser);
        return handleString("message", "User created successfully");
    }
}
