package edu.carroll.bankapp.jpa.repo;

import java.util.List;

import edu.carroll.bankapp.jpa.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Interface implemented by Hibernate for querying User information from the database
 */
public interface UserRepository extends JpaRepository<User, Integer> {
    /**
     * Case-insensitively fetch a User based on their username.
     * @param username
     * @return
     */
    List<User> findByUsernameIgnoreCase(String username);
}