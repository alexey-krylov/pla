package com.pla.grouplife.endorsement.application.service;

import com.google.common.collect.Maps;
import com.pla.grouplife.endorsement.application.service.excel.generator.*;
import com.pla.grouplife.sharedresource.model.GLEndorsementType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * Created by Samir on 8/10/2015.
 */
@Configuration
public class GLEndorsementExcelBeanConfiguration {

    @Autowired
    private GLMemberAdditionEndorsementExcelGenerator glMemberAdditionEndorsementExcelGenerator;

    @Autowired
    private GLMemberDeletionEndorsementExcelGenerator glMemberDeletionEndorsementExcelGenerator;

    @Autowired
    private GLMemberPromotionEndorsementExcelGenerator glMemberPromotionEndorsementExcelGenerator;

    @Autowired
    private GLNewCategoryEndorsementExcelGenerator glNewCategoryEndorsementExcelGenerator;

    @Autowired
    private GLNameCorrectionEndorsementExcelGenerator glNameCorrectionEndorsementExcelGenerator;

    @Autowired
    private GLNRCCorrectionEndorsementExcelGenerator glnrcCorrectionEndorsementExcelGenerator;

    @Autowired
    private GLMANCorrectionEndorsementExcelGenerator glmanCorrectionEndorsementExcelGenerator;

    @Autowired
    private GLGenderCorrectionEndorsementExcelGenerator glGenderCorrectionEndorsementExcelGenerator;

    @Autowired
    private GLDOBCorrectionEndorsementExcelGenerator gldobCorrectionEndorsementExcelGenerator;


    @Bean(name = "glEndorsementService")
    public GLEndorsementService glEndorsementService() {
        Map<GLEndorsementType, GLEndorsementExcelGenerator> excelGenerators = Maps.newHashMap();
        excelGenerators.put(GLEndorsementType.ASSURED_MEMBER_ADDITION, glMemberAdditionEndorsementExcelGenerator);
        excelGenerators.put(GLEndorsementType.ASSURED_MEMBER_DELETION, glMemberDeletionEndorsementExcelGenerator);
        excelGenerators.put(GLEndorsementType.MEMBER_PROMOTION, glMemberPromotionEndorsementExcelGenerator);
        excelGenerators.put(GLEndorsementType.NEW_CATEGORY_RELATION, glNewCategoryEndorsementExcelGenerator);
        excelGenerators.put(GLEndorsementType.CHANGE_ASSURED_NAME, glNameCorrectionEndorsementExcelGenerator);
        excelGenerators.put(GLEndorsementType.CHANGE_NRC, glnrcCorrectionEndorsementExcelGenerator);
        excelGenerators.put(GLEndorsementType.CHANGE_MAN_NUMBER, glmanCorrectionEndorsementExcelGenerator);
        excelGenerators.put(GLEndorsementType.CHANGE_GENDER, glGenderCorrectionEndorsementExcelGenerator);
        excelGenerators.put(GLEndorsementType.CHANGE_DOB, gldobCorrectionEndorsementExcelGenerator);
        GLEndorsementService glEndorsementService = new GLEndorsementService(excelGenerators);
        return glEndorsementService;
    }

}
