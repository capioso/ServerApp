package networksTwo.adapter.in;

import com.fasterxml.jackson.databind.JsonNode;
import networksTwo.domain.model.Operation;
import networksTwo.domain.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static networksTwo.adapter.in.UserHandler.*;

public class OperationHandler {

    private static final Logger logger = LoggerFactory.getLogger(OperationHandler.class);
    private final UserService userService;

    public OperationHandler(UserService userService) {
        this.userService = userService;
    }

    public String handleOperation(String op, JsonNode rootNode) {
        try {
            Operation operation = Operation.valueOf(op);
            return switch (operation) {
                case Operation.LOGIN_USER -> handleLogInUser(userService, rootNode);
                case Operation.CREATE_USER -> handleCreateUser(userService, rootNode);
                case Operation.GET_USER -> handleGetUser(userService, rootNode);
                default -> "Unknown operation: " + op;
            };
        } catch (Exception e) {
            logger.error("Error handling operation: " + op, e.getMessage());
            return "Error processing request";
        }
    }
}
