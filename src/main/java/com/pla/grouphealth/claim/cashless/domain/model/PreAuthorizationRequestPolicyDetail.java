package com.pla.grouphealth.claim.cashless.domain.model;

import com.google.common.collect.Sets;
import com.pla.grouphealth.claim.cashless.presentation.dto.*;
import com.pla.sharedkernel.identifier.PlanId;
import lombok.*;
import org.apache.commons.beanutils.BeanUtils;
import org.hibernate.annotations.Immutable;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;
import org.nthdimenzion.utils.UtilValidator;

import javax.persistence.Embeddable;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.nthdimenzion.utils.UtilValidator.*;

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
public class PreAuthorizationRequestPolicyDetail {
    private String policyNumber;
    private String policyName;
    private String planCode;
    private String planName;
    private BigDecimal sumAssured;
    private PreAuthorizationRequestAssuredDetail assuredDetail;
    private Set<PreAuthorizationRequestCoverageDetail> coverageDetailDtoList;
    private PlanId planId;

    public PreAuthorizationRequestPolicyDetail updateWithDetails(PreAuthorizationClaimantDetailCommand preAuthorizationClaimantDetailCommand) {
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
    }

    private Set<PreAuthorizationRequestCoverageDetail> constructPreAuthorizationRequestCoverageDetail(Set<CoverageBenefitDetailDto> benefitDetails) {
        return isNotEmpty(benefitDetails) ? benefitDetails.parallelStream().map(new Function<CoverageBenefitDetailDto, PreAuthorizationRequestCoverageDetail>() {
            @Override
            public PreAuthorizationRequestCoverageDetail apply(CoverageBenefitDetailDto coverageBenefitDetailDto) {
                PreAuthorizationRequestCoverageDetail preAuthorizationRequestCoverageDetail = new PreAuthorizationRequestCoverageDetail();
                preAuthorizationRequestCoverageDetail.updateWithCoverageDetails(coverageBenefitDetailDto);
                /*
                * Unable to do deep copying
                * */
                //BeanUtils.copyProperties(preAuthorizationRequestCoverageDetail, coverageBenefitDetailDto);
                return preAuthorizationRequestCoverageDetail;
            }
        }).collect(Collectors.toSet()) : Sets.newHashSet();
    }
}
