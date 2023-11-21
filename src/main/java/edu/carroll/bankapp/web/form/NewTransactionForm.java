package edu.carroll.bankapp.web.form;

import jakarta.validation.constraints.NotNull;

/**
 * This form collects and validates the necessary information to create a new
 * transaction
 */
public class NewTransactionForm {
    @NotNull
    private String toFrom;

    @NotNull
    private long amountInDollars;

    @NotNull
    private String name;

    private String type;

    private Integer accountId;

    /**
     * Default constructor for Hibernate
     */
    public NewTransactionForm() {

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
     * Get the amount in dollars for the transaction
     *
     * @return amount in dollars - long - monetary amount in dollars
     */
    public long getAmountInDollars() {
        return amountInDollars;
    }

    /**
     * Get the amount in cents for the transaction
     *
     * @return amountInCents - long - amount of transaction in US cents
     */
    public long getAmountInCents() {
        return (long) amountInDollars * (long) 100;
    }

    /**
     * Set the amount in dollars for the transaction
     *
     * @param amountInDollars amount in dollars - long - monetary amount in dollars
     */
    public void setAmountInDollars(long amountInDollars) {
        this.amountInDollars = amountInDollars;
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

    /**
     * Gets the type of the account
     *
     * @return type - String - type of the account
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the type of the account
     *
     * @param type - String - type of the account
     */
    public void setType(String type) {
        this.type = type;
    }

}
