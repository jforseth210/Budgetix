package edu.carroll.bankapp.service;

import edu.carroll.bankapp.jpa.model.SecurityUser;
import edu.carroll.bankapp.jpa.model.SiteUser;
import edu.carroll.bankapp.jpa.repo.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implements the UserDetailsService required by Spring Security
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {
    private static final Logger log = LoggerFactory.getLogger(CustomUserDetailsService.class);
    private final UserRepository userRepo;

    /**
     * Default Constructor - takes a userRepo as argument
     *
     * @param userRepo - UserRepository
     */
    public CustomUserDetailsService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    /**
     * Fetch a SecurityUser using the given username
     *
     * @param username the username identifying the user whose data is required.
     * @return siteUser - a user that is using our site
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Lookup the siteUser
        List<SiteUser> siteUsers = userRepo.findByUsernameIgnoreCase(username);
        if (siteUsers.size() == 0) {
            // The UserDetailsService contract requires us to throw an exception instead of
            // returning null.
            log.warn("Didn't find siteUser with username: {}", username);
            throw new UsernameNotFoundException(username, null);
        }
        if (siteUsers.size() > 1) {
            log.error("Got more than one siteUser with username: {}", username);
            throw new IllegalStateException("Multiple siteUsers with username: " + username, null);
        }

        SiteUser siteUser = siteUsers.get(0);
        return new SecurityUser(siteUser);
    }
}
