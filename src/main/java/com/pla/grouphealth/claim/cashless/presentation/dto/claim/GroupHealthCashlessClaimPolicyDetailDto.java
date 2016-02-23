package com.pla.grouphealth.claim.cashless.presentation.dto.claim;

import com.google.common.collect.Sets;
import com.pla.grouphealth.claim.cashless.domain.model.claim.GroupHealthCashlessClaimAssuredDetail;
import com.pla.grouphealth.claim.cashless.domain.model.claim.GroupHealthCashlessClaimCoverageDetail;
import com.pla.grouphealth.claim.cashless.domain.model.claim.GroupHealthCashlessClaimPolicyDetail;
import com.pla.sharedkernel.domain.model.PolicyNumber;
import com.pla.sharedkernel.identifier.PlanId;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.beanutils.BeanUtils;
import org.nthdimenzion.utils.UtilValidator;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static org.nthdimenzion.utils.UtilValidator.*;

/**
 * Author - Mohan Sharma Created on 2/4/2016.
 */
@Getter
@Setter
@NoArgsConstructor
public class GroupHealthCashlessClaimPolicyDetailDto {
    private PolicyNumber policyNumber;
    private String policyName;
    private String planCode;
    private String planName;
    private BigDecimal sumAssured;
    private GroupHealthCashlessClaimAssuredDetail assuredDetail;
    private Set<GroupHealthCashlessClaimCoverageDetailDto> coverageDetails;
    private PlanId planId;

    public GroupHealthCashlessClaimPolicyDetailDto updateWithDetails(GroupHealthCashlessClaimPolicyDetail groupHealthCashlessClaimPolicyDetail) {
        if(isNotEmpty(groupHealthCashlessClaimPolicyDetail)) {
            this.policyNumber = groupHealthCashlessClaimPolicyDetail.getPolicyNumber();
            this.policyName = groupHealthCashlessClaimPolicyDetail.getPolicyName();
            this.planCode = groupHealthCashlessClaimPolicyDetail.getPlanCode();
            this.planName = groupHealthCashlessClaimPolicyDetail.getPlanName();
            this.sumAssured = groupHealthCashlessClaimPolicyDetail.getSumAssured();
            this.planId = groupHealthCashlessClaimPolicyDetail.getPlanId();
            this.assuredDetail = groupHealthCashlessClaimPolicyDetail.getAssuredDetail();
            this.coverageDetails = constructCoverageDetails(groupHealthCashlessClaimPolicyDetail.getCoverageDetails());
        }
        return this;
    }

    private Set<GroupHealthCashlessClaimCoverageDetailDto> constructCoverageDetails(Set<GroupHealthCashlessClaimCoverageDetail> coverageDetails) {
        return isNotEmpty(coverageDetails) ? coverageDetails.stream().map(coverage ->  new GroupHealthCashlessClaimCoverageDetailDto().updateWithDetails(coverage)).collect(Collectors.toSet()) : Sets.newHashSet();
    }

    public GroupHealthCashlessClaimPolicyDetailDto updateWithCoverages(Set<GroupHealthCashlessClaimCoverageDetailDto> coverageBenefitDetails) {
        this.coverageDetails = coverageBenefitDetails;
        return this;
    }

    public GroupHealthCashlessClaimPolicyDetailDto updateWithPolicyNumber(PolicyNumber policyNumber) {
        this.policyNumber = policyNumber;
        return this;
    }

    public GroupHealthCashlessClaimPolicyDetailDto updateWithPolicyName(String policyName) {
        this.policyName = policyName;
        return this;
    }

    public GroupHealthCashlessClaimPolicyDetailDto updateWithPlanName(String planName) {
        this.planName = planName;
        return this;
    }

    public GroupHealthCashlessClaimPolicyDetailDto updateWithPlanCode(String planCode) {
        this.planCode = planCode;
        return this;
    }

    public GroupHealthCashlessClaimPolicyDetailDto updateWithAssuredDetails(GroupHealthCashlessClaimAssuredDetail assuredDetail) {
        this.assuredDetail = assuredDetail;
        return this;
    }
}

