package com.backend_pmgt.controller;


import com.backend_pmgt.dto.audittrails.AuditTrailsRulesValueResponseAPI;
import com.backend_pmgt.service.AuditTrailsRulesValueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/logrulesvalue")
public class AuditTrailsRulesValueController {

    @Autowired
    private AuditTrailsRulesValueService auditTrailsService;

    @GetMapping
    public ResponseEntity<AuditTrailsRulesValueResponseAPI> getAllAuditTrails(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue="5") int size ) {
        AuditTrailsRulesValueResponseAPI auditTrails = auditTrailsService.getAllAuditTrails(page, size);
        return ResponseEntity.ok(auditTrails);
    }

}