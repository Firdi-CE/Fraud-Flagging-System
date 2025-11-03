package com.backend_pmgt.service;

import com.backend_pmgt.dto.fraud.FraudResponseDTO;
import com.backend_pmgt.dto.rules.RulesLogicResultDTO;
import com.backend_pmgt.dto.transaction.TransactionResponseDTO;
import com.backend_pmgt.entity.AuditTrailsFraud;
import com.backend_pmgt.entity.Fraud;
import com.backend_pmgt.entity.RulesValue;
import com.backend_pmgt.entity.Transaction;
import com.backend_pmgt.repository.AuditTrailsFraudRepository;
import com.backend_pmgt.repository.FraudRepository;
import com.backend_pmgt.repository.RulesValueRepository;
import com.backend_pmgt.repository.TransactionRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class RulesService {

    @Autowired
    private RulesValueRepository rulesRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private FraudRepository fraudRepository;
    @Autowired
    LocationCheckService locationCheckService;
    @Autowired
    BottingCheckService bottingCheckService;
    @Autowired
    AmountCheckService amountCheckService;
    @Autowired
    AuditTrailsFraudRepository auditTrailsFraudRepository;

    ObjectMapper objectMapper = new ObjectMapper();

    // Check if the transaction is fraudulent
    public RulesLogicResultDTO checkTransaction(Transaction currentTransaction, Transaction previousTransaction, List<Transaction> transactionHistory) {
        RulesLogicResultDTO locationCheck = locationCheckService.check();
        RulesLogicResultDTO bottingCheck = bottingCheckService.check(currentTransaction, transactionHistory);
        RulesLogicResultDTO amountCheck = amountCheckService.check(currentTransaction);
        RulesLogicResultDTO result=new RulesLogicResultDTO();

        if(locationCheck.getFlagResult()){
            result.setFraudType(locationCheck.getFraudType());
            result.setFraudID(locationCheck.getFraudID());
        }
        if(bottingCheck.getFlagResult()){
            result.setFraudType(bottingCheck.getFraudType());
            result.setFraudID(bottingCheck.getFraudID());
        }
        if(amountCheck.getFlagResult()){
            result.setFraudType(amountCheck.getFraudType());
            result.setFraudID(amountCheck.getFraudID());
        }

        result.setFlagResult(locationCheck.getFlagResult() || bottingCheck.getFlagResult() || amountCheck.getFlagResult());

        return result;
    }

    public Transaction getLatestTransaction() {
        Optional<Transaction> latestTransaction = transactionRepository.findLatestTransaction();
        return latestTransaction.orElse(null);
    }

    public Transaction getPreviousTransaction() {
        Optional<Transaction> previousTransaction = transactionRepository.findPreviousTransaction();
        return previousTransaction.orElse(null);
    }

    public List<Transaction> getTransactionsWithinOneHour() {
        Transaction latestTransaction = getLatestTransaction();
        if (latestTransaction != null) {
            Date cutoffTime = new Date(latestTransaction.getTrxDate().getTime() - 3600 * 1000); // Subtract one hour
            return transactionRepository.findTransactionsWithinOneHour(cutoffTime);
        }
        return List.of();
    }

    // Update for Check method on rules
    public Transaction updateTransactionFlag(Transaction latestTransaction, RulesLogicResultDTO result) {
        Transaction transaction = new Transaction();
        transaction.setTrxID(latestTransaction.getTrxID());
        transaction.setTrxPaymentMethod(latestTransaction.getTrxPaymentMethod());
        transaction.setTrxAccountSender(latestTransaction.getTrxAccountSender());
        transaction.setTrxAccountRecipient(latestTransaction.getTrxAccountRecipient());
        transaction.setTrxAmount(latestTransaction.getTrxAmount());
        transaction.setTrxLocation(latestTransaction.getTrxLocation());
        transaction.setTrxDate(latestTransaction.getTrxDate());
        transaction.setTrxFlag(result.getFlagResult());
        RulesValue rv;
        switch (result.getFraudID()) {
            case 1:
                rv = rulesRepository.getReferenceById(Long.valueOf(1));
                transaction.setTrxDesc("impossible location, distance w/ prev over "+rv.getRulesAmountLimit()+"km in "+rv.getRulesTimeRange()+" min range");
                break;
            case 2:
                rv = rulesRepository.getReferenceById(Long.valueOf(2));
                transaction.setTrxDesc("botting suspicion, number of transaction over "+ rv.getRulesAmountLimit().intValue() + "x in "+rv.getRulesTimeRange()+" min range");
                break;
            case 3:
                rv = rulesRepository.getReferenceById(Long.valueOf(3));
                transaction.setTrxDesc("Online Trx Amount Over limit of: "+ rv.getRulesAmountLimit() +" in "+rv.getRulesTimeRange()+" min range");
                break;
            case 4:
                rv = rulesRepository.getReferenceById(Long.valueOf(4));
                transaction.setTrxDesc("BI Fast Amount over limit of: "+ rv.getRulesAmountLimit() +" in "+rv.getRulesTimeRange()+" min range");
                break;
            case 5:
                rv = rulesRepository.getReferenceById(Long.valueOf(5));
                transaction.setTrxDesc("SKN Amount over limit of: "+ rv.getRulesAmountLimit() +" in "+rv.getRulesTimeRange()+" min range");
                break;
            case 6:
                rv = rulesRepository.getReferenceById(Long.valueOf(6));
                transaction.setTrxDesc("RTGS Amount over limit of: "+ rv.getRulesAmountLimit() +" in "+rv.getRulesTimeRange()+" min range");
                break;
            default:
                transaction.setTrxDesc(" - ");
                break;
        }

        return transactionRepository.save(transaction);
    }

    public TransactionResponseDTO checkForFraud() throws JsonProcessingException {

        //getLatest
        Transaction latestTransaction = getLatestTransaction();
        //getPreviousOne
        Transaction previousTransaction = getPreviousTransaction();
        //getHistoryUpToOneHour
        List<Transaction> transactionHistoryOneHour=getTransactionsWithinOneHour();

        RulesLogicResultDTO result = checkTransaction(latestTransaction,previousTransaction,transactionHistoryOneHour);

        Transaction transaction = updateTransactionFlag(latestTransaction, result);
        RulesValue rv;
        if (result.getFlagResult()){
            Fraud fraud = new Fraud();
            if (result.getFraudType()==2){
                fraud.setFraudAccount(transaction.getTrxAccountRecipient());
            }
            else {
                fraud.setFraudAccount(transaction.getTrxAccountSender());
            }
            switch (result.getFraudID()) {
                case 1:
                    fraud.setFraudLabel("location");
                    rv = rulesRepository.getReferenceById(Long.valueOf(1));
                    fraud.setFraudDescription("impossible location, distance w/ prev over "+rv.getRulesAmountLimit()+"km in "+rv.getRulesTimeRange()+" min range");
                    break;
                case 2:
                    fraud.setFraudLabel("botting");
                    rv = rulesRepository.getReferenceById(Long.valueOf(2));
                    fraud.setFraudDescription("Botting suspicion, number of transaction over "+ rv.getRulesAmountLimit().intValue() + "x in "+rv.getRulesTimeRange()+" min range");
                    break;
                case 3:
                    rv = rulesRepository.getReferenceById(Long.valueOf(3));
                    fraud.setFraudLabel("amount");
                    fraud.setFraudDescription("Online trx Amount over limit of: "+ rv.getRulesAmountLimit() +" in "+rv.getRulesTimeRange()+" min range");
                    break;
                case 4:
                    rv = rulesRepository.getReferenceById(Long.valueOf(4));
                    fraud.setFraudLabel("amount");
                    fraud.setFraudDescription("BI Fast Amount over limit of: "+ rv.getRulesAmountLimit() +" in "+rv.getRulesTimeRange()+" min range");
                    break;
                case 5:
                    rv = rulesRepository.getReferenceById(Long.valueOf(5));
                    fraud.setFraudLabel("amount");
                    fraud.setFraudDescription("SKN Amount over limit of: "+ rv.getRulesAmountLimit() +" in "+rv.getRulesTimeRange()+" min range");
                    break;
                case 6:
                    rv = rulesRepository.getReferenceById(Long.valueOf(6));
                    fraud.setFraudLabel("amount");
                    fraud.setFraudDescription("RTGS Amount over limit of: "+ rv.getRulesAmountLimit() +" in "+rv.getRulesTimeRange()+" min range");
                    break;
                default:
                    fraud.setFraudLabel("undefined");
                    fraud.setFraudDescription("empty");
            }
            fraud.setFraudDate(new Date());
            fraudRepository.save(fraud);

            FraudResponseDTO fraudResponseDTO = FraudResponseDTO.builder()
                    .fraudID(fraud.getFraudID())
                    .fraudAccount(fraud.getFraudAccount())
                    .fraudLabel(fraud.getFraudLabel())
                    .build();

            //Insert new Audit Trails
            AuditTrailsFraud auditTrailsFraud = new AuditTrailsFraud();
            auditTrailsFraud.setLogAction("Create Fraud");
            auditTrailsFraud.setLogDescription("Add New Fraud:"+fraudResponseDTO.getFraudLabel());
            auditTrailsFraud.setLogRequest("none");
            auditTrailsFraud.setLogResponse(objectMapper.writeValueAsString(fraudResponseDTO));
            auditTrailsFraud.setLogDate(new Date());

            auditTrailsFraudRepository.save(auditTrailsFraud);

        }

        return TransactionResponseDTO.builder()
                .trxID(transaction.getTrxID())
                .trxPaymentMethod(transaction.getTrxPaymentMethod())
                .trxAccountSender(transaction.getTrxAccountSender())
                .trxAccountRecipient(transaction.getTrxAccountRecipient())
                .trxAmount(transaction.getTrxAmount())
                .trxLocation(transaction.getTrxLocation())
                .trxDate(transaction.getTrxDate())
                .trxFlag(transaction.getTrxFlag())
                .build();
    }
}