/*
 * Copyright (c) 3/30/15 8:16 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.application.service.plan.premium;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pla.core.application.exception.PremiumTemplateParseException;
import com.pla.core.domain.model.plan.Plan;
import com.pla.sharedkernel.domain.model.PremiumInfluencingFactor;
import com.pla.sharedkernel.identifier.CoverageId;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.nthdimenzion.common.AppConstants;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static com.pla.core.application.exception.PremiumTemplateParseException.*;
import static org.nthdimenzion.utils.UtilValidator.isEmpty;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * @author: Samir
 * @since 1.0 30/03/2015
 */
public class PremiumTemplateParser {

    public boolean validatePremiumDataForAGivenPlanAndCoverage(HSSFWorkbook hssfWorkbook, Plan plan, CoverageId coverageId, List<PremiumInfluencingFactor> premiumInfluencingFactors) throws IOException, PremiumTemplateParseException {
        boolean isValidPremiumTemplate = true;
        HSSFSheet premiumSheet = hssfWorkbook.getSheetAt(0);
        Iterator<Row> rowsIterator = premiumSheet.iterator();
        Row headerRow = rowsIterator.next();
        if (!isValidHeader(headerRow, premiumInfluencingFactors)) {
            raiseHeaderInvalidException();
        }
        int noOfExpectedPremiumRow = plan.getTotalNoOfPremiumCombination(premiumInfluencingFactors, coverageId);
        int noOfRowNonEmptyRow = getNoOfNonEmptyRow(premiumSheet);
        if (noOfRowNonEmptyRow != noOfExpectedPremiumRow) {
            raiseNumberRowMismatchException(noOfExpectedPremiumRow, noOfRowNonEmptyRow);
        }
        Map<PremiumInfluencingFactor, Integer> influencingFactorCellIndexMap = buildInfluencingFactorAndCellIndexMap(headerRow, premiumInfluencingFactors);
        List<Row> allRows = Lists.newArrayList(rowsIterator);
        List<Row> allRowsToBeCompared = Lists.newArrayList(allRows);
        int premiumCellNumber = getCellNumberFor(AppConstants.PREMIUM_CELL_HEADER_NAME, transformCellIteratorToList(headerRow.cellIterator()));
        for (Row row : allRows) {
            String validValueErrorMessage = "";
            for (PremiumInfluencingFactor premiumInfluencingFactor : premiumInfluencingFactors) {
                int cellIndex = influencingFactorCellIndexMap.get(premiumInfluencingFactor);
                Cell cell = row.getCell(cellIndex);
                if (cell == null) {
                    isValidPremiumTemplate = false;
                    validValueErrorMessage = validValueErrorMessage + "\n" + premiumInfluencingFactor.getDescription() + " is missing";
                    continue;
                }
                boolean isValidValue = premiumInfluencingFactor.isValidValue(plan, coverageId, Cell.CELL_TYPE_NUMERIC == cell.getCellType() ? ((Double) cell.getNumericCellValue()).toString() : cell.getStringCellValue());
                if (!isValidValue) {
                    isValidPremiumTemplate = false;
                    validValueErrorMessage = validValueErrorMessage + "\n" + premiumInfluencingFactor.getErrorMessage(Cell.CELL_TYPE_NUMERIC == cell.getCellType() ? ((Double) cell.getNumericCellValue()).toString() : cell.getStringCellValue());
                }
            }
            Cell premiumCell = row.getCell(premiumCellNumber);
            if (premiumCell == null || Cell.CELL_TYPE_NUMERIC != premiumCell.getCellType()) {
                isValidPremiumTemplate = false;
                validValueErrorMessage = validValueErrorMessage + "\n" + " Premium value is missing";
            }
            String duplicateRowNumbers = checkAndGetDuplicateWithRow(row, allRowsToBeCompared, premiumCellNumber);
            int errorCellNumber = premiumInfluencingFactors.size() + 1;
            String duplicateRowErrorMessage = "";
            if (isNotEmpty(duplicateRowNumbers.trim())) {
                isValidPremiumTemplate = false;
                duplicateRowErrorMessage = "This row is duplicate with row(s): " + duplicateRowNumbers;
            }
            if (isNotEmpty(validValueErrorMessage.trim()) || isNotEmpty(duplicateRowErrorMessage.trim())) {
                isValidPremiumTemplate = false;
                createErrorCellAndWriteErrorMessage(errorCellNumber, validValueErrorMessage, duplicateRowErrorMessage, headerRow, row);
            }
        }
        return isValidPremiumTemplate;
    }

    private void createErrorCellAndWriteErrorMessage(int errorCellNumber, String validValueErrorMessage, String duplicateRowErrorMessage, Row headerRow, Row dataRow) {
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
        Cell errorCell = dataRow.createCell(errorCellNumber);
        String errorMessage = validValueErrorMessage + "\n" + duplicateRowErrorMessage;
        errorCell.setCellValue(errorMessage);
    }


    private String checkAndGetDuplicateWithRow(Row row, List<Row> allRows, int premiumCellNumber) {
        List<Row> duplicationRows = allRows.parallelStream().filter(new Predicate<Row>() {
            @Override
            public boolean test(Row otherRow) {
                return isTwoRowIdentical(row, otherRow, premiumCellNumber);
            }
        }).collect(Collectors.toList());
        String duplicateRows = "";
        for (Row duplicationRow : duplicationRows) {
            duplicateRows = duplicateRows + duplicationRow.getRowNum() + ",";
        }
        return duplicateRows;
    }

    private boolean isTwoRowIdentical(Row firstRow, Row secondRow, int premiumCellNumber) {
        if (firstRow.getRowNum() == secondRow.getRowNum()) {
            return false;
        }
        boolean isDuplicate = false;
        List<Cell> firstRowCellList = transformCellIteratorToList(firstRow.cellIterator());
        List<Cell> secondRowCellList = transformCellIteratorToList(secondRow.cellIterator());
        secondRowCellList.remove(secondRow.getCell(premiumCellNumber + 1));
        if (firstRowCellList.size() == secondRowCellList.size()) {
            isDuplicate = areAllCellContainsUniqueValue(firstRowCellList, secondRowCellList, premiumCellNumber, firstRowCellList.size());
        }
        return isDuplicate;
    }

    private List<Cell> transformCellIteratorToList(Iterator<Cell> cellIterator) {
        Stream<Cell> cellStream = StreamSupport.stream(Spliterators.spliteratorUnknownSize(cellIterator, Spliterator.ORDERED), true);
        List<Cell> cellList = cellStream.collect(Collectors.toList());
        return cellList;
    }

    private boolean areAllCellContainsUniqueValue(List<Cell> firstRowCells, List<Cell> secondRowCells, int premiumCellNumber, int noOfCell) {
        boolean allHasUniqueValue = true;
        for (int count = 0; count < noOfCell; count++) {
            if (count != premiumCellNumber) {
                Cell firstRowCell = firstRowCells.get(count);
                Cell secondRowCell = secondRowCells.get(count);
                if (firstRowCell != null && secondRowCell != null && firstRowCell.getCellType() == secondRowCell.getCellType()) {
                    if (Cell.CELL_TYPE_NUMERIC == firstRowCell.getCellType()) {
                        allHasUniqueValue = firstRowCell.getNumericCellValue() == secondRowCell.getNumericCellValue();
                    } else if (Cell.CELL_TYPE_STRING == firstRowCell.getCellType()) {
                        allHasUniqueValue = firstRowCell.getStringCellValue().equals(secondRowCell.getStringCellValue());
                    }
                }
            }
        }
        return allHasUniqueValue;
    }

    private Integer getNoOfNonEmptyRow(HSSFSheet hssfSheet) {
        Iterator<Row> rowsIterator = hssfSheet.iterator();
        rowsIterator.next();
        List<Row> nonEmptyRows = Lists.newArrayList();
        List<Row> allRows = Lists.newArrayList(rowsIterator);
        for (Row row : allRows) {
            if (!isRowEmpty(row)) {
                nonEmptyRows.add(row);
            } else {
                raiseTemplateContainsEmptyRowInBetweenException();
            }
        }
        return isEmpty(nonEmptyRows) ? 0 : nonEmptyRows.size();
    }

    private boolean isRowEmpty(Row row) {
        List<Cell> cells = Lists.newArrayList(row.cellIterator());
        List<Cell> nonEmptyCells = cells.stream().filter(new Predicate<Cell>() {
            @Override
            public boolean test(Cell cell) {
                String cellValue = "";
                if (cell != null) {
                    cellValue = Cell.CELL_TYPE_NUMERIC == cell.getCellType() ? ((Double) cell.getNumericCellValue()).toString() : cell.getStringCellValue();
                }
                return isNotEmpty(cellValue);
            }
        }).collect(Collectors.toList());
        return isEmpty(nonEmptyCells);
    }

    private boolean isValidHeader(Row headerRow, List<PremiumInfluencingFactor> premiumInfluencingFactors) {
        Iterator<Cell> cellIterator = headerRow.cellIterator();
        Stream<Cell> targetStream = StreamSupport.stream(Spliterators.spliteratorUnknownSize(cellIterator, Spliterator.ORDERED), true);
        List<Cell> cells = targetStream.collect(Collectors.toList());
        List<String> influencingFactorsPresentInExcel = cells.stream().map(cell -> cell.getStringCellValue()).collect(Collectors.toList());
        influencingFactorsPresentInExcel.remove(AppConstants.PREMIUM_CELL_HEADER_NAME);
        List<String> transformedInfluencingFactors = premiumInfluencingFactors.stream().map(premiumInfluencingFactor -> premiumInfluencingFactor.getDescription()).collect(Collectors.toList());
        return transformedInfluencingFactors.equals(influencingFactorsPresentInExcel);
    }

    private Map<PremiumInfluencingFactor, Integer> buildInfluencingFactorAndCellIndexMap(Row headerRow, List<PremiumInfluencingFactor> premiumInfluencingFactors) {
        Iterator<Cell> cellIterator = headerRow.cellIterator();
        Stream<Cell> targetStream = StreamSupport.stream(Spliterators.spliteratorUnknownSize(cellIterator, Spliterator.ORDERED), true);
        List<Cell> cells = targetStream.collect(Collectors.toList());
        Map<PremiumInfluencingFactor, Integer> indexMap = premiumInfluencingFactors.parallelStream().collect(Collectors.toMap(Function.identity(), new TransformToIndexMap(cells)));
        return indexMap;
    }

    private class TransformToIndexMap implements Function<PremiumInfluencingFactor, Integer> {
        List<Cell> cells;

        TransformToIndexMap(List<Cell> cells) {
            this.cells = cells;
        }

        @Override
        public Integer apply(PremiumInfluencingFactor premiumInfluencingFactor) {
            return getCellNumberFor(premiumInfluencingFactor.getDescription(), cells);
        }
    }

    private int getCellNumberFor(String cellName, List<Cell> cells) {
        return cells.stream().filter(cell -> cellName.equals(cell.getStringCellValue().trim())).mapToInt(cell -> cells.indexOf(cell)).findAny().getAsInt();
    }


    public List<Map<Map<PremiumInfluencingFactor, String>, Double>> parseAndTransformToPremiumData(HSSFWorkbook hssfWorkbook, List<PremiumInfluencingFactor> premiumInfluencingFactors) {
        HSSFSheet premiumSheet = hssfWorkbook.getSheetAt(0);
        Iterator<Row> rowsIterator = premiumSheet.iterator();
        Row headerRow = rowsIterator.next();
        int premiumCellNumber = getCellNumberFor(AppConstants.PREMIUM_CELL_HEADER_NAME, Lists.newArrayList(headerRow.cellIterator()));
        Map<PremiumInfluencingFactor, Integer> influencingFactorCellIndexMap = buildInfluencingFactorAndCellIndexMap(headerRow, premiumInfluencingFactors);
        List<Row> validRows = Lists.newArrayList(rowsIterator);
        List<Map<Map<PremiumInfluencingFactor, String>, Double>> premiumInfluencingFactorLineItem = Lists.newArrayList();
        for (Row dataRow : validRows) {
            Map<Map<PremiumInfluencingFactor, String>, Double> premiumLineItemMap = Maps.newHashMap();
            Map<PremiumInfluencingFactor, String> influencingFactorValueMap = Maps.newHashMap();
            for (PremiumInfluencingFactor premiumInfluencingFactor : premiumInfluencingFactors) {
                int cellNumber = influencingFactorCellIndexMap.get(premiumInfluencingFactor);
                Cell cell = dataRow.getCell(cellNumber);
                String cellValue = Cell.CELL_TYPE_NUMERIC == cell.getCellType() ? ((Double) cell.getNumericCellValue()).toString() : cell.getStringCellValue();
                influencingFactorValueMap.put(premiumInfluencingFactor, cellValue);
            }
            Cell premiumCell = dataRow.getCell(premiumCellNumber);
            premiumLineItemMap.put(influencingFactorValueMap, premiumCell.getNumericCellValue());
            premiumInfluencingFactorLineItem.add(premiumLineItemMap);
        }
        return premiumInfluencingFactorLineItem;
    }

}
