package com.pla.core.domain.service;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pla.core.domain.model.Admin;
import com.pla.core.domain.model.generalinformation.*;
import com.pla.core.dto.*;
import com.pla.publishedlanguage.domain.model.PremiumFrequency;
import com.pla.sharedkernel.domain.model.*;
import com.pla.sharedkernel.identifier.LineOfBusinessEnum;
import org.nthdimenzion.common.AppConstants;
import org.nthdimenzion.utils.UtilValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static org.nthdimenzion.utils.UtilValidator.isEmpty;

/**
 * Created by Admin on 4/1/2015.
 */
@Service
public class GeneralInformationService {

    private AdminRoleAdapter adminRoleAdapter;
    private ObjectMapper objectMapper;
    private MongoTemplate mongoTemplate;

    @Autowired
    public GeneralInformationService(AdminRoleAdapter adminRoleAdapter, MongoTemplate mongoTemplate) {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JodaModule());
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.setVisibilityChecker(objectMapper.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withCreatorVisibility(JsonAutoDetect.Visibility.ANY));
        this.adminRoleAdapter = adminRoleAdapter;
        this.mongoTemplate = mongoTemplate;
    }

    public boolean createProductLineInformation(LineOfBusinessEnum lineOfBusinessId, UserDetails userDetails, GeneralInformationDto generalInformationDto) {
        Admin admin = adminRoleAdapter.userToAdmin(userDetails);
        List<Map<ProductLineProcessType,Integer>> quotationProcessItem =   transformProductLine(generalInformationDto.getQuotationProcessItems());
        List<Map<ProductLineProcessType,Integer>> enrollmentProcessItem =   transformProductLine(generalInformationDto.getEnrollmentProcessItems());
        List<Map<ProductLineProcessType,Integer>> endorsementProcessItem =   transformProductLine(generalInformationDto.getEndorsementProcessItems());
        List<Map<ProductLineProcessType,Integer>> claimProcessItem =   transformProductLine(generalInformationDto.getClaimProcessItems());
        List<Map<ProductLineProcessType,Integer>> reinstatementProcessItem =   transformProductLine(generalInformationDto.getReinstatementProcessItems());
        List<Map<ProductLineProcessType,Integer>> maturityProcessItem =   transformProductLine(generalInformationDto.getMaturityProcessItems());
        List<Map<ProductLineProcessType,Integer>> surrenderProcessItem =   transformProductLine(generalInformationDto.getSurrenderProcessItems());
        Map<PremiumFrequency, List<Map<ProductLineProcessType,Integer>>> premiumFollowUpFrequencyItems =  transformPremiumFrequencyFollowUp(generalInformationDto.getPremiumFollowUpFrequency());
        List<Map<PolicyFeeProcessType,Integer>> policyFeeProcess  = transformProductLineFeeProcess(generalInformationDto.getPolicyFeeProcessItems());
        List<Map<PolicyProcessMinimumLimitType,Integer>>  minimumLimitProcess =  transformProductLineMinimumLimitProcess(generalInformationDto.getPolicyProcessMinimumLimitItems());
        List<Map<ModalFactorItem, BigDecimal>> modalFactorItems =  transformModalFactorItem(generalInformationDto.getModelFactorItems());
        List<Map<DiscountFactorItem, BigDecimal>> discountFactorItems =  transformDiscountFactorItem(generalInformationDto.getDiscountFactorItems());
        ProductLineGeneralInformation productLineGeneralInformation = admin.createProductLineGeneralInformation(lineOfBusinessId, quotationProcessItem,enrollmentProcessItem,reinstatementProcessItem,endorsementProcessItem,claimProcessItem,policyFeeProcess,minimumLimitProcess,surrenderProcessItem,maturityProcessItem,
                premiumFollowUpFrequencyItems,modalFactorItems,discountFactorItems,generalInformationDto.getAgeLoadingFactor(),generalInformationDto.getMoratoriumPeriod(),generalInformationDto.getSurrenderCharges(),generalInformationDto.getReinstatementInterest() );
        mongoTemplate.save(productLineGeneralInformation);
        return AppConstants.SUCCESS;
    }

    public List<Map<ProductLineProcessType,Integer>> transformProductLine(List<ProductLineProcessItemDto> productLineProcessItemDtos){
        List<Map<ProductLineProcessType,Integer>> productLineProcessList = Lists.newArrayList();
        for (ProductLineProcessItemDto productLineProcessItemDto :  productLineProcessItemDtos){
            Map<ProductLineProcessType,Integer> processTypeIntegerMap = Maps.newLinkedHashMap();
            processTypeIntegerMap.put(productLineProcessItemDto.getProductLineProcessItem(),productLineProcessItemDto.getValue());
            productLineProcessList.add(processTypeIntegerMap);
        }
        return productLineProcessList;
    }

    public List<Map<PolicyFeeProcessType,Integer>> transformProductLineFeeProcess(List<PolicyFeeProcessItemDto> policyFeeProcessItemDtos){
        List<Map<PolicyFeeProcessType,Integer>> productLineProcessList = Lists.newArrayList();
        for (PolicyFeeProcessItemDto policyFeeProcessItemDto : policyFeeProcessItemDtos){
            Map<PolicyFeeProcessType,Integer> processTypeIntegerMap = Maps.newLinkedHashMap();
            processTypeIntegerMap.put(policyFeeProcessItemDto.getPolicyFeeProcessType(), policyFeeProcessItemDto.getPolicyFee());
            productLineProcessList.add(processTypeIntegerMap);
        }
        return productLineProcessList;
    }

    public List<Map<PolicyProcessMinimumLimitType,Integer>> transformProductLineMinimumLimitProcess(List<PolicyProcessMinimumLimitItemDto> policyProcessMinimumLimitItemDtos){
        List<Map<PolicyProcessMinimumLimitType,Integer>> productLineProcessList = Lists.newArrayList();
        for (PolicyProcessMinimumLimitItemDto policyProcessMinimumLimitItemDto : policyProcessMinimumLimitItemDtos){
            Map<PolicyProcessMinimumLimitType,Integer> policyProcessMinimumLimit = Maps.newLinkedHashMap();
            policyProcessMinimumLimit.put(policyProcessMinimumLimitItemDto.getPolicyProcessMinimumLimitType(),policyProcessMinimumLimitItemDto.getValue());
            productLineProcessList.add(policyProcessMinimumLimit);
        }
        return productLineProcessList;
    }

    public Map<PremiumFrequency, List<Map<ProductLineProcessType,Integer>>> transformPremiumFrequencyFollowUp(List<PremiumFrequencyFollowUpDto> premiumFrequencyFollowUpDtos){
        Map<PremiumFrequency, List<Map<ProductLineProcessType,Integer>>> premiumFrequencyFollowUp = Maps.newLinkedHashMap();
        for (PremiumFrequencyFollowUpDto premiumFrequencyFollowUpDto : premiumFrequencyFollowUpDtos){
            List<Map<ProductLineProcessType,Integer>> productLineProcessItem =  transformProductLine(premiumFrequencyFollowUpDto.getPremiumFollowUpFrequencyItems());
            premiumFrequencyFollowUp.put(premiumFrequencyFollowUpDto.getPremiumFrequency(),productLineProcessItem);
        }
        return premiumFrequencyFollowUp;
    }


    public boolean createOrganizationInformation(GeneralInformationDto generalInformationDto, UserDetails userDetails) {
        Admin admin = adminRoleAdapter.userToAdmin(userDetails);
        List<Map<ModalFactorItem, BigDecimal>> modelFactorItems =  transformModalFactorItem(generalInformationDto.getModelFactorItems());
        List<Map<DiscountFactorItem, BigDecimal>> discountFactorItems =  transformDiscountFactorItem(generalInformationDto.getDiscountFactorItems());
        Map<Tax,BigDecimal> serviceTax = Maps.newLinkedHashMap();
        serviceTax.put(generalInformationDto.getServiceTax().getTax(),generalInformationDto.getServiceTax().getValue());
        OrganizationGeneralInformation organizationGeneralInformation = admin.createOrganizationGeneralInformation(modelFactorItems, discountFactorItems, serviceTax);
        mongoTemplate.save(organizationGeneralInformation);
        return AppConstants.SUCCESS;
    }

    public Boolean updateOrganizationInformation(GeneralInformationDto generalInformationDto, UserDetails userDetails) {
        Admin admin = adminRoleAdapter.userToAdmin(userDetails);
        Query findGeneralInformation = new Query();
        Update update = new Update();
        findGeneralInformation.addCriteria(Criteria.where("organizationInformationId").is(generalInformationDto.getOrganizationInformationId()));
        OrganizationGeneralInformation organizationGeneralInformation = mongoTemplate.findOne(findGeneralInformation, OrganizationGeneralInformation.class);
        checkArgument(organizationGeneralInformation != null);
        List<Map<ModalFactorItem, BigDecimal>>  modalFactorItem =  transformModalFactorItem(generalInformationDto.getModelFactorItems());
        List<Map<DiscountFactorItem, BigDecimal>>  discountFactorItem =  transformDiscountFactorItem(generalInformationDto.getDiscountFactorItems());
        Map<Tax,BigDecimal> serviceTax = Maps.newLinkedHashMap();
        serviceTax.put(generalInformationDto.getServiceTax().getTax(),generalInformationDto.getServiceTax().getValue());
        organizationGeneralInformation = admin.updateOrganizationInformation(organizationGeneralInformation,modalFactorItem,discountFactorItem ,serviceTax);
        update.set("modelFactorItems", organizationGeneralInformation.getModelFactorItems());
        update.set("discountFactorItems", organizationGeneralInformation.getDiscountFactorItems());
        update.set("serviceTax", organizationGeneralInformation.getServiceTax());
        mongoTemplate.updateFirst(findGeneralInformation, update, OrganizationGeneralInformation.class);
        return AppConstants.SUCCESS;
    }

    public Boolean updateProductLineInformation(GeneralInformationDto generalInformationDto, UserDetails userDetails) {
        Admin admin = adminRoleAdapter.userToAdmin(userDetails);
        Update update = new Update();
        Query findGeneralInformation = new Query();
        findGeneralInformation.addCriteria(Criteria.where("productLineInformationId").is(generalInformationDto.getProductLineInformationId()));
        ProductLineGeneralInformation productLineGeneralInformation = mongoTemplate.findOne(findGeneralInformation, ProductLineGeneralInformation.class);
        checkArgument(productLineGeneralInformation != null);
        List<Map<ProductLineProcessType,Integer>> quotationProcessItem = transformProductLine(generalInformationDto.getQuotationProcessItems());
        List<Map<ProductLineProcessType,Integer>> enrollmentProcessItem = transformProductLine(generalInformationDto.getEnrollmentProcessItems());
        List<Map<ProductLineProcessType,Integer>> endorsementProcessItem = transformProductLine(generalInformationDto.getEndorsementProcessItems());
        List<Map<ProductLineProcessType,Integer>> claimProcessItem = transformProductLine(generalInformationDto.getClaimProcessItems());
        List<Map<ProductLineProcessType,Integer>> reinstatementProcessItem = transformProductLine(generalInformationDto.getReinstatementProcessItems());
        List<Map<ProductLineProcessType,Integer>> maturityProcessItem = transformProductLine(generalInformationDto.getMaturityProcessItems());
        List<Map<ProductLineProcessType,Integer>> surrenderProcessItem = transformProductLine(generalInformationDto.getSurrenderProcessItems());
        List<Map<PolicyFeeProcessType,Integer>> policyFeeProcess  = transformProductLineFeeProcess(generalInformationDto.getPolicyFeeProcessItems());
        List<Map<PolicyProcessMinimumLimitType,Integer>>  minimumLimitProcess =  transformProductLineMinimumLimitProcess(generalInformationDto.getPolicyProcessMinimumLimitItems());
        Map<PremiumFrequency, List<Map<ProductLineProcessType,Integer>>>  premiumFrequencyFollowUp =  transformPremiumFrequencyFollowUp(generalInformationDto.getPremiumFollowUpFrequency());
        List<Map<ModalFactorItem, BigDecimal>> modalFactorItems  = transformModalFactorItem(generalInformationDto.getModelFactorItems());
        List<Map<DiscountFactorItem, BigDecimal>> discountFactorItems = transformDiscountFactorItem(generalInformationDto.getDiscountFactorItems());
        SurrenderCharges surrenderCharges = generalInformationDto.getSurrenderCharges();
        ReinstatementInterest reinstatementInterest =  generalInformationDto.getReinstatementInterest();
        productLineGeneralInformation = admin.updateProductLineInformation(productLineGeneralInformation, quotationProcessItem,enrollmentProcessItem,reinstatementProcessItem,endorsementProcessItem,claimProcessItem,policyFeeProcess,minimumLimitProcess,surrenderProcessItem,maturityProcessItem,premiumFrequencyFollowUp,modalFactorItems,discountFactorItems,generalInformationDto.getAgeLoadingFactor(), generalInformationDto.getMoratoriumPeriod(),surrenderCharges,reinstatementInterest);
        productLineGeneralInformation.withThresholdSumAssured(generalInformationDto.getThresholdSumAssured());
        update = updateProductLineInformation(update, productLineGeneralInformation);
        mongoTemplate.updateFirst(findGeneralInformation, update, ProductLineGeneralInformation.class);
        return AppConstants.SUCCESS;
    }

    private Update updateProductLineInformation(Update update, ProductLineGeneralInformation updatedProductLineInformation) {
        update.set("quotationProcessInformation", updatedProductLineInformation.getQuotationProcessInformation());
        update.set("enrollmentProcessInformation", updatedProductLineInformation.getEnrollmentProcessInformation());
        update.set("reinstatementProcessInformation", updatedProductLineInformation.getReinstatementProcessInformation());
        update.set("endorsementProcessInformation", updatedProductLineInformation.getEndorsementProcessInformation());
        update.set("claimProcessInformation", updatedProductLineInformation.getClaimProcessInformation());
        update.set("policyFeeProcessInformation", updatedProductLineInformation.getPolicyFeeProcessInformation());
        update.set("policyProcessMinimumLimit", updatedProductLineInformation.getPolicyProcessMinimumLimit());
        update.set("surrenderProcessInformation", updatedProductLineInformation.getSurrenderProcessInformation());
        update.set("maturityProcessInformation", updatedProductLineInformation.getMaturityProcessInformation());
        update.set("premiumFollowUpFrequency", updatedProductLineInformation.getPremiumFollowUpFrequency());
        update.set("modalFactorProcessInformation", updatedProductLineInformation.getModalFactorProcessInformation());
        update.set("discountFactorProcessInformation", updatedProductLineInformation.getDiscountFactorProcessInformation());
        update.set("ageLoadingFactor", updatedProductLineInformation.getAgeLoadingFactor());
        update.set("moratoriumPeriod", updatedProductLineInformation.getMoratoriumPeriod());
        update.set("thresholdSumAssured", updatedProductLineInformation.getThresholdSumAssured());
        update.set("reinstatementInterest", updatedProductLineInformation.getReinstatementInterest());
        update.set("surrenderCharges", updatedProductLineInformation.getSurrenderCharges());
        return update;
    }

    List<Map<ModalFactorItem, BigDecimal>> transformModalFactorItem(List<ModalFactorInformationDto> modalFactorInformationDtos){
        List<Map<ModalFactorItem, BigDecimal>> modalFactorItems = Lists.newArrayList();
        for (ModalFactorInformationDto modalFactorInformationDto : modalFactorInformationDtos){
            Map<ModalFactorItem, BigDecimal> modalFactorItemMap = Maps.newLinkedHashMap();
            modalFactorItemMap.put(modalFactorInformationDto.getModalFactorItem(),modalFactorInformationDto.getValue());
            modalFactorItems.add(modalFactorItemMap);
        }
        return modalFactorItems;
    }

    List<Map<DiscountFactorItem, BigDecimal>> transformDiscountFactorItem(List<DiscountFactorInformationDto> modalFactorInformationDtos){
        List<Map<DiscountFactorItem, BigDecimal>> discountFactorItems = Lists.newArrayList();
        for (DiscountFactorInformationDto discountFactorInformationDto : modalFactorInformationDtos){
            Map<DiscountFactorItem, BigDecimal> discountFactorItemMap = Maps.newLinkedHashMap();
            discountFactorItemMap.put(discountFactorInformationDto.getDiscountFactorItem(),discountFactorInformationDto.getValue());
            discountFactorItems.add(discountFactorItemMap);
        }
        return discountFactorItems;
    }

    public List<Map> getAllOrganizationInformation(){
        List<Map> organizationInformation = findAllOrganizationInformation();
        if (isEmpty(organizationInformation)){
            return populateOrganizationGeneralInformationData();
        }
        return organizationInformation;
    }

    public List<Map> getProductLineInformation(){
        List<Map> productLineInformation = findAllProductLineInformation();
        List<Map> productLineInformationList = Lists.newArrayList();
        if (isEmpty(productLineInformation)){
            productLineInformationList.add(getProductLineInformationByLineOfBusiness(LineOfBusinessEnum.GROUP_HEALTH));
            productLineInformationList.add(getProductLineInformationByLineOfBusiness(LineOfBusinessEnum.GROUP_LIFE));
            productLineInformationList.add(getProductLineInformationByLineOfBusiness(LineOfBusinessEnum.INDIVIDUAL_LIFE));
            return productLineInformationList;
        }
        for (Map productLineInformationMap : productLineInformation){
            Map<String,Object> productLineInformationByBusinessId = Maps.newLinkedHashMap();
            productLineInformationByBusinessId.put("productLine",productLineInformationMap.get("productLine"));
            productLineInformationByBusinessId.put("productLineInformationId", productLineInformationMap.get("productLineInformationId"));
            Map quotationMap = (Map) productLineInformationMap.get("quotationProcessInformation");
            productLineInformationByBusinessId.put("quotationProcessItems", ((List) quotationMap.get("quotationProcessItems")).parallelStream().map(new ProcessTransformation("Quotation")).collect(Collectors.toList()));
            Map enrollmentMap = (Map) productLineInformationMap.get("enrollmentProcessInformation");
            productLineInformationByBusinessId.put("enrollmentProcessItems",((List)enrollmentMap.get("enrollmentProcessItems")).parallelStream().map(new ProcessTransformation("New business/Enrollment")).collect(Collectors.toList()));
            Map reinstatementMap = (Map) productLineInformationMap.get("reinstatementProcessInformation");
            productLineInformationByBusinessId.put("reinstatementProcessItems",((List)reinstatementMap.get("reinstatementProcessItems")).parallelStream().map(new ProcessTransformation("Reinstatement")).collect(Collectors.toList()));
            Map  endorsementMap = (Map) productLineInformationMap.get("endorsementProcessInformation");
            productLineInformationByBusinessId.put("endorsementProcessItems",((List)endorsementMap.get("endorsementProcessItems")).parallelStream().map(new ProcessTransformation("Endorsement")).collect(Collectors.toList()));
            Map claimMap = (Map) productLineInformationMap.get("claimProcessInformation");
            productLineInformationByBusinessId.put("claimProcessItems",((List)claimMap.get("claimProcessItems")).parallelStream().map(new ProcessTransformation("Claim")).collect(Collectors.toList()));
            Map policyFeeMap = (Map) productLineInformationMap.get("policyFeeProcessInformation");
            productLineInformationByBusinessId.put("policyFeeProcessItems",((List) policyFeeMap.get("policyFeeProcessItems")).parallelStream().map(new Function<Map, Map>() {
                @Override
                public Map apply(Map map) {
                    PolicyFeeProcessType policyFeeProcessType = PolicyFeeProcessType.valueOf((String)map.get("policyFeeProcessType"));
                    map.put("description",policyFeeProcessType.getDescription());
                    map.put("fullDescription",policyFeeProcessType.getDescription());
                    return map;
                }
            }).collect(Collectors.toList()));
            Map minimumLimitMap = (Map) productLineInformationMap.get("policyProcessMinimumLimit");
            productLineInformationByBusinessId.put("policyProcessMinimumLimitItems",((List<Map>)minimumLimitMap.get("policyProcessMinimumLimitItems")).parallelStream().map(new Function<Map, Map>() {
                @Override
                public Map apply(Map map) {
                    PolicyProcessMinimumLimitType policyProcessMinimumLimitType = PolicyProcessMinimumLimitType.valueOf((String)map.get("policyProcessMinimumLimitType"));
                    map.put("description",policyProcessMinimumLimitType.getDescription());
                    map.put("fullDescription",policyProcessMinimumLimitType.getDescription());
                    return map;
                }
            }).collect(Collectors.toList()));
            Map surrenderMap  = (Map) productLineInformationMap.get("surrenderProcessInformation");
            productLineInformationByBusinessId.put("surrenderProcessItems", ((List) surrenderMap.get("surrenderProcessItems")).parallelStream().map(new ProcessTransformation("Surrender")).collect(Collectors.toList()));
            Map  maturityMap = (Map) productLineInformationMap.get("maturityProcessInformation");
            productLineInformationByBusinessId.put("maturityProcessItems",((List)maturityMap.get("maturityProcessItems")).parallelStream().map(new ProcessTransformation("Maturity")).collect(Collectors.toList()));
            productLineInformationByBusinessId.put("premiumFollowUpFrequency", ((List<Map>)productLineInformationMap.get("premiumFollowUpFrequency")).parallelStream().map(new Function<Map, Map>() {
                @Override
                public Map apply(Map map) {
                    map.put("premiumFollowUpFrequencyItems",((List) map.get("premiumFollowUpFrequencyItems")).parallelStream().map(new ProcessTransformation("Premium Frequency Follow Up")).collect(Collectors.toList()));
                    return map;
                }
            }).collect(Collectors.toList()));
            Map discountFactorMap = (Map) productLineInformationMap.get("discountFactorProcessInformation");
            productLineInformationByBusinessId.put("discountFactorItems",((List)discountFactorMap.get("discountFactorItems")).parallelStream().map(new DiscountFactorTransformation()).collect(Collectors.toList()));
            Map  modalFactorMap = (Map) productLineInformationMap.get("modalFactorProcessInformation");
            productLineInformationByBusinessId.put("modelFactorItems",((List)modalFactorMap.get("modelFactorItems")).parallelStream().map(new ModalFactorTransformation()).collect(Collectors.toList()));
            productLineInformationByBusinessId.put("ageLoadingFactor",productLineInformationMap.get("ageLoadingFactor"));
            productLineInformationByBusinessId.put("moratoriumPeriod",productLineInformationMap.get("moratoriumPeriod"));
            productLineInformationByBusinessId.put("thresholdSumAssured",productLineInformationMap.get("thresholdSumAssured"));
            productLineInformationByBusinessId.put("reinstatementInterest",productLineInformationMap.get("reinstatementInterest"));
            productLineInformationByBusinessId.put("surrenderCharges",productLineInformationMap.get("surrenderCharges"));
            productLineInformationList.add(productLineInformationByBusinessId);
        }
        return transformProductLineInformation(productLineInformationList);
    }

    private List<Map> transformProductLineInformation(List<Map> productLineInformation) {
        List<String> strings = Lists.newArrayList();
        for (Map map : productLineInformation){
            strings.add((String) map.get("productLine"));
        }
        if (!strings.contains(LineOfBusinessEnum.GROUP_HEALTH.name())) {
            productLineInformation.add(getProductLineInformationByLineOfBusiness(LineOfBusinessEnum.GROUP_HEALTH));
        }
        if (!strings.contains(LineOfBusinessEnum.GROUP_LIFE.name())) {
            productLineInformation.add(getProductLineInformationByLineOfBusiness(LineOfBusinessEnum.GROUP_LIFE));
        }
        if (!strings.contains(LineOfBusinessEnum.INDIVIDUAL_LIFE.name())) {
            productLineInformation.add(getProductLineInformationByLineOfBusiness(LineOfBusinessEnum.INDIVIDUAL_LIFE));
        }
        return productLineInformation;
    }

    private List<Map> findAllProductLineInformation() {
        List<Map> productLineInformationList = new ArrayList<Map>();
        List<ProductLineGeneralInformation> productLineInformation =  mongoTemplate.findAll(ProductLineGeneralInformation.class, "product_line_information");
        for (ProductLineGeneralInformation productLineGeneralInformation : productLineInformation) {
            Map plan = objectMapper.convertValue(productLineGeneralInformation, Map.class);
            productLineInformationList.add(plan);
        }
        return productLineInformationList;
    }

    private List<Map> findAllOrganizationInformation() {
        List<Map> organizationInformationList = new ArrayList<Map>();
        List<OrganizationGeneralInformation> organizationGeneralInformations =  mongoTemplate.findAll(OrganizationGeneralInformation.class, "organization_information");
        for (OrganizationGeneralInformation organizationGeneralInformation : organizationGeneralInformations) {
            Map organizationInformation = objectMapper.convertValue(organizationGeneralInformation, Map.class);
            organizationInformation.put("modelFactorItems", ((List<Map>) organizationInformation.get("modelFactorItems")).parallelStream().map(new ModalFactorTransformation()).collect(Collectors.toList()));
            organizationInformation.put("discountFactorItems", ((List<Map>) organizationInformation.get("discountFactorItems")).parallelStream().map(new DiscountFactorTransformation()).collect(Collectors.toList()));
            Map serviceTaxMap  =((Map) organizationInformation.get("serviceTax"));
            serviceTaxMap.put("description","Service Tax");
            serviceTaxMap.put("fullDescription","Service Tax");
            organizationInformation.put("serviceTax",serviceTaxMap);
            organizationInformationList.add(organizationInformation);
        }
        return organizationInformationList;
    }


    public Map getProductLineInformationByLineOfBusiness(LineOfBusinessEnum lineOfBusinessEnum){
        Map productLineMap  = Maps.newLinkedHashMap();
        productLineMap.put("productLine", lineOfBusinessEnum);
        productLineMap.put("productLineInformationId",null);
        productLineMap = getProductLineGeneralInformation(productLineMap,lineOfBusinessEnum);
        return productLineMap;
    }

    private List populateOrganizationGeneralInformationData(){
        List list = Lists.newArrayList();
        Map organizationInformation = Maps.newLinkedHashMap();
        organizationInformation.put("modelFactorItems", GeneralInformationProcessItem.MODAL_FACTOR.getOrganizationLevelProcessInformationItem(LineOfBusinessEnum.GROUP_HEALTH));
        organizationInformation.put("discountFactorItems", GeneralInformationProcessItem.DISCOUNT_FACTOR.getOrganizationLevelProcessInformationItem(LineOfBusinessEnum.GROUP_HEALTH));
        organizationInformation.put("serviceTax", GeneralInformationProcessItem.SERVICE_TAX.getOrganizationLevelProcessInformationItem(LineOfBusinessEnum.GROUP_HEALTH).get(0));
        list.add(organizationInformation);
        return list;
    }

    private Map getProductLineGeneralInformation(Map productLineInformationMap, LineOfBusinessEnum lineOfBusinessId) {
        productLineInformationMap.put("quotationProcessItems", GeneralInformationProcessItem.QUOTATION.getOrganizationLevelProcessInformationItem(lineOfBusinessId));
        productLineInformationMap.put("enrollmentProcessItems",  GeneralInformationProcessItem.ENROLLMENT.getOrganizationLevelProcessInformationItem(lineOfBusinessId));
        productLineInformationMap.put("reinstatementProcessItems",  GeneralInformationProcessItem.REINSTATEMENT.getOrganizationLevelProcessInformationItem(lineOfBusinessId));
        productLineInformationMap.put("endorsementProcessItems", GeneralInformationProcessItem.ENDORSEMENT.getOrganizationLevelProcessInformationItem(lineOfBusinessId));
        productLineInformationMap.put("claimProcessItems", GeneralInformationProcessItem.CLAIM.getOrganizationLevelProcessInformationItem(lineOfBusinessId));
        productLineInformationMap.put("policyFeeProcessItems",GeneralInformationProcessItem.POLICY_FEE.getOrganizationLevelProcessInformationItem(lineOfBusinessId));
        productLineInformationMap.put("policyProcessMinimumLimitItems", GeneralInformationProcessItem.MINIMUM_LIMIT.getOrganizationLevelProcessInformationItem(lineOfBusinessId));
        productLineInformationMap.put("surrenderProcessItems", GeneralInformationProcessItem.SURRENDER.getOrganizationLevelProcessInformationItem(lineOfBusinessId));
        productLineInformationMap.put("maturityProcessItems", GeneralInformationProcessItem.MATURITY.getOrganizationLevelProcessInformationItem(lineOfBusinessId));
        productLineInformationMap.put("premiumFollowUpFrequency", transformPremiumFollowUp(lineOfBusinessId));
        productLineInformationMap.put("modelFactorItems",GeneralInformationProcessItem.MODAL_FACTOR.getOrganizationLevelProcessInformationItem(lineOfBusinessId));
        productLineInformationMap.put("discountFactorItems",GeneralInformationProcessItem.DISCOUNT_FACTOR.getOrganizationLevelProcessInformationItem(lineOfBusinessId));
        productLineInformationMap.put("ageLoadingFactor", ImmutableMap.of("age", 0, "loadingFactor", 0));
        productLineInformationMap.put("moratoriumPeriod", 0);
        productLineInformationMap.put("thresholdSumAssured", BigDecimal.ZERO);
        return productLineInformationMap;
    }

    public List<Map<String, Object>> transformPremiumFollowUp(LineOfBusinessEnum lineOfBusinessId) {
        List<Map<String,Object>> productLineProcessList = GeneralInformationProcessItem.PREMIUM_FOLLOW_UP.getOrganizationLevelProcessInformationItem(lineOfBusinessId);
        List<Map<String,Object>> premiumFrequencyFollowUpList = Lists.newArrayList();
        for (PremiumFrequency premiumFrequency : PremiumFrequency.values()){
            Map<String,Object> premiumFrequencyMap = Maps.newLinkedHashMap();
            premiumFrequencyMap.put("premiumFrequency", premiumFrequency);
            premiumFrequencyMap.put("premiumFollowUpFrequencyItems",productLineProcessList);
            premiumFrequencyFollowUpList.add(premiumFrequencyMap);
        }
        return premiumFrequencyFollowUpList;
    }

    public ProductLineGeneralInformation findProductLineInformationByLineOfBusinessId(LineOfBusinessEnum lineOfBusinessEnum)    {
        Query findGeneralInformation = new Query();
        findGeneralInformation.addCriteria(Criteria.where("productLine").is(lineOfBusinessEnum));
        ProductLineGeneralInformation productLineGeneralInformation = mongoTemplate.findOne(findGeneralInformation, ProductLineGeneralInformation.class);
        return productLineGeneralInformation;
    }

    public BigDecimal getTheServiceTaxAmount(){
        List<OrganizationGeneralInformation> organizationGeneralInformationList = mongoTemplate.findAll(OrganizationGeneralInformation.class);
        if(UtilValidator.isNotEmpty(organizationGeneralInformationList)){
            OrganizationGeneralInformation organizationGeneralInformation = organizationGeneralInformationList.get(0);
            return organizationGeneralInformation.getTheServiceTaxAmount();
        }
        return BigDecimal.ZERO;
    }

    private class ProcessTransformation implements Function<Map, Map> {
        private String process;
        public ProcessTransformation(String process) {
            this.process = process;
        }
        @Override
        public Map apply(Map map) {
            String productLineProcessType = (String) map.get("productLineProcessItem");
            map.put("description",ProductLineProcessType.valueOf(productLineProcessType).toString());
            map.put("fullDescription",ProductLineProcessType.valueOf(productLineProcessType).getFullDescriptionByProcess(process, ""));
            return map;
        }
    }

    private class ModalFactorTransformation implements Function<Map,Map> {
        @Override
        public Map apply(Map map) {
            ModalFactorItem modalFactorItem = ModalFactorItem.valueOf((String)map.get("modalFactorItem"));
            map.put("description",modalFactorItem.getDescription());
            map.put("fullDescription",modalFactorItem.getDescription());
            return map;
        }
    }

    private class DiscountFactorTransformation implements Function<Map,Map> {
        @Override
        public Map apply(Map map) {
            DiscountFactorItem discountFactorItem = DiscountFactorItem.valueOf((String)map.get("discountFactorItem"));
            map.put("description",discountFactorItem.getDescription());
            map.put("fullDescription",discountFactorItem.getDescription());
            return map;
        }
    }
}
