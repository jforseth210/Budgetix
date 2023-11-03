package edu.carroll.bankapp;

import edu.carroll.bankapp.jpa.model.SiteUser;
import edu.carroll.bankapp.service.UserService;
import edu.carroll.bankapp.testdata.TestUsers;
import jakarta.transaction.Transactional;

import jakarta.validation.constraints.Null;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

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

        // Make sure user is actually in the database
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
        testUsers.createAliceJohnson();
        testUsers.createJaneSmith();

        // Make sure they're in the database
        SiteUser alice = userService.getUser("alicejohnson");
        SiteUser jane = userService.getUser("janesmith");
        assertNotNull(alice);
        assertNotNull(jane);

        // Delete all site users
        userService.deleteAllSiteUsers();

        // Make sure they're no longer in the database
        alice = userService.getUser("alicejohnson");
        jane = userService.getUser("janesmith");
        assertNull(alice);
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
    public void testUpdateShortUsername() {
        SiteUser jane = testUsers.createJaneSmith();

        // Should not update username because username is too short
        assertFalse(userService.updateUsername(jane, "letmein456", "j"));
        assertEquals("janesmith", jane.getUsername());

        // Should not update username because password is wrong
        assertFalse(userService.updateUsername(jane, "password_time", "janie"));
        assertEquals("janesmith", jane.getUsername());
    }

    @Test
    public void testUpdateNullUsername() {
        SiteUser jane = testUsers.createJaneSmith();

        // Should not update (thus return false) for an empty username
        assertFalse(userService.updateUsername(jane, "letmein456", ""));
        assertEquals("janesmith", jane.getUsername());

        // Should not update (thus return false) for a null username
        assertFalse(userService.updateUsername(jane, "letmein456", null));
        assertEquals("janesmith", jane.getUsername());
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
    public void testCreateUserNullInputs() {
        // we expect this to fail as the full name is null
        assertNull(userService.createUser(null, "mail@mail.com", "no_name", "password"));

        // we expect this to fail as the email is null
        assertNull(userService.createUser("Null User", null, "no_email", "password"));

        // we expect this to fail as the full name and email is null
        assertNull(userService.createUser(null, null, "no_name_email", "password"));

        // we expect this to fail as the username is null
        assertNull(userService.createUser("Null user", "null@mail.com", null, "password"));

        // we expect this to fail as the username and email is null
        assertNull(userService.createUser("Null user", null, null, "password"));

        // we expect this to fail as the username, email, and full name is null
        assertNull(userService.createUser(null, null, null, "password"));

        // we expect this to fail as the password is null
        assertNull(userService.createUser("Null user", "null@mail.com", "null_password", null));

        // we expect this to fail as the username and password is null
        assertNull(userService.createUser("Null user", "null@mail.com", null, null));

        // we expect this to fail as the username, full name, and password is null
        assertNull(userService.createUser("Null user", null, null, null));

        // we expect this to fail as everything is null
        assertNull(userService.createUser(null, null, null, null));
    }

    @Test
    public void testCreateUserWithBadCredentials() {
        // Attempt to create a user with bad credentials
        // This user has a short password, short username, and invalid email address
        assertNull(testUsers.createBadUser());
    }

    @Test
    public void testCreateUserWithShortUsername() {
        assertNull(userService.createUser("Silly User", "mail@mail.com", "B", "password"));
    }

    @Test
    public void testCreateUserWithShortEmail() {
        // we expect the mail to work since this has proper 'mail formatting'
        assertNotNull(userService.createUser("Silly User", "m@m.com", "short_email", "password"));

        // we expect this mail to not work because there is no '@'
        assertNull(userService.createUser("Silly User", "mm.com", "short_email_no_at", "password"));

        // we expect this mail to not work because there is no '.com'
        assertNull(userService.createUser("Silly User", "m@m", "short_email_no_com", "password"));

        // we expect this mail to not work because there is no '@' and '.com'
        assertNull(userService.createUser("Silly User", "mm", "email_just_letters", "password"));
    }

    @Test
    public void testCreateUserWithInvalidPasswords() {
        // we expect this password to work since it has proper 'password formatting (i.e., 8 character minimum)
        assertNotNull(userService.createUser("Silly Password Man", "password@mail.com", "good_password", "password"));

        // we expect this password to work since it has proper 'password formatting (i.e., 8 character minimum)
        assertNotNull(userService.createUser("Silly Password Man", "password@mail.com", "good_password_num", "12345678"));

        // we expect this password to NOT work since it is too short (i.e., less than 8 character minimum)
        assertNull(userService.createUser("Silly Password Man", "password@mail.com", "good_password", "pass"));
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
        assertFalse(userService.updatePassword(user, "incorrectpassword", "newpassword456", "newpassword456"));
    }

    @Test
    public void testUpdatePasswordWithMismatchedNewPasswords() {
        // Get a user for password update
        SiteUser user = testUsers.createUnicodeMan();

        // TODO: Remove this test and password confirmation in service
        // Attempt to update the user's password with mismatched new passwords
        assertFalse(userService.updatePassword(user, "⿈⍺✋⇏⮊⎏⇪⤸Ⲥ↴⍁➄⼉⦕ⶓ∧⻟⍀⇝⧽", "newpassword456", "mismatchedpassword789"));
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
