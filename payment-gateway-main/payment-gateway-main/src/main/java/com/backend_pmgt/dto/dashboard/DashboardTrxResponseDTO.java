package com.backend_pmgt.dto.dashboard;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class DashboardTrxResponseDTO {
    private Long trxId;
    private String trxPaymentMethod;
    private String trxAccountSender;
    private Double trxAmount;
    private String trxAccountRecipient;
    private Date trxDate;
    private String trxLocation;
    private Boolean trxFlag;
}
