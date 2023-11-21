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

    /**
     * Blank constructor for Thymeleaf
     */
    public NewTransferForm() {

    }

    /**
     * Getter for the transfer amount
     * 
     * @return long representing the amount of money to transfer
     */
    public long getTransferAmountInDollars() {
        return transferAmountInDollars;
    }

    /**
     * Setter for transfer amount
     * 
     * @param transferAmountInDollars long representing the amount of money to
     *                                transfer
     */
    public void setTransferAmountInDollars(long transferAmountInDollars) {
        this.transferAmountInDollars = transferAmountInDollars;
    }

    /**
     * Getter for id of account money is being transfered from
     * 
     * @return the from account's id
     */
    public Integer getFromAccountId() {
        return fromAccountId;
    }

    /**
     * Setter for id of account money is being transfered from
     * 
     * @param fromAccountId the from account's id
     */
    public void setFromAccountId(Integer fromAccountId) {
        this.fromAccountId = fromAccountId;
    }

    /**
     * Getter for id of account money is being transfered to
     * 
     * @return the to account's id
     */

    public Integer getToAccountId() {
        return toAccountId;
    }

    /**
     * Setter for id of account money is being transfered to
     * 
     * @param toAccountId the to account's id
     */
    public void setToAccountId(Integer toAccountId) {
        this.toAccountId = toAccountId;
    }
}
