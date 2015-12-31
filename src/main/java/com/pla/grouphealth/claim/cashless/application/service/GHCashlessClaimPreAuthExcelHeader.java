package com.pla.grouphealth.claim.cashless.application.service;

import com.google.common.collect.Lists;
import com.pla.core.hcp.presentation.dto.HCPServiceDetailDto;
import org.apache.poi.ss.usermodel.Row;

import java.util.List;
import java.util.Map;

import static org.nthdimenzion.utils.UtilValidator.isEmpty;

/**
 * Created by Mohan Sharma on 12/30/2015.
 */
public enum GHCashlessClaimPreAuthExcelHeader {
    HOSPITALIZATION_EVENT("Hospitalization Event"){
        @Override
        public String getAllowedValue(HCPServiceDetailDto hcpServiceDetailDto) {
            return hcpServiceDetailDto.getServiceDepartment();
        }

        @Override
        public void populateInsuredDetail(Map insuredDto, Row row, List<String> headers) {
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(Map detailsMap, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Service Department cannot be empty.";
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage;
            }
            return errorMessage;
        }
    },POLICY_NUMBER("Policy Number"){
        @Override
        public String getAllowedValue(HCPServiceDetailDto hcpServiceDetailDto) {
            return hcpServiceDetailDto.getServiceDepartment();
        }

        @Override
        public void populateInsuredDetail(Map insuredDto, Row row, List<String> headers) {
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(Map detailsMap, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Service Department cannot be empty.";
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage;
            }
            return errorMessage;
        }
    },CLIENT_ID("Client ID"){
        @Override
        public String getAllowedValue(HCPServiceDetailDto hcpServiceDetailDto) {
            return hcpServiceDetailDto.getServiceDepartment();
        }

        @Override
        public void populateInsuredDetail(Map insuredDto, Row row, List<String> headers) {
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(Map detailsMap, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Service Department cannot be empty.";
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage;
            }
            return errorMessage;
        }
    },TREATING_DOCTOR_NAME("Name of the Treating Doctor"){
        @Override
        public String getAllowedValue(HCPServiceDetailDto hcpServiceDetailDto) {
            return hcpServiceDetailDto.getServiceDepartment();
        }

        @Override
        public void populateInsuredDetail(Map insuredDto, Row row, List<String> headers) {
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(Map detailsMap, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Service Department cannot be empty.";
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage;
            }
            return errorMessage;
        }
    },DOCTOR_CONTACT_NUMBER("Doctor's Contact Number"){
        @Override
        public String getAllowedValue(HCPServiceDetailDto hcpServiceDetailDto) {
            return hcpServiceDetailDto.getServiceDepartment();
        }

        @Override
        public void populateInsuredDetail(Map insuredDto, Row row, List<String> headers) {
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(Map detailsMap, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Service Department cannot be empty.";
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage;
            }
            return errorMessage;
        }
    },REASONS("Please indicate whether it is a"){
        @Override
        public String getAllowedValue(HCPServiceDetailDto hcpServiceDetailDto) {
            return hcpServiceDetailDto.getServiceDepartment();
        }

        @Override
        public void populateInsuredDetail(Map insuredDto, Row row, List<String> headers) {
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(Map detailsMap, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Service Department cannot be empty.";
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage;
            }
            return errorMessage;
        }
    },PREGNANCY_G("Diagnosis/Treatment in case Of Pregnancy -G"){
        @Override
        public String getAllowedValue(HCPServiceDetailDto hcpServiceDetailDto) {
            return hcpServiceDetailDto.getServiceDepartment();
        }

        @Override
        public void populateInsuredDetail(Map insuredDto, Row row, List<String> headers) {
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(Map detailsMap, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Service Department cannot be empty.";
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage;
            }
            return errorMessage;
        }
    },PREGNANCY_P("Diagnosis/Treatment in case Of Pregnancy"){
        @Override
        public String getAllowedValue(HCPServiceDetailDto hcpServiceDetailDto) {
            return hcpServiceDetailDto.getServiceDepartment();
        }

        @Override
        public void populateInsuredDetail(Map insuredDto, Row row, List<String> headers) {
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(Map detailsMap, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Service Department cannot be empty.";
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage;
            }
            return errorMessage;
        }
    },PREGNANCY_L("Diagnosis/Treatment in case Of Pregnancy -L"){
        @Override
        public String getAllowedValue(HCPServiceDetailDto hcpServiceDetailDto) {
            return hcpServiceDetailDto.getServiceDepartment();
        }

        @Override
        public void populateInsuredDetail(Map insuredDto, Row row, List<String> headers) {
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(Map detailsMap, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Service Department cannot be empty.";
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage;
            }
            return errorMessage;
        }
    },PREGNANCY_A("Diagnosis/Treatment in case Of Pregnancy -A"){
        @Override
        public String getAllowedValue(HCPServiceDetailDto hcpServiceDetailDto) {
            return hcpServiceDetailDto.getServiceDepartment();
        }

        @Override
        public void populateInsuredDetail(Map insuredDto, Row row, List<String> headers) {
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(Map detailsMap, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Service Department cannot be empty.";
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage;
            }
            return errorMessage;
        }
    },PREGNANCY_DATE_OF_DELIVERY("Diagnosis/Treatment in case Of Pregnancy- Date of Delivery"){
        @Override
        public String getAllowedValue(HCPServiceDetailDto hcpServiceDetailDto) {
            return hcpServiceDetailDto.getServiceDepartment();
        }

        @Override
        public void populateInsuredDetail(Map insuredDto, Row row, List<String> headers) {
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(Map detailsMap, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Service Department cannot be empty.";
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage;
            }
            return errorMessage;
        }
    },PREGNANCY_MODE_OF_DELIVERY("Diagnosis/Treatment in case Of Pregnancy- Mode Of Delivery"){
        @Override
        public String getAllowedValue(HCPServiceDetailDto hcpServiceDetailDto) {
            return hcpServiceDetailDto.getServiceDepartment();
        }

        @Override
        public void populateInsuredDetail(Map insuredDto, Row row, List<String> headers) {
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(Map detailsMap, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Service Department cannot be empty.";
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage;
            }
            return errorMessage;
        }
    },DIAGNOSIS_TREATMENT_ILLNESS_TRAUMA_ILLNESS_DISEASE_NAME_AND_PRESENTING_COMPLAINTS("Diagnosis/Treatment in case Of Illness Or Trauma - Name of illness / disease with presenting complaints"){
        @Override
        public String getAllowedValue(HCPServiceDetailDto hcpServiceDetailDto) {
            return hcpServiceDetailDto.getServiceDepartment();
        }

        @Override
        public void populateInsuredDetail(Map insuredDto, Row row, List<String> headers) {
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(Map detailsMap, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Service Department cannot be empty.";
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage;
            }
            return errorMessage;
        }
    },DIAGNOSIS_TREATMENT_ILLNESS_TRAUMA_RELEVANT_CLINICAL_FINDINGS("Diagnosis/Treatment in case Of Illness Or Trauma - Relevant clinical findings"){
        @Override
        public String getAllowedValue(HCPServiceDetailDto hcpServiceDetailDto) {
            return hcpServiceDetailDto.getServiceDepartment();
        }

        @Override
        public void populateInsuredDetail(Map insuredDto, Row row, List<String> headers) {
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(Map detailsMap, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Service Department cannot be empty.";
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage;
            }
            return errorMessage;
        }
    },DIAGNOSIS_TREATMENT_ILLNESS_TRAUMA_PRESENT_AILMENT_DURATION("Diagnosis/Treatment in case Of Illness Or Trauma -Duration of the present ailment in days"){
        @Override
        public String getAllowedValue(HCPServiceDetailDto hcpServiceDetailDto) {
            return hcpServiceDetailDto.getServiceDepartment();
        }

        @Override
        public void populateInsuredDetail(Map insuredDto, Row row, List<String> headers) {
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(Map detailsMap, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Service Department cannot be empty.";
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage;
            }
            return errorMessage;
        }
    },DIAGNOSIS_TREATMENT_ILLNESS_TRAUMA_FIRST_CONSULTATION_DATE("Diagnosis/Treatment in case Of Illness Or Trauma - Date of first consultation"){
        @Override
        public String getAllowedValue(HCPServiceDetailDto hcpServiceDetailDto) {
            return hcpServiceDetailDto.getServiceDepartment();
        }

        @Override
        public void populateInsuredDetail(Map insuredDto, Row row, List<String> headers) {
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(Map detailsMap, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Service Department cannot be empty.";
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage;
            }
            return errorMessage;
        }
    },DIAGNOSIS_TREATMENT_ILLNESS_TRAUMA_PRESENT_AILMENT_PAST_HISTORY("Diagnosis/Treatment in case Of Illness Or Trauma - Past history of present ailment if any"){
        @Override
        public String getAllowedValue(HCPServiceDetailDto hcpServiceDetailDto) {
            return hcpServiceDetailDto.getServiceDepartment();
        }

        @Override
        public void populateInsuredDetail(Map insuredDto, Row row, List<String> headers) {
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(Map detailsMap, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Service Department cannot be empty.";
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage;
            }
            return errorMessage;
        }
    },DIAGNOSIS_TREATMENT_ILLNESS_TRAUMA_DIAGNOSIS("Diagnosis/Treatment in case Of Illness Or Trauma - Diagnosis"){
        @Override
        public String getAllowedValue(HCPServiceDetailDto hcpServiceDetailDto) {
            return hcpServiceDetailDto.getServiceDepartment();
        }

        @Override
        public void populateInsuredDetail(Map insuredDto, Row row, List<String> headers) {
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(Map detailsMap, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Service Department cannot be empty.";
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage;
            }
            return errorMessage;
        }
    },DIAGNOSIS_TREATMENT_LINE_OF_TREATMENT("Diagnosis/Treatment - Line of treatment"){
        @Override
        public String getAllowedValue(HCPServiceDetailDto hcpServiceDetailDto) {
            return hcpServiceDetailDto.getServiceDepartment();
        }

        @Override
        public void populateInsuredDetail(Map insuredDto, Row row, List<String> headers) {
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(Map detailsMap, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Service Department cannot be empty.";
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage;
            }
            return errorMessage;
        }
    },DIAGNOSIS_TREATMENT_TEST("Diagnosis/Treatment - If Investigations, indicate tests"){
        @Override
        public String getAllowedValue(HCPServiceDetailDto hcpServiceDetailDto) {
            return hcpServiceDetailDto.getServiceDepartment();
        }

        @Override
        public void populateInsuredDetail(Map insuredDto, Row row, List<String> headers) {
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(Map detailsMap, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Service Department cannot be empty.";
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage;
            }
            return errorMessage;
        }
    },DIAGNOSIS_TREATMENT_DRUG_NAME("Diagnosis/Treatment - If Medical Please Provide Drug Name"){
        @Override
        public String getAllowedValue(HCPServiceDetailDto hcpServiceDetailDto) {
            return hcpServiceDetailDto.getServiceDepartment();
        }

        @Override
        public void populateInsuredDetail(Map insuredDto, Row row, List<String> headers) {
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(Map detailsMap, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Service Department cannot be empty.";
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage;
            }
            return errorMessage;
        }
    },DIAGNOSIS_TREATMENT_MEDICAL_DURATION("Diagnosis/Treatment - If Medical Please Provide Duration"){
        @Override
        public String getAllowedValue(HCPServiceDetailDto hcpServiceDetailDto) {
            return hcpServiceDetailDto.getServiceDepartment();
        }

        @Override
        public void populateInsuredDetail(Map insuredDto, Row row, List<String> headers) {
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(Map detailsMap, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Service Department cannot be empty.";
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage;
            }
            return errorMessage;
        }
    },DIAGNOSIS_TREATMENT_SURGERY_NAME("Diagnosis/Treatment - If Surgery Please provide name of surgery"){
        @Override
        public String getAllowedValue(HCPServiceDetailDto hcpServiceDetailDto) {
            return hcpServiceDetailDto.getServiceDepartment();
        }

        @Override
        public void populateInsuredDetail(Map insuredDto, Row row, List<String> headers) {
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(Map detailsMap, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Service Department cannot be empty.";
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage;
            }
            return errorMessage;
        }
    }, DIAGNOSIS_TREATMENT_SURGERY_ACCOMMODATION_TYPE("Diagnosis/Treatment - If Surgery Please provide Type Of Accommodation"){
        @Override
        public String getAllowedValue(HCPServiceDetailDto hcpServiceDetailDto) {
            return hcpServiceDetailDto.getServiceDepartment();
        }

        @Override
        public void populateInsuredDetail(Map insuredDto, Row row, List<String> headers) {
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(Map detailsMap, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Service Department cannot be empty.";
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage;
            }
            return errorMessage;
        }
    },DIAGNOSIS_TREATMENT_SURGERY_DATE_OF_ADMISSION("Diagnosis/Treatment - If Surgery Please provide  Date of Admission"){
        @Override
        public String getAllowedValue(HCPServiceDetailDto hcpServiceDetailDto) {
            return hcpServiceDetailDto.getServiceDepartment();
        }

        @Override
        public void populateInsuredDetail(Map insuredDto, Row row, List<String> headers) {
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(Map detailsMap, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Service Department cannot be empty.";
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage;
            }
            return errorMessage;
        }
    },DIAGNOSIS_TREATMENT_SURGERY_DATE_OF_DISCHARGE("Diagnosis/Treatment - If Surgery Please provide Date of Discharge"){
        @Override
        public String getAllowedValue(HCPServiceDetailDto hcpServiceDetailDto) {
            return hcpServiceDetailDto.getServiceDepartment();
        }

        @Override
        public void populateInsuredDetail(Map insuredDto, Row row, List<String> headers) {
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(Map detailsMap, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Service Department cannot be empty.";
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage;
            }
            return errorMessage;
        }
    },PAST_HISTORY_SUFFERING_FROM_HTN("Past history of any chronic illness - Suffering From HTN"){
        @Override
        public String getAllowedValue(HCPServiceDetailDto hcpServiceDetailDto) {
            return hcpServiceDetailDto.getServiceDepartment();
        }

        @Override
        public void populateInsuredDetail(Map insuredDto, Row row, List<String> headers) {
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(Map detailsMap, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Service Department cannot be empty.";
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage;
            }
            return errorMessage;
        }
    },DETAILS_OF_HTN("Past history of any chronic illness - Please provide details"){
        @Override
        public String getAllowedValue(HCPServiceDetailDto hcpServiceDetailDto) {
            return hcpServiceDetailDto.getServiceDepartment();
        }

        @Override
        public void populateInsuredDetail(Map insuredDto, Row row, List<String> headers) {
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(Map detailsMap, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Service Department cannot be empty.";
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage;
            }
            return errorMessage;
        }
    },PAST_HISTORY_SUFFERING_FROM_IHD_CAD("Past history of any chronic illness - Suffering From IHD/CAD"){
        @Override
        public String getAllowedValue(HCPServiceDetailDto hcpServiceDetailDto) {
            return hcpServiceDetailDto.getServiceDepartment();
        }

        @Override
        public void populateInsuredDetail(Map insuredDto, Row row, List<String> headers) {
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(Map detailsMap, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Service Department cannot be empty.";
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage;
            }
            return errorMessage;
        }
    },DETAILS_OF_IHD_CAD("Past history of any chronic illness - Please provide details"){
        @Override
        public String getAllowedValue(HCPServiceDetailDto hcpServiceDetailDto) {
            return hcpServiceDetailDto.getServiceDepartment();
        }

        @Override
        public void populateInsuredDetail(Map insuredDto, Row row, List<String> headers) {
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(Map detailsMap, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Service Department cannot be empty.";
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage;
            }
            return errorMessage;
        }
    },PAST_HISTORY_SUFFERING_FROM_DIABETES("Past history of any chronic illness - Suffering From Diabetes"){
        @Override
        public String getAllowedValue(HCPServiceDetailDto hcpServiceDetailDto) {
            return hcpServiceDetailDto.getServiceDepartment();
        }

        @Override
        public void populateInsuredDetail(Map insuredDto, Row row, List<String> headers) {
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(Map detailsMap, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Service Department cannot be empty.";
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage;
            }
            return errorMessage;
        }
    },DETAILS_OF_DIABETES("Past history of any chronic illness - Please provide details"){
        @Override
        public String getAllowedValue(HCPServiceDetailDto hcpServiceDetailDto) {
            return hcpServiceDetailDto.getServiceDepartment();
        }

        @Override
        public void populateInsuredDetail(Map insuredDto, Row row, List<String> headers) {
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(Map detailsMap, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Service Department cannot be empty.";
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage;
            }
            return errorMessage;
        }
    }, PAST_HISTORY_SUFFERING_FROM_ASTHMA_COPD_TB("Past history of any chronic illness - Suffering From Asthma/COPD/TB"){
        @Override
        public String getAllowedValue(HCPServiceDetailDto hcpServiceDetailDto) {
            return hcpServiceDetailDto.getServiceDepartment();
        }

        @Override
        public void populateInsuredDetail(Map insuredDto, Row row, List<String> headers) {
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(Map detailsMap, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Service Department cannot be empty.";
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage;
            }
            return errorMessage;
        }
    },DETAILS_OF_ASTHMA_COPD_TB("Past history of any chronic illness - Please provide details"){
        @Override
        public String getAllowedValue(HCPServiceDetailDto hcpServiceDetailDto) {
            return hcpServiceDetailDto.getServiceDepartment();
        }

        @Override
        public void populateInsuredDetail(Map insuredDto, Row row, List<String> headers) {
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(Map detailsMap, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Service Department cannot be empty.";
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage;
            }
            return errorMessage;
        }
    }, PAST_HISTORY_SUFFERING_FROM_PARALYSIS_CVA_EPILEPSY("Past history of any chronic illness - Suffering From Paralysis/CVA/Epilepsy"){
        @Override
        public String getAllowedValue(HCPServiceDetailDto hcpServiceDetailDto) {
            return hcpServiceDetailDto.getServiceDepartment();
        }

        @Override
        public void populateInsuredDetail(Map insuredDto, Row row, List<String> headers) {
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(Map detailsMap, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Service Department cannot be empty.";
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage;
            }
            return errorMessage;
        }
    },DETAILS_OF_PARALYSIS_CVA("Past history of any chronic illness - Please provide details"){
        @Override
        public String getAllowedValue(HCPServiceDetailDto hcpServiceDetailDto) {
            return hcpServiceDetailDto.getServiceDepartment();
        }

        @Override
        public void populateInsuredDetail(Map insuredDto, Row row, List<String> headers) {
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(Map detailsMap, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Service Department cannot be empty.";
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage;
            }
            return errorMessage;
        }
    }, PAST_HISTORY_SUFFERING_FROM_ARTHIRITIS("Past history of any chronic illness - Suffering From Arthritis"){
        @Override
        public String getAllowedValue(HCPServiceDetailDto hcpServiceDetailDto) {
            return hcpServiceDetailDto.getServiceDepartment();
        }

        @Override
        public void populateInsuredDetail(Map insuredDto, Row row, List<String> headers) {
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(Map detailsMap, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Service Department cannot be empty.";
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage;
            }
            return errorMessage;
        }
    }, DETAILS_OF_SUFFERING_FROM_ARTHIRITIS("Past history of any chronic illness - Please provide details"){
        @Override
        public String getAllowedValue(HCPServiceDetailDto hcpServiceDetailDto) {
            return hcpServiceDetailDto.getServiceDepartment();
        }

        @Override
        public void populateInsuredDetail(Map insuredDto, Row row, List<String> headers) {
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(Map detailsMap, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Service Department cannot be empty.";
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage;
            }
            return errorMessage;
        }
    }, PAST_HISTORY_SUFFERING_FROM_CANCER_TUMOR_CYST("Past history of any chronic illness - Suffering From Cancer/Tumor/Cyst") {
        @Override
        public String getAllowedValue(HCPServiceDetailDto hcpServiceDetailDto) {
            return hcpServiceDetailDto.getServiceDepartment();
        }

        @Override
        public void populateInsuredDetail(Map insuredDto, Row row, List<String> headers) {
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(Map detailsMap, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if (isEmpty(value)) {
                    errorMessage = errorMessage + "Service Department cannot be empty.";
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage;
            }
            return errorMessage;
        }
    }, DETAILS_OF_CANCER_TUMOR_CYST("Past history of any chronic illness - Please provide details"){
        @Override
        public String getAllowedValue(HCPServiceDetailDto hcpServiceDetailDto) {
            return hcpServiceDetailDto.getServiceDepartment();
        }

        @Override
        public void populateInsuredDetail(Map insuredDto, Row row, List<String> headers) {
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(Map detailsMap, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Service Department cannot be empty.";
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage;
            }
            return errorMessage;
        }
    },PAST_HISTORY_SUFFERING_FROM_STD_HIV_AIDS("Past history of any chronic illness - Suffering From STD/HIV/AIDS"){
        @Override
        public String getAllowedValue(HCPServiceDetailDto hcpServiceDetailDto) {
            return hcpServiceDetailDto.getServiceDepartment();
        }

        @Override
        public void populateInsuredDetail(Map insuredDto, Row row, List<String> headers) {
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(Map detailsMap, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Service Department cannot be empty.";
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage;
            }
            return errorMessage;
        }
    }, DETAIL_OF_STD_HIV_AIDS("Past history of any chronic illness - Please provide details"){
        @Override
        public String getAllowedValue(HCPServiceDetailDto hcpServiceDetailDto) {
            return hcpServiceDetailDto.getServiceDepartment();
        }

        @Override
        public void populateInsuredDetail(Map insuredDto, Row row, List<String> headers) {
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(Map detailsMap, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Service Department cannot be empty.";
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage;
            }
            return errorMessage;
        }
    }, PAST_HISTORY_SUFFERING_FROM_ALCOHOL_DRUG_ABUSE("Past history of any chronic illness - Suffering From Alcohol/Drug Abuse"){
        @Override
        public String getAllowedValue(HCPServiceDetailDto hcpServiceDetailDto) {
            return hcpServiceDetailDto.getServiceDepartment();
        }

        @Override
        public void populateInsuredDetail(Map insuredDto, Row row, List<String> headers) {
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(Map detailsMap, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Service Department cannot be empty.";
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage;
            }
            return errorMessage;
        }
    }, DETAIL_OF_ALCOHOL_DRUG_ABUSE("Past history of any chronic illness - Please provide detail"){
        @Override
        public String getAllowedValue(HCPServiceDetailDto hcpServiceDetailDto) {
            return hcpServiceDetailDto.getServiceDepartment();
        }

        @Override
        public void populateInsuredDetail(Map insuredDto, Row row, List<String> headers) {
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(Map detailsMap, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Service Department cannot be empty.";
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage;
            }
            return errorMessage;
        }
    }, PAST_HISTORY_SUFFERING_FRO__PSYCHIATRIC_CONDITION("Past history of any chronic illness - Suffering From Psychiatric Condition"){
        @Override
        public String getAllowedValue(HCPServiceDetailDto hcpServiceDetailDto) {
            return hcpServiceDetailDto.getServiceDepartment();
        }

        @Override
        public void populateInsuredDetail(Map insuredDto, Row row, List<String> headers) {
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(Map detailsMap, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Service Department cannot be empty.";
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage;
            }
            return errorMessage;
        }
    }, DETAILS_PSYCHIATRIC_CONDITION("Past history of any chronic illness - Please provide details"){
        @Override
        public String getAllowedValue(HCPServiceDetailDto hcpServiceDetailDto) {
            return hcpServiceDetailDto.getServiceDepartment();
        }

        @Override
        public void populateInsuredDetail(Map insuredDto, Row row, List<String> headers) {
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(Map detailsMap, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Service Department cannot be empty.";
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage;
            }
            return errorMessage;
        }
    },SERVICE("Service to be Availed - Service"){
        @Override
        public String getAllowedValue(HCPServiceDetailDto hcpServiceDetailDto) {
            return hcpServiceDetailDto.getServiceDepartment();
        }

        @Override
        public void populateInsuredDetail(Map insuredDto, Row row, List<String> headers) {
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(Map detailsMap, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Service Department cannot be empty.";
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage;
            }
            return errorMessage;
        }
    },TYPE("Service to be Availed - Type"){
        @Override
        public String getAllowedValue(HCPServiceDetailDto hcpServiceDetailDto) {
            return hcpServiceDetailDto.getServiceDepartment();
        }

        @Override
        public void populateInsuredDetail(Map insuredDto, Row row, List<String> headers) {
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(Map detailsMap, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Service Department cannot be empty.";
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
        List<String> headers = Lists.newArrayList();
        for (GHCashlessClaimPreAuthExcelHeader hcpRateExcelHeader : GHCashlessClaimPreAuthExcelHeader.values()) {
            headers.add(hcpRateExcelHeader.getDescription());
        }
        return headers;
    }
    public String getDescription() {
        return description;
    }

    public abstract String getAllowedValue(HCPServiceDetailDto hcpServiceDetailDto);

    public abstract void populateInsuredDetail(Map insuredDto, Row row, List<String> headers);

    public abstract String validateAndIfNotBuildErrorMessage(Map details, Row row, String value, List<String> excelHeaders);
}
