package edu.carroll.bankapp.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import edu.carroll.bankapp.jpa.model.SiteUser;
import edu.carroll.bankapp.service.UserService;

@Component
public class AuthHelper {
    private static final Logger log = LoggerFactory.getLogger(AuthHelper.class);
    private UserService userService;

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
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            log.info("No one currently logged in");
            return null;
        }
        String currentUserName = authentication.getName();
        return userService.getUserByUsername(currentUserName);
    }
}
