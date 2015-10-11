package com.pla.grouplife.endorsement.application.service.excel.parser;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.pla.grouplife.endorsement.dto.GLEndorsementInsuredDto;
import com.pla.grouplife.sharedresource.dto.InsuredDto;
import com.pla.grouplife.sharedresource.model.GLEndorsementExcelHeader;
import com.pla.grouplife.sharedresource.model.GLEndorsementType;
import com.pla.grouplife.sharedresource.model.vo.Insured;
import com.pla.grouplife.sharedresource.query.GLFinder;
import com.pla.publishedlanguage.contract.IPlanAdapter;
import com.pla.sharedkernel.identifier.PolicyId;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static com.pla.grouplife.sharedresource.exception.GLInsuredTemplateExcelParseException.raiseNotValidHeaderException;
import static com.pla.sharedkernel.util.ExcelGeneratorUtil.getCellValue;
import static org.nthdimenzion.utils.UtilValidator.isEmpty;

/**
 * Created by Samir on 8/21/2015.
 */
@Component
public class GLMemberPromotionExcelParser extends AbstractGLEndorsementExcelParser {


    @Autowired
    private GLFinder glFinder;

    @Autowired
    private IPlanAdapter planAdapter;

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
        List<InsuredDto> insuredDtos = Lists.newArrayList();
        dataRows.forEach(dataRow -> {
            insuredDtos.add(createInsuredDto(dataRow, headers));
        });
        GLEndorsementInsuredDto glEndorsementInsuredDto = new GLEndorsementInsuredDto();
        glEndorsementInsuredDto.setInsureds(insuredDtos);
        return glEndorsementInsuredDto;
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
