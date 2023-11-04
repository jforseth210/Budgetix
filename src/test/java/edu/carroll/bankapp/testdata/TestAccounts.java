package edu.carroll.bankapp.testdata;

import org.springframework.stereotype.Component;

import edu.carroll.bankapp.jpa.model.Account;
import edu.carroll.bankapp.jpa.model.SiteUser;
import edu.carroll.bankapp.service.AccountService;

/**
 * Some generic accounts that can be created for testing purposes
 */
@Component
public class TestAccounts {
    AccountService accountService;

    public TestAccounts(AccountService accountService) {
        this.accountService = accountService;
    }

    public Account createSavings(SiteUser owner) {
        return accountService.createAccount(String.format("%s's Savings Account", owner.getFullName()), 11597, owner);
    }

    public Account createChecking(SiteUser owner) {
        return accountService.createAccount(String.format("%s's Checking Account", owner.getFullName()), 99104, owner);
    }

    public Account createInvestment(SiteUser owner) {
        return accountService.createAccount(String.format("%s's Investment Account", owner.getFullName()), 105110118,
                owner);
    }

    public Account createMoneyBag(SiteUser owner) {
        return accountService.createAccount("💰💰💰", -128176, owner);
    }

}
