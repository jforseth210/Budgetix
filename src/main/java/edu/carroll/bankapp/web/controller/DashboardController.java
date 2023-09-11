package edu.carroll.bankapp.web.controller;

import edu.carroll.bankapp.User;
import edu.carroll.bankapp.service.LoginServiceImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.view.RedirectView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Controller
public class DashboardController {
    LoginServiceImpl loginService = new LoginServiceImpl();
    private static final Logger log = LoggerFactory.getLogger(DashboardController.class);

    private String[] accounts = new String[]{"Checking", "Savings", "Credit", "Cash"};

    @GetMapping("/")
    public RedirectView index(@CookieValue(name = "session", defaultValue = "") String session) {
        User loggedInUser = loginService.getUserFromToken(session);
        if (loggedInUser == null) {
            return new RedirectView("/login");
        }
        log.debug("Request for \"/\", redirecting to \"/{}\"", accounts[0]);
        return new RedirectView("/account/" + accounts[0]);
    }

    @GetMapping("/account/{account}")
    public String index(@CookieValue(name = "session", defaultValue = "") String session, @PathVariable String account, Model model) {
        User loggedInUser = loginService.getUserFromToken(session);
        if (loggedInUser == null) {
            return "redirect:/login";
        }
        log.debug("Request for account: {}", account);
        model.addAttribute("accounts", accounts);
        model.addAttribute("currentAccount", account);
        return "index";
    }
}