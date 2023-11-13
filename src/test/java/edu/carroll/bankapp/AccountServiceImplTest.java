package edu.carroll.bankapp;

import edu.carroll.bankapp.jpa.model.Account;
import edu.carroll.bankapp.jpa.model.SiteUser;
import edu.carroll.bankapp.service.AccountService;
import edu.carroll.bankapp.testdata.TestAccounts;
import edu.carroll.bankapp.testdata.TestUsers;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
public class AccountServiceImplTest {
    @Autowired
    private AccountService accountService;

    @Autowired
    private TestUsers testUsers;
    @Autowired
    private TestAccounts testAccounts;

    @Test
    public void testGetUserAccounts() {
        // Create some users
        SiteUser john = testUsers.createJohnDoe();
        SiteUser jane = testUsers.createJaneSmith();

        // Make some accounts for john
        List<Account> createdAccounts = new ArrayList<>();
        createdAccounts.add(testAccounts.createChecking(john));
        createdAccounts.add(testAccounts.createSavings(john));

        // Make an account for jane
        testAccounts.createInvestment(jane);

        // Get john's accounts
        List<Account> retrievedAccounts = accountService.getUserAccounts(john);

        // See if result makes sense
        assertNotNull(retrievedAccounts);
        assertEquals(retrievedAccounts.size(), 2);

        // Make sure the accounts we made and the accounts we got back match
        assertTrue(retrievedAccounts.containsAll(createdAccounts)
                && createdAccounts.containsAll(retrievedAccounts));

        // Double-check we didn't get any accounts that belong to jane
        List<Account> janesAccounts = accountService.getUserAccounts(jane);
        for (Account janesAccount : janesAccounts) {
            assertFalse(retrievedAccounts.contains(janesAccount));
        }
    }

    @Test
    public void testGetUserAccountsWithCrazyUnicode() {
        // Create test data
        SiteUser unicodeMan = testUsers.createUnicodeMan();
        Account moneybag = testAccounts.createMoneyBag(unicodeMan);

        // Get unicode man's accounts, make sure they make sense
        List<Account> retrievedAccounts = accountService.getUserAccounts(unicodeMan);
        assertNotNull(retrievedAccounts);
        assertEquals(retrievedAccounts.size(), 1);

        // Look at the first (and only) account
        Account retrievedAccount = retrievedAccounts.get(0);

        // Make sure it matches up with the account we created
        assertEquals(retrievedAccount.getBalanceInDollars(), moneybag.getBalanceInDollars());
        assertEquals(retrievedAccount.getName(), moneybag.getName());
    }

    @Test
    public void testGetUserAccountsNotLoggedIn() {
        List<Account> accountList = accountService.getUserAccounts(null);
        assertTrue(accountList.isEmpty());
    }

    @Test
    public void testGetUserAccountsNoAccounts() {
        // Create alice, but don't give her any accounts
        SiteUser alice = testUsers.createAliceJohnson();

        // Create some other accounts to make sure they 
        // AREN'T returned
        SiteUser john = testUsers.createJohnDoe();
        testAccounts.createChecking(john);
        testAccounts.createSavings(john);

        // Get alice's accounts
        List<Account> accountList = accountService.getUserAccounts(alice);

        // Make sure alice has no accounts
        assertNotNull(accountList);
        assertTrue(accountList.isEmpty());
    }
}
