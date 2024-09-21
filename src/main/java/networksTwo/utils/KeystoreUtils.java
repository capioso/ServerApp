package networksTwo.utils;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Map;

public class KeystoreUtils {
    private static final Map<String, String> env = System.getenv();
    private static final String KEYSTORE_PASSWORD = env.get("JWT_KEYSTORE_PASS");
    private static final String KEYSTORE_PATH = env.get("JWT_KEYSTORE_PATH");
    private static final String ALIAS_KEYS = "jwt-cert";

    public KeyStore loadKeystore() throws Exception {
        InputStream keystoreInput = getClass().getClassLoader().getResourceAsStream(KEYSTORE_PATH);
        if (keystoreInput == null) {
            throw new FileNotFoundException("Keystore file not found: " + KEYSTORE_PATH);
        }
        KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.load(keystoreInput, KEYSTORE_PASSWORD.toCharArray());
        return keyStore;
    }

    public RSAPrivateKey loadPrivateKey() throws Exception{
        KeyStore keyStore = loadKeystore();
        if (keyStore == null) {
            throw new IllegalArgumentException("Keystore null");
        }
        PrivateKey privateKey = (PrivateKey) keyStore.getKey(ALIAS_KEYS, KEYSTORE_PASSWORD.toCharArray());
        if (privateKey instanceof RSAPrivateKey) {
            return (RSAPrivateKey) privateKey;
        } else {
            throw new IllegalArgumentException("Key is not an instance of RSAPrivateKey");
        }
    }

    public RSAPublicKey loadPublicKey() throws Exception {
        KeyStore keyStore = loadKeystore();
        if (keyStore == null) {
            throw new IllegalArgumentException("Keystore null");
        }
        Certificate cert = keyStore.getCertificate(ALIAS_KEYS);

        if (cert.getPublicKey() instanceof RSAPublicKey) {
            return (RSAPublicKey) cert.getPublicKey();
        } else {
            throw new IllegalArgumentException("Public key is not an instance of RSAPublicKey");
        }
    }
}
