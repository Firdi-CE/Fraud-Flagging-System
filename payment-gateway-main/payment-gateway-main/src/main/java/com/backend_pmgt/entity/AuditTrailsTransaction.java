package com.backend_pmgt.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.Date;

@Data
@Entity
public class AuditTrailsTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long logID;
    private String logAction;
    private String logDescription;
    private String logRequest;
    private String logResponse;
    private Date logDate;

}
