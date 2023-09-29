package edu.carroll.bankapp.service;

import edu.carroll.bankapp.jpa.model.SecurityUser;
import edu.carroll.bankapp.jpa.model.User;
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
    private final UserRepository userRepo;

    private static final Logger log = LoggerFactory.getLogger(CustomUserDetailsService.class);

    public CustomUserDetailsService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Lookup the user
        List<User> users = userRepo.findByUsernameIgnoreCase(username);
        // Complain if we don't get exactly one user
        if (users.size() == 0) {
            log.warn("Didn't find user with username: " + username);
            throw new UsernameNotFoundException(username, null);
        }
        if (users.size() > 1) {
            log.error("Got more than one user with username: " + username);
            throw new IllegalStateException("Multiple users with username: " + username, null);
        }
        // Return the user
        User user = users.get(0);
        return new SecurityUser(user);
    }
}
