package com.pla.grouphealth.sharedresource.service;

import com.google.common.collect.Sets;
import com.pla.core.domain.model.generalinformation.PolicyProcessMinimumLimit;
import com.pla.core.domain.model.generalinformation.PolicyProcessMinimumLimitItem;
import com.pla.core.domain.model.generalinformation.ProductLineGeneralInformation;
import com.pla.grouphealth.sharedresource.dto.CategoryPlanDataHolder;
import com.pla.grouphealth.sharedresource.dto.RelationshipPlanDataHolder;
import com.pla.sharedkernel.domain.model.PolicyProcessMinimumLimitType;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.nthdimenzion.utils.UtilValidator;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.pla.sharedkernel.util.ExcelGeneratorUtil.getCellValue;

/**
 * Created by Mohan Sharma on 11/20/2015.
 */
public class QuotationProposalUtilityService {

    public static boolean isSamePlanForAllCategory(Map<Row, List<Row>> relationshipGroupRowMap, List<String> headers) {
        RelationshipPlanDataHolder relationshipPlanDataHolder = null;
        Set<RelationshipPlanDataHolder> relationshipPlanDataHolderSet = Sets.newLinkedHashSet();
        for (Map.Entry<Row, List<Row>> rowEntry : relationshipGroupRowMap.entrySet()) {
            Row row = rowEntry.getKey();
            Cell relationshipCell = getCellByName(row, headers, GHInsuredExcelHeader.RELATIONSHIP.getDescription());
            String relationship = getCellValue(relationshipCell);
            Cell planCell = getCellByName(row, headers, GHInsuredExcelHeader.PLAN.getDescription());
            String planCode = getCellValue(planCell);
            relationshipPlanDataHolder = new RelationshipPlanDataHolder(relationship, planCode);
            relationshipPlanDataHolderSet.add(relationshipPlanDataHolder);
        }
        return checkIfSameRelationshipHasDifferentPlan(relationshipPlanDataHolderSet);
    }

    private static boolean checkIfSameRelationshipHasDifferentPlan(Set<RelationshipPlanDataHolder> relationshipPlanDataHolderSet) {
        for(RelationshipPlanDataHolder firstEntry : relationshipPlanDataHolderSet){
            for(RelationshipPlanDataHolder secondEntry : relationshipPlanDataHolderSet){
                if(firstEntry.getRelationship().equals(secondEntry.getRelationship()) && !firstEntry.getPlanCode().equals(secondEntry.getPlanCode()))
                    return Boolean.FALSE;
            }
        }
        return Boolean.TRUE;
    }

    public static boolean isSamePlanForAllRelation(Map<Row, List<Row>> relationshipGroupRowMap, List<String> headers) {
        CategoryPlanDataHolder categoryPlanDataHolder = null;
        Set<CategoryPlanDataHolder> categoryPlanDataHolderSet = Sets.newLinkedHashSet();
        for (Map.Entry<Row, List<Row>> rowEntry : relationshipGroupRowMap.entrySet()) {
            Row row = rowEntry.getKey();
            Cell categoryCell = getCellByName(row, headers, GHInsuredExcelHeader.CATEGORY.getDescription());
            String category = getCellValue(categoryCell);
            Cell planCell = getCellByName(row, headers, GHInsuredExcelHeader.PLAN.getDescription());
            String planCode = getCellValue(planCell);
            categoryPlanDataHolder = new CategoryPlanDataHolder(category, planCode);
            categoryPlanDataHolderSet.add(categoryPlanDataHolder);
        }
        return checkIfSameCategoryHasDifferentPlan(categoryPlanDataHolderSet);
    }

    private static boolean checkIfSameCategoryHasDifferentPlan(Set<CategoryPlanDataHolder> categoryPlanDataHolderSet) {
        for(CategoryPlanDataHolder firstEntry : categoryPlanDataHolderSet){
            for(CategoryPlanDataHolder secondEntry : categoryPlanDataHolderSet){
                if(firstEntry.getCategory().equals(secondEntry.getCategory()) && !firstEntry.getPlanCode().equals(secondEntry.getPlanCode()))
                    return Boolean.FALSE;
            }
        }
        return Boolean.TRUE;
    }

    public static boolean isSamePlanForAllRelationshipCategory(Map<Row, List<Row>> insuredDependentMap, List<String> headers) {
        Set<String> dataSet = Sets.newLinkedHashSet();
        for (Map.Entry<Row, List<Row>> rowEntry : insuredDependentMap.entrySet()) {
            Row row = rowEntry.getKey();
            Cell planCell = getCellByName(row, headers, GHInsuredExcelHeader.PLAN.getDescription());
            String planCode = getCellValue(planCell);
            dataSet.add(planCode);
        }
        if(dataSet.size() > 1)
            return Boolean.FALSE;
        return Boolean.TRUE;
    }

    private static Cell getCellByName(Row row, List<String> headers, String cellName) {
        int cellNumber = headers.indexOf(cellName);
        return row.getCell(cellNumber);
    }

    public static int getMinimumValueForGivenCriteria(ProductLineGeneralInformation productLineInformation, PolicyProcessMinimumLimitType policyProcessMinimumLimitType) {
        Set<PolicyProcessMinimumLimitItem> policyProcessMinimumLimitItems = getPolicyProcessMinimumLimitItems(productLineInformation);
        if(UtilValidator.isNotEmpty(policyProcessMinimumLimitItems)){
            for(PolicyProcessMinimumLimitItem policyProcessMinimumLimitItem : policyProcessMinimumLimitItems){
                if(policyProcessMinimumLimitItem.getPolicyProcessMinimumLimitType().equals(policyProcessMinimumLimitType)){
                    return policyProcessMinimumLimitItem.getValue();
                }

            }
        }
        return 0;
    }

    private static Set<PolicyProcessMinimumLimitItem> getPolicyProcessMinimumLimitItems(ProductLineGeneralInformation productLineInformation) {
        if(productLineInformation != null){
            PolicyProcessMinimumLimit policyProcessMinimumLimit = productLineInformation.getPolicyProcessMinimumLimit();
            if(policyProcessMinimumLimit != null){
                return policyProcessMinimumLimit.getPolicyProcessMinimumLimitItemsByForce();
            }
        }
        return Collections.EMPTY_SET;
    }


    public static boolean isPremiumGreaterThenMinimumConfiguredPremium(Map<Row, List<Row>> insuredDependentMap, List<String> headers, int minimumPremium) {
        BigDecimal totalPremiumInExcel = BigDecimal.ZERO;
        for (Map.Entry<Row, List<Row>> rowEntry : insuredDependentMap.entrySet()) {
            Row row = rowEntry.getKey();
            Cell planPremiumCell = getCellByName(row, headers, GHInsuredExcelHeader.PLAN_PREMIUM.getDescription());
            String planPremium = getCellValue(planPremiumCell);
            Cell noOfAssuredCell = getCellByName(row, headers, GHInsuredExcelHeader.NO_OF_ASSURED.getDescription());
            String noOfAssured = getCellValue(noOfAssuredCell);
            totalPremiumInExcel = totalPremiumInExcel.add(getPremiumDetail(planPremium, noOfAssured));
        }
        if(totalPremiumInExcel.compareTo(new BigDecimal(minimumPremium)) == 1 || totalPremiumInExcel.compareTo(new BigDecimal(minimumPremium)) == 0)
            return Boolean.FALSE;
        return Boolean.TRUE;
    }

    private static BigDecimal getPremiumDetail(String planPremium, String noOfAssured) {
        BigDecimal sumOfplanPremiumAndNoOfAssured = BigDecimal.ZERO;
        if(UtilValidator.isNotEmpty(planPremium)){
            sumOfplanPremiumAndNoOfAssured = new BigDecimal(planPremium);
            if(UtilValidator.isNotEmpty(noOfAssured)){
                return new BigDecimal(noOfAssured).multiply(sumOfplanPremiumAndNoOfAssured);
            }
        }
        return sumOfplanPremiumAndNoOfAssured;
    }

    public static boolean isNoOfPersonsGreaterThenMinimumConfiguredPersons(Map<Row, List<Row>> insuredDependentMap, List<String> headers, int minimumPremium) {
        int totalNumberOfPersonInExcel = 0;
        for (Map.Entry<Row, List<Row>> rowEntry : insuredDependentMap.entrySet()) {
            Row row = rowEntry.getKey();
            Cell noOfAssuredCell = getCellByName(row, headers, GHInsuredExcelHeader.NO_OF_ASSURED.getDescription());
            String noOfAssured = getCellValue(noOfAssuredCell);
            if(UtilValidator.isNotEmpty(noOfAssured)){
                totalNumberOfPersonInExcel = totalNumberOfPersonInExcel += new BigDecimal(noOfAssured).intValue();
            } else {
                totalNumberOfPersonInExcel = totalNumberOfPersonInExcel += 1;
            }
        }
        if(totalNumberOfPersonInExcel >= minimumPremium)
            return Boolean.FALSE;
        return Boolean.TRUE;
    }
}
