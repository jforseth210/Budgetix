package edu.carroll.bankapp.jpa.model;

import edu.carroll.bankapp.Ownable;
import jakarta.persistence.*;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A user with credentials in the system.
 */
@Entity
@Table(name = "site_user")
public class SiteUser {
    private static final Logger log = LoggerFactory.getLogger(SiteUser.class);

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @Column(name = "fullName", nullable = false)
    private String fullName;
    @Column(name = "email", nullable = false, unique = true)
    private String email;
    @Column(name = "username", nullable = false, unique = true)
    private String username;
    @Column(name = "hashed_password", nullable = false, unique = true)
    private String hashedPassword;

    /**
     * Hibernate wants a default constructor
     */
    public SiteUser() {

    }

    /**
     * Initialize a user with username and hashed password
     *
     * @param fullName
     * @param email
     * @param username
     * @param hashedPassword
     */
    public SiteUser(String fullName, String email, String username, String hashedPassword) {
        this.fullName = fullName;
        this.email = email;
        this.username = username;
        this.hashedPassword = hashedPassword;
    }

    /**
     * Gets and returns the user's username
     *
     * @return username - String
     */
    public String getUsername() {
        return username;
    }

    /**
     * Gets and returns the hashed password for a user
     *
     * @return hashed password - String
     */
    public String getHashedPassword() {
        return hashedPassword;
    }

    /**
     * Whether or not this user owns the given object
     */
    public boolean owns(Ownable item) {
        if (item == null) {
            log.warn("Attempt to check ownership on null object for user {}", username);
            return false;
        }
        return item.getOwner().equals(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        SiteUser siteUser = (SiteUser) o;
        return Objects.equals(fullName, siteUser.fullName) && Objects.equals(email, siteUser.email)
                && Objects.equals(username, siteUser.username)
                && Objects.equals(hashedPassword, siteUser.hashedPassword);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fullName, email, username, hashedPassword);
    }
}
