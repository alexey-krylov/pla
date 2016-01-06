package com.pla.grouphealth.claim.cashless.domain.model;

import com.pla.core.hcp.domain.model.HCPCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.DateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by Mohan Sharma on 12/30/2015.
 */

@NoArgsConstructor
@Getter
@Setter
public class PreAuthorizationDetail {
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
    private DateTime pregnancyDateOfDelivery;
    private String pregnancyModeOfDelivery;
    private String diagnosisTreatmentIllnessTraumaIllnessDiseaseNameAndPresentingComplaints;
    private String diagnosisTreatmentIllnessTraumaRelevantClinicalFindings;
    private String diagnosisTreatmentIllnessTraumaPresentAilmentDuration;
    public DateTime consultationDate;
    private String diagnosisTreatmentIllnessTraumaPresentAilmentPastHistory;
    private String diagnosisTreatmentIllnessTraumaDiagnosis;
    private String diagnosisTreatmentLineOfTreatment;
    private String diagnosisTreatmentTest;
    private String diagnosisTreatmentDrugName;
    private String diagnosisTreatmentDrugType;
    private String diagnosisTreatmentDrugDosage;
    private String diagnosisTreatmentDrugStrength;
    private String diagnosisTreatmentMedicalDuration;
    private String diagnosisTreatmentSurgeryName;
    private String diagnosisTreatmentSurgeryAccommodationType;
    private DateTime diagnosisTreatmentSurgeryDateOfAdmission;
    private DateTime diagnosisTreatmentSurgeryDateOfDischarge;
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
