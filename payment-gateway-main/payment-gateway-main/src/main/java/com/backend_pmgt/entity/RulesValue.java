package com.backend_pmgt.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class RulesValue {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long rulesID;
    private Integer rulesType;
    private int rulesTimeRange;
    private Double rulesAmountLimit;
    private String rulesDesc;
    private String rulesLabel;

}
