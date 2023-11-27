package edu.carroll.bankapp.web.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * This form collects the information necessary to update the username of an individual's into an account
 */
public class UpdateUsernameForm {
    @NotNull
    @NotBlank
    private String confirmPassword;

    @NotNull
    @NotBlank
    @Size(min = 4, message = "Username must be at least 4 characters long")
    private String newUsername;

    /**
     * Gets the username associated with a user's account
     *
     * @return oldUsername - String - old account username
     */
    public String getConfirmPassword() {
        return confirmPassword;
    }

    /**
     * Sets the oldUsername associated with a user's account
     *
     * @param confirmPassword - String - account username
     */
    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    /**
     * Gets the new username associated with a user's account
     *
     * @return newUsername - String - new account username
     */
    public String getNewUsername() {
        return newUsername;
    }

    /**
     * Sets the newUsername associated with a user's account
     *
     * @param newUsername - String - account username
     */
    public void setNewUsername(String newUsername) {
        this.newUsername = newUsername;
    }
}


