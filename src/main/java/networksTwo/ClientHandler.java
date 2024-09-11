package networksTwo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import networksTwo.model.User;
import networksTwo.service.UserService;

public class ClientHandler implements Runnable {
    private final UserService userService;
    private final Socket clientSocket;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ClientHandler(Socket socket, UserService userService) {
        this.clientSocket = socket;
        this.userService = userService;
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            String clientMessage;

            while ((clientMessage = in.readLine()) != null) {
                JsonNode rootNode = objectMapper.readTree(clientMessage);
                String operation = rootNode.path("operation").asText();
                String response;
                switch (operation) {
                    case "CREATE_USER":
                        response = handleCreateUser(rootNode);
                        break;
                    default:
                        response = "Unknown operation: " + operation;
                        break;
                }
                out.println(response);
            }

            in.close();
            out.close();
            clientSocket.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private String handleCreateUser(JsonNode node) {
        try {
            String username = node.path("username").asText();
            String email = node.path("email").asText();
            String password = node.path("password").asText();
            User newUser = new User();
            newUser.setUsername(username);
            newUser.setEmail(email);
            newUser.setPassword(password);
            userService.createUser(newUser);
            return "User created";
        }catch (Exception e) {
            return "User creation failed: " + e.getMessage();
        }
    }
}
