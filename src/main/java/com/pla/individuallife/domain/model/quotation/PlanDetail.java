package com.pla.individuallife.domain.model.quotation;

import com.pla.sharedkernel.identifier.PlanId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigInteger;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Created by Karunakar on 5/25/2015.
 */
@Entity
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
@Setter(value = AccessLevel.PACKAGE)
public class PlanDetail {

    @Id
    private String planDetailId;

    private PlanId planId;

    private Integer policyTerm;

    private Integer premiumPaymentTerm;

    private BigInteger sumAssured;

    @OneToMany(targetEntity = RiderDetail.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "palndetail_rider", joinColumns = @JoinColumn(name = "PLAN_DETAIL_ID"), inverseJoinColumns = @JoinColumn(name = "RIDER_DETAIL_ID"))
    private Set<RiderDetail> riderDetails;

    PlanDetail(PlanDetailBuilder planDetailBuilder) {
        checkArgument(planDetailBuilder != null);
        this.planDetailId = planDetailBuilder.getPlanDetailId();
        this.planId = planDetailBuilder.getPlanId();
        this.policyTerm = planDetailBuilder.getPolicyTerm();
        this.premiumPaymentTerm = planDetailBuilder.getPremiumPaymentTerm();
        this.sumAssured = planDetailBuilder.getSumAssured();
        this.riderDetails = planDetailBuilder.getRiderDetails();
    }

    public static PlanDetailBuilder getPlanDetailBuilder( String planDetailId, PlanId planId, Integer policyTerm, Integer premiumPaymentTerm, BigInteger sumAssured, Set<RiderDetail> riderDetails) {
        return new PlanDetailBuilder( planDetailId,  planId,  policyTerm,  premiumPaymentTerm,  sumAssured, riderDetails);
    }

}
