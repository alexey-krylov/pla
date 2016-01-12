package com.pla.grouphealth.claim.cashless.presentation.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.util.List;

/**
 * Author - Mohan Sharma Created on 1/6/2016.
 */
@Getter
@Setter
@NoArgsConstructor
public class PreAuthorizationClaimantDetailCommand {
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

    public static PreAuthorizationClaimantDetailCommand getInstance() {
        return new PreAuthorizationClaimantDetailCommand();
    }

    public PreAuthorizationClaimantDetailCommand updateWithBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
        return this;
    }

    public PreAuthorizationClaimantDetailCommand updateWithPreAuthorizationId(String preAuthorizationId) {
        this.preAuthorizationId = preAuthorizationId;
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
}
