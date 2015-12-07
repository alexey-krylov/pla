/*
 * Copyright (c) 3/26/15 8:00 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.application.service.plan.premium;

import com.google.common.collect.Lists;
import com.pla.core.domain.model.plan.Plan;
import com.pla.core.query.PlanFinder;
import com.pla.core.repository.PlanRepository;
import com.pla.publishedlanguage.domain.model.PremiumInfluencingFactor;
import com.pla.sharedkernel.domain.model.PremiumFactor;
import com.pla.sharedkernel.domain.model.PremiumTermType;
import com.pla.sharedkernel.identifier.CoverageId;
import com.pla.sharedkernel.identifier.PlanId;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.nthdimenzion.utils.UtilValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * @author: Samir
 * @since 1.0 26/03/2015
 */
@Service
public class PremiumService {

    private PlanRepository planRepository;

    private MongoTemplate mongoTemplate;

    private PremiumTemplateExcelGenerator premiumTemplateExcelGenerator;

    private PremiumTemplateParser premiumTemplateParser;

    private PlanFinder planFinder;

    @Autowired
    public PremiumService(PlanRepository planRepository, MongoTemplate mongoTemplate, PremiumTemplateParser premiumTemplateParser, PremiumTemplateExcelGenerator premiumTemplateExcelGenerator, PlanFinder planFinder) {
        this.planRepository = planRepository;
        this.mongoTemplate = mongoTemplate;
        this.premiumTemplateParser = premiumTemplateParser;
        this.premiumTemplateExcelGenerator = premiumTemplateExcelGenerator;
        this.planFinder = planFinder;
    }

    public HSSFWorkbook generatePremiumExcelTemplate(PremiumInfluencingFactor[] premiumInfluencingFactors, String planId, String coverageId) throws IOException {
        Plan plan = planRepository.findOne(new PlanId(planId));
        return premiumTemplateExcelGenerator.generatePremiumTemplate(Arrays.asList(premiumInfluencingFactors), plan, new CoverageId(coverageId));
    }

    public boolean validatePremiumTemplateData(HSSFWorkbook hssfWorkbook, PremiumInfluencingFactor[] premiumInfluencingFactors, String planId, String coverageId) throws IOException {
        Plan plan = planRepository.findOne(new PlanId(planId));
        List<PremiumInfluencingFactor> premiumInfluencingFactorList = UtilValidator.isNotEmpty(premiumInfluencingFactors) ? Arrays.asList(premiumInfluencingFactors) : new ArrayList<>();
        boolean isValidTemplate = premiumTemplateParser.validatePremiumDataForAGivenPlanAndCoverage(hssfWorkbook, plan, new CoverageId(coverageId), premiumInfluencingFactorList);
        return isValidTemplate;
    }

    public Map<String, List<Map<Map<PremiumInfluencingFactor, String>, Double>>> parsePremiumTemplate(HSSFWorkbook hssfWorkbook, PremiumInfluencingFactor[] premiumInfluencingFactors, String planId, String coverageId, PremiumTermType premiumTermType) {
        List<PremiumInfluencingFactor> premiumInfluencingFactorList = isNotEmpty(premiumInfluencingFactors) ? Arrays.asList(premiumInfluencingFactors) : new ArrayList<>();
        return premiumTemplateParser.parseAndTransformToPremiumData(hssfWorkbook, premiumInfluencingFactorList, premiumTermType);
    }

    public List<Map> getAllPremium() {
        Query query = new Query();
        query.fields().include("premiumId").include("planId").include("coverageId").include("effectiveFrom").include("validTill").include("premiumFactor").include("premiumRateFrequency").include("premiumInfluencingFactors");
        List<Map> premiumPlan = mongoTemplate.find(query, Map.class, "premium");
        List<Map> listOfPremiumPlan = Lists.newArrayList();
        for (Map plans : premiumPlan) {
            String planId = plans.get("planId").toString();
            List premiumInfluencingFactors = (List) plans.get("premiumInfluencingFactors");
            List premiumInfluencingFactorWithDescription = Lists.newArrayList();
            for(Object premiumInfluencingFactor:premiumInfluencingFactors){
                premiumInfluencingFactorWithDescription.add(PremiumInfluencingFactor.valueOf((String)premiumInfluencingFactor).getDescription());
            }
            plans.put("premiumInfluencingFactors",premiumInfluencingFactorWithDescription);
            String planName = planFinder.getPlanName(new PlanId(planId));
            plans.put("premiumFactor", PremiumFactor.valueOf((String) plans.get("premiumFactor")).getDescription());
            if (isNotEmpty(planName))
                plans.put("planName", planName);
            Map coverageId = (Map) plans.get("coverageId");
            plans.put("coverageNames", "");
            if (isNotEmpty(coverageId)) {
                String coverages = planFinder.getCoverageAssociatedWithPremiumPlan((String) coverageId.get("coverageId"));
                plans.put("coverageNames", coverages);
            }
            listOfPremiumPlan.add(plans);
        }
        return listOfPremiumPlan;
    }

}
