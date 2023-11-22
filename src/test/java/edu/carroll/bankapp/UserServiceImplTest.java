package edu.carroll.bankapp;

import edu.carroll.bankapp.jpa.model.SiteUser;
import edu.carroll.bankapp.service.UserService;
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

    // Constants for any time we want a generic value for a field
    private final String GENERIC_USERNAME = "a_user";
    private final String GENERIC_EMAIL = "someone@example.com";
    private final String GENERIC_PASSWORD = "password";
    private final String GENERIC_FULL_NAME = "A Name";

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

    /**
     * User Creation Tests:
     */

    @Test
    // Create a user with valid data
    public void testCreateValidUser() {
        // Define string constants
        final String username = "newuser";
        final String email = "new@example.com";
        final String name = "New User";
        final String password = "secure123";

        // Create a new user
        SiteUser newUser = userService.createUser(name, email, username, password);

        // Expect the created user to be present
        assertNotNull(newUser, "The user should have been created");
        // Expect the user's full name to be set correctly
        assertEquals(name, newUser.getFullName(), "Full name should be set correctly");
        // Expect the user's email to be set correctly
        assertEquals(email, newUser.getEmail(), "Email should be set correctly");
        // Expect the username to be set correctly
        assertEquals(username, newUser.getUsername(), "Username should be set correctly");

        // Expect the user to be found in the database
        SiteUser fetchedUser = userService.getUserByUsername(username);
        assertNotNull(fetchedUser, "The user should be stored in the database");
        // Expect the retrieved user's full name to match the created user's name
        assertEquals(name, fetchedUser.getFullName(), "Retrieved user full name should match the created user's name");
        // Expect the retrieved user's email to match the created user's email
        assertEquals(email, fetchedUser.getEmail(), "Retrieved user email should match the created user's email");
        // Expect the retrieved user's username to match the created user's username
        assertEquals(username, fetchedUser.getUsername(),
                "Retrieved user username should match the created user's username");
    }

    @Test
    // Attempt to create a user with an already taken username
    public void testCreateUserWithExistingUsername() {
        // Create user
        assertNotNull(userService.createUser(JOHN_NAME, JOHN_EMAIL, JOHN_USERNAME, JOHN_PASSWORD),
                "Setup failed, unable to create initial user");
        // Make sure the user isn't created
        assertNull(
                userService.createUser(GENERIC_FULL_NAME, GENERIC_EMAIL, JOHN_USERNAME,
                        GENERIC_PASSWORD),
                "Allowed creation of a user with an existing username");
        // Make sure nothing actually changes
        SiteUser johnDoeFromDB = userService.getUserByUsername(JOHN_USERNAME);
        assertEquals(johnDoeFromDB.getFullName(), JOHN_NAME, "Existing user\'s name was changed");
        assertEquals(johnDoeFromDB.getEmail(), JOHN_EMAIL, "Existing user\'s email was changed");
    }

    @Test
    // Attempt to create user without a name
    public void testCreateUserNullFullName() {
        // Assert that the createUser method returns null when the full name is null
        assertNull(userService.createUser(null, GENERIC_EMAIL, GENERIC_USERNAME, GENERIC_PASSWORD),
                "User should not be created when the full name is null");

        // Assert that the user is not created in the database
        assertNull(userService.getUserByUsername(GENERIC_USERNAME),
                "User should not exist in the database when the full name is null");
    }

    @Test
    // Attempt to create user without an email
    public void testCreateUserNullEmail() {
        // Assert that the createUser method returns null when the email is null
        assertNull(userService.createUser(GENERIC_FULL_NAME, null, GENERIC_USERNAME, GENERIC_PASSWORD),
                "User should not be created when the email is null");

        // Assert that the user is not created in the database
        assertNull(userService.getUserByUsername(GENERIC_USERNAME),
                "User should not exist in the database when the email is null");
    }

    @Test
    // Attempt to create user without a name or email
    public void testCreateUserNullFullNameAndEmail() {
        // Assert that the createUser method returns null when the full name and email
        // are null
        assertNull(userService.createUser(null, null, GENERIC_USERNAME, GENERIC_PASSWORD),
                "User should not be created when the full name and email are null");

        // Assert that the user is not created in the database
        assertNull(userService.getUserByUsername(GENERIC_USERNAME),
                "User should not exist in the database when the full name and email are null");
    }

    @Test
    // Attempt to create user without a username
    public void testCreateUserNullUsername() {
        // Assert that the createUser method returns null when the username is null
        assertNull(userService.createUser(GENERIC_FULL_NAME, GENERIC_EMAIL, null, GENERIC_PASSWORD),
                "User should not be created when the username is null");

        // Since the username is a unique identifier, there's no need to check for the
        // user's existence in the database.
    }

    @Test
    // Attempt to create user without a username or email
    public void testCreateUserNullUsernameAndEmail() {
        // Assert that the createUser method returns null when both the username and
        // email are null
        assertNull(userService.createUser(GENERIC_FULL_NAME, null, null, GENERIC_PASSWORD),
                "User should not be created when both the username and email are null");

        // Since both the username and email are required fields, there's no need to
        // check for the user's existence in the database.
    }

    @Test
    // Attempt to create user with a password and nulls for other values
    public void testCreateUserNullInputsAndPassword() {
        // Assert that the createUser method returns null when all other fields are null
        // and only the password is provided
        assertNull(userService.createUser(null, null, null, GENERIC_PASSWORD),
                "User should not be created when all other fields are null and only the password is provided");

        // Since all other fields are required, there's no need to check for the user's
        // existence in the database.
    }

    @Test
    // Attempt to create a user without a password
    public void testCreateUserNullPassword() {
        // Assert that the createUser method returns null when the password is null
        assertNull(userService.createUser(GENERIC_FULL_NAME, GENERIC_EMAIL, GENERIC_USERNAME, null),
                "User should not be created when the password is null");

        // Assert that the user is not created in the database
        assertNull(userService.getUserByUsername(GENERIC_USERNAME),
                "User should not exist in the database when the password is null");
    }

    @Test
    // Attempt to create a user without a username or password
    public void testCreateUserNullUsernameAndPassword() {
        // Assert that the createUser method returns null when both the username and
        // password are null
        assertNull(userService.createUser(GENERIC_FULL_NAME, GENERIC_EMAIL, null, null),
                "User should not be created when both the username and password are null");

        // Since both the username and password are required fields, there's no need to
        // check for the user's existence in the database.
    }

    @Test
    // Attempt to create a user with only a full name
    public void testCreateUserNullUsernameFullNameAndPassword() {
        // Assert that the createUser method returns null when only the full name is
        // provided
        assertNull(userService.createUser(GENERIC_FULL_NAME, null, null, null),
                "User should not be created when only the full name is provided");

        // Since none of the required fields are provided, there's no need to check for
        // the user's existence in the database.
    }

    @Test
    // Attempt to create a user with all null values
    public void testCreateUserAllNullInputs() {
        // Assert that the createUser method returns null when all fields are null
        assertNull(userService.createUser(null, null, null, null),
                "User should not be created when all fields are null");

        // Since none of the required fields are provided, there's no need to check for
        // the user's existence in the database.
    }

    @Test
    // Attempt to create a user with an empty string for the first name
    public void testCreateUserEmptyFullName() {
        // Assert that the createUser method returns null when the full name is an empty
        // string
        assertNull(userService.createUser("", GENERIC_EMAIL, GENERIC_USERNAME, GENERIC_PASSWORD),
                "User should not be created when the full name is an empty string");

        // Assert that the user is not created in the database
        assertNull(userService.getUserByUsername(GENERIC_USERNAME),
                "User should not exist in the database when the full name is an empty string");
    }

    @Test
    // Attempt to create a user with an empty string for the email
    public void testCreateUserEmptyEmail() {
        // Assert that the createUser method returns null when the email is an empty
        // string
        assertNull(userService.createUser(GENERIC_FULL_NAME, "", GENERIC_USERNAME, GENERIC_PASSWORD),
                "User should not be created when the email is an empty string");

        // Assert that the user is not created in the database
        assertNull(userService.getUserByUsername(GENERIC_USERNAME),
                "User should not exist in the database when the email is an empty string");
    }

    @Test
    // Attempt to create a user with an empty string for the full name and email
    public void testCreateUserEmptyFullNameAndEmail() {
        // Assert that the createUser method returns null when both the full name and
        // email are empty strings
        assertNull(userService.createUser("", "", GENERIC_USERNAME, GENERIC_PASSWORD),
                "User should not be created when both the full name and email are empty strings");

        // Assert that the user is not created in the database
        assertNull(userService.getUserByUsername(GENERIC_USERNAME),
                "User should not exist in the database when both the full name and email are empty strings");
    }

    @Test
    // Attempt to create a user with an empty string for the username
    public void testCreateUserEmptyUsername() {
        // Assert that the createUser method returns null when the username is an empty
        // string
        assertNull(userService.createUser(GENERIC_FULL_NAME, GENERIC_EMAIL, "", GENERIC_PASSWORD),
                "User should not be created when the username is an empty string");

        // Since the username is a unique identifier, there's no need to check for the
        // user's existence in the database.
    }

    @Test
    // Attempt to create a user with an empty string for the email and username
    public void testCreateUserEmptyUsernameAndEmail() {
        // Assert that the createUser method returns null when both the email and
        // username are empty strings
        assertNull(userService.createUser(GENERIC_FULL_NAME, "", "", GENERIC_PASSWORD),
                "User should not be created when both the email and username are empty strings");

        // Since both the email and username are required fields, there's no need to
        // check for the user's existence in the database.
    }

    @Test
    // Attempt to create a user with an empty string for the full name, email, and
    // username
    public void testCreateUserEmptyInputsAndFullName() {
        // Assert that the createUser method returns null when all fields are empty
        // strings except for the full name
        assertNull(userService.createUser("", "", "", GENERIC_PASSWORD),
                "User should not be created when all fields are empty strings except for the full name");

        // Since none of the required fields are provided, there's no need to check for
        // the user's existence in the database.
    }

    @Test
    // Attempt to create a user with an empty string for the password
    public void testCreateUserEmptyPassword() {
        // Assert that the createUser method returns null when the password is an empty
        // string
        assertNull(userService.createUser(GENERIC_FULL_NAME, GENERIC_EMAIL, GENERIC_PASSWORD, ""),
                "User should not be created when the password is an empty string");

        // Assert that the user is not created in the database
        assertNull(userService.getUserByUsername(GENERIC_USERNAME),
                "User should not exist in the database when the password is an empty string");
    }

    @Test
    // Attempt to create a user with an empty string for the username and password
    public void testCreateUserEmptyUsernameAndPassword() {
        // Assert that the createUser method returns null when both the username and
        // password are empty strings
        assertNull(userService.createUser(GENERIC_FULL_NAME, GENERIC_EMAIL, "", ""),
                "User should not be created when both the username and password are empty strings");

        // Since both the username and password are required fields, there's no need to
        // check for the user's existence in the database.
    }

    @Test
    // Attempt to create a user with an empty string for the email, username, and
    // password
    public void testCreateUserEmptyUsernameFullNameAndPassword() {
        // Assert that the createUser method returns null when all fields are empty
        // strings except for the full name
        assertNull(userService.createUser(GENERIC_FULL_NAME, "", "", ""),
                "User should not be created when all fields are empty strings except for the full name");

        // Since none of the required fields are provided, there's no need to check for
        // the user's existence in the database.
    }

    @Test
    // Attempt to create a user with an empty string for everything
    public void testCreateUserAllEmptyInputs() {
        // Assert that the createUser method returns null when all fields are empty
        // strings
        assertNull(userService.createUser("", "", "", ""),
                "User should not be created when all fields are empty strings");

        // Since none of the required fields are provided, there's no need to check for
        // the user's existence in the database.
    }

    @Test
    // Attempt to create a user with bad credentials
    public void testCreateUserWithBadCredentials() {
        // Assert that the createUser method returns null when all credentials are
        // invalid
        assertNull(userService.createUser(BAD_USER_NAME, BAD_USER_EMAIL, BAD_USER_USERNAME, BAD_USER_PASSWORD),
                "User should not be created when all credentials are invalid");
    }

    @Test
    // Create a user that doesn't meet username length reqs
    public void testCreateUserWithShortUsername() {
        final String shortUsername = "B";
        // Assert that the createUser method returns null when the username is too short
        assertNull(userService.createUser(GENERIC_FULL_NAME, GENERIC_EMAIL, shortUsername, GENERIC_PASSWORD),
                "User should not be created when the username is too short");
    }

    @Test
    // Attempt to create a user with a short but valid email
    public void testCreateUserWithShortValidEmail() {
        final String shortEmail = "m@m.com";

        // Assert that the createUser method creates a user with a valid short email
        // address
        assertNotNull(userService.createUser(GENERIC_FULL_NAME, shortEmail, GENERIC_USERNAME, GENERIC_PASSWORD),
                "User should be created when the email address is short but valid");

        // Verify that the user is created correctly
        SiteUser user = userService.getUserByUsername(GENERIC_USERNAME);
        assertNotNull(user, "User should exist in the database after creation");
        assertEquals(user.getEmail(), shortEmail,
                "User's email address should match the specified short email address");
    }

    @Test
    // Attempt to create a user with short invalid emails
    public void testCreateUsersWithShortInvalidEmails() {
        final String shortEmailNoAt = "mm.com";
        final String shortEmailNoTld = "m@m";
        final String shortEmailNoAtNoTld = "mm";

        // Assert that the createUser method returns null when the email address lacks
        // an '@' symbol
        assertNull(userService.createUser(GENERIC_FULL_NAME, shortEmailNoAt, GENERIC_USERNAME, GENERIC_PASSWORD),
                "User should not be created when the email address lacks an '@' symbol");

        // Assert that the createUser method returns null when the email address lacks a
        // top-level domain (TLD)
        assertNull(userService.createUser(GENERIC_FULL_NAME, shortEmailNoTld, GENERIC_USERNAME, GENERIC_PASSWORD),
                "User should not be created when the email address lacks a top-level domain (TLD)");

        // Assert that the createUser method returns null when the email address lacks
        // both an '@' symbol and a TLD
        assertNull(userService.createUser(GENERIC_FULL_NAME, shortEmailNoAtNoTld, GENERIC_USERNAME, GENERIC_PASSWORD),
                "User should not be created when the email address lacks both an '@' symbol and a top-level domain (TLD)");
    }

    @Test
    // Make sure numeric passwords work
    public void testCreateUserWithValidPasswordContainingNumbers() {
        final String numericPassword = "12345678";

        // Assert that the createUser method creates a user with a valid numeric
        // password
        assertNotNull(userService.createUser(GENERIC_FULL_NAME, GENERIC_EMAIL, GENERIC_USERNAME, numericPassword),
                "User should be created when the password contains numbers and meets the minimum length requirement");

        // Verify that the user is created correctly
        SiteUser user = userService.getUserByUsername(GENERIC_USERNAME);
        assertNotNull(user, "User should exist in the database after creation");
    }

    @Test
    // Attempt to create a user with a password that's too short
    public void testCreateUserWithInvalidShortPassword() {
        final String shortPassword = "pass";

        // Assert that the createUser method returns null when the password is too short
        assertNull(userService.createUser(GENERIC_FULL_NAME, GENERIC_EMAIL, GENERIC_USERNAME, shortPassword),
                "User should not be created when the password is too short (less than the minimum 8-character requirement)");

        // Verify that the user is not created in the database
        assertNull(userService.getUserByUsername(GENERIC_USERNAME),
                "User should not exist in the database since creation failed due to a short password");
    }

    /*
     * User Lookup Tests
     */

    @Test
    // Lookup a user by id
    public void testGetUserById() {
        // Create users
        SiteUser createdJohn = userService.createUser(JOHN_NAME, JOHN_EMAIL, JOHN_USERNAME, JOHN_PASSWORD);
        userService.createUser(ALICE_NAME, ALICE_EMAIL, ALICE_USERNAME, ALICE_PASSWORD);
        userService.createUser(JANE_NAME, JANE_EMAIL, JANE_USERNAME, JANE_PASSWORD);

        // Get an existing user by their ID
        SiteUser fetchedJohn = userService.getUserById(createdJohn.getId());

        // Ensure the returned user is not null
        assertNotNull(fetchedJohn, "The user should exist");
        // Verify that the user's full name matches
        assertEquals(createdJohn.getFullName(), fetchedJohn.getFullName(),
                "The user should have the correct full name");
        // Verify that the user's email matches
        assertEquals(createdJohn.getEmail(), fetchedJohn.getEmail(), "The user should have the correct email address");
        // Verify that the username matches
        assertEquals(createdJohn.getUsername(), fetchedJohn.getUsername(), "The user should have the correct username");
    }

    @Test
    // Look up a user that doesn't exist by their id
    public void testGetNonExistentUserById() {
        final String nonExistantUsername = "nonexistentusername";
        // Attempt to get a user that does not exist by their ID
        SiteUser user = userService.getUserByUsername(nonExistantUsername);
        assertNull(user, "Retrieving a non-existant user from the database should return null");
    }

    @Test
    // Look up a nonexistent user by username
    public void testGetNonExistentUserByUsername() {
        final String nonExistantUsername = "nonexistentusername";
        // Attempt to get a user that does not exist by their username
        SiteUser user = userService.getUserByUsername(nonExistantUsername);
        assertNull(user, "Retrieving a non-existant user from the database should return null");
    }

    @Test
    // Look up a user by username
    public void testGetUserByUsername() {
        // Create users
        assertNotNull(userService.createUser(JOHN_NAME, JOHN_EMAIL, JOHN_USERNAME, JOHN_PASSWORD),
                "Initial user creation should succeed");
        assertNotNull(userService.createUser(ALICE_NAME, ALICE_EMAIL, ALICE_USERNAME, ALICE_PASSWORD),
                "Initial user creation should succeed");
        SiteUser createdJane = userService.createUser(JANE_NAME, JANE_EMAIL, JANE_USERNAME, JANE_PASSWORD);
        assertNotNull(createdJane, "Initial user creation should succeed");

        // Get an existing user by their username
        SiteUser fetchedJane = userService.getUserByUsername(createdJane.getUsername());

        // Ensure the returned user is not null
        assertNotNull(fetchedJane, "User should be retrieved from the database");
        // Verify that the user's full name matches
        assertEquals(createdJane.getFullName(), fetchedJane.getFullName(),
                "Retrieved user should have the correct full name");
        // Verify that the user's email matches
        assertEquals(createdJane.getEmail(), fetchedJane.getEmail(), "Retrieved user should have the correct email");
        // Verify that the username matches
        assertEquals(createdJane.getUsername(), fetchedJane.getUsername(),
                "Retrieved user should have the correct username");
    }

    /**
     * Password Update Tests
     */

    @Test
    // Correctly update a password
    public void testUpdatePassword() {
        final String newPassword = "newpassword456";
        // Get a user for password update
        SiteUser unicodeMan = userService.createUser(UNICODE_NAME, UNICODE_EMAIL, UNICODE_USERNAME, UNICODE_PASSWORD);

        // Update the user's password
        userService.updatePassword(unicodeMan, UNICODE_PASSWORD, newPassword);

        // Verify that the password has been updated
        SiteUser updatedUser = userService.getUserById(unicodeMan.getId());
        assertNotNull(updatedUser, "User should exist");
        assertTrue(BCrypt.checkpw(newPassword, unicodeMan.getHashedPassword()),
                "Hashed password should match newly created password");
    }

    @Test
    // Try to update a user with an incorrect current password
    public void testUpdatePasswordWithIncorrectCurrentPassword() {
        final String incorrectPassword = "incorrectpassword";
        final String newPassword = "newpassword456";
        // Get a user for password update
        SiteUser user = userService.createUser(UNICODE_NAME, UNICODE_EMAIL, UNICODE_USERNAME, UNICODE_PASSWORD);

        // Attempt to update the user's password with an incorrect current password
        assertFalse(userService.updatePassword(user, incorrectPassword, newPassword), "Password change should fail");
    }

    @Test
    // Try to update a password with a weak new password
    public void testUpdatePasswordWithWeakNewPassword() {
        final String weakPassword = "weak";
        // Get a user for password update
        SiteUser user = userService.createUser(UNICODE_NAME, UNICODE_EMAIL, UNICODE_USERNAME, UNICODE_PASSWORD);

        // Attempt to update the user's password with a weak new password
        assertFalse(userService.updatePassword(user, UNICODE_PASSWORD, weakPassword),
                "Service shouldn't allow password change");
    }

    @Test
    // Try to update a password with an empty new password
    public void testUpdatePasswordWithEmptyNewPassword() {
        final String emptyPassword = "";
        // Get a user for password update
        SiteUser user = userService.createUser(UNICODE_NAME, UNICODE_EMAIL, UNICODE_USERNAME, UNICODE_PASSWORD);

        // Attempt to update the user's password with an empty new password
        assertFalse(userService.updatePassword(user, UNICODE_PASSWORD, emptyPassword),
                "Service shouldn't allow password change to empty password");
    }

    @Test
    // Try to update a password with a null new password
    public void testUpdatePasswordWithNullNewPassword() {
        // Get a user for password update
        SiteUser user = userService.createUser(UNICODE_NAME, UNICODE_EMAIL, UNICODE_USERNAME, UNICODE_PASSWORD);

        // Attempt to update the user's password with a null new password
        assertFalse(userService.updatePassword(user, UNICODE_PASSWORD, null),
                "Service shouldn't allow password change to null password");
    }

    @Test
    // Try to update the password for a null user
    public void testUpdatePasswordWithNullUser() {
        final String newPassword = "newpassword456";

        // Attempt to update the password for a null user
        assertFalse(userService.updatePassword(null, UNICODE_PASSWORD, newPassword),
                "Service shouldn't allow password change for null user");
    }

    @Test
    // Try to update the password without providing a current password
    public void testUpdatePasswordWithoutCurrentPassword() {
        final String newPassword = "newpassword456";
        // Get a user for password update
        SiteUser user = userService.createUser(UNICODE_NAME, UNICODE_EMAIL, UNICODE_USERNAME, UNICODE_PASSWORD);

        // Attempt to update the password without providing a current password
        assertFalse(userService.updatePassword(user, null, newPassword),
                "Service shouldn't allow password change without authenticating");
    }

    /*
     * Username Update Tests
     */
    @Test
    // Update a username correctly
    public void testUpdateUsername() {
        final String newUsername = "newusername123";
        // Get a user for username update
        SiteUser unicodeMan = userService.createUser(UNICODE_NAME, UNICODE_EMAIL, UNICODE_USERNAME, UNICODE_PASSWORD);

        // Update the user's username
        assertTrue(userService.updateUsername(unicodeMan, UNICODE_PASSWORD, newUsername),
                "Service should report password change success");

        // Verify that the username has been updated
        SiteUser updatedUser = userService.getUserById(unicodeMan.getId());
        assertNotNull(updatedUser, "User id should not change");
        assertEquals(newUsername, updatedUser.getUsername(), "Username should not change");

        // Make sure fetching by username works too
        updatedUser = userService.getUserByUsername(newUsername);
        assertNotNull(updatedUser, "Should be able to retrieve by new username");
        assertEquals(unicodeMan.getId(), updatedUser.getId(), "Ids should still match");

        // Make sure old username doesn't work anymore
        updatedUser = userService.getUserByUsername(UNICODE_USERNAME);
        assertNull(updatedUser, "Shouldn't be possible to retrieve by old username");
    }

    @Test
    // Attempt to update a username with a name that doesn't meet length
    // requirements
    public void testUpdateShortUsername() {
        final String newPassword = JANE_PASSWORD;
        final String newShortUsername = "j";
        SiteUser jane = userService.createUser(JANE_NAME, JANE_EMAIL, JANE_USERNAME, JANE_PASSWORD);

        // Should not update username because username is too short
        assertFalse(userService.updateUsername(jane, newPassword, newShortUsername),
                "Should report username unchanged");
        assertEquals(JANE_USERNAME, jane.getUsername(), "Username should not be changed");
    }

    @Test
    // Attempt to update a username with an incorrect password confirmation
    public void testUpdateUsernameWrongPassword() {
        final String wrongPassword = "this_password_is_wrong";
        final String newUsername = "janie";
        SiteUser jane = userService.createUser(JANE_NAME, JANE_EMAIL, JANE_USERNAME, JANE_PASSWORD);
        final int janeId = jane.getId();

        // Should not update username because password is wrong
        assertFalse(userService.updateUsername(jane, wrongPassword, newUsername),
                "Service should report username unchanged");
        assertEquals(JANE_USERNAME, jane.getUsername(), "Username should be unchanged");
        assertNull(userService.getUserByUsername(newUsername),
                "Shouldn't be possible to retrieve user from db by new username");
        assertEquals(userService.getUserById(janeId).getUsername(), JANE_USERNAME,
                "Should still be possible to retieve user by id");
    }

    @Test
    // Attempt to update a username with a null value
    public void testUpdateNullUsername() {
        final String newPassword = JANE_PASSWORD;
        final String emptyUsername = "";
        final String nullUsername = null;
        SiteUser jane = userService.createUser(JANE_NAME, JANE_EMAIL, JANE_USERNAME, JANE_PASSWORD);

        // Should not update (thus return false) for an empty username
        assertFalse(userService.updateUsername(jane, newPassword, emptyUsername),
                "Should report username unchanged for empty username");
        assertEquals(JANE_USERNAME, jane.getUsername(), "Username should remain unchanged");

        // Should not update (thus return false) for a null username
        assertFalse(userService.updateUsername(jane, newPassword, nullUsername),
                "Should report username unchanged for null username");
        assertEquals(JANE_USERNAME, jane.getUsername(), "Username should remain unchanged");
    }

    @Test
    // Attempt to change username to one that's already taken
    public void testUpdateUsernameWithExistingUsername() {
        userService.createUser(JOHN_NAME, JOHN_EMAIL, JOHN_USERNAME, JOHN_PASSWORD);
        SiteUser user = userService.createUser(UNICODE_NAME, UNICODE_EMAIL, UNICODE_USERNAME, UNICODE_PASSWORD);

        // Attempt to update the user's username to an existing username
        assertFalse(userService.updateUsername(user, UNICODE_USERNAME, JOHN_USERNAME),
                "Username should remain unchanged");

        SiteUser unicodeMan = userService.getUserByUsername(JOHN_USERNAME);
        assertEquals(unicodeMan.getFullName(), JOHN_NAME, "Existing user\'s name should be unchanged");
        assertEquals(unicodeMan.getEmail(), JOHN_EMAIL, "Existing user\'s email should be unchanged");
    }

}
