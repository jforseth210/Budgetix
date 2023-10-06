package edu.carroll.bankapp.web.controller;

import edu.carroll.bankapp.jpa.model.Account;
import edu.carroll.bankapp.jpa.model.Transaction;
import edu.carroll.bankapp.service.AccountService;
import edu.carroll.bankapp.service.TransactionService;
import edu.carroll.bankapp.service.UserService;
import edu.carroll.bankapp.web.form.DeleteAccountForm;
import edu.carroll.bankapp.web.form.DeleteTransactionForm;
import edu.carroll.bankapp.web.form.NewAccountForm;
import edu.carroll.bankapp.web.form.NewTransactionForm;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.view.RedirectView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * This controller is responsible for the primary account management routes.
 * Account and transaction creation, reading, modification, deletion.
 */
@Controller
public class DashboardController {
    private final AccountService accountService;
    private final UserService userService;
    private final TransactionService transactionService;

    private static final Logger log = LoggerFactory.getLogger(DashboardController.class);

    /**
     * Default Constructor - helps to set up our database
     *
     * @param accountService
     * @param accountRepo
     * @param userService
     * @param transRepo
     */
    public DashboardController(AccountService accountService, UserService userService,
            TransactionService transactionService) {
        this.accountService = accountService;
        this.userService = userService;
        this.transactionService = transactionService;
    }

    /**
     * Redirect from the root path to the first user account found
     *
     * @return
     */
    @GetMapping("/")
    public RedirectView index() {
        // Get all of the user's accounts
        List<Account> accounts = accountService.getUserAccounts();
        // Deal with the user not having any accounts
        if (accounts == null || accounts.size() == 0) {
            return new RedirectView("/add-account");
        }
        log.debug("Request for \"/\", redirecting to \"/{}\"", accounts.get(0).getId());
        // Redirect to the first account found
        return new RedirectView("/account/" + accounts.get(0).getId());
    }

    /**
     * Page for viewing an account
     *
     * @param accountId the id of the account being viewed
     * @param model     data to pass to Thymeleaf
     * @return
     */
    @GetMapping("/account/{accountId}")
    public String index(@PathVariable Integer accountId, Model model) {
        model.addAttribute("currentUser", userService.getLoggedInUser());
        model.addAttribute("newAccountForm", new NewAccountForm());
        if (accountId == 0 && accountService.getUserAccounts().isEmpty()) {
            return "redirect:/add-account";
        }
        final Account account = accountService.getUserAccount(accountId);
        if (account == null) {
            return "redirect:/";
        }
        List<Account> accounts = accountService.getUserAccounts();
        log.debug("Request for account: {}", account.getName());
        model.addAttribute("newTransactionForm", new NewTransactionForm());
        model.addAttribute("accounts", accounts);
        model.addAttribute("currentAccount", account);
        model.addAttribute("newAccountForm", new NewAccountForm());
        model.addAttribute("deleteTransactionForm", new DeleteTransactionForm());
        model.addAttribute("deleteAccountForm", new DeleteAccountForm());
        return "index";
    }

    /**
     * Page for adding an account
     *
     * @param model data to pass to Thymeleaf
     * @return redirect view
     */
    @GetMapping("/add-account")
    public String addAccountPage(Model model) {
        model.addAttribute("currentUser", userService.getLoggedInUser());
        model.addAttribute("newAccountForm", new NewAccountForm());
        return "addAccountPage";
    }

    /**
     * Accept form submissions for (financial) account creation
     *
     * @param newAccountForm
     * @return
     */
    @PostMapping("/add-account")
    public RedirectView addAccount(@Valid @ModelAttribute NewAccountForm newAccountForm) {
        accountService.createAccount(newAccountForm);
        // Redirect back to the root path
        return new RedirectView("/");
    }

    /**
     * Accept form submission for transaction addition
     *
     * @param newTransactionForm
     * @return redirect view to page showing new transaction
     */
    @PostMapping("/add-transaction")
    public RedirectView addTransaction(@Valid @ModelAttribute NewTransactionForm newTransactionForm) {
        transactionService.createTransaction(newTransactionForm);
        return new RedirectView("/");
    }

    @PostMapping("/delete-transaction")
    public String deleteTransaction(@ModelAttribute("deleteTransactionForm") DeleteTransactionForm form) {
        Transaction transaction = transactionService.getUserTransaction(form.getTransactionId());
        transactionService.deleteTransaction(transaction);
        return "redirect:/account/" + transaction.getAccount().getId().toString();
    }

    @PostMapping("/delete-account")
    public String deleteAccount(@ModelAttribute("deleteAccountForm") DeleteAccountForm form) {
        accountService.deleteAccount(form.getAccountId());
        // Redirect or return the appropriate view
        return "redirect:/";
    }

}
