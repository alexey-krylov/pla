package com.pla.sharedkernel.domain.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

/**
 * Created by Admin on 4/23/2015.
 */
public enum GeneralInformationProcessItem {

    QUOTATION{
        @Override
        public List<Map<String, Object>> getOrganizationLevelProcessInformationItem() {
            List<Map<String,Object>> productLineProcessList = Lists.newArrayList();
            for (ProductLineProcessType productLineProcessType : ProductLineProcessType.values()){
                if (!ProductLineProcessType.EARLY_DEATH_CRITERIA.equals(productLineProcessType)){
                    Map<String,Object> quotationProcessItems = Maps.newLinkedHashMap();
                    quotationProcessItems.put("productLineProcessItem",productLineProcessType);
                    quotationProcessItems.put("value",0);
                    productLineProcessList.add(quotationProcessItems);
                }
            }
            return productLineProcessList;
        }
    },
    ENROLLMENT{
        @Override
        public List<Map<String, Object>> getOrganizationLevelProcessInformationItem() {
            List<Map<String,Object>> productLineProcessList = Lists.newArrayList();
            for (ProductLineProcessType productLineProcessType : ProductLineProcessType.values()){
                if (!ProductLineProcessType.EARLY_DEATH_CRITERIA.equals(productLineProcessType)){
                    Map<String,Object> quotationProcessItems = Maps.newLinkedHashMap();
                    quotationProcessItems.put("productLineProcessItem",productLineProcessType);
                    quotationProcessItems.put("value",0);
                    productLineProcessList.add(quotationProcessItems);
                }
            }
            return productLineProcessList;
        }
    },
    ENDORSEMENT{
        @Override
        public List<Map<String, Object>> getOrganizationLevelProcessInformationItem() {
            List<Map<String,Object>> productLineProcessList = Lists.newArrayList();
            for (ProductLineProcessType productLineProcessType : ProductLineProcessType.values()){
                if (!ProductLineProcessType.EARLY_DEATH_CRITERIA.equals(productLineProcessType)){
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
        public List<Map<String, Object>> getOrganizationLevelProcessInformationItem() {
            List<Map<String,Object>> productLineProcessList = Lists.newArrayList();
            for (ProductLineProcessType productLineProcessType : ProductLineProcessType.values()){
                if (!ProductLineProcessType.EARLY_DEATH_CRITERIA.equals(productLineProcessType)){
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
        public List<Map<String, Object>> getOrganizationLevelProcessInformationItem() {
            List<Map<String,Object>> productLineProcessList = Lists.newArrayList();
            for (ProductLineProcessType productLineProcessType : ProductLineProcessType.values()){
                Map<String,Object> quotationProcessItems = Maps.newLinkedHashMap();
                quotationProcessItems.put("productLineProcessItem",productLineProcessType);
                quotationProcessItems.put("value",0);
                productLineProcessList.add(quotationProcessItems);
            }
            return productLineProcessList;
        }
    },
    POLICY_FEE{
        @Override
        public List<Map<String, Object>> getOrganizationLevelProcessInformationItem() {
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
        public List<Map<String, Object>> getOrganizationLevelProcessInformationItem() {
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
    SURRENDER{
        @Override
        public List<Map<String, Object>> getOrganizationLevelProcessInformationItem() {
            List<Map<String,Object>> productLineProcessList = Lists.newArrayList();
            for (ProductLineProcessType productLineProcessType : ProductLineProcessType.values()){
                if (!ProductLineProcessType.EARLY_DEATH_CRITERIA.equals(productLineProcessType)){
                    Map<String,Object> quotationProcessItems = Maps.newLinkedHashMap();
                    quotationProcessItems.put("productLineProcessItem",productLineProcessType);
                    quotationProcessItems.put("value",0);
                    productLineProcessList.add(quotationProcessItems);
                }
            }
            return productLineProcessList;
        }
    },
    MATURITY{
        @Override
        public List<Map<String, Object>> getOrganizationLevelProcessInformationItem() {
            List<Map<String,Object>> productLineProcessList = Lists.newArrayList();
            for (ProductLineProcessType productLineProcessType : ProductLineProcessType.values()){
                if (!ProductLineProcessType.EARLY_DEATH_CRITERIA.equals(productLineProcessType)){
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
        public List<Map<String, Object>> getOrganizationLevelProcessInformationItem() {
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
        public List<Map<String, Object>> getOrganizationLevelProcessInformationItem() {
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
        public List<Map<String, Object>> getOrganizationLevelProcessInformationItem() {
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

    public abstract List<Map<String,Object>> getOrganizationLevelProcessInformationItem();

}
