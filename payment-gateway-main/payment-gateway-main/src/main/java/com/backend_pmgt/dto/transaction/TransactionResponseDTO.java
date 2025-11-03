package com.backend_pmgt.dto.transaction;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class TransactionResponseDTO {

    private Long trxID;
    private String trxPaymentMethod;
    private String trxLocation;
    private String trxAccountSender;
    private String trxAccountRecipient;
    private Double trxAmount;
    private Date trxDate;
    private Boolean trxFlag;
    private String trxDesc;

}
