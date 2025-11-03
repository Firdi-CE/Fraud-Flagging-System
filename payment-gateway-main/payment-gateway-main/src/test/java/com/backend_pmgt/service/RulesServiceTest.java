package com.backend_pmgt.service;

import com.backend_pmgt.dto.rules.RulesLogicResultDTO;
import com.backend_pmgt.entity.Transaction;
import com.backend_pmgt.repository.AuditTrailsFraudRepository;
import com.backend_pmgt.repository.FraudRepository;
import com.backend_pmgt.repository.RulesValueRepository;
import com.backend_pmgt.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RulesServiceTest {

    @Mock
    private RulesValueRepository rulesRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private FraudRepository fraudRepository;

    @Mock
    private LocationCheckService locationCheckService;

    @Mock
    private BottingCheckService bottingCheckService;

    @Mock
    private AmountCheckService amountCheckService;

    @Mock
    private AuditTrailsFraudRepository auditTrailsFraudRepository;

    @InjectMocks
    private RulesService rulesService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCheckTransaction_AllChecksFalse() {
        Transaction currentTransaction = new Transaction();
        Transaction previousTransaction = new Transaction();
        List<Transaction> transactionHistory = new ArrayList<>();

        when(locationCheckService.check(currentTransaction, previousTransaction)).thenReturn(false);
        when(bottingCheckService.check(currentTransaction, transactionHistory)).thenReturn(false);
        when(amountCheckService.check(currentTransaction)).thenReturn(false);

        RulesLogicResultDTO result = rulesService.checkTransaction(currentTransaction, previousTransaction, transactionHistory);

        assertFalse(result.getFlagResult());
        verify(locationCheckService).check(currentTransaction, previousTransaction);
        verify(bottingCheckService).check(currentTransaction, transactionHistory);
        verify(amountCheckService).check(currentTransaction);
    }

    @Test
    void testCheckTransaction_LocationCheckTrue() {
        Transaction currentTransaction = new Transaction();
        Transaction previousTransaction = new Transaction();
        List<Transaction> transactionHistory = new ArrayList<>();

        when(locationCheckService.check(currentTransaction, previousTransaction)).thenReturn(true);
        when(bottingCheckService.check(currentTransaction, transactionHistory)).thenReturn(false);
        when(amountCheckService.check(currentTransaction)).thenReturn(false);

        RulesLogicResultDTO result = rulesService.checkTransaction(currentTransaction, previousTransaction, transactionHistory);

        assertTrue(result.getFlagResult());
        assertEquals(1, result.getFraudType());
    }

    @Test
    void testCheckTransaction_BottingCheckTrue() {
        Transaction currentTransaction = new Transaction();
        Transaction previousTransaction = new Transaction();
        List<Transaction> transactionHistory = new ArrayList<>();

        when(locationCheckService.check(currentTransaction, previousTransaction)).thenReturn(false);
        when(bottingCheckService.check(currentTransaction, transactionHistory)).thenReturn(true);
        when(amountCheckService.check(currentTransaction)).thenReturn(false);

        RulesLogicResultDTO result = rulesService.checkTransaction(currentTransaction, previousTransaction, transactionHistory);

        assertTrue(result.getFlagResult());
        assertEquals(2, result.getFraudType());
    }

    @Test
    void testCheckTransaction_AmountCheckTrue() {
        Transaction currentTransaction = new Transaction();
        Transaction previousTransaction = new Transaction();
        List<Transaction> transactionHistory = new ArrayList<>();

        when(locationCheckService.check(currentTransaction, previousTransaction)).thenReturn(false);
        when(bottingCheckService.check(currentTransaction, transactionHistory)).thenReturn(false);
        when(amountCheckService.check(currentTransaction)).thenReturn(true);

        RulesLogicResultDTO result = rulesService.checkTransaction(currentTransaction, previousTransaction, transactionHistory);

        assertTrue(result.getFlagResult());
        assertEquals(3, result.getFraudType());
    }
}
