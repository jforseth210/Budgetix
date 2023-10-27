package edu.carroll.bankapp;

import edu.carroll.bankapp.jpa.model.Account;
import edu.carroll.bankapp.jpa.model.SiteUser;
import edu.carroll.bankapp.jpa.model.Transaction;
import edu.carroll.bankapp.jpa.repo.TransactionRepository;
import edu.carroll.bankapp.service.AccountService;
import edu.carroll.bankapp.service.TransactionService;
import edu.carroll.bankapp.service.UserService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.ExpectedCount.times;

@Transactional
@SpringBootTest
public class TransactionServiceTest {
    @Autowired
    private AccountService accountService;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private UserService userService;
    private TransactionRepository transactionRepository;

    @BeforeEach
    public void setUp() {
        SiteUser john = userService.createUser("John Doe", "john@example.com", "johndoe", "password123");
        SiteUser jane = userService.createUser("Jane Smith", "jane@example.com", "janesmith", "letmein456");
        userService.createUser("Alice Johnson", "alice@example.com", "alicejohnson", "mysecret123");
        SiteUser unicodeMan = userService.createUser("𝔘𝔫𝔦𝔠𝔬𝔡𝔢 𝔐𝔞𝔫!",
                "𝕚𝕝𝕚𝕜𝕖𝕥𝕠𝕓𝕣𝕖𝕒𝕜𝕥𝕙𝕚𝕟𝕘𝕤@𝕖𝕞𝕒𝕚𝕝.𝕔𝕠𝕞",
                "☕☕☕☕", "⿈⍺✋⇏⮊⎏⇪⤸Ⲥ↴⍁➄⼉⦕ⶓ∧⻟⍀⇝⧽");

        accountService.createAccount("John's Savings Account", 0, john);
        accountService.createAccount("John's Checking Account", 0, john);
        accountService.createAccount("Jane's Investment Account", 0, jane);
        accountService.createAccount("💰💰💰", -1000, unicodeMan);
    }

    @Test
    public void testCreateTransaction() {
        SiteUser john = userService.getUser("johndoe");
        List<Account> johnsAccounts = accountService.getUserAccounts(john);
        Transaction createdTransaction = transactionService.createTransaction("TestTransaction", 100.0, "TestToFrom", johnsAccounts.get(0));

        assertNotNull(createdTransaction);
        assertEquals("TestTransaction", createdTransaction.getName());
        assertEquals(100.0, createdTransaction.getAmountInDollars(), 0.01);
    }

//    @Test
//    public void testGetUserTransaction() {
//        SiteUser loggedInUser = new SiteUser(); // Create a user for testing
//        TransactionService expectedTransaction = new TransactionService();
//        expectedTransaction.setId(1);
//
//        when(transactionRepository.findById(1)).thenReturn(List.of(expectedTransaction));
//
//        TransactionService retrievedTransaction = transactionService.getUserTransaction(loggedInUser, 1);
//
//        assertNotNull(retrievedTransaction);
//        assertEquals(expectedTransaction, retrievedTransaction);
//    }
//
//    @Test
//    public void testDeleteTransaction() {
//        SiteUser loggedInUser = new SiteUser(); // Create a user for testing
//        TransactionService transaction = new TransactionService();
//        transaction.setId(1);
//        transaction.setAccount(new Account());
//
//        when(loggedInUser.owns(transaction)).thenReturn(true);
//
//        transactionService.deleteTransaction(loggedInUser, transaction);
//
//        // Verify that the account balance was updated and saved
//        verify(accountService, times(1)).saveAccount(transaction.getAccount());
//        // Verify that the transaction was deleted
//        verify(transactionRepository, times(1)).delete(eq(transaction));
//    }
}
