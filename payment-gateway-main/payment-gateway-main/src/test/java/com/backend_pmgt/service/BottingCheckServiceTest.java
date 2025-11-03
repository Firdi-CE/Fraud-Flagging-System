package com.backend_pmgt.service;

import com.backend_pmgt.entity.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BottingCheckServiceTest {

    private BottingCheckService bottingCheckService;

    @BeforeEach
    void setUp() {
        bottingCheckService = new BottingCheckService();
    }

    @Test
    void testCheck_Flagged() {
        List<Transaction> transactions = new ArrayList<>();

        // Create 5 senders with amounts over threshold to same recipient
        for (int i = 0; i < 5; i++) {
            Transaction txn = new Transaction();
            txn.setTrxAccountSender("Sender" + i);
            txn.setTrxAccountRecipient("Recipient1");
            txn.setTrxAmount(150_000.0);
            transactions.add(txn);
        }

        Transaction currentTransaction = new Transaction();
        currentTransaction.setTrxAccountRecipient("Recipient1");

        assertTrue(bottingCheckService.check(currentTransaction, transactions));
    }

    @Test
    void testCheck_NotFlagged() {
        List<Transaction> transactions = new ArrayList<>();

        // Create 4 senders with amounts over threshold to same recipient
        for (int i = 0; i < 4; i++) {
            Transaction txn = new Transaction();
            txn.setTrxAccountSender("Sender" + i);
            txn.setTrxAccountRecipient("Recipient1");
            txn.setTrxAmount(150_000.0);
            transactions.add(txn);
        }

        Transaction currentTransaction = new Transaction();
        currentTransaction.setTrxAccountRecipient("Recipient1");

        assertFalse(bottingCheckService.check(currentTransaction, transactions));
    }
}
