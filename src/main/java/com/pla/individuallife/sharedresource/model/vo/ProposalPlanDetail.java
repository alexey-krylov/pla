package com.pla.individuallife.sharedresource.model.vo;

import com.google.common.collect.Sets;
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

    private String premiumPaymentType;

    private Integer premiumPaymentTerm;

    private BigDecimal sumAssured;

    private Set<ILRiderDetail> riderDetails= Sets.newLinkedHashSet();

    private BigDecimal annualPolicyFee  = BigDecimal.ZERO;
    private BigDecimal semiAnnualFee  = BigDecimal.ZERO;
    private BigDecimal quarterlyFee  = BigDecimal.ZERO;
    private BigDecimal monthlyFee  = BigDecimal.ZERO;

    public ProposalPlanDetail updateWithPolicyFee(BigDecimal annualPolicyFee,BigDecimal semiAnnualFee,BigDecimal quarterlyFee,BigDecimal monthlyFee){
        this.annualPolicyFee  = annualPolicyFee;
        this.semiAnnualFee  = semiAnnualFee;
        this.quarterlyFee  = quarterlyFee;
        this.monthlyFee = monthlyFee;
        return this;
    }

}
