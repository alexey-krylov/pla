package com.pla.grouphealth.claim.cashless.domain.model.claim;

import com.pla.core.domain.model.plan.PlanDetail;
import com.pla.grouphealth.sharedresource.model.vo.GHInsured;
import com.pla.grouphealth.sharedresource.model.vo.GHInsuredDependent;
import com.pla.sharedkernel.domain.model.PolicyNumber;
import com.pla.sharedkernel.identifier.PlanId;
import lombok.*;
import org.hibernate.annotations.Immutable;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

import javax.persistence.Embeddable;
import java.math.BigDecimal;
import java.util.Set;

import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Author - Mohan Sharma Created on 1/9/2016.
 */
@ValueObject
@Immutable
@Embeddable
@EqualsAndHashCode
@Getter
@Setter
public class GroupHealthCashlessClaimPolicyDetail {
    private PolicyNumber policyNumber;
    private String policyName;
    private String planCode;
    private String planName;
    private BigDecimal sumAssured;
    private GroupHealthCashlessClaimAssuredDetail assuredDetail;
    private Set<GroupHealthCashlessClaimCoverageDetail> coverageDetails;
    private PlanId planId;

    public GroupHealthCashlessClaimPolicyDetail updateWithPolicyNumber(PolicyNumber policyNumber) {
        this.policyNumber = policyNumber;
        return this;
    }

    public GroupHealthCashlessClaimPolicyDetail updateWithPolicyName(String schemeName) {
        this.policyName = schemeName;
        return this;
    }

    public GroupHealthCashlessClaimPolicyDetail updateWithSumAssured(BigDecimal sumAssured) {
        this.sumAssured = sumAssured;
        return this;
    }

    public GroupHealthCashlessClaimPolicyDetail updateWithPlanDetails(PlanDetail planDetail, PlanId planId) {
        if(isNotEmpty(planDetail)){
            this.planCode = planDetail.getPlanCode();
            this.planName = planDetail.getPlanName();
            this.planId = planId;
        }
        return this;
    }

    public GroupHealthCashlessClaimPolicyDetail updateWithCoverages(Set<GroupHealthCashlessClaimCoverageDetail> coverageDetails) {
        this.coverageDetails = coverageDetails;
        return this;
    }

    public GroupHealthCashlessClaimPolicyDetail updateWithAssuredDetails(GHInsured groupHealthInsured, String clientId) {
        this.assuredDetail = new GroupHealthCashlessClaimAssuredDetail().updateWithAssuredDetails(groupHealthInsured, clientId);
        return this;
    }

    public GroupHealthCashlessClaimPolicyDetail updateWithDependentAssuredDetail(GHInsuredDependent ghInsuredDependent, GHInsured groupHealthInsured, String clientId) {
        this.assuredDetail = new GroupHealthCashlessClaimAssuredDetail().updateWithAssuredDetailsForDependent(ghInsuredDependent, groupHealthInsured, clientId);
        return this;
    }
}