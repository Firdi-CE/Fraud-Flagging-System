package com.backend_pmgt.service;

import com.backend_pmgt.entity.RulesValue;
import com.backend_pmgt.entity.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AmountCheckServiceTest {

    private AmountCheckService amountCheckService;

    @BeforeEach
    void setUp() {
        amountCheckService = new AmountCheckService();
    }

    @Test
    void testCheck_DefaultLimit() {
        Transaction txn = new Transaction();
        txn.setTrxPaymentMethod("Other");
        txn.setTrxAmount(60_000_000.0);
        assertTrue(amountCheckService.check(txn));

        txn.setTrxAmount(40_000_000.0);
        assertFalse(amountCheckService.check(txn));
    }

    @Test
    void testCheck_BiFastLimit() {
        Transaction txn = new Transaction();
        txn.setTrxPaymentMethod("Bi Fast");
        txn.setTrxAmount(15_000_000.0);
        assertTrue(amountCheckService.check(txn));

        txn.setTrxAmount(5_000_000.0);
        assertFalse(amountCheckService.check(txn));
    }

    @Test
    void testCheck_GoPayLimit() {
        Transaction txn = new Transaction();
        txn.setTrxPaymentMethod("Go pay");
        txn.setTrxAmount(2_000_000.0);
        assertTrue(amountCheckService.check(txn));

        txn.setTrxAmount(500_000.0);
        assertFalse(amountCheckService.check(txn));
    }

    @Test
    void testCheckTimeRule() {
        List<Transaction> transactions = new ArrayList<>();

        long now = new Date().getTime();

        Transaction txn1 = new Transaction();
        txn1.setTrxDate(new Date(now - 1 * 60 * 1000)); // 1 minute ago
        txn1.setTrxAmount(600_000.0);

        Transaction txn2 = new Transaction();
        txn2.setTrxDate(new Date(now - 2 * 60 * 1000)); // 2 minutes ago
        txn2.setTrxAmount(500_000.0);

        RulesValue rv = new RulesValue();
        rv.setRulesTimeRange(3);
        rv.setRulesAmountLimit(1_000_000.00);

        transactions.add(txn1);
        transactions.add(txn2);

        assertTrue(amountCheckService.checkTimeRule(txn2,rv));

        transactions.clear();
        txn1.setTrxAmount(400_000.0);
        txn2.setTrxAmount(300_000.0);
        transactions.add(txn1);
        transactions.add(txn2);

        assertFalse(amountCheckService.checkTimeRule(txn2,rv));
    }
}
