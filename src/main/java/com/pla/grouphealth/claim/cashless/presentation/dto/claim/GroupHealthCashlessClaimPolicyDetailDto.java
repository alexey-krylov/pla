package com.pla.grouphealth.claim.cashless.presentation.dto.claim;

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
    private Set<GroupHealthCashlessClaimCoverageDetail> coverageDetails;
    private PlanId planId;

    public GroupHealthCashlessClaimPolicyDetailDto updateWithDetails(GroupHealthCashlessClaimPolicyDetail groupHealthCashlessClaimPolicyDetail) {
        try {
            BeanUtils.copyProperties(this, groupHealthCashlessClaimPolicyDetail);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return this;
    }

    public GroupHealthCashlessClaimPolicyDetailDto updateWithCoverages(Set<GroupHealthCashlessClaimCoverageDetail> coverageBenefitDetails) {
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

    public GroupHealthCashlessClaimPolicyDetailDto updateWithClientId(GroupHealthCashlessClaimAssuredDetail assuredDetail) {
        if(isNotEmpty(assuredDetail)){
            this.assuredDetail = constructClientIdFromGroupHealthCashlessClaimAssuredDetail(assuredDetail);
        }
        return this;
    }

    private GroupHealthCashlessClaimAssuredDetail constructClientIdFromGroupHealthCashlessClaimAssuredDetail(GroupHealthCashlessClaimAssuredDetail assuredDetail) {
        return new GroupHealthCashlessClaimAssuredDetail().updateWithCLientId(assuredDetail.getClientId());
    }

    public GroupHealthCashlessClaimPolicyDetailDto updateWithPlanName(String planName) {
        this.planName = planName;
        return this;
    }

    public GroupHealthCashlessClaimPolicyDetailDto updateWithPlanCode(String planCode) {
        this.planCode = planCode;
        return this;
    }
}

