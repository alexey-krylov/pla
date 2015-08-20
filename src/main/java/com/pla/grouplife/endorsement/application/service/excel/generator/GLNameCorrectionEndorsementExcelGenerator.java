package com.pla.grouplife.endorsement.application.service.excel.generator;

import com.google.common.collect.Lists;
import com.pla.grouplife.sharedresource.model.GLEndorsementExcelHeader;
import com.pla.grouplife.sharedresource.model.GLEndorsementType;
import com.pla.sharedkernel.identifier.EndorsementId;
import com.pla.sharedkernel.identifier.PolicyId;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by Samir on 8/10/2015.
 */

@Component
public class GLNameCorrectionEndorsementExcelGenerator extends AbstractGLEndorsementExcelGenerator{
    @Override
    public HSSFWorkbook generate(PolicyId policyId,EndorsementId endorsementId) {
        List<GLEndorsementExcelHeader> excelHeaderList = GLEndorsementType.CHANGE_ASSURED_NAME.getExcelHeaderByEndorsementType();
        List<String> excelHeaderInString = excelHeaderList.stream().map(new Function<GLEndorsementExcelHeader, String>() {
            @Override
            public String apply(GLEndorsementExcelHeader glEndorsementExcelHeader) {
                return glEndorsementExcelHeader.getDescription();
            }
        }).collect(Collectors.toList());
        HSSFWorkbook workbook = createExcel(excelHeaderInString, Lists.newArrayList());
        return workbook;
    }
}
