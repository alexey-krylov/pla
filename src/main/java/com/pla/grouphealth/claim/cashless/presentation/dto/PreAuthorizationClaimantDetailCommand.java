package com.pla.grouphealth.claim.cashless.presentation.dto;

import com.pla.grouphealth.claim.cashless.domain.model.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.nthdimenzion.utils.UtilValidator;

import java.util.List;

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
}
