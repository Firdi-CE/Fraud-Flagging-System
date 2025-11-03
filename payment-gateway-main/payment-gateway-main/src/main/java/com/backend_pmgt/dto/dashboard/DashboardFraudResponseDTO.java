package com.backend_pmgt.dto.dashboard;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardFraudResponseDTO {

    private Long fraudID;
    private String fraudAccount;
    private String fraudLabel;
    private String fraudDescription;

}
