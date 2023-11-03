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

    public SiteUser createJohnDoe() {
        return userService.createUser("John Doe", "john@example.com", "johndoe", "password123");
    }

    public SiteUser createJaneSmith() {
        return userService.createUser("Jane Smith", "jane@example.com", "janesmith", "letmein456");
    }

    public SiteUser createAliceJohnson() {
        return userService.createUser("Alice Johnson", "alice@example.com", "alicejohnson", "mysecret123");
    }

    public SiteUser createUnicodeMan() {
        return userService.createUser("𝔘𝔫𝔦𝔠𝔬𝔡𝔢 𝔐𝔞𝔫!",
                "𝕚𝕝𝕚𝕜𝕖𝕥𝕠𝕓𝕣𝕖𝕒𝕜𝕥𝕙𝕚𝕟𝕘𝕤@𝕖𝕞𝕒𝕚𝕝.𝕔𝕠𝕞",
                "☕☕☕☕", "⿈⍺✋⇏⮊⎏⇪⤸Ⲥ↴⍁➄⼉⦕ⶓ∧⻟⍀⇝⧽");
    }

    public SiteUser createBadUser() {
        return userService.createUser("Bob Marley", "bobby", "B", "p");
    }
}
