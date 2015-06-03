package com.pla.core.domain.service;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pla.core.domain.model.Admin;
import com.pla.core.domain.model.generalinformation.GeneralInformationProcessItem;
import com.pla.core.domain.model.generalinformation.OrganizationGeneralInformation;
import com.pla.core.domain.model.generalinformation.ProductLineGeneralInformation;
import com.pla.core.dto.*;
import com.pla.publishedlanguage.domain.model.PremiumFrequency;
import com.pla.sharedkernel.domain.model.*;
import com.pla.sharedkernel.identifier.LineOfBusinessEnum;
import org.nthdimenzion.common.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
                premiumFollowUpFrequencyItems,modalFactorItems,discountFactorItems);
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
        List<Map<ProductLineProcessType,Integer>> reinstatementProcessItem =transformProductLine(generalInformationDto.getReinstatementProcessItems());
        List<Map<ProductLineProcessType,Integer>> maturityProcessItem = transformProductLine(generalInformationDto.getMaturityProcessItems());
        List<Map<ProductLineProcessType,Integer>> surrenderProcessItem = transformProductLine(generalInformationDto.getSurrenderProcessItems());
        List<Map<PolicyFeeProcessType,Integer>> policyFeeProcess  = transformProductLineFeeProcess(generalInformationDto.getPolicyFeeProcessItems());
        List<Map<PolicyProcessMinimumLimitType,Integer>>  minimumLimitProcess =  transformProductLineMinimumLimitProcess(generalInformationDto.getPolicyProcessMinimumLimitItems());
        Map<PremiumFrequency, List<Map<ProductLineProcessType,Integer>>>  premiumFrequencyFollowUp =  transformPremiumFrequencyFollowUp(generalInformationDto.getPremiumFollowUpFrequency());
        List<Map<ModalFactorItem, BigDecimal>> modalFactorItems  = transformModalFactorItem(generalInformationDto.getModelFactorItems());
        List<Map<DiscountFactorItem, BigDecimal>> discountFactorItems = transformDiscountFactorItem(generalInformationDto.getDiscountFactorItems());
        productLineGeneralInformation = admin.updateProductLineInformation(productLineGeneralInformation,  quotationProcessItem,enrollmentProcessItem,reinstatementProcessItem,endorsementProcessItem,claimProcessItem,policyFeeProcess,minimumLimitProcess,surrenderProcessItem,maturityProcessItem,premiumFrequencyFollowUp,modalFactorItems,discountFactorItems);
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


    public Map<String,List<GeneralInformationProcessDto>> getOrganizationProcessItems(){
        Map<String,List<GeneralInformationProcessDto>> organizationInformationProcess = Maps.newLinkedHashMap();
        organizationInformationProcess.put("modalFactorProcess",getModalFactorItems());
        organizationInformationProcess.put("discountFactorProcess",getDiscountFactorItems());
        organizationInformationProcess.put("serviceTax",getServiceTaxItem());
        return organizationInformationProcess;
    }

    public List<GeneralInformationProcessDto> getProductLineProcessItems(){
        List<GeneralInformationProcessDto> productLineProcessList = Lists.newArrayList();
        productLineProcessList = getPolicyFeeProcessType(productLineProcessList);
        productLineProcessList = getProductLineProcessType(productLineProcessList);
        productLineProcessList = getPolicyProcessMinimumLimitType(productLineProcessList);
        return productLineProcessList;
    }

    private List<GeneralInformationProcessDto> getModalFactorItems(){
        List<GeneralInformationProcessDto> organizationProcessList = Lists.newArrayList();
        for (ModalFactorItem modalFactorItem : ModalFactorItem.values()){
            organizationProcessList.add(transformProductLineProcessItem(modalFactorItem.name(),modalFactorItem.getDescription(),modalFactorItem.getFullDescription()));
        }
        return organizationProcessList;
    }

    private List<GeneralInformationProcessDto> getServiceTaxItem(){
        List<GeneralInformationProcessDto> organizationProcessList = Lists.newArrayList();
        organizationProcessList.add(transformProductLineProcessItem(Tax.SERVICE_TAX.name(), Tax.SERVICE_TAX.getDescription(), Tax.SERVICE_TAX.getFullDescription()));
        return organizationProcessList;
    }

    private List<GeneralInformationProcessDto> getDiscountFactorItems(){
        List<GeneralInformationProcessDto> organizationProcessList = Lists.newArrayList();
        for (DiscountFactorItem discountFactorItem : DiscountFactorItem.values()){
            organizationProcessList.add(transformProductLineProcessItem(discountFactorItem.name(),discountFactorItem.getDescription(),discountFactorItem.getFullDescription()));
        }
        return organizationProcessList;
    }

    private  List<GeneralInformationProcessDto> getPolicyFeeProcessType(List<GeneralInformationProcessDto> productLineProcessList ){
        for(PolicyFeeProcessType policyFeeProcessType : PolicyFeeProcessType.values()){
            productLineProcessList.add(transformProductLineProcessItem(policyFeeProcessType.name(),policyFeeProcessType.getDescription(),policyFeeProcessType.getFullDescription()));
        }
        return productLineProcessList;
    }

    private  List<GeneralInformationProcessDto> getProductLineProcessType(List<GeneralInformationProcessDto> productLineProcessList){
        for(ProductLineProcessType productLineProcessType : ProductLineProcessType.values()){
            productLineProcessList.add(transformProductLineProcessItem(productLineProcessType.name(),productLineProcessType.getDescription(),productLineProcessType.getFullDescription()));
        }
        return productLineProcessList;
    }

    private List<GeneralInformationProcessDto> getPolicyProcessMinimumLimitType(List<GeneralInformationProcessDto> productLineProcessList ){
        for(PolicyProcessMinimumLimitType minimumLimitType :PolicyProcessMinimumLimitType.values()){
            productLineProcessList.add(transformProductLineProcessItem(minimumLimitType.name(),minimumLimitType.getDescription(),minimumLimitType.getFullDescription()));
        }
        return productLineProcessList;
    }

    private GeneralInformationProcessDto transformProductLineProcessItem(String type,String description,String fullDescription){
        GeneralInformationProcessDto generalInformationProcessDto = new GeneralInformationProcessDto();
        generalInformationProcessDto.setType(type);
        generalInformationProcessDto.setDescription(description);
        generalInformationProcessDto.setFullDescription(fullDescription);
        return generalInformationProcessDto;
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
            productLineInformationList.add(getGroupHealthProductLineInformation());
            productLineInformationList.add(getGroupInsuranceProductLineInformation());
            productLineInformationList.add(getIndividualInsuranceProductLineInformation());
            return productLineInformationList;
        }
        for (Map productLineInformationMap: productLineInformation){
            Map<String,Object> productLineInformationByBusinessId = Maps.newLinkedHashMap();
            productLineInformationByBusinessId.put("productLine",productLineInformationMap.get("productLine"));
            productLineInformationByBusinessId.put("productLineInformationId", productLineInformationMap.get("productLineInformationId"));
            Map quotationMap = (Map) productLineInformationMap.get("quotationProcessInformation");
            productLineInformationByBusinessId.put("quotationProcessItems", quotationMap.get("quotationProcessItems"));
            Map enrollmentMap = (Map) productLineInformationMap.get("enrollmentProcessInformation");
            productLineInformationByBusinessId.put("enrollmentProcessItems",enrollmentMap.get("enrollmentProcessItems") );
            Map reinstatementMap = (Map) productLineInformationMap.get("reinstatementProcessInformation");
            productLineInformationByBusinessId.put("reinstatementProcessItems",reinstatementMap.get("reinstatementProcessItems") );
            Map  endorsementMap = (Map) productLineInformationMap.get("endorsementProcessInformation");
            productLineInformationByBusinessId.put("endorsementProcessItems",endorsementMap.get("endorsementProcessItems") );
            Map claimMap = (Map) productLineInformationMap.get("claimProcessInformation");
            productLineInformationByBusinessId.put("claimProcessItems",claimMap.get("claimProcessItems"));
            Map policyFeeMap = (Map) productLineInformationMap.get("policyFeeProcessInformation");
            productLineInformationByBusinessId.put("policyFeeProcessItems", policyFeeMap.get("policyFeeProcessItems"));
            Map minimumLimitMap = (Map) productLineInformationMap.get("policyProcessMinimumLimit");
            productLineInformationByBusinessId.put("policyProcessMinimumLimitItems",minimumLimitMap.get("policyProcessMinimumLimitItems"));
            Map surrenderMap  = (Map) productLineInformationMap.get("surrenderProcessInformation");
            productLineInformationByBusinessId.put("surrenderProcessItems",surrenderMap.get("surrenderProcessItems"));
            Map  maturityMap = (Map) productLineInformationMap.get("maturityProcessInformation");
            productLineInformationByBusinessId.put("maturityProcessItems",maturityMap.get("maturityProcessItems"));
            productLineInformationByBusinessId.put("premiumFollowUpFrequency", productLineInformationMap.get("premiumFollowUpFrequency"));
            Map  discountFactorMap = (Map) productLineInformationMap.get("discountFactorProcessInformation");
            productLineInformationByBusinessId.put("discountFactorItems",discountFactorMap.get("discountFactorItems"));
            Map  modalFactorMap = (Map) productLineInformationMap.get("modalFactorProcessInformation");
            productLineInformationByBusinessId.put("modelFactorItems",modalFactorMap.get("modelFactorItems"));

            productLineInformationList.add(productLineInformationByBusinessId);
        }
        return transformProductLineInformation(productLineInformationList);
    }

    private List<Map> transformProductLineInformation(List<Map> productLineInformation){
        List<String> strings = Lists.newArrayList();
        for (Map map : productLineInformation){
            strings.add((String) map.get("productLine"));
        }
        if (!strings.contains(LineOfBusinessEnum.GROUP_HEALTH.name())) {
            productLineInformation.add(getGroupHealthProductLineInformation());
        }
        if (!strings.contains(LineOfBusinessEnum.GROUP_LIFE.name())) {
            productLineInformation.add(getGroupInsuranceProductLineInformation());
        }
        if (!strings.contains(LineOfBusinessEnum.INDIVIDUAL_LIFE.name())) {
            productLineInformation.add(getIndividualInsuranceProductLineInformation());
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
            Map plan = objectMapper.convertValue(organizationGeneralInformation, Map.class);
            organizationInformationList.add(plan);
        }
        return organizationInformationList;
    }

    public Map getGroupHealthProductLineInformation(){
        Map productLineMap  = Maps.newLinkedHashMap();
        productLineMap.put("productLine", LineOfBusinessEnum.GROUP_HEALTH);
        productLineMap.put("productLineInformationId",null);
        productLineMap = getProductLineGeneralInformation(productLineMap, LineOfBusinessEnum.GROUP_HEALTH);
        return productLineMap;
    }

    public Map getGroupInsuranceProductLineInformation(){
        Map insuranceMap  = Maps.newLinkedHashMap();
        insuranceMap.put("productLine", LineOfBusinessEnum.GROUP_LIFE);
        insuranceMap.put("productLineInformationId",null);
        insuranceMap = getProductLineGeneralInformation(insuranceMap, LineOfBusinessEnum.GROUP_LIFE);
        return insuranceMap;
    }

    public Map getIndividualInsuranceProductLineInformation(){
        Map individualInsurance  = Maps.newLinkedHashMap();
        individualInsurance.put("productLine", LineOfBusinessEnum.INDIVIDUAL_LIFE);
        individualInsurance.put("productLineInformationId",null);
        individualInsurance = getProductLineGeneralInformation(individualInsurance, LineOfBusinessEnum.INDIVIDUAL_LIFE);
        return individualInsurance;
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
        productLineInformationMap.put("quotationProcessItems", GeneralInformationProcessItem.DEFAULT.getOrganizationLevelProcessInformationItem(lineOfBusinessId));
        productLineInformationMap.put("enrollmentProcessItems",  GeneralInformationProcessItem.DEFAULT.getOrganizationLevelProcessInformationItem(lineOfBusinessId));
        productLineInformationMap.put("reinstatementProcessItems",  GeneralInformationProcessItem.REINSTATEMENT.getOrganizationLevelProcessInformationItem(lineOfBusinessId));
        productLineInformationMap.put("endorsementProcessItems", GeneralInformationProcessItem.DEFAULT.getOrganizationLevelProcessInformationItem(lineOfBusinessId));
        productLineInformationMap.put("claimProcessItems", GeneralInformationProcessItem.CLAIM.getOrganizationLevelProcessInformationItem(lineOfBusinessId));
        productLineInformationMap.put("policyFeeProcessItems",GeneralInformationProcessItem.POLICY_FEE.getOrganizationLevelProcessInformationItem(lineOfBusinessId));
        productLineInformationMap.put("policyProcessMinimumLimitItems", GeneralInformationProcessItem.MINIMUM_LIMIT.getOrganizationLevelProcessInformationItem(lineOfBusinessId));
        productLineInformationMap.put("surrenderProcessItems", GeneralInformationProcessItem.SURRENDER.getOrganizationLevelProcessInformationItem(lineOfBusinessId));
        productLineInformationMap.put("maturityProcessItems", GeneralInformationProcessItem.DEFAULT.getOrganizationLevelProcessInformationItem(lineOfBusinessId));
        productLineInformationMap.put("premiumFollowUpFrequency", transformPremiumFollowUp(lineOfBusinessId));
        productLineInformationMap.put("modelFactorItems",GeneralInformationProcessItem.MODAL_FACTOR.getOrganizationLevelProcessInformationItem(lineOfBusinessId));
        productLineInformationMap.put("discountFactorItems",GeneralInformationProcessItem.DISCOUNT_FACTOR.getOrganizationLevelProcessInformationItem(lineOfBusinessId));
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
}
