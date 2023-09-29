package edu.carroll.bankapp.jpa.repo;

import edu.carroll.bankapp.jpa.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
}
