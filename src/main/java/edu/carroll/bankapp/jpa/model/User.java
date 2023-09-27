package edu.carroll.bankapp.jpa.model;

import jakarta.persistence.*;

import java.util.Set;

/**
 * A user with credentials in the system.
 */
@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue
    private Long id;
    @Column(name = "fullName", nullable = false)
    private String fullName;
    @Column(name = "email", nullable = false, unique = true)
    private String email;
    @Column(name = "username", nullable = false, unique = true)
    private String username;
    @Column(name = "hashed_password", nullable = false, unique = true)
    private String hashedPassword;

    @OneToMany
    private Set<Account> accounts;

    /**
     * Hibernate wants a default constructor
     */
    public User() {

    }

    /**
     * Initialize a user with username and hashed password
     *
     * @param fullName
     * @param email
     * @param username
     * @param hashedPassword
     */
    public User(String fullName, String email, String username, String hashedPassword) {
        this.fullName = fullName;
        this.email = email;
        this.username = username;
        this.hashedPassword = hashedPassword;
    }

    public String getUsername() {
        return username;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public Set<Account> getAccounts() {
        return accounts;
    }
}
