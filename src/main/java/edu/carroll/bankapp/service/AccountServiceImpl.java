package edu.carroll.bankapp.service;

import edu.carroll.bankapp.jpa.model.Account;
import edu.carroll.bankapp.jpa.model.SiteUser;
import edu.carroll.bankapp.jpa.repo.AccountRepository;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service for managing accounts.
 */
@Service
public class AccountServiceImpl implements AccountService {
    private static final Logger log = LoggerFactory.getLogger(AccountServiceImpl.class);

    private final AccountRepository accountRepo;
    private final TransactionService transactionService;

    /**
     * Default constructor
     *
     * @param accountRepo        - account database repo
     * @param transactionService - for creating starting transactions
     */
    public AccountServiceImpl(AccountRepository accountRepo, TransactionService transactionService) {
        this.accountRepo = accountRepo;
        this.transactionService = transactionService;
    }

    /**
     * Returns a list of Accounts owned by the given user
     *
     * @param user the user to get accounts for
     * @return list of accounts
     */
    public List<Account> getUserAccounts(SiteUser user) {
        if (user == null) {
            return new ArrayList<>();
        }
        return accountRepo.findByOwner(user);
    }

    /**
     * Returns the Account matching the given id, if the account is owned by the
     * currently logged-in user.
     * Returns null if the requested account is owned by someone else.
     *
     * @param id the id of the requested account
     * @return account/null
     */
    public Account getUserAccount(SiteUser loggedInUser, int id) {
        List<Account> accounts = accountRepo.findById(id);
        // Check if account exists
        if (accounts == null || accounts.isEmpty()) {
            log.info("Account with id {} doesn't exist", id);
            return null;
            // Check if we have two accounts with the same id. This would be very bad
        } else if (accounts.size() > 1) {
            log.error("Got multiple accounts with id {}. Bailing out", id);
            return null;
        }
        Account account = accounts.get(0);
        // Make sure the account actually belongs to the current user
        if (!loggedInUser.owns(account)) {
            // Log the attempted access
            String currentUsername = loggedInUser.getUsername();
            String accountOwnerUsername = account.getOwner().getUsername();
            log.warn("{} attempted to access one of {}'s accounts", currentUsername, accountOwnerUsername);
        }
        return account;
    }

    /**
     * Create an account and save it in the database
     */
    public ServiceResponse<Account> createAccount(String accountName, Long balanceInDollars, SiteUser owner) {
        // Don't accept negative starting balance
        if (balanceInDollars < 0) {
            return new ServiceResponse<Account>(null, "Account balance cannot be negative");
        }
        // Don't accept account names longer than the DB schema will allow
        if (accountName.length() > 255) {
            return new ServiceResponse<Account>(null, "Account name too long");
        }

        List<Account> ownerAccounts = getUserAccounts(owner);
        // Prevent user from creating two accounts with the same name
        for (Account account : ownerAccounts) {
            if (account.getName().equals(accountName)) {
                log.info("{} tried to create two accounts named {}", owner.getUsername(), accountName);
                return new ServiceResponse<Account>(null, "You already have an account named " + accountName);
            }
        }

        log.info("Creating account named {} for user {}", accountName, owner.getUsername());
        // Create and save a new account
        Account newAccount = new Account();
        newAccount.setName(accountName);
        newAccount.setOwner(owner);
        // Set the balance in a transaction instead of just starting with money
        newAccount.setBalanceInCents(0);
        accountRepo.save(newAccount);

        transactionService.createTransaction("Starting Balance", balanceInDollars, "", newAccount);
        return new ServiceResponse<Account>(newAccount, "Account created successfully");
    }

    /**
     * Delete the given account
     *
     * @param loggedInUser - for ownership check
     * @param account      - the account to be deleted
     * @return true if successful, false if failed
     */
    public ServiceResponse<Boolean> deleteAccount(SiteUser loggedInUser, Account account) {
        // Make sure the user can delete this account, then delete it
        if (loggedInUser.owns(account)) {
            accountRepo.delete(account);
            return new ServiceResponse<Boolean>(true, "Deleted account");
        }
        return new ServiceResponse<Boolean>(false, "Failed to delete account");
    }

    /**
     * Delete the account with the given id
     * 
     * @param loggedInUser - for ownership check
     * @param accountID    - the id of the account to be deleted
     * @return true if successful, false if failed
     */
    public ServiceResponse<Boolean> deleteAccount(SiteUser loggedInUser, int accountID) {
        // Look up the account
        Account account = getUserAccount(loggedInUser, accountID);
        if (account == null) {
            log.warn("{} tried to delete a null account", loggedInUser.getUsername());
            return new ServiceResponse<Boolean>(false, "Failed to locate account");
        }
        // Try to delete it
        return deleteAccount(loggedInUser, account);
    }
}
