package com.backend_pmgt.service;

import com.backend_pmgt.entity.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class LocationCheckServiceTest {

    private LocationCheckService locationCheckService;

    @BeforeEach
    void setUp() {
        locationCheckService = new LocationCheckService();
    }

    @Test
    void testCheck_Flagged_DifferentLocationWithinThreshold() {
        Transaction previousTransaction = new Transaction();
        previousTransaction.setTrxDate(new Date(System.currentTimeMillis() - 2 * 60 * 1000)); // 2 minutes ago
        previousTransaction.setTrxLocation("LocationA");

        Transaction currentTransaction = new Transaction();
        currentTransaction.setTrxDate(new Date());
        currentTransaction.setTrxLocation("LocationB");

        assertTrue(locationCheckService.check(currentTransaction, previousTransaction));
    }

    @Test
    void testCheck_NotFlagged_SameLocationWithinThreshold() {
        Transaction previousTransaction = new Transaction();
        previousTransaction.setTrxDate(new Date(System.currentTimeMillis() - 2 * 60 * 1000)); // 2 minutes ago
        previousTransaction.setTrxLocation("LocationA");

        Transaction currentTransaction = new Transaction();
        currentTransaction.setTrxDate(new Date());
        currentTransaction.setTrxLocation("LocationA");

        assertFalse(locationCheckService.check(currentTransaction, previousTransaction));
    }

    @Test
    void testCheck_NotFlagged_DifferentLocationOutsideThreshold() {
        Transaction previousTransaction = new Transaction();
        previousTransaction.setTrxDate(new Date(System.currentTimeMillis() - 10 * 60 * 1000)); // 10 minutes ago
        previousTransaction.setTrxLocation("LocationA");

        Transaction currentTransaction = new Transaction();
        currentTransaction.setTrxDate(new Date());
        currentTransaction.setTrxLocation("LocationB");

        assertFalse(locationCheckService.check(currentTransaction, previousTransaction));
    }

    @Test
    void testCheck_NotFlagged_NoPreviousTransaction() {
        Transaction currentTransaction = new Transaction();
        currentTransaction.setTrxDate(new Date());
        currentTransaction.setTrxLocation("LocationA");

        assertFalse(locationCheckService.check(currentTransaction, null));
    }
}
