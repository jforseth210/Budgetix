package edu.carroll.bankapp;

import edu.carroll.bankapp.web.controller.DashboardController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;
import java.util.Base64;

/**
 * This will all have to be rewritten after we set up a database
 */
public class User {
    private static final Logger log = LoggerFactory.getLogger(User.class);
    // 1 hr
    private static final long EXPIRY_TIME_MS = 1 * 60 * 60 * 1000;
    private static final SecureRandom secureRandom = new SecureRandom(); //threadsafe
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder();
    private String username;
    private String hashedPassword;
    private String token;

    private long tokenExpiry;

    public User() {
    }

    public User(String username, String hashedPassword) {
        this.username = username;
        this.hashedPassword = hashedPassword;

    }

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
