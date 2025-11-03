package com.backend_pmgt.dto.fraud;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class FraudResponseAPI {
    private List<FraudResponseDTO> frauds;
    private long totalCount;
}


