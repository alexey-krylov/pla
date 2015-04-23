package com.pla.core.domain.service;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pla.core.domain.model.Admin;
import com.pla.core.domain.model.generalinformation.OrganizationGeneralInformation;
import com.pla.core.domain.model.generalinformation.ProductLineGeneralInformation;
import com.pla.core.dto.*;
import com.pla.sharedkernel.domain.model.*;
import com.pla.sharedkernel.identifier.LineOfBusinessId;
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
    private MongoTemplate springMongoTemplate;

    @Autowired
    public GeneralInformationService(AdminRoleAdapter adminRoleAdapter, MongoTemplate springMongoTemplate) {
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
        this.springMongoTemplate = springMongoTemplate;
    }

    public boolean createProductLineInformation(LineOfBusinessId lineOfBusinessId, UserDetails userDetails, GeneralInformationDto generalInformationDto) {
        Admin admin = adminRoleAdapter.userToAdmin(userDetails);
        List<Map<ProductLineProcessType,Integer>> quotationProcessItem =   transformProductLine(generalInformationDto.getQuotationProcessItems());
        List<Map<ProductLineProcessType,Integer>> enrollmentProcessItem =   transformProductLine(generalInformationDto.getEnrollmentProcessItems());
        List<Map<ProductLineProcessType,Integer>> endorsementProcessItem =   transformProductLine(generalInformationDto.getEndorsementProcessItems());
        List<Map<ProductLineProcessType,Integer>> claimProcessItem =   transformProductLine(generalInformationDto.getClaimProcessItems());
        List<Map<ProductLineProcessType,Integer>> reinstatementProcessItem =   transformProductLine(generalInformationDto.getReinstatementProcessItems());
        List<Map<ProductLineProcessType,Integer>> maturityProcessItem =   transformProductLine(generalInformationDto.getMaturityProcessItems());
        List<Map<ProductLineProcessType,Integer>> surrenderProcessItem =   transformProductLine(generalInformationDto.getSurrenderProcessItems());
        List<Map<PolicyFeeProcessType,Integer>> policyFeeProcess  = transformProductLineFeeProcess(generalInformationDto.getPolicyFeeProcessItems());
        List<PolicyProcessMinimumLimitItemDto>  minimumLimitProcess =  transformProductLineMinimumLimitProcess(generalInformationDto.getPolicyProcessMinimumLimitItems());
        ProductLineGeneralInformation productLineGeneralInformation = admin.createProductLineGeneralInformation(lineOfBusinessId, quotationProcessItem,enrollmentProcessItem,reinstatementProcessItem,endorsementProcessItem,claimProcessItem,policyFeeProcess,minimumLimitProcess,surrenderProcessItem,maturityProcessItem);
        springMongoTemplate.save(productLineGeneralInformation);
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

    public List<PolicyProcessMinimumLimitItemDto> transformProductLineMinimumLimitProcess(List<PolicyProcessMinimumLimitItemDto> policyFeeProcessItemDtos){
        List<PolicyProcessMinimumLimitItemDto> productLineProcessList = Lists.newArrayList();
        for (PolicyProcessMinimumLimitItemDto policyProcessMinimumLimitItemDto : policyFeeProcessItemDtos){
            productLineProcessList.add(policyProcessMinimumLimitItemDto);
        }
        return productLineProcessList;
    }


    public boolean createOrganizationInformation(GeneralInformationDto generalInformationDto, UserDetails userDetails) {
        Admin admin = adminRoleAdapter.userToAdmin(userDetails);
        List<Map<ModalFactorItem, BigDecimal>> modelFactorItems =  transformModalFactorItem(generalInformationDto.getModelFactorItems());
        List<Map<DiscountFactorItem, BigDecimal>> discountFactorItems =  transformDiscountFactorItem(generalInformationDto.getDiscountFactorItems());
        Map<Tax,BigDecimal> serviceTax = Maps.newLinkedHashMap();
        serviceTax.put(generalInformationDto.getServiceTax().getTax(),generalInformationDto.getServiceTax().getValue());
        OrganizationGeneralInformation organizationGeneralInformation = admin.createOrganizationGeneralInformation(modelFactorItems, discountFactorItems, serviceTax);
        springMongoTemplate.save(organizationGeneralInformation);
        return AppConstants.SUCCESS;
    }

    public Boolean updateOrganizationInformation(GeneralInformationDto generalInformationDto, UserDetails userDetails) {
        Admin admin = adminRoleAdapter.userToAdmin(userDetails);
        Query findGeneralInformation = new Query();
        Update update = new Update();
        findGeneralInformation.addCriteria(Criteria.where("organizationInformationId").is(generalInformationDto.getOrganizationInformationId()));
        OrganizationGeneralInformation organizationGeneralInformation = springMongoTemplate.findOne(findGeneralInformation, OrganizationGeneralInformation.class);
        checkArgument(organizationGeneralInformation != null);
        List<Map<ModalFactorItem, BigDecimal>>  modalFactorItem =  transformModalFactorItem(generalInformationDto.getModelFactorItems());
        List<Map<DiscountFactorItem, BigDecimal>>  discountFactorItem =  transformDiscountFactorItem(generalInformationDto.getDiscountFactorItems());
        Map<Tax,BigDecimal> serviceTax = Maps.newLinkedHashMap();
        serviceTax.put(generalInformationDto.getServiceTax().getTax(),generalInformationDto.getServiceTax().getValue());
        organizationGeneralInformation = admin.updateOrganizationInformation(organizationGeneralInformation,modalFactorItem,discountFactorItem ,serviceTax);
        update.set("modelFactorItems", organizationGeneralInformation.getModelFactorItems());
        update.set("discountFactorItems", organizationGeneralInformation.getDiscountFactorItems());
        update.set("serviceTax", organizationGeneralInformation.getServiceTax());
        springMongoTemplate.updateFirst(findGeneralInformation, update, OrganizationGeneralInformation.class);
        return AppConstants.SUCCESS;
    }

    public Boolean updateProductLineInformation(GeneralInformationDto generalInformationDto, UserDetails userDetails) {
        Admin admin = adminRoleAdapter.userToAdmin(userDetails);
        Update update = new Update();
        Query findGeneralInformation = new Query();
        findGeneralInformation.addCriteria(Criteria.where("productLineInformationId").is(generalInformationDto.getProductLineInformationId()));
        ProductLineGeneralInformation productLineGeneralInformation = springMongoTemplate.findOne(findGeneralInformation, ProductLineGeneralInformation.class);
        checkArgument(productLineGeneralInformation != null);
        List<Map<ProductLineProcessType,Integer>> quotationProcessItem = transformProductLine(generalInformationDto.getQuotationProcessItems());
        List<Map<ProductLineProcessType,Integer>> enrollmentProcessItem = transformProductLine(generalInformationDto.getEnrollmentProcessItems());
        List<Map<ProductLineProcessType,Integer>> endorsementProcessItem = transformProductLine(generalInformationDto.getEndorsementProcessItems());
        List<Map<ProductLineProcessType,Integer>> claimProcessItem = transformProductLine(generalInformationDto.getClaimProcessItems());
        List<Map<ProductLineProcessType,Integer>> reinstatementProcessItem =transformProductLine(generalInformationDto.getReinstatementProcessItems());
        List<Map<ProductLineProcessType,Integer>> maturityProcessItem = transformProductLine(generalInformationDto.getMaturityProcessItems());
        List<Map<ProductLineProcessType,Integer>> surrenderProcessItem = transformProductLine(generalInformationDto.getSurrenderProcessItems());
        List<Map<PolicyFeeProcessType,Integer>> policyFeeProcess  = transformProductLineFeeProcess(generalInformationDto.getPolicyFeeProcessItems());
        List<PolicyProcessMinimumLimitItemDto>  minimumLimitProcess =  transformProductLineMinimumLimitProcess(generalInformationDto.getPolicyProcessMinimumLimitItems());
        productLineGeneralInformation = admin.updateProductLineInformation(productLineGeneralInformation,  quotationProcessItem,enrollmentProcessItem,reinstatementProcessItem,endorsementProcessItem,claimProcessItem,policyFeeProcess,minimumLimitProcess,surrenderProcessItem,maturityProcessItem);
        update = updateProductLineInformation(update, productLineGeneralInformation);
        springMongoTemplate.updateFirst(findGeneralInformation, update, ProductLineGeneralInformation.class);
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
        return update;
    }

    private List<Map<ModalFactorItem, BigDecimal>> transformModalFactorItem(List<ModalFactorInformationDto> modalFactorInformationDtos){
        List<Map<ModalFactorItem, BigDecimal>> modalFactorItems = Lists.newArrayList();
        for (ModalFactorInformationDto modalFactorInformationDto : modalFactorInformationDtos){
            Map<ModalFactorItem, BigDecimal> modalFactorItemMap = Maps.newLinkedHashMap();
            modalFactorItemMap.put(modalFactorInformationDto.getModalFactorItem(),modalFactorInformationDto.getValue());
            modalFactorItems.add(modalFactorItemMap);
        }
        return modalFactorItems;
    }

    private List<Map<DiscountFactorItem, BigDecimal>> transformDiscountFactorItem(List<DiscountFactorInformationDto> modalFactorInformationDtos){
        List<Map<DiscountFactorItem, BigDecimal>> discountFactorItems = Lists.newArrayList();
        for (DiscountFactorInformationDto discountFactorInformationDto : modalFactorInformationDtos){
            Map<DiscountFactorItem, BigDecimal> discountFactorItemMap = Maps.newLinkedHashMap();
            discountFactorItemMap.put(discountFactorInformationDto.getDiscountFactorItem(),discountFactorInformationDto.getValue());
            discountFactorItems.add(discountFactorItemMap);
        }
        return discountFactorItems;
    }


    public List<GeneralInformationProcessDto> getOrganizationProcessItems(){
        List<GeneralInformationProcessDto> organizationProcessList = Lists.newArrayList();
        organizationProcessList = getModalFactorItems(organizationProcessList);
        organizationProcessList = getDiscountFactorItems(organizationProcessList);
        organizationProcessList = getServiceTaxItem(organizationProcessList);
        return organizationProcessList;
    }

    public List<GeneralInformationProcessDto> getProductLineProcessItems(){
        List<GeneralInformationProcessDto> productLineProcessList = Lists.newArrayList();
        productLineProcessList = getPolicyFeeProcessType(productLineProcessList);
        productLineProcessList = getProductLineProcessType(productLineProcessList);
        productLineProcessList = getPolicyProcessMinimumLimitType(productLineProcessList);
        return productLineProcessList;
    }

    private List<GeneralInformationProcessDto> getModalFactorItems(List<GeneralInformationProcessDto> organizationProcessList){
        for (ModalFactorItem modalFactorItem : ModalFactorItem.values()){
            organizationProcessList.add(transformProductLineProcessItem(modalFactorItem.name(),modalFactorItem.getDescription(),modalFactorItem.getFullDescription()));
        }
        return organizationProcessList;
    }

    private List<GeneralInformationProcessDto> getServiceTaxItem(List<GeneralInformationProcessDto> organizationProcessList){
        organizationProcessList.add(transformProductLineProcessItem(Tax.SERVICE_TAX.name(), Tax.SERVICE_TAX.getDescription(), Tax.SERVICE_TAX.getFullDescription()));
        return organizationProcessList;
    }

    private List<GeneralInformationProcessDto> getDiscountFactorItems(List<GeneralInformationProcessDto> organizationProcessList){
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
            productLineInformationList.add(productLineInformationByBusinessId);
        }
        return transformProductLineInformation(productLineInformationList);
    }

    private List<Map> transformProductLineInformation(List<Map> productLineInformation){
        List<String> strings = Lists.newArrayList();
        for (Map map : productLineInformation){
            strings.add((String) map.get("productLine"));
        }
        if (!strings.contains(LineOfBusinessId.GROUP_HEALTH.name())){
            productLineInformation.add(getGroupHealthProductLineInformation());
        }
        if (!strings.contains(LineOfBusinessId.GROUP_INSURANCE.name())){
            productLineInformation.add(getGroupInsuranceProductLineInformation());
        }
        if (!strings.contains(LineOfBusinessId.INDIVIDUAL_INSURANCE.name())){
            productLineInformation.add(getIndividualInsuranceProductLineInformation());
        }
        return productLineInformation;
    }

    private List<Map> findAllProductLineInformation() {
        List<Map> productLineInformationList = new ArrayList<Map>();
        List<ProductLineGeneralInformation> productLineInformation =  springMongoTemplate.findAll(ProductLineGeneralInformation.class, "product_line_information");
        for (ProductLineGeneralInformation productLineGeneralInformation : productLineInformation) {
            Map plan = objectMapper.convertValue(productLineGeneralInformation, Map.class);
            productLineInformationList.add(plan);
        }
        return productLineInformationList;
    }

    private List<Map> findAllOrganizationInformation() {
        List<Map> organizationInformationList = new ArrayList<Map>();
        List<OrganizationGeneralInformation> organizationGeneralInformations =  springMongoTemplate.findAll(OrganizationGeneralInformation.class, "organization_information");
        for (OrganizationGeneralInformation organizationGeneralInformation : organizationGeneralInformations) {
            Map plan = objectMapper.convertValue(organizationGeneralInformation, Map.class);
            organizationInformationList.add(plan);
        }
        return organizationInformationList;
    }

    public Map getGroupHealthProductLineInformation(){
        Map productLineMap  = Maps.newLinkedHashMap();
        productLineMap.put("productLine",LineOfBusinessId.GROUP_HEALTH);
        productLineMap.put("productLineInformationId",null);
        productLineMap = getProductLineGeneralInformation(productLineMap);
        return productLineMap;
    }

    public Map getGroupInsuranceProductLineInformation(){
        Map insuranceMap  = Maps.newLinkedHashMap();
        insuranceMap.put("productLine",LineOfBusinessId.GROUP_INSURANCE);
        insuranceMap.put("productLineInformationId",null);
        insuranceMap = getProductLineGeneralInformation(insuranceMap);
        return insuranceMap;
    }

    public Map getIndividualInsuranceProductLineInformation(){
        Map individualInsurance  = Maps.newLinkedHashMap();
        individualInsurance.put("productLine",LineOfBusinessId.INDIVIDUAL_INSURANCE);
        individualInsurance.put("productLineInformationId",null);
        individualInsurance = getProductLineGeneralInformation(individualInsurance);
        return individualInsurance;
    }

    private List populateOrganizationGeneralInformationData(){
        List list = Lists.newArrayList();
        GeneralInformationDto generalInformationDto = getOrganizationGeneralInformation();
        list.add(generalInformationDto);
        return list;
    }


    private Map getProductLineGeneralInformation(Map productLineInformationMap){
        productLineInformationMap.put("quotationProcessItems", populateProcessItems());
        productLineInformationMap.put("enrollmentProcessItems", populateProcessItems());
        productLineInformationMap.put("reinstatementProcessItems", populateProcessItems());
        productLineInformationMap.put("endorsementProcessItems", populateProcessItems());
        productLineInformationMap.put("claimProcessItems", populateClaimProcessItems());
        productLineInformationMap.put("policyFeeProcessItems",populatePolicyFeeProcessData());
        productLineInformationMap.put("policyProcessMinimumLimitItems", populateMinimumLimitProcessData());
        productLineInformationMap.put("surrenderProcessItems", populateProcessItems());
        productLineInformationMap.put("maturityProcessItems", populateProcessItems());
        return productLineInformationMap;
    }

    private GeneralInformationDto getOrganizationGeneralInformation(){
        GeneralInformationDto generalInformationDto = new GeneralInformationDto();
        generalInformationDto.setModelFactorItems(populateModalFactorDiscount());
        generalInformationDto.setDiscountFactorItems(populateDiscountFactorDiscount());
        ServiceTaxDto serviceTaxDto = new ServiceTaxDto();
        serviceTaxDto.setValue(BigDecimal.ZERO);
        serviceTaxDto.setTax(Tax.SERVICE_TAX);
        generalInformationDto.setServiceTax(serviceTaxDto);
        return generalInformationDto;
    }

    private List<Map<String,Object>> populateProcessItems(){
        List<Map<String,Object>> productLineProcessList = Lists.newArrayList();
        Map<String,Object> quotationProcessItems = Maps.newLinkedHashMap();
        quotationProcessItems.put("productLineProcessItem",ProductLineProcessType.PURGE_TIME_PERIOD);
        quotationProcessItems.put("value",0);
        productLineProcessList.add(quotationProcessItems);
        quotationProcessItems = Maps.newLinkedHashMap();
        quotationProcessItems.put("productLineProcessItem", ProductLineProcessType.FIRST_REMAINDER);
        quotationProcessItems.put("value", 0);
        productLineProcessList.add(quotationProcessItems);
        quotationProcessItems = Maps.newLinkedHashMap();
        quotationProcessItems.put("productLineProcessItem",ProductLineProcessType.NO_OF_REMAINDER);
        quotationProcessItems.put("value", 0);
        productLineProcessList.add(quotationProcessItems);
        quotationProcessItems = Maps.newLinkedHashMap();
        quotationProcessItems.put("productLineProcessItem",ProductLineProcessType.GAP);
        quotationProcessItems.put("value", 0);
        productLineProcessList.add(quotationProcessItems);
        quotationProcessItems = Maps.newLinkedHashMap();
        quotationProcessItems.put("productLineProcessItem",ProductLineProcessType.CLOSURE);
        quotationProcessItems.put("value", 0);
        productLineProcessList.add(quotationProcessItems);
        return productLineProcessList;
    }
    private List<Map<String,Object>> populateClaimProcessItems(){
        List<Map<String,Object>> productLineProcessList = Lists.newArrayList();
        Map<String,Object> quotationProcessItems = Maps.newLinkedHashMap();
        quotationProcessItems.put("productLineProcessItem",ProductLineProcessType.PURGE_TIME_PERIOD);
        quotationProcessItems.put("value",0);
        productLineProcessList.add(quotationProcessItems);
        quotationProcessItems = Maps.newLinkedHashMap();
        quotationProcessItems.put("productLineProcessItem",ProductLineProcessType.FIRST_REMAINDER);
        quotationProcessItems.put("value", 0);
        productLineProcessList.add(quotationProcessItems);
        quotationProcessItems = Maps.newLinkedHashMap();
        quotationProcessItems.put("productLineProcessItem",ProductLineProcessType.NO_OF_REMAINDER);
        quotationProcessItems.put("value", 0);
        productLineProcessList.add(quotationProcessItems);
        quotationProcessItems = Maps.newLinkedHashMap();
        quotationProcessItems.put("productLineProcessItem", ProductLineProcessType.GAP);
        quotationProcessItems.put("value", 0);
        productLineProcessList.add(quotationProcessItems);
        quotationProcessItems = Maps.newLinkedHashMap();
        quotationProcessItems.put("productLineProcessItem", ProductLineProcessType.CLOSURE);
        quotationProcessItems.put("value", 0);
        productLineProcessList.add(quotationProcessItems);
        quotationProcessItems = Maps.newLinkedHashMap();
        quotationProcessItems.put("productLineProcessItem",ProductLineProcessType.EARLY_DEATH_CRITERIA);
        quotationProcessItems.put("value", 0);
        productLineProcessList.add(quotationProcessItems);
        return productLineProcessList;
    }

    private List<DiscountFactorInformationDto> populateDiscountFactorDiscount(){
        List<DiscountFactorInformationDto>  discountFactorList = Lists.newArrayList();
        DiscountFactorInformationDto discountFactorInformationDto = new DiscountFactorInformationDto();
        discountFactorInformationDto.setDiscountFactorItem(DiscountFactorItem.ANNUAL);
        discountFactorInformationDto.setValue(BigDecimal.ZERO);
        discountFactorList.add(discountFactorInformationDto);
        discountFactorInformationDto = new DiscountFactorInformationDto();
        discountFactorInformationDto.setDiscountFactorItem(DiscountFactorItem.SEMI_ANNUAL);
        discountFactorInformationDto.setValue(BigDecimal.ZERO);
        discountFactorInformationDto = new DiscountFactorInformationDto();
        discountFactorInformationDto.setDiscountFactorItem(DiscountFactorItem.QUARTERLY);
        discountFactorInformationDto.setValue(BigDecimal.ZERO);
        discountFactorList.add(discountFactorInformationDto);
        return discountFactorList;
    }

    private List<ModalFactorInformationDto> populateModalFactorDiscount(){
        List<ModalFactorInformationDto>  modalFactorList = Lists.newArrayList();
        ModalFactorInformationDto modalFactorInformationDto = new ModalFactorInformationDto();
        modalFactorInformationDto.setModalFactorItem(ModalFactorItem.SEMI_ANNUAL);
        modalFactorInformationDto.setValue(BigDecimal.ZERO);
        modalFactorList.add(modalFactorInformationDto);
        modalFactorInformationDto = new ModalFactorInformationDto();
        modalFactorInformationDto.setModalFactorItem(ModalFactorItem.QUARTERLY);
        modalFactorInformationDto.setValue(BigDecimal.ZERO);
        modalFactorInformationDto = new ModalFactorInformationDto();
        modalFactorInformationDto.setModalFactorItem(ModalFactorItem.MONTHLY);
        modalFactorInformationDto.setValue(BigDecimal.ZERO);
        modalFactorList.add(modalFactorInformationDto);
        return modalFactorList;
    }

    private List<Map<String,Object>> populatePolicyFeeProcessData(){
        List<Map<String,Object>>  processFeeList = Lists.newArrayList();
        Map<String,Object> policyFeeProcessItems = Maps.newLinkedHashMap();
        policyFeeProcessItems.put("policyFeeProcessType",PolicyFeeProcessType.ANNUAL);
        policyFeeProcessItems.put("policyFee",0);
        processFeeList.add(policyFeeProcessItems);
        policyFeeProcessItems = Maps.newLinkedHashMap();
        policyFeeProcessItems.put("policyFeeProcessType",PolicyFeeProcessType.SEMI_ANNUAL);
        policyFeeProcessItems.put("policyFee",0);
        processFeeList.add(policyFeeProcessItems);
        policyFeeProcessItems = Maps.newLinkedHashMap();
        policyFeeProcessItems.put("policyFeeProcessType",PolicyFeeProcessType.QUARTERLY);
        policyFeeProcessItems.put("policyFee",0);
        processFeeList.add(policyFeeProcessItems);
        policyFeeProcessItems = Maps.newLinkedHashMap();
        policyFeeProcessItems.put("policyFeeProcessType",PolicyFeeProcessType.MONTHLY);
        policyFeeProcessItems.put("policyFee",0);
        processFeeList.add(policyFeeProcessItems);
        return processFeeList;
    }


    private List<PolicyProcessMinimumLimitItemDto> populateMinimumLimitProcessData(){
        List<PolicyProcessMinimumLimitItemDto>  policyProcessMinimumLimitItems = Lists.newArrayList();
        PolicyProcessMinimumLimitItemDto policyProcessMinimumLimitItemDto = new PolicyProcessMinimumLimitItemDto();
        policyProcessMinimumLimitItemDto.setPolicyProcessMinimumLimitType(PolicyProcessMinimumLimitType.SEMI_ANNUAL);
        policyProcessMinimumLimitItemDto.setMinimumPremium(0);
        policyProcessMinimumLimitItemDto.setNoOfPersonPerPolicy(0);
        policyProcessMinimumLimitItems.add(policyProcessMinimumLimitItemDto);
        policyProcessMinimumLimitItemDto = new PolicyProcessMinimumLimitItemDto();
        policyProcessMinimumLimitItemDto.setPolicyProcessMinimumLimitType(PolicyProcessMinimumLimitType.ANNUAL);
        policyProcessMinimumLimitItemDto.setMinimumPremium(0);
        policyProcessMinimumLimitItemDto.setNoOfPersonPerPolicy(0);
        policyProcessMinimumLimitItems.add(policyProcessMinimumLimitItemDto);
        return policyProcessMinimumLimitItems;
    }

    public Map<Tax,BigDecimal> populateServiceTax(){
        Map<Tax,BigDecimal> serviceTax = Maps.newLinkedHashMap();
        serviceTax.put(Tax.SERVICE_TAX, null);
        return serviceTax;
    }

}
