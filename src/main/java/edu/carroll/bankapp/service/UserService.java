package edu.carroll.bankapp.service;

import edu.carroll.bankapp.jpa.model.SiteUser;
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

    /**
     * Default Constructor - takes userRepo as argument
     *
     * @param userRepo - userRepository
     */
    public UserService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    /**
     * Get the currently logged-in user (if any).
     *
     * @return user
     */
    public SiteUser getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null)
            return null;
        if (authentication instanceof AnonymousAuthenticationToken) {
            return null;
        }
        String currentUserName = authentication.getName();
        return userRepo.findByUsernameIgnoreCase(currentUserName).get(0);
    }
}
