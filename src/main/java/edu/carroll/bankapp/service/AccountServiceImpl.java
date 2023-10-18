package edu.carroll.bankapp.service;

import edu.carroll.bankapp.jpa.model.Account;
import edu.carroll.bankapp.jpa.model.SiteUser;
import edu.carroll.bankapp.jpa.repo.AccountRepository;
import edu.carroll.bankapp.web.controller.DashboardController;

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
    private static final Logger log = LoggerFactory.getLogger(DashboardController.class);

    private final AccountRepository accountRepo;
    private final UserServiceImpl userServiceImpl;

    /**
     * Default constructor
     *
     * @param userServiceImpl - user
     * @param accountRepo     - account
     */
    public AccountServiceImpl(UserServiceImpl userServiceImpl, AccountRepository accountRepo) {
        this.userServiceImpl = userServiceImpl;
        this.accountRepo = accountRepo;
    }

    /**
     * Save changes made to an account using setters
     */
    public Account saveAccount(Account account) {
        return accountRepo.save(account);
    }

    /**
     * Returns a list of Accounts owned by the given user
     *
     * @param user the user to get accounts for
     * @return list of accounts
     */
    public List<Account> getUserAccounts(SiteUser user) {
        if (user == null) {
            return new ArrayList<Account>();
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
        if (accounts.isEmpty()) {
            log.warn("Account with id {} doesn't exist", id);
            return null;
        } else if (accounts.size() > 1) {
            log.error("Got multiple accounts with id {}. Bailing out", id);
            throw new IllegalStateException();
        }
        Account account = accounts.get(0);
        if (loggedInUser.owns(account)) {
            return account;
        }
        String currentUsername = loggedInUser.getUsername();
        String accountOwnerUsername = account.getOwner().getUsername();
        log.warn("{} attempted to access one of {}'s accounts", currentUsername, accountOwnerUsername);
        return null;
    }

    /**
     * Create an account and save it in the database
     */
    public boolean createAccount(String accountName, double balanceInDollars, SiteUser owner) {
        List<Account> ownerAccounts = getUserAccounts(owner);
        for (Account account : ownerAccounts) {
            if (account.getName().equals(accountName)) {
                log.info("{} tried to create two accounts named {}", accountName);
                return false;
            }
        }
        // Create and save a new account
        Account newAccount = new Account();
        newAccount.setName(accountName);
        newAccount.setBalanceInCents((int) (balanceInDollars * 100));
        newAccount.setOwner(owner);
        accountRepo.save(newAccount);
        return true;
    }

    /**
     * Delete the given account
     *
     * @param account
     */
    public void deleteAccount(SiteUser loggedInUser, Account account) {
        if (loggedInUser.owns(account)) {
            accountRepo.delete(account);
        }
    }

    /**
     * Delete the account with the given id
     *
     * @param accountID
     */
    public void deleteAccount(SiteUser loggedInUser, int accountID) {
        Account account = getUserAccount(loggedInUser, accountID);
        if (account == null) {
            log.warn("{} tried to delete a null account", loggedInUser.getUsername());
            return;
        }
        deleteAccount(loggedInUser, account);
    }

    /**
     * Delete every account
     */
    public void deleteAllAccounts() {
        log.warn("Deleting all accounts");
        accountRepo.deleteAll();
    }
}
