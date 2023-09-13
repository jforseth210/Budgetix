package edu.carroll.bankapp.service;

import java.io.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import edu.carroll.bankapp.User;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class LoginServiceImpl implements LoginService {
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final Logger log = LoggerFactory.getLogger(LoginServiceImpl.class);
    private User[] users;


    /**
     * Given a loginForm, determine if the information provided is valid, and the user exists in the system.
     *
     * @param username - Username of the person attempting to login
     * @param password - Raw password provided by the user logging in
     * @return true if data exists and matches what's on record, false otherwise
     */
    @Override
    public boolean validateUser(String username, String password) {
        log.info("Checking login with username:" + username);
        if (users == null)
            loadUsers();
        for (User user : users) {
            if (user.getUsername().equals(username) && checkPassword(password, user.getHashedPassword())) {
                user.generateNewToken();
                writeUsers();
                log.info(username + " logged in successfully");
                return true;
            }
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
        loadUsers();
        for (User user : users) {
            if (token.equals(user.getToken()) && System.currentTimeMillis() < user.getTokenExpiry()) {
                log.info("Found user: " + user.getUsername());
                return user;
            } else if (token.equals(user.getToken()) && System.currentTimeMillis() > user.getTokenExpiry()) {
                log.info(user.getUsername() + " token expired");
            }
        }
        return null;
    }

    /**
     * Get a User object given their username
     *
     * @param username
     * @return User with that username
     */
    public User getUserFromUsername(String username) {
        log.debug("Looking up user from " + username);
        if (users == null)
            loadUsers();
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        log.info("Didn't find: " + username);
        return null;
    }

    private void loadUsers() {
        log.debug("Loading users");
        FileReader reader = null;
        try {
            reader = new FileReader("users.json");
            users = gson.fromJson(reader, User[].class);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        log.debug("Got " + users.length + " users");
    }

    private void writeUsers() {
        log.debug("Writing users");
        try (FileWriter writer = new FileWriter("users.json")) {
            gson.toJson(users, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.debug("Wrote " + users.length + " users");
    }

    public static boolean checkPassword(String passwordToCheck, String hashedPassword) {
        return BCrypt.checkpw(passwordToCheck, hashedPassword);
    }
}
