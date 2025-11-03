package com.backend_pmgt.controller;

import com.backend_pmgt.dto.rules.RulesValueRequestDTO;
import com.backend_pmgt.dto.rules.RulesValueResponseDTO;
import com.backend_pmgt.entity.RulesValue;
import com.backend_pmgt.service.RulesValueService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rulesvalues")
public class RulesValueController {

    @Autowired
    private RulesValueService rulesValueService;

    @GetMapping
    public ResponseEntity<List<RulesValue>> getAllRulesValues() {
        List<RulesValue> rulesValues = rulesValueService.getAllRulesValues();
        return ResponseEntity.ok(rulesValues);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RulesValue> getRulesValueById(@PathVariable Long id) {
        RulesValue rulesValue = rulesValueService.getRulesValueById(id);
        return ResponseEntity.ok(rulesValue);
    }

    @PostMapping
    public ResponseEntity<RulesValueResponseDTO> createRulesValue(@RequestBody RulesValueRequestDTO rulesValue, @RequestParam int ruleType) throws JsonProcessingException {
        RulesValueResponseDTO createdRulesValue = rulesValueService.createRulesValue(ruleType,rulesValue);
        return ResponseEntity.ok(createdRulesValue);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<RulesValueResponseDTO> updateRulesValue(@PathVariable String id ,@RequestBody RulesValueRequestDTO rulesValues) throws JsonProcessingException {
        return ResponseEntity.ok(rulesValueService.updateRulesValue(id, rulesValues));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRulesValue(@PathVariable Long id) {
        rulesValueService.deleteRulesValue(id);
        return ResponseEntity.noContent().build();
    }
}