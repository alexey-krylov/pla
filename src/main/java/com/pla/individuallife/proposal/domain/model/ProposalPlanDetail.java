package com.pla.individuallife.proposal.domain.model;

import com.pla.individuallife.quotation.presentation.dto.RiderDetailDto;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Set;

/**
 * Created by pradyumna on 22-05-2015.
 */
@Getter
public class ProposalPlanDetail {

    private String planId;

    private Integer policyTerm;

    private Integer premiumPaymentTerm;

    private BigDecimal sumAssured;

    private Set<RiderDetailDto> riderDetails;

}
