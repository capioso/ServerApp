package networksTwo.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

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

    public static String generateToken(UUID userId) throws Exception {
        Algorithm algorithm = Algorithm.RSA256(rsaPublicKey, rsaPrivateKey);
        return JWT.create()
                .withIssuer("capioso")
                .withSubject(String.valueOf(userId))
                .sign(algorithm);
    }

    public static DecodedJWT validateToken(String token) throws Exception {
        Algorithm algorithm = Algorithm.RSA256(rsaPublicKey, null);
        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer("capioso")
                .build();
        return verifier.verify(token);
    }
}