package com.backend_pmgt.repository;

import com.backend_pmgt.entity.AuditTrailsTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditTrailsTransactionRepository extends JpaRepository<AuditTrailsTransaction, Long> {
}
