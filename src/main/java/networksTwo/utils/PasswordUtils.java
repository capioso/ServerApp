package networksTwo.utils;

import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class PasswordUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(PasswordUtils.class);

    /**
     * Hash a password with Bcrypt.
     *
     * @param password string.
     * @return hashedPassword
     */
    public static Optional<String> hashPassword(String password) {
        try {
            return Optional.of(BCrypt.hashpw(password, BCrypt.gensalt()));
        } catch (Exception e) {
            LOGGER.error("Error while hashing password: {}", e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Check if password received in login is equals to hashed password saved in DB.
     *
     * @param password password not encrypted.
     * @param hashed password encrypted.
     * @return Optional<True> if it matches.
     */
    public static Optional<Boolean> checkPassword(String password, String hashed) {
        try {
            boolean matches = BCrypt.checkpw(password, hashed);
            return Optional.of(matches);
        } catch (Exception e) {
            LOGGER.error("Error while checking password: {}", e.getMessage());
            return Optional.empty();
        }
    }
}
