package com.pla.grouphealth.claim.presentation.dto;

/**
 * Created by Rudra on 12/31/2015.
 */

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class GHCashlessClaimPreAuthExcelDetailDto {
    private String hospitalizationEvent;
    private String policyNumber;
    private String clientId;
    private String treatingDoctorName;
    private String doctorContactNumber;
    private String reasons;
    private String pregnancyG;
    private String pregnancyP;
    private String pregnancyL;
    private String pregnancyA;
    private String pregnancyDateOfDelivery;
    private String pregnancyModeOfDelivery;
    private String diagnosisTreatmentIllnessTraumaIllnessDiseaseNameandPresentingComplaints;
    private String diagnosisTreatmentIllnessTraumaRelevantClinicalFindings;
    private String diagnosisTreatmentIllnessTraumapresentailmentDuration;
    private String diagnosisTreatmentIllnesstraumaFirstConsultationDate;
    private String diagnosisTreatmentIllnessTraumapresentailmentPastHistory;
    private String diagnosisTreatmentIllnessTraumaDiagnosis;
    private String diagnosisTreatmentLineofTreatment;
    private String diagnosisTreatmentTest;
    private String diagnosisTreatmentDrugName;
    private String diagnosisTreatmentMedicalDuration;
    private String diagnosisTreatmentSurgeryName;
    private String diagnosisTreatmentSurgeryAccommodationType;
    private String diagnosisTreatmentSurgeryDateOfAdmission;
    private String diagnosisTreatmentSurgeryDateOfDischarge;
    private String pastHistorySufferingFromHTN;
    private String detailsOfHTN;
    private String pastHistorySufferingFromihdcad;
    private String detailsOfihdcad;
    private String pastHistorySufferingFromDiabetes;
    private String detailsOfDiabetes;
    private String pastHistorySufferingFromAsthmacopdtb;
    private String detailsOfAsthmacopdtb;
    private String pastHistorySufferingfromParalysiscvaepilepsy;
    private String detailsOfParalysiscva;
    private String pastHistorySufferingfromArthiritis;
    private String detailsOfSufferingFromArthiritis;
    private String pastHistorySufferingFromCancertumorcyst;
    private String detailsOfCancertumorcyst;
    private String pastHistorySufferingFromStdhivaids;
    private String detailofStdHivAids;
    private String pastHistorySufferingFromAlcoholDrugAbuse;
    private String detailOfAlcoholDrugAbuse;
    private String pastHistorySufferingFromPychiatricCondition;
    private String detailsPsychiatricCondition;
    private String service;
    private String type;
}