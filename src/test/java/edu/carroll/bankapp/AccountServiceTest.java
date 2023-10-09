package edu.carroll.bankapp;

import edu.carroll.bankapp.jpa.model.Account;
import edu.carroll.bankapp.jpa.model.SiteUser;
import edu.carroll.bankapp.service.AccountService;
import edu.carroll.bankapp.service.UserService;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
public class AccountServiceTest {
    @Autowired
    private AccountService accountService;
    @Autowired
    private UserService userService;

    @BeforeEach
    public void setUp() {
        SiteUser john = userService.createUser("John Doe", "john@example.com", "johndoe", "password123");
        SiteUser jane = userService.createUser("Jane Smith", "jane@example.com", "janesmith", "letmein456");
        userService.createUser("Alice Johnson", "alice@example.com", "alicejohnson", "mysecret123");
        SiteUser unicodeMan = userService.createUser("𝔘𝔫𝔦𝔠𝔬𝔡𝔢 𝔐𝔞𝔫!",
                "𝕚𝕝𝕚𝕜𝕖𝕥𝕠𝕓𝕣𝕖𝕒𝕜𝕥𝕙𝕚𝕟𝕘𝕤@𝕖𝕞𝕒𝕚𝕝.𝕔𝕠𝕞",
                "☕☕☕☕", "⿈⍺✋⇏⮊⎏⇪⤸Ⲥ↴⍁➄⼉⦕ⶓ∧⻟⍀⇝⧽");

        accountService.createAccount("John's Savings Account", 0, john);
        accountService.createAccount("John's Checking Account", 0, john);
        accountService.createAccount("Jane's Investment Account", 0, jane);
        accountService.createAccount("💰💰💰", -1000, unicodeMan);
    }

    @Test
    @WithUserDetails("johndoe")
    public void testGetUserAccounts() {
        List<Account> johnsAccounts = accountService.getUserAccounts();
        assertNotNull(johnsAccounts);
        assertEquals(johnsAccounts.size(), 2);
        // Not necessarily ordered this way
        assertEquals(johnsAccounts.get(0).getName(), "John's Savings Account");
        assertEquals(johnsAccounts.get(1).getName(), "John's Checking Account");

        SiteUser jane = userService.getUser("janesmith");
        List<Account> janesAccounts = accountService.getUserAccounts(jane);

        for (Account janesAccount : janesAccounts) {
            assertEquals(johnsAccounts.indexOf(janesAccount), -1);
        }
    }

    @Test
    @WithUserDetails("☕☕☕☕")
    public void testGetUserAccountsWithCrazyUnicode() {
        Account unicodeMansAccount = accountService.getUserAccounts().get(0);
        assertEquals(unicodeMansAccount.getBalanceInDollars(), -1000);
        assertEquals(unicodeMansAccount.getName(), "💰💰💰");
    }

    @Test
    public void testGetUserAccountsNotLoggedIn() {
        List<Account> accountList = accountService.getUserAccounts();
        assertTrue(accountList.isEmpty());
    }

    @Test
    @WithUserDetails("alicejohnson")
    public void testGetUserAccountsNoAccounts() {
        List<Account> accountList = accountService.getUserAccounts();
        assertTrue(accountList.isEmpty());
    }
}
