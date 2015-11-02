package com.pla.grouplife.endorsement.application.service.excel.parser;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.pla.grouplife.endorsement.dto.GLEndorsementInsuredDto;
import com.pla.grouplife.policy.query.GLPolicyFinder;
import com.pla.grouplife.sharedresource.dto.InsuredDto;
import com.pla.grouplife.sharedresource.model.GLEndorsementExcelHeader;
import com.pla.grouplife.sharedresource.model.GLEndorsementType;
import com.pla.grouplife.sharedresource.model.vo.Insured;
import com.pla.grouplife.sharedresource.model.vo.InsuredDependent;
import com.pla.grouplife.sharedresource.model.vo.PlanPremiumDetail;
import com.pla.publishedlanguage.contract.IPlanAdapter;
import com.pla.sharedkernel.domain.model.Relationship;
import com.pla.sharedkernel.identifier.PolicyId;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static com.pla.grouplife.sharedresource.exception.GLInsuredTemplateExcelParseException.raiseNotValidHeaderException;
import static com.pla.sharedkernel.util.ExcelGeneratorUtil.getCellValue;
import static org.nthdimenzion.utils.UtilValidator.isEmpty;


/**
 * Created by Samir on 8/19/2015.
 */
@Component
public class GLMemberAdditionExcelParser extends AbstractGLEndorsementExcelParser {


    @Autowired
    private IPlanAdapter planAdapter;

    @Autowired
    private GLPolicyFinder glPolicyFinder;

    @Override
    public boolean isValidExcel(HSSFWorkbook excelFile, PolicyId policyId) {
        boolean isValidTemplate = true;
        List<Row> dataRows = getDataRowsFromExcel(excelFile);
        Row headerRow = getHeaderRow(excelFile);
        List<String> headers = getHeaders(excelFile);
        List<String> allowedHeaders = transformToString(GLEndorsementType.ASSURED_MEMBER_ADDITION.getAllowedExcelHeaders());
        if (!isValidHeader(headers, allowedHeaders)) {
            raiseNotValidHeaderException();
        }
        Map policyMap = glPolicyFinder.findPolicyById(policyId.getPolicyId());
        List<Insured> insureds = (List<Insured>) policyMap.get("insureds");
        GLEndorsementExcelValidator glEndorsementExcelValidator = new GLEndorsementExcelValidator(policyId, insureds, planAdapter);
        Cell errorMessageHeaderCell = null;
        for (Row currentRow : dataRows) {
            String errorMessage = validateRow(currentRow, headers, glEndorsementExcelValidator);
            List<Row> duplicateRows = findDuplicateRow(dataRows, currentRow, headers);
            String duplicateRowErrorMessage = buildDuplicateRowMessage(duplicateRows);
            if (isEmpty(errorMessage) && isEmpty(duplicateRowErrorMessage)) {
                continue;
            }
            isValidTemplate = false;
            if (errorMessageHeaderCell == null) {
                errorMessageHeaderCell = createErrorMessageHeaderCell(excelFile, headerRow, headers);
            }
            Cell errorMessageCell = currentRow.createCell(headers.size());
            errorMessage = errorMessage + "\n" + duplicateRowErrorMessage;
            errorMessageCell.setCellValue(errorMessage);
        }
        return isValidTemplate;
    }

    @Override
    public GLEndorsementInsuredDto transformExcelToGLEndorsementDto(HSSFWorkbook workbook, PolicyId policyId) {
        List<Row> dataRows = getDataRowsFromExcel(workbook);
        List<String> headers = getHeaders(workbook);
        Map<Row, List<Row>> datRowMap = groupByRelationship(dataRows, headers);
        GLEndorsementInsuredDto glEndorsementInsuredDto = new GLEndorsementInsuredDto();
        List<InsuredDto> insuredDtos = buildInsuredDetail(datRowMap, headers, policyId);
        glEndorsementInsuredDto.setInsureds(insuredDtos);
        return glEndorsementInsuredDto;
    }

    private List<InsuredDto> buildInsuredDetail(Map<Row, List<Row>> excelRowsGroupedByRelationship, List<String> excelHeaders, PolicyId policyId) {
        List<InsuredDto> insuredDtoList = excelRowsGroupedByRelationship.entrySet().stream().map(new Function<Map.Entry<Row, List<Row>>, InsuredDto>() {
            @Override
            public InsuredDto apply(Map.Entry<Row, List<Row>> rowListEntry) {
                List<Row> dependentRows = rowListEntry.getValue() != null ? rowListEntry.getValue() : Lists.newArrayList();
                InsuredDto insuredDto = null;
                if (rowListEntry.getKey() != null) {
                    Row insuredRow = rowListEntry.getKey();
                    insuredDto = createInsuredDto(insuredRow, excelHeaders);
                    Cell relationshipCell = getCellByName(insuredRow, excelHeaders, GLEndorsementExcelHeader.RELATIONSHIP.getDescription());
                    Cell categoryCell = getCellByName(insuredRow, excelHeaders, GLEndorsementExcelHeader.CATEGORY.getDescription());
                    String relationship = getCellValue(relationshipCell);
                    String category = getCellValue(categoryCell);
                    if (insuredDto.getPlanPremiumDetail() == null) {
                        insuredDto.setPlanPremiumDetail(new InsuredDto.PlanPremiumDetailDto());
                    }
                    insuredDto.setPlanPremiumDetail(findPlanIdByRelationshipFromPolicy(policyId, Relationship.getRelationship(relationship), insuredDto.getNoOfAssured(), category));
                } else {
                    insuredDto = new InsuredDto();
                }
                Set<InsuredDto.InsuredDependentDto> insuredDependentDtoSet = dependentRows.stream().map(new Function<Row, InsuredDto.InsuredDependentDto>() {
                    @Override
                    public InsuredDto.InsuredDependentDto apply(Row row) {
                        InsuredDto.InsuredDependentDto insuredDependentDto = createInsuredDependentDto(row, excelHeaders);
                        Cell relationshipCell = getCellByName(row, excelHeaders, GLEndorsementExcelHeader.RELATIONSHIP.getDescription());
                        String relationship = getCellValue(relationshipCell);
                        Cell categoryCell = getCellByName(row, excelHeaders, GLEndorsementExcelHeader.CATEGORY.getDescription());
                        String category = getCellValue(categoryCell);
                        if (insuredDependentDto.getPlanPremiumDetail() == null) {
                            insuredDependentDto.setPlanPremiumDetail(new InsuredDto.PlanPremiumDetailDto());
                        }
                        insuredDependentDto.setPlanPremiumDetail(findPlanIdByRelationshipOfDependentsFromPolicy(policyId, Relationship.getRelationship(relationship), insuredDependentDto.getNoOfAssured(), category));
                        return insuredDependentDto;
                    }
                }).collect(Collectors.toSet());
                insuredDto.setInsuredDependents(insuredDependentDtoSet);
                return insuredDto;
            }
        }).collect(Collectors.toList());
        return insuredDtoList;
    }

    private InsuredDto createInsuredDto(Row excelRow, List<String> excelHeaders) {
        final InsuredDto[] insuredDto = {new InsuredDto()};
        excelHeaders.forEach(excelHeader -> {
            int cellNumber = excelHeaders.indexOf(excelHeader);
            Cell cell = excelRow.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            GLEndorsementExcelHeader glEndorsementExcelHeader = GLEndorsementExcelHeader.findGLEndorsementExcelHeaderTypeFromDescription(excelHeader);
            insuredDto[0] = glEndorsementExcelHeader.populate(insuredDto[0], cellValue);
        });

        return insuredDto[0];
    }

    private InsuredDto.InsuredDependentDto createInsuredDependentDto(Row excelRow, List<String> excelHeaders) {
        final InsuredDto.InsuredDependentDto[] insuredDependentDto = {new InsuredDto.InsuredDependentDto()};
        excelHeaders.forEach(excelHeader -> {
            int cellNumber = excelHeaders.indexOf(excelHeader);
            Cell cell = excelRow.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            GLEndorsementExcelHeader glEndorsementExcelHeader = GLEndorsementExcelHeader.findGLEndorsementExcelHeaderTypeFromDescription(excelHeader);
            insuredDependentDto[0] = glEndorsementExcelHeader.populate(insuredDependentDto[0], cellValue);
        });
        return insuredDependentDto[0];
    }


    //TODO populate plan and sum assured detail
    private InsuredDto.PlanPremiumDetailDto findPlanIdByRelationshipFromPolicy(PolicyId policyId, Relationship relationship,Integer noOfAssuredInEndorsement,String category) {
        noOfAssuredInEndorsement = noOfAssuredInEndorsement!=null?noOfAssuredInEndorsement:1;
        Map<String,Object> policyMap  = glPolicyFinder.findPolicyById(policyId.getPolicyId());
        List<Insured> insureds = (List<Insured>) policyMap.get("insureds");
        Optional<Insured> insuredOptional  = insureds.parallelStream().filter(new Predicate<Insured>() {
            @Override
            public boolean test(Insured insured) {
                return (Relationship.SELF.equals(relationship) && insured.getCategory().equals(category));
            }
        }).findAny();
        if (insuredOptional.isPresent()){
            Insured insured  = insuredOptional.get();
            Integer noOfAssured = insured.getNoOfAssured()!=null?insured.getNoOfAssured():1;
            PlanPremiumDetail planPremiumDetail = insured.getPlanPremiumDetail();
            BigDecimal totalPremium = planPremiumDetail.getPremiumAmount().divide(new BigDecimal(noOfAssured),2,BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(noOfAssuredInEndorsement).setScale(0, BigDecimal.ROUND_FLOOR));
            InsuredDto.PlanPremiumDetailDto planPremiumDetailDto = new InsuredDto.PlanPremiumDetailDto(planPremiumDetail.getPlanId().getPlanId(),planPremiumDetail.getPlanCode(),totalPremium,planPremiumDetail.getSumAssured());
            return planPremiumDetailDto;
        }
        return new InsuredDto.PlanPremiumDetailDto();
    }

    //TODO populate plan and sum assured detail
    private InsuredDto.PlanPremiumDetailDto findPlanIdByRelationshipOfDependentsFromPolicy(PolicyId policyId, Relationship relationship,Integer noOfAssuredInEndorsement,String category) {
        noOfAssuredInEndorsement = noOfAssuredInEndorsement!=null?noOfAssuredInEndorsement:1;
        Map<String,Object> policyMap  = glPolicyFinder.findPolicyById(policyId.getPolicyId());
        List<Insured> insureds = (List<Insured>) policyMap.get("insureds");
        for (Insured insured : insureds){
            Optional<InsuredDependent> dependentOptional =  insured.getInsuredDependents().parallelStream().filter(new Predicate<InsuredDependent>() {
                @Override
                public boolean test(InsuredDependent insuredDependent) {
                    return (insuredDependent.getRelationship().equals(relationship) && insuredDependent.getCategory().equals(category));
                }
            }).findAny();
            if (dependentOptional.isPresent()){
                InsuredDependent insuredDependent = dependentOptional.get();
                Integer noOfInsuredDependent =  insuredDependent.getNoOfAssured()!=null?insuredDependent.getNoOfAssured():1;
                PlanPremiumDetail planPremiumDetail = insuredDependent.getPlanPremiumDetail();
                BigDecimal totalPremium = planPremiumDetail.getPremiumAmount().divide(new BigDecimal(noOfInsuredDependent),2,BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(noOfAssuredInEndorsement).setScale(0, BigDecimal.ROUND_FLOOR));
                InsuredDto.PlanPremiumDetailDto planPremiumDetailDto = new InsuredDto.PlanPremiumDetailDto(planPremiumDetail.getPlanId().getPlanId(),planPremiumDetail.getPlanCode(),totalPremium,planPremiumDetail.getSumAssured());
                return planPremiumDetailDto;
            }
        }
        return new InsuredDto.PlanPremiumDetailDto();
    }

    private Map<Row, List<Row>> groupByRelationship(List<Row> dataRows, List<String> headers) {
        Iterator<Row> rowIterator = dataRows.iterator();
        Map<Row, List<Row>> categoryRowMap = Maps.newLinkedHashMap();
        Row selfRelationshipRow = null;
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            Cell relationshipCell = getCellByName(row, headers, GLEndorsementExcelHeader.RELATIONSHIP.getDescription());
            Cell mainAssuredClientIdCell = getCellByName(row, headers, GLEndorsementExcelHeader.MAIN_ASSURED_CLIENT_ID.getDescription());
            String relationship = getCellValue(relationshipCell);
            String mainAssuredClientId = getCellValue(mainAssuredClientIdCell);
            if (Relationship.SELF.description.equals(relationship) && isEmpty(mainAssuredClientId)) {
                selfRelationshipRow = row;
                categoryRowMap.put(selfRelationshipRow, new ArrayList<>());
            } else {
                List<Row> rows = categoryRowMap.get(selfRelationshipRow)!=null?categoryRowMap.get(selfRelationshipRow):Lists.newArrayList();
                rows.add(row);
                categoryRowMap.put(selfRelationshipRow, rows);
            }
        }
        return categoryRowMap;
    }

    @Override
    protected String validateRow(Row row, List<String> headers, GLEndorsementExcelValidator glEndorsementExcelValidator) {
        Set<String> errorMessages = Sets.newHashSet();
        headers.forEach(header -> {
            Cell cell = row.getCell(headers.indexOf(header));
            String cellValue = getCellValue(cell);
            GLEndorsementExcelHeader glInsuredExcelHeader = GLEndorsementExcelHeader.findGLEndorsementExcelHeaderTypeFromDescription(header);
            checkArgument(glInsuredExcelHeader != null, "Header is not valid");
            errorMessages.add(glInsuredExcelHeader.getErrorMessageIfNotValid(glEndorsementExcelValidator, row, cellValue, headers));
        });
        String errorMessage = buildErrorMessage(errorMessages);
        return errorMessage;
    }

    private List<Row> findDuplicateRow(List<Row> dataRowsForDuplicateCheck, Row currentRow, List<String> headers) {
        List<Row> duplicateRows = Lists.newArrayList();
        Cell firstNameCell = currentRow.getCell(headers.indexOf(GLEndorsementExcelHeader.FIRST_NAME.getDescription()));
        String firstNameCellValue = getCellValue(firstNameCell);
        Cell lastNameCell = currentRow.getCell(headers.indexOf(GLEndorsementExcelHeader.LAST_NAME.getDescription()));
        String lastNameCellValue = getCellValue(lastNameCell);
        Cell dateOfBirthCell = currentRow.getCell(headers.indexOf(GLEndorsementExcelHeader.DATE_OF_BIRTH.getDescription()));
        String dateOfBirthCellValue = getCellValue(dateOfBirthCell);
        NameRelationshipCellValueHolder currentRowNameRelationshipHolder = new NameRelationshipCellValueHolder(firstNameCellValue, lastNameCellValue, dateOfBirthCellValue);
        dataRowsForDuplicateCheck.forEach(dataRowForDuplicateCheck -> {
            if (currentRow.getRowNum() != dataRowForDuplicateCheck.getRowNum()) {
                Cell otherRowFirstNameCell = dataRowForDuplicateCheck.getCell(headers.indexOf(GLEndorsementExcelHeader.FIRST_NAME.getDescription()));
                String otherRowFirstNameCellValue = getCellValue(otherRowFirstNameCell);
                Cell otherRowLastNameCell = dataRowForDuplicateCheck.getCell(headers.indexOf(GLEndorsementExcelHeader.LAST_NAME.getDescription()));
                String otherRowLastNameCellValue = getCellValue(otherRowLastNameCell);
                Cell otherRowDateOfBirthCell = dataRowForDuplicateCheck.getCell(headers.indexOf(GLEndorsementExcelHeader.DATE_OF_BIRTH.getDescription()));
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
}
