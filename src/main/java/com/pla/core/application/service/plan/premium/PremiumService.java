/*
 * Copyright (c) 3/26/15 8:00 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.application.service.plan.premium;

import com.pla.core.domain.model.plan.Plan;
import com.pla.core.repository.PlanRepository;
import com.pla.sharedkernel.domain.model.PremiumInfluencingFactor;
import com.pla.sharedkernel.identifier.CoverageId;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.nthdimenzion.utils.UtilValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author: Samir
 * @since 1.0 26/03/2015
 */
@Service
public class PremiumService {

    private PlanRepository planRepository;

    private MongoTemplate mongoTemplate;

    @Autowired
    public PremiumService(PlanRepository planRepository, MongoTemplate mongoTemplate) {
        this.planRepository = planRepository;
        this.mongoTemplate = mongoTemplate;
    }

    public HSSFWorkbook generatePremiumExcelTemplate(PremiumInfluencingFactor[] premiumInfluencingFactors, String planId, String coverageId) throws IOException {
        Plan plan = planRepository.findByPlanId(planId);
        return new PremiumTemplateExcelGenerator().generatePremiumTemplate(Arrays.asList(premiumInfluencingFactors), plan, new CoverageId(coverageId));
    }

    public boolean validatePremiumTemplateData(HSSFWorkbook hssfWorkbook, PremiumInfluencingFactor[] premiumInfluencingFactors, String planId, String coverageId) throws IOException {
        Plan plan = planRepository.findByPlanId(planId);
        PremiumTemplateParser premiumTemplateParser = new PremiumTemplateParser();
        List<PremiumInfluencingFactor> premiumInfluencingFactorList = UtilValidator.isNotEmpty(premiumInfluencingFactors) ? Arrays.asList(premiumInfluencingFactors) : new ArrayList<>();
        boolean isValidTemplate = premiumTemplateParser.validatePremiumDataForAGivenPlanAndCoverage(hssfWorkbook, plan, new CoverageId(coverageId), premiumInfluencingFactorList);
        FileOutputStream stream = new FileOutputStream("E:\\pla\\afterParsing.xls");
        hssfWorkbook.write(stream);
        stream.close();
        return isValidTemplate;
    }

    public List<Map<Map<PremiumInfluencingFactor, String>, Double>> parsePremiumTemplate(HSSFWorkbook hssfWorkbook, PremiumInfluencingFactor[] premiumInfluencingFactors, String planId, String coverageId) {
        PremiumTemplateParser premiumTemplateParser = new PremiumTemplateParser();
        List<PremiumInfluencingFactor> premiumInfluencingFactorList = UtilValidator.isNotEmpty(premiumInfluencingFactors) ? Arrays.asList(premiumInfluencingFactors) : new ArrayList<>();
        return premiumTemplateParser.parseAndTransformToPremiumData(hssfWorkbook, premiumInfluencingFactorList);
    }

    public List<Map> getAllPremium() {
        Query query = new Query();
        query.fields().include("premiumId").include("planId").include("coverageId").include("effectiveFrom").include("validTill").include("premiumFactor").include("premiumRateFrequency");
        return mongoTemplate.find(query, Map.class,"premium");
    }


}
