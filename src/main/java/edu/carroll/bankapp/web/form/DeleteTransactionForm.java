package edu.carroll.bankapp.web.form;

/**
 * Form object for deleting a transaction
 */
public class DeleteTransactionForm {
    private int transactionId;

    /**
     * Getter for the id of the transaction to be deleted
     * 
     * @return the id of the transaction to be deleted
     */
    public Integer getTransactionId() {
        return transactionId;
    }

    /**
     * Setter for the id of the transaction to be deleted
     * 
     * @param transactionId the id of the transaction to be deleted
     */
    public void setTransactionId(Integer transactionId) {
        this.transactionId = transactionId;
    }
}
