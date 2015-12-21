package com.pla.core.hcp.application.service;

import com.google.common.collect.Lists;
import com.pla.core.hcp.presentation.dto.HCPServiceDetailDto;
import org.apache.poi.ss.usermodel.Row;

import java.util.List;
import java.util.Map;

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
        public String validateAndIfNotBuildErrorMessage(Map planAdapter, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
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
        public String validateAndIfNotBuildErrorMessage(Map planAdapter, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
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
        public String validateAndIfNotBuildErrorMessage(Map planAdapter, Row row, String value, List<String> excelHeaders) {
            return null;
        }
    }, AFTER_HOURS("AFTER HRS") {
        @Override
        public String getAllowedValue(HCPServiceDetailDto hcpServiceDetailDto) {
            return String.valueOf(hcpServiceDetailDto.getAfterHours());
        }

        @Override
        public void populateInsuredDetail(Map insuredDto, Row row, List<String> headers) {

        }

        @Override
        public String validateAndIfNotBuildErrorMessage(Map planAdapter, Row row, String value, List<String> excelHeaders) {
            return null;
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

    public String getDescription() {
        return description;
    }

    public abstract String getAllowedValue(HCPServiceDetailDto hcpServiceDetailDto);

    public abstract void populateInsuredDetail(Map insuredDto, Row row, List<String> headers);

    public abstract String validateAndIfNotBuildErrorMessage(Map planAdapter, Row row, String value, List<String> excelHeaders);
}
