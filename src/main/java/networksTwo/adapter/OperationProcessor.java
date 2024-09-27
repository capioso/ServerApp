package networksTwo.adapter;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import networksTwo.application.handler.OperationHandler;
import networksTwo.domain.model.Response;
import networksTwo.utils.MessagePackUtils;
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
                InputStream in = clientSocket.getInputStream();
                OutputStream out = clientSocket.getOutputStream();
        ) {
            byte[] buffer = new byte[1024];
            int bytesRead;

            createSession(sessionId, out)
                    .filter(created -> created)
                    .orElseThrow(() -> new RuntimeException("Could not create session"));

            while ((bytesRead = in.read(buffer)) != -1) {
                byte[] clientMessageBytes = Arrays.copyOf(buffer, bytesRead);
                JsonNode rootNode = MessagePackUtils.getInstance().readTree(clientMessageBytes);
                Response response = operationHandler.handleOperation(rootNode, sessionId);
                byte[] responseBytes = MessagePackUtils.getInstance().writeValueAsBytes(response);
                out.write(responseBytes);
                out.flush();
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
