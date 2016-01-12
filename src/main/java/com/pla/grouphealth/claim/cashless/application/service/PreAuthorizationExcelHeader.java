package com.pla.grouphealth.claim.cashless.application.service;

import com.google.common.collect.Lists;
import com.pla.grouphealth.claim.cashless.presentation.dto.PreAuthorizationDetailDto;
import com.pla.grouphealth.policy.domain.model.GroupHealthPolicy;
import com.pla.publishedlanguage.contract.IExcelPropagator;
import com.pla.sharedkernel.identifier.PolicyId;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.nthdimenzion.common.AppConstants;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.pla.sharedkernel.util.ExcelGeneratorUtil.getCellValue;
import static org.nthdimenzion.utils.UtilValidator.isEmpty;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;
import static org.springframework.util.Assert.notNull;

/**
 * Author - Mohan Sharma Created on 12/30/2015.
 */
public enum PreAuthorizationExcelHeader {

    HOSPITALIZATION_EVENT("Hospitalization Event"){
        @Override
        public String getAllowedValue(PreAuthorizationDetailDto preAuthorizationDetailDto) {
            return preAuthorizationDetailDto.getHospitalizationEvent();
        }

        @Override
        public PreAuthorizationDetailDto populatePreAuthorizationDetail(PreAuthorizationDetailDto preAuthorizationDetailDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            preAuthorizationDetailDto.setHospitalizationEvent(cellValue);
            return preAuthorizationDetailDto;
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";

            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Hospitalization Event cannot be empty.";
                    return errorMessage;
                }

            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage;
            }
            return errorMessage;
        }
    },POLICY_NUMBER("Policy Number"){
        @Override
        public String getAllowedValue(PreAuthorizationDetailDto preAuthorizationDetailDto) {
            return preAuthorizationDetailDto.getPolicyNumber();
        }

        @Override
        public PreAuthorizationDetailDto populatePreAuthorizationDetail(PreAuthorizationDetailDto preAuthorizationDetailDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            preAuthorizationDetailDto.setPolicyNumber(cellValue);
            return preAuthorizationDetailDto;
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Policy Number cannot be empty.";
                    return errorMessage;
                }
                Cell policyNumberCell = row.getCell(excelHeaders.indexOf(POLICY_NUMBER.description));
                String policyNumberValue = getCellValue(policyNumberCell);
                GroupHealthPolicy groupHealthPolicy = iExcelPropagator.findPolicyByPolicyNumber(policyNumberValue);
                if(isEmpty(groupHealthPolicy)){
                    errorMessage = errorMessage + "No Group Health Policy found with given Policy Number";
                    return errorMessage;
                }
                DateTime  expiredOn= groupHealthPolicy.getExpiredOn();
                if(expiredOn.compareTo(new DateTime()) == -1){
                    errorMessage=errorMessage+  "Policy Is Expired";
                }

            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage;
            }
            return errorMessage;
        }
    },CLIENT_ID("Client ID"){
        @Override
        public String getAllowedValue(PreAuthorizationDetailDto preAuthorizationDetailDto) {
            return preAuthorizationDetailDto.getPolicyNumber();
        }

        @Override
        public PreAuthorizationDetailDto populatePreAuthorizationDetail(PreAuthorizationDetailDto preAuthorizationDetailDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            try {
                cellValue = new BigDecimal(cellValue).toString();
            } catch(NumberFormatException e){
                preAuthorizationDetailDto.setClientId(cellValue);
                return preAuthorizationDetailDto;
            }
            preAuthorizationDetailDto.setClientId(cellValue);
            return preAuthorizationDetailDto;
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Client ID cannot be empty.";
                    return errorMessage;
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage;
            }
            return errorMessage;
        }
    },CONSULTATION_DATE("Date Of Consultation"){
        @Override
        public String getAllowedValue(PreAuthorizationDetailDto preAuthorizationDetailDto) {
            return isNotEmpty(preAuthorizationDetailDto.getConsultationDate()) ? preAuthorizationDetailDto.getConsultationDate().toString() : "";
        }

        @Override
        public PreAuthorizationDetailDto populatePreAuthorizationDetail(PreAuthorizationDetailDto preAuthorizationDetailDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            preAuthorizationDetailDto.setConsultationDate(isNotEmpty(cellValue) ? DateTime.parse(cellValue, DateTimeFormat.forPattern(AppConstants.DD_MM_YYY_FORMAT)) : null);
            return preAuthorizationDetailDto;
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Date Of Consultation cannot be empty.";
                    return errorMessage;
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage;
            }
            return errorMessage;
        }
    },TREATING_DOCTOR_NAME("Name of the Treating Doctor"){
        @Override
        public String getAllowedValue(PreAuthorizationDetailDto preAuthorizationDetailDto) {
            return preAuthorizationDetailDto.getTreatingDoctorName();
        }

        @Override
        public PreAuthorizationDetailDto populatePreAuthorizationDetail(PreAuthorizationDetailDto preAuthorizationDetailDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            preAuthorizationDetailDto.setTreatingDoctorName(cellValue);
            return preAuthorizationDetailDto;
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Treating Doctor name cannot be empty.";
                    return  errorMessage;
                }
                if(!Pattern.compile("^[A-Za-z, ]++$").matcher(value).matches())
                    errorMessage = errorMessage + " only characters allowed."+"\n";
                if(value.length() > 100)
                    errorMessage = errorMessage + " Length should not be greater than 100 character."+"\n";
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage;
            }
            return errorMessage;
        }
    },DOCTOR_CONTACT_NUMBER("Doctor's Contact Number"){
        @Override
        public String getAllowedValue(PreAuthorizationDetailDto preAuthorizationDetailDto) {
            return preAuthorizationDetailDto.getDoctorContactNumber();
        }

        @Override
        public PreAuthorizationDetailDto populatePreAuthorizationDetail(PreAuthorizationDetailDto preAuthorizationDetailDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            try {
                cellValue = new BigDecimal(cellValue).toString();
            } catch(NumberFormatException e){
                preAuthorizationDetailDto.setClientId(cellValue);
                return preAuthorizationDetailDto;
            }
            preAuthorizationDetailDto.setDoctorContactNumber(cellValue);
            return preAuthorizationDetailDto;
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            return errorMessage;
        }
    },REASONS("Please indicate whether it is a"){
        @Override
        public String getAllowedValue(PreAuthorizationDetailDto preAuthorizationDetailDto) {
            return preAuthorizationDetailDto.getReasons();
        }

        @Override
        public PreAuthorizationDetailDto populatePreAuthorizationDetail(PreAuthorizationDetailDto preAuthorizationDetailDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            preAuthorizationDetailDto.setReasons(cellValue);
            return preAuthorizationDetailDto;
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Please indicate whether it is a cannot be empty.";
                    return errorMessage;
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage;
            }
            return errorMessage;
        }
    },PREGNANCY_G("Diagnosis/Treatment in case Of Pregnancy -G"){
        @Override
        public String getAllowedValue(PreAuthorizationDetailDto preAuthorizationDetailDto) {
            return preAuthorizationDetailDto.getPregnancyG();
        }

        @Override
        public PreAuthorizationDetailDto populatePreAuthorizationDetail(PreAuthorizationDetailDto preAuthorizationDetailDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            preAuthorizationDetailDto.setPregnancyG(cellValue);
            return preAuthorizationDetailDto;
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            return errorMessage;
        }
    },PREGNANCY_P("Diagnosis/Treatment in case Of Pregnancy"){
        @Override
        public String getAllowedValue(PreAuthorizationDetailDto preAuthorizationDetailDto) {
            return preAuthorizationDetailDto.getPregnancyP();
        }

        @Override
        public PreAuthorizationDetailDto populatePreAuthorizationDetail(PreAuthorizationDetailDto preAuthorizationDetailDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            preAuthorizationDetailDto.setPregnancyP(cellValue);
            return preAuthorizationDetailDto;
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            return errorMessage;
        }
    },PREGNANCY_L("Diagnosis/Treatment in case Of Pregnancy -L"){
        @Override
        public String getAllowedValue(PreAuthorizationDetailDto preAuthorizationDetailDto) {
            return preAuthorizationDetailDto.getPregnancyL();
        }

        @Override
        public PreAuthorizationDetailDto populatePreAuthorizationDetail(PreAuthorizationDetailDto preAuthorizationDetailDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            preAuthorizationDetailDto.setPregnancyL(cellValue);
            return preAuthorizationDetailDto;
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            return errorMessage;
        }
    },PREGNANCY_A("Diagnosis/Treatment in case Of Pregnancy -A"){
        @Override
        public String getAllowedValue(PreAuthorizationDetailDto preAuthorizationDetailDto) {
            return preAuthorizationDetailDto.getPregnancyA();
        }

        @Override
        public PreAuthorizationDetailDto populatePreAuthorizationDetail(PreAuthorizationDetailDto preAuthorizationDetailDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            preAuthorizationDetailDto.setPregnancyA(cellValue);
            return preAuthorizationDetailDto;
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            return errorMessage;
        }
    },PREGNANCY_DATE_OF_DELIVERY("Diagnosis/Treatment in case Of Pregnancy- Date of Delivery"){
        @Override
        public String getAllowedValue(PreAuthorizationDetailDto preAuthorizationDetailDto) {
            return isNotEmpty(preAuthorizationDetailDto.getPregnancyDateOfDelivery()) ? preAuthorizationDetailDto.getPregnancyDateOfDelivery().toString() : "";
        }

        @Override
        public PreAuthorizationDetailDto populatePreAuthorizationDetail(PreAuthorizationDetailDto preAuthorizationDetailDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            preAuthorizationDetailDto.setPregnancyDateOfDelivery(isNotEmpty(cellValue) ? DateTime.parse(cellValue, DateTimeFormat.forPattern(AppConstants.DD_MM_YYY_FORMAT)) : null);
            return preAuthorizationDetailDto;
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Diagnosis/Treatment in case Of Pregnancy- Date of Delivery cannot be empty.";
                    return errorMessage;
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage;
            }
            return errorMessage;
        }
    },PREGNANCY_MODE_OF_DELIVERY("Diagnosis/Treatment in case Of Pregnancy- Mode Of Delivery"){
        @Override
        public String getAllowedValue(PreAuthorizationDetailDto preAuthorizationDetailDto) {
            return preAuthorizationDetailDto.getPregnancyModeOfDelivery();
        }

        @Override
        public PreAuthorizationDetailDto populatePreAuthorizationDetail(PreAuthorizationDetailDto preAuthorizationDetailDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            preAuthorizationDetailDto.setPregnancyModeOfDelivery(cellValue);
            return preAuthorizationDetailDto;
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Diagnosis/Treatment in case Of Pregnancy- Mode Of Delivery cannot be empty.";
                    return errorMessage;
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage;
            }
            return errorMessage;
        }
    },DIAGNOSIS_TREATMENT_ILLNESS_TRAUMA_ILLNESS_DISEASE_NAME_AND_PRESENTING_COMPLAINTS("Diagnosis/Treatment in case Of Illness Or Trauma - Name of illness / disease with presenting complaints"){
        @Override
        public String getAllowedValue(PreAuthorizationDetailDto preAuthorizationDetailDto) {
            return preAuthorizationDetailDto.getDiagnosisTreatmentIllnessTraumaIllnessDiseaseNameAndPresentingComplaints();
        }

        @Override
        public PreAuthorizationDetailDto populatePreAuthorizationDetail(PreAuthorizationDetailDto preAuthorizationDetailDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            preAuthorizationDetailDto.setDiagnosisTreatmentIllnessTraumaIllnessDiseaseNameAndPresentingComplaints(cellValue);
            return preAuthorizationDetailDto;
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            return errorMessage;
        }
    },DIAGNOSIS_TREATMENT_ILLNESS_TRAUMA_RELEVANT_CLINICAL_FINDINGS("Diagnosis/Treatment in case Of Illness Or Trauma - Relevant clinical findings"){
        @Override
        public String getAllowedValue(PreAuthorizationDetailDto preAuthorizationDetailDto) {
            return preAuthorizationDetailDto.getDiagnosisTreatmentIllnessTraumaRelevantClinicalFindings();
        }

        @Override
        public PreAuthorizationDetailDto populatePreAuthorizationDetail(PreAuthorizationDetailDto preAuthorizationDetailDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            preAuthorizationDetailDto.setDiagnosisTreatmentIllnessTraumaRelevantClinicalFindings(cellValue);
            return preAuthorizationDetailDto;
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            return errorMessage;
        }
    },DIAGNOSIS_TREATMENT_ILLNESS_TRAUMA_PRESENT_AILMENT_DURATION("Diagnosis/Treatment in case Of Illness Or Trauma -Duration of the present ailment in days"){
        @Override
        public String getAllowedValue(PreAuthorizationDetailDto preAuthorizationDetailDto) {
            return preAuthorizationDetailDto.getDiagnosisTreatmentIllnessTraumaPresentAilmentDuration();
        }

        @Override
        public PreAuthorizationDetailDto populatePreAuthorizationDetail(PreAuthorizationDetailDto preAuthorizationDetailDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            preAuthorizationDetailDto.setDiagnosisTreatmentIllnessTraumaPresentAilmentDuration(cellValue);
            return preAuthorizationDetailDto;
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            return errorMessage;
        }
    },DIAGNOSIS_TREATMENT_ILLNESS_TRAUMA_PRESENT_AILMENT_PAST_HISTORY("Diagnosis/Treatment in case Of Illness Or Trauma - Past history of present ailment if any"){
        @Override
        public String getAllowedValue(PreAuthorizationDetailDto preAuthorizationDetailDto) {
            return preAuthorizationDetailDto.getDiagnosisTreatmentIllnessTraumaPresentAilmentPastHistory();
        }

        @Override
        public PreAuthorizationDetailDto populatePreAuthorizationDetail(PreAuthorizationDetailDto preAuthorizationDetailDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            preAuthorizationDetailDto.setDiagnosisTreatmentIllnessTraumaPresentAilmentPastHistory(cellValue);
            return preAuthorizationDetailDto;
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            return errorMessage;
        }
    },DIAGNOSIS_TREATMENT_ILLNESS_TRAUMA_DIAGNOSIS("Diagnosis/Treatment in case Of Illness Or Trauma - Provisional Diagnosis"){
        @Override
        public String getAllowedValue(PreAuthorizationDetailDto preAuthorizationDetailDto) {
            return preAuthorizationDetailDto.getDiagnosisTreatmentIllnessTraumaDiagnosis();
        }

        @Override
        public PreAuthorizationDetailDto populatePreAuthorizationDetail(PreAuthorizationDetailDto preAuthorizationDetailDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            preAuthorizationDetailDto.setDiagnosisTreatmentIllnessTraumaDiagnosis(cellValue);
            return preAuthorizationDetailDto;
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Diagnosis/Treatment in case Of Illness Or Trauma - Diagnosis cannot be empty.";
                    return errorMessage;
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage;
            }
            return errorMessage;
        }
    },DIAGNOSIS_TREATMENT_LINE_OF_TREATMENT("Diagnosis/Treatment - Line of treatment"){
        @Override
        public String getAllowedValue(PreAuthorizationDetailDto preAuthorizationDetailDto) {
            return preAuthorizationDetailDto.getDiagnosisTreatmentLineOfTreatment();
        }

        @Override
        public PreAuthorizationDetailDto populatePreAuthorizationDetail(PreAuthorizationDetailDto preAuthorizationDetailDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            preAuthorizationDetailDto.setDiagnosisTreatmentLineOfTreatment(cellValue);
            return preAuthorizationDetailDto;
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Diagnosis/Treatment - Line of treatment cannot be empty.";
                    return errorMessage;
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage;
            }
            return errorMessage;
        }
    },DIAGNOSIS_TREATMENT_TEST("Diagnosis/Treatment - If Investigations, indicate tests"){
        @Override
        public String getAllowedValue(PreAuthorizationDetailDto preAuthorizationDetailDto) {
            return preAuthorizationDetailDto.getDiagnosisTreatmentTest();
        }

        @Override
        public PreAuthorizationDetailDto populatePreAuthorizationDetail(PreAuthorizationDetailDto preAuthorizationDetailDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            preAuthorizationDetailDto.setDiagnosisTreatmentTest(cellValue);
            return preAuthorizationDetailDto;
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Diagnosis/Treatment -Investigations, indicate tests cannot be empty.";
                    return errorMessage;
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage;
            }
            return errorMessage;
        }
    },DIAGNOSIS_TREATMENT_DRUG_NAME("Diagnosis/Treatment - If Medical Please Provide Drug Name"){
        @Override
        public String getAllowedValue(PreAuthorizationDetailDto preAuthorizationDetailDto) {
            return preAuthorizationDetailDto.getDiagnosisTreatmentDrugName();
        }

        @Override
        public PreAuthorizationDetailDto populatePreAuthorizationDetail(PreAuthorizationDetailDto preAuthorizationDetailDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            preAuthorizationDetailDto.setDiagnosisTreatmentDrugName(cellValue);
            return preAuthorizationDetailDto;
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Diagnosis/Treatment - Provide Drug Name cannot be empty.";
                    return errorMessage;
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage;
            }
            return errorMessage;
        }
    },DIAGNOSIS_TREATMENT_DRUG_TYPE("Diagnosis/Treatment - If Medical Please Provide Drug Type"){
        @Override
        public String getAllowedValue(PreAuthorizationDetailDto preAuthorizationDetailDto) {
            return preAuthorizationDetailDto.getDiagnosisTreatmentDrugType();
        }

        @Override
        public PreAuthorizationDetailDto populatePreAuthorizationDetail(PreAuthorizationDetailDto preAuthorizationDetailDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            preAuthorizationDetailDto.setDiagnosisTreatmentDrugType(cellValue);
            return preAuthorizationDetailDto;
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Diagnosis/Treatment - Drug Type cannot be empty.";
                    return errorMessage;
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage;
            }
            return errorMessage;
        }
    },DIAGNOSIS_TREATMENT_DRUG_DOSAGE("Diagnosis/Treatment - If Medical Please Provide Drug Dosage/ Day"){
        @Override
        public String getAllowedValue(PreAuthorizationDetailDto preAuthorizationDetailDto) {
            return preAuthorizationDetailDto.getDiagnosisTreatmentDrugDosage();
        }

        @Override
        public PreAuthorizationDetailDto populatePreAuthorizationDetail(PreAuthorizationDetailDto preAuthorizationDetailDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            preAuthorizationDetailDto.setDiagnosisTreatmentDrugDosage(String.valueOf(new BigDecimal(cellValue).intValue()));
            return preAuthorizationDetailDto;
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Diagnosis/Treatment - Drug Dosage/ Day cannot be empty.";
                    return errorMessage;
                }
                if(isNotEmpty(value) && !value.matches("(?<![\\d.])(\\d{1,3}|\\d{0,3}\\.\\d{1,2})?(?![\\d.])")){
                    errorMessage = errorMessage + "Diagnosis/Treatment - Drug Dosage/ Day 3 digit number allowed.";
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage;
            }
            return errorMessage;
        }
    },DIAGNOSIS_TREATMENT_DRUG_STRENGTH("Diagnosis/Treatment - If Medical Please Provide Drug Strength/Strength Type"){
        @Override
        public String getAllowedValue(PreAuthorizationDetailDto preAuthorizationDetailDto) {
            return preAuthorizationDetailDto.getDiagnosisTreatmentDrugStrength();
        }

        @Override
        public PreAuthorizationDetailDto populatePreAuthorizationDetail(PreAuthorizationDetailDto preAuthorizationDetailDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            preAuthorizationDetailDto.setDiagnosisTreatmentDrugStrength(String.valueOf(new BigDecimal(cellValue).intValue()));
            return preAuthorizationDetailDto;
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Diagnosis/Treatment - Drug Strength/Strength Type cannot be empty.";
                    return errorMessage;
                }
                if(isNotEmpty(value) && !value.matches("(?<![\\d.])(\\d{1,3}|\\d{0,3}\\.\\d{1,2})?(?![\\d.])")){
                    errorMessage = errorMessage + "Diagnosis/Treatment - Drug Strength/Strength Type 3 digit number allowed.";
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage;
            }
            return errorMessage;
        }
    },DIAGNOSIS_TREATMENT_MEDICAL_DURATION("Diagnosis/Treatment - If Medical Please Provide Duration"){
        @Override
        public String getAllowedValue(PreAuthorizationDetailDto preAuthorizationDetailDto) {
            return preAuthorizationDetailDto.getDiagnosisTreatmentMedicalDuration();
        }

        @Override
        public PreAuthorizationDetailDto populatePreAuthorizationDetail(PreAuthorizationDetailDto preAuthorizationDetailDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            preAuthorizationDetailDto.setDiagnosisTreatmentMedicalDuration(String.valueOf(new BigDecimal(cellValue).intValue()));
            return preAuthorizationDetailDto;
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Diagnosis/Treatment -  Provide Duration cannot be empty.";
                    return errorMessage;
                }
                if(isNotEmpty(value) && !value.matches("(?<![\\d.])(\\d{1,20}|\\d{0,20}\\.\\d{1,2})?(?![\\d.])")){
                    errorMessage = errorMessage + "Diagnosis/Treatment - Duration only numbers allowed.";
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage;
            }
            return errorMessage;
        }
    },DIAGNOSIS_TREATMENT_SURGERY_NAME("Diagnosis/Treatment - If Surgery Please provide name of surgery"){
        @Override
        public String getAllowedValue(PreAuthorizationDetailDto preAuthorizationDetailDto) {
            return preAuthorizationDetailDto.getDiagnosisTreatmentSurgeryName();
        }

        @Override
        public PreAuthorizationDetailDto populatePreAuthorizationDetail(PreAuthorizationDetailDto preAuthorizationDetailDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            preAuthorizationDetailDto.setDiagnosisTreatmentSurgeryName(cellValue);
            return preAuthorizationDetailDto;
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Diagnosis/Treatment - name of surgery cannot be empty.";
                    return errorMessage;
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage;
            }
            return errorMessage;
        }
    }, DIAGNOSIS_TREATMENT_SURGERY_ACCOMMODATION_TYPE("Diagnosis/Treatment - If Surgery Please provide Type Of Accommodation"){
        @Override
        public String getAllowedValue(PreAuthorizationDetailDto preAuthorizationDetailDto) {
            return preAuthorizationDetailDto.getDiagnosisTreatmentSurgeryAccommodationType();
        }

        @Override
        public PreAuthorizationDetailDto populatePreAuthorizationDetail(PreAuthorizationDetailDto preAuthorizationDetailDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            preAuthorizationDetailDto.setDiagnosisTreatmentSurgeryAccommodationType(cellValue);
            return preAuthorizationDetailDto;
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Diagnosis/Treatment - provide Type Of Accommodation cannot be empty.";
                    return errorMessage;
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage;
            }
            return errorMessage;
        }
    },DIAGNOSIS_TREATMENT_SURGERY_DATE_OF_ADMISSION("Diagnosis/Treatment - If Surgery Please provide  Date of Admission"){
        @Override
        public String getAllowedValue(PreAuthorizationDetailDto preAuthorizationDetailDto) {
            return isNotEmpty(preAuthorizationDetailDto.getDiagnosisTreatmentSurgeryDateOfAdmission()) ? preAuthorizationDetailDto.getDiagnosisTreatmentSurgeryDateOfAdmission().toString() : "";
        }

        @Override
        public PreAuthorizationDetailDto populatePreAuthorizationDetail(PreAuthorizationDetailDto preAuthorizationDetailDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            preAuthorizationDetailDto.setDiagnosisTreatmentSurgeryDateOfAdmission(isNotEmpty(cellValue) ? DateTime.parse(cellValue, DateTimeFormat.forPattern(AppConstants.DD_MM_YYY_FORMAT)) : null);
            return preAuthorizationDetailDto;
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Diagnosis/Treatment -  Date of Admission cannot be empty.";
                    return errorMessage;
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage;
            }
            return errorMessage;
        }
    }
    /*DIAGNOSIS_TREATMENT_SURGERY_DATE_OF_DISCHARGE("Diagnosis/Treatment - If Surgery Please provide Date of Discharge"){
        @Override
        public String getAllowedValue(PreAuthorizationDetailDto preAuthorizationDetailDto) {
            return isNotEmpty(preAuthorizationDetailDto.getDiagnosisTreatmentSurgeryDateOfDischarge()) ? preAuthorizationDetailDto.getDiagnosisTreatmentSurgeryDateOfDischarge().toString() : "";
        }

        @Override
        public PreAuthorizationDetailDto populatePreAuthorizationDetail(PreAuthorizationDetailDto preAuthorizationDetailDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            preAuthorizationDetailDto.setDiagnosisTreatmentSurgeryDateOfDischarge(isNotEmpty(cellValue) ? DateTime.parse(cellValue, DateTimeFormat.forPattern(AppConstants.DD_MM_YYY_FORMAT)) : null);
            return preAuthorizationDetailDto;
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Diagnosis/Treatment -  Date of Discharge cannot be empty.";
                    return errorMessage;
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage;
            }
            return errorMessage;
        }
    }*/,DIAGNOSIS_TREATMENT_SURGERY_LENGTH_OF_STAY("Diagnosis/Treatment - If Surgery Please provide  Length of Stay"){
        @Override
        public String getAllowedValue(PreAuthorizationDetailDto preAuthorizationDetailDto) {
            return isNotEmpty(preAuthorizationDetailDto.getDiagnosisTreatmentSurgeryLengthOStay()) ? String.valueOf(preAuthorizationDetailDto.getDiagnosisTreatmentSurgeryLengthOStay()) : "";
        }

        @Override
        public PreAuthorizationDetailDto populatePreAuthorizationDetail(PreAuthorizationDetailDto preAuthorizationDetailDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            preAuthorizationDetailDto.setDiagnosisTreatmentSurgeryLengthOStay(isNotEmpty(cellValue) ? new BigDecimal(cellValue).intValue() : null);
            return preAuthorizationDetailDto;
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Diagnosis/Treatment -  Date of Discharge cannot be empty.";
                    return errorMessage;
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage;
            }
            return errorMessage;
        }
    },PAST_HISTORY_SUFFERING_FROM_HTN("Past history of any chronic illness - Suffering From HTN"){
        @Override
        public String getAllowedValue(PreAuthorizationDetailDto preAuthorizationDetailDto) {
            return preAuthorizationDetailDto.getPastHistorySufferingFromHTN();
        }

        @Override
        public PreAuthorizationDetailDto populatePreAuthorizationDetail(PreAuthorizationDetailDto preAuthorizationDetailDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            preAuthorizationDetailDto.setPastHistorySufferingFromHTN(cellValue);
            return preAuthorizationDetailDto;
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            return errorMessage;
        }
    },DETAILS_OF_HTN("Past history of chronic illness(HTN) - Please provide details"){
        @Override
        public String getAllowedValue(PreAuthorizationDetailDto preAuthorizationDetailDto) {
            return preAuthorizationDetailDto.getDetailsOfHTN();
        }

        @Override
        public PreAuthorizationDetailDto populatePreAuthorizationDetail(PreAuthorizationDetailDto preAuthorizationDetailDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            preAuthorizationDetailDto.setDetailsOfHTN(cellValue);
            return preAuthorizationDetailDto;
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            return errorMessage;
        }
    },PAST_HISTORY_SUFFERING_FROM_IHD_CAD("Past history of any chronic illness - Suffering From IHD/CAD"){
        @Override
        public String getAllowedValue(PreAuthorizationDetailDto preAuthorizationDetailDto) {
            return preAuthorizationDetailDto.getPastHistorySufferingFromIHCCAD();
        }

        @Override
        public PreAuthorizationDetailDto populatePreAuthorizationDetail(PreAuthorizationDetailDto preAuthorizationDetailDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            preAuthorizationDetailDto.setPastHistorySufferingFromIHCCAD(cellValue);
            return preAuthorizationDetailDto;
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            return errorMessage;
        }
    },DETAILS_OF_IHD_CAD("Past history of chronic illness(IHD/CAD) - Please provide details"){
        @Override
        public String getAllowedValue(PreAuthorizationDetailDto preAuthorizationDetailDto) {
            return preAuthorizationDetailDto.getDetailsOfIHDCAD();
        }

        @Override
        public PreAuthorizationDetailDto populatePreAuthorizationDetail(PreAuthorizationDetailDto preAuthorizationDetailDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            preAuthorizationDetailDto.setDetailsOfIHDCAD(cellValue);
            return preAuthorizationDetailDto;
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            return errorMessage;
        }
    },PAST_HISTORY_SUFFERING_FROM_DIABETES("Past history of any chronic illness - Suffering From Diabetes"){
        @Override
        public String getAllowedValue(PreAuthorizationDetailDto preAuthorizationDetailDto) {
            return preAuthorizationDetailDto.getPastHistorySufferingFromDiabetes();
        }

        @Override
        public PreAuthorizationDetailDto populatePreAuthorizationDetail(PreAuthorizationDetailDto preAuthorizationDetailDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            preAuthorizationDetailDto.setPastHistorySufferingFromDiabetes(cellValue);
            return preAuthorizationDetailDto;
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            return errorMessage;
        }
    },DETAILS_OF_DIABETES("Past history of chronic illness(DIABETES) - Please provide details"){
        @Override
        public String getAllowedValue(PreAuthorizationDetailDto preAuthorizationDetailDto) {
            return preAuthorizationDetailDto.getDetailsOfDiabetes();
        }

        @Override
        public PreAuthorizationDetailDto populatePreAuthorizationDetail(PreAuthorizationDetailDto preAuthorizationDetailDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            preAuthorizationDetailDto.setDetailsOfDiabetes(cellValue);
            return preAuthorizationDetailDto;
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            return errorMessage;
        }
    }, PAST_HISTORY_SUFFERING_FROM_ASTHMA_COPD_TB("Past history of any chronic illness - Suffering From Asthma/COPD/TB"){
        @Override
        public String getAllowedValue(PreAuthorizationDetailDto preAuthorizationDetailDto) {
            return preAuthorizationDetailDto.getPastHistorySufferingFromAsthmaCOPDTB();
        }

        @Override
        public PreAuthorizationDetailDto populatePreAuthorizationDetail(PreAuthorizationDetailDto preAuthorizationDetailDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            preAuthorizationDetailDto.setPastHistorySufferingFromAsthmaCOPDTB(cellValue);
            return preAuthorizationDetailDto;
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            return errorMessage;
        }
    },DETAILS_OF_ASTHMA_COPD_TB("Past history of chronic illness(ASTHMA/COPD/TB) - Please provide details"){
        @Override
        public String getAllowedValue(PreAuthorizationDetailDto preAuthorizationDetailDto) {
            return preAuthorizationDetailDto.getDetailsOfAsthmaCOPDTB();
        }

        @Override
        public PreAuthorizationDetailDto populatePreAuthorizationDetail(PreAuthorizationDetailDto preAuthorizationDetailDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            preAuthorizationDetailDto.setDetailsOfAsthmaCOPDTB(cellValue);
            return preAuthorizationDetailDto;
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            return errorMessage;
        }
    }, PAST_HISTORY_SUFFERING_FROM_PARALYSIS_CVA_EPILEPSY("Past history of any chronic illness - Suffering From Paralysis/CVA/Epilepsy"){
        @Override
        public String getAllowedValue(PreAuthorizationDetailDto preAuthorizationDetailDto) {
            return preAuthorizationDetailDto.getPastHistorySufferingFromParalysisCVAEpilepsy();
        }

        @Override
        public PreAuthorizationDetailDto populatePreAuthorizationDetail(PreAuthorizationDetailDto preAuthorizationDetailDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            preAuthorizationDetailDto.setPastHistorySufferingFromParalysisCVAEpilepsy(cellValue);
            return preAuthorizationDetailDto;
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            return errorMessage;
        }
    },DETAILS_OF_PARALYSIS_CVA("Past history of chronic illness(PARALYSIS/CVA) - Please provide details"){
        @Override
        public String getAllowedValue(PreAuthorizationDetailDto preAuthorizationDetailDto) {
            return preAuthorizationDetailDto.getDetailsOfParalysisCVA();
        }

        @Override
        public PreAuthorizationDetailDto populatePreAuthorizationDetail(PreAuthorizationDetailDto preAuthorizationDetailDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            preAuthorizationDetailDto.setDetailsOfParalysisCVA(cellValue);
            return preAuthorizationDetailDto;
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            return errorMessage;
        }
    }, PAST_HISTORY_SUFFERING_FROM_ARTHRITIS("Past history of any chronic illness - Suffering From Arthritis"){
        @Override
        public String getAllowedValue(PreAuthorizationDetailDto preAuthorizationDetailDto) {
            return preAuthorizationDetailDto.getPastHistorySufferingFromArthritis();
        }

        @Override
        public PreAuthorizationDetailDto populatePreAuthorizationDetail(PreAuthorizationDetailDto preAuthorizationDetailDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            preAuthorizationDetailDto.setPastHistorySufferingFromArthritis(cellValue);
            return preAuthorizationDetailDto;
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            return errorMessage;
        }
    }, DETAILS_OF_SUFFERING_FROM_ARTHRITIS("Past history of chronic illness(ARTHRITIS) - Please provide details"){
        @Override
        public String getAllowedValue(PreAuthorizationDetailDto preAuthorizationDetailDto) {
            return preAuthorizationDetailDto.getDetailsOfSufferingFromArthritis();
        }

        @Override
        public PreAuthorizationDetailDto populatePreAuthorizationDetail(PreAuthorizationDetailDto preAuthorizationDetailDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            preAuthorizationDetailDto.setDetailsOfSufferingFromArthritis(cellValue);
            return preAuthorizationDetailDto;
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            return errorMessage;
        }
    }, PAST_HISTORY_SUFFERING_FROM_CANCER_TUMOR_CYST("Past history of any chronic illness - Suffering From Cancer/Tumor/Cyst") {
        @Override
        public String getAllowedValue(PreAuthorizationDetailDto preAuthorizationDetailDto) {
            return preAuthorizationDetailDto.getPastHistorySufferingFromCancerTumorCyst();
        }

        @Override
        public PreAuthorizationDetailDto populatePreAuthorizationDetail(PreAuthorizationDetailDto preAuthorizationDetailDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            preAuthorizationDetailDto.setPastHistorySufferingFromCancerTumorCyst(cellValue);
            return preAuthorizationDetailDto;
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            return errorMessage;
        }
    }, DETAILS_OF_CANCER_TUMOR_CYST("Past history of chronic illness(CANCER/TUMOR/CYST) - Please provide details"){
        @Override
        public String getAllowedValue(PreAuthorizationDetailDto preAuthorizationDetailDto) {
            return preAuthorizationDetailDto.getDetailsOfCancerTumorCyst();
        }

        @Override
        public PreAuthorizationDetailDto populatePreAuthorizationDetail(PreAuthorizationDetailDto preAuthorizationDetailDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            preAuthorizationDetailDto.setDetailsOfCancerTumorCyst(cellValue);
            return preAuthorizationDetailDto;
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            return errorMessage;
        }
    },PAST_HISTORY_SUFFERING_FROM_STD_HIV_AIDS("Past history of any chronic illness - Suffering From STD/HIV/AIDS"){
        @Override
        public String getAllowedValue(PreAuthorizationDetailDto preAuthorizationDetailDto) {
            return preAuthorizationDetailDto.getPastHistorySufferingFromStdHivAids();
        }

        @Override
        public PreAuthorizationDetailDto populatePreAuthorizationDetail(PreAuthorizationDetailDto preAuthorizationDetailDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            preAuthorizationDetailDto.setPastHistorySufferingFromStdHivAids(cellValue);
            return preAuthorizationDetailDto;
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            return errorMessage;
        }
    }, DETAIL_OF_STD_HIV_AIDS("Past history of chronic illness(STD/HIV/AIDS) - Please provide details"){
        @Override
        public String getAllowedValue(PreAuthorizationDetailDto preAuthorizationDetailDto) {
            return preAuthorizationDetailDto.getDetailOfStdHivAids();
        }

        @Override
        public PreAuthorizationDetailDto populatePreAuthorizationDetail(PreAuthorizationDetailDto preAuthorizationDetailDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            preAuthorizationDetailDto.setDetailOfStdHivAids(cellValue);
            return preAuthorizationDetailDto;
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            return errorMessage;
        }
    }, PAST_HISTORY_SUFFERING_FROM_ALCOHOL_DRUG_ABUSE("Past history of any chronic illness - Suffering From Alcohol/Drug Abuse"){
        @Override
        public String getAllowedValue(PreAuthorizationDetailDto preAuthorizationDetailDto) {
            return preAuthorizationDetailDto.getPastHistorySufferingFromAlcoholDrugAbuse();
        }

        @Override
        public PreAuthorizationDetailDto populatePreAuthorizationDetail(PreAuthorizationDetailDto preAuthorizationDetailDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            preAuthorizationDetailDto.setPastHistorySufferingFromAlcoholDrugAbuse(cellValue);
            return preAuthorizationDetailDto;
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Past history - Suffering From Alcohol/Drug Abuse cannot be empty.";
                    return errorMessage;
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage;
            }
            return errorMessage;
        }
    }, DETAIL_OF_ALCOHOL_DRUG_ABUSE("Past history of chronic illness(ALCOHOL/DRUG/ABUSE) - Please provide detail"){
        @Override
        public String getAllowedValue(PreAuthorizationDetailDto preAuthorizationDetailDto) {
            return preAuthorizationDetailDto.getDetailOfAlcoholDrugAbuse();
        }

        @Override
        public PreAuthorizationDetailDto populatePreAuthorizationDetail(PreAuthorizationDetailDto preAuthorizationDetailDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            preAuthorizationDetailDto.setDetailOfAlcoholDrugAbuse(cellValue);
            return preAuthorizationDetailDto;
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            return errorMessage;
        }
    }, PAST_HISTORY_SUFFERING_FROM__PSYCHIATRIC_CONDITION("Past history of any chronic illness - Suffering From Psychiatric Condition"){
        @Override
        public String getAllowedValue(PreAuthorizationDetailDto preAuthorizationDetailDto) {
            return preAuthorizationDetailDto.getPastHistorySufferingFromPsychiatricCondition();
        }

        @Override
        public PreAuthorizationDetailDto populatePreAuthorizationDetail(PreAuthorizationDetailDto preAuthorizationDetailDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            preAuthorizationDetailDto.setPastHistorySufferingFromPsychiatricCondition(cellValue);
            return preAuthorizationDetailDto;
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            return errorMessage;
        }
    }, DETAILS_PSYCHIATRIC_CONDITION("Past history of chronic illness(PSYCHIATRIC/CONDITION) - Please provide details"){
        @Override
        public String getAllowedValue(PreAuthorizationDetailDto preAuthorizationDetailDto) {
            return preAuthorizationDetailDto.getDetailsPsychiatricCondition();
        }

        @Override
        public PreAuthorizationDetailDto populatePreAuthorizationDetail(PreAuthorizationDetailDto preAuthorizationDetailDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            preAuthorizationDetailDto.setDetailsPsychiatricCondition(cellValue);
            return preAuthorizationDetailDto;
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            return errorMessage;
        }
    },SERVICE("Service to be Availed - Service"){
        @Override
        public String getAllowedValue(PreAuthorizationDetailDto preAuthorizationDetailDto) {
            return preAuthorizationDetailDto.getService();
        }

        @Override
        public PreAuthorizationDetailDto populatePreAuthorizationDetail(PreAuthorizationDetailDto preAuthorizationDetailDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            preAuthorizationDetailDto.setService(cellValue);
            return preAuthorizationDetailDto;
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Service to be Availed - Service cannot be empty.";
                    return errorMessage;
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage;
            }
            return errorMessage;
        }
    },TYPE("Service to be Availed - Type"){
        @Override
        public String getAllowedValue(PreAuthorizationDetailDto preAuthorizationDetailDto) {
            return preAuthorizationDetailDto.getType();
        }

        @Override
        public PreAuthorizationDetailDto populatePreAuthorizationDetail(PreAuthorizationDetailDto preAuthorizationDetailDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            preAuthorizationDetailDto.setType(cellValue);
            return preAuthorizationDetailDto;
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Service to be Availed - Type cannot be empty.";
                    return errorMessage;
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage;
            }
            return errorMessage;
        }
    };

    private String description;

    PreAuthorizationExcelHeader(String description){
        this.description = description;
    }

    public static List<String> getAllowedHeaders(){
        return Stream.of(PreAuthorizationExcelHeader.values()).map(PreAuthorizationExcelHeader::getDescription).collect(Collectors.toList());
    }

    public String getDescription() {
        return description;
    }

    public static PreAuthorizationExcelHeader getEnum(String description) {
        notNull(description, "description cannot be empty for HCPRateExcelHeader");
        for (PreAuthorizationExcelHeader preAuthorizationExcelHeader : values()) {
            if (preAuthorizationExcelHeader.description.equalsIgnoreCase(description.trim())) {
                return preAuthorizationExcelHeader;
            }
        }
        throw new IllegalArgumentException(description);
    }

    public abstract String getAllowedValue(PreAuthorizationDetailDto preAuthorizationDetailDto);

    public abstract PreAuthorizationDetailDto populatePreAuthorizationDetail(PreAuthorizationDetailDto preAuthorizationDetailDto, Row row, List<String> headers);

    public abstract String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders);

    public static List<Row> findDuplicateRow(List<Row> dataRowsForDuplicateCheck, Row currentRow, List<String> headers) {
        List<Row> duplicateRows = Lists.newArrayList();
        Cell clientIdCell = currentRow.getCell(headers.indexOf(PreAuthorizationExcelHeader.CLIENT_ID.name()));
        String clientIdCellValue = getCellValue(clientIdCell);
        Cell serviceCell = currentRow.getCell(headers.indexOf(PreAuthorizationExcelHeader.SERVICE.name()));
        String serviceCellValue = getCellValue(serviceCell);
        Map<String,Object> currentRowNameRelationshipHolder = new HashMap<String,Object>();
        currentRowNameRelationshipHolder.put("ClientID",clientIdCellValue);
        currentRowNameRelationshipHolder.put("Service",serviceCellValue);

        dataRowsForDuplicateCheck.forEach(dataRowForDuplicateCheck -> {
            if (currentRow.getRowNum() != dataRowForDuplicateCheck.getRowNum()) {
                Cell otherClientIdCell = currentRow.getCell(headers.indexOf(PreAuthorizationExcelHeader.CLIENT_ID.name()));
                String otherClientIdValue = getCellValue(otherClientIdCell);
                Cell otherServiceCell = currentRow.getCell(headers.indexOf(PreAuthorizationExcelHeader.SERVICE.name()));
                String otherServiceValue = getCellValue(otherServiceCell);
                Map<String,Object> otherRowNameRelationshipHolder = new HashMap<String,Object>();
                currentRowNameRelationshipHolder.put("ClientID",otherClientIdValue);
                currentRowNameRelationshipHolder.put("Service",otherServiceValue);
                if (currentRowNameRelationshipHolder.equals(otherRowNameRelationshipHolder)) {
                    duplicateRows.add(dataRowForDuplicateCheck);
                }
            }
        });
        return duplicateRows;
    }
}
