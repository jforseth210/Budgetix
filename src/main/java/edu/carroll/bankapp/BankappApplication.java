package edu.carroll.bankapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Class for application entry-point
 */
@SpringBootApplication
public class BankappApplication {
    /**
     * Entry point for the application
     *
     * @param args - command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(BankappApplication.class, args);
    }

}
