package networksTwo.adapter.in;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import networksTwo.domain.model.Operation;
import networksTwo.domain.model.Response;
import networksTwo.domain.service.ChatService;
import networksTwo.domain.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.util.UUID;

import static networksTwo.adapter.in.ChatHandler.handleCreateChat;
import static networksTwo.adapter.in.ChatHandler.handleGetChat;
import static networksTwo.adapter.in.UserHandler.*;
import static networksTwo.utils.SerializerUtils.handleString;

public class OperationHandler {

    private static final Logger logger = LoggerFactory.getLogger(OperationHandler.class);
    private final UserService userService;
    private final ChatService chatService;

    public OperationHandler(UserService userService, ChatService chatService) {
        this.userService = userService;
        this.chatService = chatService;
    }

    public String handleOperation(String op, JsonNode rootNode, UUID sessionId) {
        try {
            Operation operation = Operation.valueOf(op);
            return switch (operation) {
                case Operation.LOGIN_USER -> handleLogInUser(userService, rootNode, sessionId);
                case Operation.CREATE_USER -> handleCreateUser(userService, rootNode);
                case Operation.GET_USER -> handleGetUser(userService, rootNode);
                case Operation.CREATE_CHAT -> handleCreateChat(userService, chatService, rootNode);
                case Operation.GET_CHAT -> handleGetChat(userService, rootNode);
                default -> throw new IllegalStateException("Unexpected value: " + op);
            };
        } catch (Exception e) {
            logger.error("Error handling operation: " + e.getMessage());
            return handleString("Error", e.getMessage());
        }
    }
}
