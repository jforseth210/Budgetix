package edu.carroll.bankapp.service;

import edu.carroll.bankapp.jpa.model.SecurityUser;
import edu.carroll.bankapp.jpa.model.SiteUser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Implements the UserDetailsService required by Spring Security
 */
@Service
public class CustomUserDetailsServiceImpl implements CustomUserDetailsService {
    private static final Logger log = LoggerFactory.getLogger(CustomUserDetailsServiceImpl.class);
    private final UserServiceImpl userServiceImpl;

    public CustomUserDetailsServiceImpl(UserServiceImpl userServiceImpl) {
        this.userServiceImpl = userServiceImpl;
    }

    /**
     * Fetch a SecurityUser using the given username
     *
     * @param username the username identifying the user whose data is required.
     * @return siteUser - a user that is using our site
     * @throws UsernameNotFoundException if there's no user matching the given username
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Lookup the siteUser
        SiteUser siteUser = userServiceImpl.getUserByUsername(username);

        // UserDetailsService contract requires us to throw an exception instead of
        // returning null
        if (siteUser == null) {
            throw new UsernameNotFoundException("Didn't find user with username: " + username);
        }

        // Create a Spring Security UserDetails object from siteUser 
        return new SecurityUser(siteUser);
    }
}
