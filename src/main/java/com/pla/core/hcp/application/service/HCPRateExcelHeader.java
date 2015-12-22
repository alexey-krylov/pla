package com.pla.core.hcp.application.service;

import com.google.common.collect.Lists;
import com.pla.core.hcp.presentation.dto.HCPServiceDetailDto;
import org.apache.poi.ss.usermodel.Row;
import org.nthdimenzion.utils.UtilValidator;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.nthdimenzion.utils.UtilValidator.*;
import static org.springframework.util.Assert.notNull;

/**
 * Created by Mohan Sharma on 12/21/2015.
 */
public enum HCPRateExcelHeader {
    SERVICE_DEPARTMENT("SERVICE DEPT"){
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
    }, SERVICE_AVAILED("SERVICE AVAILED"){
        @Override
        public String getAllowedValue(HCPServiceDetailDto hcpServiceDetailDto) {
            return hcpServiceDetailDto.getServiceAvailed();
        }

        @Override
        public void populateInsuredDetail(Map insuredDto, Row row, List<String> headers) {
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(Map detailsMap, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(isEmpty(value)) {
                    errorMessage = errorMessage + "Service Availed cannot be empty.";
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage;
            }
            return errorMessage;
        }
    }, NORMAL("NORMAL"){
        @Override
        public String getAllowedValue(HCPServiceDetailDto hcpServiceDetailDto) {
            return hcpServiceDetailDto.getNormalAmount() != null ? hcpServiceDetailDto.getNormalAmount().toString() : "";
        }

        @Override
        public void populateInsuredDetail(Map insuredDto, Row row, List<String> headers) {

        }

        @Override
        public String validateAndIfNotBuildErrorMessage(Map detailsMap, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(new BigDecimal(value).signum() == -1) {
                    errorMessage = errorMessage + "Normal Amount cannot be negative.";
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage;
            }
            return errorMessage;
        }
    }, AFTER_HOURS("AFTER HRS") {
        @Override
        public String getAllowedValue(HCPServiceDetailDto hcpServiceDetailDto) {
            return isNotEmpty(hcpServiceDetailDto.getAfterHours()) ? String.valueOf(hcpServiceDetailDto.getAfterHours()) : "";
        }

        @Override
        public void populateInsuredDetail(Map insuredDto, Row row, List<String> headers) {

        }

        @Override
        public String validateAndIfNotBuildErrorMessage(Map detailsMap, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                if(new BigDecimal(value).signum() == -1) {
                    errorMessage = errorMessage + "Normal Amount cannot be negative.";
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage;
            }
            return errorMessage;
        }
    };

    private String description;

    HCPRateExcelHeader(String description){
        this.description = description;
    }

    public static List<String> getAllowedHeaders(){
        List<String> headers = Lists.newArrayList();
        for (HCPRateExcelHeader hcpRateExcelHeader : HCPRateExcelHeader.values()) {
            headers.add(hcpRateExcelHeader.getDescription());
        }
        return headers;
    }

    public static HCPRateExcelHeader getHCPCategory(String description) {
        notNull(description, "description cannot be empty for HCPRateExcelHeader");
        for (HCPRateExcelHeader hcpRateExcelHeader : values()) {
            if (hcpRateExcelHeader.description.equalsIgnoreCase(description.trim())) {
                return hcpRateExcelHeader;
            }
        }
        throw new IllegalArgumentException(description);
    }
    public String getDescription() {
        return description;
    }

    public abstract String getAllowedValue(HCPServiceDetailDto hcpServiceDetailDto);

    public abstract void populateInsuredDetail(Map insuredDto, Row row, List<String> headers);

    public abstract String validateAndIfNotBuildErrorMessage(Map details, Row row, String value, List<String> excelHeaders);
}
