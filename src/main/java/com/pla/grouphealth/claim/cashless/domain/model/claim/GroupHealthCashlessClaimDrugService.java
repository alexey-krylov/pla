package com.pla.grouphealth.claim.cashless.domain.model.claim;

import com.pla.grouphealth.claim.cashless.presentation.dto.preauthorization.ClaimUploadedExcelDataDto;
import lombok.*;
import org.hibernate.annotations.Immutable;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

import javax.persistence.Embeddable;

import java.math.BigDecimal;

import static org.nthdimenzion.utils.UtilValidator.*;

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
    private String accommodationType;
    private String duration;
    private int lengthOfStay;
    private String strength;
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
        }
        return this;
    }
}
