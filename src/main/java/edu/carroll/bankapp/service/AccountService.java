package edu.carroll.bankapp.service;

import edu.carroll.bankapp.jpa.model.Account;
import edu.carroll.bankapp.jpa.model.SiteUser;
import edu.carroll.bankapp.jpa.repo.AccountRepository;
import edu.carroll.bankapp.web.controller.DashboardController;
import edu.carroll.bankapp.web.form.NewAccountForm;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service for managing accounts.
 */
@Service
public class AccountService {
    private static final Logger log = LoggerFactory.getLogger(DashboardController.class);

    private final AccountRepository accountRepo;
    private final UserService userService;

    /**
     * Default constructor
     *
     * @param userService - user
     * @param accountRepo - account
     */
    public AccountService(UserService userService, AccountRepository accountRepo) {
        this.userService = userService;
        this.accountRepo = accountRepo;
    }

    /**
     * Returns a list of Accounts owned by the currently logged-in user
     *
     * @return list of accounts
     */
    public List<Account> getUserAccounts() {
        SiteUser loggedInSiteUser = userService.getLoggedInUser();
        if (loggedInSiteUser == null) {
            return new ArrayList<Account>();
        }
        return getUserAccounts(loggedInSiteUser);
    }

    /**
     * Returns a list of Accounts owned by the given user
     * 
     * @param user the user to get accounts for
     * @return list of accounts
     */
    public List<Account> getUserAccounts(SiteUser user) {
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
    public Account getUserAccount(int id) {
        Account account = accountRepo.findById(id).get(0);
        if (userService.getLoggedInUser().owns(account)) {
            return account;
        }
        String currentUsername = userService.getLoggedInUser().getUsername();
        String accountOwnerUsername = account.getOwner().getUsername();
        log.warn("{} attempted to access one of {}'s accounts", currentUsername, accountOwnerUsername);
        return null;
    }

    /**
     * Create an account and save it in the database
     */
    public boolean createAccount(String accountName, double balanceInDollars, SiteUser owner) {
        // Create and save a new account
        Account newAccount = new Account();
        newAccount.setName(accountName);
        newAccount.setBalanceInCents((int) (balanceInDollars * 100));
        newAccount.setOwner(owner);
        accountRepo.save(newAccount);
        return true;
    }

    /**
     * Account creation that unpacks a newAccountForm
     */
    public boolean createAccount(NewAccountForm newAccountForm) {
        return createAccount(newAccountForm.getAccountName(), newAccountForm.getAccountBalance(),
                userService.getLoggedInUser());
    }

    /**
     * Delete the given account
     * 
     * @param account
     */
    public void deleteAccount(Account account) {
        if (userService.getLoggedInUser().owns(account)) {
            accountRepo.delete(account);
        }
    }

    /**
     * Delete the account with the given id
     * 
     * @param accountID
     */
    public void deleteAccount(int accountID) {
        Account account = getUserAccount(accountID);
        deleteAccount(account);
    }

    /**
     * Delete every account
     */
    public void deleteAllAccounts() {
        log.warn("Deleting all accounts");
        accountRepo.deleteAll();
    }
}
