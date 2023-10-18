package edu.carroll.bankapp.jpa.model;

import java.util.Date;

import edu.carroll.bankapp.Ownable;
import jakarta.persistence.*;

/**
 * A transaction within the system
 */
@Entity
@Table(name = "transaction")
public class Transaction implements Ownable, Comparable<Transaction> {
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

    @Column(name = "date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    /**
     * Default Constructor
     */
    public Transaction() {
    }

    /**
     * Get the SiteUser that owns this transaction
     */
    public SiteUser getOwner() {
        return getAccount().getOwner();
    }

    /**
     * Gets the id of the transaction
     *
     * @return id - Integer - id of transaction
     */
    public Integer getId() {
        return id;
    }

    /**
     * Sets the id of the transaction
     *
     * @param id - Integer - id of transaction
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Gets the account from which the money was given/taken
     *
     * @return toFrom - String - who the money came from
     */
    public String getToFrom() {
        return toFrom;
    }

    /**
     * Sets the account from which the money was given/taken
     *
     * @param toFrom - String - who the money came from
     */
    public void setToFrom(String toFrom) {
        this.toFrom = toFrom;
    }

    /**
     * Gets the transaction amount in cents
     *
     * @return amountInCents - Integer - monetary amount in US cents
     */
    public Integer getAmountInCents() {
        return amountInCents;
    }

    /**
     * Gets the transaction amount in US dollars
     *
     * @return dollar amount - double - the conversion of US cents to US dollars
     */
    public double getAmountInDollars() {
        return (double) amountInCents / (double) 100;
    }

    /**
     * Sets the transaction amount in US cents
     *
     * @param amountInCents - Integer - transaction amount in US cents
     */
    public void setAmountInCents(Integer amountInCents) {
        this.amountInCents = amountInCents;
    }

    /**
     * Sets the transaction amount in US dollars
     *
     * @param amountInDollars - Double - transaction abount in US dollars (anything
     *                        less than a penny will be lost)
     */
    public void setAmountInDollars(double amountInDollars) {
        setAmountInCents((int) (amountInDollars * 100));
    }

    /**
     * Gets the name for the transaction
     *
     * @return name - String - name of transaction
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name for the transaction
     *
     * @param name - String - name of transaction
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the user's account
     *
     * @return account - Account - account of user
     */
    public Account getAccount() {
        return account;
    }

    /**
     * Sets the user's account
     *
     * @param account - Account - account of user
     */
    public void setAccount(Account account) {
        this.account = account;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public int compareTo(Transaction t) {
        return getDate().compareTo(t.getDate());
    }
}
