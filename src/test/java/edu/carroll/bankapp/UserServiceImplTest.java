package edu.carroll.bankapp;

import edu.carroll.bankapp.jpa.model.SiteUser;
import edu.carroll.bankapp.service.UserService;
import edu.carroll.bankapp.testdata.TestUsers;
import jakarta.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    private TestUsers testUsers;

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

        // Make sure user is actually in the
        SiteUser fetchedUser = userService.getUser("newuser");
        // Ensure the created user is not null
        assertNotNull(fetchedUser);
        // Verify that the user's full name is set correctly
        assertEquals("New User", fetchedUser.getFullName());
        // Verify that the user's email is set correctly
        assertEquals("new@example.com", fetchedUser.getEmail());
        // Verify that the username is set correctly
        assertEquals("newuser", fetchedUser.getUsername());
    }

    @Test
    public void testGetUserById() {
        // Create users
        SiteUser createdJohn = testUsers.createJohnDoe();
        testUsers.createAliceJohnson();
        testUsers.createJaneSmith();

        // Get an existing user by their ID
        SiteUser fetchedJohn = userService.getUser(createdJohn.getId());

        // Ensure the returned user is not null
        assertNotNull(fetchedJohn);
        // Verify that the user's full name matches
        assertEquals(createdJohn.getFullName(), fetchedJohn.getFullName());
        // Verify that the user's email matches
        assertEquals(createdJohn.getEmail(), fetchedJohn.getEmail());
        // Verify that the username matches
        assertEquals(createdJohn.getUsername(), fetchedJohn.getUsername());
    }

    @Test
    public void testGetUserByUsername() {
        // Create users
        testUsers.createJohnDoe();
        testUsers.createAliceJohnson();
        SiteUser createdJane = testUsers.createJaneSmith();

        // Get an existing user by their username
        SiteUser fetchedJane = userService.getUser(createdJane.getUsername());

        // Ensure the returned user is not null
        assertNotNull(fetchedJane);
        // Verify that the user's full name matches
        assertEquals(createdJane.getFullName(), fetchedJane.getFullName());
        // Verify that the user's email matches
        assertEquals(createdJane.getEmail(), fetchedJane.getEmail());
        // Verify that the username matches
        assertEquals(createdJane.getUsername(), fetchedJane.getUsername());
    }

    @Test
    public void testDeleteAllSiteUsers() {
        // Create users
        testUsers.createJohnDoe();
        testUsers.createJaneSmith();

        // Make sure they're in the database
        SiteUser john = userService.getUser("alicejohnson");
        SiteUser jane = userService.getUser("janesmith");
        assertNotNull(john);
        assertNotNull(jane);

        // Delete all site users
        userService.deleteAllSiteUsers();

        // Make sure they're no longer in the database
        john = userService.getUser("alicejohnson");
        jane = userService.getUser("janesmith");
        assertNull(john);
        assertNull(jane);
    }

    @Test
    public void testUpdatePassword() {
        // Get a user for password update
        SiteUser unicodeMan = testUsers.createUnicodeMan();

        // Update the user's password
        userService.updatePassword(unicodeMan, "⿈⍺✋⇏⮊⎏⇪⤸Ⲥ↴⍁➄⼉⦕ⶓ∧⻟⍀⇝⧽", "newpassword456", "newpassword456");

        // Verify that the password has been updated
        SiteUser updatedUser = userService.getUser(unicodeMan.getId());
        assertNotNull(updatedUser);
        assertTrue(BCrypt.checkpw("newpassword456", unicodeMan.getHashedPassword()));
    }

    @Test
    public void testUpdateUsername() {
        // Get a user for username update
        SiteUser unicodeMan = testUsers.createUnicodeMan();

        // Update the user's username
        userService.updateUsername(unicodeMan, "☕☕☕☕", "newusername123");

        // Verify that the username has been updated
        SiteUser updatedUser = userService.getUser(unicodeMan.getId());
        assertNotNull(updatedUser);
        assertEquals("newusername123", updatedUser.getUsername());

        // Make sure fetching by username works too
        updatedUser = userService.getUser("newusername123");
        assertNotNull(updatedUser);
        assertEquals("newusername123", updatedUser.getUsername());

        // Make sure old username doesn't work anymore
        updatedUser = userService.getUser("☕☕☕☕");
        assertNull(updatedUser);
    }

    @Test
    public void testCreateUserWithExistingUsername() {
        // Create user
        testUsers.createJohnDoe();

        // Attempt to create another user with an existing username
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
        SiteUser user = testUsers.createUnicodeMan();

        // TODO: This probably shouldn't throw an error like this...
        // Attempt to update the user's password with an incorrect current password
        assertThrows(RuntimeException.class, () -> {
            userService.updatePassword(user, "incorrectpassword", "newpassword456", "newpassword456");
        });
    }

    @Test
    public void testUpdatePasswordWithMismatchedNewPasswords() {
        // Get a user for password update
        SiteUser user = testUsers.createUnicodeMan();

        // TODO: Remove this test and password confirmation in service
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
