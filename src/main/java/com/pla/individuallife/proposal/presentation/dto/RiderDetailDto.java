package com.pla.individuallife.proposal.presentation.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Created by Karunakar on 02-07-2015.
 */
@Getter
@Setter
public class RiderDetailDto {

    private String coverageId;
    private BigDecimal sumAssured;
    private int coverTerm;
    private int waiverOfPremium;
    private String coverageName;

}
