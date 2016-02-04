package com.pla.grouphealth.claim.cashless.domain.model.claim;

import com.pla.grouphealth.claim.cashless.presentation.dto.preauthorization.ClaimUploadedExcelDataDto;
import lombok.*;
import org.hibernate.annotations.Immutable;
import org.joda.time.LocalDate;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

import javax.persistence.Embeddable;

import static org.nthdimenzion.utils.UtilValidator.*;

/**
 * Author - Mohan Sharma Created on 1/9/2016.
 */
@ValueObject
@Immutable
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class GroupHealthCashlessClaimDiagnosisTreatmentDetail {
    private String doctorName;
    private String doctorContactNumber;
    private String indicateWhether;
    private String pregnancyG;
    private String pregnancyP;
    private String pregnancyL;
    private String pregnancyA;
    private LocalDate pregnancyDateOfDelivery;
    private String modeOdDelivery;
    private String nameOfIllnessDisease;
    private String relevantClinicalFinding;
    private String durationOfPresentAilment;
    private LocalDate dateOfConsultation;
    private String pastHistoryOfPresentAilment;
    private String provisionalDiagnosis;
    private String lineOfTreatment;
    private String indicateTest;
    private String nameOfSurgery;
    private LocalDate dateOfAdmission;
    private int lengthOfStay;
    private String typeOfAccommodation;

    public GroupHealthCashlessClaimDiagnosisTreatmentDetail updateWithDeatils(ClaimUploadedExcelDataDto claimUploadedExcelDataDto) {
        if(isNotEmpty(claimUploadedExcelDataDto)) {
            this.doctorName = claimUploadedExcelDataDto.getTreatingDoctorName();
            this.doctorContactNumber = claimUploadedExcelDataDto.getDoctorContactNumber();
            this.indicateWhether = claimUploadedExcelDataDto.getReasons();
            this.pregnancyG = claimUploadedExcelDataDto.getPregnancyG();
            this.pregnancyP = claimUploadedExcelDataDto.getPregnancyP();
            this.pregnancyL = claimUploadedExcelDataDto.getPregnancyL();
            this.pregnancyA = claimUploadedExcelDataDto.getPregnancyA();
            this.pregnancyDateOfDelivery = claimUploadedExcelDataDto.getPregnancyDateOfDelivery();
            this.modeOdDelivery = claimUploadedExcelDataDto.getPregnancyModeOfDelivery();
            this.nameOfIllnessDisease = claimUploadedExcelDataDto.getDiagnosisTreatmentIllnessTraumaIllnessDiseaseNameAndPresentingComplaints();
            this.relevantClinicalFinding = claimUploadedExcelDataDto.getDiagnosisTreatmentIllnessTraumaRelevantClinicalFindings();
            this.durationOfPresentAilment = claimUploadedExcelDataDto.getDiagnosisTreatmentIllnessTraumaPresentAilmentDuration();
            this.dateOfConsultation = claimUploadedExcelDataDto.getConsultationDate();
            this.pastHistoryOfPresentAilment = claimUploadedExcelDataDto.getDiagnosisTreatmentIllnessTraumaPresentAilmentPastHistory();
            this.provisionalDiagnosis = claimUploadedExcelDataDto.getDiagnosisTreatmentIllnessTraumaDiagnosis();
            this.lineOfTreatment = claimUploadedExcelDataDto.getDiagnosisTreatmentLineOfTreatment();
            this.indicateTest = claimUploadedExcelDataDto.getDiagnosisTreatmentTest();
            this.nameOfSurgery = claimUploadedExcelDataDto.getDiagnosisTreatmentSurgeryName();
            this.dateOfAdmission = claimUploadedExcelDataDto.getDiagnosisTreatmentSurgeryDateOfAdmission();
            this.lengthOfStay = claimUploadedExcelDataDto.getDiagnosisTreatmentSurgeryLengthOStay();
            this.typeOfAccommodation = claimUploadedExcelDataDto.getDiagnosisTreatmentSurgeryAccommodationType();
        }
        return this;
    }
}
