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
        UserDetails userDetails = customUserDetailsService.loadUserByUsername("johndoe");

        // Validate user
        assertNotNull(userDetails);
        assertEquals("johndoe", userDetails.getUsername());
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
            customUserDetailsService.loadUserByUsername("nonexistentuser");
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
        UserDetails userDetails = customUserDetailsService.loadUserByUsername("☕☕☕☕☕");

        // Make sure unicode strings match
        assertNotNull(userDetails);
        assertEquals("☕☕☕☕☕", userDetails.getUsername());
    }
}
