package com.backend_pmgt.controller;

import com.backend_pmgt.dto.fraud.FraudResponseAPI;
import com.backend_pmgt.dto.fraud.FraudSearchRequestDTO;
import com.backend_pmgt.service.FraudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/frauds")
public class FraudController {

    @Autowired
    private FraudService fraudService;

    @GetMapping
    public ResponseEntity<FraudResponseAPI> getFraudAll(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue="5") int size) {
        FraudResponseAPI fraudResponseAPI = fraudService.getAllFraud(page, size);
        return ResponseEntity.ok(fraudResponseAPI);
    }

    @PostMapping("/search")
    public ResponseEntity<FraudResponseAPI> searchFraud(@RequestBody FraudSearchRequestDTO fraudSearchRequestDTO, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue="2") int size) {
        FraudResponseAPI fraudResponseAPI = fraudService.searchFraud(fraudSearchRequestDTO, page, size);
        return ResponseEntity.ok(fraudResponseAPI);
    }

    public ResponseEntity<FraudResponseAPI> searchFraudLabelAccount(@RequestBody FraudSearchRequestDTO fraudSearchRequestDTO, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue="2") int size) {
        FraudResponseAPI fraudResponseAPI = fraudService.getFraudByAccountAndOrLabel(fraudSearchRequestDTO.getFraudAccount(), fraudSearchRequestDTO.getFraudLabel(), page, size);
        return ResponseEntity.ok(fraudResponseAPI);
    }


}
