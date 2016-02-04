package com.pla.grouphealth.claim.cashless.presentation.dto.preauthorization;

import com.pla.grouphealth.claim.cashless.domain.model.preauthorization.PreAuthorizationDetail;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.LocalDate;

/**
 * Author - Mohan Sharma Created on 1/7/2016.
 */
@Getter
@Setter
@NoArgsConstructor
public class DiagnosisTreatmentDto {
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

    public DiagnosisTreatmentDto updateWithDetails(PreAuthorizationDetail preAuthorizationDetail) {
        this.doctorName = preAuthorizationDetail.getTreatingDoctorName();
        this.doctorContactNumber = preAuthorizationDetail.getDoctorContactNumber();
        this.indicateWhether = preAuthorizationDetail.getReasons();
        this.pregnancyG = preAuthorizationDetail.getPregnancyG();
        this.pregnancyP = preAuthorizationDetail.getPregnancyP();
        this.pregnancyL = preAuthorizationDetail.getPregnancyL();
        this.pregnancyA = preAuthorizationDetail.getPregnancyA();
        this.pregnancyDateOfDelivery = preAuthorizationDetail.getPregnancyDateOfDelivery();
        this.modeOdDelivery = preAuthorizationDetail.getPregnancyModeOfDelivery();
        this.nameOfIllnessDisease = preAuthorizationDetail.getDiagnosisTreatmentIllnessTraumaIllnessDiseaseNameAndPresentingComplaints();
        this.relevantClinicalFinding = preAuthorizationDetail.getDiagnosisTreatmentIllnessTraumaRelevantClinicalFindings();
        this.durationOfPresentAilment = preAuthorizationDetail.getDiagnosisTreatmentIllnessTraumaPresentAilmentDuration();
        this.dateOfConsultation = preAuthorizationDetail.getConsultationDate();
        this.pastHistoryOfPresentAilment = preAuthorizationDetail.getDiagnosisTreatmentIllnessTraumaPresentAilmentPastHistory();
        this.provisionalDiagnosis = preAuthorizationDetail.getDiagnosisTreatmentIllnessTraumaDiagnosis();
        this.lineOfTreatment = preAuthorizationDetail.getDiagnosisTreatmentLineOfTreatment();
        this.indicateTest = preAuthorizationDetail.getDiagnosisTreatmentTest();
        this.nameOfSurgery = preAuthorizationDetail.getDiagnosisTreatmentSurgeryName();
        this.dateOfAdmission = preAuthorizationDetail.getDiagnosisTreatmentSurgeryDateOfAdmission();
        this.lengthOfStay = preAuthorizationDetail.getDiagnosisTreatmentSurgeryLengthOStay();
        this.typeOfAccommodation = preAuthorizationDetail.getDiagnosisTreatmentSurgeryAccommodationType();
        return this;
    }
}
