package edu.carroll.bankapp.testdata;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import edu.carroll.bankapp.jpa.model.SiteUser;
import edu.carroll.bankapp.service.UserService;

/**
 * Generic users that can be created for testing purposes
 */
@Component
public class TestUsers {
    @Autowired
    UserService userService;

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

    public SiteUser createJohnDoe() {
        return userService.createUser(JOHN_NAME, JOHN_EMAIL, JOHN_USERNAME, JOHN_PASSWORD);
    }

    public SiteUser createJaneSmith() {
        return userService.createUser(JANE_NAME, JANE_EMAIL, JANE_USERNAME, JANE_PASSWORD);
    }

    public SiteUser createAliceJohnson() {
        return userService.createUser(ALICE_NAME, ALICE_EMAIL, ALICE_USERNAME, ALICE_PASSWORD);
    }

    public SiteUser createUnicodeMan() {
        return userService.createUser(UNICODE_NAME, UNICODE_EMAIL, UNICODE_USERNAME, UNICODE_PASSWORD);
    }

    public SiteUser createBadUser() {
        return userService.createUser(BAD_USER_NAME, BAD_USER_EMAIL, BAD_USER_USERNAME, BAD_USER_PASSWORD);
    }
}
