package edu.carroll.bankapp.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Interface for a custom user details service.
 */
public interface CustomUserDetailsService extends UserDetailsService {
    /**
     * Fetch a UserDetails object using the given username.
     *
     * @param username the username identifying the user whose data is required.
     * @return userDetails - a user details object for the specified username
     * @throws UsernameNotFoundException if the user is not found
     */
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
}
