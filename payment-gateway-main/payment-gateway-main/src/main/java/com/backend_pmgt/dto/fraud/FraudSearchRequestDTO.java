package com.backend_pmgt.dto.fraud;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class FraudSearchRequestDTO {

    private String fraudAccount;
    private String fraudLabel;
    private Date fraudDateStart;
    private Date fraudDateEnd;

}
