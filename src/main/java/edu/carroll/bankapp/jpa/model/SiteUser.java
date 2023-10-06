package edu.carroll.bankapp.jpa.model;

import jakarta.persistence.*;

/**
 * A user with credentials in the system.
 */
@Entity
@Table(name = "site_user")
public class SiteUser {
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
     * Whether or not this user owns the given transaction
     */
    public boolean owns(Transaction transaction) {
        return transaction.getAccount().getOwner().equals(this);
    }

    /**
     * Whether or not this user owns the given account
     */
    public boolean owns(Account account) {
        return account.getOwner().equals(this);
    }

}
