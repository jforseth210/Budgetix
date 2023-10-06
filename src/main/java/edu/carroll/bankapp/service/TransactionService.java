package edu.carroll.bankapp.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import edu.carroll.bankapp.jpa.model.Account;
import edu.carroll.bankapp.jpa.model.Transaction;
import edu.carroll.bankapp.jpa.repo.TransactionRepository;
import edu.carroll.bankapp.web.form.NewTransactionForm;

/**
 * Service for managing accounts.
 */
@Service
public class TransactionService {
    private static final Logger log = LoggerFactory.getLogger(TransactionService.class);

    private final TransactionRepository transactionRepo;
    private final AccountService accountService;
    private final UserService userService;

    public TransactionService(TransactionRepository transactionRepository, AccountService accountService,
            UserService userService) {
        this.transactionRepo = transactionRepository;
        this.accountService = accountService;
        this.userService = userService;
    }

    /**
     * Create and save a new transaction in the database
     */
    public Transaction createTransaction(String name, double amountInDollars, String toFrom, Account account) {
        Transaction newTransaction = new Transaction();
        newTransaction.setName(name);
        newTransaction.setAmountInDollars(amountInDollars);
        newTransaction.setToFrom(toFrom);
        newTransaction.setAccount(account);
        transactionRepo.save(newTransaction);
        return newTransaction;
    }

    /**
     * Create and save a new transaction for the logged-in user from a
     * newTransactionForm
     */
    public Transaction createTransaction(NewTransactionForm newTransactionForm) {
        Account account = accountService.getUserAccount(newTransactionForm.getAccountId());
        return createTransaction(newTransactionForm.getName(), newTransactionForm.getAmountInDollars(),
                newTransactionForm.getToFrom(), account);
    }

    /**
     * Get a transaction from the given id (and make sure it belongs to the current
     * user)
     */
    public Transaction getUserTransaction(int transactionId) {
        // Use Spring Data JPA to find and delete the transaction by ID
        Transaction transaction = transactionRepo.findById(transactionId).get(0);
        if (userService.getLoggedInUser().owns(transaction)) {
            return transaction;
        }
        return null;
    }

    /**
     * Delete the given transaction if owned by the currently logged-in user
     */
    public void deleteTransaction(Transaction transaction) {
        if (userService.getLoggedInUser().owns(transaction)) {
            transactionRepo.delete(transaction);
        }
    }
}
