package com.backend_pmgt.dto.audittrails;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class AuditTrailsDTO {
    private String logAction;
    private String logDescription;
    private String logRequest;
    private String logResponse;
    private Date logDate;
}
