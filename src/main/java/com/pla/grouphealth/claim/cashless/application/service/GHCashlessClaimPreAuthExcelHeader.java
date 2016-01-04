package com.pla.grouphealth.claim.cashless.application.service;

import com.pla.grouphealth.claim.presentation.dto.GHCashlessClaimPreAuthExcelDetailDto;
import com.pla.grouphealth.policy.domain.model.GroupHealthPolicy;
import com.pla.publishedlanguage.contract.IExcelPropagator;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.pla.sharedkernel.util.ExcelGeneratorUtil.getCellValue;
import static org.nthdimenzion.utils.UtilValidator.isEmpty;
import static org.springframework.util.Assert.notNull;

/**
 * Created by Mohan Sharma on 12/30/2015.
 */
public enum GHCashlessClaimPreAuthExcelHeader {
    HOSPITALIZATION_EVENT("Hospitalization Event"){
        @Override
        public String getAllowedValue(GHCashlessClaimPreAuthExcelDetailDto ghCashlessClaimPreAuthExcelDetailDto) {
            return ghCashlessClaimPreAuthExcelDetailDto.getHospitalizationEvent(); 
        }

        @Override
        public void populateInsuredDetail(GHCashlessClaimPreAuthExcelDetailDto ghCashlessClaimPreAuthExcelDetailDto, Row row, List<String> headers) {
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Hospitalization Event cannot be empty.";
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage;
            }
            return errorMessage;
        }
    },POLICY_NUMBER("Policy Number"){
        @Override
        public String getAllowedValue(GHCashlessClaimPreAuthExcelDetailDto ghCashlessClaimPreAuthExcelDetailDto) {
            return ghCashlessClaimPreAuthExcelDetailDto.getPolicyNumber();
        }

        @Override
        public void populateInsuredDetail(GHCashlessClaimPreAuthExcelDetailDto ghCashlessClaimPreAuthExcelDetailDto, Row row, List<String> headers) {
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Policy Number cannot be empty.";
                }
                Cell policyNumberCell = row.getCell(excelHeaders.indexOf(POLICY_NUMBER.name()));
                String policyNumberValue = getCellValue(policyNumberCell);
                GroupHealthPolicy groupHealthPolicy = iExcelPropagator.findPolicyByPolicyNumber(policyNumberValue);
                //groupHealthPolicy.getExpiredOn()
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage;
            }
            return errorMessage;
        }
    },CLIENT_ID("Client ID"){
        @Override
        public String getAllowedValue(GHCashlessClaimPreAuthExcelDetailDto ghCashlessClaimPreAuthExcelDetailDto) {
            return ghCashlessClaimPreAuthExcelDetailDto.getPolicyNumber();
        }

        @Override
        public void populateInsuredDetail(GHCashlessClaimPreAuthExcelDetailDto ghCashlessClaimPreAuthExcelDetailDto, Row row, List<String> headers) {
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Client ID cannot be empty.";
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage;
            }
            return errorMessage;
        }
    },TREATING_DOCTOR_NAME("Name of the Treating Doctor"){
        @Override
        public String getAllowedValue(GHCashlessClaimPreAuthExcelDetailDto ghCashlessClaimPreAuthExcelDetailDto) {
            return ghCashlessClaimPreAuthExcelDetailDto.getTreatingDoctorName();
        }

        @Override
        public void populateInsuredDetail(GHCashlessClaimPreAuthExcelDetailDto ghCashlessClaimPreAuthExcelDetailDto, Row row, List<String> headers) {
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Treating Doctor name cannot be empty.";
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage;
            }
            return errorMessage;
        }
    },DOCTOR_CONTACT_NUMBER("Doctor's Contact Number"){
        @Override
        public String getAllowedValue(GHCashlessClaimPreAuthExcelDetailDto ghCashlessClaimPreAuthExcelDetailDto) {
            return ghCashlessClaimPreAuthExcelDetailDto.getDoctorContactNumber();
        }

        @Override
        public void populateInsuredDetail(GHCashlessClaimPreAuthExcelDetailDto ghCashlessClaimPreAuthExcelDetailDto, Row row, List<String> headers) {
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Doctor's Contact Number cannot be empty.";
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage;
            }
            return errorMessage;
        }
    },REASONS("Please indicate whether it is a"){
        @Override
        public String getAllowedValue(GHCashlessClaimPreAuthExcelDetailDto ghCashlessClaimPreAuthExcelDetailDto) {
            return ghCashlessClaimPreAuthExcelDetailDto.getReasons();
        }

        @Override
        public void populateInsuredDetail(GHCashlessClaimPreAuthExcelDetailDto ghCashlessClaimPreAuthExcelDetailDto, Row row, List<String> headers) {
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Please indicate whether it is a cannot be empty.";
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage;
            }
            return errorMessage;
        }
    },PREGNANCY_G("Diagnosis/Treatment in case Of Pregnancy -G"){
        @Override
        public String getAllowedValue(GHCashlessClaimPreAuthExcelDetailDto ghCashlessClaimPreAuthExcelDetailDto) {
            return ghCashlessClaimPreAuthExcelDetailDto.getPregnancyG();
        }

        @Override
        public void populateInsuredDetail(GHCashlessClaimPreAuthExcelDetailDto ghCashlessClaimPreAuthExcelDetailDto, Row row, List<String> headers) {
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Diagnosis/Treatment in case Of Pregnancy -G cannot be empty.";
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage;
            }
            return errorMessage;
        }
    },PREGNANCY_P("Diagnosis/Treatment in case Of Pregnancy"){
        @Override
        public String getAllowedValue(GHCashlessClaimPreAuthExcelDetailDto ghCashlessClaimPreAuthExcelDetailDto) {
            return ghCashlessClaimPreAuthExcelDetailDto.getPregnancyP(); 
        }

        @Override
        public void populateInsuredDetail(GHCashlessClaimPreAuthExcelDetailDto ghCashlessClaimPreAuthExcelDetailDto, Row row, List<String> headers) {
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Diagnosis/Treatment in case Of Pregnancy cannot be empty.";
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage; 
            }
            return errorMessage; 
        }
    },PREGNANCY_L("Diagnosis/Treatment in case Of Pregnancy -L"){
        @Override
        public String getAllowedValue(GHCashlessClaimPreAuthExcelDetailDto ghCashlessClaimPreAuthExcelDetailDto) {
            return ghCashlessClaimPreAuthExcelDetailDto.getPregnancyL();
        }
        @Override
        public void populateInsuredDetail(GHCashlessClaimPreAuthExcelDetailDto ghCashlessClaimPreAuthExcelDetailDto, Row row, List<String> headers) {
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "asd cannot be empty.";
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage; 
            }
            return errorMessage; 
        }
    },PREGNANCY_A("Diagnosis/Treatment in case Of Pregnancy -A"){
        @Override
        public String getAllowedValue(GHCashlessClaimPreAuthExcelDetailDto ghCashlessClaimPreAuthExcelDetailDto) {
            return ghCashlessClaimPreAuthExcelDetailDto.getPregnancyA(); 
        }

        @Override
        public void populateInsuredDetail(GHCashlessClaimPreAuthExcelDetailDto ghCashlessClaimPreAuthExcelDetailDto, Row row, List<String> headers) {
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Diagnosis/Treatment in case Of Pregnancy -A cannot be empty.";
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage;
            }
            return errorMessage;
        }
    },PREGNANCY_DATE_OF_DELIVERY("Diagnosis/Treatment in case Of Pregnancy- Date of Delivery"){
        @Override
        public String getAllowedValue(GHCashlessClaimPreAuthExcelDetailDto ghCashlessClaimPreAuthExcelDetailDto) {
            return ghCashlessClaimPreAuthExcelDetailDto.getPregnancyDateOfDelivery();
        }

        @Override
        public void populateInsuredDetail(GHCashlessClaimPreAuthExcelDetailDto ghCashlessClaimPreAuthExcelDetailDto, Row row, List<String> headers) {
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Diagnosis/Treatment in case Of Pregnancy- Date of Delivery cannot be empty.";
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage;
            }
            return errorMessage;
        }
    },PREGNANCY_MODE_OF_DELIVERY("Diagnosis/Treatment in case Of Pregnancy- Mode Of Delivery"){
        @Override
        public String getAllowedValue(GHCashlessClaimPreAuthExcelDetailDto ghCashlessClaimPreAuthExcelDetailDto) {
            return ghCashlessClaimPreAuthExcelDetailDto.getPregnancyModeOfDelivery(); 
        }

        @Override
        public void populateInsuredDetail(GHCashlessClaimPreAuthExcelDetailDto ghCashlessClaimPreAuthExcelDetailDto, Row row, List<String> headers) {
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Diagnosis/Treatment in case Of Pregnancy- Mode Of Delivery cannot be empty.";
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage; 
            }
            return errorMessage; 
        }
    },DIAGNOSIS_TREATMENT_ILLNESS_TRAUMA_ILLNESS_DISEASE_NAME_AND_PRESENTING_COMPLAINTS("Diagnosis/Treatment in case Of Illness Or Trauma - Name of illness / disease with presenting complaints"){
        @Override
        public String getAllowedValue(GHCashlessClaimPreAuthExcelDetailDto ghCashlessClaimPreAuthExcelDetailDto) {
            return ghCashlessClaimPreAuthExcelDetailDto.getDiagnosisTreatmentIllnessTraumaIllnessDiseaseNameandPresentingComplaints();
        }

        @Override
        public void populateInsuredDetail(GHCashlessClaimPreAuthExcelDetailDto ghCashlessClaimPreAuthExcelDetailDto, Row row, List<String> headers) {
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Diagnosis/Treatment in case Of Illness Or Trauma - Name of illness / disease with presenting complaints cannot be empty.";
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage; 
            }
            return errorMessage; 
        }
    },DIAGNOSIS_TREATMENT_ILLNESS_TRAUMA_RELEVANT_CLINICAL_FINDINGS("Diagnosis/Treatment in case Of Illness Or Trauma - Relevant clinical findings"){
        @Override
        public String getAllowedValue(GHCashlessClaimPreAuthExcelDetailDto ghCashlessClaimPreAuthExcelDetailDto) {
            return ghCashlessClaimPreAuthExcelDetailDto.getDiagnosisTreatmentIllnessTraumaRelevantClinicalFindings();
        }

        @Override
        public void populateInsuredDetail(GHCashlessClaimPreAuthExcelDetailDto ghCashlessClaimPreAuthExcelDetailDto, Row row, List<String> headers) {
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Diagnosis/Treatment in case Of Illness Or Trauma - Relevant clinical findings cannot be empty.";
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage; 
            }
            return errorMessage; 
        }
    },DIAGNOSIS_TREATMENT_ILLNESS_TRAUMA_PRESENT_AILMENT_DURATION("Diagnosis/Treatment in case Of Illness Or Trauma -Duration of the present ailment in days"){
        @Override
        public String getAllowedValue(GHCashlessClaimPreAuthExcelDetailDto ghCashlessClaimPreAuthExcelDetailDto) {
            return ghCashlessClaimPreAuthExcelDetailDto.getDiagnosisTreatmentIllnessTraumapresentailmentDuration(); 
        }

        @Override
        public void populateInsuredDetail(GHCashlessClaimPreAuthExcelDetailDto ghCashlessClaimPreAuthExcelDetailDto, Row row, List<String> headers) {
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Diagnosis/Treatment in case Of Illness Or Trauma -Duration of the present ailment in days cannot be empty.";
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage; 
            }
            return errorMessage; 
        }
    },DIAGNOSIS_TREATMENT_ILLNESS_TRAUMA_FIRST_CONSULTATION_DATE("Diagnosis/Treatment in case Of Illness Or Trauma - Date of first consultation"){
        @Override
        public String getAllowedValue(GHCashlessClaimPreAuthExcelDetailDto ghCashlessClaimPreAuthExcelDetailDto) {
            return ghCashlessClaimPreAuthExcelDetailDto.getDiagnosisTreatmentIllnesstraumaFirstConsultationDate(); 
        }

        @Override
        public void populateInsuredDetail(GHCashlessClaimPreAuthExcelDetailDto ghCashlessClaimPreAuthExcelDetailDto, Row row, List<String> headers) {
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Diagnosis/Treatment in case Of Illness Or Trauma - Date of first consultation cannot be empty.";
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage; 
            }
            return errorMessage; 
        }
    },DIAGNOSIS_TREATMENT_ILLNESS_TRAUMA_PRESENT_AILMENT_PAST_HISTORY("Diagnosis/Treatment in case Of Illness Or Trauma - Past history of present ailment if any"){
        @Override
        public String getAllowedValue(GHCashlessClaimPreAuthExcelDetailDto ghCashlessClaimPreAuthExcelDetailDto) {
            return ghCashlessClaimPreAuthExcelDetailDto.getDiagnosisTreatmentIllnessTraumapresentailmentPastHistory(); 
        }

        @Override
        public void populateInsuredDetail(GHCashlessClaimPreAuthExcelDetailDto ghCashlessClaimPreAuthExcelDetailDto, Row row, List<String> headers) {
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Diagnosis/Treatment in case Of Illness Or Trauma - Past history of present ailment if any cannot be empty.";
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage; 
            }
            return errorMessage; 
        }
    },DIAGNOSIS_TREATMENT_ILLNESS_TRAUMA_DIAGNOSIS("Diagnosis/Treatment in case Of Illness Or Trauma - Diagnosis"){
        @Override
        public String getAllowedValue(GHCashlessClaimPreAuthExcelDetailDto ghCashlessClaimPreAuthExcelDetailDto) {
            return ghCashlessClaimPreAuthExcelDetailDto.getDiagnosisTreatmentIllnessTraumaDiagnosis(); 
        }

        @Override
        public void populateInsuredDetail(GHCashlessClaimPreAuthExcelDetailDto ghCashlessClaimPreAuthExcelDetailDto, Row row, List<String> headers) {
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Diagnosis/Treatment in case Of Illness Or Trauma - Diagnosis cannot be empty.";
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage; 
            }
            return errorMessage; 
        }
    },DIAGNOSIS_TREATMENT_LINE_OF_TREATMENT("Diagnosis/Treatment - Line of treatment"){
        @Override
        public String getAllowedValue(GHCashlessClaimPreAuthExcelDetailDto ghCashlessClaimPreAuthExcelDetailDto) {
            return ghCashlessClaimPreAuthExcelDetailDto.getDiagnosisTreatmentLineofTreatment();
        }

        @Override
        public void populateInsuredDetail(GHCashlessClaimPreAuthExcelDetailDto ghCashlessClaimPreAuthExcelDetailDto, Row row, List<String> headers) {
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Diagnosis/Treatment - Line of treatment cannot be empty.";
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage; 
            }
            return errorMessage; 
        }
    },DIAGNOSIS_TREATMENT_TEST("Diagnosis/Treatment - If Investigations, indicate tests"){
        @Override
        public String getAllowedValue(GHCashlessClaimPreAuthExcelDetailDto ghCashlessClaimPreAuthExcelDetailDto) {
            return ghCashlessClaimPreAuthExcelDetailDto.getDiagnosisTreatmentTest();
        }

        @Override
        public void populateInsuredDetail(GHCashlessClaimPreAuthExcelDetailDto ghCashlessClaimPreAuthExcelDetailDto, Row row, List<String> headers) {
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Diagnosis/Treatment - If Investigations, indicate tests cannot be empty.";
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage; 
            }
            return errorMessage; 
        }
    },DIAGNOSIS_TREATMENT_DRUG_NAME("Diagnosis/Treatment - If Medical Please Provide Drug Name"){
        @Override
        public String getAllowedValue(GHCashlessClaimPreAuthExcelDetailDto ghCashlessClaimPreAuthExcelDetailDto) {
            return ghCashlessClaimPreAuthExcelDetailDto.getDiagnosisTreatmentDrugName(); 
        }

        @Override
        public void populateInsuredDetail(GHCashlessClaimPreAuthExcelDetailDto ghCashlessClaimPreAuthExcelDetailDto, Row row, List<String> headers) {
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Diagnosis/Treatment - If Medical Please Provide Drug Name cannot be empty.";
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage; 
            }
            return errorMessage; 
        }
    },DIAGNOSIS_TREATMENT_MEDICAL_DURATION("Diagnosis/Treatment - If Medical Please Provide Duration"){
        @Override
        public String getAllowedValue(GHCashlessClaimPreAuthExcelDetailDto ghCashlessClaimPreAuthExcelDetailDto) {
            return ghCashlessClaimPreAuthExcelDetailDto.getDiagnosisTreatmentMedicalDuration(); 
        }

        @Override
        public void populateInsuredDetail(GHCashlessClaimPreAuthExcelDetailDto ghCashlessClaimPreAuthExcelDetailDto, Row row, List<String> headers) {
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Diagnosis/Treatment - If Medical Please Provide Duration cannot be empty.";
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage; 
            }
            return errorMessage; 
        }
    },DIAGNOSIS_TREATMENT_SURGERY_NAME("Diagnosis/Treatment - If Surgery Please provide name of surgery"){
        @Override
        public String getAllowedValue(GHCashlessClaimPreAuthExcelDetailDto ghCashlessClaimPreAuthExcelDetailDto) {
            return ghCashlessClaimPreAuthExcelDetailDto.getDiagnosisTreatmentSurgeryName(); 
        }

        @Override
        public void populateInsuredDetail(GHCashlessClaimPreAuthExcelDetailDto ghCashlessClaimPreAuthExcelDetailDto, Row row, List<String> headers) {
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Diagnosis/Treatment - If Surgery Please provide name of surgery cannot be empty.";
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage; 
            }
            return errorMessage; 
        }
    }, DIAGNOSIS_TREATMENT_SURGERY_ACCOMMODATION_TYPE("Diagnosis/Treatment - If Surgery Please provide Type Of Accommodation"){
        @Override
        public String getAllowedValue(GHCashlessClaimPreAuthExcelDetailDto ghCashlessClaimPreAuthExcelDetailDto) {
            return ghCashlessClaimPreAuthExcelDetailDto.getDiagnosisTreatmentSurgeryAccommodationType(); 
        }

        @Override
        public void populateInsuredDetail(GHCashlessClaimPreAuthExcelDetailDto ghCashlessClaimPreAuthExcelDetailDto, Row row, List<String> headers) {
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Diagnosis/Treatment - If Surgery Please provide Type Of Accommodation cannot be empty.";
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage; 
            }
            return errorMessage; 
        }
    },DIAGNOSIS_TREATMENT_SURGERY_DATE_OF_ADMISSION("Diagnosis/Treatment - If Surgery Please provide  Date of Admission"){
        @Override
        public String getAllowedValue(GHCashlessClaimPreAuthExcelDetailDto ghCashlessClaimPreAuthExcelDetailDto) {
            return ghCashlessClaimPreAuthExcelDetailDto.getDiagnosisTreatmentSurgeryDateOfAdmission(); 
        }

        @Override
        public void populateInsuredDetail(GHCashlessClaimPreAuthExcelDetailDto ghCashlessClaimPreAuthExcelDetailDto, Row row, List<String> headers) {
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Diagnosis/Treatment - If Surgery Please provide  Date of Admission cannot be empty.";
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage; 
            }
            return errorMessage; 
        }
    },DIAGNOSIS_TREATMENT_SURGERY_DATE_OF_DISCHARGE("Diagnosis/Treatment - If Surgery Please provide Date of Discharge"){
        @Override
        public String getAllowedValue(GHCashlessClaimPreAuthExcelDetailDto ghCashlessClaimPreAuthExcelDetailDto) {
            return ghCashlessClaimPreAuthExcelDetailDto.getDiagnosisTreatmentSurgeryDateOfDischarge();
        }

        @Override
        public void populateInsuredDetail(GHCashlessClaimPreAuthExcelDetailDto ghCashlessClaimPreAuthExcelDetailDto, Row row, List<String> headers) {
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Diagnosis/Treatment - If Surgery Please provide Date of Discharge cannot be empty.";
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage; 
            }
            return errorMessage; 
        }
    },PAST_HISTORY_SUFFERING_FROM_HTN("Past history of any chronic illness - Suffering From HTN"){
        @Override
        public String getAllowedValue(GHCashlessClaimPreAuthExcelDetailDto ghCashlessClaimPreAuthExcelDetailDto) {
            return ghCashlessClaimPreAuthExcelDetailDto.getPastHistorySufferingFromHTN();
        }

        @Override
        public void populateInsuredDetail(GHCashlessClaimPreAuthExcelDetailDto ghCashlessClaimPreAuthExcelDetailDto, Row row, List<String> headers) {
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Past history of any chronic illness - Suffering From HTN cannot be empty.";
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage; 
            }
            return errorMessage; 
        }
    },DETAILS_OF_HTN("Past history of any chronic illness - Please provide details"){
        @Override
        public String getAllowedValue(GHCashlessClaimPreAuthExcelDetailDto ghCashlessClaimPreAuthExcelDetailDto) {
            return ghCashlessClaimPreAuthExcelDetailDto.getDetailsOfHTN(); 
        }

        @Override
        public void populateInsuredDetail(GHCashlessClaimPreAuthExcelDetailDto ghCashlessClaimPreAuthExcelDetailDto, Row row, List<String> headers) {
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Past history of any chronic illness - Please provide details cannot be empty.";
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage; 
            }
            return errorMessage; 
        }
    },PAST_HISTORY_SUFFERING_FROM_IHD_CAD("Past history of any chronic illness - Suffering From IHD/CAD"){
        @Override
        public String getAllowedValue(GHCashlessClaimPreAuthExcelDetailDto ghCashlessClaimPreAuthExcelDetailDto) {
            return ghCashlessClaimPreAuthExcelDetailDto.getPastHistorySufferingFromihdcad();
        }

        @Override
        public void populateInsuredDetail(GHCashlessClaimPreAuthExcelDetailDto ghCashlessClaimPreAuthExcelDetailDto, Row row, List<String> headers) {
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Past history of any chronic illness - Suffering From IHD/CAD cannot be empty.";
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage; 
            }
            return errorMessage; 
        }
    },DETAILS_OF_IHD_CAD("Past history of any chronic illness - Please provide details"){
        @Override
        public String getAllowedValue(GHCashlessClaimPreAuthExcelDetailDto ghCashlessClaimPreAuthExcelDetailDto) {
            return ghCashlessClaimPreAuthExcelDetailDto.getDetailsOfihdcad();
        }

        @Override
        public void populateInsuredDetail(GHCashlessClaimPreAuthExcelDetailDto ghCashlessClaimPreAuthExcelDetailDto, Row row, List<String> headers) {
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Past history of any chronic illness - Please provide details cannot be empty.";
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage; 
            }
            return errorMessage; 
        }
    },PAST_HISTORY_SUFFERING_FROM_DIABETES("Past history of any chronic illness - Suffering From Diabetes"){
        @Override
        public String getAllowedValue(GHCashlessClaimPreAuthExcelDetailDto ghCashlessClaimPreAuthExcelDetailDto) {
            return ghCashlessClaimPreAuthExcelDetailDto.getPastHistorySufferingFromDiabetes(); 
        }

        @Override
        public void populateInsuredDetail(GHCashlessClaimPreAuthExcelDetailDto ghCashlessClaimPreAuthExcelDetailDto, Row row, List<String> headers) {
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Past history of any chronic illness - Suffering From Diabetes cannot be empty.";
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage; 
            }
            return errorMessage; 
        }
    },DETAILS_OF_DIABETES("Past history of any chronic illness - Please provide details"){
        @Override
        public String getAllowedValue(GHCashlessClaimPreAuthExcelDetailDto ghCashlessClaimPreAuthExcelDetailDto) {
            return ghCashlessClaimPreAuthExcelDetailDto.getDetailsOfDiabetes(); 
        }

        @Override
        public void populateInsuredDetail(GHCashlessClaimPreAuthExcelDetailDto ghCashlessClaimPreAuthExcelDetailDto, Row row, List<String> headers) {
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Past history of any chronic illness - Please provide details cannot be empty.";
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage; 
            }
            return errorMessage; 
        }
    }, PAST_HISTORY_SUFFERING_FROM_ASTHMA_COPD_TB("Past history of any chronic illness - Suffering From Asthma/COPD/TB"){
        @Override
        public String getAllowedValue(GHCashlessClaimPreAuthExcelDetailDto ghCashlessClaimPreAuthExcelDetailDto) {
            return ghCashlessClaimPreAuthExcelDetailDto.getPastHistorySufferingFromAsthmacopdtb(); 
        }

        @Override
        public void populateInsuredDetail(GHCashlessClaimPreAuthExcelDetailDto ghCashlessClaimPreAuthExcelDetailDto, Row row, List<String> headers) {
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Past history of any chronic illness - Suffering From Asthma/COPD/TB cannot be empty.";
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage; 
            }
            return errorMessage; 
        }
    },DETAILS_OF_ASTHMA_COPD_TB("Past history of any chronic illness - Please provide details"){
        @Override
        public String getAllowedValue(GHCashlessClaimPreAuthExcelDetailDto ghCashlessClaimPreAuthExcelDetailDto) {
            return ghCashlessClaimPreAuthExcelDetailDto.getDetailsOfAsthmacopdtb(); 
        }

        @Override
        public void populateInsuredDetail(GHCashlessClaimPreAuthExcelDetailDto ghCashlessClaimPreAuthExcelDetailDto, Row row, List<String> headers) {
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Past history of any chronic illness - Please provide details cannot be empty.";
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage; 
            }
            return errorMessage; 
        }
    }, PAST_HISTORY_SUFFERING_FROM_PARALYSIS_CVA_EPILEPSY("Past history of any chronic illness - Suffering From Paralysis/CVA/Epilepsy"){
        @Override
        public String getAllowedValue(GHCashlessClaimPreAuthExcelDetailDto ghCashlessClaimPreAuthExcelDetailDto) {
            return ghCashlessClaimPreAuthExcelDetailDto.getPastHistorySufferingfromParalysiscvaepilepsy(); 
        }

        @Override
        public void populateInsuredDetail(GHCashlessClaimPreAuthExcelDetailDto ghCashlessClaimPreAuthExcelDetailDto, Row row, List<String> headers) {
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Past history of any chronic illness - Suffering From Paralysis/CVA/Epilepsy cannot be empty.";
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage; 
            }
            return errorMessage; 
        }
    },DETAILS_OF_PARALYSIS_CVA("Past history of any chronic illness - Please provide details"){
        @Override
        public String getAllowedValue(GHCashlessClaimPreAuthExcelDetailDto ghCashlessClaimPreAuthExcelDetailDto) {
            return ghCashlessClaimPreAuthExcelDetailDto.getDetailsOfParalysiscva(); 
        }

        @Override
        public void populateInsuredDetail(GHCashlessClaimPreAuthExcelDetailDto ghCashlessClaimPreAuthExcelDetailDto, Row row, List<String> headers) {
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Past history of any chronic illness - Please provide details cannot be empty.";
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage; 
            }
            return errorMessage; 
        }
    }, PAST_HISTORY_SUFFERING_FROM_ARTHIRITIS("Past history of any chronic illness - Suffering From Arthritis"){
        @Override
        public String getAllowedValue(GHCashlessClaimPreAuthExcelDetailDto ghCashlessClaimPreAuthExcelDetailDto) {
            return ghCashlessClaimPreAuthExcelDetailDto.getPastHistorySufferingfromArthiritis(); 
        }

        @Override
        public void populateInsuredDetail(GHCashlessClaimPreAuthExcelDetailDto ghCashlessClaimPreAuthExcelDetailDto, Row row, List<String> headers) {
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Past history of any chronic illness - Suffering From Arthritis cannot be empty.";
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage; 
            }
            return errorMessage; 
        }
    }, DETAILS_OF_SUFFERING_FROM_ARTHIRITIS("Past history of any chronic illness - Please provide details"){
        @Override
        public String getAllowedValue(GHCashlessClaimPreAuthExcelDetailDto ghCashlessClaimPreAuthExcelDetailDto) {
            return ghCashlessClaimPreAuthExcelDetailDto.getDetailsOfSufferingFromArthiritis(); 
        }

        @Override
        public void populateInsuredDetail(GHCashlessClaimPreAuthExcelDetailDto ghCashlessClaimPreAuthExcelDetailDto, Row row, List<String> headers) {
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Past history of any chronic illness - Please provide details cannot be empty.";
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage; 
            }
            return errorMessage; 
        }
    }, PAST_HISTORY_SUFFERING_FROM_CANCER_TUMOR_CYST("Past history of any chronic illness - Suffering From Cancer/Tumor/Cyst") {
        @Override
        public String getAllowedValue(GHCashlessClaimPreAuthExcelDetailDto ghCashlessClaimPreAuthExcelDetailDto) {
            return ghCashlessClaimPreAuthExcelDetailDto.getPastHistorySufferingFromCancertumorcyst(); 
        }

        @Override
        public void populateInsuredDetail(GHCashlessClaimPreAuthExcelDetailDto ghCashlessClaimPreAuthExcelDetailDto, Row row, List<String> headers) {
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if (isEmpty(value)) {
                    errorMessage = errorMessage + "Past history of any chronic illness - Suffering From Cancer/Tumor/Cyst cannot be empty.";
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage; 
            }
            return errorMessage; 
        }
    }, DETAILS_OF_CANCER_TUMOR_CYST("Past history of any chronic illness - Please provide details"){
        @Override
        public String getAllowedValue(GHCashlessClaimPreAuthExcelDetailDto ghCashlessClaimPreAuthExcelDetailDto) {
            return ghCashlessClaimPreAuthExcelDetailDto.getDetailsOfCancertumorcyst(); 
        }

        @Override
        public void populateInsuredDetail(GHCashlessClaimPreAuthExcelDetailDto ghCashlessClaimPreAuthExcelDetailDto, Row row, List<String> headers) {
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Past history of any chronic illness - Please provide details cannot be empty.";
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage; 
            }
            return errorMessage; 
        }
    },PAST_HISTORY_SUFFERING_FROM_STD_HIV_AIDS("Past history of any chronic illness - Suffering From STD/HIV/AIDS"){
        @Override
        public String getAllowedValue(GHCashlessClaimPreAuthExcelDetailDto ghCashlessClaimPreAuthExcelDetailDto) {
            return ghCashlessClaimPreAuthExcelDetailDto.getPastHistorySufferingFromStdhivaids(); 
        }

        @Override
        public void populateInsuredDetail(GHCashlessClaimPreAuthExcelDetailDto ghCashlessClaimPreAuthExcelDetailDto, Row row, List<String> headers) {
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Past history of any chronic illness - Suffering From STD/HIV/AIDS cannot be empty.";
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage; 
            }
            return errorMessage; 
        }
    }, DETAIL_OF_STD_HIV_AIDS("Past history of any chronic illness - Please provide details"){
        @Override
        public String getAllowedValue(GHCashlessClaimPreAuthExcelDetailDto ghCashlessClaimPreAuthExcelDetailDto) {
            return ghCashlessClaimPreAuthExcelDetailDto.getDetailOfStdHivAids(); 
        }

        @Override
        public void populateInsuredDetail(GHCashlessClaimPreAuthExcelDetailDto ghCashlessClaimPreAuthExcelDetailDto, Row row, List<String> headers) {
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Past history of any chronic illness - Please provide details cannot be empty.";
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage; 
            }
            return errorMessage; 
        }
    }, PAST_HISTORY_SUFFERING_FROM_ALCOHOL_DRUG_ABUSE("Past history of any chronic illness - Suffering From Alcohol/Drug Abuse"){
        @Override
        public String getAllowedValue(GHCashlessClaimPreAuthExcelDetailDto ghCashlessClaimPreAuthExcelDetailDto) {
            return ghCashlessClaimPreAuthExcelDetailDto.getPastHistorySufferingFromAlcoholDrugAbuse(); 
        }

        @Override
        public void populateInsuredDetail(GHCashlessClaimPreAuthExcelDetailDto ghCashlessClaimPreAuthExcelDetailDto, Row row, List<String> headers) {
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Past history of any chronic illness - Suffering From Alcohol/Drug Abuse cannot be empty.";
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage; 
            }
            return errorMessage; 
        }
    }, DETAIL_OF_ALCOHOL_DRUG_ABUSE("Past history of any chronic illness - Please provide detail"){
        @Override
        public String getAllowedValue(GHCashlessClaimPreAuthExcelDetailDto ghCashlessClaimPreAuthExcelDetailDto) {
            return ghCashlessClaimPreAuthExcelDetailDto.getDetailOfAlcoholDrugAbuse(); 
        }

        @Override
        public void populateInsuredDetail(GHCashlessClaimPreAuthExcelDetailDto ghCashlessClaimPreAuthExcelDetailDto, Row row, List<String> headers) {
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Past history of any chronic illness - Please provide detail cannot be empty.";
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage; 
            }
            return errorMessage; 
        }
    }, PAST_HISTORY_SUFFERING_FRO__PSYCHIATRIC_CONDITION("Past history of any chronic illness - Suffering From Psychiatric Condition"){
        @Override
        public String getAllowedValue(GHCashlessClaimPreAuthExcelDetailDto ghCashlessClaimPreAuthExcelDetailDto) {
            return ghCashlessClaimPreAuthExcelDetailDto.getPastHistorySufferingFromPychiatricCondition(); 
        }

        @Override
        public void populateInsuredDetail(GHCashlessClaimPreAuthExcelDetailDto ghCashlessClaimPreAuthExcelDetailDto, Row row, List<String> headers) {
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Past history of any chronic illness - Suffering From Psychiatric Condition cannot be empty.";
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage; 
            }
            return errorMessage; 
        }
    }, DETAILS_PSYCHIATRIC_CONDITION("Past history of any chronic illness - Please provide details"){
        @Override
        public String getAllowedValue(GHCashlessClaimPreAuthExcelDetailDto ghCashlessClaimPreAuthExcelDetailDto) {
            return ghCashlessClaimPreAuthExcelDetailDto.getDetailsPsychiatricCondition(); 
        }

        @Override
        public void populateInsuredDetail(GHCashlessClaimPreAuthExcelDetailDto ghCashlessClaimPreAuthExcelDetailDto, Row row, List<String> headers) {
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Past history of any chronic illness - Please provide details cannot be empty.";
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage; 
            }
            return errorMessage; 
        }
    },SERVICE("Service to be Availed - Service"){
        @Override
        public String getAllowedValue(GHCashlessClaimPreAuthExcelDetailDto ghCashlessClaimPreAuthExcelDetailDto) {
            return ghCashlessClaimPreAuthExcelDetailDto.getService(); 
        }

        @Override
        public void populateInsuredDetail(GHCashlessClaimPreAuthExcelDetailDto ghCashlessClaimPreAuthExcelDetailDto, Row row, List<String> headers) {
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Service to be Availed - Service cannot be empty.";
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage; 
            }
            return errorMessage; 
        }
    },TYPE("Service to be Availed - Type"){
        @Override
        public String getAllowedValue(GHCashlessClaimPreAuthExcelDetailDto ghCashlessClaimPreAuthExcelDetailDto) {
            return ghCashlessClaimPreAuthExcelDetailDto.getType(); 
        }

        @Override
        public void populateInsuredDetail(GHCashlessClaimPreAuthExcelDetailDto ghCashlessClaimPreAuthExcelDetailDto, Row row, List<String> headers) {
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Service to be Availed - Type cannot be empty.";
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage; 
            }
            return errorMessage; 
        }
    };



    private String description;

    GHCashlessClaimPreAuthExcelHeader(String description){
        this.description = description;
    }

    public static List<String> getAllowedHeaders(){
       return Stream.of(GHCashlessClaimPreAuthExcelHeader.values()).map(GHCashlessClaimPreAuthExcelHeader::getDescription).collect(Collectors.toList());
    }

    public String getDescription() {
        return description;
    }

    public static GHCashlessClaimPreAuthExcelHeader getEnum(String description) {
        notNull(description, "description cannot be empty for HCPRateExcelHeader");
        for (GHCashlessClaimPreAuthExcelHeader ghCashlessClaimPreAuthExcelHeader : values()) {
            if (ghCashlessClaimPreAuthExcelHeader.description.equalsIgnoreCase(description.trim())) {
                return ghCashlessClaimPreAuthExcelHeader;
            }
        }
        throw new IllegalArgumentException(description);
    }

    public abstract String getAllowedValue(GHCashlessClaimPreAuthExcelDetailDto ghCashlessClaimPreAuthExcelDetailDto);

    public abstract void populateInsuredDetail(GHCashlessClaimPreAuthExcelDetailDto ghCashlessClaimPreAuthExcelDetailDto, Row row, List<String> headers);

    public abstract String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders);
}
