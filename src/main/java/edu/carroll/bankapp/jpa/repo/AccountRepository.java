package edu.carroll.bankapp.jpa.repo;

import java.util.List;

import edu.carroll.bankapp.jpa.model.Account;
import edu.carroll.bankapp.jpa.model.SiteUser;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Interface implemented by Hibernate for querying Account information from the database
 */
public interface AccountRepository extends JpaRepository<Account, Integer> {
    /**
     * Return a list of accounts owned by the given siteUser
     *
     * @param siteUser The account owner
     * @return
     */
    List<Account> findByOwner(SiteUser siteUser);

    /**
     * Return a single element list containing the account with the provided id.
     *
     * @param id The account id
     * @return
     */
    List<Account> findById(int id);
}