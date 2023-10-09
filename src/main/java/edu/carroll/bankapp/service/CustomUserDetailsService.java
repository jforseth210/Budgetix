package edu.carroll.bankapp.service;

import edu.carroll.bankapp.jpa.model.SecurityUser;
import edu.carroll.bankapp.jpa.model.SiteUser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Implements the UserDetailsService required by Spring Security
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {
    private static final Logger log = LoggerFactory.getLogger(CustomUserDetailsService.class);
    private final UserService userService;

    public CustomUserDetailsService(UserService userService) {
        this.userService = userService;
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
        SiteUser siteUser = userService.getUser(username);

        // UserDetailsService contract requires us to throw an exception instead of
        // returning null
        if (siteUser == null) {
            throw new UsernameNotFoundException("Didn't find user with username: " + username);
        }
        return new SecurityUser(siteUser);
    }
}
