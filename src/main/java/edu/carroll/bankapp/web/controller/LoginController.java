package edu.carroll.bankapp.web.controller;

import edu.carroll.bankapp.jpa.model.User;
import edu.carroll.bankapp.jpa.repo.UserRepository;
import edu.carroll.bankapp.web.form.LoginForm;
import edu.carroll.bankapp.web.form.NewLoginForm;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import java.util.ArrayList;
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

//    @PostMapping("/login")
//    public String loginPost(@Valid @ModelAttribute LoginForm loginForm, BindingResult result) {
//        if (result.hasErrors()) {
//            log.info("Log form has errors, redirecting back to login page");
//            return "loginNew";
//        }
//
//        if (!userRepo.equals(loginForm.getUsername())) {
//            result.addError(new ObjectError("global", "User not found"));
//            return "loginNew";
//        }
//
//        return "redirect:/";
//    }

    @GetMapping("/loginNew")
    public String loginNewGet(Model model) {
         model.addAttribute("newLoginForm", new NewLoginForm());
        return "loginNew";
    }

    @PostMapping("/loginNew")
    public String loginNewPost(@Valid @ModelAttribute NewLoginForm newLoginForm, BindingResult result) {
        if (result.hasErrors()) {
            log.info("Log form has errors, redirecting back to login page");
            return "loginNew";
        }

        if (!newLoginForm.getPassword().equals(newLoginForm.getConfirm())) {
            log.info("Passwords must match");
            result.addError(new ObjectError("confirm", "Passwords must match"));
            return "loginNew";
        }

        User defaultUser = new User(newLoginForm.getFullName(), newLoginForm.getEmail(),
                newLoginForm.getUsername(), BCrypt.hashpw(newLoginForm.getPassword(), BCrypt.gensalt()));
        userRepo.save(defaultUser);

        log.info("Redirecting to \"/\"");
        return "redirect:/";
    }
}
