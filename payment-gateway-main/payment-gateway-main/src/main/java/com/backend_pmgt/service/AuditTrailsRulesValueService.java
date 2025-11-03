package com.backend_pmgt.service;

import com.backend_pmgt.dto.audittrails.AuditTrailsResponseDTO;
import com.backend_pmgt.dto.audittrails.AuditTrailsRulesValueResponseAPI;
import com.backend_pmgt.entity.AuditTrailsRulesValue;
import com.backend_pmgt.repository.AuditTrailsRulesValueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AuditTrailsRulesValueService {

    @Autowired
    private AuditTrailsRulesValueRepository auditTrailsRepository;

    public AuditTrailsRulesValueResponseAPI getAllAuditTrails(int page, int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<AuditTrailsRulesValue> logrvPage = auditTrailsRepository.findAllByOrderByLogDateDesc(pageable);
        long totalCount = logrvPage.getTotalElements();

        List<AuditTrailsResponseDTO> logrvResponses = new ArrayList<>();

        for (AuditTrailsRulesValue logrv : logrvPage.getContent()) {
            AuditTrailsResponseDTO result = AuditTrailsResponseDTO.builder()
                    .logID(logrv.getLogID())
                    .logAction(logrv.getLogAction())
                    .logDescription(logrv.getLogDescription())
                    .logDate(logrv.getLogDate())
                    .logRequest(logrv.getLogRequest())
                    .logResponse(logrv.getLogResponse())
                    .build();
            logrvResponses.add(result);
        }

        return AuditTrailsRulesValueResponseAPI.builder().logrv(logrvResponses).totalCount(totalCount).build();

    }

}
