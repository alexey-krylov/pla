package com.pla.sharedkernel.util;

import com.google.common.collect.Maps;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.nthdimenzion.common.AppConstants;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import static org.nthdimenzion.utils.UtilValidator.isEmpty;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Created by Samir on 4/28/2015.
 */
public class ExcelGeneratorUtil {


    public static HSSFWorkbook generateExcel(List<String> headers, List<Map<Integer, String>> rowCellData) {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet hssfSheet = workbook.createSheet();
        int rowNumber = 0;
        createHeaderRowWithCell(hssfSheet, rowNumber, headers.size(), headers);
        for (Map<Integer, String> cellData : rowCellData) {
            rowNumber = rowNumber + 1;
            createRowWithCell(hssfSheet, rowNumber, headers.size(), cellData);
        }
        return workbook;

    }

    public static HSSFWorkbook generateExcelWithDvConstraintCell(List<String> headers, List<Map<Integer, String>> rowCellData, Map<Integer, List<String>> constraintCellData) {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet hssfSheet = workbook.createSheet();
        int rowNumber = 0;
        createHeaderRowWithCell(hssfSheet, rowNumber, headers.size(), headers);
        if (isEmpty(rowCellData)) {
            createRowWithDvConstraintCellData(workbook, hssfSheet, 1, 50000, headers.size(), Maps.newHashMap(), constraintCellData);
            return workbook;
        }
        for (Map<Integer, String> cellData : rowCellData) {
            rowNumber = rowNumber + 1;
            createRowWithDvConstraintCellData(workbook, hssfSheet, rowNumber, 50000, headers.size(), cellData, constraintCellData);
        }
        return workbook;
    }

    public static HSSFWorkbook generateExcelWithDvConstraintCell(List<String> headers, List<Map<Integer, String>> rowCellData) {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet hssfSheet = workbook.createSheet();
        int rowNumber = 0;
        createHeaderRowWithCell(hssfSheet, rowNumber, headers.size(), headers);
        if (isEmpty(rowCellData)) {
            createRowWithDvConstraintCellData(workbook, hssfSheet, 1, 50000, headers.size(), Maps.newHashMap());
            return workbook;
        }
        for (Map<Integer, String> cellData : rowCellData) {
            rowNumber = rowNumber + 1;
            createRowWithDvConstraintCellData(workbook, hssfSheet, rowNumber, 50000, headers.size(), cellData);
        }
        return workbook;
    }

    private static HSSFRow createRowWithCell(HSSFSheet hssfSheet, int rowNumber, int noOfCell, Map<Integer, String> cellData) {
        HSSFRow hssfRow = createRow(hssfSheet, rowNumber);
        for (int cellNumber = 0; cellNumber < noOfCell; cellNumber++) {
            createStringCellAndValue(hssfRow, cellNumber, cellData.get(cellNumber));
        }

        return hssfRow;
    }

    private static HSSFRow createHeaderRowWithCell(HSSFSheet hssfSheet, int rowNumber, int noOfCell, List<String> cellData) {
        HSSFRow hssfRow = createRow(hssfSheet, rowNumber);
        HSSFCellStyle cellStyle = hssfSheet.getWorkbook().createCellStyle();
        HSSFFont hssfFont = hssfSheet.getWorkbook().createFont();
        hssfFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        cellStyle.setFont(hssfFont);
        for (int cellNumber = 0; cellNumber < noOfCell; cellNumber++) {
            createStringHeaderCellAndValue(cellStyle, hssfRow, cellNumber, cellData.get(cellNumber));
        }

        return hssfRow;
    }

    private static HSSFRow createRowWithDvConstraintCellData(HSSFWorkbook workbook, HSSFSheet hssfSheet, int rowNumber, int lastRowNumber, int noOfCell, Map<Integer, String> cellData, Map<Integer, List<String>> constraintCellData) {
        HSSFRow hssfRow = createRow(hssfSheet, rowNumber);
        for (int cellNumber = 0; cellNumber < noOfCell; cellNumber++) {
            if (isNotEmpty(constraintCellData.get(cellNumber))) {
                String columnIndex = cellNumber > 25 ? getRefurbishedColumnIndex(cellNumber) : String.valueOf((char) (65 + cellNumber));
                String hiddenSheetName = "hidden" + columnIndex;
                if(workbook.getSheet(hiddenSheetName) == null) {
                    HSSFSheet hiddenSheetForNamedCell = workbook.getSheet(hiddenSheetName) == null ? workbook.createSheet(hiddenSheetName) : workbook.getSheet(hiddenSheetName);
                    List<String> dataList = constraintCellData.get(cellNumber);
                    createNamedRowWithCell(dataList, hiddenSheetForNamedCell, cellNumber);
                    HSSFName namedCell = workbook.createName();
                    namedCell.setNameName(hiddenSheetName);
                    String formula = hiddenSheetName + "!$" + columnIndex + "$1:$" + columnIndex + "$";
                    namedCell.setRefersToFormula(formula + (dataList.size() == 0 ? 1 : dataList.size()));
                    DVConstraint constraint = DVConstraint.createFormulaListConstraint(hiddenSheetName);
                    CellRangeAddressList addressList = new CellRangeAddressList(rowNumber, lastRowNumber, cellNumber, cellNumber);
                    HSSFDataValidation dataValidation = new HSSFDataValidation(addressList, constraint);
                    dataValidation.setErrorStyle(DataValidation.ErrorStyle.STOP);
                    dataValidation.createErrorBox("Error", "Provide proper value");
                    workbook.setSheetHidden(workbook.getSheetIndex(hiddenSheetForNamedCell), true);
                    hssfSheet.addValidationData(dataValidation);
                }
            }
            if (isNotEmpty(cellData.get(cellNumber))) {
                createStringCellAndValue(hssfRow, cellNumber, cellData.get(cellNumber));
            }
        }
        return hssfRow;
    }

    private static String getRefurbishedColumnIndex(int cellNumber) {
        return String.valueOf((char)65)+String.valueOf((char)((65 + cellNumber) - 26));
    }

    private static HSSFRow createRowWithDvConstraintCellData(HSSFWorkbook workbook, HSSFSheet hssfSheet, int rowNumber, int lastRowNumber, int noOfCell, Map<Integer, String> cellData) {
        HSSFRow hssfRow = createRow(hssfSheet, rowNumber);
        for (int cellNumber = 0; cellNumber < noOfCell; cellNumber++) {
            if (isNotEmpty(cellData.get(cellNumber))) {
                createStringCellAndValue(hssfRow, cellNumber, cellData.get(cellNumber));
            }
        }
        return hssfRow;
    }

    private static void createNamedRowWithCell(List<String> planData, HSSFSheet hiddenSheet, int cellNumber) {
        for (int count = 0; count < planData.size(); count++) {
            String name = planData.get(count);
            HSSFRow row = hiddenSheet.createRow(count);
            HSSFCell cell = row.createCell(cellNumber);
            cell.setCellValue(name);
        }
    }

    private static HSSFRow createRow(HSSFSheet hssfSheet, int rowNumber) {
        return hssfSheet.createRow(rowNumber);
    }

    private static HSSFCell createStringCellAndValue(HSSFRow row, int cellNumber, String value) {
        HSSFCell hssfCell = row.createCell(cellNumber);
        hssfCell.setCellValue(value);
        return hssfCell;
    }

    public static HSSFCell createStringHeaderCellAndValue(HSSFCellStyle cellStyle, HSSFRow row, int cellNumber, String value) {
        HSSFCell hssfCell = row.createCell(cellNumber);
        hssfCell.setCellValue(value);
        hssfCell.setCellStyle(cellStyle);
        return hssfCell;
    }

    public static String getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        if (Cell.CELL_TYPE_BLANK == cell.getCellType()) {
            return "";
        } else if (Cell.CELL_TYPE_NUMERIC == cell.getCellType()) {
            if (DateUtil.isCellDateFormatted(cell)) {
                if (cell.getDateCellValue() == null) {
                    return "";
                }
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
                simpleDateFormat.applyPattern(AppConstants.DD_MM_YYY_FORMAT);
                return simpleDateFormat.format(cell.getDateCellValue());
            }
            return String.valueOf(cell.getNumericCellValue());
        }

        return cell.getStringCellValue().trim();
    }
}
