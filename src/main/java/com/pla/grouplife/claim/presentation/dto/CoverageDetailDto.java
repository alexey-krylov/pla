package com.pla.grouplife.claim.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Created by nthdimensioncompany on 31/12/2015.
 */

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor

public class CoverageDetailDto {

    private String coverageCode;

    private String coverageId;

    private String coverageName;

    private BigDecimal sumAssured;

    private BigDecimal premium;
}
