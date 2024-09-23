package networksTwo.adapter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import networksTwo.domain.persistence.SessionRepository;
import networksTwo.application.handler.OperationHandler;
import networksTwo.domain.model.Session;
import networksTwo.utils.ObjectMapperUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OperationProcessor implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(OperationProcessor.class);

    private final Socket clientSocket;
    private final UUID sessionId;
    private final OperationHandler operationHandler;

    public OperationProcessor(Socket clientSocket, OperationHandler operationHandler, UUID sessionId) {
        this.clientSocket = clientSocket;
        this.operationHandler = operationHandler;
        this.sessionId = sessionId;
    }

    @Override
    public void run() {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            String clientMessage;

            SessionRepository.activeUsers.put(sessionId, new Session(null, out));

            while ((clientMessage = in.readLine()) != null) {
                JsonNode rootNode = ObjectMapperUtils.getInstance().readTree(clientMessage);
                String op = rootNode.path("operation").asText();
                String response = operationHandler.handleOperation(op, rootNode, sessionId);
                logger.info(response);
                out.println(response);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            SessionRepository.activeUsers.remove(sessionId);
            try {
                if (clientSocket != null && !clientSocket.isClosed()) {
                    clientSocket.close();
                }
            } catch (IOException e) {
                logger.error("Error closing socket: {}", e.getMessage());
            }
        }
    }
}
