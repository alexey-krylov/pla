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
import com.pla.core.query.MasterFinder;
import com.pla.publishedlanguage.domain.model.PremiumInfluencingFactor;
import com.pla.sharedkernel.identifier.CoverageId;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.nthdimenzion.common.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
@Component
public class PremiumTemplateParser {

    private MasterFinder masterFinder;

    @Autowired
    public PremiumTemplateParser(MasterFinder masterFinder) {
        this.masterFinder = masterFinder;
    }

    public Map<Integer, String> validatePremiumDataForAGivenPlanAndCoverage(HSSFWorkbook hssfWorkbook, Plan plan, CoverageId coverageId, List<PremiumInfluencingFactor> premiumInfluencingFactors) throws IOException, PremiumTemplateParseException {
        HSSFSheet premiumSheet = hssfWorkbook.getSheetAt(0);
        Iterator<Row> rowsIterator = premiumSheet.iterator();
        Row headerRow = rowsIterator.next();
        if (!isValidHeader(headerRow, premiumInfluencingFactors)) {
            raiseHeaderInvalidException();
        }
        int noOfExpectedPremiumRow = getTotalNoOfPremiumCombination(premiumInfluencingFactors, coverageId, plan);
        int noOfRowNonEmptyRow = getNoOfNonEmptyRow(premiumSheet);
        if (noOfRowNonEmptyRow != noOfExpectedPremiumRow) {
            raiseNumberRowMismatchException(noOfExpectedPremiumRow, noOfRowNonEmptyRow);
        }
        Map<PremiumInfluencingFactor, Integer> influencingFactorCellIndexMap = buildInfluencingFactorAndCellIndexMap(headerRow, premiumInfluencingFactors);
        List<Row> allRows = Lists.newArrayList(rowsIterator);
        List<Row> allRowsToBeCompared = Lists.newArrayList(allRows);
        int premiumCellNumber = getCellNumberFor(AppConstants.PREMIUM_CELL_HEADER_NAME, transformCellIteratorToList(headerRow.cellIterator()));
        Map<Integer, String> errorRowAndMessageMap = Maps.newHashMap();
        for (Row row : allRows) {
            String validValueErrorMessage = "";
            for (PremiumInfluencingFactor premiumInfluencingFactor : premiumInfluencingFactors) {
                int cellIndex = influencingFactorCellIndexMap.get(premiumInfluencingFactor);
                Cell cell = row.getCell(cellIndex);
                if (cell == null) {
                    validValueErrorMessage = validValueErrorMessage + "\n" + premiumInfluencingFactor.getDescription() + " is missing";
                    continue;
                }
                boolean isValidValue = premiumInfluencingFactor.isValidValue(plan, coverageId, Cell.CELL_TYPE_NUMERIC == cell.getCellType() ? ((Long) ((Double) cell.getNumericCellValue()).longValue()).toString() : cell.getStringCellValue());
                if (!isValidValue) {
                    validValueErrorMessage = validValueErrorMessage + "\n" + premiumInfluencingFactor.getErrorMessage(Cell.CELL_TYPE_NUMERIC == cell.getCellType() ? ((Long) ((Double) cell.getNumericCellValue()).longValue()).toString() : cell.getStringCellValue());
                }
            }
            Cell premiumCell = row.getCell(premiumCellNumber);
            if (premiumCell == null || Cell.CELL_TYPE_NUMERIC != premiumCell.getCellType()) {
                validValueErrorMessage = validValueErrorMessage + "\n" + " Premium value is missing";
            }
            String duplicateRowNumbers = checkAndGetDuplicateWithRow(row, allRowsToBeCompared, premiumCellNumber);
            String duplicateRowErrorMessage = "";
            if (isNotEmpty(duplicateRowNumbers.trim())) {
                duplicateRowErrorMessage = "This row is duplicate with row(s): " + duplicateRowNumbers;
            }
            if (isNotEmpty(validValueErrorMessage.trim()) || isNotEmpty(duplicateRowErrorMessage.trim())) {
                errorRowAndMessageMap.put(row.getRowNum() + 1, validValueErrorMessage + "\n" + duplicateRowErrorMessage);
            }
        }
        return errorRowAndMessageMap;
    }


    private String checkAndGetDuplicateWithRow(Row row, List<Row> allRows, int premiumCellNumber) {
        String duplicateRows = "";
        for (Row otherRow : allRows) {
            if (isTwoRowIdentical(row, otherRow, premiumCellNumber)) {
                duplicateRows = duplicateRows + (otherRow.getRowNum() + 1) + ",";
            }
        }
        return duplicateRows;
    }


    boolean isTwoRowIdentical(Row firstRow, Row secondRow, int premiumCellNumber) {
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

    List<Cell> transformCellIteratorToList(Iterator<Cell> cellIterator) {
        Stream<Cell> cellStream = StreamSupport.stream(Spliterators.spliteratorUnknownSize(cellIterator, Spliterator.ORDERED), true);
        List<Cell> cellList = cellStream.collect(Collectors.toList());
        return cellList;
    }


    boolean areAllCellContainsUniqueValue(List<Cell> firstRowCells, List<Cell> secondRowCells, int premiumCellNumber, int noOfCell) {
        boolean allHasUniqueValue = false;
        int noOfCellHavingSameValue = 0;
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
            if (allHasUniqueValue) {
                noOfCellHavingSameValue = noOfCellHavingSameValue + 1;
            }
        }
        return noOfCell == noOfCellHavingSameValue;
    }


    Integer getNoOfNonEmptyRow(HSSFSheet hssfSheet) {
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


    boolean isRowEmpty(Row row) {
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


    boolean isValidHeader(Row headerRow, List<PremiumInfluencingFactor> premiumInfluencingFactors) {
        Iterator<Cell> cellIterator = headerRow.cellIterator();
        Stream<Cell> targetStream = StreamSupport.stream(Spliterators.spliteratorUnknownSize(cellIterator, Spliterator.ORDERED), true);
        List<Cell> cells = targetStream.collect(Collectors.toList());
        List<String> influencingFactorsPresentInExcel = cells.stream().map(cell -> cell.getStringCellValue()).collect(Collectors.toList());
        influencingFactorsPresentInExcel.remove(AppConstants.PREMIUM_CELL_HEADER_NAME);
        List<String> transformedInfluencingFactors = premiumInfluencingFactors.stream().map(premiumInfluencingFactor -> premiumInfluencingFactor.getDescription()).collect(Collectors.toList());
        return isTemplateContainsSameInfluencingFactor(transformedInfluencingFactors, influencingFactorsPresentInExcel);
    }


    private boolean isTemplateContainsSameInfluencingFactor(List<String> selectedInfluencingFactors, List<String> influencingFactorsInHeader) {
        boolean containsHeader = true;
        for (String influencingFactorInHeader : influencingFactorsInHeader) {
            if (!selectedInfluencingFactors.contains(influencingFactorInHeader)) {
                containsHeader = false;
                break;
            }
        }
        return containsHeader;
    }


    Map<PremiumInfluencingFactor, Integer> buildInfluencingFactorAndCellIndexMap(Row headerRow, List<PremiumInfluencingFactor> premiumInfluencingFactors) {
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


    int getCellNumberFor(String cellName, List<Cell> cells) {
        return cells.stream().filter(cell -> cellName.equals(cell.getStringCellValue().trim())).mapToInt(cell -> cells.indexOf(cell)).findAny().getAsInt();
    }


    // TODO :  write test
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
                if (cell == null) {
                    raiseNotValidTemplateException();
                }
                String cellValue = Cell.CELL_TYPE_NUMERIC == cell.getCellType() ? ((Double) cell.getNumericCellValue()).toString() : cell.getStringCellValue();
                influencingFactorValueMap.put(premiumInfluencingFactor, cellValue);
            }
            Cell premiumCell = dataRow.getCell(premiumCellNumber);
            premiumLineItemMap.put(influencingFactorValueMap, premiumCell.getNumericCellValue());
            premiumInfluencingFactorLineItem.add(premiumLineItemMap);
        }
        return premiumInfluencingFactorLineItem;
    }


    int getTotalNoOfPremiumCombination(List<PremiumInfluencingFactor> premiumInfluencingFactors, CoverageId coverageId, Plan plan) {
        Integer noOfRow = 1;
        for (PremiumInfluencingFactor premiumInfluencingFactor : premiumInfluencingFactors) {
            String[] data = getAllowedValues(premiumInfluencingFactor, plan, coverageId);
            Integer lengthOfAllowedValues = data.length == 0 ? 1 : data.length;
            noOfRow = noOfRow * lengthOfAllowedValues;
        }
        return noOfRow;
    }

    private String[] getAllowedValues(PremiumInfluencingFactor premiumInfluencingFactor, Plan plan, CoverageId coverageId) {
        if (PremiumInfluencingFactor.INDUSTRY.equals(premiumInfluencingFactor)) {
            List<Map<String, Object>> allIndustries = masterFinder.getAllIndustry();
            allIndustries = isNotEmpty(allIndustries) ? allIndustries : Lists.newArrayList();
            String[] industries = new String[allIndustries.size()];
            for (int count = 0; count < allIndustries.size(); count++) {
                Map<String, Object> industryMap = allIndustries.get(count);
                industries[count] = (String) industryMap.get("description");
            }
            return industries;
        } else if (PremiumInfluencingFactor.DESIGNATION.equals(premiumInfluencingFactor)) {
            List<Map<String, Object>> allDesignations = masterFinder.getAllDesignation();
            allDesignations = isNotEmpty(allDesignations) ? allDesignations : Lists.newArrayList();
            String[] designations = new String[allDesignations.size()];
            for (int count = 0; count < allDesignations.size(); count++) {
                Map<String, Object> industryMap = allDesignations.get(count);
                designations[count] = (String) industryMap.get("description");
            }
            return designations;

        } else if (PremiumInfluencingFactor.OCCUPATION_CATEGORY.equals(premiumInfluencingFactor)) {
            List<Map<String, Object>> occupationCategories = masterFinder.getAllOccupationClass();
            occupationCategories = isNotEmpty(occupationCategories) ? occupationCategories : Lists.newArrayList();
            String[] categories = new String[occupationCategories.size()];
            for (int count = 0; count < occupationCategories.size(); count++) {
                Map<String, Object> occupationCategoryMap = occupationCategories.get(count);
                categories[count] = (String) occupationCategoryMap.get("code");
            }
            return categories;
        }
        return premiumInfluencingFactor.getAllowedValues(plan, coverageId);
    }
}
