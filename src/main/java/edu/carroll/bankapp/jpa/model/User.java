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

    public String getUsername() {
        return username;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }
}

