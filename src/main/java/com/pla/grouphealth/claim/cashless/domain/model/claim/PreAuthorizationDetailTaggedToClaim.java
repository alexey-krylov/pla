package com.pla.grouphealth.claim.cashless.domain.model.claim;

import com.pla.grouphealth.claim.cashless.domain.model.preauthorization.PreAuthorizationRequestAssuredDetail;
import com.pla.grouphealth.claim.cashless.domain.model.preauthorization.PreAuthorizationRequestCoverageDetail;
import com.pla.grouphealth.claim.cashless.domain.model.preauthorization.PreAuthorizationRequestPolicyDetail;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.util.Set;

import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Author - Mohan Sharma Created on 2/8/2016.
 */
@NoArgsConstructor
@Getter
public class PreAuthorizationDetailTaggedToClaim {
    private String preAuthorizationRequestId;
    private LocalDate preAuthorizationDate;
    private String policyNumber;
    private String policyName;
    private String planCode;
    private String planName;
    private Set<PreAuthorizationRequestCoverageDetail> coverageDetails;
    private PreAuthorizationRequestAssuredDetail assuredDetail;
    private BigDecimal totalApprovedAmount;
    private boolean tagToClaim;

    public PreAuthorizationDetailTaggedToClaim updateWithPreAuthorizationId(String preAuthorizationRequestId) {
        this.preAuthorizationRequestId = preAuthorizationRequestId;
        return this;
    }

    public PreAuthorizationDetailTaggedToClaim updateWithPreAuthorizationDate(LocalDate preAuthorizationDate) {
        this.preAuthorizationDate = preAuthorizationDate;
        return this;
    }

    public PreAuthorizationDetailTaggedToClaim updateWithPolicyRelatedDetails(PreAuthorizationRequestPolicyDetail preAuthorizationRequestPolicyDetail) {
        if(isNotEmpty(preAuthorizationRequestPolicyDetail)){
            this.policyNumber = preAuthorizationRequestPolicyDetail.getPolicyNumber();
            this.policyName = preAuthorizationRequestPolicyDetail.getPolicyName();
            this.planCode = preAuthorizationRequestPolicyDetail.getPlanCode();
            this.planName = preAuthorizationRequestPolicyDetail.getPlanName();
        }
        return this;
    }

    public PreAuthorizationDetailTaggedToClaim updateWithCoverageDetails(PreAuthorizationRequestPolicyDetail preAuthorizationRequestPolicyDetail) {
        if(isNotEmpty(preAuthorizationRequestPolicyDetail)){
            this.coverageDetails = preAuthorizationRequestPolicyDetail.getCoverageDetailDtoList();
            this.totalApprovedAmount = constructTotalApprovedAmount(preAuthorizationRequestPolicyDetail.getCoverageDetailDtoList());
        }
        return this;
    }

    private BigDecimal constructTotalApprovedAmount(Set<PreAuthorizationRequestCoverageDetail> coverageDetails) {
        BigDecimal totalApprovedAmount = BigDecimal.ZERO;
        if(isNotEmpty(coverageDetails)){
            for(PreAuthorizationRequestCoverageDetail preAuthorizationRequestCoverageDetail : coverageDetails){
                totalApprovedAmount = totalApprovedAmount.add(preAuthorizationRequestCoverageDetail.getApprovedAmount());
            }
        }
        return totalApprovedAmount;
    }

    public PreAuthorizationDetailTaggedToClaim updateWithAssuredDetails(PreAuthorizationRequestPolicyDetail preAuthorizationRequestPolicyDetail) {
        if(isNotEmpty(preAuthorizationRequestPolicyDetail)){
            this.assuredDetail = preAuthorizationRequestPolicyDetail.getAssuredDetail();
        }
        return this;
    }
}
