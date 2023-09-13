package edu.carroll.bankapp.service;

import edu.carroll.bankapp.jpa.model.User;
import edu.carroll.bankapp.jpa.repo.UserRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LoginServiceImpl implements LoginService {
    private final UserRepository userRepo;

    private static final Logger log = LoggerFactory.getLogger(LoginServiceImpl.class);

    public LoginServiceImpl(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    /**
     * Given a loginForm, determine if the information provided is valid, and the
     * user exists in the system.
     *
     * @param username - Username of the person attempting to log in
     * @param password - Raw password provided by the user logging in
     * @return true if data exists and matches what's on record, false otherwise
     */
    @Override
    public boolean validateUser(String username, String password) {
        log.info("Checking login with username: " + username);
        List<User> users = userRepo.findByUsernameIgnoreCase(username);
        if (users.size() == 0) {
            log.warn("Didn't find user with username: " + username);
            return false;
        }
        if (users.size() > 1) {
            log.error("Got more than one user with username: " + username);
            return false;
        }
        User user = users.get(0);

        if (checkPassword(password, user.getHashedPassword())) {
            user.generateNewToken();
            userRepo.save(user);
            log.info(username + " logged in successfully");
            return true;
        }
        log.warn(username + " failed to log in");
        return false;
    }

    /**
     * Look up a user given their token
     *
     * @param token the user's token
     * @return The User with that token
     */
    public User getUserFromToken(String token) {
        log.debug("Looking up user from token");
        List<User> users = userRepo.findByToken(token);

        if (users.size() == 0) {
            log.warn("Didn't find user with matching token");
            return null;
        }
        if (users.size() > 1) {
            log.error("Got more than one user from token");
            return null;
        }
        User user = users.get(0);
        // The token check is redundant, but it doesn't hurt to double-check.
        if (token.equals(user.getToken()) && System.currentTimeMillis() < user.getTokenExpiry()) {
            log.info("Found user: " + user.getUsername());
            return user;
        } else if (token.equals(user.getToken()) && System.currentTimeMillis() > user.getTokenExpiry()) {
            log.info(user.getUsername() + " token expired");
        }

        return null;
    }

    public static boolean checkPassword(String passwordToCheck, String hashedPassword) {
        return BCrypt.checkpw(passwordToCheck, hashedPassword);
    }
}
