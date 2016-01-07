package com.pla.grouphealth.claim.cashless.presentation.dto;

import com.pla.grouphealth.claim.cashless.domain.model.PreAuthorizationDetail;
import lombok.Getter;
import lombok.Setter;
import org.joda.time.DateTime;

/**
 * Created by Mohan Sharma on 1/7/2016.
 */
@Getter
@Setter
public class DiagnosisTreatmentDto {
    private String indicateWhether;
    private String pregnancyG;
    private String pregnancyP;
    private String pregnancyL;
    private String pregnancyA;
    private DateTime pregnancyDateOfDelivery;
    private String modeOdDelivery;
    private String nameOfIllnessDisease;
    private String relevantClinicalFinding;
    private String durationOfPresentAilment;
    private DateTime dateOfConsultation;
    private String pastHistoryOfPresentAilment;
    private String provisionalDiagnosis;
    private String lineOfTreatment;
    private String indicateTest;
    private String nameOfSurgery;
    private DateTime dateOfAdmission;
    private int lengthOfStay;
    private String typeOfAccommodation;

    public DiagnosisTreatmentDto updateWithDetails(PreAuthorizationDetail preAuthorizationDetail) {
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
        //this.lengthOfStay = preAuthorizationDetail.
        this.typeOfAccommodation = preAuthorizationDetail.getDiagnosisTreatmentSurgeryAccommodationType();
        return this;
    }
}
