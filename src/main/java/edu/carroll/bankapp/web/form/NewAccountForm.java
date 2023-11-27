package edu.carroll.bankapp.web.form;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

/**
 * This form collects all the information to create a new account (I.E.
 * checking, savings, credit) within the application. Not to be confused with a
 * user account.
 */
public class NewAccountForm {

    @NotNull
    @NotBlank(message = "Account name must not be blank")
    @Size(min = 4, message = "Account name must be at least 4 characters")
    private String accountName;

    @NotNull
    @PositiveOrZero(message = "Account balance can't be negative")
    private double accountBalance;

    /**
     * Gets the name of the user's account
     *
     * @return accountName - String - name of account
     */
    public String getAccountName() {
        return accountName;
    }

    /**
     * Sets the name of the user's account
     *
     * @param accountName - String - name of account
     */
    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    /**
     * Gets the account balance of a user's account
     *
     * @return accountBalance - double - balance of account
     */
    public double getAccountBalance() {
        return accountBalance;
    }

    /**
     * Sets the account balance of a user's account
     *
     * @param accountBalance - double - balance of account
     */
    public void setAccountBalance(double accountBalance) {
        this.accountBalance = accountBalance;
    }
}
