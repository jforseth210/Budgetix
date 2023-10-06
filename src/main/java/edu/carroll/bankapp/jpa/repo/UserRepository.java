package edu.carroll.bankapp.jpa.repo;

import java.util.List;

import edu.carroll.bankapp.jpa.model.SiteUser;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Interface implemented by Hibernate for querying SiteUser information from the
 * database
 */
public interface UserRepository extends JpaRepository<SiteUser, Integer> {
    /**
     * Case-insensitively fetch a SiteUser based on their username.
     *
     * @param username
     * @return
     */
    List<SiteUser> findByUsernameIgnoreCase(String username);

    /**
     * Fetch a SiteUser based on the id.
     * 
     * @param id
     * @return
     */
    List<SiteUser> findById(int id);
}