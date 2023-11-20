package edu.carroll.bankapp.jpa.model;

import jakarta.persistence.*;

import java.util.*;

import edu.carroll.bankapp.Ownable;

/**
 * An account within the application (i.e. checking, savings)
 */
@Entity
@Table(name = "account")
public class Account implements Ownable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @ManyToOne
    private SiteUser owner;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Transaction> transactions;

    // No money in floating points because Nate who's worked in financial
    // software for years and years and years would make fun of us
    @Column(name = "balance_in_cents", nullable = false)
    private long balanceInCents;
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * Returns the name of the account
     *
     * @return name - String - the name of the account
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the account
     *
     * @param name - String - the given name of the account
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the ID of the account
     *
     * @return id - auto-incrementing Integer - the id of the account
     */
    public Integer getId() {
        return id;
    }

    /**
     * Sets the ID of the account
     *
     * @param id - Integer - the id of the account
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Returns the user associated with the account (who 'owns' the account)
     *
     * @return owner - User - the owner of the account
     */
    public SiteUser getOwner() {
        return owner;
    }

    /**
     * Sets the user associated with the account (the 'owner' of the account)
     *
     * @param owner - User - the owner of the account
     */
    public void setOwner(SiteUser owner) {
        this.owner = owner;
    }

    /**
     * Gets a list of all the transactions
     *
     * @return transactions- list of transactions
     */
    public List<Transaction> getTransactions() {
        if (this.transactions == null) {
            return new ArrayList<>();
        }
        // Convert set to list
        List<Transaction> fetchedTransactions = new ArrayList<>(this.transactions);
        // Sort by date
        Collections.sort(fetchedTransactions);
        return fetchedTransactions;
    }

    /**
     * Sets a list of transactions
     *
     * @param transactions - Set - list of transactions
     */
    public void setTransactions(Set<Transaction> transactions) {
        this.transactions = transactions;
    }

    /**
     * Add a transaction to the set of transactions in this account
     * 
     * @param transaction the transaction to be added
     */
    public void addTransaction(Transaction transaction) {
        // If we don't have any transactions, create a set
        if (this.transactions == null) {
            this.transactions = new HashSet<>();
        }
        this.transactions.add(transaction);
    }

    /**
     * Gets the account balance in cents (there should not be floating point values)
     *
     * @return balanceInCents - long - the account balance in cents
     */
    public long getBalanceInCents() {
        return this.balanceInCents;
    }

    /**
     * Returns the account balance in dollars using simple math
     *
     * @return long - returns account balance in dollars (long)
     */
    public long getBalanceInDollars() {
        return (long) balanceInCents / (long) 100;
    }

    /**
     * Sets the account balance in cents
     *
     * @param balanceInCents - long - account balance in cents
     */
    public void setBalanceInCents(long balanceInCents) {
        this.balanceInCents = balanceInCents;
    }

    /**
     * Increments the balance by additionalAmount
     * 
     * @param additionalAmount the amount to add to the balance
     */
    public void addBalanceInCents(long additionalAmount) {
        this.balanceInCents += additionalAmount;

    }

    /**
     * Decrements the balance by subtractionAmount
     * 
     * @param subtractionAmount the amount to subract from the balance
     */
    public void subtractBalanceInCents(long subtractionAmount) {
        this.balanceInCents -= subtractionAmount;

    }

    /**
     * Removes a transaction from this account
     * 
     * @param transaction to be removed
     */
    public void removeTransaction(Transaction transaction) {
        this.transactions.remove(transaction);
    }
}
