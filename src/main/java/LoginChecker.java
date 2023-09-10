import org.mindrot.jbcrypt.BCrypt;

public class LoginChecker {
    public static String generateHash(String plaintextPassword) {
        return BCrypt.hashpw(plaintextPassword, BCrypt.gensalt());
    }

    public static boolean checkPassword(String passwordToCheck, String hashedPassword) {
        return BCrypt.checkpw(passwordToCheck, hashedPassword);
    }

}
