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
    private long amountInCents;

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
     * @return amountInCents - long - monetary amount in US cents
     */
    public long getAmountInCents() {
        return amountInCents;
    }

    /**
     * Gets the transaction amount in US dollars
     *
     * @return dollar amount - long - the conversion of US cents to US dollars
     */
    public long getAmountInDollars() {
        return (long) amountInCents / (long) 100;
    }

    /**
     * Sets the transaction amount in US cents
     *
     * @param amountInCents - Integer - transaction amount in US cents
     */
    public void setAmountInCents(long amountInCents) {
        this.amountInCents = amountInCents;
    }

    /**
     * Sets the transaction amount in US dollars
     *
     * @param amountInDollars - Long - transaction amount in US dollars (anything
     *                        less than a penny will be lost)
     */
    public void setAmountInDollars(long amountInDollars) {
        setAmountInCents((long) (amountInDollars * 100));
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

    /**
     * Getter method for the creation date of the transaction
     * 
     * @return creation date
     */
    public Date getDate() {
        return date;
    }

    /**
     * Setter method for the creation date of the transaction
     * 
     * @param date date to set
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * Comparison method for sorting transactions by creation date
     */
    @Override
    public int compareTo(Transaction t) {
        return getDate().compareTo(t.getDate());
    }
}
