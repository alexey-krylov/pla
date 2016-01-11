package com.pla.grouphealth.claim.cashless.domain.model;

import com.google.common.collect.Sets;
import com.pla.grouphealth.claim.cashless.presentation.dto.AssuredDetail;
import com.pla.grouphealth.claim.cashless.presentation.dto.ClaimantPolicyDetailDto;
import com.pla.grouphealth.claim.cashless.presentation.dto.DependentAssuredDetail;
import com.pla.grouphealth.claim.cashless.presentation.dto.PreAuthorizationClaimantDetailCommand;
import lombok.*;
import org.hibernate.annotations.Immutable;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;
import org.nthdimenzion.utils.UtilValidator;

import javax.persistence.Embeddable;
import java.math.BigDecimal;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.nthdimenzion.utils.UtilValidator.*;

/**
 * Created by Mohan Sharma on 1/9/2016.
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

    public PreAuthorizationRequestPolicyDetail updateWithDetails(PreAuthorizationClaimantDetailCommand preAuthorizationClaimantDetailCommand) {
        ClaimantPolicyDetailDto claimantPolicyDetailDto = preAuthorizationClaimantDetailCommand.getClaimantPolicyDetailDto();
        if(isNotEmpty(claimantPolicyDetailDto)) {
            this.policyName = claimantPolicyDetailDto.getPolicyName();
            this.policyNumber = claimantPolicyDetailDto.getPolicyNumber();
            this.planCode = claimantPolicyDetailDto.getPlanCode();
            this.planName = claimantPolicyDetailDto.getPlanName();
            this.sumAssured = claimantPolicyDetailDto.getSumAssured();
            PreAuthorizationRequestAssuredDetail assuredDetail = isNotEmpty(this.assuredDetail) ? this.assuredDetail : new PreAuthorizationRequestAssuredDetail();
            this.assuredDetail = assuredDetail.updateWithAssuredDetails(claimantPolicyDetailDto);
            this.coverageDetailDtoList = constructPreAuthorizationRequestCoverageDetail(claimantPolicyDetailDto.getCoverageDetailDtoList());
        }
        return this;
    }

    private Set<PreAuthorizationRequestCoverageDetail> constructPreAuthorizationRequestCoverageDetail(Set<ClaimantPolicyDetailDto.CoverageDetailDto> coverageDetailDtoList) {
        return isNotEmpty(coverageDetailDtoList) ? coverageDetailDtoList.parallelStream().map(new Function<ClaimantPolicyDetailDto.CoverageDetailDto, PreAuthorizationRequestCoverageDetail>() {
            @Override
            public PreAuthorizationRequestCoverageDetail apply(ClaimantPolicyDetailDto.CoverageDetailDto coverageDetailDto) {
                return new PreAuthorizationRequestCoverageDetail(coverageDetailDto.getCoverageCode(), coverageDetailDto.getCoverageName(), coverageDetailDto.getSumAssured());
            }
        }).collect(Collectors.toSet()) : Sets.newHashSet();
    }
}
