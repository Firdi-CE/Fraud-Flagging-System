package com.backend_pmgt.service;

import com.backend_pmgt.dto.rules.RulesLogicResultDTO;
import com.backend_pmgt.entity.RulesValue;
import com.backend_pmgt.entity.Transaction;
import com.backend_pmgt.repository.RulesValueRepository;
import com.backend_pmgt.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class AmountCheckService {

    @Autowired
    RulesValueRepository rulesValueRepository;

    @Autowired
    TransactionRepository transactionRepository;

    // Check transaction by amount with different limits for payment methods
    public RulesLogicResultDTO check(Transaction transaction) {

        RulesLogicResultDTO resultDTO = new RulesLogicResultDTO();
        if (transaction.getTrxAmount() == null) {
            return null;
        }

        String paymentMethod = transaction.getTrxPaymentMethod();
        double amount = transaction.getTrxAmount();
        RulesValue rulesValue;

        if ("bifast".equalsIgnoreCase(paymentMethod)) {
             rulesValue = rulesValueRepository.getReferenceById(Long.valueOf(4));
             resultDTO.setFraudID(4);
        } else if ("skn".equalsIgnoreCase(paymentMethod)) {
            rulesValue = rulesValueRepository.getReferenceById(Long.valueOf(5));
            resultDTO.setFraudID(5);
        } else if ("rtgs".equalsIgnoreCase(paymentMethod)) {
            rulesValue = rulesValueRepository.getReferenceById(Long.valueOf(6));;
            resultDTO.setFraudID(6);
        }
        else {
            rulesValue = rulesValueRepository.getReferenceById(Long.valueOf(3));
            resultDTO.setFraudID(3);
        }

        boolean checkTimeResult = checkTimeRule(transaction, rulesValue);
        boolean amountLimitPerTrx = amount > rulesValue.getRulesAmountLimit();
        boolean finalResult = amountLimitPerTrx || checkTimeResult;

        resultDTO.setFlagResult(finalResult);
        resultDTO.setFraudType(3);
        return  resultDTO;
    }

    // Check if total amount of transactions within x minutes exceeds limit amount
    public boolean checkTimeRule(Transaction transaction, RulesValue rulesValue) {
        long timeRangeMillis = (long) rulesValue.getRulesTimeRange() * 60 * 1000;
        double amountLimit = rulesValue.getRulesAmountLimit();

        long currentTimeMillis = System.currentTimeMillis();
        Date startDate = new Date(currentTimeMillis - timeRangeMillis);

        List<Transaction> transactions = transactionRepository.findByAccountSenderAndDateAfter(transaction.getTrxAccountSender(), startDate);

        if (transactions == null || transactions.isEmpty()) {
            return false; // no transactions in time range, so amount not exceeded
        }

        double totalAmount = 0.0;
        for (Transaction txn : transactions) {
            Double amount = txn.getTrxAmount();
            if (amount != null) {
                totalAmount += amount;
            }
        }
        return totalAmount > amountLimit;
    }
}
