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
import com.pla.core.dto.GeneralInformationDto;
import com.pla.core.dto.GeneralInformationProcessDto;
import com.pla.core.dto.PolicyProcessMinimumLimitItemDto;
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
        ProductLineGeneralInformation productLineGeneralInformation = admin.createProductLineGeneralInformation(lineOfBusinessId, generalInformationDto);
        springMongoTemplate.save(productLineGeneralInformation);
        return AppConstants.SUCCESS;
    }

    public boolean createOrganizationInformation(List<Map<ModalFactorItem, BigDecimal>> modelFactorItems, List<Map<DiscountFactorItem, BigDecimal>> discountFactorItems, Map<Tax, BigDecimal> serviceTax, UserDetails userDetails) {
        Admin admin = adminRoleAdapter.userToAdmin(userDetails);
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
        organizationGeneralInformation = admin.updateOrganizationInformation(organizationGeneralInformation, generalInformationDto);
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
        productLineGeneralInformation = admin.updateProductLineInformation(productLineGeneralInformation, generalInformationDto);
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
        if (isEmpty(productLineInformation)){
            return populateProductLineGeneralInformationData();
        }
        List<Map> productLineInformationList = Lists.newArrayList();
        for (Map productLineInformationMap: productLineInformation){
            Map<String,Object> productLineInformationByBusinessId = Maps.newLinkedHashMap();
            List listOfProcess = Lists.newArrayList();
            productLineInformationByBusinessId.put("productLine",productLineInformationMap.get("productLine"));
            productLineInformationByBusinessId.put("productLineInformationId", productLineInformationMap.get("productLineInformationId"));
            listOfProcess.add(productLineInformationMap.get("quotationProcessInformation"));
            listOfProcess.add(productLineInformationMap.get("enrollmentProcessInformation"));
            listOfProcess.add(productLineInformationMap.get("reinstatementProcessInformation"));
            listOfProcess.add(productLineInformationMap.get("endorsementProcessInformation"));
            listOfProcess.add(productLineInformationMap.get("claimProcessInformation"));
            listOfProcess.add(productLineInformationMap.get("policyFeeProcessInformation"));
            listOfProcess.add(productLineInformationMap.get("policyProcessMinimumLimit"));
            listOfProcess.add(productLineInformationMap.get("surrenderProcessInformation"));
            listOfProcess.add(productLineInformationMap.get("maturityProcessInformation"));
            productLineInformationByBusinessId.put("processType",listOfProcess);
            productLineInformationList.add(productLineInformationByBusinessId);
        }
        return productLineInformationList;
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

    private List populateProductLineGeneralInformationData(){
        List list = Lists.newArrayList();
        GeneralInformationDto generalInformationDto = getProductLineGeneralInformation();
        generalInformationDto.setLineOfBusinessId(LineOfBusinessId.GROUP_HEALTH);
        list.add(generalInformationDto);

        generalInformationDto = getProductLineGeneralInformation();
        generalInformationDto.setLineOfBusinessId(LineOfBusinessId.GROUP_INSURANCE);
        list.add(generalInformationDto);

        generalInformationDto = getProductLineGeneralInformation();
        generalInformationDto.setLineOfBusinessId(LineOfBusinessId.INDIVIDUAL_INSURANCE);
        list.add(generalInformationDto);
        return list;
    }


    private List populateOrganizationGeneralInformationData(){
        List list = Lists.newArrayList();
        GeneralInformationDto generalInformationDto = getOrganizationGeneralInformation();
        list.add(generalInformationDto);
        return list;
    }


    private GeneralInformationDto getProductLineGeneralInformation(){
        GeneralInformationDto generalInformationDto = new GeneralInformationDto();
        generalInformationDto.setQuotationProcessItems(populateQuotationProcessItems());
        generalInformationDto.setEnrollmentProcessItems(populateEnrollmentProcessItems());
        generalInformationDto.setReinstatementProcessItems(populateReinstatementProcessItems());
        generalInformationDto.setEndorsementProcessItems(populateEndorsementProcessItems());
        generalInformationDto.setClaimProcessItems(populateClaimProcessItems());
        generalInformationDto.setPolicyFeeProcessItems(populatePolicyFeeProcessData());
        generalInformationDto.setPolicyProcessMinimumLimitItems(populateMinimumLimitProcessData());
        generalInformationDto.setSurrenderProcessItems(populateSurrenderProcessItems());
        generalInformationDto.setMaturityProcessItems(populateMaturityProcessItems());
        return generalInformationDto;
    }

    private GeneralInformationDto getOrganizationGeneralInformation(){
        GeneralInformationDto generalInformationDto = new GeneralInformationDto();
        generalInformationDto.setModelFactorItems(populateModalFactorDiscount());
        generalInformationDto.setDiscountFactorItems(populateDiscountFactorDiscount());
        generalInformationDto.setServiceTax(populateServiceTax());
        return generalInformationDto;
    }

    private List<Map<ProductLineProcessType,Integer>> populateQuotationProcessItems(){
        List<Map<ProductLineProcessType,Integer>> productLineProcessList = Lists.newArrayList();
        Map<ProductLineProcessType,Integer> quotationProcessItems = Maps.newLinkedHashMap();
        quotationProcessItems.put(ProductLineProcessType.NO_OF_REMAINDER, 0);
        productLineProcessList.add(quotationProcessItems);
        quotationProcessItems = Maps.newLinkedHashMap();
        quotationProcessItems.put(ProductLineProcessType.GAP, 0);
        productLineProcessList.add(quotationProcessItems);
        quotationProcessItems = Maps.newLinkedHashMap();
        quotationProcessItems.put(ProductLineProcessType.CLOSURE, 0);
        productLineProcessList.add(quotationProcessItems);
        quotationProcessItems = Maps.newLinkedHashMap();
        quotationProcessItems.put(ProductLineProcessType.PURGE_TIME_PERIOD, 0);
        productLineProcessList.add(quotationProcessItems);
        return productLineProcessList;
    }

    private List<Map<ProductLineProcessType,Integer>> populateEnrollmentProcessItems(){
        List<Map<ProductLineProcessType,Integer>> productLineProcessList = Lists.newArrayList();
        Map<ProductLineProcessType,Integer> enrollmentProcessItems = Maps.newLinkedHashMap();
        enrollmentProcessItems.put(ProductLineProcessType.NO_OF_REMAINDER, 0);
        productLineProcessList.add(enrollmentProcessItems);
        enrollmentProcessItems = Maps.newLinkedHashMap();
        enrollmentProcessItems.put(ProductLineProcessType.GAP, 0);
        productLineProcessList.add(enrollmentProcessItems);
        enrollmentProcessItems = Maps.newLinkedHashMap();
        enrollmentProcessItems.put(ProductLineProcessType.CLOSURE, 0);
        productLineProcessList.add(enrollmentProcessItems);
        enrollmentProcessItems = Maps.newLinkedHashMap();
        enrollmentProcessItems.put(ProductLineProcessType.PURGE_TIME_PERIOD, 0);
        productLineProcessList.add(enrollmentProcessItems);
        return productLineProcessList;
    }

    private List<Map<ProductLineProcessType,Integer>> populateEndorsementProcessItems(){
        List<Map<ProductLineProcessType,Integer>> productLineProcessList = Lists.newArrayList();
        Map<ProductLineProcessType,Integer> endorsementProcessItems = Maps.newLinkedHashMap();
        endorsementProcessItems.put(ProductLineProcessType.NO_OF_REMAINDER, 0);
        productLineProcessList.add(endorsementProcessItems);
        endorsementProcessItems = Maps.newLinkedHashMap();
        endorsementProcessItems.put(ProductLineProcessType.GAP, 0);
        productLineProcessList.add(endorsementProcessItems);
        endorsementProcessItems = Maps.newLinkedHashMap();
        endorsementProcessItems.put(ProductLineProcessType.CLOSURE, 0);
        productLineProcessList.add(endorsementProcessItems);
        endorsementProcessItems = Maps.newLinkedHashMap();
        endorsementProcessItems.put(ProductLineProcessType.PURGE_TIME_PERIOD, 0);
        productLineProcessList.add(endorsementProcessItems);
        return productLineProcessList;
    }

    private List<Map<ProductLineProcessType,Integer>> populateClaimProcessItems(){
        List<Map<ProductLineProcessType,Integer>> productLineProcessList = Lists.newArrayList();
        Map<ProductLineProcessType,Integer> claimProcessItems = Maps.newLinkedHashMap();
        claimProcessItems.put(ProductLineProcessType.NO_OF_REMAINDER, 0);
        productLineProcessList.add(claimProcessItems);
        claimProcessItems = Maps.newLinkedHashMap();
        claimProcessItems.put(ProductLineProcessType.GAP, 0);
        productLineProcessList.add(claimProcessItems);
        claimProcessItems = Maps.newLinkedHashMap();
        claimProcessItems.put(ProductLineProcessType.CLOSURE, 0);
        productLineProcessList.add(claimProcessItems);
        claimProcessItems = Maps.newLinkedHashMap();
        claimProcessItems.put(ProductLineProcessType.PURGE_TIME_PERIOD, 0);
        productLineProcessList.add(claimProcessItems);
        claimProcessItems = Maps.newLinkedHashMap();
        claimProcessItems.put(ProductLineProcessType.EARLY_DEATH_CRITERIA, 0);
        productLineProcessList.add(claimProcessItems);
        return productLineProcessList;
    }

    private List<Map<ProductLineProcessType,Integer>> populateReinstatementProcessItems(){
        List<Map<ProductLineProcessType,Integer>> productLineProcessList = Lists.newArrayList();
        Map<ProductLineProcessType,Integer> quotationProcessItems = Maps.newLinkedHashMap();
        quotationProcessItems.put(ProductLineProcessType.NO_OF_REMAINDER, 0);
        productLineProcessList.add(quotationProcessItems);
        quotationProcessItems = Maps.newLinkedHashMap();
        quotationProcessItems.put(ProductLineProcessType.GAP, 0);
        productLineProcessList.add(quotationProcessItems);
        quotationProcessItems = Maps.newLinkedHashMap();
        quotationProcessItems.put(ProductLineProcessType.CLOSURE, 0);
        productLineProcessList.add(quotationProcessItems);
        quotationProcessItems = Maps.newLinkedHashMap();
        quotationProcessItems.put(ProductLineProcessType.PURGE_TIME_PERIOD, 0);
        productLineProcessList.add(quotationProcessItems);
        return productLineProcessList;
    }

    private List<Map<ProductLineProcessType,Integer>> populateSurrenderProcessItems(){
        List<Map<ProductLineProcessType,Integer>> productLineProcessList = Lists.newArrayList();
        Map<ProductLineProcessType,Integer> surrenderProcessItems = Maps.newLinkedHashMap();
        surrenderProcessItems.put(ProductLineProcessType.NO_OF_REMAINDER, 0);
        productLineProcessList.add(surrenderProcessItems);
        surrenderProcessItems = Maps.newLinkedHashMap();
        surrenderProcessItems.put(ProductLineProcessType.GAP, 0);
        productLineProcessList.add(surrenderProcessItems);
        surrenderProcessItems = Maps.newLinkedHashMap();
        surrenderProcessItems.put(ProductLineProcessType.CLOSURE, 0);
        productLineProcessList.add(surrenderProcessItems);
        surrenderProcessItems = Maps.newLinkedHashMap();
        surrenderProcessItems.put(ProductLineProcessType.PURGE_TIME_PERIOD, 0);
        productLineProcessList.add(surrenderProcessItems);
        return productLineProcessList;
    }

    private List<Map<ProductLineProcessType,Integer>> populateMaturityProcessItems(){
        List<Map<ProductLineProcessType,Integer>> productLineProcessList = Lists.newArrayList();
        Map<ProductLineProcessType,Integer> maturityProcessItems = Maps.newLinkedHashMap();
        maturityProcessItems.put(ProductLineProcessType.NO_OF_REMAINDER, 0);
        productLineProcessList.add(maturityProcessItems);
        maturityProcessItems = Maps.newLinkedHashMap();
        maturityProcessItems.put(ProductLineProcessType.GAP, 0);
        productLineProcessList.add(maturityProcessItems);
        maturityProcessItems = Maps.newLinkedHashMap();
        maturityProcessItems.put(ProductLineProcessType.CLOSURE, 0);
        productLineProcessList.add(maturityProcessItems);
        maturityProcessItems = Maps.newLinkedHashMap();
        maturityProcessItems.put(ProductLineProcessType.PURGE_TIME_PERIOD, 0);
        productLineProcessList.add(maturityProcessItems);
        return productLineProcessList;
    }



    private List<Map<DiscountFactorItem, BigDecimal>> populateDiscountFactorDiscount(){
        List<Map<DiscountFactorItem,BigDecimal>>  discountFactorList = Lists.newArrayList();
        Map<DiscountFactorItem,BigDecimal> discountFactorItems = Maps.newLinkedHashMap();
        discountFactorItems.put(DiscountFactorItem.ANNUAL, null);
        discountFactorList.add(discountFactorItems);
        discountFactorItems = Maps.newLinkedHashMap();
        discountFactorItems.put(DiscountFactorItem.SEMI_ANNUAL, null);
        discountFactorList.add(discountFactorItems);
        discountFactorItems = Maps.newLinkedHashMap();
        discountFactorItems.put(DiscountFactorItem.QUARTERLY, null);
        discountFactorList.add(discountFactorItems);
        return discountFactorList;
    }

    private List<Map<ModalFactorItem, BigDecimal>> populateModalFactorDiscount(){
        List<Map<ModalFactorItem,BigDecimal>>  modalFactorList = Lists.newArrayList();
        Map<ModalFactorItem,BigDecimal> modelFactorItems = Maps.newLinkedHashMap();
        modelFactorItems.put(ModalFactorItem.SEMI_ANNUAL, null);
        modalFactorList.add(modelFactorItems);
        modelFactorItems = Maps.newLinkedHashMap();
        modelFactorItems.put(ModalFactorItem.QUARTERLY, null);
        modalFactorList.add(modelFactorItems);
        modelFactorItems = Maps.newLinkedHashMap();
        modelFactorItems.put(ModalFactorItem.MONTHLY, null);
        modalFactorList.add(modelFactorItems);
        return modalFactorList;
    }

    private List<Map<PolicyFeeProcessType, Integer>> populatePolicyFeeProcessData(){
        List<Map<PolicyFeeProcessType,Integer>>  processFeeList = Lists.newArrayList();
        Map<PolicyFeeProcessType,Integer> policyFeeProcessItems = Maps.newLinkedHashMap();
        policyFeeProcessItems.put(PolicyFeeProcessType.ANNUAL, 0);
        processFeeList.add(policyFeeProcessItems);
        policyFeeProcessItems = Maps.newLinkedHashMap();
        policyFeeProcessItems.put(PolicyFeeProcessType.SEMI_ANNUAL, 0);
        processFeeList.add(policyFeeProcessItems);
        policyFeeProcessItems = Maps.newLinkedHashMap();
        policyFeeProcessItems.put(PolicyFeeProcessType.QUARTERLY, 0);
        processFeeList.add(policyFeeProcessItems);
        policyFeeProcessItems = Maps.newLinkedHashMap();
        policyFeeProcessItems.put(PolicyFeeProcessType.MONTHLY, 0);
        processFeeList.add(policyFeeProcessItems);
        return processFeeList;
    }

    private List<PolicyProcessMinimumLimitItemDto> populateMinimumLimitProcessData(){
        List<PolicyProcessMinimumLimitItemDto>  policyProcessMinimumLimitItems = Lists.newArrayList();
        PolicyProcessMinimumLimitItemDto policyProcessMinimumLimitItemDto = new PolicyProcessMinimumLimitItemDto();
        policyProcessMinimumLimitItemDto.setPolicyProcessMinimumLimitType(PolicyProcessMinimumLimitType.SEMI_ANNUAL);
        policyProcessMinimumLimitItemDto.setMinimumPremium(0);
        policyProcessMinimumLimitItemDto.setNoOfPersonPerPolicy(1);
        policyProcessMinimumLimitItems.add(policyProcessMinimumLimitItemDto);
        policyProcessMinimumLimitItemDto = new PolicyProcessMinimumLimitItemDto();
        policyProcessMinimumLimitItemDto.setPolicyProcessMinimumLimitType(PolicyProcessMinimumLimitType.ANNUAL);
        policyProcessMinimumLimitItemDto.setMinimumPremium(0);
        policyProcessMinimumLimitItemDto.setNoOfPersonPerPolicy(1);
        policyProcessMinimumLimitItems.add(policyProcessMinimumLimitItemDto);
        return policyProcessMinimumLimitItems;
    }

    public Map<Tax,BigDecimal> populateServiceTax(){
        Map<Tax,BigDecimal> serviceTax = Maps.newLinkedHashMap();
        serviceTax.put(Tax.SERVICE_TAX, null);
        return serviceTax;
    }

}
