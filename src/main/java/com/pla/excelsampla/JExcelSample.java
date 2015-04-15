package com.pla.excelsampla;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author: pradyumna
 * @since 1.0 21/03/2015
 */
public class JExcelSample {

    public static void main(String[] args) throws Exception {
        /*HSSFWorkbook workbook = new HSSFWorkbook();
        // Add a sheet
        org.apache.poi.hssf.usermodel.HSSFSheet sheet = workbook.createSheet("Data Validation");
        sheet.createRow(0).createCell(0).setCellValue("Sum Assured");
        CellRangeAddressList assuredList = new CellRangeAddressList(1, 100, 0, 0);
        DVConstraint dvConstraint_1 =
                DVConstraint.createExplicitListConstraint(new String[]{"1000000", "2000000"});
        sheet.addValidationData(new HSSFDataValidation(assuredList, dvConstraint_1));
        DVConstraint dvConstraint_2 =
                DVConstraint.createExplicitListConstraint(new String[]{"30", "31", "32", "33", "34","35","36","37","38"});

        sheet.addValidationData(new HSSFDataValidation(new CellRangeAddressList(1, 100, 1, 1), dvConstraint_2));
        sheet.getRow(0).createCell(1).setCellValue("Age");

        DVConstraint dvConstraint_3 =
                DVConstraint.createExplicitListConstraint(new String[]{"10", "20", "30"});
        sheet.addValidationData(new HSSFDataValidation(new CellRangeAddressList(1, 100, 2, 2), dvConstraint_3));
        sheet.getRow(0).createCell(2).setCellValue("Policy Term");


        DVConstraint dvConstraint_4 =
                DVConstraint.createExplicitListConstraint(new String[]{"5", "10", "15", "20"});
        sheet.addValidationData(new HSSFDataValidation(new CellRangeAddressList(1, 100, 3, 3), dvConstraint_4));
        sheet.getRow(0).createCell(3).setCellValue("Premium Payment Term");


        sheet.addValidationData(new HSSFDataValidation(new CellRangeAddressList(1, 100, 4, 4),
                DVConstraint.createExplicitListConstraint(new String[]{"MALE", "FEMALE"})));
        sheet.getRow(0).createCell(4).setCellValue("Gender");

        sheet.addValidationData(new HSSFDataValidation(new CellRangeAddressList(1, 100, 5, 5),
                DVConstraint.createExplicitListConstraint(new String[]{"Yes", "No"})));
        sheet.getRow(0).createCell(5).setCellValue("Smoking Status");

        sheet.addValidationData(new HSSFDataValidation(new CellRangeAddressList(1, 100, 6, 6),
                DVConstraint.createExplicitListConstraint(new String[]{"Industry -1", "Industry -2"})));
        sheet.getRow(0).createCell(6).setCellValue("Industry");

        sheet.addValidationData(new HSSFDataValidation(new CellRangeAddressList(1, 100, 78, 7),
                DVConstraint.createExplicitListConstraint(new String[]{"Designation-1", "Designation-1"})));
        sheet.getRow(0).createCell(7).setCellValue("Designation");

        sheet.addValidationData(new HSSFDataValidation(new CellRangeAddressList(1, 100, 8,8),
                DVConstraint.createExplicitListConstraint(new String[]{"Occupation-1", "Occupation-2"})));
        sheet.getRow(0).createCell(7).setCellValue("Occupation");

        sheet.addValidationData(new HSSFDataValidation(new CellRangeAddressList(1, 100, 6, 6),
                DVConstraint.createExplicitListConstraint(new String[]{"Above 25", "25-30"})));
        sheet.getRow(0).createCell(8).setCellValue("BMI");
//        dataValidation.setSuppressDropDownArrow(false);
//        sheet.addValidationData(dataValidation);
        //sheet.protectSheet("acc");
        FileOutputStream fileOut = new FileOutputStream("E:\\pla\\test.xls");
        try {
            workbook.write(fileOut);
            fileOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }*/


        String[] data= new String[]{"30", "31", "32", "33", "34","35","36","37","38"};

        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet realSheet = workbook.createSheet("Sheet xls");
        HSSFSheet hidden = workbook.createSheet("hidden");
        HSSFSheet hidden1 = workbook.createSheet("hidden1");
        HSSFSheet hidden2 = workbook.createSheet("hidden2");
        for (int i = 0, length= data.length; i < length; i++) {
            String name = data[i];
            HSSFRow row = hidden.createRow(i);
            HSSFCell cell = row.createCell(0);
            cell.setCellValue(name);
        }

        HSSFName namedCell = workbook.createName();
        namedCell.setNameName("hidden");
        namedCell.setRefersToFormula("hidden!$A$1:$A$" + data.length);
        DVConstraint constraint = DVConstraint.createFormulaListConstraint("hidden");
        CellRangeAddressList addressList = new CellRangeAddressList(1, 60000, 0, 0);
        HSSFDataValidation validation = new HSSFDataValidation(addressList, constraint);

        String[] data1= new String[]{"300", "131", "232", "333", "434","535","636","737","838"};

        for (int i = 0, length= data1.length; i < length; i++) {
            String name = data1[i];
            HSSFRow row = hidden1.createRow(i);
            HSSFCell cell = row.createCell(1);
            cell.setCellValue(name);
        }

        HSSFName namedCell1 = workbook.createName();
        namedCell1.setNameName("hidden1");
        namedCell1.setRefersToFormula("hidden1!$B$1:$B$" + data1.length);
        DVConstraint constraint1 = DVConstraint.createFormulaListConstraint("hidden1");
        CellRangeAddressList addressList1 = new CellRangeAddressList(1, 60000, 1, 1);
        HSSFDataValidation validation1 = new HSSFDataValidation(addressList1, constraint1);


        String[] data2= new String[]{"400000000000000000", "50000000000000000", "6000000000000000000", "33333333333333333333333", "434444444444444","5355555555555555555555555555","636","737","838"};

        for (int i = 0, length= data2.length; i < length; i++) {
            String name = data2[i];
            HSSFRow row = hidden2.createRow(i);
            HSSFCell cell = row.createCell(2);
            cell.setCellValue(name);
        }

        HSSFName namedCell2 = workbook.createName();
        namedCell2.setNameName("hidden2");
        namedCell2.setRefersToFormula("hidden2!$C$1:$C$" + data2.length);
        DVConstraint constraint2 = DVConstraint.createFormulaListConstraint("hidden2");
        CellRangeAddressList addressList2 = new CellRangeAddressList(1, 60000, 2, 2);
        HSSFDataValidation validation2 = new HSSFDataValidation(addressList2, constraint2);

        workbook.setSheetHidden(1, true);
        workbook.setSheetHidden(2, true);
        workbook.setSheetHidden(3, true);
        HSSFRow headerRow =  realSheet.createRow(0);
        headerRow.createCell(0).setCellValue("Header1");
        headerRow.createCell(1).setCellValue("Header2");
        headerRow.createCell(2).setCellValue("Header3");
        realSheet.addValidationData(validation);
        realSheet.addValidationData(validation1);
        realSheet.addValidationData(validation2);
        FileOutputStream stream = new FileOutputStream("E:\\pla\\range.xls");
        workbook.write(stream);
        stream.close();

    }
}
