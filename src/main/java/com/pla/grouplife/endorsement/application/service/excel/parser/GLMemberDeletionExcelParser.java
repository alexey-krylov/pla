package com.pla.grouplife.endorsement.application.service.excel.parser;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.pla.grouplife.endorsement.application.service.GroupLifeEndorsementChecker;
import com.pla.grouplife.endorsement.dto.GLEndorsementInsuredDto;
import com.pla.grouplife.policy.query.GLPolicyFinder;
import com.pla.grouplife.sharedresource.dto.InsuredDto;
import com.pla.grouplife.sharedresource.model.GLEndorsementExcelHeader;
import com.pla.grouplife.sharedresource.model.GLEndorsementType;
import com.pla.grouplife.sharedresource.model.vo.Insured;
import com.pla.grouplife.sharedresource.model.vo.InsuredBuilder;
import com.pla.grouplife.sharedresource.model.vo.InsuredDependent;
import com.pla.grouplife.sharedresource.model.vo.PlanPremiumDetail;
import com.pla.grouplife.sharedresource.query.GLFinder;
import com.pla.publishedlanguage.contract.IPlanAdapter;
import com.pla.sharedkernel.domain.model.PolicyNumber;
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
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Created by Samir on 8/21/2015.
 */
@Component
public class GLMemberDeletionExcelParser extends AbstractGLEndorsementExcelParser {

    @Autowired
    private GLFinder glFinder;


    @Autowired
    private GLPolicyFinder glPolicyFinder;

    @Autowired
    private IPlanAdapter planAdapter;

    @Autowired
    private GroupLifeEndorsementChecker groupLifeEndorsementChecker;

    @Override
    protected String validateRow(Row row, List<String> headers, GLEndorsementExcelValidator endorsementExcelValidator) {
        Set<String> errorMessages = Sets.newHashSet();
        headers.forEach(header -> {
            Cell cell = row.getCell(headers.indexOf(header));
            String cellValue = getCellValue(cell);
            GLEndorsementExcelHeader glInsuredExcelHeader = GLEndorsementExcelHeader.findGLEndorsementExcelHeaderTypeFromDescription(header);
            checkArgument(glInsuredExcelHeader != null, "Header is not valid");
            errorMessages.add(glInsuredExcelHeader.getErrorMessageIfNotValid(endorsementExcelValidator, row, cellValue, headers));
        });
        String errorMessage = buildErrorMessage(errorMessages);
        return errorMessage;
    }

    @Override
    public boolean isValidExcel(HSSFWorkbook workbook, PolicyId policyId) {
        boolean isValidTemplate = true;
        List<Row> dataRows = getDataRowsFromExcel(workbook);
        Row headerRow = getHeaderRow(workbook);
        List<String> headers = getHeaders(workbook);
        List<String> allowedHeaders = transformToString(GLEndorsementType.ASSURED_MEMBER_DELETION.getAllowedExcelHeaders());
        if (!isValidHeader(headers, allowedHeaders)) {
            raiseNotValidHeaderException();
        }
        Map glPolicyMap = glFinder.findActiveMemberFromPolicyByPolicyId(policyId.getPolicyId());
        List<Insured> insureds = (List<Insured>) glPolicyMap.get("insureds");
        PolicyNumber policyNumber = (PolicyNumber) glPolicyMap.get("policyNumber");
        insureds = groupLifeEndorsementChecker.getNewCategoryAndRelationInsuredDetail(insureds,policyNumber.getPolicyNumber());
        GLEndorsementExcelValidator glEndorsementExcelValidator = new GLMemberDeletionRowValidator(policyId, insureds, planAdapter);
        Cell errorMessageHeaderCell = null;
        for (Row currentRow : dataRows) {
            String errorMessage = validateRow(currentRow, headers, glEndorsementExcelValidator);
            if (isEmpty(errorMessage)) {
                continue;
            }
            isValidTemplate = false;
            if (errorMessageHeaderCell == null) {
                errorMessageHeaderCell = createErrorMessageHeaderCell(workbook, headerRow, headers);
            }
            Cell errorMessageCell = currentRow.createCell(headers.size());
            errorMessageCell.setCellValue(errorMessage);
        }
        return isValidTemplate;

    }

    private class GLMemberDeletionRowValidator extends GLEndorsementExcelValidator {

        public GLMemberDeletionRowValidator(PolicyId policyId, List<Insured> policyAssureds, IPlanAdapter planAdapter) {
            super(policyId, policyAssureds, planAdapter);
        }

        public boolean isValidCategory(Row row, String value, List<String> excelHeaders) {
            Cell clientIdCell = row.getCell(excelHeaders.indexOf(GLEndorsementExcelHeader.CLIENT_ID.getDescription()));
            String clientIdValue = getCellValue(clientIdCell);
            if (isNotEmpty(clientIdValue)) {
                return true;
            }
            return super.isValidCategory(row, value, excelHeaders);
        }

        public boolean isValidRelationship(Row row, String value, List<String> excelHeaders) {
            Cell clientIdCell = row.getCell(excelHeaders.indexOf(GLEndorsementExcelHeader.CLIENT_ID.getDescription()));
            String clientIdValue = getCellValue(clientIdCell);
            if (isNotEmpty(clientIdValue)) {
                return true;
            }
            return super.isValidRelationship(row, value, excelHeaders);
        }

        public boolean isValidNumberOfAssured(Row row, String value, List<String> excelHeaders) {
            Cell clientIdCell = row.getCell(excelHeaders.indexOf(GLEndorsementExcelHeader.CLIENT_ID.getDescription()));
            String clientIdValue = getCellValue(clientIdCell);
            if (isNotEmpty(clientIdValue)) {
                return true;
            }
            return (isNotEmpty(value) && Double.valueOf(value) > 0);
        }

        public boolean isValidClientId(Row row, String value, List<String> excelHeaders) {
            Cell noOfAssuredCell = row.getCell(excelHeaders.indexOf(GLEndorsementExcelHeader.CLIENT_ID.getDescription()));
            String noOfAssuredCellValue = getCellValue(noOfAssuredCell);
            if (isNotEmpty(noOfAssuredCellValue) && isEmpty(value)) {
                return true;
            }
            String clientId = isNotEmpty(value)?String.valueOf(new BigDecimal(value).longValue()):"";
            return super.isValidClientId(row, clientId, excelHeaders);
        }
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

    private Map<Row, List<Row>> groupByRelationship(List<Row> dataRows, List<String> headers) {
        Iterator<Row> rowIterator = dataRows.iterator();
        Map<Row, List<Row>> categoryRowMap = Maps.newLinkedHashMap();
        Row selfRelationshipRow = null;
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            Cell relationshipCell = getCellByName(row, headers, GLEndorsementExcelHeader.RELATIONSHIP.getDescription());
            Cell mainAssuredClientIdCell = getCellByName(row, headers, GLEndorsementExcelHeader.CLIENT_ID.getDescription());
            String relationship = getCellValue(relationshipCell);
            String mainAssuredClientId = getCellValue(mainAssuredClientIdCell);
            if (Relationship.SELF.description.equals(relationship) || isNotEmpty(mainAssuredClientId)) {
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
                    Cell clientIdCell = getCellByName(insuredRow, excelHeaders, GLEndorsementExcelHeader.CLIENT_ID.getDescription());
                    String relationship = getCellValue(relationshipCell);
                    String category = getCellValue(categoryCell);
                    String clientId = getCellValue(clientIdCell);
                    if (isNotEmpty(clientId)) {
                        clientId = String.valueOf(new BigDecimal(clientId).longValue());
                    }
                    if (insuredDto.getPlanPremiumDetail() == null) {
                        insuredDto.setPlanPremiumDetail(new InsuredDto.PlanPremiumDetailDto());
                    }
                    insuredDto = findPlanIdByRelationshipFromPolicy(policyId, Relationship.getRelationship(relationship), insuredDto.getNoOfAssured(), category, clientId, insuredDto);
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


    //TODO populate plan and sum assured detail
    private InsuredDto findPlanIdByRelationshipFromPolicy(PolicyId policyId, Relationship relationship,Integer noOfAssuredInEndorsement,String category,String clientId,InsuredDto insuredDto) {
        noOfAssuredInEndorsement = noOfAssuredInEndorsement!=null?noOfAssuredInEndorsement:1;
        Map<String,Object> policyMap  = glPolicyFinder.findActiveMemberFromPolicyByPolicyId(policyId.getPolicyId());
        List<Insured> insureds = (List<Insured>) policyMap.get("insureds");
        PolicyNumber policyNumber = (PolicyNumber) policyMap.get("policyNumber");
        insureds = groupLifeEndorsementChecker.getNewCategoryAndRelationInsuredDetail(insureds,policyNumber.getPolicyNumber());
        if (isNotEmpty(clientId)) {
            Optional<Insured> insuredOptional = insureds.parallelStream().filter(new Predicate<Insured>() {
                @Override
                public boolean test(Insured insured) {
                    if (insured.getNoOfAssured() == null)
                        return insured.getFamilyId()!=null?insured.getFamilyId().getFamilyId().equals(clientId):false;
                    return false;
                }
            }).findAny();
            if (insuredOptional.isPresent()) {
                Insured insured = insuredOptional.get();
                InsuredBuilder insuredBuilder = new InsuredBuilder();
                insuredBuilder.withInsuredName(insured.getSalutation(),insured.getFirstName(),insured.getLastName())
                .withInsuredNrcNumber(insured.getNrcNumber()).withCompanyName(insured.getCompanyName()).withDateOfBirth(insured.getDateOfBirth())
                .withGender(insured.getGender()).withCategory(insured.getCategory()).withFamilyId(insured.getFamilyId().getFamilyId());
                insuredDto = insuredBuilder.buildInsuredDto();
                Integer noOfAssured = insured.getNoOfAssured() != null ? insured.getNoOfAssured() : 1;
                PlanPremiumDetail planPremiumDetail = insured.getPlanPremiumDetail();
                BigDecimal totalPremium = planPremiumDetail.getPremiumAmount().divide(new BigDecimal(noOfAssured), 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(noOfAssuredInEndorsement).setScale(0, BigDecimal.ROUND_FLOOR));
                InsuredDto.PlanPremiumDetailDto planPremiumDetailDto = new InsuredDto.PlanPremiumDetailDto(planPremiumDetail.getPlanId().getPlanId(), planPremiumDetail.getPlanCode(), totalPremium, planPremiumDetail.getSumAssured());
                insuredDto.setPlanPremiumDetail(planPremiumDetailDto);
                return insuredDto;
            }
            else if(!insuredOptional.isPresent()) {
                for (Insured insured : insureds) {
                    Optional<InsuredDependent> dependentOptional = insured.getInsuredDependents().parallelStream().filter(new Predicate<InsuredDependent>() {
                        @Override
                        public boolean test(InsuredDependent insuredDependent) {
                            if (insuredDependent.getNoOfAssured() == null)
                                return insuredDependent.getFamilyId().getFamilyId().equals(clientId);
                            return false;
                        }
                    }).findAny();
                    if (dependentOptional.isPresent()) {
                        InsuredDependent insuredDependent = dependentOptional.get();
                        InsuredBuilder insuredBuilder = new InsuredBuilder();
                        insuredBuilder.withInsuredName(insuredDependent.getSalutation(),insuredDependent.getFirstName(),insuredDependent.getLastName())
                                .withInsuredNrcNumber(insuredDependent.getNrcNumber()).withCompanyName(insuredDependent.getCompanyName()).withDateOfBirth(insuredDependent.getDateOfBirth())
                                .withGender(insuredDependent.getGender()).withCategory(insuredDependent.getCategory()).withFamilyId(insuredDependent.getFamilyId().getFamilyId());
                        insuredDto = insuredBuilder.buildInsuredDto();
                        Integer noOfInsuredDependent = insuredDependent.getNoOfAssured() != null ? insuredDependent.getNoOfAssured() : 1;
                        PlanPremiumDetail planPremiumDetail = insuredDependent.getPlanPremiumDetail();
                        BigDecimal totalPremium = planPremiumDetail.getPremiumAmount().divide(new BigDecimal(noOfInsuredDependent), 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(noOfAssuredInEndorsement).setScale(0, BigDecimal.ROUND_FLOOR));
                        InsuredDto.PlanPremiumDetailDto planPremiumDetailDto = new InsuredDto.PlanPremiumDetailDto(planPremiumDetail.getPlanId().getPlanId(), planPremiumDetail.getPlanCode(), totalPremium, planPremiumDetail.getSumAssured());
                        insuredDto.setPlanPremiumDetail(planPremiumDetailDto);
                        return insuredDto;
                    }
                }
            }
        }
        else {
            Optional<Insured>  insuredOptional  = insureds.parallelStream().filter(new Predicate<Insured>() {
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
                insuredDto.setPlanPremiumDetail(planPremiumDetailDto);
                return insuredDto;
            }
        }
        return insuredDto;
    }

    //TODO populate plan and sum assured detail
    private InsuredDto.PlanPremiumDetailDto findPlanIdByRelationshipOfDependentsFromPolicy(PolicyId policyId, Relationship relationship,Integer noOfAssuredInEndorsement,String category) {
        noOfAssuredInEndorsement = noOfAssuredInEndorsement!=null?noOfAssuredInEndorsement:1;
        Map<String,Object> policyMap  = glPolicyFinder.findActiveMemberFromPolicyByPolicyId(policyId.getPolicyId());
        List<Insured> insureds = (List<Insured>) policyMap.get("insureds");
        PolicyNumber policyNumber = (PolicyNumber) policyMap.get("policyNumber");
        insureds = groupLifeEndorsementChecker.getNewCategoryAndRelationInsuredDetail(insureds,policyNumber.getPolicyNumber());
        for (Insured insured : insureds){
            Optional<InsuredDependent> dependentOptional =  insured.getInsuredDependents().parallelStream().filter(new Predicate<InsuredDependent>() {
                @Override
                public boolean test(InsuredDependent insuredDependent) {
                    return (insuredDependent.getRelationship().equals(relationship) && insuredDependent.getCategory().equals(category));
                }
            }).findAny();
            if (dependentOptional.isPresent()){
                InsuredDependent insuredDependent = dependentOptional.get();
                Integer noOfInsuredDependent =   insuredDependent.getNoOfAssured()!=null?insuredDependent.getNoOfAssured():1;
                PlanPremiumDetail planPremiumDetail = insuredDependent.getPlanPremiumDetail();
                BigDecimal totalPremium = planPremiumDetail.getPremiumAmount().divide(new BigDecimal(noOfInsuredDependent),2,BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(noOfAssuredInEndorsement).setScale(0, BigDecimal.ROUND_FLOOR));
                InsuredDto.PlanPremiumDetailDto planPremiumDetailDto = new InsuredDto.PlanPremiumDetailDto(planPremiumDetail.getPlanId().getPlanId(),planPremiumDetail.getPlanCode(),totalPremium,planPremiumDetail.getSumAssured());
                return planPremiumDetailDto;
            }
        }
        return new InsuredDto.PlanPremiumDetailDto();
    }

}
