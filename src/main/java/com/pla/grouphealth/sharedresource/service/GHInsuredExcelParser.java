package com.pla.grouphealth.sharedresource.service;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.pla.grouphealth.quotation.query.GHQuotationFinder;
import com.pla.grouphealth.sharedresource.dto.GHInsuredDto;
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
import org.nthdimenzion.common.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.pla.grouphealth.quotation.application.service.exception.GLInsuredTemplateExcelParseException.*;
import static com.pla.grouphealth.sharedresource.service.QuotationProposalUtilityService.*;
import static com.pla.sharedkernel.util.ExcelGeneratorUtil.getCellValue;
import static org.nthdimenzion.utils.UtilValidator.isEmpty;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Created by Samir on 5/1/2015.
 */
@Component(value = "ghInsuredExcelParser")
public class GHInsuredExcelParser {

    private IPlanAdapter planAdapter;

    private GHQuotationFinder ghQuotationFinder;


    @Autowired
    public GHInsuredExcelParser(IPlanAdapter planAdapter, GHQuotationFinder ghQuotationFinder) {
        this.planAdapter = planAdapter;
        this.ghQuotationFinder = ghQuotationFinder;
    }


    public List<GHInsuredDto> transformToInsuredDto(HSSFWorkbook hssfWorkbook, List<PlanId> agentPlans) {
        HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(0);
        Iterator<Row> rowIterator = hssfSheet.rowIterator();
        Row headerRow = rowIterator.next();
        final List<String> headers = GHInsuredExcelHeader.getAllowedHeaderForParser(planAdapter, agentPlans);
        final List<String> excelHeaders = getHeaders(headerRow);
        Map<Row, List<Row>> insuredTemplateDataRowMap = groupByRelationship(Lists.newArrayList(rowIterator), GHInsuredExcelHeader.getAllowedHeaders(planAdapter, agentPlans));
        List<GHInsuredDto> insuredDtoList = insuredTemplateDataRowMap.entrySet().stream().map(new Function<Map.Entry<Row, List<Row>>, GHInsuredDto>() {
            @Override
            public GHInsuredDto apply(Map.Entry<Row, List<Row>> rowListEntry) {
                Row insuredRow = rowListEntry.getKey();
                List<Row> dependentRows = rowListEntry.getValue();
                GHInsuredDto insuredDto = createInsuredDto(insuredRow, excelHeaders, headers, findNonEmptyOptionalCoverageCell(excelHeaders, insuredRow, agentPlans));
                insuredDto.getPlanPremiumDetail().setPlanId(planAdapter.getPlanId(insuredDto.getPlanPremiumDetail().getPlanCode()).getPlanId());
                Set<GHInsuredDto.GHInsuredDependentDto> insuredDependentDtoSet = dependentRows.stream().map(new Function<Row, GHInsuredDto.GHInsuredDependentDto>() {
                    @Override
                    public GHInsuredDto.GHInsuredDependentDto apply(Row row) {
                        GHInsuredDto.GHInsuredDependentDto insuredDependentDto = createInsuredDependentDto(row, excelHeaders, headers, findNonEmptyOptionalCoverageCell(excelHeaders, row, agentPlans));
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


    private GHInsuredDto.GHInsuredDependentDto createInsuredDependentDto(Row dependentRow, List<String> excelHeaders, List<String> headers, List<OptionalCoverageCellHolder> optionalCoverageCells) {
        GHInsuredDto.GHInsuredDependentDto insuredDependentDto = new GHInsuredDto.GHInsuredDependentDto();
        for (String header : headers) {
            if (!header.contains(AppConstants.OPTIONAL_COVERAGE_HEADER)) {
                insuredDependentDto = GHInsuredExcelHeader.valueOf(header).populateInsuredDependentDetail(insuredDependentDto, dependentRow, excelHeaders);
            }
        }
        final GHInsuredDto.GHInsuredDependentDto finalInsuredDependentDto = insuredDependentDto;
        List<GHInsuredDto.GHCoveragePremiumDetailDto> coveragePremiumDetails = optionalCoverageCells.stream().map(new Function<OptionalCoverageCellHolder, GHInsuredDto.GHCoveragePremiumDetailDto>() {
            @Override
            public GHInsuredDto.GHCoveragePremiumDetailDto apply(OptionalCoverageCellHolder optionalCoverageCellHolder) {
                GHInsuredDto.GHCoveragePremiumDetailDto coveragePremiumDetailDto = new GHInsuredDto.GHCoveragePremiumDetailDto();
                coveragePremiumDetailDto.setCoverageCode(ExcelGeneratorUtil.getCellValue(optionalCoverageCellHolder.getOptionalCoverageCell()));
                String coverageCode = coveragePremiumDetailDto.getCoverageCode();
                if (coverageCode.indexOf(".") != -1) {
                    coverageCode = coverageCode.substring(0, coverageCode.indexOf("."));
                }
                coveragePremiumDetailDto.setCoverageCode(coverageCode);
                if (isNotEmpty(coveragePremiumDetailDto.getCoverageCode())) {
                    Map<String, Object> coverageMap = ghQuotationFinder.findCoverageDetailByCoverageCode(coveragePremiumDetailDto.getCoverageCode());
                    coveragePremiumDetailDto.setCoverageId((String) coverageMap.get("coverageId"));
                }
                coveragePremiumDetailDto.setPremiumVisibility(ExcelGeneratorUtil.getCellValue(optionalCoverageCellHolder.getOptionalCoverageVisibilityCell()));
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
                if (isNotEmpty(optionalCoverageCellHolder.getBenefitCellHolders())) {
                    for (OptionalCoverageBenefitCellHolder optionalCoverageBenefitCellHolder : optionalCoverageCellHolder.getBenefitCellHolders()) {
                        GHInsuredDto.GHCoveragePremiumDetailDto.GHCoverageBenefitDetailDto ghCoverageBenefitDetailDto = new GHInsuredDto.GHCoveragePremiumDetailDto.GHCoverageBenefitDetailDto();
                        ghCoverageBenefitDetailDto.setBenefitCode(ExcelGeneratorUtil.getCellValue(optionalCoverageBenefitCellHolder.getBenefitCell()));
                        int benefitLimit = Double.valueOf(ExcelGeneratorUtil.getCellValue(optionalCoverageBenefitCellHolder.getBenefitLimitCell())).intValue();
                        ghCoverageBenefitDetailDto.setBenefitLimit(BigDecimal.valueOf(benefitLimit));
                        coveragePremiumDetailDto = coveragePremiumDetailDto.addBenefit(ghCoverageBenefitDetailDto);
                    }
                }
                return coveragePremiumDetailDto;
            }
        }).collect(Collectors.toList());
        insuredDependentDto = insuredDependentDto.addCoveragePremiumDetails(coveragePremiumDetails);
        return insuredDependentDto;
    }

    private GHInsuredDto createInsuredDto(Row row, List<String> excelHeaders, List<String> headers, List<OptionalCoverageCellHolder> optionalCoverageCells) {
        GHInsuredDto insuredDto = new GHInsuredDto();
        for (String header : headers) {
            if (!header.contains(AppConstants.OPTIONAL_COVERAGE_HEADER)) {
                insuredDto = GHInsuredExcelHeader.valueOf(header).populateInsuredDetail(insuredDto, row, excelHeaders);
            }
        }
        final GHInsuredDto finalInsuredDto = insuredDto;
        List<GHInsuredDto.GHCoveragePremiumDetailDto> coveragePremiumDetails = optionalCoverageCells.stream().map(new Function<OptionalCoverageCellHolder, GHInsuredDto.GHCoveragePremiumDetailDto>() {
            @Override
            public GHInsuredDto.GHCoveragePremiumDetailDto apply(OptionalCoverageCellHolder optionalCoverageCellHolder) {
                GHInsuredDto.GHCoveragePremiumDetailDto coveragePremiumDetailDto = new GHInsuredDto.GHCoveragePremiumDetailDto();
                coveragePremiumDetailDto.setCoverageCode(ExcelGeneratorUtil.getCellValue(optionalCoverageCellHolder.getOptionalCoverageCell()));
                if (isNotEmpty(coveragePremiumDetailDto.getCoverageCode())) {
                    String coverageCode = coveragePremiumDetailDto.getCoverageCode();
                    if (coverageCode.indexOf(".") != -1) {
                        coverageCode = coverageCode.substring(0, coverageCode.indexOf("."));
                    }
                    coveragePremiumDetailDto.setCoverageCode(coverageCode);
                    Map<String, Object> coverageMap = ghQuotationFinder.findCoverageDetailByCoverageCode(coveragePremiumDetailDto.getCoverageCode());
                    coveragePremiumDetailDto.setCoverageId((String) coverageMap.get("coverageId"));
                }
                coveragePremiumDetailDto.setPremiumVisibility(ExcelGeneratorUtil.getCellValue(optionalCoverageCellHolder.getOptionalCoverageVisibilityCell()));
                int coverageSA = Double.valueOf(ExcelGeneratorUtil.getCellValue(optionalCoverageCellHolder.getOptionalCoverageSACell())).intValue();
                coveragePremiumDetailDto.setSumAssured(BigDecimal.valueOf(coverageSA));
                String optionalCoveragePremiumCellValue = ExcelGeneratorUtil.getCellValue(optionalCoverageCellHolder.getOptionalCoveragePremiumCell());
                BigDecimal coveragePremium = null;
                if (isNotEmpty(optionalCoveragePremiumCellValue) && finalInsuredDto.getNoOfAssured() != null) {
                    coveragePremium = BigDecimal.valueOf(Double.valueOf(optionalCoveragePremiumCellValue)).multiply(BigDecimal.valueOf(Double.valueOf(finalInsuredDto.getNoOfAssured())));
                }
                if (isNotEmpty(optionalCoveragePremiumCellValue) && finalInsuredDto.getFirstName() != null && finalInsuredDto.getDateOfBirth() != null) {
                    coveragePremium = BigDecimal.valueOf(Double.valueOf(optionalCoveragePremiumCellValue)).multiply(new BigDecimal(1));
                }
                coveragePremiumDetailDto.setPremium(coveragePremium);
                if (optionalCoverageCellHolder.getBenefitCellHolders() != null) {
                    for (OptionalCoverageBenefitCellHolder optionalCoverageBenefitCellHolder : optionalCoverageCellHolder.getBenefitCellHolders()) {
                        GHInsuredDto.GHCoveragePremiumDetailDto.GHCoverageBenefitDetailDto ghCoverageBenefitDetailDto = new GHInsuredDto.GHCoveragePremiumDetailDto.GHCoverageBenefitDetailDto();
                        ghCoverageBenefitDetailDto.setBenefitCode(ExcelGeneratorUtil.getCellValue(optionalCoverageBenefitCellHolder.getBenefitCell()));
                        int benefitLimit = Double.valueOf(ExcelGeneratorUtil.getCellValue(optionalCoverageBenefitCellHolder.getBenefitLimitCell())).intValue();
                        ghCoverageBenefitDetailDto.setBenefitLimit(BigDecimal.valueOf(benefitLimit));
                        coveragePremiumDetailDto = coveragePremiumDetailDto.addBenefit(ghCoverageBenefitDetailDto);
                    }
                }
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
        if(isEmpty(dataRows)){
            raiseAssuredDataNotSharedException();
        }
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
        boolean isSamePlanForAllCategory = Boolean.TRUE;
        boolean isSamePlanForAllRelationship = Boolean.TRUE;
        boolean isSamePlanForAllRelationshipCategory = Boolean.TRUE;
        if(samePlanForAllCategory && !samePlanForAllRelation)
            isSamePlanForAllCategory = isSamePlanForAllCategory(insuredDependentMap, headers);
        if(samePlanForAllRelation && !samePlanForAllCategory)
            isSamePlanForAllRelationship = isSamePlanForAllRelation(insuredDependentMap, headers);
        if(samePlanForAllCategory && samePlanForAllRelation)
            isSamePlanForAllRelationshipCategory = isSamePlanForAllRelationshipCategory(insuredDependentMap, headers);
        if (!isSamePlanForAllCategory) {
            raiseNotSamePlanForAllCategoryException();
        } else if (!isSamePlanForAllRelationship) {
            raiseNotSamePlanForAllRelationshipException();
        } else if (!isSamePlanForAllRelationshipCategory) {
            raiseNotSamePlanForAllCategoryAndRelationshipException();
        }
        if(checkIfSameOptionalCoverage(insuredDependentMap, headers)){
            raiseSameOptionalCoverageException();
        }
        Cell errorMessageHeaderCell = null;
        Iterator<Row> dataRowIterator = dataRows.iterator();
        while (dataRowIterator.hasNext()) {
            Row currentRow = dataRowIterator.next();
            List<String> excelHeaders = GHInsuredExcelHeader.getAllowedHeaderForParser(planAdapter, agentPlans);
            String errorMessage = validateRow(currentRow, excelHeaders, agentPlans);
            List<OptionalCoverageCellHolder> optionalCoverageCellHolders = findNonEmptyOptionalCoverageCell(headers, currentRow, agentPlans);
            String coverageErrorMessage = validateOptionalCoverageCell(headers, currentRow, optionalCoverageCellHolders);
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
            if (isEmpty(errorMessage) && isEmpty(coverageErrorMessage) && isEmpty(duplicateRowErrorMessage)) {
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
            errorMessage = errorMessage + "\n" + duplicateRowErrorMessage;
            Cell errorMessageCell = currentRow.createCell(headers.size());
            errorMessageCell.setCellValue(errorMessage);
        }
        return isValidTemplate;
    }

    private List<Row> findDuplicateRow(List<Row> dataRowsForDuplicateCheck, Row currentRow, List<String> headers) {
        List<Row> duplicateRows = Lists.newArrayList();
        Cell firstNameCell = currentRow.getCell(headers.indexOf(GHInsuredExcelHeader.FIRST_NAME.name()));
        String firstNameCellValue = getCellValue(firstNameCell);
        Cell lastNameCell = currentRow.getCell(headers.indexOf(GHInsuredExcelHeader.LAST_NAME.name()));
        String lastNameCellValue = getCellValue(lastNameCell);
        Cell dateOfBirthCell = currentRow.getCell(headers.indexOf(GHInsuredExcelHeader.DATE_OF_BIRTH.name()));
        String dateOfBirthCellValue = getCellValue(dateOfBirthCell);
        NameRelationshipCellValueHolder currentRowNameRelationshipHolder = new NameRelationshipCellValueHolder(firstNameCellValue, lastNameCellValue, dateOfBirthCellValue);
        dataRowsForDuplicateCheck.forEach(dataRowForDuplicateCheck -> {
            if (currentRow.getRowNum() != dataRowForDuplicateCheck.getRowNum()) {
                Cell otherRowFirstNameCell = dataRowForDuplicateCheck.getCell(headers.indexOf(GHInsuredExcelHeader.FIRST_NAME.name()));
                String otherRowFirstNameCellValue = getCellValue(otherRowFirstNameCell);
                Cell otherRowLastNameCell = dataRowForDuplicateCheck.getCell(headers.indexOf(GHInsuredExcelHeader.LAST_NAME.name()));
                String otherRowLastNameCellValue = getCellValue(otherRowLastNameCell);
                Cell otherRowDateOfBirthCell = dataRowForDuplicateCheck.getCell(headers.indexOf(GHInsuredExcelHeader.DATE_OF_BIRTH.name()));
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
        List<String> allowedHeaders = GHInsuredExcelHeader.getAllowedHeaders(planAdapter, planIds);
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
        Cell planCell = row.getCell(headers.indexOf(GHInsuredExcelHeader.PLAN.getDescription()));
        Cell noOfAssuredCell = row.getCell(headers.indexOf(GHInsuredExcelHeader.NO_OF_ASSURED.getDescription()));
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
                String securityOptionalCover = ghQuotationFinder.getCoverageNameByCode(optionalCoverageCode);
                if (securityCover.contains(securityOptionalCover)){
                    securityCoverGiven.add(securityOptionalCover);
                }
                if (securityCoverGiven.size()>1){
                    errorMessages.add(" Multiple security optional covers cannot be given for " + finalPlanCode + ".");
                }
            }
            if (isNotEmpty(optionalCoverageCode) && !isValidCoverage(finalPlanCode, optionalCoverageCode)) {
                errorMessages.add(optionalCoverageCode + "  is not valid for plan " + finalPlanCode + ".");
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
            String premiumVisibility = getCellValue(optionalCoverageCellHolder.getOptionalCoverageVisibilityCell());
            if (isNotEmpty(premiumVisibility) && !("NO".equals(premiumVisibility) || "YES".equals(premiumVisibility))) {
                errorMessages.add("Premium visibility for optional coverage " + optionalCoverageCode + " should be YES/NO.");
            }
            try {
                BigDecimal coverageSumAssured = BigDecimal.valueOf(Double.valueOf(coverageSA).intValue());
                if (!planAdapter.isValidPlanCoverageSumAssured(finalPlanCode, optionalCoverageCode, coverageSumAssured)) {
                    errorMessages.add(coverageSA + "  is not valid Sum Assured for coverage " + optionalCoverageCode + ".");
                }
                if (optionalCoverageCellHolder.getBenefitCellHolders() != null) {
                    for (OptionalCoverageBenefitCellHolder optionalCoverageBenefitCellHolder : optionalCoverageCellHolder.getBenefitCellHolders()) {
                        String benefitCode = getCellValue(optionalCoverageBenefitCellHolder.getBenefitCell());
                        if (benefitCode.indexOf(".") != -1) {
                            benefitCode = benefitCode.substring(0, benefitCode.indexOf("."));
                        }
                        if (!planAdapter.isValidPlanCoverageBenefit(finalPlanCode, optionalCoverageCode, benefitCode)) {
                            errorMessages.add(benefitCode + "  is not valid Benefit Code for coverage " + optionalCoverageCode + ".");
                        }
                        String benefitLimit = getCellValue(optionalCoverageBenefitCellHolder.getBenefitLimitCell());
                        if (isEmpty(benefitLimit)) {
                            errorMessages.add("Benefit Limit is empty for benefit" + benefitCode + ".");
                        }
                        if (isNotEmpty(benefitLimit) && Double.valueOf(benefitLimit) < 0) {
                            errorMessages.add("Benefit limit cannot be negative for benefit code :" + benefitCode + ".");
                        }
                        try {
                            BigDecimal benefitLimitBigDecimal = BigDecimal.valueOf(Double.valueOf(benefitLimit).intValue());
                            if (!planAdapter.isValidPlanCoverageBenefitLimit(finalPlanCode, optionalCoverageCode, benefitCode, benefitLimitBigDecimal)) {
                                errorMessages.add(benefitLimit + " is not valid Benefit Limit for benefit" + benefitCode + ".");
                            }
                        } catch (Exception e) {

                        }
                    }
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
        for (PlanCoverageDetailDto planCoverageDetailDto : planCoverageDetailDtoList) {
            for (PlanCoverageDetailDto.CoverageDto coverageDto : planCoverageDetailDto.getCoverageDtoList()) {
                String coverageHeader = AppConstants.OPTIONAL_COVERAGE_HEADER + count;
                int cellNumber = headers.indexOf(coverageHeader);
                Cell cell = row.getCell(cellNumber);
                int optionalCoverageSACellNumber = headers.indexOf(coverageHeader + " " + AppConstants.OPTIONAL_COVERAGE_SA_HEADER);
                int optionalCoverageVisibilityCellNumber = headers.indexOf((AppConstants.OPTIONAL_COVERAGE_HEADER + count) + " " + AppConstants.OPTIONAL_COVERAGE_PREMIUM_VISIBILITY_HEADER);
                int optionalCoveragePremiumCellNumber = headers.indexOf((AppConstants.OPTIONAL_COVERAGE_HEADER + count) + " " + AppConstants.PREMIUM_CELL_HEADER_NAME);
                Cell optionalCoveragePremiumCell = row.getCell(optionalCoveragePremiumCellNumber);
                Cell optionalCoverageSACell = row.getCell(optionalCoverageSACellNumber);
                OptionalCoverageCellHolder optionalCoverageCellHolder = null;
                if (isNotEmpty(getCellValue(cell))) {
                    optionalCoverageCellHolder = new OptionalCoverageCellHolder(cell, optionalCoverageSACell, row.getCell(optionalCoverageVisibilityCellNumber), optionalCoveragePremiumCell);
                    int benefitCount = 1;
                    for (PlanCoverageDetailDto.BenefitDto benefitDto : coverageDto.getBenefits()) {
                        String benefitHeader = coverageHeader + " " + (AppConstants.OPTIONAL_COVERAGE_BENEFIT_HEADER + benefitCount);
                        String benefitLimitHeader = coverageHeader + " " + (AppConstants.OPTIONAL_COVERAGE_BENEFIT_HEADER + benefitCount + " Limit");
                        Cell benefitCell = row.getCell(headers.indexOf(benefitHeader));
                        Cell benefitLimitCell = row.getCell(headers.indexOf(benefitLimitHeader));
                        if (optionalCoverageCellHolder != null && isNotEmpty(getCellValue(benefitCell))) {
                            OptionalCoverageBenefitCellHolder optionalCoverageBenefitCellHolder = new OptionalCoverageBenefitCellHolder(benefitCell, benefitLimitCell);
                            optionalCoverageCellHolder = optionalCoverageCellHolder.addBenefitCellHolder(optionalCoverageBenefitCellHolder);
                        }
                        benefitCount++;
                    }
                    optionalCoverageCellHolders.add(optionalCoverageCellHolder);
                }
                count++;
            }
        }
        return optionalCoverageCellHolders;
    }

    private String validateRow(Row insureDataRow, List<String> headers, List<PlanId> agentPlans) {
        String errorMessage = "";
        Set<String> errorMessages = Sets.newHashSet();
        String planCellValue = getCellValue(insureDataRow.getCell(headers.indexOf(GHInsuredExcelHeader.PLAN.name())));
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
                GHInsuredExcelHeader ghInsuredExcelHeader = GHInsuredExcelHeader.valueOf(header);
                errorMessages.add(ghInsuredExcelHeader.validateAndIfNotBuildErrorMessage(planAdapter, insureDataRow, cellValue, headers));
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
        Cell relationshipCell = row.getCell(headers.indexOf(GHInsuredExcelHeader.RELATIONSHIP.getDescription()));
        String relationship = getCellValue(relationshipCell);
        return Relationship.SELF.description.equals(relationship);
    }

    private Map<Row, List<Row>> groupByRelationship(List<Row> dataRows, List<String> headers) {
        Iterator<Row> rowIterator = dataRows.iterator();
        Map<Row, List<Row>> categoryRowMap = Maps.newLinkedHashMap();
        Row selfRelationshipRow = null;
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            Cell relationshipCell = getCellByName(row, headers, GHInsuredExcelHeader.RELATIONSHIP.getDescription());
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

    private boolean isRowsContainSamePlan(List<Row> rows, List<String> headers) {
        boolean isRowContainsSamePlan = false;
        for (Row row : rows) {
            if (isRowContainsSamePlan) {
                break;
            }
            Cell planCell = getCellByName(row, headers, GHInsuredExcelHeader.PLAN.getDescription());
            String planCode = getCellValue(planCell);
            for (Row otherRow : rows) {
                Cell otherPlanCell = getCellByName(otherRow, headers, GHInsuredExcelHeader.PLAN.getDescription());
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

    @Getter
    public class OptionalCoverageBenefitCellHolder {

        private Cell benefitCell;

        private Cell benefitLimitCell;

        public OptionalCoverageBenefitCellHolder(Cell benefitCell, Cell benefitLimitCell) {
            this.benefitCell = benefitCell;
            this.benefitLimitCell = benefitLimitCell;
        }

    }

    @Getter
    public class OptionalCoverageCellHolder {

        private Cell optionalCoverageCell;

        private Cell optionalCoverageSACell;

        private Cell optionalCoverageVisibilityCell;

        private Cell optionalCoveragePremiumCell;

        private Set<OptionalCoverageBenefitCellHolder> benefitCellHolders;

        public OptionalCoverageCellHolder(Cell optionalCoverageCell, Cell optionalCoverageSACell, Cell optionalCoverageVisibilityCell, Cell optionalCoveragePremiumCell) {
            this.optionalCoverageCell = optionalCoverageCell;
            this.optionalCoverageSACell = optionalCoverageSACell;
            this.optionalCoverageVisibilityCell = optionalCoverageVisibilityCell;
            this.optionalCoveragePremiumCell = optionalCoveragePremiumCell;
        }

        public OptionalCoverageCellHolder addBenefitCellHolder(OptionalCoverageBenefitCellHolder benefitCellHolder) {
            if (isEmpty(this.benefitCellHolders)) {
                this.benefitCellHolders = Sets.newHashSet();
            }
            this.benefitCellHolders.add(benefitCellHolder);
            return this;
        }
    }
}
