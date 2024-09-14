package networksTwo.adapter.in;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import networksTwo.domain.model.User;
import networksTwo.domain.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ClientHandler implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(ClientHandler.class);
    private final Socket clientSocket;
    private final UserService userService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ClientHandler(Socket clientSocket, UserService userService) {
        this.clientSocket = clientSocket;
        this.userService = userService;
        initializer();
    }

    public void initializer() {
        try {
            User test = new User();
            test.setUsername("test");
            test.setEmail("test@gmail.com");
            test.setPassword("encrypted");
            userService.createUser(test);
            userService.deleteByUsername(test.getUsername());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void run() {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            String clientMessage;

            while ((clientMessage = in.readLine()) != null) {
                JsonNode rootNode = objectMapper.readTree(clientMessage);
                String op = rootNode.path("operation").asText();
                OperationHandler operationHandler = new OperationHandler(userService);
                String response = operationHandler.handleOperation(op, rootNode);
                logger.info(response);
                out.println(response);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            try {
                if (clientSocket != null && !clientSocket.isClosed()) {
                    clientSocket.close();
                }
            } catch (IOException e) {
                logger.error("Error closing socket: " + e.getMessage());
            }
        }
    }
}
