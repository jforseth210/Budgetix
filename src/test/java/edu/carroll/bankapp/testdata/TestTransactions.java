package edu.carroll.bankapp.testdata;

import org.springframework.stereotype.Component;

import edu.carroll.bankapp.jpa.model.Account;
import edu.carroll.bankapp.jpa.model.Transaction;
import edu.carroll.bankapp.service.TransactionService;

/**
 * Generic transactions that can be created for testing purposes
 */
@Component
public class TestTransactions {
    final TransactionService transactionService;

    public TestTransactions(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    public Transaction createTestTransaction(Account account) {
        return transactionService.createTransaction("Test Transaction", (long) 100.0, "Receiver",
                account);
    }

    public Transaction createATransaction(Account account) {
        return transactionService.createTransaction("A transaction!", (long) 100.0, "???",
                account);
    }
}
