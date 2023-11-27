package edu.carroll.bankapp.web.form;

import jakarta.validation.constraints.NotNull;

/**
 * Form object for deleting a financial account
 */
public class DeleteAccountForm {
    @NotNull
    private int accountId;

    /**
     * Getter for the id of the account to be deleted
     * 
     * @return accountId id of the account to be deleted
     */
    public int getAccountId() {
        return accountId;
    }

    /**
     * Setter for the id of the account to be deleted
     * 
     * @param accountId id of the account to be deleted
     */
    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

}
