package edu.carroll.bankapp.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.view.RedirectView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Principal;


@Controller
public class DashboardController {
    private static final Logger log = LoggerFactory.getLogger(DashboardController.class);

    private String[] accounts = new String[]{"Checking", "Savings", "Credit", "Cash"};


    @GetMapping("/")
    public RedirectView index() {
        log.debug("Request for \"/\", redirecting to \"/{}\"", accounts[0]);
        return new RedirectView("/account/" + accounts[0]);
    }

    @GetMapping("/account/{account}")
    public String index(@PathVariable String account, Principal principal, Model model) {
        System.out.println(principal.getName());
        log.debug("Request for account: {}", account);
        model.addAttribute("accounts", accounts);
        model.addAttribute("currentAccount", account);
        return "index";
    }
}