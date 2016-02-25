package com.pla.grouplife.claim.presentation.dto;

import com.pla.grouplife.claim.domain.model.ClaimantDetail;
import com.pla.sharedkernel.domain.model.ClaimType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.DateTime;

import java.util.List;
import java.util.Set;


/**
 * Created by ak on 5/1/2016.
 */
@Getter
@Setter
@NoArgsConstructor
public class GLClaimIntimationDetailsDto {
    private String claimNumber;
    private String claimId;
    private String policyId;
    private String policyNumber;
    private String schemeName;
    private String relationship;
    private String category;
    private ClaimType claimType;
    private DateTime claimIntimationDate;
    private DateTime claimIncidenceDate;
    private ClaimantDetail claimantDetail;
    private PlanDetailDto planDetail;
    private Set<CoverageDetailDto> coverageDetails;
    private BankDetailsDto bankDetails;
    private ClaimAssuredDetailDto claimAssuredDetail;
    private ClaimRegistrationDto claimRegistrationDetails;
    private ClaimDisabilityRegistrationDto disabilityRegistrationDetails;
    private ClaimApproverPlanDto claimApprovalPlanDetail;
    private List<ClaimApproverCoverageDetailDto> claimApprovalCoverageDetails;
    private ApprovalDetailsDto approvalDetails;
    private GLClaimSettlementDataDto claimSettlementDetails;
    private String routingLevel;

    public GLClaimIntimationDetailsDto withClaimant(ClaimantDetail claimantDetail){
        this. claimantDetail  =  claimantDetail;
        return this;
    }
    public GLClaimIntimationDetailsDto withPolicyId(String policyId){
        this.policyId  =  policyId;
        return this;
    }
    public GLClaimIntimationDetailsDto withAssuredDetail(ClaimAssuredDetailDto claimAssuredDetail){
        this.claimAssuredDetail = claimAssuredDetail;
        return this;
    }
    public GLClaimIntimationDetailsDto withPlanDetails(PlanDetailDto planDetail){
        this.planDetail = planDetail;
        return this;
    }
    public GLClaimIntimationDetailsDto withCoverageDetails(Set<CoverageDetailDto> coverageDetails){
        this.coverageDetails = coverageDetails;
        return this;
    }
    public GLClaimIntimationDetailsDto withBankDetails(BankDetailsDto bankDetails){
        this.bankDetails = bankDetails;
        return this;
    }
    public GLClaimIntimationDetailsDto withClaimRegistration(ClaimRegistrationDto claimRegistrationDetails){
        this.claimRegistrationDetails = claimRegistrationDetails;
        return this;
    }
    public GLClaimIntimationDetailsDto withDisabilityRegistration(ClaimDisabilityRegistrationDto disabilityRegistrationDetails){
        this.disabilityRegistrationDetails = disabilityRegistrationDetails;
        return this;
    }
    public GLClaimIntimationDetailsDto withApprovalDetail(ApprovalDetailsDto approvalDetails){
        this.approvalDetails = approvalDetails;
        return this;
    }
    public GLClaimIntimationDetailsDto withApprovalPlanDetailDetail(ClaimApproverPlanDto claimApprovalPlanDetail){
        this.claimApprovalPlanDetail = claimApprovalPlanDetail;
        return this;
    }
    public GLClaimIntimationDetailsDto withApprovalCoverageDetail(List<ClaimApproverCoverageDetailDto> claimApprovalCoverageDetails){
        this.claimApprovalCoverageDetails = claimApprovalCoverageDetails;
        return this;
    }
    public GLClaimIntimationDetailsDto withClaimSettlementDetail(GLClaimSettlementDataDto claimSettlementDetails){
        this.claimSettlementDetails=claimSettlementDetails;
        return this;
    }
}
