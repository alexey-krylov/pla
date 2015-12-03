package com.pla.grouphealth.sharedresource.service;

import com.google.common.collect.Sets;
import com.pla.core.domain.model.generalinformation.PolicyProcessMinimumLimit;
import com.pla.core.domain.model.generalinformation.PolicyProcessMinimumLimitItem;
import com.pla.core.domain.model.generalinformation.ProductLineGeneralInformation;
import com.pla.grouphealth.proposal.domain.model.GroupHealthProposal;
import com.pla.grouphealth.quotation.domain.model.GroupHealthQuotation;
import com.pla.grouphealth.sharedresource.dto.CategoryPlanDataHolder;
import com.pla.grouphealth.sharedresource.dto.RelationshipPlanDataHolder;
import com.pla.grouphealth.sharedresource.model.vo.GHInsured;
import com.pla.grouplife.proposal.domain.model.GroupLifeProposal;
import com.pla.grouplife.quotation.domain.model.GroupLifeQuotation;
import com.pla.grouplife.sharedresource.model.vo.Insured;
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


    public static boolean isPremiumLessThenMinimumConfiguredPremiumGH(GroupHealthQuotation groupHealthQuotation, int minimumPremium) {
        /*BigDecimal totalPremiumInExcel = BigDecimal.ZERO;
        for (GHInsured ghInsured : insuredDtos) {
            totalPremiumInExcel = totalPremiumInExcel.add(ghInsured.getPlanPremiumDetail().getPremiumAmount());
        }*/
        BigDecimal totalPayablePremium = groupHealthQuotation.getPremiumDetail() != null ? groupHealthQuotation.getPremiumDetail().getNetTotalPremium() : new BigDecimal(0);
        if(totalPayablePremium.compareTo(new BigDecimal(minimumPremium)) == 1 || totalPayablePremium.compareTo(new BigDecimal(minimumPremium)) == 0)
            return Boolean.FALSE;
        return Boolean.TRUE;
    }

    public static boolean isPremiumLessThenMinimumConfiguredPremiumGH(GroupHealthProposal groupHealthProposal, int minimumPremium) {
        BigDecimal totalPayablePremium = groupHealthProposal.getPremiumDetail() != null ? groupHealthProposal.getPremiumDetail().getNetTotalPremium() : new BigDecimal(0);
        if(totalPayablePremium.compareTo(new BigDecimal(minimumPremium)) == 1 || totalPayablePremium.compareTo(new BigDecimal(minimumPremium)) == 0)
            return Boolean.FALSE;
        return Boolean.TRUE;
    }

    public static boolean isPremiumLessThenMinimumConfiguredPremiumGL(GroupLifeQuotation groupLifeQuotation, int minimumPremium) {
       /* BigDecimal totalPremiumInExcel = BigDecimal.ZERO;
        for (Insured ghInsured : insuredDtos) {
            totalPremiumInExcel = totalPremiumInExcel.add(ghInsured.getPlanPremiumDetail().getPremiumAmount());
        }
        if(totalPremiumInExcel.compareTo(new BigDecimal(minimumPremium)) == 1 || totalPremiumInExcel.compareTo(new BigDecimal(minimumPremium)) == 0)
            return Boolean.TRUE;
        return Boolean.FALSE;*/
        BigDecimal totalPayablePremium = groupLifeQuotation.getPremiumDetail() != null ? groupLifeQuotation.getPremiumDetail().getNetTotalPremium() : new BigDecimal(0);
        if(totalPayablePremium.compareTo(new BigDecimal(minimumPremium)) == 1 || totalPayablePremium.compareTo(new BigDecimal(minimumPremium)) == 0)
            return Boolean.FALSE;
        return Boolean.TRUE;
    }

    public static boolean isPremiumLessThenMinimumConfiguredPremiumGL(GroupLifeProposal groupLifeProposal, int minimumPremium) {
        BigDecimal totalPayablePremium = groupLifeProposal.getPremiumDetail() != null ? groupLifeProposal.getPremiumDetail().getNetTotalPremium() : new BigDecimal(0);
        if(totalPayablePremium.compareTo(new BigDecimal(minimumPremium)) == 1 || totalPayablePremium.compareTo(new BigDecimal(minimumPremium)) == 0)
            return Boolean.FALSE;
        return Boolean.TRUE;
    }

    private static BigDecimal getPremiumDetail(BigDecimal planPremium, Integer noOfAssured) {
        BigDecimal sumOfPlanPremiumAndNoOfAssured = BigDecimal.ZERO;
        sumOfPlanPremiumAndNoOfAssured = planPremium.add(sumOfPlanPremiumAndNoOfAssured);
            if(noOfAssured != null){
                return new BigDecimal(noOfAssured).multiply(sumOfPlanPremiumAndNoOfAssured);
            }
        return sumOfPlanPremiumAndNoOfAssured;
    }

    public static boolean isNoOfPersonsLessThenMinimumConfiguredPersonsGH(Set<GHInsured> insuredDtos, int minimumConfiguredPersons) {
        int totalNumberOfPersonInExcel = 0;
        for (GHInsured ghInsured : insuredDtos) {
            if(ghInsured.getNoOfAssured() != null){
                totalNumberOfPersonInExcel += new BigDecimal(ghInsured.getNoOfAssured()).intValue();
            } else {
                totalNumberOfPersonInExcel += 1;
            }
        }
        if(totalNumberOfPersonInExcel >= minimumConfiguredPersons)
            return Boolean.FALSE;
        return Boolean.TRUE;
    }

    public static boolean isNoOfPersonsLessThenMinimumConfiguredPersonsGL(Set<Insured> insuredDtos, int minimumConfiguredPersons) {
        int totalNumberOfPersonInExcel = 0;
        for (Insured ghInsured : insuredDtos) {
            if(ghInsured.getNoOfAssured() != null){
                totalNumberOfPersonInExcel += new BigDecimal(ghInsured.getNoOfAssured()).intValue();
            } else {
                totalNumberOfPersonInExcel += 1;
            }
        }
        if(totalNumberOfPersonInExcel >= minimumConfiguredPersons)
            return Boolean.FALSE;
        return Boolean.TRUE;
    }

    public static boolean validateIfLessThanMinimumNoOfPersonsForGHQuotation(GroupHealthQuotation groupHealthQuotation, ProductLineGeneralInformation productLineInformation) {
        int minimumNumberOfPersonPerPolicy = getMinimumValueForGivenCriteria(productLineInformation, PolicyProcessMinimumLimitType.MINIMUM_NUMBER_OF_PERSON_PER_POLICY);
        return isNoOfPersonsLessThenMinimumConfiguredPersonsGH(groupHealthQuotation.getInsureds(), minimumNumberOfPersonPerPolicy);
    }

    public static boolean validateIfLessThanMinimumPremiumForGHQuotation(GroupHealthQuotation groupHealthQuotation, ProductLineGeneralInformation productLineInformation) {
        int minimumPremium = getMinimumValueForGivenCriteria(productLineInformation, PolicyProcessMinimumLimitType.MINIMUM_PREMIUM);
        return isPremiumLessThenMinimumConfiguredPremiumGH(groupHealthQuotation, minimumPremium);
    }

    public static boolean validateIfLessThanMinimumNoOfPersonsForGLQuotation(GroupLifeQuotation groupLifeQuotation, ProductLineGeneralInformation productLineInformation) {
        int minimumNumberOfPersonPerPolicy = getMinimumValueForGivenCriteria(productLineInformation, PolicyProcessMinimumLimitType.MINIMUM_NUMBER_OF_PERSON_PER_POLICY);
        return isNoOfPersonsLessThenMinimumConfiguredPersonsGL(groupLifeQuotation.getInsureds(), minimumNumberOfPersonPerPolicy);
    }

    public static boolean validateIfLessThanMinimumPremiumForGLQuotation(GroupLifeQuotation groupLifeQuotation, ProductLineGeneralInformation productLineInformation) {
        int minimumPremium = getMinimumValueForGivenCriteria(productLineInformation, PolicyProcessMinimumLimitType.MINIMUM_PREMIUM);
        return isPremiumLessThenMinimumConfiguredPremiumGL(groupLifeQuotation, minimumPremium);
    }

    public static boolean validateIfLessThanMinimumNoOfPersonsForGHProposal(GroupHealthProposal groupHealthProposal, ProductLineGeneralInformation productLineInformation) {
        int minimumNumberOfPersonPerPolicy = getMinimumValueForGivenCriteria(productLineInformation, PolicyProcessMinimumLimitType.MINIMUM_NUMBER_OF_PERSON_PER_POLICY);
        return isNoOfPersonsLessThenMinimumConfiguredPersonsGH(groupHealthProposal.getInsureds(), minimumNumberOfPersonPerPolicy);
    }

    public static boolean validateIfLessThanMinimumPremiumForGHProposal(GroupHealthProposal groupHealthProposal, ProductLineGeneralInformation productLineInformation) {
        int minimumPremium = getMinimumValueForGivenCriteria(productLineInformation, PolicyProcessMinimumLimitType.MINIMUM_PREMIUM);
        return isPremiumLessThenMinimumConfiguredPremiumGH(groupHealthProposal, minimumPremium);
    }

    public static boolean validateIfLessThanMinimumNoOfPersonsForGLProposal(GroupLifeProposal groupLifeProposal, ProductLineGeneralInformation productLineInformation) {
        int minimumNumberOfPersonPerPolicy = getMinimumValueForGivenCriteria(productLineInformation, PolicyProcessMinimumLimitType.MINIMUM_NUMBER_OF_PERSON_PER_POLICY);
        return isNoOfPersonsLessThenMinimumConfiguredPersonsGL(groupLifeProposal.getInsureds(), minimumNumberOfPersonPerPolicy);
    }

    public static boolean validateIfLessThanMinimumPremiumForGLProposal(GroupLifeProposal groupLifeProposal, ProductLineGeneralInformation productLineInformation) {
        int minimumPremium = getMinimumValueForGivenCriteria(productLineInformation, PolicyProcessMinimumLimitType.MINIMUM_PREMIUM);
        return isPremiumLessThenMinimumConfiguredPremiumGL(groupLifeProposal, minimumPremium);
    }

    public static boolean checkIfSameOptionalCoverage(Map<Row, List<Row>> insuredDependentMap, List<String> headers) {
        Set<String> optionalCoverageHeaders = getAllOptionalCoverageHeaders(headers);
        Set<String> optionalCoverageSet = Sets.newHashSet();
        for (Map.Entry<Row, List<Row>> rowEntry : insuredDependentMap.entrySet()) {
            Row row = rowEntry.getKey();
            for(String optionalCoverageHeader : optionalCoverageHeaders) {
                Cell optionalCoverageHeaderCell = getCellByName(row, headers, optionalCoverageHeader);
                String optionalCoverage = getCellValue(optionalCoverageHeaderCell);
                if(UtilValidator.isNotEmpty(optionalCoverage) && !optionalCoverageSet.add(optionalCoverage))
                    return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    private static Set<String> getAllOptionalCoverageHeaders(List<String> headers) {
        Set<String> optionalCoverageHeaders = Sets.newLinkedHashSet();
        for(String header : headers){
            String headerWithoutWhitespace = header.trim().replaceAll("//s+","");
            if(headerWithoutWhitespace.startsWith("OptionalCoverage") && headerWithoutWhitespace.matches("^OptionalCoverage\\d$")){
                optionalCoverageHeaders.add(header);
            }
        }
        return optionalCoverageHeaders;
    }
}
