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

    public TransactionServiceImpl(TransactionRepository transactionRepository, AccountRepository accountRepo) {
        this.transactionRepo = transactionRepository;
        this.accountRepo = accountRepo;
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
        newTransaction.setDate(new Date());
        transactionRepo.save(newTransaction);
        account.addBalanceInCents(newTransaction.getAmountInCents());
        account.addTransaction(newTransaction);
        accountRepo.save(account);
        return newTransaction;
    }

    public Transaction getUserTransaction(SiteUser loggedInUser, int id) {
        List<Transaction> transactions = transactionRepo.findById(id);
        if (transactions == null || transactions.isEmpty()) {
            log.warn("Account with id {} doesn't exist", id);
            return null;
        } else if (transactions.size() > 1) {
            log.error("Got multiple accounts with id {}. Bailing out", id);
            return null;
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
            transaction.getAccount().subtractBalanceInCents(transaction.getAmountInCents());
            accountRepo.save(transaction.getAccount());

            transactionRepo.delete(transaction);
            transaction.getAccount().removeTransaction(transaction);
            log.info("Deleted transaction: {}", transaction.getName());
        } else {
            log.warn("{} tried to delete transaction \"{}\" belonging to {}",
                    loggedInUser.getUsername(),
                    transaction.getName(), transaction.getOwner());
        }
    }
}
