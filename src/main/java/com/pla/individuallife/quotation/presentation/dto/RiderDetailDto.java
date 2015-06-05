package com.pla.individuallife.quotation.presentation.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

/**
 * Created by pradyumna on 05-06-2015.
 */
@Getter
@Setter
public class RiderDetailDto {

    private String coverageId;
    private BigInteger sumAssured;
    private Integer coverTerm;
    private Integer waiverOfPremium;
    private String coverageName;

}
