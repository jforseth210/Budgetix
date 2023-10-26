package edu.carroll.bankapp;

import edu.carroll.bankapp.jpa.model.Account;
import edu.carroll.bankapp.jpa.model.SiteUser;
import edu.carroll.bankapp.service.AccountService;
import edu.carroll.bankapp.service.CustomUserDetailsService;
import edu.carroll.bankapp.service.UserService;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
public class CustomUserDetailsServiceImplTest {
    @Autowired
    private AccountService accountService;
    @Autowired
    private UserService userService;
    @Autowired
    private CustomUserDetailsService customUserDetailsService;

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
    public void testLoadUserByUsernameValidUser() {
        UserDetails userDetails = customUserDetailsService.loadUserByUsername("johndoe");
        assertNotNull(userDetails);
        assertEquals("johndoe", userDetails.getUsername());
    }

    @Test
    public void testLoadUserByUsernameInvalidUser() {
        assertThrows(UsernameNotFoundException.class, () -> {
            customUserDetailsService.loadUserByUsername("nonexistentuser");
        });
    }

    @Test
    public void testLoadUserByUsernameUnicodeUsername() {
        UserDetails userDetails = customUserDetailsService.loadUserByUsername("☕☕☕☕");
        assertNotNull(userDetails);
        assertEquals("☕☕☕☕", userDetails.getUsername());
    }
}
