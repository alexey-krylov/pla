package com.pla.individuallife.proposal.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Created by Admin on 7/30/2015.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ILRiderDetail {
    private String coverageId;
    private BigDecimal sumAssured;
    private Integer coverTerm;
    private Integer waiverOfPremium;
    private String coverageName;

}
