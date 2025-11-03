package com.backend_pmgt.dto.transaction;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class SearchTransactionRequestDTO {

    private Double trxAmountMin;
    private Double trxAmountMax;
    private Date trxDateStart;
    private Date trxDateEnd;
    private String trxAccountSender;
    private String trxAccountRecipient;
    private String trxPaymentMethod;
    private Boolean trxFlag;

}
