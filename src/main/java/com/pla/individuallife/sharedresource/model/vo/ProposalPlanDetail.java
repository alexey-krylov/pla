package com.pla.individuallife.sharedresource.model.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Set;

/**
 * Created by pradyumna on 22-05-2015.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProposalPlanDetail {

    private String planId;

    private String planName;

    private Integer policyTerm;

    private Integer premiumPaymentTerm;

    private BigDecimal sumAssured;

    private Set<ILRiderDetail> riderDetails;

}
