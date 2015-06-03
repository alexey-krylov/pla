package com.pla.individuallife.quotation.presentation.dto;

import com.pla.individuallife.quotation.domain.model.RiderDetail;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.util.Set;

/**
 * Created by Karunakar on 5/26/2015.
 */
@Getter
@Setter
@NoArgsConstructor
public class PlanDetailDto {

    private String planId;

    private Integer policyTerm;

    private Integer premiumPaymentTerm;

    private BigInteger sumAssured;

    private Set<RiderDetail> riderDetails;
}
