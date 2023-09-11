package edu.carroll.bankapp.web.controller;

import edu.carroll.bankapp.service.LoginService;
import edu.carroll.bankapp.web.form.LoginForm;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class LoginController {
    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @GetMapping("/login")
    public String loginGet(Model model) {
        model.addAttribute("loginForm", new LoginForm());
        return "loginExisting";
    }

    @GetMapping("/loginNew")
    public String loginNewGet(Model model) {
        //model.addAttribute("loginForm", new LoginForm());
        return "loginNew";
    }

    @PostMapping("/login")
    public RedirectView loginPost(@Valid @ModelAttribute LoginForm loginForm, BindingResult result, RedirectAttributes attrs) {
        System.out.println(loginForm.getUsername());
        System.out.println(loginForm.getPassword());
        if (result.hasErrors()) {
            return new RedirectView("/login");
        }
        if (!loginService.validateUser(loginForm.getUsername(), loginForm.getPassword())) {
            result.addError(new ObjectError("globalError", "Username and password do not match known users"));
            return new RedirectView("/login");
        }
        attrs.addAttribute("username", loginForm.getUsername());
        return new RedirectView("/");
    }
}
