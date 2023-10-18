package edu.carroll.bankapp.service;

import edu.carroll.bankapp.jpa.model.SiteUser;
import edu.carroll.bankapp.jpa.repo.UserRepository;

import java.util.List;

import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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
}
