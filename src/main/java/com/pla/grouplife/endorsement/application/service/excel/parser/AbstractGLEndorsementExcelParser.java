package com.pla.grouplife.endorsement.application.service.excel.parser;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.pla.grouplife.sharedresource.model.GLEndorsementExcelHeader;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.nthdimenzion.common.AppConstants;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.nthdimenzion.utils.UtilValidator.isEmpty;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;


/**
 * Created by Samir on 8/10/2015.
 */
public abstract class AbstractGLEndorsementExcelParser implements GLEndorsementExcelParser {

    protected List<Row> getDataRowsFromExcel(HSSFWorkbook workbook) {
        Iterator<Row> rowIterator = getRows(workbook);
        rowIterator.next();
        List<Row> dataRows = Lists.newArrayList(rowIterator);
        return dataRows;
    }

    protected Row getHeaderRow(HSSFWorkbook workbook) {
        Iterator<Row> rowIterator = getRows(workbook);
        return rowIterator.next();
    }

    protected List<String> getHeaders(HSSFWorkbook workbook) {
        Iterator<Row> rowIterator = getRows(workbook);
        Row headerRow = rowIterator.next();
        List<String> headers = Lists.newArrayList();
        Iterator<Cell> cellIterator = headerRow.cellIterator();
        while (cellIterator.hasNext()) {
            Cell headerCell = cellIterator.next();
            headers.add(headerCell.getStringCellValue());
        }
        return ImmutableList.copyOf(headers);
    }

    protected boolean isValidHeader(List<String> excelHeaders, List<String> allowedHeaders) {
        boolean containsHeader = true;
        for (String influencingFactorInHeader : excelHeaders) {
            if (!allowedHeaders.contains(influencingFactorInHeader)) {
                containsHeader = false;
                break;
            }
        }
        return containsHeader;
    }

    protected Cell createErrorMessageHeaderCell(HSSFWorkbook workbook, Row headerRow, List<String> headers) {
        HSSFCellStyle cellStyle = workbook.createCellStyle();
        HSSFFont hssfFont = workbook.createFont();
        hssfFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        cellStyle.setFont(hssfFont);
        Cell errorMessageHeaderCell = headerRow.createCell(headers.size());
        errorMessageHeaderCell.setCellValue(AppConstants.ERROR_CELL_HEADER_NAME);
        errorMessageHeaderCell.setCellStyle(cellStyle);
        return errorMessageHeaderCell;
    }

    protected String buildDuplicateRowMessage(List<Row> duplicateRows) {
        if (isEmpty(duplicateRows)) {
            return "";
        }
        String duplicateRowErrorMessage = "This row is duplicate with row no(s) ";
        final String[] rowNumbers = {""};
        duplicateRows.forEach(duplicateRow -> {
            rowNumbers[0] = rowNumbers[0] + (duplicateRow.getRowNum() + 1) + ",";
        });
        duplicateRowErrorMessage = duplicateRowErrorMessage + rowNumbers[0] + ".\n";
        return duplicateRowErrorMessage;
    }

    protected List<String> transformToString(List<GLEndorsementExcelHeader> excelHeaders) {
        List<String> allowedHeaders = excelHeaders.stream().map(excelHeader -> excelHeader.getDescription()).collect(Collectors.toList());
        return allowedHeaders;
    }

    protected abstract String validateRow(Row row, List<String> headers, GLEndorsementExcelValidator endorsementExcelValidator);

    protected String buildErrorMessage(Set<String> errorMessages) {
        StringBuilder errorMessageBuilder = new StringBuilder();
        errorMessages.forEach(errorMessage -> {
            if (isNotEmpty(errorMessage)) {
                errorMessageBuilder.append(errorMessage).append("\n");
            }
        });
        return errorMessageBuilder.toString();
    }

    private Iterator<Row> getRows(HSSFWorkbook workbook) {
        HSSFSheet hssfSheet = workbook.getSheetAt(0);
        Iterator<Row> rowIterator = hssfSheet.rowIterator();
        return rowIterator;
    }

    protected Cell getCellByName(Row row, List<String> headers, String cellName) {
        int cellNumber = headers.indexOf(cellName);
        return row.getCell(cellNumber);
    }
}
