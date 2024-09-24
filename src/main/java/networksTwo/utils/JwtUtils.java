package networksTwo.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import networksTwo.domain.model.User;
import networksTwo.application.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Optional;
import java.util.UUID;

public class JwtUtils{

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtUtils.class);
    private static final RSAPublicKey rsaPublicKey;
    private static final RSAPrivateKey rsaPrivateKey;

    static {
        try {
            KeystoreUtils keystoreUtils = new KeystoreUtils();
            rsaPublicKey = keystoreUtils.loadPublicKey()
                    .orElseThrow(() -> new RuntimeException("RSA Public Key not found"));
            rsaPrivateKey = keystoreUtils.loadPrivateKey()
                    .orElseThrow(() -> new RuntimeException("RSA Private Key not found"));
        } catch (Exception e) {
            LOGGER.error("Error importing Keystore utils: {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    public static Optional<String> generateToken(UUID userId) {
        try {
            Algorithm algorithm = Algorithm.RSA256(rsaPublicKey, rsaPrivateKey);
            String token = JWT.create()
                    .withIssuer("capioso")
                    .withSubject(userId.toString())
                    .sign(algorithm);
            return Optional.of(token);
        } catch (Exception e) {
            LOGGER.error("Error generating token: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public static Optional<DecodedJWT> validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.RSA256(rsaPublicKey, null);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("capioso")
                    .build();
            DecodedJWT decodedJWT = verifier.verify(token);
            return Optional.of(decodedJWT);
        } catch (Exception e) {
            LOGGER.error("Token verification failed: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public static Optional<User> getUserFromToken(String token, UserService userService) {
        try {
            DecodedJWT decodedJWT = validateToken(token)
                    .orElseThrow(() -> new Exception("Invalid token"));
            UUID id = UUID.fromString(decodedJWT.getSubject());
            return userService.getById(id);
        }catch (Exception e) {
            LOGGER.error("Error retrieving user from token: {}", e.getMessage());
            return Optional.empty();
        }
    }
}