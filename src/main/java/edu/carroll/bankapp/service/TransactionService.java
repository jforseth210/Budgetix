package edu.carroll.bankapp.service;

import edu.carroll.bankapp.jpa.model.Account;
import edu.carroll.bankapp.jpa.model.SiteUser;
import edu.carroll.bankapp.jpa.model.Transaction;

/**
 * Interface for managing transactions.
 */
public interface TransactionService {
    /**
     * Create and save a new transaction in the database.
     *
     * @param name            the name of the transaction
     * @param amountInDollars the amount in dollars
     * @param toFrom          the recipient/sender of the transaction
     * @param account         the account associated with the transaction
     * @return the created transaction
     */
    ServiceResponse<Transaction> createTransaction(String name, long amountInDollars, String toFrom, Account account);

    /**
     * Get a transaction from the given id (and make sure it belongs to the current
     * user).
     *
     * @param loggedInUser the currently logged-in user
     * @param id           the id of the transaction
     * @return the transaction if found and owned by the user, otherwise null
     */
    Transaction getUserTransaction(SiteUser loggedInUser, int id);

    /**
     * Delete the given transaction if owned by the currently logged-in user.
     *
     * @param loggedInUser the currently logged-in user
     * @param transaction  the transaction to be deleted
     */
    ServiceResponse<Boolean> deleteTransaction(SiteUser loggedInUser, Transaction transaction);

    /**
     * Create a transfer between two accounts
     * 
     * @param toAccount   - the account to deposit money to
     * @param fromAccount - the account to withdraw money from
     * @param amount      - the amount of money to transfer
     * @return true if successful, false if not
     */
    ServiceResponse<Boolean> createTransfer(Account toAccount, Account fromAccount, long amount);
}
