package edu.carroll.bankapp.web.controller;

import edu.carroll.bankapp.jpa.model.Account;
import edu.carroll.bankapp.jpa.model.Transaction;
import edu.carroll.bankapp.jpa.repo.AccountRepository;
import edu.carroll.bankapp.jpa.repo.TransactionRepository;
import edu.carroll.bankapp.service.AccountService;
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
    private AccountService accountService;
    private AccountRepository accountRepo;
    private UserService userService;
    private TransactionRepository transactionRepository;

    private static final Logger log = LoggerFactory.getLogger(DashboardController.class);

    public DashboardController(AccountService accountService, AccountRepository accountRepo, UserService userService,
            TransactionRepository transRepo) {
        this.accountService = accountService;
        this.accountRepo = accountRepo;
        this.userService = userService;
        this.transactionRepository = transRepo;
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
        if (accountId == 0 && accountService.getUserAccounts().size() > 0) {
            return "redirect:/";
        }
        if (accountId == 0) {
            return "redirect:/add-account";
        }
        List<Account> accounts = accountService.getUserAccounts();
        final Account account = accountService.getUserAccount(accountId);
        log.debug("Request for account: {}", account.getName());
        model.addAttribute("newTransactionForm", new NewTransactionForm());
        model.addAttribute("accounts", accounts);
        model.addAttribute("currentAccount", account);
        model.addAttribute("newAccountForm", new NewAccountForm());
        model.addAttribute("deleteTransactionForm", new DeleteTransactionForm());
        model.addAttribute("deleteAccountForm", new DeleteAccountForm());
        return "index";
    }

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
        // Create and save a new account
        Account account = new Account();
        account.setName(newAccountForm.getAccountName());
        account.setBalanceInCents(newAccountForm.getAccountBalance().intValue() * 100);
        account.setOwner(userService.getLoggedInUser());
        accountRepo.save(account);
        // Redirect back to the root path
        return new RedirectView("/");
    }

    @PostMapping("/add-transaction")
    public RedirectView addTransaction(@Valid @ModelAttribute NewTransactionForm newTransaction) {
        Transaction trans = new Transaction();
        trans.setName(newTransaction.getName());
        trans.setAmountInCents(newTransaction.getAmountInCents().intValue() * 100);
        trans.setToFrom(newTransaction.getToFrom());
        Account transactionAccount = accountRepo.findById(newTransaction.getAccountId()).get();
        trans.setAccount(transactionAccount);
        transactionRepository.save(trans);
        return new RedirectView("/");
    }

    @PostMapping("/delete-transaction")
    public String deleteTransaction(@ModelAttribute("deleteTransactionForm") DeleteTransactionForm form) {
        // Extract the transaction ID from the form
        int transactionId = form.getTransactionId();

        // Use Spring Data JPA to find and delete the transaction by ID
        Transaction transaction = transactionRepository.findById(transactionId).get(0);
        Integer accountId = transaction.getAccount().getId();
        transactionRepository.delete(transaction);
        // Redirect or return the appropriate view
        return "redirect:/account/" + accountId.toString();
    }

    @PostMapping("/delete-account")
    public String deleteAccount(@ModelAttribute("deleteAccountForm") DeleteAccountForm form) {
        // Extract the account ID from the form
        int accountId = form.getAccountId();

        // Use Spring Data JPA to find and delete the transaction by ID
        Account account = accountRepo.findById(accountId).get(0);
        accountRepo.delete(account);
        // Redirect or return the appropriate view
        return "redirect:/";
    }
}
