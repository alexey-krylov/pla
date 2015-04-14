package com.pla.excelsampla;


import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author Mark Beardsley
 */
public class ValidationSheet {

    public static final int BINARY_WORKBOOK = 0;
    public static final int OPENXML_WORKBOOK = 1;

    public static void main(String[] args) throws IOException {
        new ValidationSheet("test.xls", 0);
    }

    public ValidationSheet(String filename, int bookType) throws IOException {
        Workbook workbook = null;
        Sheet sheet = null;
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        StringBuilder refersToFormula = null;
        try {
            switch (bookType) {
                case BINARY_WORKBOOK:
                    workbook = new HSSFWorkbook();
                    break;
                case OPENXML_WORKBOOK:
                    workbook = new HSSFWorkbook();
                    break;
            }

            sheet = workbook.createSheet("Validations");

            buildDataSheet(sheet);
            buildValidationsSheet(sheet);

            fos = new FileOutputStream(filename);
            bos = new BufferedOutputStream(fos);

            workbook.write(bos);
        } finally {
            if (bos != null) {
                bos.close();
            }
        }
    }

    private static final void buildValidationsSheet(Sheet sheet) {
        DataValidationHelper helper = null;
        DataValidationConstraint constraint = null;
        DataValidation validation = null;

        // Set up the first data validation. This will create a drop down list
        // whose elementsd will be recovered from the named area called GENRE. Note
        // that this drop down list will be in cell A1 and I have hard coded this
        // into the CellRangeAddressList as 0, 0, 0, 0.
        helper = sheet.getDataValidationHelper();
        constraint = helper.createFormulaListConstraint("GENRE");
        validation = helper.createValidation(constraint, new CellRangeAddressList(0, 0, 0, 0));
        sheet.addValidationData(validation);

        // Now, set up the second validation. This drop down list will display
        // value which depend upon the selection made in the previous dropdown
        // list. Note that this drop down list will appear in cell B1 as the
        // CellRangeAddressList has been hard coded at 0, 0, 1, 1. Also, look
        // at the String passed to the createFormulaListConatrsint() method
        // call. In effect, it says convert the contents of cell A1 in uppercase
        // and the treat this as the name of an area on the sheet. Get the list
        // of values for the dopr down from this named range.
        constraint = helper.createFormulaListConstraint("INDIRECT(UPPER($A$1))");
        validation = helper.createValidation(constraint, new CellRangeAddressList(0, 0, 1, 1));
        sheet.addValidationData(validation);
    }

    private static final void buildDataSheet(Sheet sheet) {
        Row row = null;
        Cell cell = null;
        Name name = null;

        // First, build the named area that will hold the data for the first
        // validation. Later, the elements of this drop down list will be used
        // to determine which values appear in a dependent drop down list. The key
        // here is to ensure that the labels used in the drop down match the names
        // of the areas which will contain their data - with the exception that
        // the latter are capitalised.
        row = sheet.createRow(9);
        cell = row.createCell(0);
        cell.setCellValue("Folk");
        cell = row.createCell(1);
        cell.setCellValue("Rock");
        cell = row.createCell(2);
        cell.setCellValue("Indie");

        // Now, build the named reagion
        name = sheet.getWorkbook().createName();
        name.setNameName("GENRE");
        name.setRefersToFormula("$A$10:$C$10");

        // Next build rows for the data that will populate the dependent drop
        // down list. There will be three named areas; one providing the data
        // for each different Genre.
        //
        // This first area will provide the contents of the dependent drop down
        // if the user selects Folk in the first list. Note the call to the
        // setNameName() method.
        row = sheet.createRow(10);
        cell = row.createCell(0);
        cell.setCellValue("Fairport Convention");
        cell = row.createCell(1);
        cell.setCellValue("The Strawbs");
        cell = row.createCell(2);
        cell.setCellValue("The Oyster Band");
        cell = row.createCell(3);
        cell.setCellValue("The Albion Band");
        cell = row.createCell(4);
        cell.setCellValue("Morris On");
        name = sheet.getWorkbook().createName();
        name.setNameName("FOLK");
        name.setRefersToFormula("$A$11:$E$11");

        // ..and this if the user selects Rock
        row = sheet.createRow(11);
        cell = row.createCell(0);
        cell.setCellValue("Cream");
        cell = row.createCell(1);
        cell.setCellValue("Free");
        cell = row.createCell(2);
        cell.setCellValue("Deep Purple");
        cell = row.createCell(3);
        cell.setCellValue("Frank Zappa");
        name = sheet.getWorkbook().createName();
        name.setNameName("ROCK");
        name.setRefersToFormula("$A$12:$D$12");

        // ...and this if they select Indie.
        row = sheet.createRow(12);
        cell = row.createCell(0);
        cell.setCellValue("The Cure");
        cell = row.createCell(1);
        cell.setCellValue("Echo and The Bunnymen");
        cell = row.createCell(2);
        cell.setCellValue("Elvis Costello");
        name = sheet.getWorkbook().createName();
        name.setNameName("INDIE");
        name.setRefersToFormula("$A$13:$C$13");
    }
}

