package networksTwo.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import networksTwo.domain.model.User;
import networksTwo.application.service.UserService;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
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

    public static String generateToken(UUID userId) {
        Algorithm algorithm = Algorithm.RSA256(rsaPublicKey, rsaPrivateKey);
        return JWT.create()
                .withIssuer("capioso")
                .withSubject(String.valueOf(userId))
                .sign(algorithm);
    }

    public static DecodedJWT validateToken(String token) {
        Algorithm algorithm = Algorithm.RSA256(rsaPublicKey, null);
        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer("capioso")
                .build();
        return verifier.verify(token);
    }

    public static User getUserFromToken(String token, UserService userService) {
        DecodedJWT decodedJWT = validateToken(token);
        UUID id = UUID.fromString(decodedJWT.getSubject());
        return userService.getById(id);
    }
}