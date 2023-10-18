package edu.carroll.bankapp.service;

import edu.carroll.bankapp.jpa.model.SiteUser;

/**
 * Interface for managing users.
 */
public interface UserService {
    /**
     * Get a user from the given user id.
     *
     * @param userId the ID of the user
     * @return the user if found, or null if not found
     */
    SiteUser getUser(int userId);

    /**
     * Get a user from the given username.
     *
     * @param username the username of the user
     * @return the user if found, or null if not found
     */
    SiteUser getUser(String username);

    /**
     * Create a new user and save it in the database (without confirm password).
     *
     * @param fullName    the full name of the user
     * @param email       the email of the user
     * @param username    the username of the user
     * @param rawPassword the raw password of the user
     * @return the created user
     */
    SiteUser createUser(String fullName, String email, String username, String rawPassword);

    /**
     * Delete all site users. (Should ONLY be used for testing)
     */
    void deleteAllSiteUsers();
}
