package edu.carroll.bankapp;

import edu.carroll.bankapp.jpa.model.Account;
import edu.carroll.bankapp.jpa.model.SiteUser;
import edu.carroll.bankapp.jpa.model.Transaction;
import edu.carroll.bankapp.service.AccountService;
import edu.carroll.bankapp.service.TransactionService;
import edu.carroll.bankapp.service.UserService;
import jakarta.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Transactional
@SpringBootTest
public class UserServiceImplTest {
    @Autowired
    private UserService userService;
    @Autowired
    private TransactionService transactionService;

    @BeforeEach
    public void setUp() {
        SiteUser john = userService.createUser("John Doe", "john@example.com", "johndoe", "password123");
        SiteUser jane = userService.createUser("Jane Smith", "jane@example.com", "janesmith", "letmein456");
        SiteUser alice = userService.createUser("Alice Johnson", "alice@example.com", "alicejohnson", "mysecret123");
        SiteUser unicodeMan = userService.createUser("𝔘𝔫𝔦𝔠𝔬𝔡𝔢 𝔐𝔞𝔫!",
                "𝕚𝕝𝕚𝕜𝕖𝕥𝕠𝕓𝕣𝕖𝕒𝕜𝕥𝕙𝕚𝕟𝕘𝕤@𝕖𝕞𝕒𝕚𝕝.𝕔𝕠𝕞",
                "☕☕☕☕", "⿈⍺✋⇏⮊⎏⇪⤸Ⲥ↴⍁➄⼉⦕ⶓ∧⻟⍀⇝⧽");
    }

    @Test
    public void testCreateUser() {
        // Create a new user
        SiteUser newUser = userService.createUser("New User", "new@example.com", "newuser", "secure123");

        // Ensure the created user is not null
        assertNotNull(newUser);
        // Verify that the user's full name is set correctly
        assertEquals("New User", newUser.getFullName());
        // Verify that the user's email is set correctly
        assertEquals("new@example.com", newUser.getEmail());
        // Verify that the username is set correctly
        assertEquals("newuser", newUser.getUsername());
        // (You might want to add more assertions for other properties as needed)
    }

    @Test
    public void testGetUserById() {
        // Get an existing user by their ID
        SiteUser john = userService.getUser("johndoe");

        // Ensure the returned user is not null
        assertNotNull(john);
        // Verify that the user's full name matches
        assertEquals("John Doe", john.getFullName());
        // Verify that the user's email matches
        assertEquals("john@example.com", john.getEmail());
        // Verify that the username matches
        assertEquals("johndoe", john.getUsername());
        // (You can add more assertions as needed)
    }

    @Test
    public void testGetUserByUsername() {
        // Get an existing user by their username
        SiteUser jane = userService.getUser("janesmith");

        // Ensure the returned user is not null
        assertNotNull(jane);
        // Verify that the user's full name matches
        assertEquals("Jane Smith", jane.getFullName());
        // Verify that the user's email matches
        assertEquals("jane@example.com", jane.getEmail());
        // Verify that the username matches
        assertEquals("janesmith", jane.getUsername());
        // (You can add more assertions as needed)
    }

    @Test
    public void testDeleteAllSiteUsers() {
        // Before deletion, ensure that there are users in the system
        SiteUser user1 = userService.getUser("alicejohnson");
        SiteUser user2 = userService.getUser("janesmith");
        assertNotNull(user1);
        assertNotNull(user2);

        // Delete all site users
        userService.deleteAllSiteUsers();

        // After deletion, check that the users no longer exist
        user1 = userService.getUser("alicejohnson");
        user2 = userService.getUser("janesmith");
        assertNull(user1);
        assertNull(user2);
    }

    @Test
    public void testUpdatePassword() {
        // Get a user for password update
        SiteUser user = userService.getUser("☕☕☕☕");

        // Update the user's password
        userService.updatePassword(user, "⿈⍺✋⇏⮊⎏⇪⤸Ⲥ↴⍁➄⼉⦕ⶓ∧⻟⍀⇝⧽", "newpassword456", "newpassword456");

        // Verify that the password has been updated
        SiteUser updatedUser = userService.getUser("☕☕☕☕");
        assertNotNull(updatedUser);
        assert (BCrypt.checkpw("newpassword456", user.getHashedPassword()));
    }

    @Test
    public void testUpdateUsername() {
        // Get a user for username update
        SiteUser user = userService.getUser("☕☕☕☕");

        // Update the user's username
        userService.updateUsername(user, "☕☕☕☕", "newusername123");

        // Verify that the username has been updated
        SiteUser updatedUser = userService.getUser("newusername123");
        assertNotNull(updatedUser);
        assertEquals("newusername123", updatedUser.getUsername());
    }

    @Test
    public void testCreateUserWithExistingUsername() {
        // Attempt to create a user with an existing username
        assertNull(
                userService.createUser("Duplicate User", "duplicate@example.com", "johndoe", "password123"));
    }

    @Test
    public void testGetNonExistentUserById() {
        // Attempt to get a user that does not exist by their ID
        SiteUser user = userService.getUser("nonexistentuser");
        assertNull(user);
    }

    @Test
    public void testGetNonExistentUserByUsername() {
        // Attempt to get a user that does not exist by their username
        SiteUser user = userService.getUser("nonexistentusername");
        assertNull(user);
    }

    @Test
    public void testUpdatePasswordWithIncorrectCurrentPassword() {
        // Get a user for password update
        SiteUser user = userService.getUser("☕☕☕☕");

        // Attempt to update the user's password with an incorrect current password
        assertThrows(RuntimeException.class, () -> {
            userService.updatePassword(user, "incorrectpassword", "newpassword456", "newpassword456");
        });
    }

    @Test
    public void testUpdatePasswordWithMismatchedNewPasswords() {
        // Get a user for password update
        SiteUser user = userService.getUser("☕☕☕☕");

        // Attempt to update the user's password with mismatched new passwords
        assertThrows(RuntimeException.class, () -> {
            userService.updatePassword(user, "⿈⍺✋⇏⮊⎏⇪⤸Ⲥ↴⍁➄⼉⦕ⶓ∧⻟⍀⇝⧽", "newpassword456", "mismatchedpassword789");
        });
    }

    // TODO: Update this when we know what existing username behavior should be
    /**
     * @Test
     *       public void testUpdateUsernameWithExistingUsername() {
     *       // Get a user for username update
     *       SiteUser user = userService.getUser("☕☕☕☕");
     * 
     *       // Attempt to update the user's username to an existing username
     *       assertThrows(RuntimeException.class, () -> {
     *       userService.updateUsername(user, "☕☕☕☕", "johndoe");
     *       });
     *       }
     */
}
