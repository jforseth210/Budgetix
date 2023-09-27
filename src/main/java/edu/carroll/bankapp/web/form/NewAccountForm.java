package edu.carroll.bankapp.web.form;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public class NewAccountForm {

    @NotNull
    @Size(min = 4, message = "Account name must be at least 4 characters")
    private String accountName;

    @NotNull
    @PositiveOrZero(message = "Account balance can't be negative")
    private Double accountBalance;

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public Double getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(Double accountBalance) {
        this.accountBalance = accountBalance;
    }
}
