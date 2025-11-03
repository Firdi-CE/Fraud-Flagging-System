package com.backend_pmgt.dto.rules;

import lombok.Data;

@Data
public class RulesLogicResultDTO {
    private Boolean flagResult;
    private int fraudID;
    private int fraudType;
}
