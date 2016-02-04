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

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Set;

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
}

