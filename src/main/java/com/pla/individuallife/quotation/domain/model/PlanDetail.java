package com.pla.individuallife.quotation.domain.model;

import com.pla.sharedkernel.identifier.PlanId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import java.math.BigInteger;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Created by Karunakar on 5/25/2015.
 */
@Embeddable
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
public class PlanDetail {

    @Embedded
    private PlanId planId;

    private Integer policyTerm;

    private Integer premiumPaymentTerm;

    private BigInteger sumAssured;

    @Embedded
    @ElementCollection
    private Set<RiderDetail> riderDetails;

    PlanDetail(PlanDetailBuilder planDetailBuilder) {
        checkArgument(planDetailBuilder != null);
        this.planId = planDetailBuilder.getPlanId();
        this.policyTerm = planDetailBuilder.getPolicyTerm();
        this.premiumPaymentTerm = planDetailBuilder.getPremiumPaymentTerm();
        this.sumAssured = planDetailBuilder.getSumAssured();
        this.riderDetails = planDetailBuilder.getRiderDetails();
    }

    public static PlanDetailBuilder planDetailBuilder() {
        return new PlanDetailBuilder();
    }

}
