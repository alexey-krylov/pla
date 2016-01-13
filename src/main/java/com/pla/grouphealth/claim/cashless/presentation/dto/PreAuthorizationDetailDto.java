package com.pla.grouphealth.claim.cashless.presentation.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

/**
 * Author - Mohan Sharma Created on 1/4/2016.
 */
@Getter
@Setter
@EqualsAndHashCode
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
    private LocalDate pregnancyDateOfDelivery;
    private String pregnancyModeOfDelivery;
    private String diagnosisTreatmentIllnessTraumaIllnessDiseaseNameAndPresentingComplaints;
    private String diagnosisTreatmentIllnessTraumaRelevantClinicalFindings;
    private String diagnosisTreatmentIllnessTraumaPresentAilmentDuration;
    public LocalDate consultationDate;
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
    private LocalDate diagnosisTreatmentSurgeryDateOfAdmission;
    //private DateTime diagnosisTreatmentSurgeryDateOfDischarge;
    private int diagnosisTreatmentSurgeryLengthOStay;
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

    public static class ConsultationDateClientIdPolicyNumber{
        LocalDate consultationDate;
        String policyNumber;
        String clientId;

        public ConsultationDateClientIdPolicyNumber(LocalDate consultationDate, String policyNumber, String clientId){
            this.consultationDate = consultationDate;
            this.policyNumber = policyNumber;
            this.clientId = clientId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ConsultationDateClientIdPolicyNumber that = (ConsultationDateClientIdPolicyNumber) o;

            if (!clientId.equals(that.clientId)) return false;
            if (!consultationDate.equals(that.consultationDate)) return false;
            if (!policyNumber.equals(that.policyNumber)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = consultationDate.hashCode();
            result = 31 * result + policyNumber.hashCode();
            result = 31 * result + clientId.hashCode();
            return result;
        }
    }

    public ConsultationDateClientIdPolicyNumber getConsultationDateClientIdPolicyNumber(){
        return new ConsultationDateClientIdPolicyNumber(consultationDate, policyNumber, clientId);
    }
}
