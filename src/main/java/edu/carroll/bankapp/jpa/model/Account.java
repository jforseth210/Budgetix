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

    /**
     * Hibernate wants a default constructor
     */
    public Account() {

    }
}
