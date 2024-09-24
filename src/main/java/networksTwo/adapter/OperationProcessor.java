package networksTwo.adapter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import networksTwo.application.handler.OperationHandler;
import networksTwo.utils.ObjectMapperUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static networksTwo.application.service.SessionService.createSession;
import static networksTwo.application.service.SessionService.deleteSessionById;

public class OperationProcessor implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(OperationProcessor.class);

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

            createSession(sessionId, out)
                    .filter(created -> created)
                    .orElseThrow(() -> new RuntimeException("Could not create session"));

            while ((clientMessage = in.readLine()) != null) {
                JsonNode rootNode = ObjectMapperUtils.getInstance().readTree(clientMessage);
                String op = rootNode.path("operation").asText();
                String response = operationHandler.handleOperation(op, rootNode, sessionId);
                LOGGER.info(response);
                out.println(response);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        } finally {
            disconnectClient();
        }
    }

    private void disconnectClient() {
        try {
            deleteSessionById(sessionId)
                    .filter(deleted -> deleted)
                    .orElseThrow(() -> new RuntimeException("Could not delete session"));

            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
            }
        } catch (IOException e) {
            LOGGER.error("Error closing socket: {}", e.getMessage());
        }
    }
}
