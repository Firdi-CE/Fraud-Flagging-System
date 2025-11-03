package com.backend_pmgt.service;

import com.backend_pmgt.dto.transaction.SearchTransactionRequestDTO;
import com.backend_pmgt.dto.transaction.TransactionRequestDTO;
import com.backend_pmgt.dto.transaction.TransactionResponseAPI;
import com.backend_pmgt.dto.transaction.TransactionResponseDTO;
import com.backend_pmgt.entity.AuditTrailsTransaction;
import com.backend_pmgt.entity.Transaction;
import com.backend_pmgt.repository.AuditTrailsTransactionRepository;
import com.backend_pmgt.repository.TransactionRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.query.criteria.HibernateCriteriaBuilder;
import org.hibernate.query.criteria.JpaCriteriaQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TransactionService {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    AuditTrailsTransactionRepository auditTrailsTransactionRepository;

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    RulesService rulesService;

    ObjectMapper objectMapper = new ObjectMapper();

    public String combineString(String length, String width) {
        return String.join(",", length, width);
    }

    public TransactionResponseDTO addTransaction(TransactionRequestDTO transactionRequestDTO) throws JsonProcessingException {
        String location = combineString(transactionRequestDTO.getTrxLocationLatitude(), transactionRequestDTO.getTrxLocationLongitude());

        Transaction transaction = new Transaction();
        transaction.setTrxPaymentMethod(transactionRequestDTO.getTrxPaymentMethod());
        transaction.setTrxAccountSender(transactionRequestDTO.getTrxAccountSender());
        transaction.setTrxAccountRecipient(transactionRequestDTO.getTrxAccountRecipient());
        transaction.setTrxAmount(transactionRequestDTO.getTrxAmount());
        transaction.setTrxLocation(location);
        transaction.setTrxDate(new Date());

        transactionRepository.save(transaction);

        rulesService.checkForFraud();

        TransactionResponseDTO transactionResponseDTO = TransactionResponseDTO.builder()
                .trxID(transaction.getTrxID())
                .trxPaymentMethod(transaction.getTrxPaymentMethod())
                .trxAccountSender(transaction.getTrxAccountSender())
                .trxAccountRecipient(transaction.getTrxAccountRecipient())
                .trxAmount(transaction.getTrxAmount())
                .trxLocation(transaction.getTrxLocation())
                .trxDate(transaction.getTrxDate())
                .trxFlag(transaction.getTrxFlag())
                .build();

        //Insert new Audit Trails
        AuditTrailsTransaction auditTrailsTransaction = new AuditTrailsTransaction();
        auditTrailsTransaction.setLogAction("Create Transaction");
        auditTrailsTransaction.setLogDescription("Add New Transaction");
        auditTrailsTransaction.setLogRequest(objectMapper.writeValueAsString(transactionRequestDTO));
        auditTrailsTransaction.setLogResponse(objectMapper.writeValueAsString(transactionResponseDTO));
        auditTrailsTransaction.setLogDate(new Date());

        auditTrailsTransactionRepository.save(auditTrailsTransaction);

        return transactionResponseDTO;
    }

    public TransactionResponseAPI getAllTransactions(int page, int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Transaction> transactionsPage = transactionRepository.findAllByOrderByTrxDateDesc(pageable);
        long totalCount = transactionsPage.getTotalElements();

        List<TransactionResponseDTO> transactionResponses = new ArrayList<>();

        for (Transaction transaction : transactionsPage.getContent()) {
            TransactionResponseDTO result = TransactionResponseDTO.builder()
                    .trxID(transaction.getTrxID())
                    .trxPaymentMethod(transaction.getTrxPaymentMethod())
                    .trxAccountRecipient(transaction.getTrxAccountRecipient())
                    .trxAccountSender(transaction.getTrxAccountSender())
                    .trxAmount(transaction.getTrxAmount())
                    .trxLocation(transaction.getTrxLocation())
                    .trxDate(transaction.getTrxDate())
                    .trxFlag(transaction.getTrxFlag())
                    .trxDesc(transaction.getTrxDesc())
                    .build();
            transactionResponses.add(result);
        }

        return TransactionResponseAPI.builder().transactions(transactionResponses).totalCount(totalCount).build();
    }

    public List<TransactionResponseDTO> getRecentTransactions() {

        List<Transaction> transactions = transactionRepository.findTop5ByOrderByTrxDateDesc();

        List<TransactionResponseDTO> transactionResponses = new ArrayList<>();

        for (Transaction transaction : transactions) {
            TransactionResponseDTO result = TransactionResponseDTO.builder()
                    .trxID(transaction.getTrxID())
                    .trxPaymentMethod(transaction.getTrxPaymentMethod())
                    .trxAccountRecipient(transaction.getTrxAccountRecipient())
                    .trxAccountSender(transaction.getTrxAccountSender())
                    .trxAmount(transaction.getTrxAmount())
                    .trxLocation(transaction.getTrxLocation())
                    .trxDate(transaction.getTrxDate())
                    .trxFlag(transaction.getTrxFlag())
                    .trxDesc(transaction.getTrxDesc())
                    .build();
            transactionResponses.add(result);
        }
        return transactionResponses;
    }

    public List<TransactionResponseDTO> getTransactionByAccountNo(String accountNo) {
        List<Transaction> transactions = transactionRepository.findByAccountSenderOrAccountRecipient(accountNo);

        List<TransactionResponseDTO> transactionResponses = new ArrayList<>();

        for (Transaction transaction : transactions) {
            TransactionResponseDTO result = TransactionResponseDTO.builder()
                    .trxID(transaction.getTrxID())
                    .trxPaymentMethod(transaction.getTrxPaymentMethod())
                    .trxAccountRecipient(transaction.getTrxAccountRecipient())
                    .trxAccountSender(transaction.getTrxAccountSender())
                    .trxAmount(transaction.getTrxAmount())
                    .trxLocation(transaction.getTrxLocation())
                    .trxDate(transaction.getTrxDate())
                    .trxFlag(transaction.getTrxFlag())
                    .trxDesc(transaction.getTrxDesc())
                    .build();
            transactionResponses.add(result);
        }
        return transactionResponses;
    }


    public TransactionResponseAPI searchTransactions(SearchTransactionRequestDTO request, int page, int size) {
        if (request.getTrxDateEnd() != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(request.getTrxDateEnd());
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            cal.set(Calendar.MILLISECOND, 999);
            request.setTrxDateEnd(cal.getTime());
        }

        HibernateCriteriaBuilder cb = entityManager.unwrap(Session.class).getCriteriaBuilder();
        JpaCriteriaQuery<Transaction> cq = cb.createQuery(Transaction.class);
        Root<Transaction> root = cq.from(Transaction.class);

        List<Predicate> predicates = new ArrayList<>();

        if (request.getTrxAccountSender() != null && !Objects.equals(request.getTrxAccountSender(), "")) {
            predicates.add(cb.equal(root.get("trxAccountSender"), request.getTrxAccountSender()));
        }
        if (request.getTrxAccountRecipient() != null && !Objects.equals(request.getTrxAccountRecipient(), "")) {
            predicates.add(cb.equal(root.get("trxAccountRecipient"), request.getTrxAccountRecipient()));
        }

        if (request.getTrxPaymentMethod() != null && !Objects.equals(request.getTrxPaymentMethod(), "")) {
            predicates.add(cb.equal(root.get("trxPaymentMethod"), request.getTrxPaymentMethod()));
        }

        if (request.getTrxFlag() != false) {
            predicates.add(cb.equal(root.get("trxFlag"), request.getTrxFlag()));
        }

        if (request.getTrxAmountMin() != null && request.getTrxAmountMin() != 0) {
            predicates.add(cb.ge(root.get("trxAmount"), request.getTrxAmountMin()));
        }

        if (request.getTrxAmountMax() != null && request.getTrxAmountMax() !=0) {
            predicates.add(cb.le(root.get("trxAmount"), request.getTrxAmountMax()));
        }

        if (request.getTrxDateStart() != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("trxDate"), request.getTrxDateStart()));
        }

        if (request.getTrxDateEnd() != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("trxDate"), request.getTrxDateEnd()));
        }

        cq.where(cb.and(predicates.toArray(new Predicate[0])));
        cq.orderBy(cb.desc(root.get("trxDate")));

        Pageable pageable = PageRequest.of(page, size);

        List<Transaction> trx = entityManager.createQuery(cq)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        Long totalCount = entityManager.createQuery(cq.createCountQuery()).getSingleResult();

        List<TransactionResponseDTO> content = new ArrayList<>();
        for (Transaction tx : trx) {
            TransactionResponseDTO dto = TransactionResponseDTO.builder()
                    .trxID(tx.getTrxID())
                    .trxPaymentMethod(tx.getTrxPaymentMethod())
                    .trxAccountSender(tx.getTrxAccountSender())
                    .trxAccountRecipient(tx.getTrxAccountRecipient())
                    .trxAmount(tx.getTrxAmount())
                    .trxLocation(tx.getTrxLocation())
                    .trxDate(tx.getTrxDate())
                    .trxFlag(tx.getTrxFlag())
                    .trxDesc(tx.getTrxDesc())
                    .build();
            content.add(dto);
        }
        return TransactionResponseAPI.builder().transactions(content).totalCount(totalCount).build();
    }

    public Transaction updateTransaction(Long id, Transaction transaction) {
        transaction.setTrxID(id);
        return transactionRepository.save(transaction);
    }

    public void deleteTransaction(Long id) {
        transactionRepository.deleteById(id);
    }

    public List<Transaction> getTransactionHistory(String accountNumber) {
        return transactionRepository.findByAccountSenderOrAccountRecipient(accountNumber);
    }
    }