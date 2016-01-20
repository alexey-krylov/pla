package com.pla.sharedkernel.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.pla.core.SBCM.domain.model.ServiceBenefitCoverageMapping;
import com.pla.core.SBCM.repository.SBCMRepository;
import com.pla.grouphealth.claim.cashless.application.service.PreAuthorizationExcelHeader;
import com.pla.grouphealth.policy.domain.model.GroupHealthPolicy;
import com.pla.grouphealth.sharedresource.model.vo.GHInsured;
import com.pla.publishedlanguage.contract.IExcelPropagator;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.nthdimenzion.common.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.pla.core.hcp.application.service.ExcelParserException.raiseDataNotSharedException;
import static com.pla.core.hcp.application.service.ExcelParserException.raiseNotValidHeaderException;
import static com.pla.sharedkernel.util.ExcelGeneratorUtil.getCellValue;
import static org.nthdimenzion.utils.UtilValidator.isEmpty;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Author - Mohan Sharma Created on 12/22/2015.
 */
@NoArgsConstructor
@Component
public class ExcelUtilityProvider {

    @Autowired
    private IExcelPropagator iExcelPropagator;
    private SBCMRepository sbcmRepository;
    public boolean isValidInsuredExcel(HSSFWorkbook hssfWorkbook, List<String> excelHeaders, Class dynamicClass) {
        boolean isValidTemplate = true;
        HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(0);
        Iterator<Row> rowIterator = hssfSheet.rowIterator();
        Row headerRow = rowIterator.next();
        List<Row> dataRows = Lists.newArrayList(rowIterator);
        if(isEmpty(dataRows)){
            raiseDataNotSharedException();
        }
        final List<String> headersFromUploadedSheet = getHeaders(headerRow);
        boolean isValidHeader = isValidHeader(excelHeaders, headersFromUploadedSheet);
        if (!isValidHeader) {
            raiseNotValidHeaderException();
        }
        Cell errorMessageHeaderCell = null;
        Iterator<Row> dataRowIterator = dataRows.iterator();
        while (dataRowIterator.hasNext()) {
            Row currentRow = dataRowIterator.next();
            String errorMessage = validateRow(currentRow, excelHeaders, dynamicClass);
            if(dynamicClass.getName().equalsIgnoreCase("PreAuthorizationExcelHeader")){
                String serviceNotCoveredError = isServiceDrugCoveredUnderThePolicy(currentRow, excelHeaders);
                if(isNotEmpty(serviceNotCoveredError))
                    errorMessage = errorMessage + " \n " + serviceNotCoveredError;

            }
            List<Row> duplicateRows = findDuplicateRow(dataRows, currentRow, excelHeaders, dynamicClass);
            String duplicateRowErrorMessage = "";
            if (isNotEmpty(duplicateRows)) {
                duplicateRowErrorMessage = "This row is duplicate with row no(s) ";
                final String[] rowNumbers = {""};
                duplicateRows.forEach(duplicateRow -> {
                    rowNumbers[0] = rowNumbers[0] + (duplicateRow.getRowNum() + 1) + ",";
                });
                duplicateRowErrorMessage = duplicateRowErrorMessage + rowNumbers[0] + ".\n";
            }
            if (isEmpty(errorMessage) && isEmpty(duplicateRowErrorMessage)) {
                continue;
            }
            isValidTemplate = false;
            if (errorMessageHeaderCell == null) {
                HSSFCellStyle cellStyle = hssfSheet.getWorkbook().createCellStyle();
                HSSFFont hssfFont = hssfSheet.getWorkbook().createFont();
                hssfFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
                cellStyle.setFont(hssfFont);
                errorMessageHeaderCell = headerRow.createCell(headersFromUploadedSheet.size());
                errorMessageHeaderCell.setCellValue(AppConstants.ERROR_CELL_HEADER_NAME);
                errorMessageHeaderCell.setCellStyle(cellStyle);
            }
            errorMessage = errorMessage + " \n " + duplicateRowErrorMessage;
            Cell errorMessageCell = currentRow.createCell(headersFromUploadedSheet.size());
            errorMessageCell.setCellValue(errorMessage);
        }
        return isValidTemplate;
    }

    private boolean isValidHeader(List<String> allowedHeaders, List<String> excelHeaders) {
        //List<String> allowedHeaders = Stream.of(HCPRateExcelHeader.values()).map(HCPRateExcelHeader :: getDescription).collect(Collectors.toList());
        return isTemplateContainsSameExcelHeader(allowedHeaders, excelHeaders);
    }

    private boolean isTemplateContainsSameExcelHeader(List<String> allowedHeaders, List<String> excelHeaders) {
        boolean containsHeader = true;
        for (String excelHeader : excelHeaders) {
            if (!allowedHeaders.contains(excelHeader)) {
                containsHeader = false;
                break;
            }
        }
        return containsHeader;
    }

    public static List<String> getHeaders(Row headerRow) {
        List<String> headers = Lists.newArrayList();
        Iterator<Cell> cellIterator = headerRow.cellIterator();
        while (cellIterator.hasNext()) {
            Cell headerCell = cellIterator.next();
            headers.add(headerCell.getStringCellValue());
        }
        return ImmutableList.copyOf(headers);
    }

    private boolean isFileBlank(List<Row> dataRows) {
        Optional<Row> rowOptional = dataRows.parallelStream().filter(new Predicate<Row>() {
            @Override
            public boolean test(Row row) {
                return isRowEmpty(row);
            }
        }).findAny();
        return !rowOptional.isPresent();
    }

    public static boolean isRowEmpty(Row row) {
        List<Cell> cells = Lists.newArrayList(row.cellIterator());
        Optional<Cell> cellOptional = cells.parallelStream().filter(new Predicate<Cell>() {
            @Override
            public boolean test(Cell cell) {
                return (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK);
            }
        }).findAny();
        return cellOptional.isPresent();
    }

    private String validateRow(Row insureDataRow, List<String> headers, Class dynamicClass) {
        String errorMessage = "";
        Set<String> errorMessages = Sets.newHashSet();
        headers.forEach(header -> {
            Cell cell = insureDataRow.getCell(headers.indexOf(header));
            String cellValue = getCellValue(cell);
            try {
                Method getEnumMethod = dynamicClass.getMethod("getEnum", String.class);
                Object dynamicEnum = getEnumMethod.invoke(null, header);
                Method validateAndIfNotBuildErrorMessageMethod = dynamicClass.getMethod("validateAndIfNotBuildErrorMessage", IExcelPropagator.class, Row.class, String.class, List.class);
                errorMessages.add((String) validateAndIfNotBuildErrorMessageMethod.invoke(dynamicEnum, iExcelPropagator, insureDataRow, cellValue, headers));
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            /*HCPRateExcelHeader hcpRateExcelHeader = HCPRateExcelHeader.getEnum(header);
            errorMessages.add(hcpRateExcelHeader.validateAndIfNotBuildErrorMessage(Maps.newHashMap(), insureDataRow, cellValue, headers));*/
        });
        errorMessage = buildErrorMessage(errorMessages);
        return errorMessage;
    }

    private String buildErrorMessage(Set<String> errorMessages) {
        StringBuilder errorMessageBuilder = new StringBuilder();
        errorMessages.forEach(errorMessage -> {
            if (isNotEmpty(errorMessage) && errorMessage != null) {
                errorMessageBuilder.append(errorMessage).append("\n");
            }
        });
        return errorMessageBuilder.toString();
    }

    private List<Row> findDuplicateRow(List<Row> dataRowsForDuplicateCheck, Row currentRow, List<String> headers, Class dynamicClass) {
        try {
            Method findDuplicateRow = dynamicClass.getMethod("findDuplicateRow", List.class, Row.class, List.class);
            return (List<Row>) findDuplicateRow.invoke(null, dataRowsForDuplicateCheck, currentRow, headers);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return Lists.newArrayList();
    }

    public String isServiceDrugCoveredUnderThePolicy(Row CurrentRow, List<String> excelHeaders){
        String planCode;
        Cell policyNumberCell = CurrentRow.getCell(excelHeaders.indexOf(PreAuthorizationExcelHeader.POLICY_NUMBER.getDescription()));
        String policyNumberValue = getCellValue(policyNumberCell);
        GroupHealthPolicy groupHealthPolicy =iExcelPropagator.findPolicyByPolicyNumber(policyNumberValue);
        Cell serviceNumberCell = CurrentRow.getCell(excelHeaders.indexOf(PreAuthorizationExcelHeader.SERVICE.getDescription()));
        String serviceNumberValue = getCellValue(serviceNumberCell);
        Set<GHInsured> ghInsureds = groupHealthPolicy.getInsureds();
        if(isNotEmpty(ghInsureds)) {
            GHInsured ghInsured = ghInsureds.iterator().next();
            planCode = ghInsured.getPlanPremiumDetail().getPlanCode();
            List<ServiceBenefitCoverageMapping> serviceBenefitCoverageMappings = sbcmRepository.findAllByPlanCode(planCode);
            Set<String> services = isNotEmpty(serviceBenefitCoverageMappings) ? serviceBenefitCoverageMappings.parallelStream().map(new Function<ServiceBenefitCoverageMapping, String>() {
                @Override
                public String apply(ServiceBenefitCoverageMapping serviceBenefitCoverageMapping) {
                    return serviceBenefitCoverageMapping.getService();
                }
            }).collect(Collectors.toSet()): Collections.EMPTY_SET;

            if(!services.contains(serviceNumberValue)){
                return serviceNumberValue+" service is not covered under  policy number - "+policyNumberValue;
            }
            return StringUtils.EMPTY;

        }
        return  "No related plan found for the policy";
    }

}
