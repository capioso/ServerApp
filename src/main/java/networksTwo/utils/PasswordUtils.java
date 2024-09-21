package networksTwo.utils;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtils {
    public static String hashPassword(String password) throws RuntimeException {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public static boolean checkPassword(String password, String hashed) throws RuntimeException {
        return BCrypt.checkpw(password, hashed);
    }
}
