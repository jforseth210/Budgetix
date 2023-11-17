package edu.carroll.bankapp.service;

import edu.carroll.bankapp.jpa.model.SiteUser;
import edu.carroll.bankapp.jpa.repo.UserRepository;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.List;

import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * A service to handle business logic related to managing users
 */
@Service
public class UserServiceImpl implements UserService {
    private static final int MIN_USERNAME_LENGTH = 4;
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final int MIN_EMAIL_LENGTH = 5;
    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepo;

    /**
     * Default Constructor - takes userRepo as argument
     *
     * @param userRepo - userRepository
     */
    public UserServiceImpl(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    /**
     * Get a user from the given user id
     */
    public SiteUser getUserById(int userId) {
        log.debug("Getting user with id: {}", userId);
        // Fetch users with matching id from database
        List<SiteUser> siteUsers = userRepo.findById(userId);
        // Check if a user exists with that id
        if (siteUsers.isEmpty()) {
            log.warn("Didn't find siteUser with id: {}", userId);
            return null;
        }
        // Check if multiple users exist with the same id
        if (siteUsers.size() > 1) {
            log.error("Got more than one siteUser with id: {}", userId);
            return null;
        }
        // Return the user
        return siteUsers.get(0);
    }

    /**
     * Get a user from the given username
     */
    public SiteUser getUserByUsername(String username) {
        log.debug("Getting user with username: {}", username);
        // Get user by username
        List<SiteUser> siteUsers = userRepo.findByUsernameIgnoreCase(username);
        // Check if we found a user
        if (siteUsers.isEmpty()) {
            log.info("Didn't find siteUser with username: {}", username);
            return null;
        }
        // Check if we got multiple uses with the same id
        if (siteUsers.size() > 1) {
            log.error("Got more than one siteUser with username: {}", username);
            return null;
        }
        // Return the user
        return siteUsers.get(0);
    }

    /**
     * Create a username and save it in the database (without confirm password)
     *
     * @return The SiteUser if created successfully, null otherwise
     */
    public SiteUser createUser(String fullName, String email, String username, String rawPassword) {
        // Validate that fields aren't blank
        if (fullName == null || fullName.equals("")) {
            log.debug("Invalid username: {}", fullName);
            return null;
        }
        if (email == null || email.equals("")) {
            log.debug("Invalid email: {}", email);
            return null;
        }
        if (username == null || username.equals("")) {
            log.debug("Invalid username: {}", username);
            return null;
        }
        if (rawPassword == null || rawPassword.equals("")) {
            log.debug("Invalid raw password");
            return null;
        }
        // Make sure email is actually an email
        if (!isEmail(email)) {
            log.debug("{} doesn't look like an email address", email);
            return null;
        }

        // Check arbitrary length requirements
        if (username.length() <= MIN_USERNAME_LENGTH || rawPassword.length() < MIN_PASSWORD_LENGTH
                || email.length() <= MIN_EMAIL_LENGTH) {
            log.debug("username {}, password, or email {} doesn't meet length requirements", username, email);
            return null;
        }

        // Make sure the username isn't taken
        if (getUserByUsername(username) != null) {
            log.info("Attempt was made to create existing user {}", username);
            return null;
        }
        log.info("Creating a user with username: {}", username);
        // Create new user object
        SiteUser newUser = new SiteUser(
                fullName,
                email,
                username,
                BCrypt.hashpw(rawPassword, BCrypt.gensalt()));
        // Save user to database
        userRepo.save(newUser);
        return newUser;
    }

    /**
     * Allows the user to update their password and compares that the passwords that
     * they enter will match.
     *
     * @param user        - user using our site
     * @param oldPassword - the old password for the user's account
     * @param newPassword - the new password for the user's account
     * @return true / false if the password is updated
     */
    public boolean updatePassword(SiteUser user, String oldPassword, String newPassword) {
        // Make sure user to update is valid
        if (user == null) {
            return false;
        }
        // Make sure old password is valid
        if (oldPassword == null) {
            log.info("Password didn't meet length requirements");
            return false;
        }
        // Make sure password is valid
        if (newPassword == null || newPassword.length() <= MIN_PASSWORD_LENGTH) {
            log.info("New password was null or didn't meet length requirements");
            return false;
        }
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        // Make sure the user entered their old password correctly
        if (!passwordEncoder.matches(oldPassword, user.getHashedPassword())) {
            log.info("{} inputted an incorrect current password", user.getUsername());
            return false;
        }

        // Update the password to the new one
        user.setHashedPassword(BCrypt.hashpw(newPassword, BCrypt.gensalt()));
        userRepo.save(user);
        log.info("Successfully saved new password for {}", user.getUsername());
        return true;
    }

    /**
     * Allows the user to update their username and password, then logs them out and
     * back in immediately.
     *
     * @param user            - user using our site
     * @param confirmPassword - the old username for the user's account
     * @param newUsername     - the new username for the user's account
     * @return true / false if the username is updated
     */
    public boolean updateUsername(SiteUser user, String confirmPassword, String newUsername) {
        // Make sure the password is correct
        if (!BCrypt.checkpw(confirmPassword, user.getHashedPassword())) {
            log.info("Password confirmation incorrect");
            return false;
        }
        // Make sure the username isn't empty
        if (newUsername == null || newUsername.equals("")) {
            log.info("Attempt was made to update username from {} to empty string", user.getUsername(),
                    newUsername);
            return false;
        }
        // Make sure the username meets length requirements
        if (newUsername.length() <= MIN_USERNAME_LENGTH) {
            log.info("Username {} doesn't meet the minimum length requirements", user.getUsername(),
                    newUsername);
            return false;
        }
        // Make sure the username we're changing to isn't already taken
        if (getUserByUsername(newUsername) != null) {
            log.info("Attempt was made to update username from {} to existing user {}", user.getUsername(),
                    newUsername);
            return false;
        }

        // Update the usernameS
        user.setUsername(newUsername);
        userRepo.save(user);
        return true;
    }

    /**
     * Use regex from <a href="https://emailregex.com">...</a>. Jakarta *should*
     * catch this in the
     * frontend, but we want to double check in the service, just in case.
     */
    private boolean isEmail(String email) {
        String regex = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)])";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();

    }
}
