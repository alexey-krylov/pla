package com.pla.grouplife.claim.presentation.dto;

import com.pla.sharedkernel.identifier.PlanId;
import lombok.*;
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

    private PlanId planId;;

    private String planName;

    private String planCode;

    private BigDecimal premiumAmount;

    private BigDecimal sumAssured;
}
