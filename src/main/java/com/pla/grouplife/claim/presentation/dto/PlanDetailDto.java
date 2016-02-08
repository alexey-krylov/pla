package com.pla.grouplife.claim.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

import java.math.BigDecimal;

/**
 * Created by nthdimensioncompany on 31/12/2015.
 */
@ValueObject
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class PlanDetailDto {

    private String planId;;

    private String planName;

    private String planCode;

    private BigDecimal premiumAmount;

    private BigDecimal sumAssured;
}
