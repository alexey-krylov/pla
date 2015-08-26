package com.pla.grouplife.endorsement.application.service.excel.parser;

import com.pla.grouplife.endorsement.dto.GLEndorsementInsuredDto;
import com.pla.sharedkernel.identifier.PolicyId;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;


/**
 * Created by Samir on 8/19/2015.
 */
public interface GLEndorsementExcelParser {

    boolean isValidExcel(HSSFWorkbook workbook, PolicyId policyId);

    GLEndorsementInsuredDto transformExcelToGLEndorsementDto(HSSFWorkbook workbook, PolicyId policyId);
}
