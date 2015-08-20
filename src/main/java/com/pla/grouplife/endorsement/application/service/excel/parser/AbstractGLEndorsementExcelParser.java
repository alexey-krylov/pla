package com.pla.grouplife.endorsement.application.service.excel.parser;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.util.Iterator;
import java.util.List;


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

    protected abstract String validateRow(Row row, List<String> headers);

    private Iterator<Row> getRows(HSSFWorkbook workbook) {
        HSSFSheet hssfSheet = workbook.getSheetAt(0);
        Iterator<Row> rowIterator = hssfSheet.rowIterator();
        return rowIterator;
    }
}
