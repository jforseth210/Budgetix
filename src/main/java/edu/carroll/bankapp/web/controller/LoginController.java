package edu.carroll.bankapp.web.controller;

import edu.carroll.bankapp.FlashHelper;
import edu.carroll.bankapp.jpa.model.SiteUser;
import edu.carroll.bankapp.service.UserService;
import edu.carroll.bankapp.web.AuthHelper;
import edu.carroll.bankapp.web.form.LoginForm;
import edu.carroll.bankapp.web.form.NewLoginForm;
import edu.carroll.bankapp.web.form.UpdatePasswordForm;
import edu.carroll.bankapp.web.form.UpdateUsernameForm;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

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

    public LoginController(UserService userService, AuthHelper authHelper) {
        this.userService = userService;
        this.authHelper = authHelper;

    }

    /**
     * The login page
     */
    @GetMapping("/login")
    public String loginGet(Model model) {
        model.addAttribute("loginForm", new LoginForm());
        return "loginExisting";
    }

    /**
     * The sign-up pages
     */
    @GetMapping("/loginNew")
    public String loginNewGet(Model model) {
        model.addAttribute("newLoginForm", new NewLoginForm());
        return "loginNew";
    }

    /**
     * This page accepts form submissions for (user) account creation
     *
     * @param newLoginForm The data collected from the form
     * @param result       Form errors (if any)
     * @return String redirect view - redirect leads user to new page based on
     * submission
     */
    @PostMapping("/loginNew")
    public String loginNewPost(HttpServletRequest request, @Valid @ModelAttribute NewLoginForm newLoginForm,
                               BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "loginNew";
        }

        // Make sure password and confirm password match
        if (!newLoginForm.getPassword().equals(newLoginForm.getConfirm())) {
            log.info("A user, {}, attempted to make an account with non-matching passwords",
                    newLoginForm.getUsername());
            result.addError(new ObjectError("confirm", "Passwords must match"));
            return "loginNew";
        }

        if (userService.getUserByUsername(newLoginForm.getUsername()) != null) {
            result.addError(new ObjectError("username", "Username already taken"));
            return "loginNew";
        }

        // Create the user
        SiteUser createdUser = userService.createUser(newLoginForm.getFullName(), newLoginForm.getEmail(),
                newLoginForm.getUsername(), newLoginForm.getPassword());
        if (createdUser == null) {
            // TODO: Add "something went wrong" feedback here
            return "loginNew";
        }
        log.info("Created a new user: {}", createdUser.getUsername());

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
     * @param form - update password form
     * @return - redirect to the homepage
     */
    @PostMapping("/update-password")
    public String updatePassword(@ModelAttribute("updatePassword") UpdatePasswordForm form,
                                 RedirectAttributes redirectAttributes) {
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
        boolean success = userService.updatePassword(user, form.getOldPassword(), form.getNewPassword());
        if (success) {
            FlashHelper.flash(redirectAttributes, "Your password has been updated successfully");
        } else {
            FlashHelper.flash(redirectAttributes, "Password change unsuccessful. Please try again.");
        }
        return "redirect:/";
    }

    /**
     * Changes the username for the user
     *
     * @param form - update username form
     * @return redirect to the home page
     */
    @PostMapping("/update-username")
    public String updateUsername(@ModelAttribute("updateUsername") UpdateUsernameForm form,
                                 HttpServletRequest request, RedirectAttributes redirectAttributes) {
        SiteUser user = authHelper.getLoggedInUser();
        // Try updating the username
        boolean success = userService.updateUsername(user, form.getConfirmPassword(), form.getNewUsername());

        // Let the user know if update failed
        if (!success) {
            FlashHelper.flash(redirectAttributes, "Something went wrong. Your username has not been changed");
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
