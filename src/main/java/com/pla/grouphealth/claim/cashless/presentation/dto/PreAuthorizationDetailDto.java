package com.pla.grouphealth.claim.cashless.presentation.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Mohan Sharma on 1/4/2016.
 */
@Getter
@Setter
@EqualsAndHashCode(of={"policyNumber","clientId","diagnosisTreatmentIllnessTraumaFirstConsultationDate"})
public class PreAuthorizationDetailDto {
    private String hospitalizationEvent;
    public String policyNumber;
    public String clientId;
    private String treatingDoctorName;
    private String doctorContactNumber;
    private String reasons;
    private String pregnancyG;
    private String pregnancyP;
    private String pregnancyL;
    private String pregnancyA;
    private String pregnancyDateOfDelivery;
    private String pregnancyModeOfDelivery;
    private String diagnosisTreatmentIllnessTraumaIllnessDiseaseNameAndPresentingComplaints;
    private String diagnosisTreatmentIllnessTraumaRelevantClinicalFindings;
    private String diagnosisTreatmentIllnessTraumaPresentAilmentDuration;
    public String diagnosisTreatmentIllnessTraumaFirstConsultationDate;
    private String diagnosisTreatmentIllnessTraumaPresentAilmentPastHistory;
    private String diagnosisTreatmentIllnessTraumaDiagnosis;
    private String diagnosisTreatmentLineOfTreatment;
    private String diagnosisTreatmentTest;
    private String diagnosisTreatmentDrugName;
    private String diagnosisTreatmentMedicalDuration;
    private String diagnosisTreatmentSurgeryName;
    private String diagnosisTreatmentSurgeryAccommodationType;
    private String diagnosisTreatmentSurgeryDateOfAdmission;
    private String diagnosisTreatmentSurgeryDateOfDischarge;
    private String pastHistorySufferingFromHTN;
    private String detailsOfHTN;
    private String pastHistorySufferingFromIHCCAD;
    private String detailsOfIHDCAD;
    private String pastHistorySufferingFromDiabetes;
    private String detailsOfDiabetes;
    private String pastHistorySufferingFromAsthmaCOPDTB;
    private String detailsOfAsthmaCOPDTB;
    private String pastHistorySufferingFromParalysisCVAEpilepsy;
    private String detailsOfParalysisCVA;
    private String pastHistorySufferingFromArthritis;
    private String detailsOfSufferingFromArthritis;
    private String pastHistorySufferingFromCancerTumorCyst;
    private String detailsOfCancerTumorCyst;
    private String pastHistorySufferingFromStdHivAids;
    private String detailOfStdHivAids;
    private String pastHistorySufferingFromAlcoholDrugAbuse;
    private String detailOfAlcoholDrugAbuse;
    private String pastHistorySufferingFromPsychiatricCondition;
    private String detailsPsychiatricCondition;
    private String service;
    private String type;
}
