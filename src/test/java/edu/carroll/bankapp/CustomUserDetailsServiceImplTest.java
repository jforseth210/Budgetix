package edu.carroll.bankapp;

import edu.carroll.bankapp.service.CustomUserDetailsService;
import edu.carroll.bankapp.service.UserService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
public class CustomUserDetailsServiceImplTest {
    private static final String NONEXISTANT_USERNAME = "nonexistantuser";

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

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
    public void testLoadUserByUsernameValidUser() {
        // Create user
        userService.createUser(JOHN_NAME, JOHN_EMAIL, JOHN_USERNAME, JOHN_PASSWORD);
        // Create some other users
        userService.createUser(ALICE_NAME, ALICE_EMAIL, ALICE_USERNAME, ALICE_PASSWORD);
        userService.createUser(JANE_NAME, JANE_EMAIL, JANE_USERNAME, JANE_PASSWORD).getResult();

        // Fetch user
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(JOHN_USERNAME);

        // Validate user
        assertNotNull(userDetails);
        assertEquals(JOHN_USERNAME, userDetails.getUsername());
    }

    @Test
    public void testLoadUserByUsernameInvalidUser() {
        // Create some users
        userService.createUser(JOHN_NAME, JOHN_EMAIL, JOHN_USERNAME, JOHN_PASSWORD);
        userService.createUser(ALICE_NAME, ALICE_EMAIL, ALICE_USERNAME, ALICE_PASSWORD);
        userService.createUser(JANE_NAME, JANE_EMAIL, JANE_USERNAME, JANE_PASSWORD).getResult();

        // Make sure not existent user throws error
        assertThrows(UsernameNotFoundException.class, () -> {
            // Lookup nonexistent user
            customUserDetailsService.loadUserByUsername(NONEXISTANT_USERNAME);
        });
    }

    @Test
    public void testLoadUserByUsernameUnicodeUsername() {
        // Create user
        userService.createUser(UNICODE_NAME, UNICODE_EMAIL, UNICODE_USERNAME, UNICODE_PASSWORD).getResult();
        ;
        // Create some other users
        userService.createUser(JOHN_NAME, JOHN_EMAIL, JOHN_USERNAME, JOHN_PASSWORD);
        userService.createUser(ALICE_NAME, ALICE_EMAIL, ALICE_USERNAME, ALICE_PASSWORD);
        userService.createUser(JANE_NAME, JANE_EMAIL, JANE_USERNAME, JANE_PASSWORD).getResult();

        // Fetch user
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(UNICODE_USERNAME);

        // Make sure unicode strings match
        assertNotNull(userDetails);
        assertEquals(UNICODE_USERNAME, userDetails.getUsername());
    }
}
