package com.pla.grouplife.endorsement.application.service.excel.parser;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.pla.core.domain.model.agent.AgentId;
import com.pla.grouplife.endorsement.application.service.GroupLifeEndorsementChecker;
import com.pla.grouplife.endorsement.dto.GLEndorsementInsuredDto;
import com.pla.grouplife.endorsement.query.GLEndorsementFinder;
import com.pla.grouplife.policy.query.GLPolicyFinder;
import com.pla.grouplife.quotation.query.GLQuotationFinder;
import com.pla.grouplife.sharedresource.dto.InsuredDto;
import com.pla.grouplife.sharedresource.model.GLEndorsementExcelHeader;
import com.pla.grouplife.sharedresource.model.vo.Insured;
import com.pla.grouplife.sharedresource.model.vo.InsuredDependent;
import com.pla.grouplife.sharedresource.query.GLFinder;
import com.pla.grouplife.sharedresource.service.GLInsuredExcelHeader;
import com.pla.publishedlanguage.contract.IPlanAdapter;
import com.pla.publishedlanguage.dto.PlanCoverageDetailDto;
import com.pla.sharedkernel.domain.model.PolicyNumber;
import com.pla.sharedkernel.domain.model.Relationship;
import com.pla.sharedkernel.identifier.PlanId;
import com.pla.sharedkernel.identifier.PolicyId;
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

import static com.pla.grouplife.sharedresource.exception.GLInsuredTemplateExcelParseException.*;
import static com.pla.sharedkernel.util.ExcelGeneratorUtil.getCellValue;
import static org.nthdimenzion.utils.UtilValidator.isEmpty;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Created by Samir on 8/21/2015.
 */
@Component
public class GLNewCategoryEndorsementExcelParser extends AbstractGLEndorsementExcelParser{


    @Autowired
    private GLEndorsementFinder glEndorsementFinder;

    @Autowired
    private GLFinder glFinder;
    @Autowired
    private IPlanAdapter planAdapter;
    @Autowired
    private GLQuotationFinder glQuotationFinder;
    @Autowired
    private GLPolicyFinder glPolicyFinder;

    @Autowired
    private GroupLifeEndorsementChecker groupLifeEndorsementChecker;

    @Override
    protected String validateRow(Row row, List<String> headers, GLEndorsementExcelValidator endorsementExcelValidator) {
        return null;
    }

    @Override
    public boolean isValidExcel(HSSFWorkbook workbook, PolicyId policyId) {
        Map policyMap = glFinder.findPolicyById(policyId.getPolicyId());
        AgentId agentId = (AgentId) policyMap.get("agentId");
        List<PlanId> authorizedPlans = getAgentAuthorizedPlans(agentId.getAgentId());
        return isValidInsuredExcel(workbook, false, false, authorizedPlans,policyId);
    }

    @Override
    public GLEndorsementInsuredDto transformExcelToGLEndorsementDto(HSSFWorkbook workbook, PolicyId policyId) {
        Map policyMap = glFinder.findPolicyById(policyId.getPolicyId());
        AgentId agentId = (AgentId) policyMap.get("agentId");
        List<PlanId> authorizedPlans = getAgentAuthorizedPlans(agentId.getAgentId());
        List<InsuredDto> insuredDtos = transformToInsuredDto(workbook, authorizedPlans);
        GLEndorsementInsuredDto glEndorsementInsuredDto = new GLEndorsementInsuredDto();
        glEndorsementInsuredDto.setInsureds(insuredDtos);
        return glEndorsementInsuredDto;
    }


    private List<PlanId> getAgentAuthorizedPlans(String agentId) {
        List<Map<String, Object>> authorizedPlans = glEndorsementFinder.getAgentAuthorizedPlan(agentId);
        List<PlanId> planIds = authorizedPlans.stream().map(new Function<Map<String, Object>, PlanId>() {
            @Override
            public PlanId apply(Map<String, Object> authorizePlanMap) {
                String planId = (String) authorizePlanMap.get("planId");
                return new PlanId(planId);
            }
        }).collect(Collectors.toList());
        return planIds;
    }

    public boolean isValidInsuredExcel(HSSFWorkbook hssfWorkbook, boolean samePlanForAllCategory, boolean samePlanForAllRelation, List<PlanId> agentPlans, PolicyId policyId) {
        boolean isValidTemplate = true;
        HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(0);
        Iterator<Row> rowIterator = hssfSheet.rowIterator();
        Row headerRow = rowIterator.next();
        List<Row> dataRows = Lists.newArrayList(rowIterator);
        final List<String> headers = getHeaders(headerRow);
        boolean isValidHeader = isValidHeaders(headers, agentPlans);
        if (!isValidHeader) {
            raiseNotValidHeaderException();
        }
        boolean isFirstRowContainsSelfRelationship = isFirstRowContainsSelfRelationship(dataRows, headers);
        Map<Row, List<Row>> insuredDependentMap = groupByRelationship(dataRows, headers);
        boolean isSamePlanForAllCategory = true;
        if (isFirstRowContainsSelfRelationship) {
            isSamePlanForAllCategory = isSamePlanForAllCategory(insuredDependentMap, headers);
        }
        boolean isSamePlanForAllRelationship = isSamePlanForAllRelation(insuredDependentMap, headers);
        if (samePlanForAllCategory && !isSamePlanForAllCategory) {
            raiseNotSamePlanForAllCategoryException();
        } else if (samePlanForAllRelation && !isSamePlanForAllRelationship) {
            raiseNotSamePlanForAllRelationshipException();
        } else if (samePlanForAllCategory && samePlanForAllRelation && (!isSamePlanForAllCategory && !isSamePlanForAllCategory)) {
            raiseNotSamePlanForAllCategoryAndRelationshipException();
        }
        Map policyMap = glPolicyFinder.findPolicyById(policyId.getPolicyId());
        List<Insured> insureds = (List<Insured>) policyMap.get("insureds");
        PolicyNumber policyNumber = (PolicyNumber) policyMap.get("policyNumber");
        insureds = groupLifeEndorsementChecker.getNewCategoryAndRelationInsuredDetail(insureds,policyNumber.getPolicyNumber());
        Cell errorMessageHeaderCell = null;
        Iterator<Row> dataRowIterator = dataRows.iterator();
        while (dataRowIterator.hasNext()) {
            Row currentRow = dataRowIterator.next();
            List<String> excelHeaders = GLInsuredExcelHeader.getAllowedHeaderForParserEndorsement(planAdapter, agentPlans);
            String errorMessage = validateRow(currentRow, excelHeaders, agentPlans);
            String invalidCombinationMessage =  buildErrorMessageIfInvalidCategoryRelationCombination(currentRow,insureds,headers);
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
            if (isEmpty(errorMessage) && isEmpty(coverageErrorMessage) && isEmpty(duplicateRowErrorMessage) && isEmpty(invalidCombinationMessage)) {
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
            errorMessage = errorMessage + "\n" + coverageErrorMessage + "\n" +invalidCombinationMessage;
            errorMessage = errorMessage + duplicateRowErrorMessage;
            Cell errorMessageCell = currentRow.createCell(headers.size());
            errorMessageCell.setCellValue(errorMessage);
        }
        return isValidTemplate;
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

    private boolean isValidHeaders(List<String> headers, List<PlanId> planIds) {
        List<String> allowedHeaders = GLInsuredExcelHeader.getAllowedHeadersForEndorsement(planAdapter, planIds);
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
        return buildErrorMessages(errorMessages);
    }

    private List<OptionalCoverageCellHolder> findNonEmptyOptionalCoverageCell(List<String> headers, Row row, List<PlanId> planIds) {
        List<OptionalCoverageCellHolder> optionalCoverageCellHolders = Lists.newArrayList();
        List<PlanCoverageDetailDto> planCoverageDetailDtoList = planAdapter.getPlanAndCoverageDetail(planIds);
        int count = 1;
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
        errorMessage = buildErrorMessages(errorMessages);
        return errorMessage;
    }

    private String buildErrorMessages(Set<String> errorMessages) {
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
            Cell relationshipCell = getCellByNames(row, headers, GLInsuredExcelHeader.RELATIONSHIP.getDescription());
            String relationship = getCellValue(relationshipCell);
            if (Relationship.SELF.description.equals(relationship)) {
                selfRelationshipRow = row;
                categoryRowMap.put(selfRelationshipRow, new ArrayList<>());
            } else {
                List<Row> rows = selfRelationshipRow!=null?categoryRowMap.get(selfRelationshipRow):Lists.newArrayList();
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
            Cell planCell = getCellByNames(row, headers, GLInsuredExcelHeader.PLAN.getDescription());
            String planCode = getCellValue(planCell);
            if (isSamePlanForAllCategory) {
                break;
            }
            for (Map.Entry<Row, List<Row>> otherRowEntry : relationshipGroupRowMap.entrySet()) {
                Row otherRow = otherRowEntry.getKey();
                Cell otherPlanCell = getCellByNames(otherRow, headers, GLInsuredExcelHeader.PLAN.getDescription());
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
            Cell planCell = getCellByNames(row, headers, GLInsuredExcelHeader.PLAN.getDescription());
            String planCode = getCellValue(planCell);
            for (Row otherRow : rows) {
                Cell otherPlanCell = getCellByNames(otherRow, headers, GLInsuredExcelHeader.PLAN.getDescription());
                String otherPlanCode = getCellValue(otherPlanCell);
                if (isNotEmpty(planCode) && isNotEmpty(otherPlanCode) && planCode.equals(otherPlanCode)) {
                    isRowContainsSamePlan = true;
                    break;
                }
            }
        }
        return isRowContainsSamePlan;
    }

    private Cell getCellByNames(Row row, List<String> headers, String cellName) {
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
    private class OptionalCoverageCellHolder {

        private Cell optionalCoverageCell;

        private Cell optionalCoverageSACell;

        private Cell optionalCoveragePremiumCell;


        public OptionalCoverageCellHolder(Cell optionalCoverageCell, Cell optionalCoverageSACell, Cell optionalCoveragePremiumCell) {
            this.optionalCoverageCell = optionalCoverageCell;
            this.optionalCoverageSACell = optionalCoverageSACell;
            this.optionalCoveragePremiumCell = optionalCoveragePremiumCell;
        }

    }

    public List<InsuredDto> transformToInsuredDto(HSSFWorkbook hssfWorkbook, List<PlanId> agentPlans) {
        HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(0);
        Iterator<Row> rowIterator = hssfSheet.rowIterator();
        Row headerRow = rowIterator.next();
        final List<String> headers = GLInsuredExcelHeader.getAllowedHeaderForParserEndorsement(planAdapter, agentPlans);
        final List<String> excelHeaders = getHeaders(headerRow);
        Map<Row, List<Row>> insuredTemplateDataRowMap = groupByRelationship(Lists.newArrayList(rowIterator), GLInsuredExcelHeader.getAllowedHeadersForEndorsement(planAdapter, agentPlans));
        List<InsuredDto> insuredDtoList = insuredTemplateDataRowMap.entrySet().stream().map(new Function<Map.Entry<Row, List<Row>>, InsuredDto>() {
            @Override
            public InsuredDto apply(Map.Entry<Row, List<Row>> rowListEntry) {
                InsuredDto insuredDto = null;
                if (rowListEntry.getKey() != null) {
                    Row insuredRow = rowListEntry.getKey();
                    insuredDto = createInsuredDto(insuredRow, excelHeaders, headers, findNonEmptyOptionalCoverageCell(excelHeaders, insuredRow, agentPlans));
                    insuredDto.getPlanPremiumDetail().setPlanId(planAdapter.getPlanId(insuredDto.getPlanPremiumDetail().getPlanCode()).getPlanId());
                }
                if (insuredDto == null) {
                    insuredDto = new InsuredDto();
                }
                List<Row> dependentRows = rowListEntry.getValue() != null ? rowListEntry.getValue() : Lists.newArrayList();
                Set<InsuredDto.InsuredDependentDto> insuredDependentDtoSet = dependentRows.stream().map(new Function<Row, InsuredDto.InsuredDependentDto>() {
                    @Override
                    public InsuredDto.InsuredDependentDto apply(Row row) {
                        InsuredDto.InsuredDependentDto insuredDependentDto = createInsuredDependentDto(row, excelHeaders, headers, findNonEmptyOptionalCoverageCell(excelHeaders, row, agentPlans));
                        insuredDependentDto.getPlanPremiumDetail().setPlanId(planAdapter.getPlanId(insuredDependentDto.getPlanPremiumDetail().getPlanCode()).getPlanId());
                        return insuredDependentDto;
                    }
                }).collect(Collectors.toSet());
                insuredDto.setCategory(insuredDto.getOccupationCategory());
                insuredDto.setInsuredDependents(insuredDependentDtoSet);
                return insuredDto;
            }
        }).collect(Collectors.toList());
        return insuredDtoList;
    }


    private InsuredDto.InsuredDependentDto createInsuredDependentDto(Row dependentRow, List<String> excelHeaders, List<String> headers, List<OptionalCoverageCellHolder> optionalCoverageCells) {
        InsuredDto.InsuredDependentDto insuredDependentDto = new InsuredDto.InsuredDependentDto();
        for (String header : headers) {
            if (!header.contains(AppConstants.OPTIONAL_COVERAGE_HEADER)) {
                insuredDependentDto = GLInsuredExcelHeader.valueOf(header).populateInsuredDependentDetail(insuredDependentDto, dependentRow, excelHeaders);
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
                coveragePremiumDetailDto.setPremium(coveragePremium);
                int coverageSA = Double.valueOf(ExcelGeneratorUtil.getCellValue(optionalCoverageCellHolder.getOptionalCoverageSACell())).intValue();
                coveragePremiumDetailDto.setSumAssured(BigDecimal.valueOf(coverageSA));
                return coveragePremiumDetailDto;
            }
        }).collect(Collectors.toList());
        insuredDependentDto = insuredDependentDto.addCoveragePremiumDetails(coveragePremiumDetails);
        return insuredDependentDto;
    }

    private InsuredDto createInsuredDto(Row row, List<String> excelHeaders, List<String> headers, List<OptionalCoverageCellHolder> optionalCoverageCells) {
        InsuredDto insuredDto = new InsuredDto();
        for (String header : headers) {
            if (!header.contains(AppConstants.OPTIONAL_COVERAGE_HEADER)) {
                insuredDto = GLInsuredExcelHeader.valueOf(header).populateInsuredDetail(insuredDto, row, excelHeaders);
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

    private String buildErrorMessageIfInvalidCategoryRelationCombination(Row currentRow, List<Insured> insureds,List<String> headers){
        Cell categoryCell = currentRow.getCell(headers.indexOf(GLEndorsementExcelHeader.CATEGORY.getDescription()));
        String categoryCellValue = getCellValue(categoryCell);
        Cell relationshipCell = currentRow.getCell(headers.indexOf(GLEndorsementExcelHeader.RELATIONSHIP.getDescription()));
        String relationCellValue = getCellValue(relationshipCell);
        boolean isExists  = false;
        for (Insured insured :insureds){
            isExists = insured.getCategory()!=null?insured.getCategory().equals(categoryCellValue):false && Relationship.SELF.description.equals(relationCellValue);
            if (!isExists){
                Set<InsuredDependent> insuredDependents =insured.getInsuredDependents()!=null?insured.getInsuredDependents():Sets.newLinkedHashSet();
                for (InsuredDependent insuredDependent :insuredDependents){
                    if (insuredDependent.getCategory()!=null?insuredDependent.getCategory().equals(categoryCellValue):false && insuredDependent.getRelationship()!=null?insuredDependent.getRelationship().description.equals(relationCellValue):false){
                        isExists = true;
                        break;
                    }
                }
            }
            if (isExists){
                break;
            }
        }
        return isExists?"Category - Relation is an existing combination":"";
    }

}
