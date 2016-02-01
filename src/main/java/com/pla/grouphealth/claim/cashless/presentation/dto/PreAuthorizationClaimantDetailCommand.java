package com.pla.grouphealth.claim.cashless.presentation.dto;

import com.google.common.collect.Sets;
import com.pla.grouphealth.claim.cashless.domain.model.*;
import com.pla.publishedlanguage.dto.ClientDocumentDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.nthdimenzion.utils.UtilValidator;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.nthdimenzion.utils.UtilValidator.*;

/**
 * Author - Mohan Sharma Created on 1/6/2016.
 */
@Getter
@Setter
@NoArgsConstructor
public class PreAuthorizationClaimantDetailCommand {
    private String status;
    private String preAuthorizationRequestId;
    private String preAuthorizationId;
    private String batchNumber;
    private String claimType;
    private LocalDate claimIntimationDate;
    private LocalDate preAuthorizationDate;
    private ClaimantHCPDetailDto claimantHCPDetailDto;
    private ClaimantPolicyDetailDto claimantPolicyDetailDto;
    private List<DiagnosisTreatmentDto> diagnosisTreatmentDtos;
    private IllnessDetailDto illnessDetailDto;
    private List<DrugServiceDto> drugServicesDtos;
    private boolean submitEventFired;
    private boolean submitted;
    private String preAuthProcessorUserId;
    private Set<CommentDetail> commentDetails;
    private String batchUploaderUserId;
    private Set<ClientDocumentDto> additionalRequiredDocuments;

    public static PreAuthorizationClaimantDetailCommand getInstance() {
        return new PreAuthorizationClaimantDetailCommand();
    }

    public PreAuthorizationClaimantDetailCommand updateWithBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
        return this;
    }

    public PreAuthorizationClaimantDetailCommand updateWithPreAuthorizationId(PreAuthorizationId preAuthorizationId) {
        if(isNotEmpty(preAuthorizationId))
            this.preAuthorizationId = preAuthorizationId.getPreAuthorizationId();
        return this;
    }

    public PreAuthorizationClaimantDetailCommand updateWithPreAuthorizationDate(LocalDate batchDate) {
        this.preAuthorizationDate = batchDate;
        return this;
    }

    public PreAuthorizationClaimantDetailCommand updateWithClaimantHCPDetailDto(ClaimantHCPDetailDto claimantHCPDetailDto) {
        this.claimantHCPDetailDto = claimantHCPDetailDto;
        return this;
    }

    public PreAuthorizationClaimantDetailCommand updateWithClaimantPolicyDetailDto(ClaimantPolicyDetailDto claimantPolicyDetailDto) {
        this.claimantPolicyDetailDto = claimantPolicyDetailDto;
        return this;
    }

    public PreAuthorizationClaimantDetailCommand updateWithDiagnosisTreatment(List<DiagnosisTreatmentDto> diagnosisTreatmentDtos) {
        this.diagnosisTreatmentDtos = diagnosisTreatmentDtos;
        return this;
    }

    public PreAuthorizationClaimantDetailCommand updateWithIllnessDetails(IllnessDetailDto illnessDetailDto) {
        this.illnessDetailDto = illnessDetailDto;
        return this;
    }

    public PreAuthorizationClaimantDetailCommand updateWithDrugServices(List<DrugServiceDto> drugServiceDtos) {
        this.drugServicesDtos = drugServiceDtos;
        return this;
    }

    public PreAuthorizationClaimantDetailCommand updateWithPreAuthorizationRequestId(String preAuthorizationRequestId) {
        if(isNotEmpty(preAuthorizationRequestId))
            this.preAuthorizationRequestId = preAuthorizationRequestId;
        return this;
    }

    public PreAuthorizationClaimantDetailCommand updateWithClaimType(String claimType) {
        this.claimType = claimType;
        return this;
    }

    public PreAuthorizationClaimantDetailCommand updateWithClaimIntimationDate(LocalDate claimIntimationDate) {
        this.claimIntimationDate = claimIntimationDate;
        return this;
    }

    public PreAuthorizationClaimantDetailCommand updateWithPolicy(PreAuthorizationRequestPolicyDetail preAuthorizationRequestPolicyDetail) {
        if(isNotEmpty(preAuthorizationRequestPolicyDetail)){
            ClaimantPolicyDetailDto claimantPolicyDetailDto = new ClaimantPolicyDetailDto();
            claimantPolicyDetailDto.setPolicyNumber(preAuthorizationRequestPolicyDetail.getPolicyNumber());
            claimantPolicyDetailDto.setPolicyName(preAuthorizationRequestPolicyDetail.getPolicyName());
            if(isNotEmpty(preAuthorizationRequestPolicyDetail.getAssuredDetail())){
                AssuredDetail assuredDetail = new AssuredDetail();
                assuredDetail.setClientId(preAuthorizationRequestPolicyDetail.getAssuredDetail().getClientId());
                claimantPolicyDetailDto.setAssuredDetail(assuredDetail);
            }
            this.claimantPolicyDetailDto = claimantPolicyDetailDto;
        }
        return this;
    }

    public PreAuthorizationClaimantDetailCommand updateWithHcp(PreAuthorizationRequestHCPDetail preAuthorizationRequestHCPDetail) {
        if(isNotEmpty(preAuthorizationRequestHCPDetail)){
            ClaimantHCPDetailDto claimantHCPDetailDto = new ClaimantHCPDetailDto();
            claimantHCPDetailDto.setHcpName(preAuthorizationRequestHCPDetail.getHcpName());
            claimantHCPDetailDto.setHcpCode(preAuthorizationRequestHCPDetail.getHcpCode());
            this.claimantHCPDetailDto = claimantHCPDetailDto;
        }
        return this;
    }

    public PreAuthorizationClaimantDetailCommand updateWithStatus(PreAuthorizationRequest.Status status) {
        if(isNotEmpty(status))
            this.status = status.getDescription();
        return this;
    }

    public PreAuthorizationClaimantDetailCommand updateWithSubmittedFlag(boolean submitted) {
        this.submitted = submitted;
        return this;
    }

    public PreAuthorizationClaimantDetailCommand updateWithProcessorUserId(String preAuthorizationProcessorUserId) {
        if(isNotEmpty(preAuthorizationProcessorUserId))
            this.preAuthProcessorUserId = preAuthorizationProcessorUserId;
        return this;
    }

    public PreAuthorizationClaimantDetailCommand updateWithComments(Set<CommentDetail> commentDetails) {
        this.commentDetails = commentDetails;
        return this;
    }

    public PreAuthorizationClaimantDetailCommand updateWithBatchUploaderUserId(String batchUploaderUserId) {
        this.batchUploaderUserId = batchUploaderUserId;
        return this;
    }

    public BigDecimal getSumOfAllProbableClaimAmount() {
        BigDecimal sumOfAllProbableClaimAmount = BigDecimal.ZERO;
        if(isNotEmpty(this.getClaimantPolicyDetailDto())) {
            Set<CoverageBenefitDetailDto> coverageDetails = this.getClaimantPolicyDetailDto().getCoverageBenefitDetails();
            if(isNotEmpty(coverageDetails)){
                for(CoverageBenefitDetailDto coverageBenefitDetailDto :  coverageDetails){
                    Set<BenefitDetailDto> benefitDetails = coverageBenefitDetailDto.getBenefitDetails();
                    if(isNotEmpty(benefitDetails)){
                        for(BenefitDetailDto benefitDetail : benefitDetails){
                            sumOfAllProbableClaimAmount = sumOfAllProbableClaimAmount.add(benefitDetail.getProbableClaimAmount());
                        }
                    }
                }
            }
        }
        return sumOfAllProbableClaimAmount;
    }

    public int getAgeOfTheClient() {
        return isNotEmpty(this.getClaimantPolicyDetailDto()) ? isNotEmpty(this.getClaimantPolicyDetailDto().getAssuredDetail()) ?  this.getClaimantPolicyDetailDto().getAssuredDetail().getAgeNextBirthday() : isNotEmpty(this.claimantPolicyDetailDto.getDependentAssuredDetail()) ? this.claimantPolicyDetailDto.getDependentAssuredDetail().getAgeNextBirthday() : 0 : 0;
    }

    public PreAuthorizationClaimantDetailCommand updateWithAdditionalRequiredDocuments(Set<AdditionalDocument> additionalRequiredDocumentsByUnderwriter) {
        this.additionalRequiredDocuments = isNotEmpty(additionalRequiredDocumentsByUnderwriter) ? additionalRequiredDocumentsByUnderwriter.stream().map(document -> new ClientDocumentDto(document.getDocumentCode(), document.getDocumentName(), Boolean.FALSE)).collect(Collectors.toSet()): Sets.newHashSet();
        return this;
    }
}
