package com.pla.grouphealth.claim.cashless.application.service.claim;

import com.google.common.collect.Lists;
import com.pla.grouphealth.claim.cashless.presentation.dto.preauthorization.ClaimUploadedExcelDataDto;
import com.pla.grouphealth.policy.domain.model.GroupHealthPolicy;
import com.pla.publishedlanguage.contract.IExcelPropagator;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

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
 * Author - Mohan Sharma Created on 2/3/2016.
 */
public enum GHCashlessClaimExcelHeader {


    HOSPITALIZATION_EVENT("Hospitalization Event"){
        @Override
        public String getAllowedValue(ClaimUploadedExcelDataDto claimUploadedExcelDataDto) {
            return claimUploadedExcelDataDto.getHospitalizationEvent();
        }

        @Override
        public ClaimUploadedExcelDataDto populatePreAuthorizationDetail(ClaimUploadedExcelDataDto claimUploadedExcelDataDto, Row
        row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            claimUploadedExcelDataDto.setHospitalizationEvent(cellValue);
            return claimUploadedExcelDataDto;
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator
        iExcelPropagator, Row row, String value, List<String> excelHeaders, Map dataMap) {
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
        public String getAllowedValue(ClaimUploadedExcelDataDto claimUploadedExcelDataDto) {
            return claimUploadedExcelDataDto.getPolicyNumber();
        }

        @Override
        public ClaimUploadedExcelDataDto populatePreAuthorizationDetail(ClaimUploadedExcelDataDto claimUploadedExcelDataDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            claimUploadedExcelDataDto.setPolicyNumber(cellValue);
            return claimUploadedExcelDataDto;
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders, Map dataMap) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Policy Number cannot be empty.";
                    return errorMessage;
                }
                GroupHealthPolicy groupHealthPolicy = iExcelPropagator.findPolicyByPolicyNumber(value);
                if(isEmpty(groupHealthPolicy)){
                    errorMessage = errorMessage + "No Group Health Policy found with given Policy Number";
                    return errorMessage;
                }
                DateTime expiredOn= groupHealthPolicy.getExpiredOn();
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
        public String getAllowedValue(ClaimUploadedExcelDataDto claimUploadedExcelDataDto) {
            return claimUploadedExcelDataDto.getPolicyNumber();
        }

        @Override
        public ClaimUploadedExcelDataDto populatePreAuthorizationDetail(ClaimUploadedExcelDataDto claimUploadedExcelDataDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            try {
                cellValue = isNotEmpty(cellValue) ? String.valueOf( new BigDecimal(cellValue).intValue()) : cellValue;
            } catch(NumberFormatException e){
                claimUploadedExcelDataDto.setClientId(cellValue);
                return claimUploadedExcelDataDto;
            }
            claimUploadedExcelDataDto.setClientId(cellValue);
            return claimUploadedExcelDataDto;
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders, Map dataMap) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Client ID cannot be empty.";
                    return errorMessage;
                }
                Cell policyNumberCell = row.getCell(excelHeaders.indexOf(POLICY_NUMBER.description));
                String policyNumberValue = getCellValue(policyNumberCell);
                try {
                    value = String.valueOf( new BigDecimal(value).intValue());
                } catch(NumberFormatException e){}
                if(!iExcelPropagator.checkIfClientBelongsToTheGivenPolicy(value, policyNumberValue)){
                    errorMessage = errorMessage + "Client not covered under the mentioned Policy";
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage;
            }
            return errorMessage;
        }
    },CONSULTATION_DATE("Date Of Consultation"){
        @Override
        public String getAllowedValue(ClaimUploadedExcelDataDto claimUploadedExcelDataDto) {
            return isNotEmpty(claimUploadedExcelDataDto.getConsultationDate()) ? claimUploadedExcelDataDto.getConsultationDate().toString() : "";
        }

        @Override
        public ClaimUploadedExcelDataDto populatePreAuthorizationDetail(ClaimUploadedExcelDataDto claimUploadedExcelDataDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            claimUploadedExcelDataDto.setConsultationDate(isNotEmpty(cellValue) ? DateTimeFormat.forPattern("dd/MM/yyyy").parseLocalDate(cellValue) : null);
            return claimUploadedExcelDataDto;
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders, Map dataMap) {
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
        public String getAllowedValue(ClaimUploadedExcelDataDto claimUploadedExcelDataDto) {
            return claimUploadedExcelDataDto.getTreatingDoctorName();
        }

        @Override
        public ClaimUploadedExcelDataDto populatePreAuthorizationDetail(ClaimUploadedExcelDataDto claimUploadedExcelDataDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            claimUploadedExcelDataDto.setTreatingDoctorName(cellValue);
            return claimUploadedExcelDataDto;
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders, Map dataMap) {
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
        public String getAllowedValue(ClaimUploadedExcelDataDto claimUploadedExcelDataDto) {
            return claimUploadedExcelDataDto.getDoctorContactNumber();
        }

        @Override
        public ClaimUploadedExcelDataDto populatePreAuthorizationDetail(ClaimUploadedExcelDataDto claimUploadedExcelDataDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            try {
                cellValue = isNotEmpty(cellValue) ? String.valueOf( new BigDecimal(cellValue).intValue()) : cellValue;
            } catch(NumberFormatException e){
                claimUploadedExcelDataDto.setDoctorContactNumber(cellValue);
                return claimUploadedExcelDataDto;
            }
            claimUploadedExcelDataDto.setDoctorContactNumber(cellValue);
            return claimUploadedExcelDataDto;
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders, Map dataMap) {
            return "";
        }
    },REASONS("Please indicate whether it is a"){
        @Override
        public String getAllowedValue(ClaimUploadedExcelDataDto claimUploadedExcelDataDto) {
            return claimUploadedExcelDataDto.getReasons();
        }

        @Override
        public ClaimUploadedExcelDataDto populatePreAuthorizationDetail(ClaimUploadedExcelDataDto claimUploadedExcelDataDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            claimUploadedExcelDataDto.setReasons(cellValue);
            return claimUploadedExcelDataDto;
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders, Map dataMap) {
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
        public String getAllowedValue(ClaimUploadedExcelDataDto claimUploadedExcelDataDto) {
            return claimUploadedExcelDataDto.getPregnancyG();
        }

        @Override
        public ClaimUploadedExcelDataDto populatePreAuthorizationDetail(ClaimUploadedExcelDataDto claimUploadedExcelDataDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            claimUploadedExcelDataDto.setPregnancyG(cellValue);
            return claimUploadedExcelDataDto;
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders, Map dataMap) {
            String errorMessage = "";
            Cell reasonCell = row.getCell(excelHeaders.indexOf(REASONS.description));
            String reason = getCellValue(reasonCell);
            if(reason.equalsIgnoreCase("Pregnancy")){
                if(isEmpty(value)) {
                    errorMessage  = "As you selected pregnancy Diagnosis/Treatment in case Of Pregnancy -G cannot be empty";
                    return errorMessage;
                }
            }
            return errorMessage;
        }
    },PREGNANCY_P("Diagnosis/Treatment in case Of Pregnancy"){
        @Override
        public String getAllowedValue(ClaimUploadedExcelDataDto claimUploadedExcelDataDto) {
            return claimUploadedExcelDataDto.getPregnancyP();
        }

        @Override
        public ClaimUploadedExcelDataDto populatePreAuthorizationDetail(ClaimUploadedExcelDataDto claimUploadedExcelDataDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            claimUploadedExcelDataDto.setPregnancyP(cellValue);
            return claimUploadedExcelDataDto;
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders, Map dataMap) {
            //first find reason
            //check if reason is pregnancy
            //if pregnancy make mandatory
            //if(reason==pregnancy){isempty(value)} // return errorMessage;
            String errorMessage = "";
            Cell reasonCell= row.getCell(excelHeaders.indexOf(REASONS.getDescription()));
            String reasonValue = getCellValue(reasonCell);
            if (reasonValue.equalsIgnoreCase("Pregnancy") && isEmpty(value)){
                errorMessage  = "As you selected pregnancy Diagnosis/Treatment in case Of Pregnancy -p cannot be empty";
                return errorMessage;
            }
            return  errorMessage;
        }
    },PREGNANCY_L("Diagnosis/Treatment in case Of Pregnancy -L"){
        @Override
        public String getAllowedValue(ClaimUploadedExcelDataDto claimUploadedExcelDataDto) {
            return claimUploadedExcelDataDto.getPregnancyL();
        }

        @Override
        public ClaimUploadedExcelDataDto populatePreAuthorizationDetail(ClaimUploadedExcelDataDto claimUploadedExcelDataDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            claimUploadedExcelDataDto.setPregnancyL(cellValue);
            return claimUploadedExcelDataDto;
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders, Map dataMap) {
            String errorMessage = "";
            Cell reasonCell= row.getCell(excelHeaders.indexOf(REASONS.getDescription()));
            String reasonValue = getCellValue(reasonCell);
            if (reasonValue.equalsIgnoreCase("Pregnancy") && isEmpty(value)){
                errorMessage  = "As you selected pregnancy Diagnosis/Treatment in case Of Pregnancy -L cannot be empty";
                return errorMessage;
            }
            return  errorMessage;

        }
    },PREGNANCY_A("Diagnosis/Treatment in case Of Pregnancy -A"){
        @Override
        public String getAllowedValue(ClaimUploadedExcelDataDto claimUploadedExcelDataDto) {
            return claimUploadedExcelDataDto.getPregnancyA();
        }

        @Override
        public ClaimUploadedExcelDataDto populatePreAuthorizationDetail(ClaimUploadedExcelDataDto claimUploadedExcelDataDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            claimUploadedExcelDataDto.setPregnancyA(cellValue);
            return claimUploadedExcelDataDto;
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders, Map dataMap) {
            String errorMessage = "";
            Cell reasonCell= row.getCell(excelHeaders.indexOf(REASONS.getDescription()));
            String reasonValue = getCellValue(reasonCell);
            if (reasonValue.equalsIgnoreCase("Pregnancy") && isEmpty(value)){
                errorMessage  = "As you selected pregnancy Diagnosis/Treatment in case Of Pregnancy -A cannot be empty";
                return errorMessage;
            }
            return  errorMessage;
        }
    },PREGNANCY_DATE_OF_DELIVERY("Diagnosis/Treatment in case Of Pregnancy- Date of Delivery"){
        @Override
        public String getAllowedValue(ClaimUploadedExcelDataDto claimUploadedExcelDataDto) {
            return isNotEmpty(claimUploadedExcelDataDto.getPregnancyDateOfDelivery()) ? claimUploadedExcelDataDto.getPregnancyDateOfDelivery().toString() : "";
        }

        @Override
        public ClaimUploadedExcelDataDto populatePreAuthorizationDetail(ClaimUploadedExcelDataDto claimUploadedExcelDataDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            claimUploadedExcelDataDto.setPregnancyDateOfDelivery(isNotEmpty(cellValue) ? DateTimeFormat.forPattern("dd/MM/yyyy").parseLocalDate(cellValue) : null);
            return claimUploadedExcelDataDto;
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders, Map dataMap) {
            String errorMessage = "";
            Cell reasonCell= row.getCell(excelHeaders.indexOf(REASONS.getDescription()));
            String reasonValue = getCellValue(reasonCell);
            if (reasonValue.equalsIgnoreCase("Pregnancy") && isEmpty(value)){
                errorMessage  = "As you selected pregnancy Diagnosis/Treatment in case Of Pregnancy Date of Delivery cannot be empty";
                return errorMessage;
            }
            return  errorMessage;
        }
    },PREGNANCY_MODE_OF_DELIVERY("Diagnosis/Treatment in case Of Pregnancy- Mode Of Delivery"){
        @Override
        public String getAllowedValue(ClaimUploadedExcelDataDto claimUploadedExcelDataDto) {
            return claimUploadedExcelDataDto.getPregnancyModeOfDelivery();
        }

        @Override
        public ClaimUploadedExcelDataDto populatePreAuthorizationDetail(ClaimUploadedExcelDataDto claimUploadedExcelDataDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            claimUploadedExcelDataDto.setPregnancyModeOfDelivery(cellValue);
            return claimUploadedExcelDataDto;
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders, Map dataMap) {
            String errorMessage = "";
            Cell reasonCell= row.getCell(excelHeaders.indexOf(REASONS.getDescription()));
            String reasonValue = getCellValue(reasonCell);
            if (reasonValue.equalsIgnoreCase("Pregnancy") && isEmpty(value)){
                errorMessage  = "As you selected pregnancy Diagnosis/Treatment in case Of Pregnancy Mode Of Delivery cannot be empty";
                return errorMessage;
            }
            return  errorMessage;
        }
    },DIAGNOSIS_TREATMENT_ILLNESS_TRAUMA_ILLNESS_DISEASE_NAME_AND_PRESENTING_COMPLAINTS("Diagnosis/Treatment in case Of Illness Or Trauma - Name of illness / disease with presenting complaints"){
        @Override
        public String getAllowedValue(ClaimUploadedExcelDataDto claimUploadedExcelDataDto) {
            return claimUploadedExcelDataDto.getDiagnosisTreatmentIllnessTraumaIllnessDiseaseNameAndPresentingComplaints();
        }

        @Override
        public ClaimUploadedExcelDataDto populatePreAuthorizationDetail(ClaimUploadedExcelDataDto claimUploadedExcelDataDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            claimUploadedExcelDataDto.setDiagnosisTreatmentIllnessTraumaIllnessDiseaseNameAndPresentingComplaints(cellValue);
            return claimUploadedExcelDataDto;
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders, Map dataMap) {
            String errorMessage = "";
            Cell reasonCell= row.getCell(excelHeaders.indexOf(REASONS.getDescription()));
            String reasonValue = getCellValue(reasonCell);
            if( (reasonValue.equalsIgnoreCase("Trauma")|| reasonValue.equalsIgnoreCase("Illness") ) && isEmpty(value)){
                errorMessage  = "As you selected Diagnosis/Treatment in case Of Illness Or Trauma   Name of illness  cannot be empty";
                return errorMessage;
            }
            return  errorMessage;
        }
    },DIAGNOSIS_TREATMENT_ILLNESS_TRAUMA_RELEVANT_CLINICAL_FINDINGS("Diagnosis/Treatment in case Of Illness Or Trauma - Relevant clinical findings"){
        @Override
        public String getAllowedValue(ClaimUploadedExcelDataDto claimUploadedExcelDataDto) {
            return claimUploadedExcelDataDto.getDiagnosisTreatmentIllnessTraumaRelevantClinicalFindings();
        }

        @Override
        public ClaimUploadedExcelDataDto populatePreAuthorizationDetail(ClaimUploadedExcelDataDto claimUploadedExcelDataDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            claimUploadedExcelDataDto.setDiagnosisTreatmentIllnessTraumaRelevantClinicalFindings(cellValue);
            return claimUploadedExcelDataDto;
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders, Map dataMap) {
            String errorMessage = "";
            Cell reasonCell= row.getCell(excelHeaders.indexOf(REASONS.getDescription()));
            String reasonValue = getCellValue(reasonCell);
            if( (reasonValue.equalsIgnoreCase("Trauma")|| reasonValue.equalsIgnoreCase("Illness") ) && isEmpty(value)){
                errorMessage  = "As you selected Diagnosis/Treatment in case Of Illness Or Trauma   Relevant clinical  cannot be empty";
                return errorMessage;
            }
            return  errorMessage;
        }
    },DIAGNOSIS_TREATMENT_ILLNESS_TRAUMA_PRESENT_AILMENT_DURATION("Diagnosis/Treatment in case Of Illness Or Trauma -Duration of the present ailment in days"){
        @Override
        public String getAllowedValue(ClaimUploadedExcelDataDto claimUploadedExcelDataDto) {
            return claimUploadedExcelDataDto.getDiagnosisTreatmentIllnessTraumaPresentAilmentDuration();
        }

        @Override
        public ClaimUploadedExcelDataDto populatePreAuthorizationDetail(ClaimUploadedExcelDataDto claimUploadedExcelDataDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            claimUploadedExcelDataDto.setDiagnosisTreatmentIllnessTraumaPresentAilmentDuration(cellValue);
            return claimUploadedExcelDataDto;
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders, Map dataMap) {
            String errorMessage = "";
            Cell reasonCell= row.getCell(excelHeaders.indexOf(REASONS.getDescription()));
            String reasonValue = getCellValue(reasonCell);
            if( (reasonValue.equalsIgnoreCase("Trauma")|| reasonValue.equalsIgnoreCase("Illness") ) && isEmpty(value)){
                errorMessage  = "As you selected Diagnosis/Treatment in case Of Illness Or Trauma   Duration of the present ailment in days cannot be empty";
                return errorMessage;
            }
            return  errorMessage;
        }
    },DIAGNOSIS_TREATMENT_ILLNESS_TRAUMA_PRESENT_AILMENT_PAST_HISTORY("Diagnosis/Treatment in case Of Illness Or Trauma - Past history of present ailment if any"){
        @Override
        public String getAllowedValue(ClaimUploadedExcelDataDto claimUploadedExcelDataDto) {
            return claimUploadedExcelDataDto.getDiagnosisTreatmentIllnessTraumaPresentAilmentPastHistory();
        }

        @Override
        public ClaimUploadedExcelDataDto populatePreAuthorizationDetail(ClaimUploadedExcelDataDto claimUploadedExcelDataDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            claimUploadedExcelDataDto.setDiagnosisTreatmentIllnessTraumaPresentAilmentPastHistory(cellValue);
            return claimUploadedExcelDataDto;
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders, Map dataMap) {
            String errorMessage = "";
            Cell reasonCell= row.getCell(excelHeaders.indexOf(REASONS.getDescription()));
            String reasonValue = getCellValue(reasonCell);
            if( (reasonValue.equalsIgnoreCase("Trauma")|| reasonValue.equalsIgnoreCase("Illness") ) && isEmpty(value)){
                errorMessage  = "As you selected Diagnosis/Treatment in case Of Illness Or Trauma Past history of present ailment  cannot be empty";
                return errorMessage;
            }
            return  errorMessage;
        }
    },DIAGNOSIS_TREATMENT_ILLNESS_TRAUMA_DIAGNOSIS("Diagnosis/Treatment in case Of Illness Or Trauma - Provisional Diagnosis"){
        @Override
        public String getAllowedValue(ClaimUploadedExcelDataDto claimUploadedExcelDataDto) {
            return claimUploadedExcelDataDto.getDiagnosisTreatmentIllnessTraumaDiagnosis();
        }

        @Override
        public ClaimUploadedExcelDataDto populatePreAuthorizationDetail(ClaimUploadedExcelDataDto claimUploadedExcelDataDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            claimUploadedExcelDataDto.setDiagnosisTreatmentIllnessTraumaDiagnosis(cellValue);
            return claimUploadedExcelDataDto;
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders, Map dataMap) {
            String errorMessage = "";
            Cell reasonCell= row.getCell(excelHeaders.indexOf(REASONS.getDescription()));
            String reasonValue = getCellValue(reasonCell);
            if( (reasonValue.equalsIgnoreCase("Trauma")|| reasonValue.equalsIgnoreCase("Illness") ) && isEmpty(value)){
                errorMessage  = "As you selected Diagnosis/Treatment in case Of Illness Or Trauma Provisional Diagnosis  cannot be empty";
                return errorMessage;
            }
            return  errorMessage;
        }
    },DIAGNOSIS_TREATMENT_LINE_OF_TREATMENT("Diagnosis/Treatment - Line of treatment"){
        @Override
        public String getAllowedValue(ClaimUploadedExcelDataDto claimUploadedExcelDataDto) {
            return claimUploadedExcelDataDto.getDiagnosisTreatmentLineOfTreatment();
        }

        @Override
        public ClaimUploadedExcelDataDto populatePreAuthorizationDetail(ClaimUploadedExcelDataDto claimUploadedExcelDataDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            claimUploadedExcelDataDto.setDiagnosisTreatmentLineOfTreatment(cellValue);
            return claimUploadedExcelDataDto;
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders, Map dataMap) {
            String errorMessage = "";
            Cell reasonCell= row.getCell(excelHeaders.indexOf(REASONS.getDescription()));
            String reasonValue = getCellValue(reasonCell);
            if( (reasonValue.equalsIgnoreCase("Trauma")|| reasonValue.equalsIgnoreCase("Illness") ) && isEmpty(value)){
                errorMessage  = "As you selected Diagnosis/Treatment in case Of Illness Or Trauma Line of treatment cannot be empty";
                return errorMessage;
            }

            return errorMessage;
        }
    },DIAGNOSIS_TREATMENT_TEST("Diagnosis/Treatment - If Investigations, indicate tests"){
        @Override
        public String getAllowedValue(ClaimUploadedExcelDataDto claimUploadedExcelDataDto) {
            return claimUploadedExcelDataDto.getDiagnosisTreatmentTest();
        }

        @Override
        public ClaimUploadedExcelDataDto populatePreAuthorizationDetail(ClaimUploadedExcelDataDto claimUploadedExcelDataDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            claimUploadedExcelDataDto.setDiagnosisTreatmentTest(cellValue);
            return claimUploadedExcelDataDto;
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders, Map dataMap) {
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
        public String getAllowedValue(ClaimUploadedExcelDataDto claimUploadedExcelDataDto) {
            return claimUploadedExcelDataDto.getDiagnosisTreatmentDrugName();
        }

        @Override
        public ClaimUploadedExcelDataDto populatePreAuthorizationDetail(ClaimUploadedExcelDataDto claimUploadedExcelDataDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            claimUploadedExcelDataDto.setDiagnosisTreatmentDrugName(cellValue);
            return claimUploadedExcelDataDto;
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders, Map dataMap) {
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
        public String getAllowedValue(ClaimUploadedExcelDataDto claimUploadedExcelDataDto) {
            return claimUploadedExcelDataDto.getDiagnosisTreatmentDrugType();
        }

        @Override
        public ClaimUploadedExcelDataDto populatePreAuthorizationDetail(ClaimUploadedExcelDataDto claimUploadedExcelDataDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            claimUploadedExcelDataDto.setDiagnosisTreatmentDrugType(cellValue);
            return claimUploadedExcelDataDto;
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders, Map dataMap) {
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
        public String getAllowedValue(ClaimUploadedExcelDataDto claimUploadedExcelDataDto) {
            return claimUploadedExcelDataDto.getDiagnosisTreatmentDrugDosage();
        }

        @Override
        public ClaimUploadedExcelDataDto populatePreAuthorizationDetail(ClaimUploadedExcelDataDto claimUploadedExcelDataDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            claimUploadedExcelDataDto.setDiagnosisTreatmentDrugDosage(String.valueOf(new BigDecimal(cellValue).intValue()));
            return claimUploadedExcelDataDto;
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders, Map dataMap) {
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
        public String getAllowedValue(ClaimUploadedExcelDataDto claimUploadedExcelDataDto) {
            return claimUploadedExcelDataDto.getDiagnosisTreatmentDrugStrength();
        }

        @Override
        public ClaimUploadedExcelDataDto populatePreAuthorizationDetail(ClaimUploadedExcelDataDto claimUploadedExcelDataDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            claimUploadedExcelDataDto.setDiagnosisTreatmentDrugStrength(String.valueOf(new BigDecimal(cellValue).intValue()));
            return claimUploadedExcelDataDto;
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders, Map dataMap) {
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
        public String getAllowedValue(ClaimUploadedExcelDataDto claimUploadedExcelDataDto) {
            return claimUploadedExcelDataDto.getDiagnosisTreatmentMedicalDuration();
        }

        @Override
        public ClaimUploadedExcelDataDto populatePreAuthorizationDetail(ClaimUploadedExcelDataDto claimUploadedExcelDataDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            claimUploadedExcelDataDto.setDiagnosisTreatmentMedicalDuration(String.valueOf(new BigDecimal(cellValue).intValue()));
            return claimUploadedExcelDataDto;
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders, Map dataMap) {
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
        public String getAllowedValue(ClaimUploadedExcelDataDto claimUploadedExcelDataDto) {
            return claimUploadedExcelDataDto.getDiagnosisTreatmentSurgeryName();
        }

        @Override
        public ClaimUploadedExcelDataDto populatePreAuthorizationDetail(ClaimUploadedExcelDataDto claimUploadedExcelDataDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            claimUploadedExcelDataDto.setDiagnosisTreatmentSurgeryName(cellValue);
            return claimUploadedExcelDataDto;
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders, Map dataMap) {
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
        public String getAllowedValue(ClaimUploadedExcelDataDto claimUploadedExcelDataDto) {
            return claimUploadedExcelDataDto.getDiagnosisTreatmentSurgeryAccommodationType();
        }

        @Override
        public ClaimUploadedExcelDataDto populatePreAuthorizationDetail(ClaimUploadedExcelDataDto claimUploadedExcelDataDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            claimUploadedExcelDataDto.setDiagnosisTreatmentSurgeryAccommodationType(cellValue);
            return claimUploadedExcelDataDto;
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders, Map dataMap) {
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
        public String getAllowedValue(ClaimUploadedExcelDataDto claimUploadedExcelDataDto) {
            return isNotEmpty(claimUploadedExcelDataDto.getDiagnosisTreatmentSurgeryDateOfAdmission()) ? claimUploadedExcelDataDto.getDiagnosisTreatmentSurgeryDateOfAdmission().toString() : "";
        }

        @Override
        public ClaimUploadedExcelDataDto populatePreAuthorizationDetail(ClaimUploadedExcelDataDto claimUploadedExcelDataDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            claimUploadedExcelDataDto.setDiagnosisTreatmentSurgeryDateOfAdmission(isNotEmpty(cellValue) ? DateTimeFormat.forPattern("dd/MM/yyyy").parseLocalDate(cellValue) : null);
            return claimUploadedExcelDataDto;
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders, Map dataMap) {
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
        public String getAllowedValue(ClaimUploadedExcelDataDto preAuthorizationDetailDto) {
            return isNotEmpty(preAuthorizationDetailDto.getDiagnosisTreatmentSurgeryDateOfDischarge()) ? preAuthorizationDetailDto.getDiagnosisTreatmentSurgeryDateOfDischarge().toString() : "";
        }

        @Override
        public ClaimUploadedExcelDataDto populatePreAuthorizationDetail(ClaimUploadedExcelDataDto preAuthorizationDetailDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            preAuthorizationDetailDto.setDiagnosisTreatmentSurgeryDateOfDischarge(isNotEmpty(cellValue) ? DateTime.parse(cellValue, DateTimeFormat.forPattern(AppConstants.DD_MM_YYY_FORMAT)) : null);
            return preAuthorizationDetailDto;
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders, Map dataMap) {
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
        public String getAllowedValue(ClaimUploadedExcelDataDto claimUploadedExcelDataDto) {
            return isNotEmpty(claimUploadedExcelDataDto.getDiagnosisTreatmentSurgeryLengthOStay()) ? String.valueOf(claimUploadedExcelDataDto.getDiagnosisTreatmentSurgeryLengthOStay()) : "";
        }

        @Override
        public ClaimUploadedExcelDataDto populatePreAuthorizationDetail(ClaimUploadedExcelDataDto claimUploadedExcelDataDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            claimUploadedExcelDataDto.setDiagnosisTreatmentSurgeryLengthOStay(isNotEmpty(cellValue) ? new BigDecimal(cellValue).intValue() : null);
            return claimUploadedExcelDataDto;
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders, Map dataMap) {
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
        public String getAllowedValue(ClaimUploadedExcelDataDto claimUploadedExcelDataDto) {
            return claimUploadedExcelDataDto.getPastHistorySufferingFromHTN();
        }

        @Override
        public ClaimUploadedExcelDataDto populatePreAuthorizationDetail(ClaimUploadedExcelDataDto claimUploadedExcelDataDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            claimUploadedExcelDataDto.setPastHistorySufferingFromHTN(cellValue);
            return claimUploadedExcelDataDto;
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders, Map dataMap) {
            String errorMessage = "";
            return errorMessage;
        }
    },DETAILS_OF_HTN("Past history of chronic illness(HTN) - Please provide details"){
        @Override
        public String getAllowedValue(ClaimUploadedExcelDataDto claimUploadedExcelDataDto) {
            return claimUploadedExcelDataDto.getDetailsOfHTN();
        }

        @Override
        public ClaimUploadedExcelDataDto populatePreAuthorizationDetail(ClaimUploadedExcelDataDto claimUploadedExcelDataDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            claimUploadedExcelDataDto.setDetailsOfHTN(cellValue);
            return claimUploadedExcelDataDto;
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders, Map dataMap) {
            String errorMessage = "";
            Cell pastHistoryHTNCell= row.getCell(excelHeaders.indexOf(PAST_HISTORY_SUFFERING_FROM_HTN.getDescription()));
            String pastHistoryHTNCellValue = getCellValue(pastHistoryHTNCell);
            if( pastHistoryHTNCellValue.equalsIgnoreCase("YES") && isEmpty(value)){
                errorMessage  = "As you selected Past history of chronic illness(HTN) YES. So It should be Mandatory";
                return errorMessage;
            }
            return  errorMessage;

        }
    },PAST_HISTORY_SUFFERING_FROM_IHD_CAD("Past history of any chronic illness - Suffering From IHD/CAD"){
        @Override
        public String getAllowedValue(ClaimUploadedExcelDataDto claimUploadedExcelDataDto) {
            return claimUploadedExcelDataDto.getPastHistorySufferingFromIHCCAD();
        }

        @Override
        public ClaimUploadedExcelDataDto populatePreAuthorizationDetail(ClaimUploadedExcelDataDto claimUploadedExcelDataDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            claimUploadedExcelDataDto.setPastHistorySufferingFromIHCCAD(cellValue);
            return claimUploadedExcelDataDto;
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders, Map dataMap) {
            return "";
        }
    },DETAILS_OF_IHD_CAD("Past history of chronic illness(IHD/CAD) - Please provide details"){
        @Override
        public String getAllowedValue(ClaimUploadedExcelDataDto claimUploadedExcelDataDto) {
            return claimUploadedExcelDataDto.getDetailsOfIHDCAD();
        }

        @Override
        public ClaimUploadedExcelDataDto populatePreAuthorizationDetail(ClaimUploadedExcelDataDto claimUploadedExcelDataDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            claimUploadedExcelDataDto.setDetailsOfIHDCAD(cellValue);
            return claimUploadedExcelDataDto;
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders, Map dataMap) {
            String errorMessage = "";
            Cell pastHistoryIHDCADCell= row.getCell(excelHeaders.indexOf(PAST_HISTORY_SUFFERING_FROM_IHD_CAD.getDescription()));
            String pastHistoryIHDCADCellValue = getCellValue(pastHistoryIHDCADCell);
            if( pastHistoryIHDCADCellValue.equalsIgnoreCase("YES") && isEmpty(value)){
                errorMessage  = "As you selected Past history of chronic illness(IHD/CAD) YES. So It should be Mandatory";
                return errorMessage;
            }
            return  errorMessage;

        }
    },PAST_HISTORY_SUFFERING_FROM_DIABETES("Past history of any chronic illness - Suffering From Diabetes"){
        @Override
        public String getAllowedValue(ClaimUploadedExcelDataDto claimUploadedExcelDataDto) {
            return claimUploadedExcelDataDto.getPastHistorySufferingFromDiabetes();
        }

        @Override
        public ClaimUploadedExcelDataDto populatePreAuthorizationDetail(ClaimUploadedExcelDataDto claimUploadedExcelDataDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            claimUploadedExcelDataDto.setPastHistorySufferingFromDiabetes(cellValue);
            return claimUploadedExcelDataDto;
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders, Map dataMap) {
            String errorMessage = "";
            return errorMessage;
        }
    },DETAILS_OF_DIABETES("Past history of chronic illness(DIABETES) - Please provide details"){
        @Override
        public String getAllowedValue(ClaimUploadedExcelDataDto claimUploadedExcelDataDto) {
            return claimUploadedExcelDataDto.getDetailsOfDiabetes();
        }

        @Override
        public ClaimUploadedExcelDataDto populatePreAuthorizationDetail(ClaimUploadedExcelDataDto claimUploadedExcelDataDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            claimUploadedExcelDataDto.setDetailsOfDiabetes(cellValue);
            return claimUploadedExcelDataDto;
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders, Map dataMap) {
            String errorMessage = "";
            Cell pastHistoryDiabetesCell= row.getCell(excelHeaders.indexOf(PAST_HISTORY_SUFFERING_FROM_DIABETES.getDescription()));
            String pastHistoryDiabetesCellValue = getCellValue(pastHistoryDiabetesCell);
            if( pastHistoryDiabetesCellValue.equalsIgnoreCase("YES") && isEmpty(value)){
                errorMessage  = "As you selected Past history of chronic illness(DIABETES) YES. So It should be Mandatory";
                return errorMessage;
            }
            return  errorMessage;

        }
    }, PAST_HISTORY_SUFFERING_FROM_ASTHMA_COPD_TB("Past history of any chronic illness - Suffering From Asthma/COPD/TB"){
        @Override
        public String getAllowedValue(ClaimUploadedExcelDataDto claimUploadedExcelDataDto) {
            return claimUploadedExcelDataDto.getPastHistorySufferingFromAsthmaCOPDTB();
        }

        @Override
        public ClaimUploadedExcelDataDto populatePreAuthorizationDetail(ClaimUploadedExcelDataDto claimUploadedExcelDataDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            claimUploadedExcelDataDto.setPastHistorySufferingFromAsthmaCOPDTB(cellValue);
            return claimUploadedExcelDataDto;
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders, Map dataMap) {
            return "";
        }
    },DETAILS_OF_ASTHMA_COPD_TB("Past history of chronic illness(ASTHMA/COPD/TB) - Please provide details"){
        @Override
        public String getAllowedValue(ClaimUploadedExcelDataDto claimUploadedExcelDataDto) {
            return claimUploadedExcelDataDto.getDetailsOfAsthmaCOPDTB();
        }

        @Override
        public ClaimUploadedExcelDataDto populatePreAuthorizationDetail(ClaimUploadedExcelDataDto claimUploadedExcelDataDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            claimUploadedExcelDataDto.setDetailsOfAsthmaCOPDTB(cellValue);
            return claimUploadedExcelDataDto;
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders, Map dataMap) {
            String errorMessage = "";
            Cell pastHistoryAsthmaCell= row.getCell(excelHeaders.indexOf(PAST_HISTORY_SUFFERING_FROM_ASTHMA_COPD_TB.getDescription()));
            String pastHistoryAsthmaCellValue = getCellValue(pastHistoryAsthmaCell);
            if( pastHistoryAsthmaCellValue.equalsIgnoreCase("YES") && isEmpty(value)){
                errorMessage  = "As you selected Past history of chronic illness(ASTHMA/COPD/TB) YES. So It should be Mandatory";
                return errorMessage;
            }
            return  errorMessage;

        }
    }, PAST_HISTORY_SUFFERING_FROM_PARALYSIS_CVA_EPILEPSY("Past history of any chronic illness - Suffering From Paralysis/CVA/Epilepsy"){
        @Override
        public String getAllowedValue(ClaimUploadedExcelDataDto claimUploadedExcelDataDto) {
            return claimUploadedExcelDataDto.getPastHistorySufferingFromParalysisCVAEpilepsy();
        }

        @Override
        public ClaimUploadedExcelDataDto populatePreAuthorizationDetail(ClaimUploadedExcelDataDto claimUploadedExcelDataDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            claimUploadedExcelDataDto.setPastHistorySufferingFromParalysisCVAEpilepsy(cellValue);
            return claimUploadedExcelDataDto;
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders, Map dataMap) {
            return "";
        }
    },DETAILS_OF_PARALYSIS_CVA("Past history of chronic illness(PARALYSIS/CVA) - Please provide details"){
        @Override
        public String getAllowedValue(ClaimUploadedExcelDataDto claimUploadedExcelDataDto) {
            return claimUploadedExcelDataDto.getDetailsOfParalysisCVA();
        }

        @Override
        public ClaimUploadedExcelDataDto populatePreAuthorizationDetail(ClaimUploadedExcelDataDto claimUploadedExcelDataDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            claimUploadedExcelDataDto.setDetailsOfParalysisCVA(cellValue);
            return claimUploadedExcelDataDto;
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders, Map dataMap) {
            String errorMessage = "";
            Cell pastHistoryParalysisCell= row.getCell(excelHeaders.indexOf(PAST_HISTORY_SUFFERING_FROM_PARALYSIS_CVA_EPILEPSY.getDescription()));
            String pastHistoryParalysisCellValue = getCellValue(pastHistoryParalysisCell);
            if( pastHistoryParalysisCellValue.equalsIgnoreCase("YES") && isEmpty(value)){
                errorMessage  = "As you selected Past history of chronic illness(PARALYSIS/CVA) YES. So It should be Mandatory";
                return errorMessage;
            }
            return  errorMessage;
        }
    }, PAST_HISTORY_SUFFERING_FROM_ARTHRITIS("Past history of any chronic illness - Suffering From Arthritis"){
        @Override
        public String getAllowedValue(ClaimUploadedExcelDataDto claimUploadedExcelDataDto) {
            return claimUploadedExcelDataDto.getPastHistorySufferingFromArthritis();
        }

        @Override
        public ClaimUploadedExcelDataDto populatePreAuthorizationDetail(ClaimUploadedExcelDataDto claimUploadedExcelDataDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            claimUploadedExcelDataDto.setPastHistorySufferingFromArthritis(cellValue);
            return claimUploadedExcelDataDto;
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders, Map dataMap) {
            return "";
        }
    }, DETAILS_OF_SUFFERING_FROM_ARTHRITIS("Past history of chronic illness(ARTHRITIS) - Please provide details"){
        @Override
        public String getAllowedValue(ClaimUploadedExcelDataDto claimUploadedExcelDataDto) {
            return claimUploadedExcelDataDto.getDetailsOfSufferingFromArthritis();
        }

        @Override
        public ClaimUploadedExcelDataDto populatePreAuthorizationDetail(ClaimUploadedExcelDataDto claimUploadedExcelDataDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            claimUploadedExcelDataDto.setDetailsOfSufferingFromArthritis(cellValue);
            return claimUploadedExcelDataDto;
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders, Map dataMap) {
            String errorMessage = "";
            Cell pastHistoryArthritisCell= row.getCell(excelHeaders.indexOf(PAST_HISTORY_SUFFERING_FROM_ARTHRITIS.getDescription()));
            String pastHistoryArthritisCellValue = getCellValue(pastHistoryArthritisCell);
            if( pastHistoryArthritisCellValue.equalsIgnoreCase("YES") && isEmpty(value)){
                errorMessage  = "As you selected Past history of chronic illness(Arthritis) YES. So It should be Mandatory";
                return errorMessage;
            }
            return  errorMessage;
        }
    }, PAST_HISTORY_SUFFERING_FROM_CANCER_TUMOR_CYST("Past history of any chronic illness - Suffering From Cancer/Tumor/Cyst") {
        @Override
        public String getAllowedValue(ClaimUploadedExcelDataDto claimUploadedExcelDataDto) {
            return claimUploadedExcelDataDto.getPastHistorySufferingFromCancerTumorCyst();
        }

        @Override
        public ClaimUploadedExcelDataDto populatePreAuthorizationDetail(ClaimUploadedExcelDataDto claimUploadedExcelDataDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            claimUploadedExcelDataDto.setPastHistorySufferingFromCancerTumorCyst(cellValue);
            return claimUploadedExcelDataDto;
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders, Map dataMap) {
            return "";
        }
    }, DETAILS_OF_CANCER_TUMOR_CYST("Past history of chronic illness(CANCER/TUMOR/CYST) - Please provide details"){
        @Override
        public String getAllowedValue(ClaimUploadedExcelDataDto claimUploadedExcelDataDto) {
            return claimUploadedExcelDataDto.getDetailsOfCancerTumorCyst();
        }

        @Override
        public ClaimUploadedExcelDataDto populatePreAuthorizationDetail(ClaimUploadedExcelDataDto claimUploadedExcelDataDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            claimUploadedExcelDataDto.setDetailsOfCancerTumorCyst(cellValue);
            return claimUploadedExcelDataDto;
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders, Map dataMap) {
            String errorMessage = "";
            Cell pastHistoryCancerCell= row.getCell(excelHeaders.indexOf(PAST_HISTORY_SUFFERING_FROM_CANCER_TUMOR_CYST.getDescription()));
            String pastHistoryCancerCellValue = getCellValue(pastHistoryCancerCell);
            if( pastHistoryCancerCellValue.equalsIgnoreCase("YES") && isEmpty(value)){
                errorMessage  = "As you selected Past history of chronic illness(CANCER/TUMOR/CYST) YES. So It should be Mandatory";
                return errorMessage;
            }
            return  errorMessage;
        }
    },PAST_HISTORY_SUFFERING_FROM_STD_HIV_AIDS("Past history of any chronic illness - Suffering From STD/HIV/AIDS"){
        @Override
        public String getAllowedValue(ClaimUploadedExcelDataDto claimUploadedExcelDataDto) {
            return claimUploadedExcelDataDto.getPastHistorySufferingFromStdHivAids();
        }

        @Override
        public ClaimUploadedExcelDataDto populatePreAuthorizationDetail(ClaimUploadedExcelDataDto claimUploadedExcelDataDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            claimUploadedExcelDataDto.setPastHistorySufferingFromStdHivAids(cellValue);
            return claimUploadedExcelDataDto;
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders, Map dataMap) {
            return "";
        }
    }, DETAIL_OF_STD_HIV_AIDS("Past history of chronic illness(STD/HIV/AIDS) - Please provide details"){
        @Override
        public String getAllowedValue(ClaimUploadedExcelDataDto claimUploadedExcelDataDto) {
            return claimUploadedExcelDataDto.getDetailOfStdHivAids();
        }

        @Override
        public ClaimUploadedExcelDataDto populatePreAuthorizationDetail(ClaimUploadedExcelDataDto claimUploadedExcelDataDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            claimUploadedExcelDataDto.setDetailOfStdHivAids(cellValue);
            return claimUploadedExcelDataDto;
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders, Map dataMap) {
            String errorMessage = "";
            Cell pastHistoryHIVCell= row.getCell(excelHeaders.indexOf(PAST_HISTORY_SUFFERING_FROM_STD_HIV_AIDS.getDescription()));
            String pastHistoryHIVCellValue = getCellValue(pastHistoryHIVCell);
            if( pastHistoryHIVCellValue.equalsIgnoreCase("YES") && isEmpty(value)){
                errorMessage  = "As you selected Past history of chronic illness(STD/HIV/AIDS) YES. So It should be Mandatory";
                return errorMessage;
            }
            return  errorMessage;
        }
    }, PAST_HISTORY_SUFFERING_FROM_ALCOHOL_DRUG_ABUSE("Past history of any chronic illness - Suffering From Alcohol/Drug Abuse"){
        @Override
        public String getAllowedValue(ClaimUploadedExcelDataDto claimUploadedExcelDataDto) {
            return claimUploadedExcelDataDto.getPastHistorySufferingFromAlcoholDrugAbuse();
        }

        @Override
        public ClaimUploadedExcelDataDto populatePreAuthorizationDetail(ClaimUploadedExcelDataDto claimUploadedExcelDataDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            claimUploadedExcelDataDto.setPastHistorySufferingFromAlcoholDrugAbuse(cellValue);
            return claimUploadedExcelDataDto;
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders, Map dataMap) {
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
        public String getAllowedValue(ClaimUploadedExcelDataDto claimUploadedExcelDataDto) {
            return claimUploadedExcelDataDto.getDetailOfAlcoholDrugAbuse();
        }

        @Override
        public ClaimUploadedExcelDataDto populatePreAuthorizationDetail(ClaimUploadedExcelDataDto claimUploadedExcelDataDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            claimUploadedExcelDataDto.setDetailOfAlcoholDrugAbuse(cellValue);
            return claimUploadedExcelDataDto;
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders, Map dataMap) {
            String errorMessage = "";
            Cell pastHistoryAlcoholCell= row.getCell(excelHeaders.indexOf(PAST_HISTORY_SUFFERING_FROM_ALCOHOL_DRUG_ABUSE.getDescription()));
            String pastHistoryAlcoholCellValue = getCellValue(pastHistoryAlcoholCell);
            if( pastHistoryAlcoholCellValue.equalsIgnoreCase("YES") && isEmpty(value)){
                errorMessage  = "As you selected Past history of chronic illness(ALCOHOL/DRUG/ABUSE) YES. So It should be Mandatory";
                return errorMessage;
            }
            return  errorMessage;
        }
    }, PAST_HISTORY_SUFFERING_FROM__PSYCHIATRIC_CONDITION("Past history of any chronic illness - Suffering From Psychiatric Condition"){
        @Override
        public String getAllowedValue(ClaimUploadedExcelDataDto claimUploadedExcelDataDto) {
            return claimUploadedExcelDataDto.getPastHistorySufferingFromPsychiatricCondition();
        }

        @Override
        public ClaimUploadedExcelDataDto populatePreAuthorizationDetail(ClaimUploadedExcelDataDto claimUploadedExcelDataDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            claimUploadedExcelDataDto.setPastHistorySufferingFromPsychiatricCondition(cellValue);
            return claimUploadedExcelDataDto;
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders, Map dataMap) {
            return "";
        }
    }, DETAILS_PSYCHIATRIC_CONDITION("Past history of chronic illness(PSYCHIATRIC/CONDITION) - Please provide details"){
        @Override
        public String getAllowedValue(ClaimUploadedExcelDataDto claimUploadedExcelDataDto) {
            return claimUploadedExcelDataDto.getDetailsPsychiatricCondition();
        }

        @Override
        public ClaimUploadedExcelDataDto populatePreAuthorizationDetail(ClaimUploadedExcelDataDto claimUploadedExcelDataDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            claimUploadedExcelDataDto.setDetailsPsychiatricCondition(cellValue);
            return claimUploadedExcelDataDto;
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders, Map dataMap) {
            String errorMessage = "";
            Cell pastPsychiatricCell= row.getCell(excelHeaders.indexOf(PAST_HISTORY_SUFFERING_FROM__PSYCHIATRIC_CONDITION.getDescription()));
            String pastPsychiatricCellValue = getCellValue(pastPsychiatricCell);
            if( pastPsychiatricCellValue.equalsIgnoreCase("YES") && isEmpty(value)){
                errorMessage  = "As you selected Past history of chronic illness(PSYCHIATRIC/CONDITION) YES. So It should be Mandatory";
                return errorMessage;
            }
            return  errorMessage;
        }
    },SERVICE("Service to be Availed - Service"){
        @Override
        public String getAllowedValue(ClaimUploadedExcelDataDto claimUploadedExcelDataDto) {
            return claimUploadedExcelDataDto.getService();
        }

        @Override
        public ClaimUploadedExcelDataDto populatePreAuthorizationDetail(ClaimUploadedExcelDataDto claimUploadedExcelDataDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            claimUploadedExcelDataDto.setService(cellValue);
            return claimUploadedExcelDataDto;
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders, Map dataMap) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Service to be Availed - Service cannot be empty.";
                    return errorMessage;
                }
                Cell policyNumberCell = row.getCell(excelHeaders.indexOf(POLICY_NUMBER.description));
                String policyNumberValue = getCellValue(policyNumberCell);
                Cell clientIdCell = row.getCell(excelHeaders.indexOf(CLIENT_ID.description));
                String clientid = getCellValue(clientIdCell);
                try {
                    clientid = isNotEmpty(clientid) ? String.valueOf( new BigDecimal(clientid).intValue()) : clientid;

                } catch(NumberFormatException e){
                    errorMessage =errorMessage + "client id is not present";
                    return  errorMessage;
                }
                errorMessage = errorMessage + iExcelPropagator.checkServiceAndDrugCoverdUnderThePolicy(clientid, policyNumberValue, value);
                String hcpCode = isNotEmpty(dataMap.get("hcpCode")) ? dataMap.get("hcpCode").toString() : StringUtils.EMPTY;
                errorMessage = errorMessage + iExcelPropagator.compareHcpRateByHcpService(hcpCode, value);


            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage;
            }
            return errorMessage;
        }
    },TYPE("Service to be Availed - Type"){
        @Override
        public String getAllowedValue(ClaimUploadedExcelDataDto claimUploadedExcelDataDto) {
            return claimUploadedExcelDataDto.getType();
        }

        @Override
        public ClaimUploadedExcelDataDto populatePreAuthorizationDetail(ClaimUploadedExcelDataDto claimUploadedExcelDataDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            claimUploadedExcelDataDto.setType(cellValue);
            return claimUploadedExcelDataDto;
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders, Map dataMap) {
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
    }, STATUS("Status"){
        @Override
        public String getAllowedValue(ClaimUploadedExcelDataDto claimUploadedExcelDataDto) {
            return claimUploadedExcelDataDto.getStatus();
        }

        @Override
        public ClaimUploadedExcelDataDto populatePreAuthorizationDetail(ClaimUploadedExcelDataDto claimUploadedExcelDataDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            claimUploadedExcelDataDto.setStatus(cellValue);
            return claimUploadedExcelDataDto;
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders, Map dataMap) {
            String errorMessage = "";
            if(isEmpty(value)) {
                errorMessage = errorMessage + "Please select status.";
                return errorMessage;
            }
            return errorMessage;
        }
    }, COMMENTS("Comments"){
        @Override
        public String getAllowedValue(ClaimUploadedExcelDataDto claimUploadedExcelDataDto) {
            return claimUploadedExcelDataDto.getComments();
        }

        @Override
        public ClaimUploadedExcelDataDto populatePreAuthorizationDetail(ClaimUploadedExcelDataDto claimUploadedExcelDataDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            claimUploadedExcelDataDto.setComments(cellValue);
            return claimUploadedExcelDataDto;
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders, Map dataMap) {
            String errorMessage = "";
            try {
                Cell statusCell = row.getCell(excelHeaders.indexOf(STATUS.description));
                String status = getCellValue(statusCell);
                if(isNotEmpty(status) && status.trim().equalsIgnoreCase("IGNORE") && isEmpty(value)) {
                    errorMessage = errorMessage + "Please provide comments as status selected is Ignore";
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

    GHCashlessClaimExcelHeader(String description){
        this.description = description;
    }

    public static List<String> getAllowedHeaders(){
        return Stream.of(GHCashlessClaimExcelHeader.values()).map(GHCashlessClaimExcelHeader::getDescription).collect(Collectors.toList());
    }

    public String getDescription() {
        return description;
    }

    public static GHCashlessClaimExcelHeader getEnum(String description) {
        notNull(description, "description cannot be empty for HCPRateExcelHeader");
        for (GHCashlessClaimExcelHeader ghCashlessClaimExcelHeader : values()) {
            if (ghCashlessClaimExcelHeader.description.equalsIgnoreCase(description.trim())) {
                return ghCashlessClaimExcelHeader;
            }
        }
        throw new IllegalArgumentException(description);
    }

    public abstract String getAllowedValue(ClaimUploadedExcelDataDto claimUploadedExcelDataDto);

    public abstract ClaimUploadedExcelDataDto populatePreAuthorizationDetail(ClaimUploadedExcelDataDto claimUploadedExcelDataDto, Row row, List<String> headers);

    public abstract String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders, Map dataMap);

    public static List<Row> findDuplicateRow(List<Row> dataRowsForDuplicateCheck, Row currentRow, List<String> headers) {
        List<Row> duplicateRows = Lists.newArrayList();
        Cell clientIdCell = currentRow.getCell(headers.indexOf(GHCashlessClaimExcelHeader.CLIENT_ID.description));
        String clientIdCellValue = getCellValue(clientIdCell);
        Cell serviceCell = currentRow.getCell(headers.indexOf(GHCashlessClaimExcelHeader.SERVICE.description));
        String serviceCellValue = getCellValue(serviceCell);
        Cell consultationCell = currentRow.getCell(headers.indexOf(GHCashlessClaimExcelHeader.CONSULTATION_DATE.description));
        String consultationCellValue = getCellValue(consultationCell);
        Map<String,Object> currentRowNameRelationshipHolder = new HashMap<String,Object>();
        currentRowNameRelationshipHolder.put("ClientID",clientIdCellValue);
        currentRowNameRelationshipHolder.put("Service",serviceCellValue);
        currentRowNameRelationshipHolder.put("ConsultationDate",consultationCellValue);

        for(Row dataRowForDuplicateCheck : dataRowsForDuplicateCheck){
            if (currentRow.getRowNum() != dataRowForDuplicateCheck.getRowNum()) {
                Cell otherClientIdCell = dataRowForDuplicateCheck.getCell(headers.indexOf(CLIENT_ID.description));
                String otherClientIdValue = getCellValue(otherClientIdCell);
                Cell otherServiceCell = dataRowForDuplicateCheck.getCell(headers.indexOf(SERVICE.description));
                String otherServiceValue = getCellValue(otherServiceCell);
                Cell otherConsultationCell = dataRowForDuplicateCheck.getCell(headers.indexOf(CONSULTATION_DATE.description));
                String otherConsultationValue = getCellValue(otherConsultationCell);
                Map<String,Object> otherRowNameRelationshipHolder = new HashMap<String,Object>();
                otherRowNameRelationshipHolder.put("ClientID",otherClientIdValue);
                otherRowNameRelationshipHolder.put("Service",otherServiceValue);
                otherRowNameRelationshipHolder.put("ConsultationDate",otherConsultationValue);
                if (currentRowNameRelationshipHolder.equals(otherRowNameRelationshipHolder)) {
                    duplicateRows.add(dataRowForDuplicateCheck);
                }
            }
        }
        return duplicateRows;
    }
}
