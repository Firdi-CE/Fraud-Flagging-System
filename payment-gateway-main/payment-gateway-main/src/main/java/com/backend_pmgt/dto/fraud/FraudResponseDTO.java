package com.backend_pmgt.dto.fraud;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class FraudResponseDTO {
    private Long fraudID;
    private String fraudAccount;
    private String fraudLabel;
    private String fraudDescription;
    private Date fraudDate;
}
