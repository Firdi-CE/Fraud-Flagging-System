package com.backend_pmgt.repository;

import com.backend_pmgt.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Page<Transaction> findAllByOrderByTrxDateDesc(Pageable pageable);

    List<Transaction> findTop5ByOrderByTrxDateDesc();

    @Query("SELECT t FROM Transaction t WHERE t.trxAccountSender = :accountNo OR t.trxAccountRecipient = :accountNo ORDER BY t.trxDate DESC")
    List<Transaction> findByAccountSenderOrAccountRecipient(@Param("accountNo") String accountNo);

    @Query("SELECT t FROM Transaction t WHERE t.trxAccountSender = :accountNo ORDER BY t.trxDate DESC")
    List<Transaction> findByAccountSender(@Param("accountNo") String accountNo);

    @Query("SELECT t FROM Transaction t WHERE t.trxAccountSender = :accountNo AND t.trxDate >= :startDate ORDER BY t.trxDate DESC")
    List<Transaction> findByAccountSenderAndDateAfter(@Param("accountNo") String accountNo, @Param("startDate") Date startDate);

    @Query("SELECT t FROM Transaction t " +
            "WHERE (COALESCE(:sender,'') = '' OR t.trxAccountSender = :sender) " +
            "AND (COALESCE(:recipient,'') = '' OR t.trxAccountRecipient = :recipient) " +
            "AND (COALESCE(:location,'') = '' OR t.trxLocation = :location) " +
            "AND (:flag IS NULL OR t.trxFlag = CAST(:flag AS Boolean)) " +
            "AND (COALESCE(:minAmount,0) = 0 OR t.trxAmount >= :minAmount) " +
            "AND (COALESCE(:maxAmount,0) = 0 OR t.trxAmount <= :maxAmount) " +
            "AND t.trxDate BETWEEN CAST(:startDate AS timestamp) AND CAST(:endDate AS timestamp)")
    Page<Transaction> findByMultipleFilters(
            @Param("sender") String sender,
            @Param("recipient") String recipient,
            @Param("location") String location,
            @Param("flag") Boolean flag,
            @Param("minAmount") Double minAmount,
            @Param("maxAmount") Double maxAmount,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );

    //for Rules...
    @Query("SELECT t FROM Transaction t ORDER BY t.trxDate DESC LIMIT 1")
    Optional<Transaction> findLatestTransaction();

    @Query("SELECT t FROM Transaction t ORDER BY t.trxDate DESC LIMIT 1 OFFSET 1")
    Optional<Transaction> findPreviousTransaction();

    @Query("SELECT t FROM Transaction t WHERE t.trxDate >= :cutoffTime")
    List<Transaction> findTransactionsWithinOneHour(Date cutoffTime);
}