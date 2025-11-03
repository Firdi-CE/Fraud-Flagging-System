package com.backend_pmgt.dto.transaction;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransactionRequestDTO {

    private String trxPaymentMethod;
    private String trxLocationLatitude;
    private String trxLocationLongitude;
    private String trxAccountSender;
    private String trxAccountRecipient;
    private Double trxAmount;

}
