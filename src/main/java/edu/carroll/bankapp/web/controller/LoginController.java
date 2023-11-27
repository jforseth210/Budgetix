package edu.carroll.bankapp.web.controller;

import edu.carroll.bankapp.FlashHelper;
import edu.carroll.bankapp.jpa.model.SiteUser;
import edu.carroll.bankapp.service.ServiceResponse;
import edu.carroll.bankapp.service.UserService;
import edu.carroll.bankapp.web.AuthHelper;
import edu.carroll.bankapp.web.form.LoginForm;
import edu.carroll.bankapp.web.form.NewLoginForm;
import edu.carroll.bankapp.web.form.UpdatePasswordForm;
import edu.carroll.bankapp.web.form.UpdateUsernameForm;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import java.security.Provider.Service;

import javax.naming.Binding;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * This controller is responsible for all authentication routes. Logging in/out,
 * signing up, etc.
 */
@Controller
public class LoginController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    private final UserService userService;
    private final AuthHelper authHelper;

    /**
     * Constructor with dependency injection
     * 
     * @param userService - For creating, retrieving, and updating users
     * @param authHelper  - For determining the current user
     */
    public LoginController(UserService userService, AuthHelper authHelper) {
        this.userService = userService;
        this.authHelper = authHelper;

    }

    /**
     * The login page
     * 
     * @param model to pass the form to thymeleaf
     * @return the login page
     */
    @GetMapping("/login")
    public String loginGet(Model model) {
        model.addAttribute("loginForm", new LoginForm());
        return "loginExisting";
    }

    /**
     * The sign-up pages
     * 
     * @param model to pass the form to thymeleaf
     * @return sign up page
     */
    @GetMapping("/loginNew")
    public String loginNewGet(Model model) {
        model.addAttribute("newLoginForm", new NewLoginForm());
        return "loginNew";
    }

    /**
     * This page accepts form submissions for (user) account creation
     *
     * @param newLoginForm       The data collected from the form
     * @param result             Form errors (if any)
     * @param request            To log the user in with their new account
     * @param redirectAttributes - for flashing messages
     * @return String redirect view - redirect leads user to new page based on
     *         submission
     */
    @PostMapping("/loginNew")
    public String loginNewPost(HttpServletRequest request, @Valid @ModelAttribute NewLoginForm newLoginForm,
            BindingResult validation, RedirectAttributes redirectAttributes) {
        if (validation.hasErrors()) {
            return "loginNew";
        }

        // Make sure password and confirm password match
        if (!newLoginForm.getPassword().equals(newLoginForm.getConfirm())) {
            log.info("A user, {}, attempted to make an account with non-matching passwords",
                    newLoginForm.getUsername());
            validation.addError(new ObjectError("confirm", "Passwords must match"));
            return "loginNew";
        }

        // See if username is available
        if (userService.getUserByUsername(newLoginForm.getUsername()) != null) {
            validation.addError(new ObjectError("username", "Username already taken"));
            return "loginNew";
        }

        // Create the user
        ServiceResponse<SiteUser> response = userService.createUser(newLoginForm.getFullName(), newLoginForm.getEmail(),
                newLoginForm.getUsername(), newLoginForm.getPassword());

        if (response.getResult() == null) {
            validation.addError(new ObjectError("global", response.getMessage()));
            return "loginNew";
        }
        log.info("Created a new user: {}", response.getResult().getUsername());

        // Attempt to log the user into the application
        try {
            request.login(newLoginForm.getUsername(), newLoginForm.getPassword());
        } catch (ServletException e) {
            log.error("Error logging {} in after signup:", newLoginForm.getUsername(), e);
        }

        log.info("New user {} created, redirecting to \"/\"", newLoginForm.getUsername());
        return "redirect:/";
    }

    /**
     * Updates the password for a user
     *
     * @param form               - update password form
     * @param redirectAttributes - for flashing messages
     * @return - redirect to the homepage
     */
    @PostMapping("/update-password")
    public String updatePassword(@ModelAttribute("updatePassword") UpdatePasswordForm form, BindingResult validation,
            RedirectAttributes redirectAttributes) {
        if (validation.hasErrors()) {
            for (ObjectError error : validation.getAllErrors()) {
                FlashHelper.flash(redirectAttributes, error.getDefaultMessage());
            }
            return "redirect:/";
        }
        SiteUser user = authHelper.getLoggedInUser();

        // Handle the case where the user doesn't exist
        if (user == null) {
            FlashHelper.flash(redirectAttributes, "Something went wrong. Your password has not been changed");
            return "redirect:/";
        }

        // Make sure the user entered the correct old password
        if (!form.getNewPassword().equals(form.getNewConfirm())) {
            FlashHelper.flash(redirectAttributes, "Passwords do not match. Please try again.");
            log.info("The new passwords do not match for {}", user.getUsername());
            return "redirect:/";
        }

        // Update the user's password
        ServiceResponse<Boolean> response = userService.updatePassword(user, form.getOldPassword(),
                form.getNewPassword());

        FlashHelper.flash(redirectAttributes, response.getMessage());
        return "redirect:/";
    }

    /**
     * Changes the username for the user
     *
     * @param form               - update username form
     * @param redirectAttributes - for flashing messages
     * @param request            - to log the user out and in again
     * @return redirect to the home page
     */
    @PostMapping("/update-username")
    public String updateUsername(@ModelAttribute("updateUsername") UpdateUsernameForm form, BindingResult validation,
            HttpServletRequest request, RedirectAttributes redirectAttributes) {
        if (validation.hasErrors()) {
            for (ObjectError error : validation.getAllErrors()) {
                FlashHelper.flash(redirectAttributes, error.getDefaultMessage());
            }
            return "redirect:/";
        }
        SiteUser user = authHelper.getLoggedInUser();
        // Try updating the username
        ServiceResponse<Boolean> response = userService.updateUsername(user, form.getConfirmPassword(),
                form.getNewUsername());
        if (!response.getResult()) {
            FlashHelper.flash(redirectAttributes, response.getMessage());
            return "redirect:/";
        }

        // Try to log out and back in again so Spring Security has the correct username
        try {
            request.logout();
            request.login(form.getNewUsername(), form.getConfirmPassword());
            // Let the user know everything worked
            FlashHelper.flash(redirectAttributes,
                    String.format("Your username has been updated to %s", form.getNewUsername()));
        } catch (ServletException e) {
            // Something went wrong, log it and let the user know.
            log.error("Error logging {} in after signup:", form.getNewUsername(), e);
            FlashHelper.flash(redirectAttributes, "Something went wrong. Please log in again");
        }
        return "redirect:/";
    }
}
