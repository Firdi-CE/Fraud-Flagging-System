package com.backend_pmgt.repository;

import com.backend_pmgt.entity.AuditTrailsRulesValue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AuditTrailsRulesValueRepository extends JpaRepository<AuditTrailsRulesValue, Long> {
    Page<AuditTrailsRulesValue> findAllByOrderByLogDateDesc(Pageable pageable);
}
