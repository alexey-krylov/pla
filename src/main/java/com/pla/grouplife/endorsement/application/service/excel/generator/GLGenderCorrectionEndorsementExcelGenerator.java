package com.pla.grouplife.endorsement.application.service.excel.generator;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pla.grouplife.sharedresource.model.GLEndorsementExcelHeader;
import com.pla.grouplife.sharedresource.model.GLEndorsementType;
import com.pla.sharedkernel.domain.model.Gender;
import com.pla.sharedkernel.identifier.EndorsementId;
import com.pla.sharedkernel.identifier.PolicyId;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Samir on 8/10/2015.
 */
@Component
public class GLGenderCorrectionEndorsementExcelGenerator extends AbstractGLEndorsementExcelGenerator {

    @Override
    public HSSFWorkbook generate(PolicyId policyId, EndorsementId endorsementId) {
        List<GLEndorsementExcelHeader> excelHeaderList = GLEndorsementType.CHANGE_GENDER.getExcelHeaderByEndorsementType();
        List<String> excelHeaderInString = excelHeaderList.stream().map(excelHeader -> excelHeader.getDescription()).collect(Collectors.toList());
        Map<Integer, List<String>> constraintCellDataMap = Maps.newHashMap();
        constraintCellDataMap.put(excelHeaderInString.indexOf("Gender"), Gender.getAllGender());
        HSSFWorkbook workbook = createExcel(excelHeaderInString, Lists.newArrayList(), constraintCellDataMap);
        return workbook;
    }
}
