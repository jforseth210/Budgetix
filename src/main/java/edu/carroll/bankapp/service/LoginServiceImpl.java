package edu.carroll.bankapp.service;

import java.util.List;

import edu.carroll.bankapp.web.form.LoginForm;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
public class LoginServiceImpl implements LoginService {

    /**
     * Given a loginForm, determine if the information provided is valid, and the user exists in the system.
     *
     * @param username - Username of the person attempting to login
     * @param password - Raw password provided by the user logging in
     * @return true if data exists and matches what's on record, false otherwise
     */
    @Override
    public boolean validateUser(String username, String password) {
        return false;
    }

    public static String generateHash(String plaintextPassword) {
        return BCrypt.hashpw(plaintextPassword, BCrypt.gensalt());
    }

    public static boolean checkPassword(String passwordToCheck, String hashedPassword) {
        return BCrypt.checkpw(passwordToCheck, hashedPassword);
    }
}
