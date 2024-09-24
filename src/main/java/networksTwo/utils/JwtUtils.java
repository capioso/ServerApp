package networksTwo.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import networksTwo.domain.model.User;
import networksTwo.application.service.UserService;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Optional;
import java.util.UUID;

public class JwtUtils{

    private static final RSAPublicKey rsaPublicKey;
    private static final RSAPrivateKey rsaPrivateKey;

    static {
        try {
            KeystoreUtils keystoreUtils = new KeystoreUtils();
            rsaPublicKey = keystoreUtils.loadPublicKey();
            rsaPrivateKey = keystoreUtils.loadPrivateKey();
        } catch (Exception e) {
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
            System.err.println("Error generating token: " + e.getMessage());
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
        } catch (JWTVerificationException e) {
            System.err.println("Token verification failed: " + e.getMessage());
            return Optional.empty();
        }
    }

    public static Optional<User> getUserFromToken(String token, UserService userService) throws Exception {
        DecodedJWT decodedJWT = validateToken(token)
                .orElseThrow(() -> new Exception("Invalid token"));
        UUID id = UUID.fromString(decodedJWT.getSubject());
        return userService.getById(id);
    }
}