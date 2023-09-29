package edu.carroll.bankapp.jpa.model;


import jakarta.persistence.*;

/**
 * A transaction within the system
 */
@Entity
@Table(name = "transaction")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "to_from", nullable = false)
    private String toFrom;

    @Column(name = "amount")
    private Integer amountInCents;

    @Column(name = "name")
    private String name;

    @ManyToOne
    private Account account;

    public Transaction() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getToFrom() {
        return toFrom;
    }

    public void setToFrom(String toFrom) {
        this.toFrom = toFrom;
    }

    public Integer getAmountInCents() {
        return amountInCents;
    }

    public double getAmountInDollars() {
        return (double) amountInCents / (double) 100;
    }

    public void setAmountInCents(Integer amountInCents) {
        this.amountInCents = amountInCents;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }
}
