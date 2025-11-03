package com.backend_pmgt.dto.transaction;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class TransactionResponseAPI {
    private List<TransactionResponseDTO> transactions;
    private long totalCount;
}
