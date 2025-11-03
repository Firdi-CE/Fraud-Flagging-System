package com.backend_pmgt.service;

import com.backend_pmgt.dto.rules.RulesLogicResultDTO;
import com.backend_pmgt.entity.RulesValue;
import com.backend_pmgt.entity.Transaction;
import com.backend_pmgt.repository.RulesValueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BottingCheckService {

    @Autowired
    RulesValueRepository rulesValueRepository;

    private static final double AMOUNT_THRESHOLD = 1_000_000.00; //threshold for individual transaction amount

    // Check transaction for botting (potential money laundering)
    public RulesLogicResultDTO check(Transaction transaction, List<Transaction> transactionHistory) {
        RulesValue rulesValue = rulesValueRepository.getReferenceById(Long.valueOf(2));
        long timeRange = (long) rulesValue.getRulesTimeRange() * 60 * 1000; // Convert minutes to milliseconds
        double countLimit = rulesValue.getRulesAmountLimit();

        long transactionTimeMillis = transaction.getTrxDate().getTime();

        // Map to track the total amount sent from different accounts to the same recipient
        Map<String, Double> senderAmounts = new HashMap<>();

        // Iterate through the transaction history
        for (Transaction txn : transactionHistory) {
            // Check if the transaction is sending money to the same recipient
            if (txn.getTrxAccountRecipient().equals(transaction.getTrxAccountRecipient())) {
                // Check if the transaction is within the time range
                long txnTimeMillis = txn.getTrxDate().getTime(); // Get the transaction date in milliseconds
                long timeDifference = transactionTimeMillis - txnTimeMillis; // Calculate the time difference

                if (timeDifference >= 0 && timeDifference <= timeRange) {
                    // Add the amount to the corresponding sender's total
                    senderAmounts.put(txn.getTrxAccountSender(),
                            senderAmounts.getOrDefault(txn.getTrxAccountSender(), 0.0) + txn.getTrxAmount());
                }
            }
        }

        // Check if there are multiple senders sending large amounts to the same recipient
        int count = 0;
        for (Double amount : senderAmounts.values()) {
            if (amount > AMOUNT_THRESHOLD) {
                count++;
            }
        }

        boolean result = count >= countLimit;

        RulesLogicResultDTO resultDTO = new RulesLogicResultDTO();
        resultDTO.setFlagResult(result);
        resultDTO.setFraudType(2);
        resultDTO.setFraudID(2);

        return resultDTO;
    }
}