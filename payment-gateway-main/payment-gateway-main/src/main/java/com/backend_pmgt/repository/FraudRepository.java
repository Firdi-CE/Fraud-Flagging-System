package com.backend_pmgt.repository;

import com.backend_pmgt.entity.Fraud;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FraudRepository extends JpaRepository<Fraud, Long> {
    Page<Fraud> findAllByOrderByFraudDateDesc(Pageable pageable);

    @Query("SELECT f FROM Fraud f " +
            "WHERE (COALESCE(:account,'') = '' OR f.fraudAccount = :account) " +
            "AND (COALESCE(:label,'') = '' OR f.fraudLabel = :label)")
    Page<Fraud> findByAccountAndLabel(
            @Param("account") String account,
            @Param("label") String label,
            Pageable pageable
    );
}
