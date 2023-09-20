package edu.carroll.bankapp.jpa.repo;

import java.util.List;

import edu.carroll.bankapp.jpa.model.Account;
import edu.carroll.bankapp.jpa.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Magic provided by Hibernate for looking up users
 */
public interface AccountRepository extends JpaRepository<Account, Integer> {
    // JPA throws an exception if we attempt to return a single object that doesn't
    // exist, so return a list even though we only expect either an empty list of a
    // single element.
    List<User> findByOwner(User user);
}