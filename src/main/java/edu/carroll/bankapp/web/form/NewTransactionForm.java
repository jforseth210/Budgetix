package edu.carroll.bankapp.web.form;


public class NewTransactionForm {

    private String toFrom;

    private Integer amountInCents;

    private String name;

    private Integer accountId;


    public NewTransactionForm() {
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

    public Integer getAccountId() {
        return this.accountId;
    }

    public void setAccountId(Integer id) {
        this.accountId = id;
    }
}
