package networksTwo.config;

import jakarta.annotation.PostConstruct;
import networksTwo.adapter.in.OperationHandler;
import networksTwo.domain.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import networksTwo.adapter.in.ClientHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.net.ssl.SSLServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class ServerConfig {

    private static final Logger logger = LoggerFactory.getLogger(ServerConfig.class);
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
                logger.info("Connected client: {}", clientSocket.getInetAddress());

                ClientHandler clientHandler = new ClientHandler(clientSocket, operationHandler);
                executorService.submit(clientHandler);
            }
        } catch (Exception e) {
            logger.error("Error accepting client connection: {}", e.getMessage());
        }
    }
}
