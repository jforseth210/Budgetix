package edu.carroll.bankapp;

import edu.carroll.bankapp.jpa.model.Account;
import edu.carroll.bankapp.jpa.model.SiteUser;
import edu.carroll.bankapp.service.AccountService;
import edu.carroll.bankapp.service.UserService;
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
    private UserService userService;

    public static final String JOHN_NAME = "John Doe";
    public static final String JOHN_EMAIL = "john@example.com";
    public static final String JOHN_USERNAME = "johndoe";
    public static final String JOHN_PASSWORD = "password123";

    public static final String JANE_NAME = "Jane Smith";
    public static final String JANE_EMAIL = "jane@example.com";
    public static final String JANE_USERNAME = "janesmith";
    public static final String JANE_PASSWORD = "letmein456";

    public static final String ALICE_NAME = "Alice Johnson";
    public static final String ALICE_EMAIL = "alice@example.com";
    public static final String ALICE_USERNAME = "alicejohnson";
    public static final String ALICE_PASSWORD = "mysecret123";

    public static final String UNICODE_NAME = "𝔘𝔫𝔦𝔠𝔬𝔡𝔢 𝔐𝔞𝔫!";
    public static final String UNICODE_EMAIL = "iliketobreakthings@email.com";
    public static final String UNICODE_USERNAME = "☕☕☕☕☕";
    public static final String UNICODE_PASSWORD = "⿈⍺✋⇏⮊⎏⇪⤸Ⲥ↴⍁➄⼉⦕ⶓ∧⻟⍀⇝⧽";

    public static final String BAD_USER_NAME = "Bob Marley";
    public static final String BAD_USER_EMAIL = "bobby";
    public static final String BAD_USER_USERNAME = "B";
    public static final String BAD_USER_PASSWORD = "p";

    @Test
    public void testGetUserAccounts() {
        // Create some users
        SiteUser john = userService.createUser(JOHN_NAME, JOHN_EMAIL, JOHN_USERNAME, JOHN_PASSWORD);
        SiteUser jane = userService.createUser(JANE_NAME, JANE_EMAIL, JANE_USERNAME, JANE_PASSWORD);

        // Make some accounts for john
        List<Account> createdAccounts = new ArrayList<>();
        createdAccounts.add(accountService.createAccount("Checking", (long) 1940, john));
        createdAccounts.add(accountService.createAccount("Savings", (long) 36553, john));

        // Make an account for jane
        accountService.createAccount("Investments", (long) -15.10, jane);

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
        SiteUser unicodeMan = userService.createUser(UNICODE_NAME, UNICODE_EMAIL, UNICODE_USERNAME, UNICODE_PASSWORD);
        assertNotNull(unicodeMan);
        Account moneybag = accountService.createAccount("💰💰💰", (long) 0, unicodeMan);
        assertNotNull(moneybag, "Account should be created");
        // Get unicode man's accounts, make sure they make sense
        List<Account> retrievedAccounts = accountService.getUserAccounts(unicodeMan);
        assertNotNull(retrievedAccounts);
        assertEquals(1, retrievedAccounts.size());

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
        SiteUser alice = userService.createUser(ALICE_NAME, ALICE_EMAIL, ALICE_USERNAME, ALICE_PASSWORD);

        // Create some other accounts to make sure they
        // AREN'T returned
        SiteUser john = userService.createUser(JOHN_NAME, JOHN_EMAIL, JOHN_USERNAME, JOHN_PASSWORD);
        accountService.createAccount("Checking", (long) 1940, john);
        accountService.createAccount("Checking", (long) 1940, john);

        // Get alice's accounts
        List<Account> accountList = accountService.getUserAccounts(alice);

        // Make sure alice has no accounts
        assertNotNull(accountList);
        assertTrue(accountList.isEmpty());
    }
}
