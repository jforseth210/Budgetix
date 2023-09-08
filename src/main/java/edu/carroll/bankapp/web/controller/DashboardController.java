package edu.carroll.bankapp.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class DashboardController {
    private String[] accounts = new String[]{"Checking", "Savings", "Credit", "Cash"};

    @GetMapping("/")
    public RedirectView index() {
        return new RedirectView("/" + accounts[0]);
    }

    @GetMapping("/{account}")
    public String index(@PathVariable String account, Model model) {
        System.out.println(account);
        model.addAttribute("accounts", accounts);
        model.addAttribute("currentAccount", account);
        return "index";
    }
}