package com.backend_pmgt.service;

import com.backend_pmgt.dto.dashboard.DashboardFraudResponseDTO;
import com.backend_pmgt.dto.dashboard.DashboardTrxResponseDTO;
import com.backend_pmgt.entity.Fraud;
import com.backend_pmgt.entity.Transaction;
import com.backend_pmgt.repository.FraudRepository;
import com.backend_pmgt.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DashboardService {
    @Autowired
    TransactionRepository transactionRepository;
    @Autowired
    FraudRepository fraudRepository;

    public List<DashboardTrxResponseDTO> getAllTransactionDashboard() {
        List<Transaction> transactions = transactionRepository.findAll();

        List<DashboardTrxResponseDTO> transactionResponses = new ArrayList<>();

        for (Transaction transaction : transactions) {
            DashboardTrxResponseDTO result = DashboardTrxResponseDTO.builder()
                    .trxId(transaction.getTrxID())
                    .trxPaymentMethod(transaction.getTrxPaymentMethod())
                    .trxLocation(transaction.getTrxLocation())
                    .trxAccountSender(transaction.getTrxAccountSender())
                    .trxAccountRecipient(transaction.getTrxAccountRecipient())
                    .trxAmount(transaction.getTrxAmount())
                    .trxDate(transaction.getTrxDate())
                    .trxFlag(transaction.getTrxFlag())
                    .build();
            transactionResponses.add(result);
        }
        return transactionResponses;
    }

    public List<DashboardFraudResponseDTO> getAllFraudDashboard() {
        List<Fraud> frauds = fraudRepository.findAll();

        List<DashboardFraudResponseDTO> fraudResponses = new ArrayList<>();

        for (Fraud fraud : frauds) {
            DashboardFraudResponseDTO result = DashboardFraudResponseDTO.builder()
                    .fraudID(fraud.getFraudID())
                    .fraudAccount(fraud.getFraudAccount())
                    .fraudDescription(fraud.getFraudDescription())
                    .fraudLabel(fraud.getFraudLabel())
                    .build();
            fraudResponses.add(result);
        }
        return fraudResponses;
    }
}
