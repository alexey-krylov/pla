package com.pla.core.hcp.application.service;

import com.google.common.collect.Lists;
import com.pla.core.hcp.presentation.dto.HCPServiceDetailDto;
import com.pla.publishedlanguage.contract.IExcelPropagator;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.pla.sharedkernel.util.ExcelGeneratorUtil.getCellValue;
import static org.nthdimenzion.utils.UtilValidator.isEmpty;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;
import static org.springframework.util.Assert.notNull;

/**
 * Author - Mohan Sharma Created on 12/21/2015.
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
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders, Map dataMap) {
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
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders, Map dataMap) {
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
            return (isNotEmpty(hcpServiceDetailDto.getNormalAmount()) && hcpServiceDetailDto.getNormalAmount().compareTo(BigDecimal.ZERO) > 0) ? hcpServiceDetailDto.getNormalAmount().toString() : "";
        }

        @Override
        public void populateInsuredDetail(Map insuredDto, Row row, List<String> headers) {

        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders, Map dataMap) {
            String errorMessage = "";
            try {
                if(isNotEmpty(value) && new BigDecimal(value).signum() == -1) {
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
            return (isNotEmpty(hcpServiceDetailDto.getAfterHours()) && hcpServiceDetailDto.getAfterHours() > 0) ? String.valueOf(hcpServiceDetailDto.getAfterHours()) : "";
        }

        @Override
        public void populateInsuredDetail(Map insuredDto, Row row, List<String> headers) {

        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders, Map dataMap) {
            String errorMessage = "";
            try {
                if(isNotEmpty(value) && new BigDecimal(value).signum() == -1) {
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

    public static HCPRateExcelHeader getEnum(String description) {
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

    public abstract String validateAndIfNotBuildErrorMessage(IExcelPropagator iExcelPropagator, Row row, String value, List<String> excelHeaders, Map dataMap);

    public static List<Row> findDuplicateRow(List<Row> dataRowsForDuplicateCheck, Row currentRow, List<String> headers) {
        List<Row> duplicateRows = Lists.newArrayList();
        Cell serviceAvailedCell = currentRow.getCell(headers.indexOf(SERVICE_AVAILED.description));
        String serviceAvailedCellValue = getCellValue(serviceAvailedCell);
        Map<String,Object> currentRowNameRelationshipHolder = new HashMap<String,Object>();
        currentRowNameRelationshipHolder.put("ServiceAvailed",serviceAvailedCellValue);
        dataRowsForDuplicateCheck.forEach(dataRowForDuplicateCheck -> {
            if (currentRow.getRowNum() != dataRowForDuplicateCheck.getRowNum()) {
                Cell otherServiceAvailedCell = dataRowForDuplicateCheck.getCell(headers.indexOf(SERVICE_AVAILED.description));
                String otherServiceAvailedValue = getCellValue(otherServiceAvailedCell);
                Map<String,Object> otherRowNameRelationshipHolder = new HashMap<String,Object>();
                otherRowNameRelationshipHolder.put("ServiceAvailed",otherServiceAvailedValue);
               if (currentRowNameRelationshipHolder.equals(otherRowNameRelationshipHolder)) {
                    duplicateRows.add(dataRowForDuplicateCheck);
                }
            }
        });
        return duplicateRows;
    }
}
