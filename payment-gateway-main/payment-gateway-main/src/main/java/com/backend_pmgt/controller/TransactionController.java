package com.backend_pmgt.controller;

import com.backend_pmgt.dto.transaction.SearchTransactionRequestDTO;
import com.backend_pmgt.dto.transaction.TransactionRequestDTO;
import com.backend_pmgt.dto.transaction.TransactionResponseAPI;
import com.backend_pmgt.dto.transaction.TransactionResponseDTO;
import com.backend_pmgt.entity.Transaction;
import com.backend_pmgt.service.TransactionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    @Autowired
    TransactionService transactionService;

    @PostMapping("/create")
    public ResponseEntity<TransactionResponseDTO> addTransaction(@RequestBody TransactionRequestDTO transactionRequest) throws JsonProcessingException {
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.addTransaction(transactionRequest));
    }

    @GetMapping
    public ResponseEntity<TransactionResponseAPI> getAllTransactions(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue="5") int size) {
        TransactionResponseAPI transactionResponseAPI = transactionService.getAllTransactions(page, size);
        return ResponseEntity.ok(transactionResponseAPI);
    }

    @GetMapping("/recent")
    public ResponseEntity<List<TransactionResponseDTO>> getRecentTransactions() {
        return ResponseEntity.ok(transactionService.getRecentTransactions());
    }

    @GetMapping("/{accountNo}")
    public ResponseEntity<List<TransactionResponseDTO>> getTransactionByAccountNo(@PathVariable String accountNo) {
        return ResponseEntity.ok(transactionService.getTransactionByAccountNo(accountNo));
    }

    @PostMapping("/search")
    public ResponseEntity<TransactionResponseAPI> searchTransaction(@RequestBody SearchTransactionRequestDTO request, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue="2") int size) {
        TransactionResponseAPI results = transactionService.searchTransactions(request, page, size);
        return ResponseEntity.ok(results);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Transaction> updateTransaction(@PathVariable Long id, @RequestBody Transaction transaction) {
        Transaction updatedTransaction = transactionService.updateTransaction(id, transaction);
        return ResponseEntity.ok(updatedTransaction);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        transactionService.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }
}