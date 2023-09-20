package edu.carroll.bankapp.jpa.model;


import jakarta.persistence.*;

@Entity
@Table(name = "transaction")
public class Transaction {
    @Id
    @GeneratedValue
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
}
