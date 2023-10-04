package edu.carroll.bankapp.web.form;

import jakarta.validation.constraints.NotNull;

/**
 * This form collects and validates the necessary information to create a new
 * transaction
 */
public class TransactionForm {

    @NotNull
    private String toFrom;

    @NotNull
    private Integer amountInCents;

    @NotNull
    private String name;

    private Integer accountId;


    /**
     * Default constructor for Hibernate
     */
    public TransactionForm() {
    }

    /**
     * Get the account from where the money was withdrawn/deposited
     *
     * @return toFrom - String - account name from where money originated
     */
    public String getToFrom() {
        return toFrom;
    }

    /**
     * Set the account from where the money was withdrawn/deposited
     *
     * @param toFrom - String - account name from where money originated
     */
    public void setToFrom(String toFrom) {
        this.toFrom = toFrom;
    }

    /**
     * Get the amount in cents for the transaction
     *
     * @return amountInCents - Integer - amount of transaction in US cents
     */
    public Integer getAmountInCents() {
        return amountInCents;
    }

    /**
     * Get the amount in dollars for the transaction (via math conversion)
     *
     * @return amount in dollars - double - monetary amount in dollars
     */
    public double getAmountInDollars() {
        return (double) amountInCents / (double) 100;
    }

    /**
     * Set the amount in cents for the transaction
     *
     * @param amountInCents - Integer - amount of transaction in US cents
     */
    public void setAmountInCents(Integer amountInCents) {
        this.amountInCents = amountInCents;
    }

    /**
     * Get the name of the transaction
     *
     * @return name - String - name of transaction
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of the transaction
     *
     * @param name - String - name of the transaction
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the account ID associated with the transaction logging
     *
     * @return accountId - Integer - account ID who logged the transaction
     */
    public Integer getAccountId() {
        return this.accountId;
    }

    /**
     * Set the account ID associated with the transaction logging
     *
     * @param id - Integer - account ID who logged the transaction
     */
    public void setAccountId(Integer id) {
        this.accountId = id;
    }
}
