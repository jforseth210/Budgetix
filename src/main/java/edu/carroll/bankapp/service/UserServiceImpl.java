package edu.carroll.bankapp.service;

import edu.carroll.bankapp.jpa.model.SiteUser;
import edu.carroll.bankapp.jpa.repo.UserRepository;

import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Email;

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
    public SiteUser getUser(int userId) {
        log.debug("Getting user with id: {}", userId);
        List<SiteUser> siteUsers = userRepo.findById(userId);
        if (siteUsers.isEmpty()) {
            log.warn("Didn't find siteUser with id: {}", userId);
            return null;
        }
        if (siteUsers.size() > 1) {
            log.error("Got more than one siteUser with id: {}", userId);
            throw new IllegalStateException("Multiple siteUsers with id: " + userId, null);
        }
        return siteUsers.get(0);
    }

    /**
     * Get a user from the given username
     */
    public SiteUser getUser(String username) {
        log.debug("Getting user with username: {}", username);
        List<SiteUser> siteUsers = userRepo.findByUsernameIgnoreCase(username);
        if (siteUsers.size() == 0) {
            log.warn("Didn't find siteUser with username: {}", username);
            return null;
        }
        if (siteUsers.size() > 1) {
            log.error("Got more than one siteUser with username: {}", username);
            throw new IllegalStateException("Multiple siteUsers with username: " + username, null);
        }
        return siteUsers.get(0);
    }

    /**
     * Create a username and save it in the database (without confirm password)
     *
     * @return
     */
    public SiteUser createUser(String fullName, String email, String username, String rawPassword) {
        if (getUser(username) != null) {
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
     * Should ONLY be used for testing
     */
    public void deleteAllSiteUsers() {
        log.warn("Deleting all users");
        userRepo.deleteAll();
    }

    public void updatePassword(SiteUser user, String oldPassword, String newPassword, String newConfirm) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if (passwordEncoder.matches(oldPassword, user.getHashedPassword())) {
            if (newPassword.equals(newConfirm)) {
                // Update the password to the new one
                user.setHashedPassword(BCrypt.hashpw(newPassword, BCrypt.gensalt()));
                userRepo.save(user);
                log.info("Successfully saved new password");
            } else {
                log.info("The new passwords do not match");
                throw new IllegalArgumentException("New password and confirmation do not match.");
            }
        } else {
            log.info("Incorrect current password");
            throw new IllegalArgumentException("Old password doesn't match the current password.");
        }
    }

    public void updateUsername(SiteUser user, String confirmPassword, String newUsername) {
        if (getUser(newUsername) != null) {
            log.info("Attempt was made to update username from {} to existing user {}", user.getUsername(), newUsername);
            return; 
        }
        user.setUsername(newUsername);
        userRepo.save(user);
    }
}
