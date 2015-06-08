package com.pla.grouplife.quotation.application.service;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.pla.publishedlanguage.contract.IPlanAdapter;
import com.pla.grouplife.quotation.query.InsuredDto;
import com.pla.sharedkernel.domain.model.Relationship;
import com.pla.sharedkernel.identifier.PlanId;
import com.pla.sharedkernel.util.ExcelGeneratorUtil;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
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

import static com.pla.grouplife.quotation.application.service.exception.GLInsuredTemplateExcelParseException.*;
import static com.pla.sharedkernel.util.ExcelGeneratorUtil.getCellValue;
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


    public List<InsuredDto> transformToInsuredDto(HSSFWorkbook hssfWorkbook, List<PlanId> agentPlans) {
        HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(0);
        Iterator<Row> rowIterator = hssfSheet.rowIterator();
        Row headerRow = rowIterator.next();
        final List<String> headers = GLInsuredExcelHeader.getAllowedHeaderForParser(planAdapter, agentPlans);
        final List<String> excelHeaders = getHeaders(headerRow);
        Map<Row, List<Row>> insuredTemplateDataRowMap = groupByRelationship(Lists.newArrayList(rowIterator), GLInsuredExcelHeader.getAllowedHeaders(planAdapter, agentPlans));
        List<InsuredDto> insuredDtoList = insuredTemplateDataRowMap.entrySet().stream().map(new Function<Map.Entry<Row, List<Row>>, InsuredDto>() {
            @Override
            public InsuredDto apply(Map.Entry<Row, List<Row>> rowListEntry) {
                Row insuredRow = rowListEntry.getKey();
                List<Row> dependentRows = rowListEntry.getValue();
                InsuredDto insuredDto = createInsuredDto(insuredRow, excelHeaders, headers, findNonEmptyOptionalCoverageCell(excelHeaders, insuredRow));
                insuredDto.getPlanPremiumDetail().setPlanId(planAdapter.getPlanId(insuredDto.getPlanPremiumDetail().getPlanCode()).getPlanId());
                Set<InsuredDto.InsuredDependentDto> insuredDependentDtoSet = dependentRows.stream().map(new Function<Row, InsuredDto.InsuredDependentDto>() {
                    @Override
                    public InsuredDto.InsuredDependentDto apply(Row row) {
                        InsuredDto.InsuredDependentDto insuredDependentDto = createInsuredDependentDto(row, excelHeaders, headers, findNonEmptyOptionalCoverageCell(excelHeaders, row));
                        insuredDependentDto.getPlanPremiumDetail().setPlanId(planAdapter.getPlanId(insuredDependentDto.getPlanPremiumDetail().getPlanCode()).getPlanId());
                        return insuredDependentDto;
                    }
                }).collect(Collectors.toSet());
                insuredDto.setInsuredDependents(insuredDependentDtoSet);
                return insuredDto;
            }
        }).collect(Collectors.toList());
        return insuredDtoList;
    }


    private InsuredDto.InsuredDependentDto createInsuredDependentDto(Row dependentRow, List<String> excelHeaders, List<String> headers, List<Cell> optionalCoverageCells) {
        InsuredDto.InsuredDependentDto insuredDependentDto = new InsuredDto.InsuredDependentDto();
        for (String header : headers) {
            if (!header.contains(AppConstants.OPTIONAL_COVERAGE_HEADER)) {
                insuredDependentDto = GLInsuredExcelHeader.valueOf(header).populateInsuredDependentDetail(insuredDependentDto, dependentRow, excelHeaders);
            }
        }
        List<InsuredDto.CoveragePremiumDetailDto> coveragePremiumDetails = optionalCoverageCells.stream().map(new Function<Cell, InsuredDto.CoveragePremiumDetailDto>() {
            @Override
            public InsuredDto.CoveragePremiumDetailDto apply(Cell cell) {
                InsuredDto.CoveragePremiumDetailDto coveragePremiumDetailDto = new InsuredDto.CoveragePremiumDetailDto();
                coveragePremiumDetailDto.setCoverageCode(ExcelGeneratorUtil.getCellValue(cell));
                return coveragePremiumDetailDto;
            }
        }).collect(Collectors.toList());
        insuredDependentDto = insuredDependentDto.addCoveragePremiumDetails(coveragePremiumDetails);
        return insuredDependentDto;
    }

    private InsuredDto createInsuredDto(Row row, List<String> excelHeaders, List<String> headers, List<Cell> optionalCoverageCell) {
        InsuredDto insuredDto = new InsuredDto();
        for (String header : headers) {
            if (!header.contains(AppConstants.OPTIONAL_COVERAGE_HEADER)) {
                insuredDto = GLInsuredExcelHeader.valueOf(header).populateInsuredDetail(insuredDto, row, excelHeaders);
            }
        }
        List<InsuredDto.CoveragePremiumDetailDto> coveragePremiumDetails = optionalCoverageCell.stream().map(new Function<Cell, InsuredDto.CoveragePremiumDetailDto>() {
            @Override
            public InsuredDto.CoveragePremiumDetailDto apply(Cell cell) {
                InsuredDto.CoveragePremiumDetailDto coveragePremiumDetailDto = new InsuredDto.CoveragePremiumDetailDto();
                coveragePremiumDetailDto.setCoverageCode(ExcelGeneratorUtil.getCellValue(cell));
                return coveragePremiumDetailDto;
            }
        }).collect(Collectors.toList());
        insuredDto = insuredDto.addCoveragePremiumDetails(coveragePremiumDetails);
        return insuredDto;
    }


    public boolean isValidInsuredExcel(HSSFWorkbook hssfWorkbook, boolean samePlanForAllCategory, boolean samePlanForAllRelation, List<PlanId> agentPlans) {
        boolean isValidTemplate = true;
        HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(0);
        Iterator<Row> rowIterator = hssfSheet.rowIterator();
        Row headerRow = rowIterator.next();
        List<Row> dataRows = Lists.newArrayList(rowIterator);
        final List<String> headers = getHeaders(headerRow);
        boolean isValidHeader = isValidHeader(headers, agentPlans);
        if (!isValidHeader) {
            raiseNotValidHeaderException();
        }
        boolean isFirstRowContainsSelfRelationship = isFirstRowContainsSelfRelationship(dataRows, headers);
        if (!isFirstRowContainsSelfRelationship) {
            raiseNotValidFirstHeaderException();
        }
        Map<Row, List<Row>> insuredDependentMap = groupByRelationship(dataRows, headers);
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
        Iterator<Row> dataRowIterator = dataRows.iterator();
        while (dataRowIterator.hasNext()) {
            Row currentRow = dataRowIterator.next();
            String errorMessage = validateRow(currentRow, GLInsuredExcelHeader.getAllowedHeaderForParser(planAdapter, agentPlans));
            String coverageErrorMessage = validateOptionalCoverageCell(headers, currentRow);
            if (isEmpty(errorMessage) && isEmpty(coverageErrorMessage)) {
                continue;
            }
            isValidTemplate = false;
            if (errorMessageHeaderCell == null) {
                HSSFCellStyle cellStyle = hssfSheet.getWorkbook().createCellStyle();
                HSSFFont hssfFont = hssfSheet.getWorkbook().createFont();
                hssfFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
                cellStyle.setFont(hssfFont);
                errorMessageHeaderCell = headerRow.createCell(headers.size());
                errorMessageHeaderCell.setCellValue(AppConstants.ERROR_CELL_HEADER_NAME);
                errorMessageHeaderCell.setCellStyle(cellStyle);
            }
            errorMessage = errorMessage + "\n" + coverageErrorMessage;
            Cell errorMessageCell = currentRow.createCell(headers.size());
            errorMessageCell.setCellValue(errorMessage);
        }
        return isValidTemplate;
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
        planCode = planCode.substring(0,planCode.indexOf("."));
        Set<String> errorMessages = Sets.newHashSet();
        List<Cell> optionalCoverageCells = findNonEmptyOptionalCoverageCell(headers, row);
        final String finalPlanCode = planCode;
        optionalCoverageCells.forEach(optionalCoverageCell -> {
            String optionalCoverageCode = getCellValue(optionalCoverageCell);
            if (isNotEmpty(optionalCoverageCode) && !isValidCoverage(finalPlanCode, optionalCoverageCode)) {
                errorMessages.add(optionalCoverageCode + "  is not valid for plan " + finalPlanCode + ".");
            }
        });
/*        optionalCoverageCells.forEach(optionalCoverageCell -> {
            String coverageCode = getCellValue(optionalCoverageCell);
            optionalCoverageCells.forEach(nextOptionalCoverageCell -> {
                String nextCoverageCode = getCellValue(nextOptionalCoverageCell);
                if (isNotEmpty(coverageCode) && isNotEmpty(nextCoverageCode) && coverageCode.equals(nextCoverageCode)) {
                    errorMessages.add("Row contains Duplicate coverage code.");
                }
            });
        });*/
        return buildErrorMessage(errorMessages);
    }

    private List<Cell> findNonEmptyOptionalCoverageCell(List<String> headers, Row row) {
        List<Cell> optionalCoverageCells = Lists.newArrayList();
        headers.forEach(header -> {
            if (header.contains(AppConstants.OPTIONAL_COVERAGE_HEADER) && !header.contains(AppConstants.PREMIUM_CELL_HEADER_NAME)) {
                int cellNumber = headers.indexOf(header);
                Cell cell = row.getCell(cellNumber);
                if (isNotEmpty(getCellValue(cell))) {
                    optionalCoverageCells.add(cell);
                }
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

    private boolean isFirstRowContainsSelfRelationship(List<Row> dataRows, List<String> headers) {
        Row row = dataRows.iterator().next();
        Cell relationshipCell = row.getCell(headers.indexOf(GLInsuredExcelHeader.RELATIONSHIP.getDescription()));
        String relationship = getCellValue(relationshipCell);
        return Relationship.SELF.description.equals(relationship);
    }

    private Map<Row, List<Row>> groupByRelationship(List<Row> dataRows, List<String> headers) {
        Iterator<Row> rowIterator = dataRows.iterator();
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
