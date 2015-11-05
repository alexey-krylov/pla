package com.pla.grouplife.endorsement.application.service.excel.parser;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.pla.grouplife.endorsement.dto.GLEndorsementInsuredDto;
import com.pla.grouplife.policy.query.GLPolicyFinder;
import com.pla.grouplife.sharedresource.dto.InsuredDto;
import com.pla.grouplife.sharedresource.model.GLEndorsementExcelHeader;
import com.pla.grouplife.sharedresource.model.GLEndorsementType;
import com.pla.grouplife.sharedresource.model.vo.Insured;
import com.pla.grouplife.sharedresource.model.vo.InsuredBuilder;
import com.pla.grouplife.sharedresource.model.vo.PlanPremiumDetail;
import com.pla.grouplife.sharedresource.query.GLFinder;
import com.pla.publishedlanguage.contract.IPlanAdapter;
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
public class GLMemberPromotionExcelParser extends AbstractGLEndorsementExcelParser {


    @Autowired
    private GLFinder glFinder;

    @Autowired
    private IPlanAdapter planAdapter;


    @Autowired
    private GLPolicyFinder glPolicyFinder;


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
        List<String> allowedHeaders = transformToString(GLEndorsementType.MEMBER_PROMOTION.getAllowedExcelHeaders());
        if (!isValidHeader(headers, allowedHeaders)) {
            raiseNotValidHeaderException();
        }
        Map glPolicyMap = glFinder.findPolicyById(policyId.getPolicyId());
        List<Insured> insureds = (List<Insured>) glPolicyMap.get("insureds");
        GLEndorsementExcelValidator glEndorsementExcelValidator = new GLMemberPromotionRowValidator(policyId, insureds, planAdapter);
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

    private class GLMemberPromotionRowValidator extends GLEndorsementExcelValidator {

        public GLMemberPromotionRowValidator(PolicyId policyId, List<Insured> policyAssureds, IPlanAdapter planAdapter) {
            super(policyId, policyAssureds, planAdapter);
        }


        public boolean isValidClientId(Row row, String value, List<String> excelHeaders) {
            if (isEmpty(value)) {
                return false;
            }
            return super.isValidClientId(row, value, excelHeaders);
        }

        public boolean isValidOldAnnualIncome(Row row, String value, List<String> excelHeaders) {
            Cell clientIdCell = row.getCell(excelHeaders.indexOf(GLEndorsementExcelHeader.MAIN_ASSURED_CLIENT_ID.getDescription()));
            String clientId = getCellValue(clientIdCell);
            if (!isValidClientId(super.getPolicyAssureds(), clientId)) {
                return false;
            }
            if (isEmpty(value)) {
                return false;
            }
            BigDecimal existingAnnualIncome = null;
            Optional<Insured> insuredOptional = super.getPolicyAssureds().stream().filter(policyAssured -> (policyAssured.getFamilyId() != null && clientId.equals(policyAssured.getFamilyId().getFamilyId()))).findAny();
            if (insuredOptional.isPresent()) {
                existingAnnualIncome = insuredOptional.get().getAnnualIncome();
            }
            BigDecimal newAnnualIncome = BigDecimal.valueOf(Double.valueOf(value));
            return newAnnualIncome.compareTo(existingAnnualIncome) == 0;
        }

        public boolean isValidNewAnnualIncome(Row row, String value, List<String> excelHeaders) {
            Cell oldAnnualIncomeCell = row.getCell(excelHeaders.indexOf(GLEndorsementExcelHeader.OLD_ANNUAL_INCOME.getDescription()));
            String oldIncome = getCellValue(oldAnnualIncomeCell);
            if (isEmpty(value)) {
                return false;
            }
            return !(value.equals(oldIncome));
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
            Cell clientIdCell = getCellByName(row, headers, GLEndorsementExcelHeader.CLIENT_ID.getDescription());
            String clientId = getCellValue(clientIdCell);
            if (isNotEmpty(clientId)) {
                selfRelationshipRow = row;
                categoryRowMap.put(selfRelationshipRow, new ArrayList<>());
            }
        }
        return categoryRowMap;
    }

    private List<InsuredDto> buildInsuredDetail(Map<Row, List<Row>> excelRowsGroupedByRelationship, List<String> excelHeaders, PolicyId policyId) {
        List<InsuredDto> insuredDtoList = excelRowsGroupedByRelationship.entrySet().stream().map(new Function<Map.Entry<Row, List<Row>>, InsuredDto>() {
            @Override
            public InsuredDto apply(Map.Entry<Row, List<Row>> rowListEntry) {
                InsuredDto insuredDto = null;
                if (rowListEntry.getKey() != null) {
                    Row insuredRow = rowListEntry.getKey();
                    insuredDto = createInsuredDto(insuredRow, excelHeaders);
                    Cell clientIdCell = getCellByName(insuredRow, excelHeaders, GLEndorsementExcelHeader.CLIENT_ID.getDescription());
                    String clientId = getCellValue(clientIdCell);
                    if (insuredDto.getPlanPremiumDetail() == null) {
                        insuredDto.setPlanPremiumDetail(new InsuredDto.PlanPremiumDetailDto());
                    }
                    if (isNotEmpty(clientId)) {
                        clientId  = String.valueOf(new BigDecimal(clientId).longValue());
                    }
                    insuredDto =  findPlanIdByRelationshipFromPolicy(policyId, clientId, insuredDto, insuredDto.getAnnualIncome(),insuredDto.getFamilyId());
                } else {
                    insuredDto = new InsuredDto();
                }
                return insuredDto;
            }
        }).collect(Collectors.toList());
        return insuredDtoList;
    }

    //TODO populate plan and sum assured detail
    private InsuredDto findPlanIdByRelationshipFromPolicy(PolicyId policyId,String clientId,InsuredDto insuredDto,BigDecimal annualIncome,String familyId) {
        Map<String,Object> policyMap  = glPolicyFinder.findPolicyById(policyId.getPolicyId());
        List<Insured> insureds = (List<Insured>) policyMap.get("insureds");
        Optional<Insured> insuredOptional  = insureds.parallelStream().filter(new Predicate<Insured>() {
            @Override
            public boolean test(Insured insured) {
                return insured.getFamilyId().getFamilyId().equals(clientId);
            }
        }).findAny();
        if (insuredOptional.isPresent()){
            Insured insured  = insuredOptional.get();
            PlanPremiumDetail planPremiumDetail = insured.getPlanPremiumDetail();
            BigDecimal totalPremium = planPremiumDetail.getPremiumAmount();
            InsuredBuilder insuredBuilder = new InsuredBuilder();
            insuredDto = insuredBuilder.withInsuredName(insured.getSalutation(), insured.getFirstName(), insured.getLastName()).withFamilyId(familyId)
                    .withDateOfBirth(insured.getDateOfBirth()).withGender(insured.getGender()).withCategory(insured.getCategory()).withAnnualIncome(annualIncome).buildInsuredDto();

            InsuredDto.PlanPremiumDetailDto planPremiumDetailDto = new InsuredDto.PlanPremiumDetailDto(planPremiumDetail.getPlanId().getPlanId(),planPremiumDetail.getPlanCode(),totalPremium,planPremiumDetail.getSumAssured(),planPremiumDetail.getIncomeMultiplier());
            insuredDto.setPlanPremiumDetail(planPremiumDetailDto);
            return insuredDto;
        }
        return insuredDto;
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
}
