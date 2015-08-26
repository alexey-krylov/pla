package com.pla.grouplife.endorsement.application.service.excel.generator;

import com.google.common.collect.Lists;
import com.pla.grouplife.sharedresource.model.GLEndorsementExcelHeader;
import com.pla.grouplife.sharedresource.model.GLEndorsementType;
import com.pla.sharedkernel.identifier.EndorsementId;
import com.pla.sharedkernel.identifier.PolicyId;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;


/**
 * Created by Samir on 8/10/2015.
 */
@Component
public class GLMANCorrectionEndorsementExcelGenerator extends AbstractGLEndorsementExcelGenerator {

    @Override
    public HSSFWorkbook generate(PolicyId policyId,EndorsementId endorsementId) {
        List<GLEndorsementExcelHeader> excelHeaderList = GLEndorsementType.CHANGE_MAN_NUMBER.getAllowedExcelHeaders();
        List<String> excelHeaderInString = excelHeaderList.stream().map(excelHeader -> excelHeader.getDescription()).collect(Collectors.toList());
        HSSFWorkbook workbook = createExcel(excelHeaderInString, Lists.newArrayList());
        return workbook;
    }
}
