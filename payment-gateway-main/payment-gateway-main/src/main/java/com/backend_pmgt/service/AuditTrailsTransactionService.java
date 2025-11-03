package com.backend_pmgt.service;

import com.backend_pmgt.entity.AuditTrailsTransaction;
import com.backend_pmgt.repository.AuditTrailsTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AuditTrailsTransactionService {

    @Autowired
    private AuditTrailsTransactionRepository auditTrailsRepository;

    public List<AuditTrailsTransaction> getAllAuditTrails() {
        return auditTrailsRepository.findAll();
    }

    public AuditTrailsTransaction getAuditTrailById(Long id) {
        Optional<AuditTrailsTransaction> auditTrail = auditTrailsRepository.findById(id);
        return auditTrail.orElse(null); // Return null if not found
    }

    public AuditTrailsTransaction createAuditTrail(AuditTrailsTransaction auditTrails) {
        return auditTrailsRepository.save(auditTrails);
    }

    public AuditTrailsTransaction updateAuditTrail(Long id, AuditTrailsTransaction auditTrails) {
        auditTrails.setLogID(id); // Set the ID for the update
        return auditTrailsRepository.save(auditTrails);
    }

    public void deleteAuditTrail(Long id) {
        auditTrailsRepository.deleteById(id);
    }
}