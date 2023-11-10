package edu.carroll.bankapp;

import edu.carroll.bankapp.service.CustomUserDetailsService;
import edu.carroll.bankapp.testdata.TestUsers;
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
    private TestUsers testUsers;

    @Test
    public void testLoadUserByUsernameValidUser() {
        // Create user
        testUsers.createJohnDoe();
        // Create some other users
        testUsers.createAliceJohnson();
        testUsers.createJaneSmith();

        // Fetch user
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(TestUsers.JOHN_USERNAME);

        // Validate user
        assertNotNull(userDetails);
        assertEquals(TestUsers.JOHN_USERNAME, userDetails.getUsername());
    }

    @Test
    public void testLoadUserByUsernameInvalidUser() {
        // Create some users
        testUsers.createJohnDoe();
        testUsers.createAliceJohnson();
        testUsers.createJaneSmith();

        // Make sure not existent user throws error
        assertThrows(UsernameNotFoundException.class, () -> {
            // Lookup nonexistent user
            customUserDetailsService.loadUserByUsername(NONEXISTANT_USERNAME);
        });
    }

    @Test
    public void testLoadUserByUsernameUnicodeUsername() {
        // Create user
        testUsers.createUnicodeMan();
        // Create some other users
        testUsers.createJohnDoe();
        testUsers.createAliceJohnson();
        testUsers.createJaneSmith();

        // Fetch user
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(TestUsers.UNICODE_USERNAME);

        // Make sure unicode strings match
        assertNotNull(userDetails);
        assertEquals(TestUsers.UNICODE_USERNAME, userDetails.getUsername());
    }
}
