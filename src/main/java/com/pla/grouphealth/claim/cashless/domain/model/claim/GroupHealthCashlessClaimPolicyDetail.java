package com.pla.grouphealth.claim.cashless.domain.model.claim;

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
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor
@Getter
@Setter
public class GroupHealthCashlessClaimPolicyDetail {
    private String policyNumber;
    private String policyName;
    private String planCode;
    private String planName;
    private BigDecimal sumAssured;
    private GroupHealthCashlessClaimAssuredDetail assuredDetail;
    private Set<GroupHealthCashlessClaimCoverageDetail> coverageDetails;
    private PlanId planId;

    /*public GHCashlessClaimPolicyDetail updateWithDetails(PreAuthorizationClaimantDetailCommand preAuthorizationClaimantDetailCommand) {
        ClaimantPolicyDetailDto claimantPolicyDetailDto = preAuthorizationClaimantDetailCommand.getClaimantPolicyDetailDto();
        if(isNotEmpty(claimantPolicyDetailDto)) {
            this.policyName = claimantPolicyDetailDto.getPolicyName();
            this.policyNumber = claimantPolicyDetailDto.getPolicyNumber();
            this.planCode = claimantPolicyDetailDto.getPlanCode();
            if(isNotEmpty(claimantPolicyDetailDto.getPlanId()))
                this.planId = new PlanId(claimantPolicyDetailDto.getPlanId());
            this.planName = claimantPolicyDetailDto.getPlanName();
            this.sumAssured = claimantPolicyDetailDto.getSumAssured();
            PreAuthorizationRequestAssuredDetail assuredDetail = isNotEmpty(this.assuredDetail) ? this.assuredDetail : new PreAuthorizationRequestAssuredDetail();
            this.assuredDetail = assuredDetail.updateWithAssuredDetails(claimantPolicyDetailDto);
            this.coverageDetailDtoList = constructPreAuthorizationRequestCoverageDetail(claimantPolicyDetailDto.getCoverageBenefitDetails());
        }
        return this;
    }*/

   /* private Set<PreAuthorizationRequestCoverageDetail> constructPreAuthorizationRequestCoverageDetail(Set<CoverageBenefitDetailDto> benefitDetails) {
        return isNotEmpty(benefitDetails) ? benefitDetails.parallelStream().map(new Function<CoverageBenefitDetailDto, PreAuthorizationRequestCoverageDetail>() {
            @Override
            public PreAuthorizationRequestCoverageDetail apply(CoverageBenefitDetailDto coverageBenefitDetailDto) {
                PreAuthorizationRequestCoverageDetail preAuthorizationRequestCoverageDetail = new PreAuthorizationRequestCoverageDetail();
                preAuthorizationRequestCoverageDetail.updateWithCoverageDetails(coverageBenefitDetailDto);
                *//*
                * Unable to do deep copying
                * *//*
                //BeanUtils.copyProperties(preAuthorizationRequestCoverageDetail, coverageBenefitDetailDto);
                return preAuthorizationRequestCoverageDetail;
            }
        }).collect(Collectors.toSet()) : Sets.newHashSet();
    }*/
}
