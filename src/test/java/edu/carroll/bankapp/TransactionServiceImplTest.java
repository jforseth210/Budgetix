package edu.carroll.bankapp;

import edu.carroll.bankapp.jpa.model.Account;
import edu.carroll.bankapp.jpa.model.SiteUser;
import edu.carroll.bankapp.jpa.model.Transaction;
import edu.carroll.bankapp.service.AccountService;
import edu.carroll.bankapp.service.TransactionService;
import edu.carroll.bankapp.service.UserService;
import jakarta.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Transactional
@SpringBootTest
public class TransactionServiceImplTest {
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private UserService userService;

    @BeforeEach
    void setUp() {
        SiteUser john = userService.createUser("John Doe", "someone@example.com", "johndoe", "password");
        Account checking = accountService.createAccount("Checking", 0, john);
        Transaction transaction = transactionService.createTransaction("A transaction!", 100.0, "???",
                checking);
    }

    @Test
    public void testCreateTransaction() {
        SiteUser john = userService.getUser("johndoe");
        Account checking = accountService.getUserAccounts(john).get(0);
        Transaction createdTransaction = transactionService.createTransaction("Test Transaction", 100.0, "Receiver",
                checking);
        assert (createdTransaction != null);
        assert (createdTransaction.getName().equals("Test Transaction"));
        assert (createdTransaction.getAmountInDollars() == 100.0);
        assert (createdTransaction.getToFrom().equals("Receiver"));
        assert (createdTransaction.getAccount().equals(checking));
        int transactionId = createdTransaction.getId();

        Transaction fetchedTransaction = transactionService.getUserTransaction(john, transactionId);
        assert (fetchedTransaction != null);
        assert (fetchedTransaction.getName().equals("Test Transaction"));
        assert (fetchedTransaction.getAmountInDollars() == 100.0);
        assert (fetchedTransaction.getToFrom().equals("Receiver"));
        assert (fetchedTransaction.getAccount().equals(checking));
        assert (fetchedTransaction.getOwner().equals(john));
    }

    @Test
    public void testGetUserTransaction() {
        SiteUser john = userService.getUser("johndoe");
        Account checking = accountService.getUserAccounts(john).get(0);
        Transaction transaction = checking.getTransactions().get(0);

        Transaction fetchedTransaction = transactionService.getUserTransaction(john, transaction.getId());
        assert (fetchedTransaction != null);
        assert (fetchedTransaction.getName().equals("A transaction!"));
        assert (fetchedTransaction.getAmountInDollars() == 100);
        assert (fetchedTransaction.getToFrom().equals("???"));
        assert (fetchedTransaction.getAccount().equals(checking));
        assert (fetchedTransaction.getOwner().equals(john));
    }

    @Test
    public void testDeleteTransaction() {
        SiteUser john = userService.getUser("johndoe");
        Account checking = accountService.getUserAccounts(john).get(0);
        Transaction transaction = checking.getTransactions().get(0);

        int numAccounts = checking.getTransactions().size();
        int transactionId = transaction.getId();

        transactionService.deleteTransaction(john, transaction);

        assert transactionService.getUserTransaction(john, transactionId) == null;

        john = userService.getUser("johndoe");
        checking = accountService.getUserAccounts(john).get(0);
        assert checking.getTransactions().size() == numAccounts - 1;
    }
}