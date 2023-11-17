package edu.carroll.bankapp.web.form;

import jakarta.validation.constraints.NotNull;

/**
 * This form collects and validates the necessary information to create a new
 * transfer
 */
public class NewTransferForm {

    @NotNull
    private long transferAmountInDollars;

    @NotNull
    private Integer fromAccountId;

    private Integer toAccountId;

    public NewTransferForm() {

    }

    public long getTransferAmountInDollars() {
        return transferAmountInDollars;
    }

    public void setTransferAmountInDollars(long transferAmountInDollars) {
        this.transferAmountInDollars = transferAmountInDollars;
    }

    public Integer getFromAccountId() {
        return fromAccountId;
    }

    public void setFromAccountId(Integer fromAccountId) {
        this.fromAccountId = fromAccountId;
    }

    public Integer getToAccountId() {
        return toAccountId;
    }

    public void setToAccountId(Integer toAccountId) {
        this.toAccountId = toAccountId;
    }
}
