package com.pla.core.domain.model.generalinformation;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pla.sharedkernel.domain.model.*;
import com.pla.sharedkernel.identifier.LineOfBusinessEnum;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by Admin on 4/23/2015.
 */
public enum GeneralInformationProcessItem {

    DEFAULT{
        @Override
        public List<Map<String, Object>> getOrganizationLevelProcessInformationItem(LineOfBusinessEnum lineOfBusinessId) {
            List<Map<String,Object>> productLineProcessList = Lists.newArrayList();
            List<ProductLineProcessType> productLineProcessTypes = Arrays.asList(ProductLineProcessType.EARLY_DEATH_CRITERIA, ProductLineProcessType.LAPSE, ProductLineProcessType.TYPE, ProductLineProcessType.CHARGES, ProductLineProcessType.INTEREST);
            for (ProductLineProcessType productLineProcessType : ProductLineProcessType.values()){
                if (!productLineProcessTypes.contains(productLineProcessType)){
                    Map<String,Object> quotationProcessItems = Maps.newLinkedHashMap();
                    quotationProcessItems.put("productLineProcessItem",productLineProcessType);
                    quotationProcessItems.put("value",0);
                    productLineProcessList.add(quotationProcessItems);
                }
            }
            return productLineProcessList;
        }
    },
    REINSTATEMENT{
        @Override
        public List<Map<String, Object>> getOrganizationLevelProcessInformationItem(LineOfBusinessEnum lineOfBusinessId) {
            List<Map<String,Object>> productLineProcessList = Lists.newArrayList();
            List<ProductLineProcessType> productLineProcessTypes = LineOfBusinessEnum.INDIVIDUAL_LIFE.equals(lineOfBusinessId) ? Arrays.asList(ProductLineProcessType.EARLY_DEATH_CRITERIA, ProductLineProcessType.LAPSE, ProductLineProcessType.CHARGES) :
                    Arrays.asList(ProductLineProcessType.EARLY_DEATH_CRITERIA, ProductLineProcessType.LAPSE, ProductLineProcessType.TYPE, ProductLineProcessType.CHARGES,ProductLineProcessType.INTEREST);
            for (ProductLineProcessType productLineProcessType : ProductLineProcessType.values()){
                if (!productLineProcessTypes.contains(productLineProcessType)){
                    Map<String,Object> quotationProcessItems = Maps.newLinkedHashMap();
                    quotationProcessItems.put("productLineProcessItem",productLineProcessType);
                    quotationProcessItems.put("value",0);
                    productLineProcessList.add(quotationProcessItems);
                }
            }
            return productLineProcessList;
        }
    },
    SURRENDER{
        @Override
        public List<Map<String, Object>> getOrganizationLevelProcessInformationItem(LineOfBusinessEnum lineOfBusinessId) {
            List<Map<String,Object>> productLineProcessList = Lists.newLinkedList();
            List<ProductLineProcessType> productLineProcessTypes = LineOfBusinessEnum.INDIVIDUAL_LIFE.equals(lineOfBusinessId) ? Arrays.asList(ProductLineProcessType.EARLY_DEATH_CRITERIA, ProductLineProcessType.LAPSE, ProductLineProcessType.INTEREST) :
                    Arrays.asList(ProductLineProcessType.EARLY_DEATH_CRITERIA, ProductLineProcessType.LAPSE, ProductLineProcessType.TYPE, ProductLineProcessType.CHARGES, ProductLineProcessType.INTEREST);
            for (ProductLineProcessType productLineProcessType : ProductLineProcessType.values()){
                if (!productLineProcessTypes.contains(productLineProcessType)){
                    Map<String,Object> quotationProcessItems = Maps.newLinkedHashMap();
                    quotationProcessItems.put("productLineProcessItem",productLineProcessType);
                    quotationProcessItems.put("value",0);
                    productLineProcessList.add(quotationProcessItems);
                }
            }
            return productLineProcessList;
        }
    },

    CLAIM{
        @Override
        public List<Map<String, Object>> getOrganizationLevelProcessInformationItem(LineOfBusinessEnum lineOfBusinessId) {
            List<Map<String,Object>> productLineProcessList = Lists.newArrayList();
            for (ProductLineProcessType productLineProcessType : ProductLineProcessType.values()){
                if (!Arrays.asList(ProductLineProcessType.LAPSE,ProductLineProcessType.TYPE,ProductLineProcessType.CHARGES,ProductLineProcessType.INTEREST).contains(productLineProcessType)) {
                    Map<String, Object> quotationProcessItems = Maps.newLinkedHashMap();
                    quotationProcessItems.put("productLineProcessItem", productLineProcessType);
                    quotationProcessItems.put("value", 0);
                    productLineProcessList.add(quotationProcessItems);
                }
            }
            return productLineProcessList;
        }
    },
    POLICY_FEE{
        @Override
        public List<Map<String, Object>> getOrganizationLevelProcessInformationItem(LineOfBusinessEnum lineOfBusinessId) {
            List<Map<String,Object>> productLineProcessList = Lists.newArrayList();
            for (PolicyFeeProcessType policyFeeProcessType : PolicyFeeProcessType.values()){
                Map<String,Object> quotationProcessItems = Maps.newLinkedHashMap();
                quotationProcessItems.put("policyFeeProcessType", policyFeeProcessType);
                quotationProcessItems.put("policyFee",0);
                productLineProcessList.add(quotationProcessItems);
            }
            return productLineProcessList;
        }
    },
    MINIMUM_LIMIT{
        @Override
        public List<Map<String, Object>> getOrganizationLevelProcessInformationItem(LineOfBusinessEnum lineOfBusinessId) {
            List<Map<String,Object>> productLineProcessList = Lists.newArrayList();
            for (PolicyProcessMinimumLimitType policyProcessMinimumLimitType : PolicyProcessMinimumLimitType.values()){
                Map<String,Object> quotationProcessItems = Maps.newLinkedHashMap();
                quotationProcessItems.put("policyProcessMinimumLimitType", policyProcessMinimumLimitType);
                quotationProcessItems.put("value",0);
                productLineProcessList.add(quotationProcessItems);
            }
            return productLineProcessList;
        }
    },
    PREMIUM_FOLLOW_UP{
        @Override
        public List<Map<String, Object>> getOrganizationLevelProcessInformationItem(LineOfBusinessEnum lineOfBusinessId) {
            List<Map<String,Object>> productLineProcessList = Lists.newArrayList();
            for (ProductLineProcessType productLineProcessType : ProductLineProcessType.values()){
                if (Arrays.asList(ProductLineProcessType.FIRST_REMAINDER, ProductLineProcessType.SECOND_REMAINDER, ProductLineProcessType.LAPSE).contains(productLineProcessType)){
                    Map<String,Object> quotationProcessItems = Maps.newLinkedHashMap();
                    quotationProcessItems.put("productLineProcessItem",productLineProcessType);
                    quotationProcessItems.put("value",0);
                    productLineProcessList.add(quotationProcessItems);
                }
            }
            return productLineProcessList;
        }
    },
    MODAL_FACTOR{
        @Override
        public List<Map<String, Object>> getOrganizationLevelProcessInformationItem(LineOfBusinessEnum lineOfBusinessId) {
            List<Map<String,Object>> organizationFactorList = Lists.newArrayList();
            for (ModalFactorItem modalFactorItem : ModalFactorItem.values()){
                Map<String,Object> quotationProcessItems = Maps.newLinkedHashMap();
                quotationProcessItems.put("modalFactorItem", modalFactorItem);
                quotationProcessItems.put("value",0);
                organizationFactorList.add(quotationProcessItems);
            }
            return organizationFactorList;
        }
    },
    DISCOUNT_FACTOR{
        @Override
        public List<Map<String, Object>> getOrganizationLevelProcessInformationItem(LineOfBusinessEnum lineOfBusinessId) {
            List<Map<String,Object>> organizationFactorList = Lists.newArrayList();
            for (DiscountFactorItem discountFactorItem : DiscountFactorItem.values()){
                Map<String,Object> quotationProcessItems = Maps.newLinkedHashMap();
                quotationProcessItems.put("discountFactorItem", discountFactorItem);
                quotationProcessItems.put("value",0);
                organizationFactorList.add(quotationProcessItems);
            }
            return organizationFactorList;
        }
    },
    SERVICE_TAX{
        @Override
        public List<Map<String, Object>> getOrganizationLevelProcessInformationItem(LineOfBusinessEnum lineOfBusinessId) {
            List<Map<String,Object>> organizationFactorList = Lists.newArrayList();
            for (Tax tax : Tax.values()){
                Map<String,Object> quotationProcessItems = Maps.newLinkedHashMap();
                quotationProcessItems.put("tax", tax);
                quotationProcessItems.put("value",0);
                organizationFactorList.add(quotationProcessItems);
            }
            return organizationFactorList;
        }
    };

    public abstract List<Map<String, Object>> getOrganizationLevelProcessInformationItem(LineOfBusinessEnum lineOfBusinessId);

}
