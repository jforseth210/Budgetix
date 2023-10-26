package edu.carroll.bankapp.web.controller;

import edu.carroll.bankapp.jpa.model.Account;
import edu.carroll.bankapp.jpa.model.SiteUser;
import edu.carroll.bankapp.jpa.model.Transaction;
import edu.carroll.bankapp.service.AccountService;
import edu.carroll.bankapp.service.TransactionService;
import edu.carroll.bankapp.service.UserService;
import edu.carroll.bankapp.web.AuthHelper;
import edu.carroll.bankapp.web.form.*;
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
    private static final Logger log = LoggerFactory.getLogger(DashboardController.class);
    private final AccountService accountService;
    private final TransactionService transactionService;
    private final AuthHelper authHelper;
    private final UserService userService;

    public DashboardController(AccountService accountService, UserService userService,
            TransactionService transactionService, AuthHelper authHelper) {
        this.accountService = accountService;
        this.transactionService = transactionService;
        this.authHelper = authHelper;
        this.userService = userService;
    }

    /**
     * Redirect from the root path to the first user account found
     *
     * @return
     */
    @GetMapping("/")
    public RedirectView index() {
        // Get all of the user's accounts
        List<Account> accounts = accountService.getUserAccounts(authHelper.getLoggedInUser());
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
        SiteUser loggedInUser = authHelper.getLoggedInUser();
        model.addAttribute("currentUser", loggedInUser);
        model.addAttribute("newAccountForm", new NewAccountForm());
        if (accountId == 0 && accountService.getUserAccounts(loggedInUser).isEmpty()) {
            return "redirect:/add-account";
        }
        final Account account = accountService.getUserAccount(loggedInUser, accountId);
        if (account == null) {
            return "redirect:/";
        }
        List<Account> accounts = accountService.getUserAccounts(loggedInUser);
        log.debug("Request for account: {}", account.getName());
        model.addAttribute("newTransactionForm", new NewTransactionForm());
        model.addAttribute("accounts", accounts);
        model.addAttribute("currentAccount", account);
        model.addAttribute("newAccountForm", new NewAccountForm());
        model.addAttribute("deleteTransactionForm", new DeleteTransactionForm());
        model.addAttribute("deleteAccountForm", new DeleteAccountForm());
        model.addAttribute("newTransferForm", new NewTransferForm());
        model.addAttribute("updateUsernameForm", new UpdateUsernameForm());
        model.addAttribute("updatePasswordForm", new UpdatePasswordForm());
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
        // If the user already has accounts, they shouldn't be on the initial account
        // creation page
        SiteUser loggedInUser = authHelper.getLoggedInUser();
        if (!accountService.getUserAccounts(loggedInUser).isEmpty()) {
            log.info("User {} already has accounts, redirecting to \"/\"", loggedInUser.getUsername());
            return "redirect:/";
        }

        model.addAttribute("currentUser", authHelper.getLoggedInUser());
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
        accountService.createAccount(newAccountForm.getAccountName(), newAccountForm.getAccountBalance(),
                authHelper.getLoggedInUser());
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
        Account account = accountService.getUserAccount(authHelper.getLoggedInUser(),
                newTransactionForm.getAccountId());

        if (!newTransactionForm.getType().equals("expense") && !newTransactionForm.getType().equals("income")) {
            log.info("Invalid transaction type {}", newTransactionForm.getType());
            return new RedirectView("/");
        }

        if (newTransactionForm.getType().equals("expense")) {
            newTransactionForm.setAmountInDollars(-1 * newTransactionForm.getAmountInDollars());
        }

        transactionService.createTransaction(newTransactionForm.getName(), newTransactionForm.getAmountInDollars(),
                newTransactionForm.getToFrom(), account);
        return new RedirectView("/");
    }

    /**
     * Accept form submission for transfer addition
     *
     * @param newTransferForm
     * @return redirect view to page showing new transaction
     */
    @PostMapping("/add-transfer")
    public RedirectView addTransfer(@Valid @ModelAttribute NewTransferForm newTransferForm) {
        Account toAccount = accountService.getUserAccount(authHelper.getLoggedInUser(),
                newTransferForm.getToAccountId());
        Account fromAccount = accountService.getUserAccount(authHelper.getLoggedInUser(),
                newTransferForm.getFromAccountId());

        transactionService.createTransaction(String.format("Transfer to %s", toAccount.getName()),
                -1 * newTransferForm.getTransferAmountInDollars(), toAccount.getName(), fromAccount);

        transactionService.createTransaction(String.format("Transfer from %s", fromAccount.getName()),
                newTransferForm.getTransferAmountInDollars(), fromAccount.getName(), toAccount);
        return new RedirectView("/");
    }

    @PostMapping("/delete-transaction")
    public String deleteTransaction(@ModelAttribute("deleteTransactionForm") DeleteTransactionForm form) {
        SiteUser loggedInUser = authHelper.getLoggedInUser();
        Transaction transaction = transactionService.getUserTransaction(loggedInUser, form.getTransactionId());
        transactionService.deleteTransaction(loggedInUser, transaction);
        return "redirect:/account/" + transaction.getAccount().getId().toString();
    }

    @PostMapping("/delete-account")
    public String deleteAccount(@ModelAttribute("deleteAccountForm") DeleteAccountForm form) {
        accountService.deleteAccount(authHelper.getLoggedInUser(), form.getAccountId());
        // Redirect or return the appropriate view
        return "redirect:/";
    }

    @PostMapping("/update-password")
    public String updatePassword(@ModelAttribute("updatePassword") UpdatePasswordForm form) {
        SiteUser user = authHelper.getLoggedInUser();

        if (user == null) {
            // Handle the case where the user doesn't exist
            return "redirect:/";
        }

        // Verify the old password
//        if (!form.isPasswordValid(user, form.getOldPassword())) {
//            // Handle the case where the old password is incorrect
//            return "redirect:/";
//        }
//
//        // Validate the new password
//        if (!form.isPasswordValid(user, form.getNewPassword())) {
//            // Handle invalid new password
//            return "redirect:/";
//        }

        // Update the user's password
        form.setNewPassword(form.getNewPassword());
        return "redirect:/";
    }

    @PostMapping("/update-username")
    public String updateUsername(@ModelAttribute("updateUsername") UpdateUsernameForm form) {
        SiteUser user = authHelper.getLoggedInUser();
        userService.updateUsername(user, form.getOldUsername(), form.getNewUsername());
        return "redirect:/";
    }
}
