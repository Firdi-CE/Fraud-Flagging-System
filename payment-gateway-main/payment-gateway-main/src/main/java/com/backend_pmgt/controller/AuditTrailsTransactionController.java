package com.backend_pmgt.controller;


import com.backend_pmgt.entity.AuditTrailsTransaction;
import com.backend_pmgt.service.AuditTrailsTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/logtransaction")
public class AuditTrailsTransactionController {

    @Autowired
    private AuditTrailsTransactionService auditTrailsService;

    @GetMapping
    public ResponseEntity<List<AuditTrailsTransaction>> getAllAuditTrails() {
        List<AuditTrailsTransaction> auditTrails = auditTrailsService.getAllAuditTrails();
        return ResponseEntity.ok(auditTrails);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuditTrailsTransaction> getAuditTrailById(@PathVariable Long id) {
        AuditTrailsTransaction auditTrail = auditTrailsService.getAuditTrailById(id);
        return ResponseEntity.ok(auditTrail);
    }

    @PostMapping
    public ResponseEntity<AuditTrailsTransaction> createAuditTrail(@RequestBody AuditTrailsTransaction auditTrails) {
        AuditTrailsTransaction createdAuditTrail = auditTrailsService.createAuditTrail(auditTrails);
        return ResponseEntity.ok(createdAuditTrail);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AuditTrailsTransaction> updateAuditTrail(@PathVariable Long id, @RequestBody AuditTrailsTransaction auditTrails) {
        AuditTrailsTransaction updatedAuditTrail = auditTrailsService.updateAuditTrail(id, auditTrails);
        return ResponseEntity.ok(updatedAuditTrail);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAuditTrail(@PathVariable Long id) {
        auditTrailsService.deleteAuditTrail(id);
        return ResponseEntity.noContent().build();
    }
}