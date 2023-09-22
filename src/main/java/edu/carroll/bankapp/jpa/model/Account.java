package edu.carroll.bankapp.jpa.model;

import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "account")
public class Account {
    @Id
    @GeneratedValue
    private Integer id;
    @ManyToOne
    private User owner;

    @OneToMany
    private Set<Transaction> transactions;

    // No money in floating points because Nate who's worked in financial
    // software for years and years and years won't like it
    @Column(name = "balance_in_cents", nullable = false)
    private Integer balanceInCents;
    @Column(name = "name", nullable = false)
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public Set<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(Set<Transaction> transactions) {
        this.transactions = transactions;
    }

    public Integer getBalanceInCents() {
        return balanceInCents;
    }

    public double getBalanceInDollars() {
        return (double) balanceInCents / (double) 100;
    }

    public void setBalanceInCents(Integer balanceInCents) {
        this.balanceInCents = balanceInCents;
    }

    /**
     * Hibernate wants a default constructor
     */
    public Account() {

    }
}
