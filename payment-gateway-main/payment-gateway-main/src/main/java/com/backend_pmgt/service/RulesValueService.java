package com.backend_pmgt.service;

import com.backend_pmgt.dto.rules.RulesValueRequestDTO;
import com.backend_pmgt.dto.rules.RulesValueResponseDTO;
import com.backend_pmgt.entity.AuditTrailsRulesValue;
import com.backend_pmgt.entity.RulesValue;
import com.backend_pmgt.repository.AuditTrailsRulesValueRepository;
import com.backend_pmgt.repository.RulesValueRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class RulesValueService {

    @Autowired
    private RulesValueRepository rulesValueRepository;

    @Autowired
    AuditTrailsRulesValueRepository auditTrailsRulesValueRepository;

    ObjectMapper objectMapper = new ObjectMapper();

    public List<RulesValue> getAllRulesValues() {
        return rulesValueRepository.findAllByOrderByRulesIDAsc();
    }

    public RulesValue getRulesValueById(Long id) {
        Optional<RulesValue> rulesValue = rulesValueRepository.findById(id);
        return rulesValue.orElse(null); // Return null if not found
    }

    public RulesValueResponseDTO createRulesValue(int ruleType , RulesValueRequestDTO rulesValueRequestDTO) throws JsonProcessingException {
        RulesValue rulesValue = new RulesValue();
        rulesValue.setRulesType(ruleType);
        rulesValue.setRulesTimeRange(rulesValueRequestDTO.getRulesTimeRange());
        rulesValue.setRulesAmountLimit(rulesValueRequestDTO.getRulesAmountLimit());
        rulesValueRepository.save(rulesValue);

        RulesValueResponseDTO rulesValueResponseDTO = RulesValueResponseDTO.builder()
                .rulesID(rulesValue.getRulesID())
                .rulesType(rulesValue.getRulesType())
                .rulesTimeRange(rulesValue.getRulesTimeRange())
                .rulesAmountLimit(rulesValue.getRulesAmountLimit())
                .build();

        //Insert new Audit Trails
        AuditTrailsRulesValue auditTrailsRulesValue = new AuditTrailsRulesValue();
        auditTrailsRulesValue.setLogAction("Create Rules Value");
        auditTrailsRulesValue.setLogDescription("Add New Rules Value");
        auditTrailsRulesValue.setLogRequest(objectMapper.writeValueAsString(rulesValueRequestDTO));
        auditTrailsRulesValue.setLogResponse(objectMapper.writeValueAsString(rulesValueResponseDTO));
        auditTrailsRulesValue.setLogDate(new Date());

        auditTrailsRulesValueRepository.save(auditTrailsRulesValue);

        return rulesValueResponseDTO;
}

    public RulesValueResponseDTO updateRulesValue(String id, RulesValueRequestDTO rulesValueRequestDTO) throws JsonProcessingException {
        RulesValue rulesValue = rulesValueRepository.getReferenceById(Long.valueOf(id));
        int oldTimeRange=rulesValue.getRulesTimeRange();
        Double oldAmountLimit=rulesValue.getRulesAmountLimit();
        rulesValue.setRulesID(rulesValue.getRulesID());
        rulesValue.setRulesType(rulesValue.getRulesType());
        rulesValue.setRulesLabel(rulesValue.getRulesLabel());
        rulesValue.setRulesDesc(rulesValue.getRulesDesc());
        rulesValue.setRulesTimeRange(rulesValueRequestDTO.getRulesTimeRange());
        rulesValue.setRulesAmountLimit(rulesValueRequestDTO.getRulesAmountLimit());
        rulesValueRepository.save(rulesValue);

        RulesValueResponseDTO rulesValueResponseDTO = RulesValueResponseDTO.builder()
                .rulesID(rulesValue.getRulesID())
                .rulesType(rulesValue.getRulesType())
                .rulesTimeRange(rulesValue.getRulesTimeRange())
                .rulesAmountLimit(rulesValue.getRulesAmountLimit())
                .build();

        //Insert new Audit Trails
        AuditTrailsRulesValue auditTrailsRulesValue = new AuditTrailsRulesValue();
        auditTrailsRulesValue.setLogAction("Update Rules Value");
        auditTrailsRulesValue.setLogDescription("Updated new value for " + rulesValue.getRulesLabel());
        auditTrailsRulesValue.setLogRequest("T: "+oldTimeRange+", V: "+oldAmountLimit);
        auditTrailsRulesValue.setLogResponse("T: "+rulesValue.getRulesTimeRange()+", V: "+rulesValue.getRulesAmountLimit());
        auditTrailsRulesValue.setLogDate(new Date());

        auditTrailsRulesValueRepository.save(auditTrailsRulesValue);

        return rulesValueResponseDTO;
    }

    // Delete a rules value
    public void deleteRulesValue(Long id) {
        rulesValueRepository.deleteById(id);
    }
}