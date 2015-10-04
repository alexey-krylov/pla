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

import java.util.List;
import java.util.Map;
import java.util.Set;

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
            Cell noOfAssuredCell = row.getCell(excelHeaders.indexOf(GLEndorsementExcelHeader.NO_OF_ASSURED.getDescription()));
            String noOfAssuredCellValue = getCellValue(noOfAssuredCell);
            if (isNotEmpty(noOfAssuredCellValue) && isEmpty(value)) {
                return true;
            }
            return super.isValidClientId(row, value, excelHeaders);
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
