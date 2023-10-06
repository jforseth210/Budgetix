package edu.carroll.bankapp.web.form;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * This form collects the information necessary to log into an account
 */
public class LoginForm {
    @NotNull
    @Size(min = 4, message = "Username must be at least 4 characters long")
    private String username;

    @NotNull
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;

    /**
     * Gets the username associated with a user's account
     *
     * @return username - String - account username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username associated with a user's account
     *
     * @param username - String - account username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the raw password for our user account
     *
     * @return password - String - the raw password for our user
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the raw password for our user account
     *
     * @param password - String - the raw password for our user
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
