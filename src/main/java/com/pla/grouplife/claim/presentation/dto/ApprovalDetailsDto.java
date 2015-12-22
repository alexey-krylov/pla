package com.pla.grouplife.claim.presentation.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by ak on 21/12/2015.
 */

@Getter
@Setter
@NoArgsConstructor

public class ApprovalDetailsDto {

    String planName;
    List<String> coverageNames;
    BigDecimal planSumAssured;
    List<BigDecimal> coverageSumAssured;

}
