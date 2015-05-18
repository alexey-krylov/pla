package com.pla.quotation.application.service;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.pla.publishedlanguage.contract.IPlanAdapter;
import com.pla.quotation.query.InsuredDto;
import com.pla.sharedkernel.domain.model.Relationship;
import com.pla.sharedkernel.identifier.PlanId;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.nthdimenzion.common.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.pla.quotation.application.service.exception.GLInsuredTemplateExcelParseException.*;
import static org.nthdimenzion.utils.UtilValidator.isEmpty;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Created by Samir on 5/1/2015.
 */
@Component(value = "glInsuredExcelParser")
public class GLInsuredExcelParser {

    private IPlanAdapter planAdapter;

    @Autowired
    public GLInsuredExcelParser(IPlanAdapter planAdapter) {
        this.planAdapter = planAdapter;
    }


    public List<InsuredDto> transformToInsuredDto(HSSFWorkbook hssfWorkbook, boolean samePlanForAllCategory, boolean samePlanForAllRelation) {
        HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(0);
        Iterator<Row> rowIterator = hssfSheet.rowIterator();
        Row headerRow = rowIterator.next();
        final List<String> headers = getHeaders(headerRow);
        Map<Row, List<Row>> insuredTemplateDataRowMap = groupByRelationship(rowIterator, headers);
        List<InsuredDto> insuredDtoList = insuredTemplateDataRowMap.entrySet().stream().map(new Function<Map.Entry<Row, List<Row>>, InsuredDto>() {
            @Override
            public InsuredDto apply(Map.Entry<Row, List<Row>> rowListEntry) {
                return null;
            }
        }).collect(Collectors.toList());
        return insuredDtoList;
    }

    public boolean isValidInsuredExcel(HSSFWorkbook hssfWorkbook, boolean samePlanForAllCategory, boolean samePlanForAllRelation, List<PlanId> agentPlans) {
        HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(0);
        Iterator<Row> rowIterator = hssfSheet.rowIterator();
        Row headerRow = rowIterator.next();
        final List<String> headers = getHeaders(headerRow);
        boolean isValidHeader = isValidHeader(headers, agentPlans);
        if (!isValidHeader) {
            raiseNotValidHeaderException();
        }
        boolean isFirstRowContainsSelfRelationship = isFirstRowContainsSelfRelationship(rowIterator, headers);
        if (!isFirstRowContainsSelfRelationship) {
            raiseNotValidFirstHeaderException();
        }
        Map<Row, List<Row>> insuredDependentMap = groupByRelationship(rowIterator, headers);
        boolean isSamePlanForAllCategory = isSamePlanForAllCategory(insuredDependentMap, headers);
        boolean isSamePlanForAllRelationship = isSamePlanForAllRelation(insuredDependentMap, headers);
        if (samePlanForAllCategory && !isSamePlanForAllCategory) {
            raiseNotSamePlanForAllCategoryException();
        } else if (samePlanForAllRelation && !isSamePlanForAllRelationship) {
            raiseNotSamePlanForAllRelationshipException();
        } else if (samePlanForAllCategory && samePlanForAllRelation && (!isSamePlanForAllCategory && !isSamePlanForAllCategory)) {
            raiseNotSamePlanForAllCategoryAndRelationshipException();
        }
        Cell errorMessageHeaderCell = null;
        while (rowIterator.hasNext()) {
            Row currentRow = rowIterator.next();
            String errorMessage = validateRow(currentRow, headers);
            String coverageErrorMessage = validateOptionalCoverageCell(headers, currentRow);
            if (isEmpty(errorMessage) && isEmpty(coverageErrorMessage)) {
                continue;
            }
            if (errorMessageHeaderCell == null) {
                errorMessageHeaderCell = headerRow.createCell(headers.size());
                errorMessageHeaderCell.setCellValue(AppConstants.ERROR_CELL_HEADER_NAME);
            }
            errorMessage = errorMessage + "\n" + coverageErrorMessage;
            Cell errorMessageCell = headerRow.getCell(headers.size());
            errorMessageCell.setCellValue(errorMessage);
        }
        return false;
    }

    private boolean isValidHeader(List<String> headers, List<PlanId> planIds) {
        List<String> allowedHeaders = GLInsuredExcelHeader.getAllowedHeaders(planAdapter, planIds);
        return isTemplateContainsSameExcelHeader(allowedHeaders, headers);
    }

    private boolean isTemplateContainsSameExcelHeader(List<String> allowedHeaders, List<String> excelHeaders) {
        boolean containsHeader = true;
        for (String influencingFactorInHeader : excelHeaders) {
            if (!allowedHeaders.contains(influencingFactorInHeader)) {
                containsHeader = false;
                break;
            }
        }
        return containsHeader;
    }

    private String validateOptionalCoverageCell(List<String> headers, Row row) {
        Cell planCell = row.getCell(headers.indexOf(GLInsuredExcelHeader.PLAN.getDescription()));
        String planCode = getCellValue(planCell);
        Set<String> errorMessages = Sets.newHashSet();
        List<Cell> optionalCoverageCells = findOptionalCoverageCell(headers, row);
        optionalCoverageCells.forEach(optionalCoverageCell -> {
            String optionalCoverageCode = getCellValue(optionalCoverageCell);
            if (isNotEmpty(optionalCoverageCode) && !isValidCoverage(planCode, optionalCoverageCode)) {
                errorMessages.add(optionalCoverageCode + "  is not valid for plan " + planCode + ".");
            }
        });
        optionalCoverageCells.forEach(optionalCoverageCell -> {
            String coverageCode = getCellValue(optionalCoverageCell);
            optionalCoverageCells.forEach(nextOptionalCoverageCell -> {
                String nextCoverageCode = getCellValue(nextOptionalCoverageCell);
                if (isNotEmpty(coverageCode) && isNotEmpty(nextCoverageCode) && coverageCode.equals(nextCoverageCode)) {
                    errorMessages.add("Row contains Duplicate coverage code.");
                }
            });
        });
        return buildErrorMessage(errorMessages);
    }

    private List<Cell> findOptionalCoverageCell(List<String> headers, Row row) {
        List<Cell> optionalCoverageCells = Lists.newArrayList();
        headers.forEach(header -> {
            if (header.contains(AppConstants.OPTIONAL_COVERAGE_HEADER) && !header.contains(AppConstants.PREMIUM_CELL_HEADER_NAME)) {
                int cellNumber = headers.indexOf(header);
                optionalCoverageCells.add(row.getCell(cellNumber));
            }
        });
        return optionalCoverageCells;
    }

    private String validateRow(Row insureDataRow, List<String> headers) {
        String errorMessage = "";
        Set<String> errorMessages = Sets.newHashSet();
        headers.forEach(header -> {
            if (!header.contains(AppConstants.OPTIONAL_COVERAGE_HEADER)) {
                Cell cell = insureDataRow.getCell(headers.indexOf(header));
                String cellValue = getCellValue(cell);
                GLInsuredExcelHeader glInsuredExcelHeader = GLInsuredExcelHeader.valueOf(header);
                errorMessages.add(glInsuredExcelHeader.validateAndIfNotBuildErrorMessage(planAdapter, insureDataRow, cellValue, headers));
            }
        });
        errorMessage = buildErrorMessage(errorMessages);
        return errorMessage;
    }

    private String buildErrorMessage(Set<String> errorMessages) {
        StringBuilder errorMessageBuilder = new StringBuilder();
        errorMessages.forEach(errorMessage -> {
            if (isNotEmpty(errorMessage)) {
                errorMessageBuilder.append(errorMessage).append("\n");
            }
        });
        return errorMessageBuilder.toString();
    }

    private boolean isValidCoverage(String planCode, String coverageCode) {
        return planAdapter.isValidPlanCoverage(planCode, coverageCode);
    }

    private boolean isFirstRowContainsSelfRelationship(Iterator<Row> dataRows, List<String> headers) {
        Row row = dataRows.next();
        Cell relationshipCell = row.getCell(headers.indexOf(GLInsuredExcelHeader.RELATIONSHIP.getDescription()));
        String relationship = getCellValue(relationshipCell);
        return Relationship.SELF.description.equals(relationship);
    }

    private Map<Row, List<Row>> groupByRelationship(Iterator<Row> rowIterator, List<String> headers) {
        Map<Row, List<Row>> categoryRowMap = Maps.newLinkedHashMap();
        Row selfRelationshipRow = null;
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            Cell relationshipCell = getCellByName(row, headers, GLInsuredExcelHeader.RELATIONSHIP.getDescription());
            String relationship = getCellValue(relationshipCell);
            if (Relationship.SELF.description.equals(relationship)) {
                selfRelationshipRow = row;
                categoryRowMap.put(selfRelationshipRow, new ArrayList<>());
            } else {
                List<Row> rows = categoryRowMap.get(selfRelationshipRow);
                rows.add(row);
                categoryRowMap.put(selfRelationshipRow, rows);
            }
        }
        return categoryRowMap;
    }

    private boolean isSamePlanForAllCategory(Map<Row, List<Row>> relationshipGroupRowMap, List<String> headers) {
        boolean isSamePlanForAllCategory = false;
        for (Map.Entry<Row, List<Row>> rowEntry : relationshipGroupRowMap.entrySet()) {
            Row row = rowEntry.getKey();
            Cell planCell = getCellByName(row, headers, GLInsuredExcelHeader.PLAN.getDescription());
            String planCode = getCellValue(planCell);
            if (isSamePlanForAllCategory) {
                break;
            }
            for (Map.Entry<Row, List<Row>> otherRowEntry : relationshipGroupRowMap.entrySet()) {
                Row otherRow = otherRowEntry.getKey();
                Cell otherPlanCell = getCellByName(otherRow, headers, GLInsuredExcelHeader.PLAN.getDescription());
                String otherPlanCode = getCellValue(otherPlanCell);
                if (isNotEmpty(planCode) && isNotEmpty(otherPlanCode) && planCode.equals(otherPlanCode)) {
                    isSamePlanForAllCategory = true;
                    break;
                }
            }
        }
        return isSamePlanForAllCategory;
    }

    private boolean isSamePlanForAllRelation(Map<Row, List<Row>> relationshipGroupRowMap, List<String> headers) {
        List<Row> allRows = Lists.newArrayList();
        for (Map.Entry<Row, List<Row>> rowEntry : relationshipGroupRowMap.entrySet()) {
            allRows.addAll(rowEntry.getValue());
        }
        return isRowsContainSamePlan(allRows, headers);
    }

    private boolean isRowsContainSamePlan(List<Row> rows, List<String> headers) {
        boolean isRowContainsSamePlan = false;
        for (Row row : rows) {
            if (isRowContainsSamePlan) {
                break;
            }
            Cell planCell = getCellByName(row, headers, GLInsuredExcelHeader.PLAN.getDescription());
            String planCode = getCellValue(planCell);
            for (Row otherRow : rows) {
                Cell otherPlanCell = getCellByName(otherRow, headers, GLInsuredExcelHeader.PLAN.getDescription());
                String otherPlanCode = getCellValue(otherPlanCell);
                if (isNotEmpty(planCode) && isNotEmpty(otherPlanCode) && planCode.equals(otherPlanCode)) {
                    isRowContainsSamePlan = true;
                    break;
                }
            }
        }
        return isRowContainsSamePlan;
    }

    private Cell getCellByName(Row row, List<String> headers, String cellName) {
        int cellNumber = headers.indexOf(cellName);
        return row.getCell(cellNumber);
    }

    private String getCellValue(Cell cell) {
        if (Cell.CELL_TYPE_BLANK == cell.getCellType()) {
            return "";
        } else if (Cell.CELL_TYPE_NUMERIC == cell.getCellType()) {
            return Double.valueOf(cell.getNumericCellValue()).toString().trim();
        }
        return cell.getStringCellValue().trim();
    }

    public List<String> getHeaders(Row headerRow) {
        List<String> headers = Lists.newArrayList();
        Iterator<Cell> cellIterator = headerRow.cellIterator();
        while (cellIterator.hasNext()) {
            Cell headerCell = cellIterator.next();
            headers.add(headerCell.getStringCellValue());
        }
        return ImmutableList.copyOf(headers);
    }

}
