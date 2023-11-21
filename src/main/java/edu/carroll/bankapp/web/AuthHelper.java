package edu.carroll.bankapp.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import edu.carroll.bankapp.jpa.model.SiteUser;
import edu.carroll.bankapp.service.UserService;

/**
 * A class for determining who the currently logged-in user is
 */
@Component
public class AuthHelper {
    private static final Logger log = LoggerFactory.getLogger(AuthHelper.class);
    private final UserService userService;

    /**
     * Inject needed dependencies
     * 
     * @param userService - For looking up persisted users
     */
    public AuthHelper(UserService userService) {
        this.userService = userService;
    }

    /**
     * Get the currently logged-in user (if any).
     *
     * @return user
     */
    public SiteUser getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // See if there's no one logged in
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            log.info("No one currently logged in");
            return null;
        }
        // Lookup the current user
        String currentUserName = authentication.getName();
        return userService.getUserByUsername(currentUserName);
    }
}
