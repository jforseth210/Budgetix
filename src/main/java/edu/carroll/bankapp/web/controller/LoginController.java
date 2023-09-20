package edu.carroll.bankapp.web.controller;

import edu.carroll.bankapp.jpa.model.User;
import edu.carroll.bankapp.jpa.repo.UserRepository;
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

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    private final UserRepository userRepo;

    public LoginController(UserRepository userRepo) {
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

        log.info("Redirecting to \"/\"");
        return new RedirectView("/");
    }
}
