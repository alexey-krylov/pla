package com.pla.individuallife.quotation.presentation.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Created by pradyumna on 05-06-2015.
 */
@Getter
@Setter
public class RiderDetailDto {

    private String coverageId;
    private BigDecimal sumAssured;
    private Integer coverTerm;
    private Integer waiverOfPremium;
    private String coverageName;

}
