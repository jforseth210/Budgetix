package edu.carroll.bankapp.web.controller;

import edu.carroll.bankapp.jpa.model.Account;
import edu.carroll.bankapp.service.AccountService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.view.RedirectView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Principal;
import java.util.List;

@Controller
public class DashboardController {
    private AccountService accountService;
    private static final Logger log = LoggerFactory.getLogger(DashboardController.class);

    public DashboardController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/")
    public RedirectView index() {
        List<Account> accounts = accountService.getUserAccounts();
        if (accounts == null || accounts.size() == 0) {
            return new RedirectView("/login");
        }
        log.debug("Request for \"/\", redirecting to \"/{}\"", accounts.get(0).getId());
        return new RedirectView("/account/" + accounts.get(0).getId());
    }

    @GetMapping("/account/{accountId}")
    public String index(@PathVariable Integer accountId, Principal principal, Model model) {
        List<Account> accounts = accountService.getUserAccounts();
        if (accounts == null || accounts.size() == 0) {
            return "redirect:/login";
        }
        final Account account = accountService.getUserAccount(accountId);

        log.debug("Request for account: {}", account.getName());
        model.addAttribute("accounts", accounts);
        model.addAttribute("currentAccount", account);
        return "index";
    }
}