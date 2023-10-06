package edu.carroll.bankapp.jpa.model;

import jakarta.persistence.*;

import java.util.Set;

import edu.carroll.bankapp.Ownable;

/**
 * An account within the application (i.e checking, savings)
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
    private Integer balanceInCents;
    @Column(name = "name", nullable = false, unique = true)
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
     * @return transactions - Set<> - list of transactions
     */
    public Set<Transaction> getTransactions() {
        return this.transactions;
    }

    /**
     * Sets a list of transactions
     *
     * @param transactions - Set<> - list of transactions
     */
    public void setTransactions(Set<Transaction> transactions) {
        this.transactions = transactions;
    }

    /**
     * Gets the account balance in cents (there should not be floating point values)
     *
     * @return balanceInCents - Integer - the account balance in cents
     */
    public Integer getBalanceInCents() {
        return this.balanceInCents;
    }

    /**
     * Returns the account balance in dollars using simple math
     *
     * @return double - returns account balance in dollars (double)
     */
    public double getBalanceInDollars() {
        return (double) balanceInCents / (double) 100;
    }

    /**
     * Sets the account balance in cents
     *
     * @param balanceInCents - Integer - account balance in cents
     */
    public void setBalanceInCents(Integer balanceInCents) {
        this.balanceInCents = balanceInCents;
    }

    /**
     * Hibernate wants a default constructor
     */
    public Account() {

    }
}
