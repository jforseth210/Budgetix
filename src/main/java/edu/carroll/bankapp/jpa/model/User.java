package edu.carroll.bankapp.jpa.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;
import java.util.Base64;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * A user with credentials in the system.
 */
@Entity
@Table(name = "user")
public class User {
    private static final long EXPIRY_TIME_MS = 1 * 60 * 60 * 1000;
    private static final Logger log = LoggerFactory.getLogger(User.class);
    // 1 hr
    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder();

    @Id
    @GeneratedValue
    private Integer id;
    @Column(name = "username", nullable = false, unique = true)
    private String username;
    @Column(name = "hashed_password", nullable = false, unique = true)
    private String hashedPassword;
    @Column(name = "token", unique = true)
    private String token;

    @Column(name = "token_expiry")
    private long tokenExpiry;

    /**
     * Hibernate wants a default constructor
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
     * Produces a string of random text that can be used to authenticate the user
     * for a certain period of time.
     *
     * @return token
     */
    public String generateNewToken() {
        log.info("Generating new token");
        byte[] randomBytes = new byte[24];
        secureRandom.nextBytes(randomBytes);
        this.token = base64Encoder.encodeToString(randomBytes);
        this.tokenExpiry = System.currentTimeMillis() + EXPIRY_TIME_MS;
        log.info("Token: " + token);
        log.info("Token Expiry: " + tokenExpiry);
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

    public void resetToken() {
        this.token = null;
        this.tokenExpiry = 0;
    }
}
