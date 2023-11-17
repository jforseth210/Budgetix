package edu.carroll.bankapp.web.controller;

import edu.carroll.bankapp.jpa.model.Account;
import edu.carroll.bankapp.jpa.model.SiteUser;
import edu.carroll.bankapp.jpa.model.Transaction;
import edu.carroll.bankapp.service.AccountService;
import edu.carroll.bankapp.service.TransactionService;
import edu.carroll.bankapp.web.AuthHelper;
import edu.carroll.bankapp.web.form.*;
import jakarta.validation.Valid;

import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import edu.carroll.bankapp.FlashHelper;

import java.util.List;

/**
 * This controller is responsible for the primary account management routes.
 * Account and transaction creation, reading, modification, deletion.
 */
@Controller
@EnableWebSecurity
public class DashboardController {
    private static final String INCOME = "income";
    private static final String EXPENSE = "expense";

    private static final Logger log = LoggerFactory.getLogger(DashboardController.class);
    private final AccountService accountService;
    private final TransactionService transactionService;
    private final AuthHelper authHelper;

    /**
     * Inject needed services
     */
    public DashboardController(AccountService accountService,
                               TransactionService transactionService, AuthHelper authHelper) {
        this.accountService = accountService;
        this.transactionService = transactionService;
        this.authHelper = authHelper;
    }

    /**
     * Redirect from the root path to the first user account found
     *
     * @return - redirect to the appropriate page
     */
    @GetMapping("/")
    public RedirectView index(Model model, RedirectAttributes redirectAttributes) {
        // Get all the user's accounts
        List<Account> accounts = accountService.getUserAccounts(authHelper.getLoggedInUser());

        // Check if the 'messages' attribute exists in the model and pass it to the
        // redirect
        if (model.containsAttribute("messages")) {
            redirectAttributes.addFlashAttribute("messages", model.getAttribute("messages"));
        }

        // Deal with the user not having any accounts
        if (accounts == null || accounts.isEmpty()) {
            return new RedirectView("/add-account");
        }

        // Redirect to the first account found
        log.debug("Request for \"/\", redirecting to \"/{}\"", accounts.get(0).getId());
        return new RedirectView("/account/" + accounts.get(0).getId());
    }

    /**
     * Page for viewing an account
     *
     * @param accountId the id of the account being viewed
     * @param model     data to pass to Thymeleaf
     * @return - account page or redirect
     */
    @GetMapping("/account/{accountId}")
    public String index(@PathVariable Integer accountId, Model model, RedirectAttributes redirectAttributes) {
        SiteUser loggedInUser = authHelper.getLoggedInUser();
        // Get the current user's accounts
        List<Account> accounts = accountService.getUserAccounts(loggedInUser);

        // The user doesn't have any accounts, go create one
        if (accounts.isEmpty()) {
            FlashHelper.flash(redirectAttributes, "Please create an account");
            return "redirect:/add-account";
        }
        // The user tried to go to an account that doesn't exist, go away
        final Account account = accountService.getUserAccount(loggedInUser, accountId);
        if (account == null) {
            FlashHelper.flash(redirectAttributes, "Account does not exist");
            return "redirect:/";
        }
        log.debug("Request for account: {}", account.getName());

        // Allow Thymeleaf to display current user
        model.addAttribute("currentUser", loggedInUser);
        // Give Thymeleaf a list of accounts to show in the navbard
        model.addAttribute("accounts", accounts);
        // Give Thymeleaf the account being displayed
        model.addAttribute("currentAccount", account);

        // Pass the necessary forms for various user operations to Thymeleaf
        model.addAttribute("newAccountForm", new NewAccountForm());
        model.addAttribute("newTransactionForm", new NewTransactionForm());
        model.addAttribute("newTransferForm", new NewTransferForm());
        model.addAttribute("deleteTransactionForm", new DeleteTransactionForm());
        model.addAttribute("deleteAccountForm", new DeleteAccountForm());
        model.addAttribute("updateUsernameForm", new UpdateUsernameForm());
        model.addAttribute("updatePasswordForm", new UpdatePasswordForm());

        return "index";
    }

    /**
     * Page for initially adding an account
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

        // Pass data to Thymeleaf
        model.addAttribute("currentUser", authHelper.getLoggedInUser());
        model.addAttribute("newAccountForm", new NewAccountForm());
        return "addAccountPage";
    }

    /**
     * Accept form submissions for (financial) account creation
     *
     * @param newAccountForm form information needed to create the account
     * @return redirect to root path
     */
    @PostMapping("/add-account")
    public RedirectView addAccount(@Valid @ModelAttribute NewAccountForm newAccountForm,
                                   RedirectAttributes redirectAttributes) {
        // Create an account
        accountService.createAccount(
                newAccountForm.getAccountName(),
                newAccountForm.getAccountBalance(),
                authHelper.getLoggedInUser());
        // Let the user know the operation completed
        FlashHelper.flash(redirectAttributes, String.format("Account %s created", newAccountForm.getAccountName()));
        // Redirect back to the root path
        return new RedirectView("/");
    }

    /**
     * Accept form submission for transaction addition
     *
     * @param newTransactionForm form information needed to create the transaction
     * @return redirect view to page showing new transaction
     */
    @PostMapping("/add-transaction")
    public RedirectView addTransaction(@Valid @ModelAttribute NewTransactionForm newTransactionForm,
                                       RedirectAttributes redirectAttributes) {
        Account account = accountService.getUserAccount(authHelper.getLoggedInUser(),
                newTransactionForm.getAccountId());

        // Is transaction type valid?
        if (!newTransactionForm.getType().equals(EXPENSE) && !newTransactionForm.getType().equals(INCOME)) {
            // Transaction is an invalid type
            log.info("Invalid transaction type {}", newTransactionForm.getType());
            FlashHelper.flash(redirectAttributes,
                    String.format("Invalid transation type: %s", newTransactionForm.getType()));
            return new RedirectView("/");
        }

        // Only allow user to submit positives amounts in income/expenses
        if (newTransactionForm.getAmountInDollars() < 0) {
            log.info("{} attempted to create a transaction with a negative amount. Making positive.",
                    authHelper.getLoggedInUser());
            newTransactionForm.setAmountInDollars(Math.abs(newTransactionForm.getAmountInDollars()));
        }

        // Express expenses as a negative amount
        if (newTransactionForm.getType().equals(EXPENSE)) {
            newTransactionForm.setAmountInDollars(-1 * newTransactionForm.getAmountInDollars());
        }

        // Create the transaction
        transactionService.createTransaction(
                newTransactionForm.getName(),
                newTransactionForm.getAmountInDollars(),
                newTransactionForm.getToFrom(),
                account);
        FlashHelper.flash(redirectAttributes, String.format("Transaction %s created", newTransactionForm.getName()));
        return new RedirectView("/");
    }

    /**
     * Accept form submission for transfer addition
     *
     * @param newTransferForm form information needed to create transfer
     * @return redirect view to page showing new transaction
     */
    @PostMapping("/add-transfer")
    public RedirectView addTransfer(@Valid @ModelAttribute NewTransferForm newTransferForm,
                                    RedirectAttributes redirectAttributes) {
        // The account to send money to
        Account toAccount = accountService.getUserAccount(authHelper.getLoggedInUser(),
                newTransferForm.getToAccountId());

        // The account to take money from
        Account fromAccount = accountService.getUserAccount(authHelper.getLoggedInUser(),
                newTransferForm.getFromAccountId());

        // Transfer the money
        boolean success = transactionService.createTransfer(toAccount, fromAccount,
                newTransferForm.getTransferAmountInDollars());

        // Give the user feedback
        if (success) {
            FlashHelper.flash(redirectAttributes,
                    String.format("Created transfer from %s to %s", fromAccount.getName(), toAccount.getName()));
        } else {
            FlashHelper.flash(redirectAttributes, "Something went wrong, the money was not transfered");
        }

        return new RedirectView("/");
    }

    /**
     * Delete a transaction from the transaction database
     *
     * @param form - a delete transaction form
     * @return a redirect to the home page
     */
    @PostMapping("/delete-transaction")
    public String deleteTransaction(@ModelAttribute("deleteTransactionForm") DeleteTransactionForm form,
                                    RedirectAttributes redirectAttributes) {
        SiteUser loggedInUser = authHelper.getLoggedInUser();
        // Look up the transaction to delete
        Transaction transaction = transactionService.getUserTransaction(loggedInUser, form.getTransactionId());

        // Delete it
        transactionService.deleteTransaction(loggedInUser, transaction);

        // Let the user know
        FlashHelper.flash(redirectAttributes, String.format("Deleted transaction: %s", transaction.getName()));
        return "redirect:/account/" + transaction.getAccount().getId().toString();
    }

    /**
     * Delete an account (i.e., savings or checking) from a user's list of accounts
     *
     * @param form - delete account form
     * @return redirect to the home page
     */
    @PostMapping("/delete-account")
    public String deleteAccount(@ModelAttribute("deleteAccountForm") DeleteAccountForm form,
                                RedirectAttributes redirectAttributes) {
        // Look up the account the user wants to delete
        Account account = accountService.getUserAccount(authHelper.getLoggedInUser(), form.getAccountId());

        // Delete it
        accountService.deleteAccount(authHelper.getLoggedInUser(), account);

        // Let the user know
        FlashHelper.flash(redirectAttributes, String.format("Deleted account: %s", account.getName()));
        // Redirect or return the appropriate view
        return "redirect:/";
    }
}
