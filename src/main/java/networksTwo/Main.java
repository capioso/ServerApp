package networksTwo;

import networksTwo.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.KeyStore;

@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Bean
    public CommandLineRunner startServer(ApplicationContext context) {
        return args -> {
            if (args.length != 2) {
                throw new IllegalArgumentException("Wrong number of arguments, required <port> <keystore_password>");
            }
            int port = Integer.parseInt(args[0]);
            String keystorePassword = args[1];

            InputStream keystoreInput = getClass().getClassLoader().getResourceAsStream("serverkeystore.jks");
            if (keystoreInput == null) {
                throw new FileNotFoundException("Keystore file not found in classpath.");
            }

            try {
                KeyStore keyStore = KeyStore.getInstance("JKS");
                keyStore.load(keystoreInput, keystorePassword.toCharArray());

                KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
                keyManagerFactory.init(keyStore, keystorePassword.toCharArray());

                SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(keyManagerFactory.getKeyManagers(), null, null);

                SSLServerSocketFactory sslServerSocketFactory = sslContext.getServerSocketFactory();
                try (SSLServerSocket serverSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(port)) {
                    System.out.println("Listening!");
                    UserService userService = context.getBean(UserService.class);
                    while (true) {
                        var clientSocket = serverSocket.accept();
                        System.out.println("Connected client: " + clientSocket.getInetAddress());

                        var clientHandler = new ClientHandler(clientSocket, userService);
                        new Thread(clientHandler).start();
                    }
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        };
    }
}