package com.backend_pmgt.dto.audittrails;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AuditTrailsRulesValueResponseAPI {
    private List<AuditTrailsResponseDTO> logrv;
    private long totalCount;
}
