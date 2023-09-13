package edu.carroll.bankapp.service;

import edu.carroll.bankapp.User;

public interface LoginService {
    /**
     * Given a loginForm, determine if the information provided is valid, and the user exists in the system.
     *
     * @param username - Username of the person attempting to login
     * @param password - Raw password provided by the user logging in
     * @return true if data exists and matches what's on record, false otherwise
     */
    boolean validateUser(String username, String password);

    /**
     * Look up a user given their token
     *
     * @param token the user's token
     * @return The User with that token
     */
    User getUserFromToken(String token);

    /**
     * as
     * Get a User object given their username
     *
     * @param username
     * @return User with that username
     */
    User getUserFromUsername(String username);
}
