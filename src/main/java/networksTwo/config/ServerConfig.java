package networksTwo.config;

import jakarta.annotation.PostConstruct;
import networksTwo.domain.service.ChatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import networksTwo.adapter.in.ClientHandler;
import networksTwo.domain.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.net.ssl.SSLServerSocket;
import java.net.Socket;

@Component
public class ServerConfig {

    private static final Logger logger = LoggerFactory.getLogger(ServerConfig.class);
    private final SSLServerSocket sslServerSocket;
    private final UserService userService;
    private final ChatService chatService;

    @Autowired
    public ServerConfig(SSLServerSocket sslServerSocket, UserService userService, ChatService chatService) {
        this.sslServerSocket = sslServerSocket;
        this.userService = userService;
        this.chatService = chatService;
    }

    @PostConstruct
    public void startServer() {
        try {
            while (true) {
                Socket clientSocket = sslServerSocket.accept();
                logger.info("Connected client: " + clientSocket.getInetAddress());

                ClientHandler clientHandler = new ClientHandler(clientSocket, userService, chatService);
                Thread clientThread = new Thread(clientHandler);
                clientThread.start();
            }
        } catch (Exception e) {
            logger.info("Error accepting client connection: " + e.getMessage());
        }
    }
}
