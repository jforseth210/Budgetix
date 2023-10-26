package edu.carroll.bankapp.web.form;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * This form collects and validates the necessary information to create a new
 * account
 */
public class NewLoginForm {
    @NotNull
    private String fullName;

    @NotNull
    @Size(min = 6, message = "Username must be at least 6 characters long")
    private String username;

    @NotNull
    @Email(message = "Must be a valid email address")
    private String email;

    @NotNull
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;

    @NotNull
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String confirm;

    /**
     * Get the full name of a user
     *
     * @return fullName - String - full name of user
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * Set the full name of the user
     *
     * @param name - String - full name of the user
     */
    public void setFullName(String name) {
        this.fullName = name;
    }

    /**
     * Get the username of the new user
     *
     * @return username - String - username of the new user
     */
    public String getUsername() {
        return username;
    }

    /**
     * Set the username for a new user
     *
     * @param username - String - new user username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Get the new user's email
     *
     * @return email - String - user's email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Set the new user's email
     *
     * @param email - String - user's email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Get the new user's password
     *
     * @return password - String - user's password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Set the new user's password
     *
     * @param password - String - user's password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Get the confirmation password (this will be tested to match in other methods)
     *
     * @return confirm - String - the confirmation of a password
     */
    public String getConfirm() {
        return confirm;
    }

    /**
     * Set the confirmation password
     *
     * @param password - String - confirmation password
     */
    public void setConfirm(String password) {
        this.confirm = password;
    }
}
