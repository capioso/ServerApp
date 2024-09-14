package networksTwo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.Map;

@Configuration
public class TlsConfig {
    private static final Map<String, String> env = System.getenv();
    private static final int SERVER_PORT = Integer.parseInt(env.get("SERVER_PORT"));
    private static final String KEYSTORE_PASSWORD = env.get("KEYSTORE_PASSWORD");
    private static final String KEYSTORE_PATH = env.get("KEYSTORE_PATH");

    @Bean
    public SSLServerSocket sslServerSocket() throws Exception {
        InputStream keystoreInput = getClass().getClassLoader().getResourceAsStream(KEYSTORE_PATH);
        if (keystoreInput == null) {
            throw new FileNotFoundException("Keystore file not found: " + KEYSTORE_PATH);
        }

        KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.load(keystoreInput, KEYSTORE_PASSWORD.toCharArray());

        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, KEYSTORE_PASSWORD.toCharArray());

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(keyManagerFactory.getKeyManagers(), null, null);

        SSLServerSocketFactory sslServerSocketFactory = sslContext.getServerSocketFactory();
        return (SSLServerSocket) sslServerSocketFactory.createServerSocket(SERVER_PORT);
    }
}
