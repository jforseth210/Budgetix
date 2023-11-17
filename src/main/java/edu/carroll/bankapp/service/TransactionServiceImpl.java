package edu.carroll.bankapp.service;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import edu.carroll.bankapp.jpa.model.Account;
import edu.carroll.bankapp.jpa.model.SiteUser;
import edu.carroll.bankapp.jpa.model.Transaction;
import edu.carroll.bankapp.jpa.repo.AccountRepository;
import edu.carroll.bankapp.jpa.repo.TransactionRepository;

/**
 * Service for managing accounts.
 */
@Service
public class TransactionServiceImpl implements TransactionService {
    private static final Logger log = LoggerFactory.getLogger(TransactionServiceImpl.class);

    private final TransactionRepository transactionRepo;
    private final AccountRepository accountRepo;

    /**
     * Inject dependencies
     *
     * @param transactionRepo - JPA repo for querying transactions
     * @param accountRepo     - JPA repo for querying accounts
     */
    public TransactionServiceImpl(TransactionRepository transactionRepo, AccountRepository accountRepo) {
        this.transactionRepo = transactionRepo;
        this.accountRepo = accountRepo;
    }

    /**
     * Create and save a new transaction in the database
     */
    public Transaction createTransaction(String name, long amountInDollars, String toFrom, Account account) {
        log.info("Creating transaction with name: {} and account: {}", name, account.getName());
        // Create the transaction
        Transaction newTransaction = new Transaction();
        // Populate its fields
        newTransaction.setName(name);
        newTransaction.setAmountInDollars(amountInDollars);
        newTransaction.setToFrom(toFrom);
        newTransaction.setAccount(account);
        newTransaction.setDate(new Date());
        // Save the transaction
        transactionRepo.save(newTransaction);
        // Update the account balance
        account.addBalanceInCents(newTransaction.getAmountInCents());
        // Add the transaction to the account
        account.addTransaction(newTransaction);
        // Save the account
        accountRepo.save(account);
        return newTransaction;
    }

    public Transaction getUserTransaction(SiteUser loggedInUser, int id) {
        List<Transaction> transactions = transactionRepo.findById(id);
        // Make sure the transaction exists
        if (transactions == null || transactions.isEmpty()) {
            log.info("Transaction with id {} doesn't exist", id);
            return null;
            // Make sure there's only one account with the given id
        } else if (transactions.size() > 1) {
            log.error("Got multiple accounts with id {}. Bailing out", id);
            return null;
        }

        Transaction transaction = transactions.get(0);
        // Make sure the transaction belongs to the current user
        if (!loggedInUser.owns(transaction)) {
            log.warn("{} tried to access transaction \"{}\" belonging to {}",
                    loggedInUser.getUsername(),
                    transaction.getName(), transaction.getOwner());
            return null;
        }
        return transaction;
    }

    /**
     * Delete the given transaction if owned by the currently logged-in user
     */
    public void deleteTransaction(SiteUser loggedInUser, Transaction transaction) {
        // Make sure the account is the current user's to delete
        if (!loggedInUser.owns(transaction)) {
            log.warn("{} tried to delete transaction \"{}\" belonging to {}",
                    loggedInUser.getUsername(),
                    transaction.getName(), transaction.getOwner());
            return;
        }

        // Update the account balance
        transaction.getAccount().subtractBalanceInCents(transaction.getAmountInCents());
        // Remove transaction from account
        transaction.getAccount().removeTransaction(transaction);
        // Save changes to account
        accountRepo.save(transaction.getAccount());

        // Delete transaction from database
        transactionRepo.delete(transaction);

        log.info("Deleted transaction: {}", transaction.getName());
    }

    public boolean createTransfer(Account toAccount, Account fromAccount, double amountInDollars) {

        // Withdraw from the fromAccount
        Transaction toTransaction = createTransaction(
                String.format("Transfer to %s", toAccount.getName()),
                -1 * amountInDollars,
                toAccount.getName(),
                fromAccount);

        // Income into the toAccount
        Transaction fromTransaction = createTransaction(
                String.format("Transfer from %s", fromAccount.getName()),
                amountInDollars,
                fromAccount.getName(),
                toAccount);

        // If either transaction creation fails, delete it all and bail out
        if (toTransaction == null) {
            transactionRepo.delete(fromTransaction);
            return false;
        }
        if (fromTransaction == null) {
            transactionRepo.delete(toTransaction);
            return false;
        }
        return true;
    }
}
