package edu.carroll.bankapp.service;

import edu.carroll.bankapp.jpa.model.Account;
import edu.carroll.bankapp.jpa.model.SiteUser;
import edu.carroll.bankapp.jpa.repo.AccountRepository;
import edu.carroll.bankapp.web.controller.DashboardController;

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

    private AccountRepository accountRepo;
    private UserService userService;

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
     * @return accountsList - list of accounts
     */
    public List<Account> getUserAccounts() {
        SiteUser loggedInSiteUser = userService.getLoggedInUser();
        if (loggedInSiteUser == null) {
            return null;
        }
        List<Account> accountsList = accountRepo.findByOwner(loggedInSiteUser);
        return accountsList;
    }

    /**
     * Returns the Account matching the given id, if the account is owned by the currently logged-in user.
     * Returns null if the requested account is owned by someone else.
     *
     * @param id the id of the requested account
     * @return account/null
     */
    public Account getUserAccount(int id) {
        Account account = accountRepo.findById(id).get(0);
        String currentUser = userService.getLoggedInUser().getUsername();
        String accountOwner = account.getOwner().getUsername();
        if (!accountOwner.equals(currentUser)) {
            log.warn(currentUser + " attempted to access one of" + accountOwner + "'s accounts");
            return null;
        }
        return account;
    }
}
