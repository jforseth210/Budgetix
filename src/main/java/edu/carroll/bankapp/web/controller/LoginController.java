package edu.carroll.bankapp.web.controller;

import edu.carroll.bankapp.web.form.LoginForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String loginGet(Model model) {
        model.addAttribute("loginForm", new LoginForm());
        return "loginExisting";
    }

    @GetMapping("/loginNew")
    public String loginNewGet(Model model) {
        return "loginNew";
    }
}
