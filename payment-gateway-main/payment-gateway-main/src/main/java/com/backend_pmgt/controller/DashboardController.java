package com.backend_pmgt.controller;

import com.backend_pmgt.dto.dashboard.DashboardFraudResponseDTO;
import com.backend_pmgt.dto.dashboard.DashboardTrxResponseDTO;
import com.backend_pmgt.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/transaction")
    public ResponseEntity<List<DashboardTrxResponseDTO>> getDashboardTransaction() {
        return ResponseEntity.ok(dashboardService.getAllTransactionDashboard());
    }

    @GetMapping("/fraud")
    public ResponseEntity<List<DashboardFraudResponseDTO>> getDashboardFraud() {
        return ResponseEntity.ok(dashboardService.getAllFraudDashboard());
    }
}
