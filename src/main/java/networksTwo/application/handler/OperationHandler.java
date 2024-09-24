package networksTwo.application.handler;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.annotation.PostConstruct;
import networksTwo.domain.enums.Operation;
import networksTwo.domain.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static networksTwo.utils.SerializerUtils.handleString;

@Component
public class OperationHandler {

    private final UserHandler userHandler;
    private final ChatHandler chatHandler;
    private final MessageHandler messageHandler;
    private static final Logger LOGGER = LoggerFactory.getLogger(OperationHandler.class);

    @Autowired
    public OperationHandler(UserHandler userHandler, ChatHandler chatHandler, MessageHandler messageHandler) {
        this.userHandler = userHandler;
        this.chatHandler = chatHandler;
        this.messageHandler = messageHandler;
    }

    @PostConstruct
    public void init() {
        userHandler.getUserService().ifPresent(userService -> {
            try {
                User test = new User();
                test.setUsername("test");
                test.setEmail("test@gmail.com");
                test.setPassword("encrypted");
                userService.createUser(test)
                        .filter(created -> created)
                        .orElseThrow(() -> new RuntimeException("User creation failed"));
                userService.deleteByUsername(test.getUsername())
                        .filter(deleted -> deleted)
                        .orElseThrow(() -> new RuntimeException("User deletion failed"));
            } catch (Exception e) {
                LOGGER.error("Error initializing userService: {}", e.getMessage());
                throw new RuntimeException("Error initializing userService: " + e.getMessage());
            }
        });
    }

    public String handleOperation(String op, JsonNode rootNode, UUID sessionId) {
        try {
            Operation operation = Operation.valueOf(op);

            return switch (operation) {
                case Operation.CREATE_USER -> userHandler.handleCreateUser(rootNode);
                case Operation.LOGIN_USER -> userHandler.handleLogInUser(rootNode, sessionId);
                case Operation.GET_USER -> userHandler.handleGetUser(rootNode);
                case Operation.CREATE_CHAT -> chatHandler.handleCreateChat(rootNode);
                case Operation.GET_CHATS -> chatHandler.handleGetChats(rootNode);
                case Operation.GET_SINGLE_CHAT -> chatHandler.handleGetSingleChat(rootNode);
                case Operation.SEND_MESSAGE -> messageHandler.handleSendMessage(rootNode);
                case Operation.GET_MESSAGES_BY_CHAT -> messageHandler.handleGetMessagesByChat(rootNode);
                default -> throw new IllegalStateException("Unexpected value: " + op);
            };
        } catch (Exception e) {
            LOGGER.error("Error handling operation: {}", e.getMessage());
            return handleString("Error", e.getMessage());
        }
    }
}
