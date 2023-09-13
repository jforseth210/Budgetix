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
        if (users == null)
            loadUsers();
        for (User user : users) {
            if (user.getUsername().equals(username) && checkPassword(password, user.getHashedPassword())) {
                user.generateNewToken();
                writeUsers();
                return true;
            }
        }
        return false;
    }


    public User getUserFromToken(String token) {
        loadUsers();
        for (User user : users) {
            if (token.equals(user.getToken()) && System.currentTimeMillis() < user.getTokenExpiry()) {
                return user;
            }
        }
        return null;
    }

    public User getUserFromUsername(String username) {
        if (users == null)
            loadUsers();
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    private void loadUsers() {
        FileReader reader = null;
        try {
            reader = new FileReader("users.json");
            users = gson.fromJson(reader, User[].class);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeUsers() {
        try (FileWriter writer = new FileWriter("users.json")) {
            gson.toJson(users, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String generateHash(String plaintextPassword) {
        return BCrypt.hashpw(plaintextPassword, BCrypt.gensalt());
    }

    public static boolean checkPassword(String passwordToCheck, String hashedPassword) {
        return BCrypt.checkpw(passwordToCheck, hashedPassword);
    }
}
