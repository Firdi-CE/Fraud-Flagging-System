package com.backend_pmgt.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.Date;

@Entity
@Data
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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

