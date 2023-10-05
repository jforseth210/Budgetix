package edu.carroll.bankapp.jpa.repo;

import edu.carroll.bankapp.jpa.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Interface implemented by Hibernate for querying Transaction information from the database
 */
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    List<Transaction> findById(int id);

}
