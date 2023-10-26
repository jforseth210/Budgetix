package edu.carroll.bankapp.web.form;

import edu.carroll.bankapp.jpa.model.SiteUser;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class UpdatePasswordForm {
    @NotNull
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String oldPassword;

    @NotNull
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String newPassword;

    @NotNull
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String newConfirm;

    /**
     * Get the user's old password
     *
     * @return oldPassword - String - user's password
     */
    public String getOldPassword() {
        return oldPassword;
    }

    /**
     * Set the new user's password
     *
     * @param oldPassword - String - user's password
     */
    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    /**
     * Get the user's new password
     *
     * @return newPassword - String - user's password
     */
    public String getNewPassword() {
        return newPassword;
    }

    /**
     * Set the user's new password
     *
     * @param newPassword - String - user's new password
     */
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    /**
     * Get the confirmation password (this will be tested to match in other methods)
     *
     * @return confirm - String - the confirmation of a password
     */
    public String getNewConfirm() {
        return newConfirm;
    }

    /**
     * Set the confirmation password
     *
     * @param newConfirm - String - confirmation password
     */
    public void setConfirm(String newConfirm) {
        this.newConfirm = newConfirm;
    }

//    public boolean isPasswordValid(SiteUser user, String password) {
//        return passwordEncoder.matches(password, user.getPassword());
//    }
}
