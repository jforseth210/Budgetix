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
     * Check whether a given email adress is already in use
     * 
     * @param email the email to look up
     * @return true if available, false if taken
     */
    public boolean isEmailAvailable(String email) {
        return userRepo.findByEmailIgnoreCase(email).isEmpty();
    }

    /**
     * Create a username and save it in the database (without confirm password)
     *
     * @return The SiteUser if created successfully, null otherwise
     */
    public ServiceResponse<SiteUser> createUser(String fullName, String email, String username, String rawPassword) {
        // Validate that fields aren't blank
        if (fullName == null || fullName.equals("")) {
            log.debug("Invalid username: {}", fullName);
            return new ServiceResponse<SiteUser>(null, "Full name cannot be blank");
        }
        if (email == null || email.equals("")) {
            log.debug("Invalid email: {}", email);
            return new ServiceResponse<SiteUser>(null, "Email cannot be blank");
        }
        if (username == null || username.equals("")) {
            log.debug("Invalid username: {}", username);
            return new ServiceResponse<SiteUser>(null, "Username cannot be blank");
        }
        if (rawPassword == null || rawPassword.equals("")) {
            log.debug("Invalid raw password");
            return new ServiceResponse<SiteUser>(null, "Password cannot be blank");
        }
        // Make sure email is actually an email
        if (!isEmail(email)) {
            log.debug("{} doesn't look like an email address", email);
            return new ServiceResponse<SiteUser>(null, "Email must be a valid email address");
        }
        // Don't accept excessively long username
        if (username.length() > 255) {
            log.debug("{} is too long", username);
            return new ServiceResponse<SiteUser>(null, "Username is too long");
        }
        if (fullName.length() > 255) {
            log.debug("{} is too long", fullName);
            return new ServiceResponse<SiteUser>(null, "Full name is too long");
        }
        // Check arbitrary length requirements for username
        if (username.length() <= MIN_USERNAME_LENGTH) {
            log.debug("Username {} doesn't meet length requirements", username);
            return new ServiceResponse<SiteUser>(null, "Username doesn't meet length requirements");
        }

        // Check arbitrary length requirements for password
        if (rawPassword.length() < MIN_PASSWORD_LENGTH) {
            log.debug("Password doesn't meet length requirements");
            return new ServiceResponse<SiteUser>(null, "Password doesn't meet length requirements");
        }

        // Check arbitrary length requirements for email
        if (email.length() <= MIN_EMAIL_LENGTH) {
            log.debug("Email {} doesn't meet length requirements", email);
            return new ServiceResponse<SiteUser>(null, "Email doesn't meet length requirements");
        }

        // Make sure the username isn't taken
        if (getUserByUsername(username) != null) {
            log.info("Attempt was made to create existing user {}", username);
            return new ServiceResponse<SiteUser>(null, "Username already taken");
        }
        if (!isEmailAvailable(email)) {
            return new ServiceResponse<SiteUser>(null, "Email already in use");
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
        return new ServiceResponse<SiteUser>(newUser, "User created successfully");
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
    public ServiceResponse<Boolean> updatePassword(SiteUser user, String oldPassword, String newPassword) {
        // Make sure user to update is valid
        if (user == null) {
            return new ServiceResponse<Boolean>(false, "User cannot be blank");
        }
        // Make sure old password is valid
        if (oldPassword == null) {
            log.info("Old password cannot be blank");
            return new ServiceResponse<Boolean>(false, "Old password cannot be blank");
        }
        // Check if new password is null
        if (newPassword == null) {
            log.info("New password was null");
            return new ServiceResponse<Boolean>(false, "New password cannot be blank");
        }

        // Check if new password meets length requirements
        if (newPassword.length() < MIN_PASSWORD_LENGTH) {
            log.info("New password doesn't meet length requirements");
            return new ServiceResponse<Boolean>(false, "New password is too short");
        }

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        // Make sure the user entered their old password correctly
        if (!passwordEncoder.matches(oldPassword, user.getHashedPassword())) {
            log.info("{} inputted an incorrect current password", user.getUsername());
            return new ServiceResponse<Boolean>(false, "Incorrect old password");
        }

        // Update the password to the new one
        user.setHashedPassword(BCrypt.hashpw(newPassword, BCrypt.gensalt()));
        userRepo.save(user);
        log.info("Successfully saved new password for {}", user.getUsername());
        return new ServiceResponse<Boolean>(true, "Your password has been updated");
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
    public ServiceResponse<Boolean> updateUsername(SiteUser user, String confirmPassword, String newUsername) {
        // Make sure the password is correct
        if (!BCrypt.checkpw(confirmPassword, user.getHashedPassword())) {
            log.info("Password confirmation incorrect");
            return new ServiceResponse<Boolean>(false, "Password confirmation incorrect");
        }
        // Make sure the username isn't empty
        if (newUsername == null || newUsername.equals("")) {
            log.info("Attempt was made to update username from {} to empty string", user.getUsername(),
                    newUsername);
            return new ServiceResponse<Boolean>(false, "New username not provided");
        }
        // Make sure the username meets length requirements
        if (newUsername.length() <= MIN_USERNAME_LENGTH) {
            log.info("Username {} doesn't meet the minimum length requirements", user.getUsername(),
                    newUsername);
            return new ServiceResponse<Boolean>(false, "New username not provided");
        }
        // Make sure the username we're changing to isn't already taken
        if (getUserByUsername(newUsername) != null) {
            log.info("Attempt was made to update username from {} to existing user {}", user.getUsername(),
                    newUsername);
            return new ServiceResponse<Boolean>(false, "Username already taken");
        }

        // Update the usernameS
        user.setUsername(newUsername);
        userRepo.save(user);
        return new ServiceResponse<Boolean>(true, "Username updated");
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
