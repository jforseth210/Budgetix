package edu.carroll.bankapp;

import edu.carroll.bankapp.jpa.model.Account;
import edu.carroll.bankapp.jpa.model.SiteUser;
import edu.carroll.bankapp.jpa.model.Transaction;
import edu.carroll.bankapp.service.TransactionService;
import edu.carroll.bankapp.service.UserService;
import edu.carroll.bankapp.service.AccountService;
import jakarta.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Transactional
@SpringBootTest
public class TransactionServiceImplTest {
    @Autowired
    private TransactionService transactionService;

    @Autowired
    private UserService userService;
    @Autowired
    private AccountService accountService;

    public static final String JOHN_NAME = "John Doe";
    public static final String JOHN_EMAIL = "john@example.com";
    public static final String JOHN_USERNAME = "johndoe";
    public static final String JOHN_PASSWORD = "password123";

    public static final String JANE_NAME = "Jane Smith";
    public static final String JANE_EMAIL = "jane@example.com";
    public static final String JANE_USERNAME = "janesmith";
    public static final String JANE_PASSWORD = "letmein456";

    public static final String ALICE_NAME = "Alice Johnson";
    public static final String ALICE_EMAIL = "alice@example.com";
    public static final String ALICE_USERNAME = "alicejohnson";
    public static final String ALICE_PASSWORD = "mysecret123";

    public static final String UNICODE_NAME = "𝔘𝔫𝔦𝔠𝔬𝔡𝔢 𝔐𝔞𝔫!";
    public static final String UNICODE_EMAIL = "iliketobreakthings@email.com";
    public static final String UNICODE_USERNAME = "☕☕☕☕☕";
    public static final String UNICODE_PASSWORD = "⿈⍺✋⇏⮊⎏⇪⤸Ⲥ↴⍁➄⼉⦕ⶓ∧⻟⍀⇝⧽";

    public static final String BAD_USER_NAME = "Bob Marley";
    public static final String BAD_USER_EMAIL = "bobby";
    public static final String BAD_USER_USERNAME = "B";
    public static final String BAD_USER_PASSWORD = "p";

    @Test
    public void testCreateTransaction() {
        // Create a user and account
        SiteUser john = userService.createUser(JOHN_NAME, JOHN_EMAIL, JOHN_USERNAME, JOHN_PASSWORD).getResult();
        Account checking = accountService.createAccount("Checking", (long) 1940, john).getResult();

        long initialCheckingBalance = checking.getBalanceInDollars();

        // Create the transaction
        Transaction createdTransaction = transactionService.createTransaction("Test Transaction", (long) 100.0,
                "Receiver",
                checking).getResult();

        // Make sure returned transaction makes sense
        assertNotNull(createdTransaction);
        assertEquals(createdTransaction.getName(), "Test Transaction");
        assertEquals(createdTransaction.getAmountInDollars(), 100.0);
        assertEquals(createdTransaction.getToFrom(), "Receiver");
        assertEquals(createdTransaction.getAccount(), checking);

        // Load transaction from database
        int transactionId = createdTransaction.getId();
        Transaction fetchedTransaction = transactionService.getUserTransaction(john, transactionId);

        // Make sure fetched transaction makes sense
        assertNotNull(fetchedTransaction);
        assertEquals(fetchedTransaction.getName(), "Test Transaction");
        assertEquals(fetchedTransaction.getAmountInDollars(), 100.0);
        assertEquals(fetchedTransaction.getToFrom(), "Receiver");
        assertEquals(fetchedTransaction.getAccount(), checking);
        assertEquals(fetchedTransaction.getOwner(), john);

        assertEquals(initialCheckingBalance + 100, checking.getBalanceInDollars());
    }

    @Test
    public void testGetUserTransaction() {
        // Make a user, account, transaction
        SiteUser john = userService.createUser(JOHN_NAME, JOHN_EMAIL, JOHN_USERNAME, JOHN_PASSWORD).getResult();
        Account checking = accountService.createAccount("Checking", (long) 1940, john).getResult();
        Transaction createdTransaction = transactionService.createTransaction("A transaction!", (long) 100.0, "???",
                checking).getResult();

        // Fetch the transaction from the database
        Transaction fetchedTransaction = transactionService.getUserTransaction(john, createdTransaction.getId());

        // Make sure fetched transaction makes sense
        assertNotNull(fetchedTransaction);
        assertEquals(fetchedTransaction.getName(), createdTransaction.getName());
        assertEquals(fetchedTransaction.getAmountInDollars(), createdTransaction.getAmountInDollars());
        assertEquals(fetchedTransaction.getToFrom(), createdTransaction.getToFrom());
        assertEquals(fetchedTransaction.getAccount(), checking);
        assertEquals(fetchedTransaction.getOwner(), john);

    }

    @Test
    public void testDeleteTransaction() {
        // Populate database
        SiteUser john = userService.createUser(JOHN_NAME, JOHN_EMAIL, JOHN_USERNAME, JOHN_PASSWORD).getResult();
        Account checking = accountService.createAccount("Checking", (long) 1940, john).getResult();
        Transaction transaction = transactionService.createTransaction("A transaction!", (long) 100.0, "???",
                checking).getResult();

        // Save information about state before deletion
        int numAccounts = checking.getTransactions().size();
        int transactionId = transaction.getId();
        long transactionAmountInCents = transaction.getAmountInCents();
        long balanceBeforeDeletionInCents = checking.getBalanceInCents();
        // Delete transaction
        transactionService.deleteTransaction(john, transaction);

        // Make sure it's gone
        assertNull(transactionService.getUserTransaction(john, transactionId));

        // Make sure checking has one less transaction
        assertEquals(checking.getTransactions().size(), numAccounts - 1);
        // Make sure balance reflects deletion
        assertEquals(balanceBeforeDeletionInCents - transactionAmountInCents, checking.getBalanceInCents());
    }
}
