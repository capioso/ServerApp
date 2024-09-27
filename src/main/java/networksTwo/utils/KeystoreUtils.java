package networksTwo.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Map;
import java.util.Optional;

public class KeystoreUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(KeystoreUtils.class);
    private static final Map<String, String> env = System.getenv();
    private static final String KEYSTORE_PASSWORD = env.get("JWT_KEYSTORE_PASS");
    private static final String KEYSTORE_PATH = env.get("JWT_KEYSTORE_PATH");
    private static final String ALIAS_KEYS = "jwt-cert";

    private Optional<KeyStore> loadKeystore() {
        try {
            InputStream keystoreInput = getClass().getClassLoader().getResourceAsStream(KEYSTORE_PATH);
            if (keystoreInput == null) {
                LOGGER.error("Keystore file not found: {}", KEYSTORE_PATH);
                return Optional.empty();
            }
            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(keystoreInput, KEYSTORE_PASSWORD.toCharArray());
            return Optional.of(keyStore);
        } catch (Exception e) {
            LOGGER.error("Error loading keystore: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public Optional<RSAPrivateKey> loadPrivateKey() {
        return loadKeystore().flatMap(keyStore -> {
            try {
                PrivateKey privateKey = (PrivateKey) keyStore.getKey(ALIAS_KEYS, KEYSTORE_PASSWORD.toCharArray());
                if (privateKey instanceof RSAPrivateKey) {
                    return Optional.of((RSAPrivateKey) privateKey);
                } else {
                    LOGGER.error("Key is not an instance of RSAPrivateKey");
                    return Optional.empty();
                }
            } catch (Exception e) {
                LOGGER.error("Error loading private key: {}", e.getMessage());
                return Optional.empty();
            }
        });
    }

    public Optional<RSAPublicKey> loadPublicKey() {
        return loadKeystore().flatMap(keyStore -> {
            try {
                Certificate cert = keyStore.getCertificate(ALIAS_KEYS);
                if (cert.getPublicKey() instanceof RSAPublicKey) {
                    return Optional.of((RSAPublicKey) cert.getPublicKey());
                } else {
                    LOGGER.error("Public key is not an instance of RSAPublicKey");
                    return Optional.empty();
                }
            } catch (Exception e) {
                LOGGER.error("Error loading public key: {}", e.getMessage());
                return Optional.empty();
            }
        });
    }
}
