package edu.carroll.bankapp.web.controller;

import edu.carroll.bankapp.jpa.model.User;
import edu.carroll.bankapp.jpa.repo.UserRepository;
import edu.carroll.bankapp.service.LoginService;
import edu.carroll.bankapp.web.form.LoginForm;
import edu.carroll.bankapp.web.form.NewLoginForm;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import java.util.List;

import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class LoginController {
    private final UserRepository userRepo;
    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    private final LoginService loginService;

    public LoginController(LoginService loginService, UserRepository userRepo) {
        this.loginService = loginService;
        this.userRepo = userRepo;
    }

    @GetMapping("/login")
    public String loginGet(Model model) {
        model.addAttribute("loginForm", new LoginForm());
        return "loginExisting";
    }

    @GetMapping("/loginNew")
    public String loginNewGet(Model model) {
         model.addAttribute("loginForm", new NewLoginForm());
        return "loginNew";
    }

    @GetMapping("/logout")
    public RedirectView logout(@CookieValue(name = "session", defaultValue = "") String session,
            HttpServletResponse response) {
        log.info("Logging user out");
        List<User> users = userRepo.findByToken(session);
        if (users.size() == 0) {
            log.warn("Didn't find user with matching token");
            return null;
        }
        if (users.size() > 1) {
            log.error("Got more than one user from token");
            return null;
        }
        User user = users.get(0);
        user.resetToken();
        userRepo.save(user);
        Cookie cookie = new Cookie("session", session);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return new RedirectView("/");
    }

    @PostMapping("/login")
    public RedirectView loginPost(@Valid @ModelAttribute LoginForm loginForm, BindingResult result,
            RedirectAttributes attrs, HttpServletResponse response) {
        if (result.hasErrors()) {
            log.info("Log form has errors, redirecting back to login page");
            return new RedirectView("/login");
        }
        if (!loginService.validateUser(loginForm.getUsername(), loginForm.getPassword())) {
            log.info("Invalid username or password, redirecting back to login page");
            result.addError(new ObjectError("globalError", "Username and password do not match known users"));
            return new RedirectView("/login");
        }
        log.debug("Setting session cookie");
        Cookie cookie = new Cookie("session",
                userRepo.findByUsernameIgnoreCase(loginForm.getUsername()).get(0).getToken());
        cookie.setPath("/");
        response.addCookie(cookie);
        log.info("Redirecting to \"/\"");
        return new RedirectView("/");
    }

    @PostMapping("/loginNew")
    public RedirectView loginNewPost(@Valid @ModelAttribute NewLoginForm loginFormNew, BindingResult result,
                                  RedirectAttributes attrs, HttpServletResponse response) {
        if (result.hasErrors()) {
            log.info("Log form has errors, redirecting back to login page");
            return new RedirectView("/loginNew");
        }

        if (!loginFormNew.getPassword().equals(loginFormNew.getConfirm())) {
            log.error("Passwords must match");
            return new RedirectView("/loginNew");
        }

        User defaultUser = new User(loginFormNew.getFullName(), loginFormNew.getEmail(),
                loginFormNew.getUsername(), BCrypt.hashpw(loginFormNew.getPassword(), BCrypt.gensalt()));
        userRepo.save(defaultUser);

        log.debug("Setting session cookie");
        Cookie cookie = new Cookie("session",
                userRepo.findByUsernameIgnoreCase(loginFormNew.getUsername()).get(0).getToken());
        cookie.setPath("/");
        response.addCookie(cookie);
        log.info("Redirecting to \"/\"");
        return new RedirectView("/");
    }

}
