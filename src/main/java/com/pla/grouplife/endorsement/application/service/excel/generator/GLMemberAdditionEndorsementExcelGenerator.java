package com.pla.grouplife.endorsement.application.service.excel.generator;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pla.core.query.MasterFinder;
import com.pla.grouplife.sharedresource.model.GLEndorsementExcelHeader;
import com.pla.grouplife.sharedresource.model.GLEndorsementType;
import com.pla.grouplife.sharedresource.query.GLFinder;
import com.pla.sharedkernel.domain.model.Gender;
import com.pla.sharedkernel.domain.model.OccupationCategory;
import com.pla.sharedkernel.domain.model.Relationship;
import com.pla.sharedkernel.identifier.EndorsementId;
import com.pla.sharedkernel.identifier.PolicyId;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by Samir on 8/10/2015.
 */
@Component
public class GLMemberAdditionEndorsementExcelGenerator extends AbstractGLEndorsementExcelGenerator {

    @Autowired
    private MasterFinder masterFinder;

    @Autowired
    private GLFinder glFinder;


    @Override
    public HSSFWorkbook generate(PolicyId policyId, EndorsementId endorsementId) {
        List<GLEndorsementExcelHeader> excelHeaderList = GLEndorsementType.ASSURED_MEMBER_ADDITION.getExcelHeaderByEndorsementType();
        List<String> excelHeaderInString = excelHeaderList.stream().map(new Function<GLEndorsementExcelHeader, String>() {
            @Override
            public String apply(GLEndorsementExcelHeader glEndorsementExcelHeader) {
                return glEndorsementExcelHeader.getDescription();
            }
        }).collect(Collectors.toList());
        Map<Integer, List<String>> constraintCellDataMap = Maps.newHashMap();
        constraintCellDataMap.put(excelHeaderInString.indexOf("Gender"), Gender.getAllGender());
        constraintCellDataMap.put(excelHeaderInString.indexOf("Relationship"), Relationship.getAllRelation());
        constraintCellDataMap.put(excelHeaderInString.indexOf("Occupation"), getAllOccupationClassification());
        constraintCellDataMap.put(excelHeaderInString.indexOf("Category"), OccupationCategory.getAllCategory());
        HSSFWorkbook workbook = createExcel(excelHeaderInString, Lists.newArrayList(), constraintCellDataMap);
        return workbook;
    }

    private List<String> getAllOccupationClassification() {
        List<Map<String, Object>> occupationClassList = masterFinder.getAllOccupationClassification();
        List<String> occupationClasses = occupationClassList.stream().map(new Function<Map<String, Object>, String>() {
            @Override
            public String apply(Map<String, Object> stringObjectMap) {
                return (String) stringObjectMap.get("description");
            }
        }).collect(Collectors.toList());
        return occupationClasses;
    }

}
