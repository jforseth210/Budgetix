package edu.carroll.bankapp;

import edu.carroll.bankapp.web.controller.DashboardController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;
import java.util.Base64;

/**
 * A user with credentials in the system.
 */
public class User {
    private static final long EXPIRY_TIME_MS = 1 * 60 * 60 * 1000;
    private static final Logger log = LoggerFactory.getLogger(User.class);
    // 1 hr
    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder();

    private String username;
    private String hashedPassword;
    private String token;

    private long tokenExpiry;

    /**
     * gson uses this constructor
     */
    public User() {
    }

    /**
     * Initialize a user with username and hashed password
     *
     * @param username
     * @param hashedPassword
     */
    public User(String username, String hashedPassword) {
        this.username = username;
        this.hashedPassword = hashedPassword;

    }

    /**
     * Produces a string of random text that can be used to authenticate the user for a certain period of time.
     *
     * @return token
     */
    public String generateNewToken() {
        log.info("Generating new token");
        byte[] randomBytes = new byte[24];
        secureRandom.nextBytes(randomBytes);
        this.token = base64Encoder.encodeToString(randomBytes);
        this.tokenExpiry = System.currentTimeMillis() + EXPIRY_TIME_MS;
        return token;
    }

    public String getUsername() {
        return username;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public String getToken() {
        return token;
    }

    public long getTokenExpiry() {
        return tokenExpiry;
    }
}
