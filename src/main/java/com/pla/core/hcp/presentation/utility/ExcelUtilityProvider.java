package com.pla.core.hcp.presentation.utility;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.pla.core.hcp.application.service.ExcelParserException;
import com.pla.core.hcp.application.service.HCPRateExcelHeader;
import com.pla.grouphealth.sharedresource.service.GHInsuredExcelHeader;
import com.pla.sharedkernel.identifier.PlanId;
import lombok.NoArgsConstructor;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.nthdimenzion.common.AppConstants;
import org.nthdimenzion.utils.UtilValidator;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.pla.core.hcp.application.service.ExcelParserException.*;
import static com.pla.sharedkernel.util.ExcelGeneratorUtil.getCellValue;
import static org.nthdimenzion.utils.UtilValidator.*;

/**
 * Created by Mohan Sharma on 12/22/2015.
 */
@NoArgsConstructor
@Component
public class ExcelUtilityProvider {

    public boolean isValidInsuredExcel(HSSFWorkbook hssfWorkbook) {
        boolean isValidTemplate = true;
        HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(0);
        Iterator<Row> rowIterator = hssfSheet.rowIterator();
        Row headerRow = rowIterator.next();
        List<Row> dataRows = Lists.newArrayList(rowIterator);
        if(isEmpty(dataRows)){
            raiseDataNotSharedException();
        }
        final List<String> headers = getHeaders(headerRow);
        boolean isValidHeader = isValidHeader(headers);
        if (!isValidHeader) {
            raiseNotValidHeaderException();
        }
        Cell errorMessageHeaderCell = null;
        Iterator<Row> dataRowIterator = dataRows.iterator();
        while (dataRowIterator.hasNext()) {
            Row currentRow = dataRowIterator.next();
            List<String> excelHeaders = HCPRateExcelHeader.getAllowedHeaders();
            String errorMessage = validateRow(currentRow, excelHeaders);
            List<Row> duplicateRows = findDuplicateRow(dataRows, currentRow, excelHeaders);
            String duplicateRowErrorMessage = "";
            if (isNotEmpty(duplicateRows)) {
                duplicateRowErrorMessage = "This row is duplicate with row no(s) ";
                final String[] rowNumbers = {""};
                duplicateRows.forEach(duplicateRow -> {
                    rowNumbers[0] = rowNumbers[0] + (duplicateRow.getRowNum() + 1) + ",";
                });
                duplicateRowErrorMessage = duplicateRowErrorMessage + rowNumbers[0] + ".\n";
            }
            if (isEmpty(errorMessage)) {
                continue;
            }
            isValidTemplate = false;
            if (errorMessageHeaderCell == null) {
                HSSFCellStyle cellStyle = hssfSheet.getWorkbook().createCellStyle();
                HSSFFont hssfFont = hssfSheet.getWorkbook().createFont();
                hssfFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
                cellStyle.setFont(hssfFont);
                errorMessageHeaderCell = headerRow.createCell(headers.size());
                errorMessageHeaderCell.setCellValue(AppConstants.ERROR_CELL_HEADER_NAME);
                errorMessageHeaderCell.setCellStyle(cellStyle);
            }
            errorMessage = errorMessage + " \n " + duplicateRowErrorMessage;
            Cell errorMessageCell = currentRow.createCell(headers.size());
            errorMessageCell.setCellValue(errorMessage);
        }
        return isValidTemplate;
    }

    private boolean isValidHeader(List<String> excelHeaders) {
        List<String> allowedHeaders = Stream.of(HCPRateExcelHeader.values()).map(HCPRateExcelHeader :: getDescription).collect(Collectors.toList());
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

    private String validateRow(Row insureDataRow, List<String> headers) {
        String errorMessage = "";
        Set<String> errorMessages = Sets.newHashSet();
        headers.forEach(header -> {
            Cell cell = insureDataRow.getCell(headers.indexOf(header));
            String cellValue = getCellValue(cell);
            HCPRateExcelHeader hcpRateExcelHeader = HCPRateExcelHeader.getHCPCategory(header);
            errorMessages.add(hcpRateExcelHeader.validateAndIfNotBuildErrorMessage(Maps.newHashMap(), insureDataRow, cellValue, headers));
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

    private List<Row> findDuplicateRow(List<Row> dataRowsForDuplicateCheck, Row currentRow, List<String> headers) {
        List<Row> duplicateRows = Lists.newArrayList();
        return duplicateRows;
    }
}
