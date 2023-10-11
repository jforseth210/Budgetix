package edu.carroll.bankapp.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import edu.carroll.bankapp.jpa.model.Account;
import edu.carroll.bankapp.jpa.model.SiteUser;
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
        log.info("Creating transaction with name: {} and account: {}", name, account.getName());
        Transaction newTransaction = new Transaction();
        newTransaction.setName(name);
        newTransaction.setAmountInDollars(amountInDollars);
        newTransaction.setToFrom(toFrom);
        newTransaction.setAccount(account);
        transactionRepo.save(newTransaction);
        account.subtractBalanceInCents(newTransaction.getAmountInCents());
        accountService.saveAccount(account);
        return newTransaction;
    }

    /**
     * Create and save a new transaction for the logged-in user from a
     * newTransactionForm
     */
    public Transaction createTransaction(SiteUser loggedInUser, NewTransactionForm newTransactionForm) {
        Account account = accountService.getUserAccount(loggedInUser, newTransactionForm.getAccountId());
        return createTransaction(newTransactionForm.getName(), newTransactionForm.getAmountInDollars(),
                newTransactionForm.getToFrom(), account);
    }

    /**
     * Get a transaction from the given id (and make sure it belongs to the current
     * user)
     */
    public Transaction getUserTransaction(SiteUser loggedInUser, int id) {
        List<Transaction> transactions = transactionRepo.findById(id);
        if (transactions.isEmpty()) {
            log.warn("Account with id {} doesn't exist", id);
            return null;
        } else if (transactions.size() > 1) {
            log.error("Got multiple accounts with id {}. Bailing out", id);
            throw new IllegalStateException();
        }
        Transaction transaction = transactions.get(0);
        if (loggedInUser.owns(transaction)) {
            return transaction;
        } else {
            log.warn("{} tried to access transaction \"{}\" belonging to {}",
                    loggedInUser.getUsername(),
                    transaction.getName(), transaction.getOwner());
        }
        return null;
    }

    /**
     * Delete the given transaction if owned by the currently logged-in user
     */
    public void deleteTransaction(SiteUser loggedInUser, Transaction transaction) {
        if (loggedInUser.owns(transaction)) {
            transaction.getAccount().addBalanceInCents(transaction.getAmountInCents());
            accountService.saveAccount(transaction.getAccount());

            transactionRepo.delete(transaction);
            log.info("Deleted transaction: {}", transaction.getName());
        } else {
            log.warn("{} tried to delete transaction \"{}\" belonging to {}",
                    loggedInUser.getUsername(),
                    transaction.getName(), transaction.getOwner());
        }
    }
}
