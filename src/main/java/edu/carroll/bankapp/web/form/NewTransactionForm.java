package edu.carroll.bankapp.web.form;


public class NewTransactionForm {

    private String toFrom;

    private double amountInDollars;

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

    public double getAmountInDollars() {
        return amountInDollars;
    }

    public double getAmountInCents() {
        return (double) amountInDollars * (double) 100;
    }

    public void setAmountInDollars(double amountInDollars) {
        this.amountInDollars = amountInDollars;
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
