package edu.carroll.bankapp.service;

import edu.carroll.bankapp.jpa.model.User;
import edu.carroll.bankapp.jpa.repo.UserRepository;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * A service to handle business logic related to managing users
 */
@Service
public class UserService {
    private final UserRepository userRepo;

    public UserService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    /**
     * Get the currently logged-in user (if any).
     * @return 
     */
    public User getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUserName = authentication.getName();
            return userRepo.findByUsernameIgnoreCase(currentUserName).get(0);
        }
        return null;
    }
}
