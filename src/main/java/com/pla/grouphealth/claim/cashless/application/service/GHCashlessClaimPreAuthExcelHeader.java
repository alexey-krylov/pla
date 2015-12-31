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
    },HCP_CODE("HCP Code"){
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
    },G("G"){
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
    },P("P"){
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
    },L("L"){
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
    },A("A"){
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
    },PROBABLE_DATE_OF_DELIVERY("Probable Date of Delivery"){
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
    },MODE_OF_DELIVERY("Mode Of Delivery"){
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
    },SUFFERING_FROM_HTN("Suffering From HTN"){
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
    },SUFFERING_FROM_IHD_CAD("Suffering From IHD/CAD"){
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
    },PLEASE_PROVIDE_DETAILS("Please provide details"){
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
    },SUFFERING_FROM_DIABETES("Suffering From Diabetes"){
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
    },SERVICE("Service"){
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
    },TYPE("Type"){
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
