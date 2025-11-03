package com.backend_pmgt.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.Date;

@Entity
@Data
public class Fraud {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long fraudID;
    private String fraudAccount;
    private String fraudLabel;
    private String fraudDescription;
    private Date fraudDate;

}
