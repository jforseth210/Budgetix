package edu.carroll.bankapp.jpa.model;

import edu.carroll.bankapp.Ownable;
import jakarta.persistence.*;

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
     * @param fullName       - The user's first and last name
     * @param email          - The user's email address
     * @param username       - The username of the user
     * @param hashedPassword - The user's already-hashed password
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
     * Whether this user owns the given object
     * 
     * @param item the Ownable to check ownership of
     * @return true if owned by this user, false if not
     */
    public boolean owns(Ownable item) {
        if (item == null) {
            log.warn("Attempt to check ownership on null object for user {}", username);
            return false;
        }
        return item.getOwner().equals(this);
    }

    /**
     * Set the username for a user
     *
     * @param username - String - the username for a user
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Set the hashed password for a user
     *
     * @param password - String - hashed password
     */
    public void setHashedPassword(String password) {
        this.hashedPassword = password;
    }

    /**
     * Get the full name for a user
     *
     * @return fullName - String - full name of the site user
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * Set the full name for a user
     *
     * @param fullName - String - full name of the site user
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    /**
     * Get the email for a user
     *
     * @return email - String - email for our user
     */
    public String getEmail() {
        return email;
    }

    /**
     * Set the email for a user
     *
     * @param email - String - email for our user
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Get the id associated with the user (they CANNOT EVER set their own ID)
     *
     * @return - int - user's id
     */
    public int getId() {
        return this.id;
    }

}
