package edu.carroll.bankapp.service;

import java.util.ArrayList;
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
    public ServiceResponse<Transaction> createTransaction(String name, long amountInDollars, String toFrom,
            Account account) {
        if (name == null || name.equals("")) {
            return new ServiceResponse<Transaction>(null, "Transaction name cannot be blank");
        }
        // Don't accept excessively long transaction names
        if (name.length() > 255) {
            return new ServiceResponse<Transaction>(null, "Transaction name is too long");
        }

        // Don't accept excessively long transaction recipients
        if (toFrom.length() > 255) {
            return new ServiceResponse<Transaction>(null, "Transaction recipient is too long");
        }
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
        return new ServiceResponse<Transaction>(newTransaction, "Transaction created successfully");
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
    public ServiceResponse<Boolean> deleteTransaction(SiteUser loggedInUser, Transaction transaction) {
        // Make sure the account is the current user's to delete
        if (!loggedInUser.owns(transaction)) {
            log.warn("{} tried to delete transaction \"{}\" belonging to {}",
                    loggedInUser.getUsername(),
                    transaction.getName(), transaction.getOwner());
            // User may be trying to do something bad, don't tell them anything useful
            return new ServiceResponse<Boolean>(false, "Something went wrong");
        }

        // Update the account balance
        transaction.getAccount().subtractBalanceInCents(transaction.getAmountInCents());
        // Remove transaction from account
        transaction.getAccount().removeTransaction(transaction);
        // Save changes to account
        accountRepo.save(transaction.getAccount());

        // TODO: This is hacky and bad!
        if (transaction.getName().startsWith("Transfer from ") || transaction.getName().startsWith("Transfer to ")) {
            deleteOtherTransferTransaction(transaction, loggedInUser);
        }

        // Delete transaction from database
        transactionRepo.delete(transaction);

        log.info("Deleted transaction: {}", transaction.getName());
        return new ServiceResponse<Boolean>(true, "Deleted transaction");
    }

    /**
     * Given one transaction in a transfer, delete the other transaction.
     * THE PROJECT SHOULD BE REDESIGNED SO THIS FUNCTION IS UNNECESSARY!
     *
     * @param givenTransaction - One transaction in a transfer
     * @param loggedInUser     - The current user
     * @return whether or not the operation succeeded
     */
    private ServiceResponse<Boolean> deleteOtherTransferTransaction(Transaction givenTransaction,
            SiteUser loggedInUser) {
        // Determine whether transaction is sending or recieving money
        String type = null;
        if (givenTransaction.getName().contains("to")) {
            // Transaction is sending money to an account
            type = "to";
        } else if (givenTransaction.getName().contains("from")) {
            // Transaction is recieving money from an account
            type = "from";
        } else {
            // Transaction doesn't contain "to" or "from". Are we sure it's a transfer?
            return new ServiceResponse<Boolean>(false, "Transfer deletion failed...");
        }
        // Determine the name of the other account involved in the transfer
        String otherAccountName = givenTransaction.getName().replace("Transfer to ", "").replace("Transfer from ", "");

        // Get the user's accounts
        List<Account> userAccounts = accountRepo.findByOwner(loggedInUser);

        // Find the account with a matching name
        Account otherAccount = null;
        for (Account userAccount : userAccounts) {
            if (userAccount.getName().equals(otherAccountName)) {
                otherAccount = userAccount;
            }
        }
        // See if we found the account we're looking for
        if (otherAccount == null) {
            log.info("Couldn't find other transfer account");
            return new ServiceResponse<Boolean>(false, "Failed to locate other transfer transaction");
        }

        // Look for transactions in the other account that could be the other half of
        // the transfer
        List<Transaction> potentialMatchingTransactions = new ArrayList<>();
        for (Transaction accountTransaction : otherAccount.getTransactions()) {
            // Make sure they're the same amount
            if (accountTransaction.getAmountInDollars() != givenTransaction.getAmountInDollars()) {
                continue;
            }
            // See if the transaction name looks right
            if (type.equals("to")) {
                if (accountTransaction.getName()
                        .startsWith(String.format("Transfer from %s", givenTransaction.getAccount().getName()))) {
                    // Transaction name looks right, add it
                    potentialMatchingTransactions.add(accountTransaction);
                }
            } else {
                if (accountTransaction.getName()
                        .startsWith(String.format("Transfer to %s", givenTransaction.getAccount().getName()))) {
                    // Transaction name looks right, add it
                    potentialMatchingTransactions.add(accountTransaction);
                }
            }
        }
        // If the two transactions were created more than five minutes apart,
        // they're probably not the same transfer
        Transaction closestTransaction = getClosestTransaction(givenTransaction, potentialMatchingTransactions);

        // We found it! Delete it
        if (closestTransaction != null) {
            // Update the account balance
            closestTransaction.getAccount().subtractBalanceInCents(closestTransaction.getAmountInCents());
            // Remove transaction from account
            closestTransaction.getAccount().removeTransaction(closestTransaction);
            // Save changes to account
            accountRepo.save(closestTransaction.getAccount());
            // Delete the transaction
            transactionRepo.delete(closestTransaction);
            return new ServiceResponse<Boolean>(true, "Deleted transfer transaction");
        }
        return new ServiceResponse<Boolean>(false, "Something went wrong");
    }

    /**
     * @param givenTransaction
     * @param potentialMatchingTransactions
     * @return
     */
    private Transaction getClosestTransaction(Transaction givenTransaction,
            List<Transaction> potentialMatchingTransactions) {
        long minDiff = 1000 * 60 * 5;
        Transaction closestTransaction = null;

        // If there are multiple transactions that have the right name, such as if there
        // are multiple transfers, find the one with the closes creation date to the
        // given transaction
        for (Transaction potentialMatchingTransaction : potentialMatchingTransactions) {
            long diff = Math
                    .abs(potentialMatchingTransaction.getDate().getTime() - givenTransaction.getDate().getTime());
            if (diff < minDiff) {
                minDiff = diff;
                closestTransaction = potentialMatchingTransaction;
            }
        }
        return closestTransaction;
    }

    public ServiceResponse<Boolean> createTransfer(Account toAccount, Account fromAccount, long amountInDollars) {
        if (toAccount.getId() == fromAccount.getId()) {
            return new ServiceResponse<Boolean>(false, "Cannot transfer money from an account to itself");
        }
        // Withdraw from the fromAccount
        ServiceResponse<Transaction> toResponse = createTransaction(
                String.format("Transfer to %s", toAccount.getName()),
                -1 * amountInDollars,
                toAccount.getName(),
                fromAccount);

        // Income into the toAccount
        ServiceResponse<Transaction> fromResponse = createTransaction(
                String.format("Transfer from %s", fromAccount.getName()),
                amountInDollars,
                fromAccount.getName(),
                toAccount);

        // If either transaction creation fails, delete it all and bail out
        if (toResponse.getResult() == null) {
            transactionRepo.delete(fromResponse.getResult());
            return new ServiceResponse<Boolean>(false,
                    String.format("Failed to create to transaction: %s", toResponse.getMessage()));
        }
        if (fromResponse.getResult() == null) {
            transactionRepo.delete(toResponse.getResult());
            return new ServiceResponse<Boolean>(false,
                    String.format("Failed to create from transaction: %s", toResponse.getMessage()));
        }
        return new ServiceResponse<Boolean>(true, "Transfer created");
    }
}
