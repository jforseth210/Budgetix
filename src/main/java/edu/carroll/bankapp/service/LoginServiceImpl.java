package edu.carroll.bankapp.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import edu.carroll.bankapp.User;
import edu.carroll.bankapp.web.form.LoginForm;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class LoginServiceImpl implements LoginService {
    private Gson gson = new Gson();
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
        System.out.println(generateHash(password));
        if (users == null)
            loadUsers();
        for (User user : users) {
            if (user.getUsername().equals(username) && checkPassword(password, user.getHashedPassword())) {
                System.out.println("Success");
                return true;
            }
        }
        return false;
    }

    private void loadUsers() {
        Scanner s = new Scanner(getClass().getResourceAsStream("/users.json")).useDelimiter("\\A");
        String result = s.hasNext() ? s.next() : "";
        users = gson.fromJson(result, User[].class);
    }


    public static String generateHash(String plaintextPassword) {
        return BCrypt.hashpw(plaintextPassword, BCrypt.gensalt());
    }

    public static boolean checkPassword(String passwordToCheck, String hashedPassword) {
        return BCrypt.checkpw(passwordToCheck, hashedPassword);
    }
}
