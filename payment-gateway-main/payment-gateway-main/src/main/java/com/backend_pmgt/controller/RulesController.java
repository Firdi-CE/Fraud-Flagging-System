package com.backend_pmgt.controller;

import com.backend_pmgt.dto.rules.RulesLogicResultDTO;
import com.backend_pmgt.entity.Transaction;
import com.backend_pmgt.service.RulesService;
import com.backend_pmgt.service.RulesValueService;
import com.backend_pmgt.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/rules")
public class RulesController {

    @Autowired
    private RulesValueService rulesValueService;

    @Autowired
    private RulesService fraudDetectionService;

    @Autowired
    private TransactionService transactionService;


    @PostMapping("/check")
    public ResponseEntity<Boolean> checkTransaction(@RequestBody Transaction transaction) {
        List<Transaction> transactionHistory = transactionService.getTransactionHistory(transaction.getTrxAccountSender());

        Transaction previousTransaction = getPreviousTransaction(transactionHistory, transaction);

        RulesLogicResultDTO isFraudulent = fraudDetectionService.checkTransaction(transaction, previousTransaction, transactionHistory);
        return ResponseEntity.ok(isFraudulent.getFlagResult());
    }

    private Transaction getPreviousTransaction(List<Transaction> transactionHistory, Transaction currentTransaction) {
        Transaction previousTransaction = null;

        // Iterate through the transaction history
        for (Transaction txn : transactionHistory) {
            // Check if the transaction date is before the current transaction date
            if (txn.getTrxDate().before(currentTransaction.getTrxDate())) {
                // Update the previous transaction if it's more recent than the last found
                if (previousTransaction == null || txn.getTrxDate().after(previousTransaction.getTrxDate())) {
                    previousTransaction = txn;
                }
            }
        }

        return previousTransaction;
    }
}