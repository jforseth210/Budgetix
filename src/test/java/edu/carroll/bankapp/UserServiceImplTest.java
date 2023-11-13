package edu.carroll.bankapp;

import edu.carroll.bankapp.jpa.model.SiteUser;
import edu.carroll.bankapp.service.UserService;
import edu.carroll.bankapp.testdata.TestUsers;
import jakarta.transaction.Transactional;

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

    // Constants for any time we want a generic value for a field
    private final String GENERIC_USERNAME = "a_user";
    private final String GENERIC_EMAIL = "someone@example.com";
    private final String GENERIC_PASSWORD = "password";
    private final String GENERIC_FULL_NAME = "A Name";

    @Test
    public void testCreateUser() {
        // Define string constants
        final String username = "newuser";
        final String email = "new@example.com";
        final String name = "New User";
        final String password = "secure123";

        // Create a new user
        SiteUser newUser = userService.createUser(name, email, username, password);

        // Ensure the created user is not null
        assertNotNull(newUser);
        // Verify that the user's full name is set correctly
        assertEquals(name, newUser.getFullName());
        // Verify that the user's email is set correctly
        assertEquals(email, newUser.getEmail());
        // Verify that the username is set correctly
        assertEquals(username, newUser.getUsername());

        // Make sure user is actually in the database
        SiteUser fetchedUser = userService.getUserByUsername(username);
        // Ensure the created user is not null
        assertNotNull(fetchedUser);
        // Verify that the user's full name is set correctly
        assertEquals(name, fetchedUser.getFullName());
        // Verify that the user's email is set correctly
        assertEquals(email, fetchedUser.getEmail());
        // Verify that the username is set correctly
        assertEquals(username, fetchedUser.getUsername());
        assertEquals(username, fetchedUser.getUsername());
    }

    @Test
    public void testGetUserById() {
        // Create users
        SiteUser createdJohn = testUsers.createJohnDoe();
        testUsers.createAliceJohnson();
        testUsers.createJaneSmith();

        // Get an existing user by their ID
        SiteUser fetchedJohn = userService.getUserById(createdJohn.getId());

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
        SiteUser fetchedJane = userService.getUserByUsername(createdJane.getUsername());

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
    public void testUpdatePassword() {
        final String newPassword = "newpassword456";
        // Get a user for password update
        SiteUser unicodeMan = testUsers.createUnicodeMan();

        // Update the user's password
        userService.updatePassword(unicodeMan, TestUsers.UNICODE_PASSWORD, newPassword);

        // Verify that the password has been updated
        SiteUser updatedUser = userService.getUserById(unicodeMan.getId());
        assertNotNull(updatedUser);
        assertTrue(BCrypt.checkpw(newPassword, unicodeMan.getHashedPassword()));
    }

    @Test
    public void testUpdateUsername() {
        final String newUsername = "newusername123";
        // Get a user for username update
        SiteUser unicodeMan = testUsers.createUnicodeMan();

        // Update the user's username
        assertTrue(userService.updateUsername(unicodeMan, TestUsers.UNICODE_PASSWORD, newUsername));

        // Verify that the username has been updated
        SiteUser updatedUser = userService.getUserById(unicodeMan.getId());
        assertNotNull(updatedUser);
        assertEquals(newUsername, updatedUser.getUsername());

        // Make sure fetching by username works too
        updatedUser = userService.getUserByUsername(newUsername);
        assertNotNull(updatedUser);
        assertEquals(newUsername, updatedUser.getUsername());

        // Make sure old username doesn't work anymore
        updatedUser = userService.getUserByUsername(TestUsers.UNICODE_USERNAME);
        assertNull(updatedUser);
    }

    @Test
    public void testUpdateShortUsername() {
        final String newPassword = TestUsers.JANE_PASSWORD;
        final String newShortUsername = "j";
        SiteUser jane = testUsers.createJaneSmith();

        // Should not update username because username is too short
        assertFalse(userService.updateUsername(jane, newPassword, newShortUsername));
        assertEquals(TestUsers.JANE_USERNAME, jane.getUsername());
    }

    @Test
    public void testUpdateWrongPassword() {
        final String wrongPassword = "this_password_is_wrong";
        final String newUsername = "janie";
        SiteUser jane = testUsers.createJaneSmith();
        final int janeId = jane.getId();

        // Should not update username because password is wrong
        assertFalse(userService.updateUsername(jane, wrongPassword, newUsername));
        assertEquals(TestUsers.JANE_USERNAME, jane.getUsername());
        assertNull(userService.getUserByUsername(newUsername));
        assertEquals(userService.getUserById(janeId).getUsername(), TestUsers.JANE_USERNAME);
    }

    @Test
    public void testUpdateNullUsername() {
        final String newPassword = TestUsers.JANE_PASSWORD;
        final String emptyUsername = "";
        final String nullUsername = null;
        SiteUser jane = testUsers.createJaneSmith();

        // Should not update (thus return false) for an empty username
        assertFalse(userService.updateUsername(jane, newPassword, emptyUsername));
        assertEquals(TestUsers.JANE_USERNAME, jane.getUsername());

        // Should not update (thus return false) for a null username
        assertFalse(userService.updateUsername(jane, newPassword, nullUsername));
        assertEquals(TestUsers.JANE_USERNAME, jane.getUsername());
    }

    @Test
    public void testCreateUserWithExistingUsername() {
        // Create user
        testUsers.createJohnDoe();
        assertNull(
                userService.createUser(GENERIC_FULL_NAME, GENERIC_EMAIL, TestUsers.JOHN_USERNAME,
                        GENERIC_PASSWORD));
    }

    @Test
    public void testCreateUserNullFullName() {
        assertNull(userService.createUser(null, GENERIC_EMAIL, GENERIC_USERNAME, GENERIC_PASSWORD));
    }

    @Test
    public void testCreateUserNullEmail() {
        assertNull(userService.createUser(GENERIC_FULL_NAME, null, GENERIC_USERNAME, GENERIC_PASSWORD));
    }

    @Test
    public void testCreateUserNullFullNameAndEmail() {
        assertNull(userService.createUser(null, null, GENERIC_USERNAME, GENERIC_PASSWORD));
    }

    @Test
    public void testCreateUserNullUsername() {
        assertNull(userService.createUser(GENERIC_FULL_NAME, GENERIC_EMAIL, null, GENERIC_PASSWORD));
    }

    @Test
    public void testCreateUserNullUsernameAndEmail() {
        assertNull(userService.createUser(GENERIC_FULL_NAME, null, null, GENERIC_PASSWORD));
    }

    @Test
    public void testCreateUserNullInputsAndFullName() {
        assertNull(userService.createUser(null, null, null, GENERIC_PASSWORD));
    }

    @Test
    public void testCreateUserNullPassword() {
        assertNull(userService.createUser(GENERIC_FULL_NAME, GENERIC_EMAIL, GENERIC_PASSWORD, null));
    }

    @Test
    public void testCreateUserNullUsernameAndPassword() {
        assertNull(userService.createUser(GENERIC_FULL_NAME, GENERIC_EMAIL, null, null));
    }

    @Test
    public void testCreateUserNullUsernameFullNameAndPassword() {
        assertNull(userService.createUser(GENERIC_FULL_NAME, null, null, null));
    }

    @Test
    public void testCreateUserAllNullInputs() {
        assertNull(userService.createUser(null, null, null, null));
    }

    @Test
    public void testCreateUserEmptyFullName() {
        assertNull(userService.createUser("", GENERIC_EMAIL, GENERIC_USERNAME, GENERIC_PASSWORD));
    }

    @Test
    public void testCreateUserEmptyEmail() {
        assertNull(userService.createUser(GENERIC_FULL_NAME, "", GENERIC_USERNAME, GENERIC_PASSWORD));
    }

    @Test
    public void testCreateUserEmptyFullNameAndEmail() {
        assertNull(userService.createUser("", "", GENERIC_USERNAME, GENERIC_PASSWORD));
    }

    @Test
    public void testCreateUserEmptyUsername() {
        assertNull(userService.createUser(GENERIC_FULL_NAME, GENERIC_EMAIL, "", GENERIC_PASSWORD));
    }

    @Test
    public void testCreateUserEmptyUsernameAndEmail() {
        assertNull(userService.createUser(GENERIC_FULL_NAME, "", "", GENERIC_PASSWORD));
    }

    @Test
    public void testCreateUserEmptyInputsAndFullName() {
        assertNull(userService.createUser("", "", "", GENERIC_PASSWORD));
    }

    @Test
    public void testCreateUserEmptyPassword() {
        assertNull(userService.createUser(GENERIC_FULL_NAME, GENERIC_EMAIL, GENERIC_PASSWORD, ""));
    }

    @Test
    public void testCreateUserEmptyUsernameAndPassword() {
        assertNull(userService.createUser(GENERIC_FULL_NAME, GENERIC_EMAIL, "", ""));
    }

    @Test
    public void testCreateUserEmptyUsernameFullNameAndPassword() {
        assertNull(userService.createUser(GENERIC_FULL_NAME, "", "", ""));
    }

    @Test
    public void testCreateUserAllEmptyInputs() {
        assertNull(userService.createUser("", "", "", ""));
    }

    @Test
    public void testCreateUserWithBadCredentials() {
        // Attempt to create a user with bad credentials
        // This user has a short password, short username, and invalid email address
        assertNull(testUsers.createBadUser());
    }

    @Test
    public void testCreateUserWithShortUsername() {
        final String shortUsername = "B";
        assertNull(userService.createUser(GENERIC_FULL_NAME, GENERIC_EMAIL, shortUsername, GENERIC_PASSWORD));
    }

    @Test
    public void testCreateUserWithShortEmail() {
        final String shortEmail = "m@m.com";
        final String shortEmailNoAt = "mm.com";
        final String shortEmailNoTld = "m@m";
        final String shortEmailNoAtNoTld = "mm";
        // we expect the mail to work since this has proper 'mail formatting'
        assertNotNull(userService.createUser(GENERIC_FULL_NAME, shortEmail, GENERIC_USERNAME, GENERIC_PASSWORD));

        // we expect this mail to not work because there is no '@'
        assertNull(userService.createUser(GENERIC_FULL_NAME, shortEmailNoAt, GENERIC_USERNAME, GENERIC_PASSWORD));

        // we expect this mail to not work because there is no '.com'
        assertNull(userService.createUser(GENERIC_FULL_NAME, shortEmailNoTld, GENERIC_USERNAME, GENERIC_PASSWORD));

        // we expect this mail to not work because there is no '@' and '.com'
        assertNull(userService.createUser(GENERIC_FULL_NAME, shortEmailNoAtNoTld, GENERIC_USERNAME, GENERIC_PASSWORD));
    }

    @Test
    public void testCreateUserWithValidPassword() {
        // we expect this password to work since it has proper 'password formatting
        // (i.e., 8 character minimum)
        assertNotNull(
                userService.createUser(GENERIC_FULL_NAME, GENERIC_EMAIL, GENERIC_USERNAME, GENERIC_PASSWORD));
    }

    @Test
    public void testCreateUserWithValidPasswordContainingNumbers() {
        final String numericPassword = "12345678";
        // we expect this password to work since it has proper 'password formatting
        // (i.e., 8 character minimum)
        assertNotNull(
                userService.createUser(GENERIC_FULL_NAME, GENERIC_EMAIL, GENERIC_USERNAME, numericPassword));
    }

    @Test
    public void testCreateUserWithInvalidShortPassword() {
        final String shortPassword = "pass";
        // we expect this password to NOT work since it is too short (i.e., less than 8
        // character minimum)
        assertNull(userService.createUser(GENERIC_FULL_NAME, GENERIC_EMAIL, GENERIC_USERNAME, shortPassword));
    }

    @Test
    public void testGetNonExistentUserById() {
        final String nonExistantUsername = "nonexistentusername";
        // Attempt to get a user that does not exist by their ID
        SiteUser user = userService.getUserByUsername(nonExistantUsername);
        assertNull(user);
    }

    @Test
    public void testGetNonExistentUserByUsername() {
        final String nonExistantUsername = "nonexistentusername";
        // Attempt to get a user that does not exist by their username
        SiteUser user = userService.getUserByUsername(nonExistantUsername);
        assertNull(user);
    }

    @Test
    public void testUpdatePasswordWithIncorrectCurrentPassword() {
        final String incorrectPassword = "incorrectpassword";
        final String newPassword = "newpassword456"
        // Get a user for password update
        SiteUser user = testUsers.createUnicodeMan();

        // Attempt to update the user's password with an incorrect current password
        assertFalse(userService.updatePassword(user, incorrectPassword, newPassword));
    }

    @Test
    public void testUpdateUsernameWithExistingUsername() {
        testUsers.createUnicodeMan();
        // Get a user for username update
        SiteUser user = userService.getUserByUsername(TestUsers.UNICODE_USERNAME);

        // Attempt to update the user's username to an existing username
        assertFalse(userService.updateUsername(user, TestUsers.UNICODE_USERNAME, TestUsers.JOHN_USERNAME));
        assertNotNull(userService.getUserByUsername(TestUsers.UNICODE_USERNAME));
    }

}
