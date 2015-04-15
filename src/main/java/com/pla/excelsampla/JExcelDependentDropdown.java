package com.pla.excelsampla;

/**
 * Created by Samir on 4/6/2015.
 */

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;

import java.io.File;
import java.io.FileOutputStream;

public class JExcelDependentDropdown {

    private static final String[] organisations = {"Ford", "Toyota"};
    private static final String[] fordDepts = {"Production", "Design", "Marketing"};
    private static final String[] toyotaDepts = {"Planning", "Design", "Marketing-Europe", "Marketing-Americas", "Marketing-Australasia", "Marketing-Rest Of The World", "Production"};

    public void dependentDropDownLists(String filename) {
        HSSFWorkbook workbook = null;
        HSSFSheet sheet = null;
        HSSFSheet dataSheet = null;
        HSSFDataValidation organisationValidation = null;
        HSSFDataValidation departmentValidation = null;
        HSSFDataValidation employeeValidation = null;
        HSSFName orgStartingPoint = null;
        HSSFName orgDataRange = null;
        HSSFName fordProdOrigin = null;
        HSSFName fordProdRange = null;
        HSSFName fordDesignOrigin = null;
        HSSFName fordDesignRange = null;
        HSSFName fordMktOrigin = null;
        HSSFName fordMktRange = null;
        HSSFRow row = null;
        HSSFCell cell = null;
        CellRangeAddressList organisationCellAddressList = null;
        CellRangeAddressList departmentCellAddressList = null;
        CellRangeAddressList employeeCellAddressList = null;
        DVConstraint organisationConstraint = null;
        DVConstraint departmentConstraint = null;
        DVConstraint employeeConstraint = null;
        File outputFile = null;
        FileOutputStream fos = null;

        try {
            // New Workbook.
            outputFile = new File(filename);
            fos = new FileOutputStream(outputFile);
            workbook = new HSSFWorkbook();

            sheet = workbook.createSheet("List Validation");
            dataSheet = workbook.createSheet("Data_Sheet");

            // Populate the data sheet.
            this.populateDataSheet(dataSheet);
            row = sheet.createRow(0);
            cell = row.createCell(0);
            cell.setCellValue("Organisation");
            cell = row.createCell(1);
            cell.setCellValue("Departments");

            organisationCellAddressList = new CellRangeAddressList(1, 1, 0, 0);
            departmentCellAddressList = new CellRangeAddressList(1, 1, 1, 1);
            organisationConstraint = DVConstraint.createFormulaListConstraint(
                    "OFFSET('Data_Sheet'!$B$1, 0, 0, 1, COUNTA('Data_Sheet'!$B$1:$Z$1))");
            organisationValidation = new HSSFDataValidation(
                    organisationCellAddressList, organisationConstraint);
            organisationValidation.setSuppressDropDownArrow(false);
            sheet.addValidationData(organisationValidation);

            departmentConstraint = DVConstraint.createFormulaListConstraint(
                    "IF(A2=\"Ford\",'Data_Sheet'!$B$2:$F$2, IF(A2=\"Toyota\", " +
                            "'Data_Sheet'!$B$6:$H$6))");
            departmentValidation = new HSSFDataValidation(
                    departmentCellAddressList, departmentConstraint);
            departmentValidation.setSuppressDropDownArrow(false);
            sheet.addValidationData(departmentValidation);
            workbook.write(fos);
        } catch (Exception pEx) {
            System.out.println("Caught an: " + pEx.getClass().getName());
            System.out.println("Message : " + pEx.getMessage());
            System.out.println("Stacktrace foillows: ");
            pEx.printStackTrace(System.out);
        } finally {
            if (fos != null) {
                try {
                    fos.flush();
                    fos.close();
                } catch (Exception ex) {
                    // IGNORE //
                }
            }
        }
    }


    private void populateDataSheet(HSSFSheet worksheet) {
        int rowIndex = 0;
        int lastColIndex = 0;
        int result = 0;

        // Firstly, add the organisations
        result = this.populateRow(worksheet.createRow(rowIndex++),
                "Organisations.", JExcelDependentDropdown.organisations);
        if (result > lastColIndex) {
            lastColIndex = result;
        }

        // Now add the the list of departments for the first organisation,
        // Ford in this case.
        result = this.populateRow(worksheet.createRow(rowIndex++),
                "Ford's Departments.", JExcelDependentDropdown.fordDepts);
        if (result > lastColIndex) {
            lastColIndex = result;
        }


        // Now, add the list of departments for the second organisation followed
        // by the lists of employees for each department.
        result = this.populateRow(worksheet.createRow(rowIndex++),
                "Toyota's Departments.", JExcelDependentDropdown.toyotaDepts);
        if (result > lastColIndex) {
            lastColIndex = result;
        }

        for (int i = 0; i < lastColIndex; i++) {
            worksheet.autoSizeColumn(i);
        }
    }


    public int populateRow(HSSFRow row, String label, String[] data) {
        HSSFCell cell = null;
        int columnIndex = 0;
        cell = row.createCell(columnIndex++);
        cell.setCellValue(label);
        for (String item : data) {
            cell = row.createCell(columnIndex++);
            cell.setCellValue(item);
        }
        return (columnIndex);
    }


    public static void main(String[] args) {
        new JExcelDependentDropdown().dependentDropDownLists("E:\\pla\\dependent.xls");
    }
}





