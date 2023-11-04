package edu.carroll.bankapp;

import edu.carroll.bankapp.jpa.model.Account;
import edu.carroll.bankapp.jpa.model.SiteUser;
import edu.carroll.bankapp.jpa.model.Transaction;
import edu.carroll.bankapp.service.TransactionService;
import edu.carroll.bankapp.testdata.TestAccounts;
import edu.carroll.bankapp.testdata.TestTransactions;
import edu.carroll.bankapp.testdata.TestUsers;
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
    private TestUsers testUsers;
    @Autowired
    private TestAccounts testAccounts;
    @Autowired
    private TestTransactions testTransactions;

    @Test
    public void testCreateTransaction() {
        // Create a user and account
        SiteUser john = testUsers.createJohnDoe();
        Account checking = testAccounts.createChecking(john);

        double initialCheckingBalance = checking.getBalanceInDollars();

        // Create the transaction
        Transaction createdTransaction = transactionService.createTransaction("Test Transaction", 100.0, "Receiver",
                checking);

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
        SiteUser john = testUsers.createJohnDoe();
        Account checking = testAccounts.createChecking(john);
        Transaction createdTransaction = testTransactions.createATransaction(checking);

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
        SiteUser john = testUsers.createJohnDoe();
        Account checking = testAccounts.createChecking(john);
        Transaction transaction = testTransactions.createATransaction(checking);

        // Save information about state before deletion
        int numAccounts = checking.getTransactions().size();
        int transactionId = transaction.getId();
        int transactionAmountInCents = transaction.getAmountInCents();
        int balanceBeforeDeletionInCents = checking.getBalanceInCents();
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
