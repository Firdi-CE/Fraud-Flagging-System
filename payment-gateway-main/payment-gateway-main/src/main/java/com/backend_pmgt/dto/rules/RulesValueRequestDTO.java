package com.backend_pmgt.dto.rules;

import lombok.Data;

@Data
public class RulesValueRequestDTO {
    private int rulesTimeRange;
    private Double rulesAmountLimit;
}
