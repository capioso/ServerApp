package networksTwo;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.Socket;
import java.security.KeyStore;

public class Main {
    public static void main(String[] args) {
        try {
            if (args.length != 2) {
                throw new IllegalArgumentException("Wrong number of arguments, required <port> <keystore_password>");
            }
            int port = Integer.parseInt(args[0]);
            String keystorePassword = args[1];

            InputStream keystoreInput = Main.class.getClassLoader().getResourceAsStream("serverkeystore.jks");
            if (keystoreInput == null) {
                throw new FileNotFoundException("Keystore file not found in classpath.");
            }

            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(keystoreInput, keystorePassword.toCharArray());

            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore, keystorePassword.toCharArray());

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagerFactory.getKeyManagers(), null, null);

            SSLServerSocketFactory sslServerSocketFactory = sslContext.getServerSocketFactory();
            try (SSLServerSocket serverSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(port)) {
                System.out.println("Listening on port " + port);

                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Connected client: " + clientSocket.getInetAddress());

                    ClientHandler clientHandler = new ClientHandler(clientSocket);
                    new Thread(clientHandler).start();
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}