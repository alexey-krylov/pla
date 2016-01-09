package com.pla.grouphealth.claim.cashless.presentation.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.DateTime;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Created by Mohan Sharma on 1/6/2016.
 */
@Getter
@Setter
@NoArgsConstructor
public class PreAuthorizationClaimantDetailCommand {
    private String preAuthorizationId;
    private String batchNumber;
    private String claimType;
    private DateTime claimIntimationDate;
    private ClaimantHCPDetailDto claimantHCPDetailDto;
    private ClaimantPolicyDetailDto claimantPolicyDetailDto;
    private DateTime preAuthorizationDate;
    private List<DiagnosisTreatmentDto> diagnosisTreatmentDtos;
    private IllnessDetailDto illnessDetailDto;
    private List<DrugServiceDto> drugServicesDtos;
    private List<MultipartFile> documents;
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

    public PreAuthorizationClaimantDetailCommand updateWithPreAuthorizationDate(DateTime batchDate) {
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
