package edu.carroll.bankapp.service;

import edu.carroll.bankapp.jpa.model.Account;
import edu.carroll.bankapp.jpa.model.SiteUser;

import java.util.List;

/**
 * Interface for managing accounts.
 */
public interface AccountService {

    /**
     * Returns a list of Accounts owned by the given user
     * 
     * @param user the user to get accounts for
     * @return list of accounts
     */
    List<Account> getUserAccounts(SiteUser user);

    /**
     * Returns the Account matching the given id, if the account is owned by the
     * currently logged-in user.
     * Returns null if the requested account is owned by someone else.
     *
     * @param loggedInUser the currently logged-in user
     * @param id           the id of the requested account
     * @return account/null
     */
    Account getUserAccount(SiteUser loggedInUser, int id);

    /**
     * Create an account and save it in the database
     *
     * @param accountName      the name of the account
     * @param balanceInDollars the initial balance in dollars
     * @param owner            the owner of the account
     * @return Account if the account was created successfully, null otherwise
     */
    ServiceResponse<Account> createAccount(String accountName, Long balanceInDollars, SiteUser owner);

    /**
     * Delete the given account
     * 
     * @param loggedInUser the currently logged-in user
     * @param account      the account to be deleted
     * @return true if successful, false if failed
     */
    ServiceResponse<Boolean> deleteAccount(SiteUser loggedInUser, Account account);

    /**
     * Delete the account with the given id
     *
     * @param loggedInUser the currently logged-in user
     * @param accountID    the id of the account to be deleted
     * @return true if successful, false if failed
     */
    ServiceResponse<Boolean> deleteAccount(SiteUser loggedInUser, int accountID);
}
