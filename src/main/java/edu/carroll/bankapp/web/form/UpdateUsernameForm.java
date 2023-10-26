package edu.carroll.bankapp.web.form;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * This form collects the information necessary to update the username of an individual's into an account
 */
public class UpdateUsernameForm {
    @NotNull
    @Size(min = 4, message = "Username must be at least 4 characters long")
    private String oldUsername;

    @NotNull
    @Size(min = 4, message = "Username must be at least 4 characters long")
    private String newUsername;

    /**
     * Gets the username associated with a user's account
     *
     * @return oldUsername - String - old account username
     */
    public String getOldUsername() {
        return oldUsername;
    }

    /**
     * Sets the oldUsername associated with a user's account
     *
     * @param oldUsername - String - account username
     */
    public void setOldUsername(String oldUsername) {
        this.oldUsername = oldUsername;
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


