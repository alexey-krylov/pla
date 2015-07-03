package com.pla.underwriter.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pla.publishedlanguage.contract.IPlanAdapter;
import com.pla.sharedkernel.domain.model.RoutingLevel;
import com.pla.underwriter.domain.model.UnderWriterInfluencingFactor;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.nthdimenzion.common.AppConstants;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static com.pla.underwriter.exception.UnderWriterTemplateParseException.raiseHeaderInvalidException;
import static com.pla.underwriter.exception.UnderWriterTemplateParseException.raiseNumberCellMismatchException;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Created by Admin on 5/11/2015.
 */
@Component
public class UnderWriterTemplateParser {

    public HSSFWorkbook generateUnderWriterTemplate(List<UnderWriterInfluencingFactor> underWriterInfluencingFactors, String planName) throws IOException {
        HSSFWorkbook underWriterTemplateWorkbook = new HSSFWorkbook();
        HSSFSheet underWriterSheet = underWriterTemplateWorkbook.createSheet(planName);
        createHeaderRowWithInfluencingFactor(0, convertToStringArray(underWriterInfluencingFactors), underWriterSheet);
        CellRangeAddressList addressList = new CellRangeAddressList(1, 1000, convertToStringArray(underWriterInfluencingFactors).size()-1,convertToStringArray(underWriterInfluencingFactors).size()-1);
        List<String> routingLevelArray =  Arrays.asList(RoutingLevel.values()).stream().map(new Function<RoutingLevel, String>() {
            @Override
            public String apply(RoutingLevel routingLevel) {
                return routingLevel.getDescription();
            }
        }).collect(Collectors.toList());
        String[] data = Arrays.copyOf(routingLevelArray.toArray(), routingLevelArray.toArray().length, String[].class);
        DVConstraint dvConstraint = DVConstraint.createExplicitListConstraint(data);
        DataValidation dataValidation = new HSSFDataValidation(addressList, dvConstraint);
        dataValidation.setSuppressDropDownArrow(false);
        underWriterSheet.addValidationData(dataValidation);
        return underWriterTemplateWorkbook;
    }

    public List<String> convertToStringArray(List<UnderWriterInfluencingFactor> underWriterInfluencingFactors) {
        List<String> headerRow = getUnderWriterInfluencingFactorsRange(underWriterInfluencingFactors);
        headerRow.add(AppConstants.UNDER_WRITER_ROUTING_HEADER_NAME);
        return headerRow;
    }

    private HSSFRow createHeaderRowWithInfluencingFactor(int rowNumber, List<String> headerRow, HSSFSheet hssfSheet) {
        String[] data = Arrays.copyOf(headerRow.toArray(), headerRow.toArray().length, String[].class);
        HSSFRow row = hssfSheet.createRow(rowNumber);
        for (int cellNumber = 0; cellNumber < data.length; cellNumber++) {
            HSSFCell cell = row.createCell(cellNumber);
            cell.setCellValue(data[cellNumber]);
        }
        return row;
    }

    public boolean validateUnderWritingRoutingLevelDataForAGivenPlanAndCoverage(HSSFWorkbook hssfWorkbook, String planCode, String coverageId, List<UnderWriterInfluencingFactor> underWriterInfluencingFactors,IPlanAdapter iPlanAdapter) throws IOException, UnknownFormatConversionException {
        boolean isValidUnderWriterTemplate = true;
        HSSFSheet underWriterDocumentSheet = hssfWorkbook.getSheetAt(0);
        Iterator<Row> rowsIterator = underWriterDocumentSheet.iterator();
        Row headerRow = rowsIterator.next();
        List<Cell> headerCell = Lists.newArrayList(headerRow.cellIterator());
        List<String> selectedInfluencingFactorRange = convertToStringArray(underWriterInfluencingFactors);
        checkForValidHeader(headerCell, selectedInfluencingFactorRange);
        int errorCellNumber = selectedInfluencingFactorRange.size();
        Map<String, Integer>  influencingFactorCellIndexMap = buildInfluencingFactorAndCellIndexMap(headerRow, selectedInfluencingFactorRange);
        List<Row> allRows = Lists.newArrayList(rowsIterator);
        for (Row row : allRows) {
            String validValueErrorMessage = "";
            for (String influencingFactorRange : selectedInfluencingFactorRange) {
                int cellIndex = influencingFactorCellIndexMap.get(influencingFactorRange);
                Cell cell = row.getCell(cellIndex);
                if (cell == null) {
                    isValidUnderWriterTemplate = false;
                    validValueErrorMessage = validValueErrorMessage + "\n" + influencingFactorRange + " is missing \n";
                    continue;
                }
                if (!Arrays.asList(RoutingLevel.UNDERWRITING_LEVEL_ONE.getDescription(), RoutingLevel.UNDERWRITING_LEVEL_TWO.getDescription()).contains(getCellValueByType(cell))
                        && !NumberUtils.isNumber(getCellValueByType(cell))) {
                    isValidUnderWriterTemplate = false;
                    validValueErrorMessage = validValueErrorMessage + "\n" + getCellValueByType(cell) + " is is not a valid data \n";
                }
            }
            if (isNotEmpty(validValueErrorMessage.trim())) {
                isValidUnderWriterTemplate = false;
                createErrorCellAndWriteErrorMessage(errorCellNumber, validValueErrorMessage, headerRow, row);
            }
        }
        isValidUnderWriterTemplate = doesInfluencingFactorsAreValidWithPlanAndCoverage(allRows, underWriterInfluencingFactors, planCode, coverageId, influencingFactorCellIndexMap, headerRow, isValidUnderWriterTemplate, iPlanAdapter);
        isValidUnderWriterTemplate = doesRowsOverLapping(allRows,  underWriterDocumentSheet.iterator(), selectedInfluencingFactorRange, headerRow, isValidUnderWriterTemplate);
        return isValidUnderWriterTemplate;
    }

    private boolean doesRowsOverLapping(List<Row> allRows,  Iterator<Row> rowIterator , List<String> selectedInfluencingFactorRange, Row headerRow, boolean isValidUnderWriterTemplate){
        rowIterator.next();
        List<Row> comparedRows = Lists.newArrayList(rowIterator);
        int errorCellNumber = selectedInfluencingFactorRange.size();
        for (Row currentRow : allRows) {
            if (!isRowHasEmptyCell(currentRow,selectedInfluencingFactorRange)) {
                comparedRows.remove(currentRow);
                for (Row rowToBeCompared : comparedRows) {
                    if (!isRowHasEmptyCell(rowToBeCompared, selectedInfluencingFactorRange)) {
                        boolean isOverLapping = isRowOverlapping(currentRow, rowToBeCompared, selectedInfluencingFactorRange);
                        if (isOverLapping) {
                            String validValueErrorMessage = "Row " + (currentRow.getRowNum()+1)+ " overlaps with row " + (rowToBeCompared.getRowNum()+1) + " \n";
                            isValidUnderWriterTemplate = false;
                            createErrorCellAndWriteErrorMessage(errorCellNumber, validValueErrorMessage, headerRow, currentRow);
                        }
                    }
                }
                comparedRows.add(currentRow);
            }
        }
        return isValidUnderWriterTemplate;
    }

    private boolean isRowHasEmptyCell(Row row,List<String> headerRow){
        for (int index=0;index<=headerRow.size()-1;index++){
            if (row.getCell(index)==null){
                return true;
            }
        }
        return false;
    }

    boolean isRowOverlapping(Row currentRow,Row rowToBeCompared,List<String> headerRow){
        boolean isRowOverLapping = true;
        for (int index=0;index<=headerRow.size()-1;index++){
            String cellValue = Cell.CELL_TYPE_NUMERIC == currentRow.getCell(index).getCellType() ? ((Double) currentRow.getCell(index).getNumericCellValue()).toString() : currentRow.getCell(index).getStringCellValue();
            if (!Arrays.asList(RoutingLevel.UNDERWRITING_LEVEL_ONE.getDescription(), RoutingLevel.UNDERWRITING_LEVEL_TWO.getDescription()).contains(cellValue)) {
                Double currentCellFrom  = getCellValueFor(currentRow.getCell(index));
                Double secondRowCellFrom = getCellValueFor(rowToBeCompared.getCell(index));
                index = index + 1;
                Double currentCellTo = getCellValueFor(currentRow.getCell(index));
                Double secondRowCellTo = getCellValueFor(rowToBeCompared.getCell(index));
                if (!(currentCellFrom.compareTo(secondRowCellTo) <= 0 && secondRowCellFrom.compareTo(currentCellTo) <= 0)) {
                    isRowOverLapping = false;
                }
            }
        }
        return isRowOverLapping;
    }

    private boolean doesInfluencingFactorsAreValidWithPlanAndCoverage(List<Row> allRows,List<UnderWriterInfluencingFactor> underWriterInfluencingFactors,String planCode,String coverageId,Map<String,Integer> indexMap,Row headerRow,boolean isValidUnderWriterTemplate,IPlanAdapter  iPlanAdapter){
        int errorCellNumber = convertToStringArray(underWriterInfluencingFactors).size();
        List<String> selectedInfluencingFactorRange = getUnderWriterInfluencingFactorsRange(underWriterInfluencingFactors);
        for (Row row : allRows){
            if (!isRowHasEmptyCell(row, selectedInfluencingFactorRange)) {
                StringBuilder errorMessageBuilder = new StringBuilder();
                for (UnderWriterInfluencingFactor underWriterInfluencingFactor : underWriterInfluencingFactors){
                    underWriterInfluencingFactor.isValueAvailableForTheProduct(row, planCode, coverageId, indexMap, errorMessageBuilder,iPlanAdapter, null,null );
                }
                if (isNotEmpty(errorMessageBuilder.toString())){
                    isValidUnderWriterTemplate= false;
                    createErrorCellAndWriteErrorMessage(errorCellNumber, errorMessageBuilder.toString(), headerRow, row);
                }
            }
        }
        return isValidUnderWriterTemplate;
    }

    private Double getCellValueFor(Cell cell){
        return NumberUtils.isNumber(getCellValueByType(cell))==true?Double.valueOf(Cell.CELL_TYPE_NUMERIC == cell.getCellType() ? ((Double) cell.getNumericCellValue()).toString() : cell.getStringCellValue()):0L;
    }

    public List<Map<String, Object>> parseUnderWriterRoutingLevelTemplate(HSSFWorkbook underWriterWorkBook,List<UnderWriterInfluencingFactor> underWriterInfluencingFactors){
        HSSFSheet underWriterSheet = underWriterWorkBook.getSheetAt(0);
        Iterator<Row> rowsIterator = underWriterSheet.iterator();
        Row headerRow = rowsIterator.next();
        Iterator<Cell> cells = headerRow.cellIterator();
        while (cells.hasNext()){
            Cell cell = cells.next();
            if (cell.getStringCellValue().equals(AppConstants.ERROR_CELL_HEADER_NAME)){
                headerRow.removeCell(cell);
            }
        }
        List<Cell> headerCell = Lists.newArrayList(headerRow.cellIterator());
        List<String> underWriterInfluencingFactor = convertToStringArray(underWriterInfluencingFactors);
        checkForValidHeader(headerCell, underWriterInfluencingFactor);
        Map<String, Integer>  indexMap = buildInfluencingFactorAndCellIndexMap(headerRow, underWriterInfluencingFactor);
        List<Map<String, Object>> underWriterRoutingLevel = Lists.newArrayList();
        while (rowsIterator.hasNext()) {
            Row row = rowsIterator.next();
            Iterator<Cell> cellIterator = row.cellIterator();
            Map<String, Object> influencingFactorValue = Maps.newLinkedHashMap();
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
                int columnIndex = cell.getColumnIndex();
                String headerName = findHeaderByColumnIndex(columnIndex, indexMap);
                influencingFactorValue.put(headerName, getCellValueByType(cell));
            }
            underWriterRoutingLevel.add(influencingFactorValue);
        }
        return underWriterRoutingLevel;
    }

    public List<Map<Object,Map<String,Object>>> groupUnderWriterLineItemByInfluencingFactor(List<Map<String,Object>> underWriterRoutingLineItemList,List<UnderWriterInfluencingFactor>  underWriterInfluencingFactors){
        List<Map<Object,Map<String,Object>>> underWriterRoutingLevelList = Lists.newArrayList();
        for (Map<String,Object> underWriterMap : underWriterRoutingLineItemList){
            Map<Object,Map<String,Object>> underWriterLineItemMap = Maps.newLinkedHashMap();
            for (UnderWriterInfluencingFactor underWriterInfluencingFactor :underWriterInfluencingFactors){
                underWriterLineItemMap = underWriterInfluencingFactor.groupUnderWriterRoutingLevelItem(underWriterMap, underWriterLineItemMap);
            }
            if (underWriterMap.containsKey(AppConstants.UNDER_WRITER_ROUTING_HEADER_NAME)){
                Map<String,Object> routingLevelMap = Maps.newLinkedHashMap();
                routingLevelMap.put(AppConstants.UNDER_WRITER_ROUTING_HEADER_NAME,underWriterMap.get(AppConstants.UNDER_WRITER_ROUTING_HEADER_NAME));
                underWriterLineItemMap.put(AppConstants.UNDER_WRITER_ROUTING_HEADER_NAME,routingLevelMap);
            }
            underWriterRoutingLevelList.add(underWriterLineItemMap);
        }
        return underWriterRoutingLevelList;
    }

    private void checkForValidHeader(List<Cell> headerRowCells, List<String> selectedInfluencingFactors){
        if (headerRowCells.size()!= selectedInfluencingFactors.size()) {
            raiseNumberCellMismatchException(selectedInfluencingFactors.size(), headerRowCells.size());
        }
        for (int index = 0; index<=selectedInfluencingFactors.size()-1 ;index++){
            if (!headerRowCells.get(index).getStringCellValue().equals(selectedInfluencingFactors.get(index))){
                raiseHeaderInvalidException();
            }
        }
    }

    public String findHeaderByColumnIndex(int columnIndex, Map<String,Integer> headerRowIndexMap) {
        String headerName = "";
        for (Map.Entry<String, Integer> columnIndexer : headerRowIndexMap.entrySet()) {
            if (columnIndex == columnIndexer.getValue()) {
                headerName = columnIndexer.getKey();
                break;
            }
        }
        return headerName;
    }

    Map<String, Integer> buildInfluencingFactorAndCellIndexMap(Row headerRow, List<String> underWriterInfluencingFactors) {
        Iterator<Cell> cellIterator = headerRow.cellIterator();
        Stream<Cell> targetStream = StreamSupport.stream(Spliterators.spliteratorUnknownSize(cellIterator, Spliterator.ORDERED), true);
        List<Cell> cells = targetStream.collect(Collectors.toList());
        Map<String, Integer> indexMap = underWriterInfluencingFactors.parallelStream().collect(Collectors.toMap(Function.identity(), new TransformToIndexMap(cells)));
        return indexMap;
    }

    private class TransformToIndexMap implements Function<String, Integer> {

        List<Cell> cells;
        TransformToIndexMap(List<Cell> cells) {
            this.cells = cells;
        }

        @Override
        public Integer apply(String underWriterInfluencingFactor) {
            return getCellNumberFor(underWriterInfluencingFactor, cells);
        }
    }

    private int getCellNumberFor(String cellName, List<Cell> cells) {
        return cells.stream().filter(cell -> cellName.equals(cell.getStringCellValue().trim())).mapToInt(cell -> cells.indexOf(cell)).findAny().getAsInt();
    }

    private List<String> getUnderWriterInfluencingFactorsRange(List<UnderWriterInfluencingFactor> underWriterInfluencingFactors){
        List<String> influencingFactorRange = Lists.newArrayList();
        underWriterInfluencingFactors.forEach(underWriterInfluencingFactor->
                underWriterInfluencingFactor.getInfluencingFactorRange(influencingFactorRange));
       /* Collections.sort(influencingFactorRange);*/
        return influencingFactorRange;
    }

    private void createErrorCellAndWriteErrorMessage(int errorCellNumber, String validValueErrorMessage, Row headerRow, Row dataRow) {
        List<Cell> headerCellList = Lists.newArrayList(headerRow.cellIterator());
        Optional<Cell> errorCellOptional = headerCellList.stream().filter(new Predicate<Cell>() {
            @Override
            public boolean test(Cell cell) {
                return AppConstants.ERROR_CELL_HEADER_NAME.equals(cell.getStringCellValue());
            }
        }).findAny();
        if (!errorCellOptional.isPresent()) {
            headerRow.createCell(errorCellNumber).setCellValue(AppConstants.ERROR_CELL_HEADER_NAME);
        }
        Cell errorCell;
        if (dataRow.getCell(errorCellNumber)==null)
            errorCell = dataRow.createCell(errorCellNumber);
        else
            errorCell = dataRow.getCell(errorCellNumber);
        String errorMessage = validValueErrorMessage;
        String error =  errorCell.getStringCellValue()+"\n "+errorMessage;
        errorCell.setCellValue(error);

    }

    private String getCellValueByType(Cell cell) {
        String cellValue = "";
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_NUMERIC:
                cellValue = String.valueOf(cell.getNumericCellValue());
                break;
            case Cell.CELL_TYPE_STRING:
                cellValue = cell.getStringCellValue();
                break;
            case Cell.CELL_TYPE_BOOLEAN:
                cellValue = String.valueOf(cell.getStringCellValue());
                break;
            case Cell.CELL_TYPE_BLANK:
                cellValue = String.valueOf(cell.getStringCellValue());
                break;
        }
        return cellValue;
    }

}

