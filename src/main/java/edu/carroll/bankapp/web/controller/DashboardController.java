package edu.carroll.bankapp.web.controller;

import edu.carroll.bankapp.jpa.model.Account;
import edu.carroll.bankapp.jpa.model.User;
import edu.carroll.bankapp.jpa.repo.AccountRepository;
import edu.carroll.bankapp.service.AccountService;
import edu.carroll.bankapp.service.UserService;

import edu.carroll.bankapp.web.form.LoginForm;
import edu.carroll.bankapp.web.form.NewAccountForm;
import edu.carroll.bankapp.web.form.NewLoginForm;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Principal;
import java.util.List;

@Controller
public class DashboardController {
    private AccountService accountService;
    private AccountRepository accountRepo;
    private UserService userService;

    private static final Logger log = LoggerFactory.getLogger(DashboardController.class);

    public DashboardController(AccountService accountService, AccountRepository accountRepo, UserService userService) {
        this.accountService = accountService;
        this.accountRepo = accountRepo;
        this.userService = userService;
    }

    @GetMapping("/")
    public RedirectView index() {
        List<Account> accounts = accountService.getUserAccounts();
        if (accounts == null || accounts.size() == 0) {
            return new RedirectView("/account/0");
        }
        log.debug("Request for \"/\", redirecting to \"/{}\"", accounts.get(0).getId());
        return new RedirectView("/account/" + accounts.get(0).getId());
    }

    @GetMapping("/account/{accountId}")
    public String index(@PathVariable Integer accountId, Principal principal, Model model) {
        List<Account> accounts = accountService.getUserAccounts();
        final Account account = accountService.getUserAccount(accountId);
        log.debug("Request for account: {}", account.getName());
        model.addAttribute("accounts", accounts);
        model.addAttribute("currentUser", userService.getLoggedInUser());
        model.addAttribute("currentAccount", account);
        model.addAttribute("newAccountForm", new NewAccountForm());

        return "index";
    }

    @PostMapping("/add-account")
    public RedirectView addAccount(@Valid @ModelAttribute NewAccountForm newAccountForm) {
        Account account = new Account();
        account.setName(newAccountForm.getAccountName());
        account.setBalanceInCents(newAccountForm.getAccountBalance().intValue() * 100);
        account.setOwner(userService.getLoggedInUser());
        accountRepo.save(account);
        return new RedirectView("/");
    }
}