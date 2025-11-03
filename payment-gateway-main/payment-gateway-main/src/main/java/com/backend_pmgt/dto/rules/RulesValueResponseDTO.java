package com.backend_pmgt.dto.rules;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RulesValueResponseDTO {
    private Long rulesID;
    private Integer rulesType;
    private int rulesTimeRange;
    private Double rulesAmountLimit;
    private String rulesDesc;
    private String rulesLabel;
}
