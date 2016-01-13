package com.pla.grouplife.sharedresource.service;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.pla.grouphealth.sharedresource.service.GHInsuredExcelHeader;
import com.pla.grouplife.quotation.query.GLQuotationFinder;
import com.pla.grouplife.sharedresource.dto.InsuredDto;
import com.pla.grouplife.sharedresource.model.vo.GLRelationCategoryCoverDetail;
import com.pla.publishedlanguage.contract.IPlanAdapter;
import com.pla.publishedlanguage.dto.PlanCoverageDetailDto;
import com.pla.sharedkernel.domain.model.Relationship;
import com.pla.sharedkernel.identifier.PlanId;
import com.pla.sharedkernel.util.ExcelGeneratorUtil;
import lombok.Getter;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.nthdimenzion.common.AppConstants;
import org.nthdimenzion.presentation.AppUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.pla.grouphealth.sharedresource.service.QuotationProposalUtilityService.checkIfSameOptionalCoverage;
import static com.pla.grouphealth.sharedresource.service.QuotationProposalUtilityService.getAllOptionalCoverageHeaders;
import static com.pla.grouplife.sharedresource.exception.GLInsuredTemplateExcelParseException.*;
import static com.pla.sharedkernel.util.ExcelGeneratorUtil.getCellValue;
import static org.nthdimenzion.utils.UtilValidator.isEmpty;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Created by Samir on 5/1/2015.
 */
@Component(value = "glInsuredExcelParser")
public class GLInsuredExcelParser {

    private IPlanAdapter planAdapter;

    private GLQuotationFinder glQuotationFinder;

    @Autowired
    public GLInsuredExcelParser(IPlanAdapter planAdapter, GLQuotationFinder glQuotationFinder) {
        this.planAdapter = planAdapter;
        this.glQuotationFinder = glQuotationFinder;
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
                InsuredDto insuredDto = createInsuredDto(insuredRow, excelHeaders, headers, findNonEmptyOptionalCoverageCell(excelHeaders, insuredRow, agentPlans),planAdapter);
                insuredDto.getPlanPremiumDetail().setPlanId(planAdapter.getPlanId(insuredDto.getPlanPremiumDetail().getPlanCode()).getPlanId());
                Set<InsuredDto.InsuredDependentDto> insuredDependentDtoSet = dependentRows.stream().map(new Function<Row, InsuredDto.InsuredDependentDto>() {
                    @Override
                    public InsuredDto.InsuredDependentDto apply(Row row) {
                        InsuredDto.InsuredDependentDto insuredDependentDto = createInsuredDependentDto(row, excelHeaders, headers, findNonEmptyOptionalCoverageCell(excelHeaders, row, agentPlans),planAdapter);
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


    private InsuredDto.InsuredDependentDto createInsuredDependentDto(Row dependentRow, List<String> excelHeaders, List<String> headers, List<OptionalCoverageCellHolder> optionalCoverageCells,IPlanAdapter iPlanAdapter) {
        InsuredDto.InsuredDependentDto insuredDependentDto = new InsuredDto.InsuredDependentDto();
        for (String header : headers) {
            if (!header.contains(AppConstants.OPTIONAL_COVERAGE_HEADER)) {
                insuredDependentDto = GLInsuredExcelHeader.valueOf(header).populateInsuredDependentDetail(insuredDependentDto, dependentRow, excelHeaders, iPlanAdapter);
            }
        }
        final InsuredDto.InsuredDependentDto finalInsuredDependentDto = insuredDependentDto;
        List<InsuredDto.CoveragePremiumDetailDto> coveragePremiumDetails = optionalCoverageCells.stream().map(new Function<OptionalCoverageCellHolder, InsuredDto.CoveragePremiumDetailDto>() {
            @Override
            public InsuredDto.CoveragePremiumDetailDto apply(OptionalCoverageCellHolder optionalCoverageCellHolder) {
                InsuredDto.CoveragePremiumDetailDto coveragePremiumDetailDto = new InsuredDto.CoveragePremiumDetailDto();
                coveragePremiumDetailDto.setCoverageCode(ExcelGeneratorUtil.getCellValue(optionalCoverageCellHolder.getOptionalCoverageCell()));
                String coverageCode = coveragePremiumDetailDto.getCoverageCode();
                if (coverageCode.indexOf(".") != -1) {
                    coverageCode = coverageCode.substring(0, coverageCode.indexOf("."));
                }
                coveragePremiumDetailDto.setCoverageCode(coverageCode);
                if (isNotEmpty(coveragePremiumDetailDto.getCoverageCode())) {
                    Map<String, Object> coverageMap = glQuotationFinder.findCoverageDetailByCoverageCode(coveragePremiumDetailDto.getCoverageCode());
                    coveragePremiumDetailDto.setCoverageId((String) coverageMap.get("coverageId"));
                }
                String optionalCoveragePremiumCellValue = ExcelGeneratorUtil.getCellValue(optionalCoverageCellHolder.getOptionalCoveragePremiumCell());
                BigDecimal coveragePremium = null;
                if (isNotEmpty(optionalCoveragePremiumCellValue) && finalInsuredDependentDto.getNoOfAssured() != null) {
                    coveragePremium = BigDecimal.valueOf(Double.valueOf(optionalCoveragePremiumCellValue)).multiply(BigDecimal.valueOf(Double.valueOf(finalInsuredDependentDto.getNoOfAssured())));
                }
                if (isNotEmpty(optionalCoveragePremiumCellValue) && finalInsuredDependentDto.getFirstName() != null && finalInsuredDependentDto.getDateOfBirth() != null) {
                    coveragePremium = BigDecimal.valueOf(Double.valueOf(optionalCoveragePremiumCellValue)).multiply(new BigDecimal(1));
                }
                coveragePremiumDetailDto.setPremium(coveragePremium);
                int coverageSA = Double.valueOf(ExcelGeneratorUtil.getCellValue(optionalCoverageCellHolder.getOptionalCoverageSACell())).intValue();
                coveragePremiumDetailDto.setSumAssured(BigDecimal.valueOf(coverageSA));
                return coveragePremiumDetailDto;
            }
        }).collect(Collectors.toList());
        insuredDependentDto = insuredDependentDto.addCoveragePremiumDetails(coveragePremiumDetails);
        return insuredDependentDto;
    }

    private InsuredDto createInsuredDto(Row row, List<String> excelHeaders, List<String> headers, List<OptionalCoverageCellHolder> optionalCoverageCells,IPlanAdapter iPlanAdapter) {
        InsuredDto insuredDto = new InsuredDto();
        for (String header : headers) {
            if (!header.contains(AppConstants.OPTIONAL_COVERAGE_HEADER)) {
                insuredDto = GLInsuredExcelHeader.valueOf(header).populateInsuredDetail(insuredDto, row, excelHeaders,iPlanAdapter);
            }
        }
        final InsuredDto finalInsuredDto = insuredDto;
        List<InsuredDto.CoveragePremiumDetailDto> coveragePremiumDetails = optionalCoverageCells.stream().map(new Function<OptionalCoverageCellHolder, InsuredDto.CoveragePremiumDetailDto>() {
            @Override
            public InsuredDto.CoveragePremiumDetailDto apply(OptionalCoverageCellHolder optionalCoverageCellHolder) {
                InsuredDto.CoveragePremiumDetailDto coveragePremiumDetailDto = new InsuredDto.CoveragePremiumDetailDto();
                coveragePremiumDetailDto.setCoverageCode(ExcelGeneratorUtil.getCellValue(optionalCoverageCellHolder.getOptionalCoverageCell()));
                String coverageCode = coveragePremiumDetailDto.getCoverageCode();
                if (coverageCode.indexOf(".") != -1) {
                    coverageCode = coverageCode.substring(0, coverageCode.indexOf("."));
                }
                coveragePremiumDetailDto.setCoverageCode(coverageCode);
                if (isNotEmpty(coveragePremiumDetailDto.getCoverageCode())) {
                    Map<String, Object> coverageMap = glQuotationFinder.findCoverageDetailByCoverageCode(coveragePremiumDetailDto.getCoverageCode());
                    coveragePremiumDetailDto.setCoverageId((String) coverageMap.get("coverageId"));
                }
                int coverageSA = Double.valueOf(ExcelGeneratorUtil.getCellValue(optionalCoverageCellHolder.getOptionalCoverageSACell())).intValue();
                coveragePremiumDetailDto.setSumAssured(BigDecimal.valueOf(coverageSA));
                String optionalCoveragePremiumCellValue = ExcelGeneratorUtil.getCellValue(optionalCoverageCellHolder.getOptionalCoveragePremiumCell());
                BigDecimal coveragePremium = null;
                if (isNotEmpty(optionalCoveragePremiumCellValue) && finalInsuredDto.getNoOfAssured() != null) {
                    coveragePremium = BigDecimal.valueOf(Double.valueOf(optionalCoveragePremiumCellValue)).multiply(BigDecimal.valueOf(Double.valueOf(finalInsuredDto.getNoOfAssured())));
                }
                coveragePremiumDetailDto.setPremium(coveragePremium);
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
        Set<String> optionalCoverageHeaders = getAllOptionalCoverageHeaders(headers);
        boolean isValidHeader = isValidHeader(headers, agentPlans);
        if (!isValidHeader) {
            raiseNotValidHeaderException();
        }
        boolean isFileBlank = isFileBlank(dataRows);
        if (isFileBlank) {
            raiseFileIsBlank();
        }
        boolean isFirstRowContainsSelfRelationship = isFirstRowContainsSelfRelationship(dataRows, headers);
        if (!isFirstRowContainsSelfRelationship) {
            raiseNotValidFirstHeaderException();
        }
        Map<Row, List<Row>> insuredDependentMap = groupByRelationship(dataRows, headers);
        Cell errorMessageHeaderCell = null;
        Iterator<Row> dataRowIterator = dataRows.iterator();
        List<Row> rowList = Lists.newArrayList(dataRows.iterator());
        Set<String> newCategoryRelationErrorMessage = getCategoryWhichDoesNotSelfRelation(rowList,headers);
        while (dataRowIterator.hasNext()) {
            Row currentRow = dataRowIterator.next();
            List<String> excelHeaders = GLInsuredExcelHeader.getAllowedHeaderForParser(planAdapter, agentPlans);
            String errorMessage = validateRow(currentRow, excelHeaders, agentPlans);
            List<OptionalCoverageCellHolder> optionalCoverageCellHolders = findNonEmptyOptionalCoverageCell(headers, currentRow, agentPlans);
            String coverageErrorMessage = validateOptionalCoverageCell(headers, currentRow, optionalCoverageCellHolders);
            Cell categoryCell = currentRow.getCell(headers.indexOf(GLInsuredExcelHeader.CATEGORY.getDescription()));
            String categoryCellValue = getCellValue(categoryCell);
            String categoryDoesNotHaveSelfRelationErrorMessage = "";
            if (newCategoryRelationErrorMessage.contains(categoryCellValue)){
                categoryDoesNotHaveSelfRelationErrorMessage = "The Given Category does not contain the self relation";
            }
            List<Row> duplicateRows = findDuplicateRow(dataRows, currentRow, excelHeaders);
            String duplicateRowErrorMessage = "";
            if (isNotEmpty(duplicateRows)) {
                duplicateRowErrorMessage = "This row is duplicate with row no(s) ";
                final String[] rowNumbers = {""};
                duplicateRows.forEach(duplicateRow -> {
                    rowNumbers[0] = rowNumbers[0] + (duplicateRow.getRowNum() + 1) + ",";
                });
                duplicateRowErrorMessage = duplicateRowErrorMessage + rowNumbers[0] + ".\n";
            }
            String sameOptionalCoverageErrorMessage = checkIfSameOptionalCoverage(currentRow, headers, optionalCoverageHeaders);
            if (isEmpty(errorMessage) && isEmpty(coverageErrorMessage) && isEmpty(duplicateRowErrorMessage) && isEmpty(sameOptionalCoverageErrorMessage) && isEmpty(categoryDoesNotHaveSelfRelationErrorMessage)) {
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
            errorMessage = errorMessage + duplicateRowErrorMessage;
            errorMessage = errorMessage +" \n "+ sameOptionalCoverageErrorMessage;
            errorMessage = errorMessage +" \n "+ categoryDoesNotHaveSelfRelationErrorMessage;
            Cell errorMessageCell = currentRow.createCell(headers.size());
            errorMessageCell.setCellValue(errorMessage);
        }
        if (isValidTemplate) {
            if (samePlanForAllCategory && !samePlanForAllRelation)
                checkAndValidateIsSameForAllCategory(insuredDependentMap, headers, agentPlans);
            if (samePlanForAllRelation && !samePlanForAllCategory)
                checkAndValidateIsSameCoverForAllRelation(dataRows, headers, agentPlans);
            if (samePlanForAllCategory && samePlanForAllRelation)
                checkAndValidateIsSameCoverForAllRelationAndCategory(dataRows, headers, agentPlans);
            if (!samePlanForAllCategory && !samePlanForAllRelation)
                checkAndValidateIsSameCoverForAllRelationAndCategoryCombination(dataRows, headers, agentPlans);
        }
        return isValidTemplate;
    }

    private Set<String> getCategoryWhichDoesNotSelfRelation(List<Row> rowList,List<String> headers) {
        if (isNotEmpty(rowList)){
            Map<String,Boolean> categoryRelationMap = Maps.newLinkedHashMap();
            for (Row currentRow : rowList){
                Cell categoryCell = currentRow.getCell(headers.indexOf(GLInsuredExcelHeader.CATEGORY.getDescription()));
                String categoryCellValue = getCellValue(categoryCell);
                Cell relationCell = currentRow.getCell(headers.indexOf(GLInsuredExcelHeader.RELATIONSHIP.getDescription()));
                if (categoryRelationMap.get(categoryCellValue)==null) {
                    categoryRelationMap.put(categoryCellValue, Boolean.TRUE);
                }
                String relationCellValue = getCellValue(relationCell);
                if (Relationship.SELF.description.equals(relationCellValue)) {
                    categoryRelationMap.put(categoryCellValue,Boolean.FALSE);
                }
            }
            Set<String> category = Sets.newLinkedHashSet();
            for (Map.Entry<String, Boolean> categoryMap : categoryRelationMap.entrySet()){
                if (categoryMap.getValue().equals(Boolean.TRUE)){
                    category.add(categoryMap.getKey());
                }
            }
            return category;
        }
        return Collections.EMPTY_SET;
    }

    private List<Row> findDuplicateRow(List<Row> dataRowsForDuplicateCheck, Row currentRow, List<String> headers) {
        List<Row> duplicateRows = Lists.newArrayList();
        Cell firstNameCell = currentRow.getCell(headers.indexOf(GLInsuredExcelHeader.FIRST_NAME.name()));
        String firstNameCellValue = getCellValue(firstNameCell);
        Cell lastNameCell = currentRow.getCell(headers.indexOf(GLInsuredExcelHeader.LAST_NAME.name()));
        String lastNameCellValue = getCellValue(lastNameCell);
        Cell dateOfBirthCell = currentRow.getCell(headers.indexOf(GLInsuredExcelHeader.DATE_OF_BIRTH.name()));
        String dateOfBirthCellValue = getCellValue(dateOfBirthCell);
        NameRelationshipCellValueHolder currentRowNameRelationshipHolder = new NameRelationshipCellValueHolder(firstNameCellValue, lastNameCellValue, dateOfBirthCellValue);
        dataRowsForDuplicateCheck.forEach(dataRowForDuplicateCheck -> {
            if (currentRow.getRowNum() != dataRowForDuplicateCheck.getRowNum()) {
                Cell otherRowFirstNameCell = dataRowForDuplicateCheck.getCell(headers.indexOf(GLInsuredExcelHeader.FIRST_NAME.name()));
                String otherRowFirstNameCellValue = getCellValue(otherRowFirstNameCell);
                Cell otherRowLastNameCell = dataRowForDuplicateCheck.getCell(headers.indexOf(GLInsuredExcelHeader.LAST_NAME.name()));
                String otherRowLastNameCellValue = getCellValue(otherRowLastNameCell);
                Cell otherRowDateOfBirthCell = dataRowForDuplicateCheck.getCell(headers.indexOf(GLInsuredExcelHeader.DATE_OF_BIRTH.name()));
                String otherRowDateOfBirthCellValue = getCellValue(otherRowDateOfBirthCell);
                NameRelationshipCellValueHolder otherRowNameRelationshipHolder = new NameRelationshipCellValueHolder(otherRowFirstNameCellValue, otherRowLastNameCellValue, otherRowDateOfBirthCellValue);
                if (currentRowNameRelationshipHolder.equals(otherRowNameRelationshipHolder)) {
                    duplicateRows.add(dataRowForDuplicateCheck);
                }
            }
        });
        return duplicateRows;
    }

    private class NameRelationshipCellValueHolder {
        private String firstName;
        private String lastName;
        private String dateOfBirth;

        public NameRelationshipCellValueHolder(String firstName, String lastName, String dateOfBirth) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.dateOfBirth = dateOfBirth;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            NameRelationshipCellValueHolder that = (NameRelationshipCellValueHolder) o;

            if (isEmpty(this.dateOfBirth) || isEmpty(that.dateOfBirth)) {
                return false;
            }
            if (isEmpty(this.firstName) || isEmpty(that.firstName)) {
                return false;
            }
            if (isEmpty(this.lastName) || isEmpty(that.lastName)) {
                return false;
            }
            if (dateOfBirth != null ? !dateOfBirth.equals(that.dateOfBirth) : that.dateOfBirth != null) return false;
            if (firstName != null ? !firstName.equalsIgnoreCase(that.firstName) : that.firstName != null) return false;
            if (lastName != null ? !lastName.equalsIgnoreCase(that.lastName) : that.lastName != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return dateOfBirth != null ? dateOfBirth.hashCode() : 0;
        }
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

    private String validateOptionalCoverageCell(List<String> headers, Row row, List<OptionalCoverageCellHolder> optionalCoverageCellHolders) {
        List<String> securityCover = Lists.newArrayList("Cash & Security Optional Covers 1",
                "Cash & Security Optional Covers 2", "Cash & Security Optional Covers 3",
                "Cash & Security Optional Covers 4");
        List<String> securityCoverGiven = Lists.newArrayList();

        Cell planCell = row.getCell(headers.indexOf(GLInsuredExcelHeader.PLAN.getDescription()));
        Cell noOfAssuredCell = row.getCell(headers.indexOf(GLInsuredExcelHeader.NO_OF_ASSURED.getDescription()));
        String planCode = getCellValue(planCell);
        String noOfAssuredCellValue = getCellValue(noOfAssuredCell);
        if (planCode.indexOf(".") != -1) {
            planCode = planCode.substring(0, planCode.indexOf("."));
        }
        Set<String> errorMessages = Sets.newHashSet();
        final String finalPlanCode = planCode;
        optionalCoverageCellHolders.forEach(optionalCoverageCellHolder -> {
            String optionalCoverageCode = getCellValue(optionalCoverageCellHolder.getOptionalCoverageCell());
            if (optionalCoverageCode.indexOf(".") != -1) {
                optionalCoverageCode = optionalCoverageCode.substring(0, optionalCoverageCode.indexOf("."));
            }
            if (isNotEmpty(optionalCoverageCode) && isValidCoverage(finalPlanCode, optionalCoverageCode)){
                String securityOptionalCover = glQuotationFinder.findCoverageNameByCoverageCode(optionalCoverageCode);
                if (securityCover.contains(securityOptionalCover)){
                    securityCoverGiven.add(securityOptionalCover);
                }
                if (securityCoverGiven.size()>1){
                    errorMessages.add(" Multiple security optional covers cannot be given for " + finalPlanCode + ".");
                }
            }
            if (isNotEmpty(optionalCoverageCode) && !isValidCoverage(finalPlanCode, optionalCoverageCode)) {
                errorMessages.add("Coverage code: " + optionalCoverageCode + "  is not valid for plan " + finalPlanCode + ".");
            }
            String coverageSA = getCellValue(optionalCoverageCellHolder.getOptionalCoverageSACell());
            if (isEmpty(coverageSA)) {
                errorMessages.add("Sum Assured is empty for" + optionalCoverageCode + ".");
            }
            if (isNotEmpty(coverageSA) && Double.valueOf(coverageSA) < 0) {
                errorMessages.add("Sum Assured cannot be negative for optional coverage :" + optionalCoverageCode + ".");
            }
            String optionalCoveragePremiumCellValue = getCellValue(optionalCoverageCellHolder.getOptionalCoveragePremiumCell());
            if (isNotEmpty(noOfAssuredCellValue) && isEmpty(optionalCoveragePremiumCellValue)) {
                errorMessages.add("Premium cannot be empty for" + optionalCoverageCode + ".");
            }
            try {
                BigDecimal coverageSumAssured = BigDecimal.valueOf(Double.valueOf(coverageSA).intValue());
                if (!planAdapter.isValidPlanCoverageSumAssured(finalPlanCode, optionalCoverageCode, coverageSumAssured)) {
                    errorMessages.add(coverageSA + "  is not valid Sum Assured for coverage " + optionalCoverageCode + ".");
                }
            } catch (Exception e) {
                errorMessages.add(coverageSA + "  is not numeric.");
            }

        });
        return buildErrorMessage(errorMessages);
    }

    private List<OptionalCoverageCellHolder> findNonEmptyOptionalCoverageCell(List<String> headers, Row row, List<PlanId> planIds) {
        List<OptionalCoverageCellHolder> optionalCoverageCellHolders = Lists.newArrayList();
        List<PlanCoverageDetailDto> planCoverageDetailDtoList = planAdapter.getPlanAndCoverageDetail(planIds);
        int count = 1;
        Cell dateOfBirthCell = row.getCell(headers.indexOf(GHInsuredExcelHeader.DATE_OF_BIRTH.getDescription()));
        String dateOfBirthCellValue = getCellValue(dateOfBirthCell);
        int age = isNotEmpty(dateOfBirthCellValue) ? AppUtils.getAgeOnNextBirthDate(LocalDate.parse(dateOfBirthCellValue, DateTimeFormat.forPattern(AppConstants.DD_MM_YYY_FORMAT))) : 0;
        Cell planCell = row.getCell(headers.indexOf(GHInsuredExcelHeader.PLAN.getDescription()));
        String plan = String.valueOf(new BigDecimal(getCellValue(planCell)).intValue());
        for (PlanCoverageDetailDto planCoverageDetailDto : planCoverageDetailDtoList) {
            for (PlanCoverageDetailDto.CoverageDto coverageDto : planCoverageDetailDto.getCoverageDtoList()) {
                String coverageHeader = (AppConstants.OPTIONAL_COVERAGE_HEADER + count);
                int cellNumber = headers.indexOf(coverageHeader);
                Cell optionalCoverageCell = row.getCell(cellNumber);
                int optionalCoverageSACellNumber = headers.indexOf(coverageHeader + " " + AppConstants.OPTIONAL_COVERAGE_SA_HEADER);
                int optionalCoveragePremiumCellNumber = headers.indexOf(coverageHeader + " " + AppConstants.PREMIUM_CELL_HEADER_NAME);
                Cell optionalCoveragePremiumCell = row.getCell(optionalCoveragePremiumCellNumber);
                Cell optionalCoverageSACell = row.getCell(optionalCoverageSACellNumber);
                OptionalCoverageCellHolder optionalCoverageCellHolder = null;
                if (isNotEmpty(getCellValue(optionalCoverageCell))) {
                    optionalCoverageCellHolder = new OptionalCoverageCellHolder(optionalCoverageCell, optionalCoverageSACell, optionalCoveragePremiumCell);
                    if(isNotEmpty(dateOfBirthCellValue)){
                        String coverageCode = String.valueOf(new BigDecimal(getCellValue(optionalCoverageCell)).intValue());
                        if (planAdapter.isValidCoverageAgeForGivenCoverageCode(plan, coverageCode, age))
                            optionalCoverageCellHolders.add(optionalCoverageCellHolder);
                    } else {
                        optionalCoverageCellHolders.add(optionalCoverageCellHolder);
                    }
                }
                count++;
            }
        }
        return optionalCoverageCellHolders;
    }

    private String validateRow(Row insureDataRow, List<String> headers, List<PlanId> agentPlans) {
        String errorMessage = "";
        Set<String> errorMessages = Sets.newHashSet();
        String planCellValue = getCellValue(insureDataRow.getCell(headers.indexOf(GLInsuredExcelHeader.PLAN.name())));
        if (isNotEmpty(planCellValue)) {
            if (planCellValue.indexOf(".") != -1) {
                planCellValue = planCellValue.substring(0, planCellValue.indexOf("."));
            }
            PlanId planId = planAdapter.getPlanId(planCellValue);
            if (!agentPlans.contains(planId)) {
                errorMessages.add("Plan code is not valid for the selected agent.");
            }
            boolean isPlanLaunched = planAdapter.isPlanActive(planCellValue);
            if (!isPlanLaunched) {
                errorMessages.add("This plan cannot be quoted as it has not been launched yet.");
            }
        }
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

    private boolean isFileBlank(List<Row> dataRows) {
        Optional<Row> rowOptional = dataRows.parallelStream().filter(new Predicate<Row>() {
            @Override
            public boolean test(Row row) {
                return isRowEmpty(row);
            }
        }).findAny();
        return !rowOptional.isPresent();
    }

    public static boolean isRowEmpty(Row row) {
        List<Cell> cells = Lists.newArrayList(row.cellIterator());
        Optional<Cell> cellOptional = cells.parallelStream().filter(new Predicate<Cell>() {
            @Override
            public boolean test(Cell cell) {
                return (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK);
            }
        }).findAny();
        return cellOptional.isPresent();
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

    @Getter
    public class OptionalCoverageCellHolder {

        private Cell optionalCoverageCell;

        private Cell optionalCoverageSACell;

        private Cell optionalCoveragePremiumCell;


        public OptionalCoverageCellHolder(Cell optionalCoverageCell, Cell optionalCoverageSACell, Cell optionalCoveragePremiumCell) {
            this.optionalCoverageCell = optionalCoverageCell;
            this.optionalCoverageSACell = optionalCoverageSACell;
            this.optionalCoveragePremiumCell = optionalCoveragePremiumCell;
        }

    }

    private void checkAndValidateIsSameForAllCategory(Map<Row, List<Row>> relationshipGroupRowMap, List<String> headers,List<PlanId> agentPlans) {
        for (Map.Entry<Row, List<Row>> rowEntry : relationshipGroupRowMap.entrySet()) {
            Row selfRow = rowEntry.getKey();
            List<OptionalCoverageCellHolder> optionalCoverageCellHolders = findNonEmptyOptionalCoverageCell(headers, selfRow, agentPlans);
            List<Row> dependentRows = rowEntry.getValue();
            GLRelationCategoryCoverDetail relationshipCoverDetail = populateRelationCategoryCover(selfRow, headers, optionalCoverageCellHolders,true);
            for (Map.Entry<Row, List<Row>> otherRowEntry : relationshipGroupRowMap.entrySet()) {
                Row otherRow = otherRowEntry.getKey();
                List<Row> otherDependentRows = otherRowEntry.getValue();
                for (Row dependentRow : dependentRows){
                    List<OptionalCoverageCellHolder> dependentOptionalCoverageCellHolders = findNonEmptyOptionalCoverageCell(headers, dependentRow, agentPlans);
                    GLRelationCategoryCoverDetail dependentRelationshipCoverDetail = populateRelationCategoryCover(dependentRow, headers, dependentOptionalCoverageCellHolders,true);
                    for (Row otherDependentRow : otherDependentRows) {
                        List<OptionalCoverageCellHolder> otherDependentOptionalCoverageCellHolders = findNonEmptyOptionalCoverageCell(headers, otherDependentRow, agentPlans);
                        GLRelationCategoryCoverDetail otherDependentRelationshipCoverDetail = populateRelationCategoryCover(otherDependentRow, headers, otherDependentOptionalCoverageCellHolders, true);
                        if (dependentRelationshipCoverDetail.getRelationship().equals(otherDependentRelationshipCoverDetail.getRelationship())) {
                            if (!dependentRelationshipCoverDetail.equals(otherDependentRelationshipCoverDetail)) {
                                raiseNotSamePlanForAllCategoryException(otherDependentRelationshipCoverDetail.getRelationship());
                            }
                        }
                    }
                }
                List<OptionalCoverageCellHolder> otherOptionalCoverageCellHolders = findNonEmptyOptionalCoverageCell(headers, otherRow, agentPlans);
                GLRelationCategoryCoverDetail otherRelationshipCoverDetail = populateRelationCategoryCover(otherRow, headers, otherOptionalCoverageCellHolders,true);
                if (relationshipCoverDetail.getRelationship().equals(otherRelationshipCoverDetail.getRelationship())) {
                    if (!relationshipCoverDetail.equals(otherRelationshipCoverDetail)) {
                        raiseNotSamePlanForAllCategoryException(otherRelationshipCoverDetail.getRelationship());
                    }
                }
            }
        }
    }


    private void checkAndValidateIsSameCoverForAllRelation(List<Row> relationshipGroupRowMap, List<String> headers,List<PlanId> agentPlans) {
        for (Row current : relationshipGroupRowMap){
            List<OptionalCoverageCellHolder> optionalCoverageCellHolders = findNonEmptyOptionalCoverageCell(headers, current, agentPlans);
            GLRelationCategoryCoverDetail categoryCoverDetail = populateRelationCategoryCover(current, headers, optionalCoverageCellHolders, false);
            for (Row otherRow : relationshipGroupRowMap){
                List<OptionalCoverageCellHolder> otherOptionalCoverageCellHolders = findNonEmptyOptionalCoverageCell(headers, otherRow, agentPlans);
                GLRelationCategoryCoverDetail otherCategoryCoverDetail = populateRelationCategoryCover(otherRow, headers, otherOptionalCoverageCellHolders, false);
                if (categoryCoverDetail.getRelationship().equals(otherCategoryCoverDetail.getRelationship())) {
                    if (!categoryCoverDetail.equals(otherCategoryCoverDetail)) {
                        raiseNotSamePlanForAllRelationshipException(otherCategoryCoverDetail.getRelationship());
                    }
                }
            }
        }
    }

    private void checkAndValidateIsSameCoverForAllRelationAndCategory(List<Row> relationshipGroupRowMap, List<String> headers,List<PlanId> agentPlans) {
        for (Row current : relationshipGroupRowMap){
            List<OptionalCoverageCellHolder> optionalCoverageCellHolders = findNonEmptyOptionalCoverageCell(headers, current, agentPlans);
            GLRelationCategoryCoverDetail categoryCoverDetail = populateRelationCategoryCover(current, headers, optionalCoverageCellHolders, false);
            for (Row otherRow : relationshipGroupRowMap){
                List<OptionalCoverageCellHolder> otherOptionalCoverageCellHolders = findNonEmptyOptionalCoverageCell(headers, otherRow, agentPlans);
                GLRelationCategoryCoverDetail otherCategoryCoverDetail = populateRelationCategoryCover(otherRow, headers, otherOptionalCoverageCellHolders, false);
                if (!categoryCoverDetail.compare(otherCategoryCoverDetail)) {
                    raiseNotSamePlanForAllCategoryAndRelationshipException();
                }
            }
            break;
        }
    }

    private void checkAndValidateIsSameCoverForAllRelationAndCategoryCombination(List<Row> relationshipGroupRowMap, List<String> headers,List<PlanId> agentPlans) {
        for (Row current : relationshipGroupRowMap){
            List<OptionalCoverageCellHolder> optionalCoverageCellHolders = findNonEmptyOptionalCoverageCell(headers, current, agentPlans);
            GLRelationCategoryCoverDetail categoryCoverDetail = populateRelationCategoryCover(current, headers, optionalCoverageCellHolders, false);
            String category = getCellValueByType(GLInsuredExcelHeader.RELATIONSHIP.getDescription(), current, headers);
            categoryCoverDetail.setCategory(category);
            for (Row otherRow : relationshipGroupRowMap){
                List<OptionalCoverageCellHolder> otherOptionalCoverageCellHolders = findNonEmptyOptionalCoverageCell(headers, otherRow, agentPlans);
                GLRelationCategoryCoverDetail otherCategoryCoverDetail = populateRelationCategoryCover(otherRow, headers, otherOptionalCoverageCellHolders, false);
                String otherCategory = getCellValueByType(GLInsuredExcelHeader.RELATIONSHIP.getDescription(), otherRow, headers);
                otherCategoryCoverDetail.setCategory(otherCategory);
                if (categoryCoverDetail.getRelationship().equals(otherCategoryCoverDetail.getRelationship()) && categoryCoverDetail.getCategory().equals(otherCategoryCoverDetail.getCategory()))
                    if (!categoryCoverDetail.equals(otherCategoryCoverDetail)) {
                        raiseNotSameCoverForAllCategoryAndRelationshipCombinationException();
                    }
            }
        }
    }


    private GLRelationCategoryCoverDetail populateRelationCategoryCover(Row row, List<String> headers, List<OptionalCoverageCellHolder> optionalCoverageCellHolders, Boolean isRelationIsTheDiff){
        String planCode = getCellValueByType(GLInsuredExcelHeader.PLAN.getDescription(), row, headers);
        String planSA = getCellValueByType(GLInsuredExcelHeader.SUM_ASSURED.getDescription(), row, headers);
        BigDecimal sumAssured = isNotEmpty(planSA)?new BigDecimal(planSA):BigDecimal.ZERO;
        String relation = isRelationIsTheDiff?getCellValueByType(GLInsuredExcelHeader.RELATIONSHIP.getDescription(), row, headers): getCellValueByType(GLInsuredExcelHeader.CATEGORY.getDescription(), row, headers);
        String premiumType = getCellValueByType(GLInsuredExcelHeader.PREMIUM_TYPE.getDescription(), row, headers);
        String multiplier = getCellValueByType(GLInsuredExcelHeader.INCOME_MULTIPLIER.getDescription(), row, headers);
        BigDecimal incomeMultiplier = isNotEmpty(multiplier)?new BigDecimal(multiplier):BigDecimal.ZERO;
        GLRelationCategoryCoverDetail glRelationCategoryCoverDetail =  new GLRelationCategoryCoverDetail().withRelationDetail(relation,planCode,sumAssured,premiumType,incomeMultiplier);
        glRelationCategoryCoverDetail = glRelationCategoryCoverDetail.withCoverageDetail(optionalCoverageCellHolders);
        return glRelationCategoryCoverDetail;
    }

    private String getCellValueByType(String header,Row otherRow ,List<String> headers){
        Cell otherPlanCell = getCellByName(otherRow, headers, header);
        return getCellValue(otherPlanCell);
    }

}
