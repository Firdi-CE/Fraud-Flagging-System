package com.backend_pmgt.repository;

import com.backend_pmgt.entity.AuditTrailsFraud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditTrailsFraudRepository extends JpaRepository<AuditTrailsFraud, Long> {
}
