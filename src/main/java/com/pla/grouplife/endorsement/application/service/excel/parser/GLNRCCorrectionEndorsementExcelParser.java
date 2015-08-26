package com.pla.grouplife.endorsement.application.service.excel.parser;

import com.pla.grouplife.endorsement.dto.GLEndorsementInsuredDto;
import com.pla.sharedkernel.identifier.PolicyId;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by Samir on 8/21/2015.
 */
@Component
public class GLNRCCorrectionEndorsementExcelParser extends AbstractGLEndorsementExcelParser{
    @Override
    protected String validateRow(Row row, List<String> headers, GLEndorsementExcelValidator endorsementExcelValidator) {
        return null;
    }

    @Override
    public boolean isValidExcel(HSSFWorkbook workbook, PolicyId policyId) {
        return false;
    }

    @Override
    public GLEndorsementInsuredDto transformExcelToGLEndorsementDto(HSSFWorkbook workbook, PolicyId policyId) {
        return null;
    }
}
