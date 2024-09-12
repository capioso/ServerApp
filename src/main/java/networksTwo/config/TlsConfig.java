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

@Configuration
public class TlsConfig {
    private static final int SERVER_PORT = 10852;
    private static final String KEYSTORE_PASSWORD = "j@L9DZQ6y=3\"";
    private static final String KEYSTORE_PATH = "serverkeystore.jks";

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
