package networksTwo.config;

import jakarta.annotation.PostConstruct;
import networksTwo.application.handler.OperationHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import networksTwo.adapter.OperationProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.net.ssl.SSLServerSocket;
import java.net.Socket;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class ServerConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerConfig.class);
    private final ExecutorService executorService = Executors.newFixedThreadPool(4);
    private final SSLServerSocket sslServerSocket;
    private final OperationHandler operationHandler;

    @Autowired
    public ServerConfig(SSLServerSocket sslServerSocket, OperationHandler operationHandler) {
        this.sslServerSocket = sslServerSocket;
        this.operationHandler = operationHandler;
    }

    @PostConstruct
    public void startServer() {
        try {
            //noinspection InfiniteLoopStatement
            while (true) {
                Socket clientSocket = sslServerSocket.accept();
                UUID sessionId = UUID.randomUUID();
                LOGGER.info("Connected client session ID: {}, IP: {}", sessionId, clientSocket.getInetAddress());

                OperationProcessor operationProcessor = new OperationProcessor(clientSocket, operationHandler, sessionId);
                executorService.submit(operationProcessor);
            }
        } catch (Exception e) {
            LOGGER.error("Error accepting client connection: {}", e.getMessage());
        }
    }
}
