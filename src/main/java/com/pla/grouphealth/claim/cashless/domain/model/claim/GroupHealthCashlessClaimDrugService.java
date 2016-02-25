package com.pla.grouphealth.claim.cashless.domain.model.claim;

import com.pla.core.hcp.domain.model.HCPServiceDetail;
import com.pla.grouphealth.claim.cashless.presentation.dto.claim.GroupHealthCashlessClaimDrugServiceDto;
import com.pla.grouphealth.claim.cashless.presentation.dto.preauthorization.ClaimUploadedExcelDataDto;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Immutable;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

import javax.persistence.Embeddable;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Author - Mohan Sharma Created on 1/9/2016.
 */
@ValueObject
@Immutable
@Embeddable
@EqualsAndHashCode
@Getter
@Setter
public class GroupHealthCashlessClaimDrugService {
    private String type;
    private String serviceName;
    private String drugName;
    private String drugType;
    private String drugDosage;
    private String accommodationType;
    private String duration;
    private int lengthOfStay;
    private String strength;
    private Status status;
    private BigDecimal billAmount;

    public GroupHealthCashlessClaimDrugService updateWithDetails(ClaimUploadedExcelDataDto claimUploadedExcelDataDto) {
        if(isNotEmpty(claimUploadedExcelDataDto)) {
            this.type = claimUploadedExcelDataDto.getType();
            this.serviceName = claimUploadedExcelDataDto.getService();
            this.drugName = claimUploadedExcelDataDto.getDiagnosisTreatmentDrugName();
            this.drugType = claimUploadedExcelDataDto.getDiagnosisTreatmentDrugType();
            this.accommodationType = claimUploadedExcelDataDto.getDiagnosisTreatmentSurgeryAccommodationType();
            this.duration = claimUploadedExcelDataDto.getDiagnosisTreatmentMedicalDuration();
            this.lengthOfStay = claimUploadedExcelDataDto.getDiagnosisTreatmentSurgeryLengthOStay();
            this.strength = claimUploadedExcelDataDto.getDiagnosisTreatmentDrugStrength();
            this.billAmount = claimUploadedExcelDataDto.getBillAmount();
            this.drugDosage = claimUploadedExcelDataDto.getDiagnosisTreatmentDrugDosage();
            this.status = isNotEmpty(claimUploadedExcelDataDto.getStatus()) ? Status.valueOf(claimUploadedExcelDataDto.getStatus()) : Status.PROCESS;
        }
        return this;
    }

    public GroupHealthCashlessClaimDrugService updateDetails(GroupHealthCashlessClaimDrugServiceDto drugServiceDto) {
        if(isNotEmpty(drugServiceDto)) {
            this.type = drugServiceDto.getType();
            this.serviceName = drugServiceDto.getServiceName();
            this.drugName = drugServiceDto.getDrugName();
            this.drugType = drugServiceDto.getDrugType();
            this.drugDosage = drugServiceDto.getDrugDosage();
            this.accommodationType = drugServiceDto.getAccommodationType();
            this.duration = drugServiceDto.getDuration();
            this.lengthOfStay = drugServiceDto.getLengthOfStay();
            this.strength = drugServiceDto.getStrength();
            this.billAmount = drugServiceDto.getBillAmount();
            this.status = isNotEmpty(drugServiceDto.getStatus()) ? Status.valueOf(drugServiceDto.getStatus()) : Status.PROCESS;
        }
        return this;
    }

    public BigDecimal getAgreedAmountForTheService(Set<HCPServiceDetail> serviceDetails) {
        Optional<HCPServiceDetail> hcpServiceDetailOptional = serviceDetails.stream().filter(HCPServiceDetail -> HCPServiceDetail.getServiceAvailed().equals(this.serviceName)).findFirst();
        if(hcpServiceDetailOptional.isPresent())
            return hcpServiceDetailOptional.get().getNormalAmount();
        return BigDecimal.ZERO;
    }

    public enum Status{
        IGNORE, PROCESS;

        public static List<String> getStatusList() {
            return Arrays.stream(values()).map(Status::name).collect(Collectors.toList());
        }
    }
}
