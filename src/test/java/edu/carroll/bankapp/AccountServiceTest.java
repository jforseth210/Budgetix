package edu.carroll.bankapp;

import edu.carroll.bankapp.jpa.model.Account;
import edu.carroll.bankapp.jpa.model.SiteUser;
import edu.carroll.bankapp.jpa.repo.AccountRepository;
import edu.carroll.bankapp.jpa.repo.UserRepository;
import edu.carroll.bankapp.service.AccountService;
import edu.carroll.bankapp.service.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.test.context.support.WithUserDetails;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class AccountServiceTest {
    @Autowired
    private AccountService accountService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private AccountRepository accountRepo;

    private AccountServiceTest() {

    }

    @BeforeEach
    public void setUp() {
        accountRepo.deleteAll();
        userRepo.deleteAll();

        // Create some sample users
        SiteUser siteUser1 = new SiteUser("John Doe", "john@example.com", "johndoe",
                BCrypt.hashpw("password123", BCrypt.gensalt()));
        userRepo.save(siteUser1);

        SiteUser siteUser2 = new SiteUser("Jane Smith", "jane@example.com", "janesmith",
                BCrypt.hashpw("letmein456", BCrypt.gensalt()));
        userRepo.save(siteUser2);

        SiteUser siteUser3 = new SiteUser("Alice Johnson", "alice@example.com", "alicejohnson",
                BCrypt.hashpw("mysecret123", BCrypt.gensalt()));
        userRepo.save(siteUser3);
        SiteUser siteUser4 = new SiteUser("𝔘𝔫𝔦𝔠𝔬𝔡𝔢 𝔐𝔞𝔫!",
                "𝕚𝕝𝕚𝕜𝕖𝕥𝕠𝕓𝕣𝕖𝕒𝕜𝕥𝕙𝕚𝕟𝕘𝕤@𝕖𝕞𝕒𝕚𝕝.𝕔𝕠𝕞", "☕☕☕☕",
                BCrypt.hashpw("⿈⍺✋⇏⮊⎏⇪⤸Ⲥ↴⍁➄⼉⦕ⶓ∧⻟⍀⇝⧽", BCrypt.gensalt()));
        userRepo.save(siteUser4);

        // Create some sample accounts
        Account account1 = new Account();
        account1.setOwner(siteUser1);
        account1.setName("John's Savings Account");
        account1.setBalanceInCents(0);
        accountRepo.save(account1);

        Account account2 = new Account();
        account2.setOwner(siteUser1);
        account2.setName("John's Checking Account");
        account2.setBalanceInCents(0);
        accountRepo.save(account2);

        Account account3 = new Account();
        account3.setOwner(siteUser2);
        account3.setName("Jane's Investment Account");
        account3.setBalanceInCents(0);
        accountRepo.save(account3);

        Account account4 = new Account();
        account4.setOwner(siteUser4);
        account4.setBalanceInCents(-1000);
        account4.setName("💰💰💰");
        accountRepo.save(account4);
    }

    @Test
    @WithUserDetails("johndoe")
    public void testGetUserAccounts() {
        List<Account> accountList = accountService.getUserAccounts();
        assertNotNull(accountList);
        assertEquals(accountList.size(), 2);
        assertEquals(accountList.get(0).getName(), "John's Savings Account");
        assertEquals(accountList.get(1).getName(), "John's Checking Account");

        SiteUser jane = userRepo.findByUsernameIgnoreCase("janesmith").get(0);
        List<Account> janesAccounts = accountRepo.findByOwner(jane);

        for (Account janesAccount : janesAccounts) {
            assertEquals(accountList.indexOf(janesAccount), -1);
        }
    }

    @Test
    @WithUserDetails("☕☕☕☕")
    public void testGetUserAccountsWithCrazyUnicode() {
        Account unicodeMansAccount = accountService.getUserAccounts().get(0);
        assertEquals(unicodeMansAccount.getBalanceInCents(), -1000);
        assertEquals(unicodeMansAccount.getName(), "💰💰💰");
    }

    @Test
    public void testGetUserAccountsNotLoggedIn() {
        List<Account> accountList = accountService.getUserAccounts();
        assertNull(accountList);
    }

    @Test
    @WithUserDetails("alicejohnson")
    public void testGetUserAccountsNoAccounts() {
        List<Account> accountList = accountService.getUserAccounts();
        assertEquals(accountList.size(), 0);
    }
}
