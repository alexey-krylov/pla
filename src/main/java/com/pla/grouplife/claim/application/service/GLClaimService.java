package com.pla.grouplife.claim.application.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mongodb.gridfs.GridFSDBFile;
import com.pla.core.domain.model.notification.NotificationBuilder;
import com.pla.core.dto.CoverageClaimTypeDto;
import com.pla.core.dto.MandatoryDocumentDto;
import com.pla.core.dto.ProductClaimTypeDto;
import com.pla.core.query.ProductClaimMapperFinder;
import com.pla.grouplife.claim.domain.model.AssuredDetail;
import com.pla.grouplife.claim.domain.model.ClaimStatus;
import com.pla.grouplife.claim.domain.model.ClaimantDetail;
import com.pla.grouplife.claim.domain.model.GLClaimDocument;
import com.pla.grouplife.claim.presentation.dto.*;
import com.pla.grouplife.claim.query.GLClaimFinder;
import com.pla.grouplife.policy.query.GLPolicyFinder;
import com.pla.grouplife.sharedresource.dto.ContactPersonDetailDto;
import com.pla.grouplife.sharedresource.dto.GLPolicyDetailDto;
import com.pla.grouplife.sharedresource.dto.SearchGLPolicyDto;
import com.pla.grouplife.sharedresource.model.vo.*;
import com.pla.grouplife.sharedresource.query.GLFinder;
import com.pla.publishedlanguage.dto.ClientDocumentDto;
import com.pla.publishedlanguage.dto.SearchDocumentDetailDto;
import com.pla.publishedlanguage.dto.UnderWriterRoutingLevelDetailDto;
import com.pla.publishedlanguage.underwriter.contract.IUnderWriterAdapter;
import com.pla.sharedkernel.domain.model.*;
import com.pla.sharedkernel.identifier.CoverageId;
import com.pla.sharedkernel.identifier.LineOfBusinessEnum;
import com.pla.sharedkernel.identifier.PlanId;
import com.pla.sharedkernel.identifier.PolicyId;
import com.pla.underwriter.domain.model.UnderWriterRoutingLevel;
import com.pla.underwriter.finder.UnderWriterFinder;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.nthdimenzion.common.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.nthdimenzion.utils.UtilValidator.isEmpty;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Created by Mirror on 8/26/2015.
 */
@Service
public class GLClaimService {

    @Autowired
    private GLFinder glFinder;

    @Autowired
    private GLClaimFinder glClaimFinder;

    @Autowired
    private ProductClaimMapperFinder productClaimMapperFinder;

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private GLPolicyFinder glPolicyFinder;

    @Autowired
    UnderWriterFinder underWriterFinder;

    @Autowired
    private IUnderWriterAdapter underWriterAdapter;

    public List<GLPolicyDetailDto> searchPolicy(SearchGLPolicyDto searchGLPolicyDto) {
        List<Map> searchedPolices = glFinder.searchPolicy(searchGLPolicyDto.getPolicyNumber(), searchGLPolicyDto.getPolicyHolderName(), searchGLPolicyDto.getClientId(), new String[]{"IN_FORCE"}, searchGLPolicyDto.getProposalNumber());
        if (isEmpty(searchedPolices)) {
            return Lists.newArrayList();
        }
        searchedPolices = searchByClientId(searchGLPolicyDto.getClientId(), searchedPolices);
        List<GLPolicyDetailDto> transformedPolicies = searchedPolices.stream().map(new Function<Map, GLPolicyDetailDto>() {
            @Override
            public GLPolicyDetailDto apply(Map map) {
                GLPolicyDetailDto policyDetailDto = transformToDto(map);
                return policyDetailDto;
            }
        }).collect(Collectors.toList());
        return transformedPolicies;
    }


    public List<GLPolicyDetailDto> searchGLPolicy(SearchPolicyDto searchPolicyDto) {

        List<Map> searchedPolices = glClaimFinder.searchPolicy(searchPolicyDto.getPolicyNumber(), searchPolicyDto.getPolicyHolderName(), searchPolicyDto.getClientId(), new String[]{"IN_FORCE"});

        if (isEmpty(searchedPolices)) {
            return Lists.newArrayList();
        }
        //searchedPolices = searchByClientId(searchPolicyDto.getClientId(), searchedPolices);
        List<GLPolicyDetailDto> transformedPolicies = searchedPolices.stream().map(new Function<Map, GLPolicyDetailDto>() {
            @Override
            public GLPolicyDetailDto apply(Map map) {
                GLPolicyDetailDto policyDetailDto = transformToDto(map);
                return policyDetailDto;
            }
        }).collect(Collectors.toList());

        return transformedPolicies;
    }

    public ClaimantDetailDto claimantDetailSearch(String policyId){
        ClaimantDetailDto claimantDetailDto=new ClaimantDetailDto();
        Map policyMap= glFinder.findPolicyById(policyId);
        String schemeName=(String)policyMap.get("schemeName");
        PolicyNumber policyNumberObj=(PolicyNumber)policyMap.get("policyNumber");
        String policyNumber=policyNumberObj.getPolicyNumber();
        List<InsuredDependent> insuredDependentList = Lists.newArrayList();
        List<Insured> insuredList = (List<Insured>)policyMap.get("insureds");
        boolean isAssuredSharedAvailable=false;
        for (Insured insured : insuredList) {
            if(insured.getNoOfAssured()==null) {
                isAssuredSharedAvailable=true;
            }
            if(insured.getNoOfAssured()!=null) {
                Set<InsuredDependent> insuredDependents = insured.getInsuredDependents();
                for (InsuredDependent insuredDependent : insuredDependents) {
                    if(insuredDependent.getNoOfAssured()!=null){
                        isAssuredSharedAvailable=true;
                    }
                    else{
                        isAssuredSharedAvailable=false;
                    }
                }
                }
            }
        claimantDetailDto.setIsAssuredDetailsShared(isAssuredSharedAvailable);

        Proposer proposer=(Proposer)policyMap.get("proposer");
        ProposerContactDetail proposerContactDetail=proposer.getContactDetail();
        claimantDetailDto.setSchemeName(schemeName);
        ClaimantDetail claimantDetail=new ClaimantDetail(proposer.getProposerName(),proposerContactDetail.getAddressLine1(),proposerContactDetail.getAddressLine2()
        ,proposerContactDetail.getPostalCode(),proposerContactDetail.getProvince(),proposerContactDetail.getTown(),proposerContactDetail.getEmailAddress(),proposerContactDetail.getEmailAddress());
        //claimantDetail.setProposerName(proposer.getProposerName());

        List<ProposerContactDetail.ContactPersonDetail> contactPersonDetailList=proposerContactDetail.getContactPersonDetail();
        List<ContactPersonDetailDto> contactPersonDetailDtoList=new ArrayList<ContactPersonDetailDto>();
        for(ProposerContactDetail.ContactPersonDetail contactPersonDetails:contactPersonDetailList){
            ContactPersonDetailDto contactPersonDetailDto=new ContactPersonDetailDto();
            contactPersonDetailDto.setContactPersonName(contactPersonDetails.getContactPersonName());
            contactPersonDetailDto.setContactPersonEmail(contactPersonDetails.getContactPersonEmail());
            contactPersonDetailDto.setContactPersonMobileNumber(contactPersonDetails.getMobileNumber());
            contactPersonDetailDto.setContactPersonWorkPhoneNumber(contactPersonDetails.getWorkPhoneNumber());
            contactPersonDetailDtoList.add(contactPersonDetailDto);
        }
        claimantDetail.withContactPersonDetails(contactPersonDetailDtoList);
        claimantDetailDto.setClaimantDetail(claimantDetail);
       Set<String> categorySet=getCategory(policyId);
        claimantDetailDto.setCategorySet(categorySet);
        claimantDetailDto.setPolicyNumber(policyNumber);
        return claimantDetailDto;
    }


    public List<ClaimIntimationDetailDto> getClaimIntimationRecord(SearchClaimIntimationDto searchClaimIntimationDto) {
        List<Map> searchedClaimRecords = glClaimFinder.getClaimIntimationDetail(searchClaimIntimationDto.getClaimNumber(), searchClaimIntimationDto.getPolicyNumber(),
                searchClaimIntimationDto.getPolicyHolderName(), searchClaimIntimationDto.getPolicyHolderClientId(), searchClaimIntimationDto.getAssuredName(),
                searchClaimIntimationDto.getAssuredClientId(), searchClaimIntimationDto.getAssuredNrcNumber());

        if (isEmpty(searchedClaimRecords)) {
            return Lists.newArrayList();
        }
        return searchedClaimRecords.parallelStream().map(new Function<Map, ClaimIntimationDetailDto>() {
            @Override
            public ClaimIntimationDetailDto apply(Map map) {
                ClaimIntimationDetailDto claimIntimationDetailDto = new ClaimIntimationDetailDto();
                Proposer proposer = (Proposer) map.get("proposer");
                claimIntimationDetailDto.withProposer(proposer);
                AssuredDetail assuredDetail = (AssuredDetail) map.get("assuredDetail");
                PlanPremiumDetail planPremiumDetail = (PlanPremiumDetail) map.get("planPremiumDetail");
                List<CoveragePremiumDetail> coveragePremiumList = (List<CoveragePremiumDetail>) map.get("coveragePremiumDetails");
                Set<CoveragePremiumDetail> coveragePremiumDetails = coveragePremiumList.stream().collect(Collectors.toSet());
                AssuredDetailDtoBuilder assuredDetailDtoBuilder = new AssuredDetailDtoBuilder();
                assuredDetailDtoBuilder.withCompanyName(assuredDetail.getCompanyName());
                assuredDetailDtoBuilder.withManNumber(assuredDetail.getManNumber());
                assuredDetailDtoBuilder.withNrcNumber(assuredDetail.getNrcNumber());
                assuredDetailDtoBuilder.withSalutation(assuredDetail.getSalutation());
                assuredDetailDtoBuilder.withFirstName(assuredDetail.getFirstName());
                assuredDetailDtoBuilder.withLastName(assuredDetail.getLastName());
                assuredDetailDtoBuilder.withDateOfBirth(assuredDetail.getDateOfBirth());
                assuredDetailDtoBuilder.withGender(assuredDetail.getGender());
                assuredDetailDtoBuilder.withCategory(assuredDetail.getCategory());
                assuredDetailDtoBuilder.withAnnualIncome(assuredDetail.getAnnualIncome());
                assuredDetailDtoBuilder.withOccupationClass(assuredDetail.getOccupationClass());
                assuredDetailDtoBuilder.withOccupationCategory(assuredDetail.getOccupationCategory());
                assuredDetailDtoBuilder.withNoOfAssured(assuredDetail.getNoOfAssured());
                assuredDetailDtoBuilder.withPlanPremiumDetail(planPremiumDetail);
                assuredDetailDtoBuilder.withCoveragePremiumDetails(coveragePremiumDetails);
                assuredDetailDtoBuilder.withFamilyId(assuredDetail.getFamilyId());
                assuredDetailDtoBuilder.withRelationship(assuredDetail.getRelationship());
                AssuredDetailDto assuredDetailDto = assuredDetailDtoBuilder.createAssuredDetailDto();
                ClaimType claimType = ClaimType.valueOf((String) map.get("claimType"));
                List<ClaimType> claimTypes = Lists.newArrayList();
                claimTypes.add(claimType);
                claimIntimationDetailDto.withProposer(proposer);
                claimIntimationDetailDto.setAssuredDetail(assuredDetailDto);
                claimIntimationDetailDto.setClaimTypes(claimTypes);
                return claimIntimationDetailDto;
            }
        }).collect(Collectors.toList());


    }

    public List<GLClaimDetailDto> getClaimDetail(SearchClaimDto searchClaimDto) {
        List<Map> searchedClaimRecords = glClaimFinder.getClaimDetails(searchClaimDto.getClaimNumber(), searchClaimDto.getPolicyNumber(),
                searchClaimDto.getPolicyHolderName(), searchClaimDto.getAssuredName(),
                searchClaimDto.getAssuredClientId(), searchClaimDto.getAssuredNrcNumber());

        if (isEmpty(searchedClaimRecords)) {
            return Lists.newArrayList();
        }
        return searchedClaimRecords.parallelStream().map(new Function<Map, GLClaimDetailDto>() {
            @Override
            public GLClaimDetailDto apply(Map map) {
                GLClaimDetailDto claimDetailDto = new GLClaimDetailDto();
                String claimId = map.get("_id").toString();
                ClaimNumber claimNumber = (ClaimNumber) map.get("claimNumber");
                //ClaimType.valueOf((String)map.get("claimType"));
                ClaimStatus claimStatus = ClaimStatus.valueOf((String) map.get("claimStatus"));
                String claimNumberInString = claimNumber.getClaimNumber();
                claimDetailDto.withClaimNumberAndClaimId(claimNumberInString, claimId);
                Policy policy = (Policy) map.get("policy");
                AssuredDetail assuredDetail = (AssuredDetail) map.get("assuredDetail");
                PlanPremiumDetail planPremiumDetail = (PlanPremiumDetail) map.get("planPremiumDetail");
                List<CoveragePremiumDetail> coveragePremiumList = (List<CoveragePremiumDetail>) map.get("coveragePremiumDetails");
                Set<CoveragePremiumDetail> coveragePremiumDetails = coveragePremiumList.stream().collect(Collectors.toSet());
                AssuredDetailDtoBuilder assuredDetailDtoBuilder = new AssuredDetailDtoBuilder();
                assuredDetailDtoBuilder.withCompanyName(assuredDetail.getCompanyName());
                assuredDetailDtoBuilder.withManNumber(assuredDetail.getManNumber());
                assuredDetailDtoBuilder.withNrcNumber(assuredDetail.getNrcNumber());
                assuredDetailDtoBuilder.withSalutation(assuredDetail.getSalutation());
                assuredDetailDtoBuilder.withFirstName(assuredDetail.getFirstName());
                assuredDetailDtoBuilder.withLastName(assuredDetail.getLastName());
                assuredDetailDtoBuilder.withDateOfBirth(assuredDetail.getDateOfBirth());
                assuredDetailDtoBuilder.withGender(assuredDetail.getGender());
                assuredDetailDtoBuilder.withCategory(assuredDetail.getCategory());
                assuredDetailDtoBuilder.withAnnualIncome(assuredDetail.getAnnualIncome());
                assuredDetailDtoBuilder.withOccupationClass(assuredDetail.getOccupationClass());
                assuredDetailDtoBuilder.withOccupationCategory(assuredDetail.getOccupationCategory());
                assuredDetailDtoBuilder.withNoOfAssured(assuredDetail.getNoOfAssured());
                assuredDetailDtoBuilder.withPlanPremiumDetail(planPremiumDetail);
                assuredDetailDtoBuilder.withCoveragePremiumDetails(coveragePremiumDetails);
                assuredDetailDtoBuilder.withFamilyId(assuredDetail.getFamilyId());
                assuredDetailDtoBuilder.withRelationship(assuredDetail.getRelationship());
                AssuredDetailDto assuredDetailDto = assuredDetailDtoBuilder.createAssuredDetailDto();
                ClaimType claimType = ClaimType.valueOf((String) map.get("claimType"));
                List<ClaimType> claimTypes = Lists.newArrayList();
                claimTypes.add(claimType);
                claimDetailDto.withPolicy(policy);
                claimDetailDto.setAssuredDetail(assuredDetailDto);
                claimDetailDto.setClaimTypes(claimTypes);
                claimDetailDto.setClaimStatus(claimStatus);
                return claimDetailDto;
            }
        }).collect(Collectors.toList());


        //return null;
    }


    public List<ClaimIntimationDetailDto> getClaimIntimationDetail(String policyNumber) {
        List<Map> assuredSearchList = glFinder.assuredSearch(policyNumber);
        return assuredSearchList.parallelStream().map(new Function<Map, ClaimIntimationDetailDto>() {
            @Override
            public ClaimIntimationDetailDto apply(Map map) {
                ClaimIntimationDetailDto claimIntimationDetailDto = new ClaimIntimationDetailDto();
                Proposer proposer = (Proposer) map.get("proposer");
                claimIntimationDetailDto.withProposer(proposer);
               /* List<InsuredDependent> insuredDependentList = Lists.newArrayList();
                List<Insured> insureds = (List<Insured>) map.get("insureds");
                for (Insured insured : insureds) {
                    Set<InsuredDependent> insuredDependents = insured.getInsuredDependents();
                    for (InsuredDependent insuredDependent : insuredDependents) {
                        String familyId = insuredDependent.getFamilyId() != null ? insuredDependent.getFamilyId().getFamilyId() : "";
                        if (familyId.equals(clientId)) {
                            insuredDependentList.add(insuredDependent);
                        }
                    }
                }
                List<Insured> insuredsList = Lists.newArrayList();
                for (Insured insured : insureds) {
                    String familyId = insured.getFamilyId() != null ? insured.getFamilyId().getFamilyId() : "";
                    if (familyId.equals(clientId)) {
                        insuredsList.add(insured);
                    }
                }
                if (isNotEmpty(insuredDependentList)) {
                    claimIntimationDetailDto.withInsuredDependentAssuredDetail(insuredDependentList.get(0));
                }
                if (isNotEmpty(insuredsList)) {
                    claimIntimationDetailDto.withInsuredAssuredDetail(insuredsList.get(0));
                }*/
                return claimIntimationDetailDto;
            }
        }).collect(Collectors.toList());
    }

    public List<GLInsuredDetailDto> assuredSearch(AssuredSearchDto assuredSearchDto) {
        List<Map> assuredSearchList = glFinder.assuredSearch(assuredSearchDto.getPolicyNumber());
        List<InsuredDependent> insuredDependentList = Lists.newArrayList();
        for (Map assuredSearchMap : assuredSearchList) {
            List<Insured> insureds = (List<Insured>) assuredSearchMap.get("insureds");
            for (Insured insured : insureds) {
                Set<InsuredDependent> insuredDependents = insured.getInsuredDependents();
                for (InsuredDependent insuredDependent : insuredDependents) {
                    String category = insuredDependent.getCategory() != null ? insuredDependent.getCategory() : "";
                    String relationship = insuredDependent.getRelationship() != null ? insuredDependent.getRelationship().name(): "";
                    if (category.equals(assuredSearchDto.getCategory()) && relationship.equals(assuredSearchDto.getRelationShip())) {
                        insuredDependentList.add(insuredDependent);

                    }
                }
            }
        }

        List<Insured> insureds = Lists.newArrayList();
        for (Map insuredMap : assuredSearchList) {
            List<Insured> insuredsList = (List<Insured>) insuredMap.get("insureds");
            for (Insured insured : insuredsList) {
                String category = insured.getCategory() != null ? insured.getCategory() : "";
                String relationship = insured.getRelationship() != null ? insured.getRelationship().description : Relationship.SELF.description;
                if (category.equals(assuredSearchDto.getCategory()) && relationship.equals(assuredSearchDto.getRelationShip())) {
                    insureds.add(insured);
                }
            }
        }

        List<GLInsuredDetailDto> searchList = Lists.newArrayList();
        for (InsuredDependent insuredDependent : insuredDependentList) {
            GLInsuredDetailDto glInsuredDetailDto = new GLInsuredDetailDto(insuredDependent.getFirstName(), insuredDependent.getLastName(),
                    insuredDependent.getDateOfBirth() != null ? insuredDependent.getDateOfBirth().toString(AppConstants.DD_MM_YYY_FORMAT) : "", insuredDependent.getGender() != null ? insuredDependent.getGender().name() : "", insuredDependent.getNrcNumber(),
                    insuredDependent.getManNumber(), insuredDependent.getFamilyId() != null ? insuredDependent.getFamilyId().getFamilyId() : "");
            searchList.add(glInsuredDetailDto);
        }
        for (Insured insured : insureds) {
            GLInsuredDetailDto glInsuredDetailDto = new GLInsuredDetailDto(insured.getFirstName(), insured.getLastName(),
                    insured.getDateOfBirth() != null ? insured.getDateOfBirth().toString(AppConstants.DD_MM_YYY_FORMAT) : "", insured.getGender() != null ? insured.getGender().name() : "", insured.getNrcNumber(),
                    insured.getManNumber(), insured.getFamilyId() != null ? insured.getFamilyId().getFamilyId() : "");
            searchList.add(glInsuredDetailDto);
        }
        return searchList;
    }
    public Map<String, Object> getConfiguredRelationShipAndCategory(String policyId) {
        Set<String> categoryList = Sets.newLinkedHashSet();
        Set<String> relationShipList = Sets.newLinkedHashSet();
        Map policy = glFinder.findPolicyById(policyId);
        if (isEmpty(policy)) {
            return Collections.EMPTY_MAP;
        }
        List<Insured> insuredList = (List<Insured>) policy.get("insureds");
        relationShipList.add(Relationship.SELF.description);
        for (Insured insured : insuredList) {
            categoryList.add(insured.getCategory() != null ? insured.getCategory() : "");
            Set<InsuredDependent> insuredDependentList = insured.getInsuredDependents();
            for (InsuredDependent insuredDependentMap : insuredDependentList) {
                categoryList.add(insuredDependentMap.getCategory() != null ? insuredDependentMap.getCategory() : "");
                relationShipList.add(insuredDependentMap.getRelationship() != null ? insuredDependentMap.getRelationship().name() : "");
            }
        }
        Map<String, Object> relationCategoryMap = Maps.newLinkedHashMap();
        relationCategoryMap.put("relationship", relationShipList);
        relationCategoryMap.put("category", categoryList);
        PolicyNumber policyNumber = policy.get("policyNumber") != null ? (PolicyNumber) policy.get("policyNumber") : null;
        relationCategoryMap.put("policyNumber", policyNumber);
        return relationCategoryMap;
    }

    public Set<String> getCategory(String policyId) {
        Set<String> categoryList = Sets.newLinkedHashSet();
        Map policy = glFinder.findPolicyById(policyId);
        if (isEmpty(policy)) {
            return Collections.EMPTY_SET;
        }
        List<Insured> insuredList = (List<Insured>) policy.get("insureds");
        for (Insured insured : insuredList) {
            categoryList.add(insured.getCategory() != null ? insured.getCategory() : "");
            Set<InsuredDependent> insuredDependentList = insured.getInsuredDependents();
            for (InsuredDependent insuredDependentMap : insuredDependentList) {
                categoryList.add(insuredDependentMap.getCategory() != null ? insuredDependentMap.getCategory() : "");
            }
        }

        return categoryList;
    }


    public Map<String, Object> getConfiguredCategory(String policyId) {
        Set<String> categoryList = Sets.newLinkedHashSet();
        Map policy = glFinder.findPolicyById(policyId);
        if (isEmpty(policy)) {
            return Collections.EMPTY_MAP;
        }
        List<Insured> insuredList = (List<Insured>) policy.get("insureds");
        for (Insured insured : insuredList) {
            categoryList.add(insured.getCategory() != null ? insured.getCategory() : "");
            Set<InsuredDependent> insuredDependentList = insured.getInsuredDependents();
            for (InsuredDependent insuredDependentMap : insuredDependentList) {
                categoryList.add(insuredDependentMap.getCategory() != null ? insuredDependentMap.getCategory() : "");
            }
        }
        Map<String, Object> categoryMap = Maps.newLinkedHashMap();
        categoryMap.put("category", categoryList);
        PolicyNumber policyNumber = policy.get("policyNumber") != null ? (PolicyNumber) policy.get("policyNumber") : null;
        categoryMap.put("policyNumber", policyNumber);
        return categoryMap;
    }


    public Map<String, Object> getConfiguredRelationShip(String policyId,String categorySearch) {
        Set<String> categoryList = Sets.newLinkedHashSet();
        Set<String> relationShipList = Sets.newLinkedHashSet();
        Map policy = glFinder.findPolicyById(policyId);
        if (isEmpty(policy)) {
            return Collections.EMPTY_MAP;
        }
        List<Insured> insuredList = (List<Insured>) policy.get("insureds");

        for (Insured insured : insuredList) {
           if(insured.getCategory() != null && insured.getCategory().equals(categorySearch)){
               String relationship = insured.getRelationship() != null ? insured.getRelationship().description : Relationship.SELF.description;
               relationShipList.add(relationship);

           }
            Set<InsuredDependent> insuredDependentList = insured.getInsuredDependents();
            for (InsuredDependent insuredDependents : insuredDependentList) {
                String category=insuredDependents.getCategory();
                if(category != null &&category.equals(categorySearch) ){
                    relationShipList.add(insuredDependents.getRelationship() != null ? insuredDependents.getRelationship().name() : "");
                }
            }
        }
        Map<String, Object> relationMap = Maps.newLinkedHashMap();
        relationMap.put("relationship", relationShipList);

        PolicyNumber policyNumber = policy.get("policyNumber") != null ? (PolicyNumber) policy.get("policyNumber") : null;
        relationMap.put("policyNumber", policyNumber);
        return relationMap;
    }

    public PlanCoverageDetailDto getPlanDetailForCategoryAndRelationship(String policyId, String searchCategory,String searchRelationship){
        Map policy = glFinder.findPolicyById(policyId);
        if (isEmpty(policy)) {
            return new PlanCoverageDetailDto();
        }
        PlanCoverageDetailDto planCoverageDetailDto=new PlanCoverageDetailDto();
        List<Insured> insuredList = (List<Insured>) policy.get("insureds");

        List<InsuredDependent> insuredDependentList = Lists.newArrayList();
        Set<Insured> insuredLists = Sets.newLinkedHashSet();
        Set<InsuredDependent> relationShipList = Sets.newLinkedHashSet();
            for (Insured insured : insuredList) {
                Set<InsuredDependent> insuredDependents = insured.getInsuredDependents();
                for (InsuredDependent insuredDependent : insuredDependents) {
                    String category = insuredDependent.getCategory() != null ? insuredDependent.getCategory() : "";
                    String relationship = insuredDependent.getRelationship() != null ? insuredDependent.getRelationship().name() : "";
                    if (category.equals(searchCategory) && relationship.equals(searchRelationship)) {
                        insuredDependentList.add(insuredDependent);

                    }
                }
                List<Insured> insureds = Lists.newArrayList();
                List<Insured> insuredsList = (List<Insured>) policy.get("insureds");
                PolicyNumber policyNumberObj = policy.get("policyNumber") != null ? (PolicyNumber) policy.get("policyNumber") : null;
                String policyNumber=policyNumberObj.getPolicyNumber();
                for (Insured insure : insuredsList) {
                    String category = insure.getCategory() != null ? insure.getCategory() : "";
                    String relationship = insured.getRelationship() != null ? insured.getRelationship().description : Relationship.SELF.description;
                   // String relationship = insure.getRelationship() != null ? insure.getRelationship().name() : "";

                    if (category.equals(searchCategory) && relationship.equals(searchRelationship)) {
                        insureds.add(insure);

                    }
                }


                if (insureds!=null){
                    Insured insuredResult=insureds.get(0);
                    PlanPremiumDetail planPremiumDetail=insuredResult.getPlanPremiumDetail();
                    String planName=null;
                    Set<String>claimTypes=null;
                    List<Map<String, Object>> planMap=productClaimMapperFinder.getPlanDetailBy(LineOfBusinessEnum.GROUP_LIFE);
                    for(Map<String,Object>plan:planMap){

                        if(((String)plan.get("planCode")).equals(planPremiumDetail.getPlanCode())){
                          planName=(String)plan.get("planName");
                        }
                        planName="";
                    }

                    List<ProductClaimTypeDto> productClaimList=productClaimMapperFinder.searchProductClaimMap(LineOfBusinessEnum.GROUP_LIFE,planName);
                  //  List<ProductClaimTypeDto> productClaimList=productClaimMapperFinder.searchProductClaimMap(LineOfBusinessEnum.GROUP_LIFE,"Life Cover");

                    for(ProductClaimTypeDto productClaimTypeDto:productClaimList){
                        List<CoverageClaimTypeDto> coverageClaimType= productClaimTypeDto.getCoverageClaimType();
                        for(CoverageClaimTypeDto coverageClaimTypeDto:coverageClaimType){
                            claimTypes=  coverageClaimTypeDto.getClaimTypes();
                        }

                    }

                    planCoverageDetailDto.setPlanPremiumDetail(insured.getPlanPremiumDetail());
                    planCoverageDetailDto.setCoveragePremiumDetails(insured.getCoveragePremiumDetails());
                    planCoverageDetailDto.setPolicyNumber(policyNumber);
                    planCoverageDetailDto.setClaimTypes(claimTypes);

            }
                else{

                    InsuredDependent  insuredDependent=insuredDependentList.get(0) ;
                    PlanPremiumDetail planPremiumDetail=insuredDependent.getPlanPremiumDetail();
                    String planName=null;
                    Set<String>claimTypes=null;
                    List<Map<String, Object>> planMap=productClaimMapperFinder.getPlanDetailBy(LineOfBusinessEnum.GROUP_LIFE);
                    for(Map<String,Object>plan:planMap){
                        if(((String)plan.get("planCode")).equals(planPremiumDetail.getPlanCode())){
                            planName=(String)plan.get("planName");
                        }
                    }

                    List<ProductClaimTypeDto> productClaimList=productClaimMapperFinder.searchProductClaimMap(LineOfBusinessEnum.GROUP_LIFE,planName);
                    for(ProductClaimTypeDto productClaimTypeDto:productClaimList){
                        List<CoverageClaimTypeDto> coverageClaimType= productClaimTypeDto.getCoverageClaimType();
                        for(CoverageClaimTypeDto coverageClaimTypeDto:coverageClaimType){
                            claimTypes=  coverageClaimTypeDto.getClaimTypes();
                        }
                    }
                    planCoverageDetailDto.setPlanPremiumDetail(insuredDependent.getPlanPremiumDetail());
                    planCoverageDetailDto.setCoveragePremiumDetails(insuredDependent.getCoveragePremiumDetails());
                    planCoverageDetailDto.setPolicyNumber(policyNumber);
                    planCoverageDetailDto.setClaimTypes(claimTypes);
                }
        }
        return planCoverageDetailDto;
    }






    private GLPolicyDetailDto transformToDto(Map policyMap) {
        DateTime inceptionDate = policyMap.get("inceptionOn") != null ? new DateTime((Date) policyMap.get("inceptionOn")) : null;
        DateTime expiryDate = policyMap.get("expiredOn") != null ? new DateTime((Date) policyMap.get("expiredOn")) : null;
        Proposer glProposer = policyMap.get("proposer") != null ? (Proposer) policyMap.get("proposer") : null;
        PolicyNumber policyNumber = policyMap.get("policyNumber") != null ? (PolicyNumber) policyMap.get("policyNumber") : null;
        GLPolicyDetailDto policyDetailDto = new GLPolicyDetailDto();
        policyDetailDto.setPolicyId(policyMap.get("_id").toString());
        policyDetailDto.setInceptionDate(inceptionDate);
        policyDetailDto.setExpiryDate(expiryDate);
        policyDetailDto.setPolicyHolderName(glProposer != null ? glProposer.getProposerName() : "");
        policyDetailDto.setPolicyNumber(policyNumber.getPolicyNumber());
        policyDetailDto.setStatus((String) policyMap.get("status"));
        return policyDetailDto;
    }

    private List<Map> searchByClientId(String clientId, List<Map> searchedPolices) {
        if (isNotEmpty(clientId)) {
            return searchedPolices.parallelStream().filter(new Predicate<Map>() {
                @Override
                public boolean test(Map searchedPolicyMap) {
                    boolean isFound = false;
                    List<Insured> insuredList = (List<Insured>) searchedPolicyMap.get("insureds");
                    for (Insured insured : insuredList) {
                        FamilyId familyId = insured.getFamilyId();
                        if (familyId != null) {
                            isFound = clientId.equals(familyId.getFamilyId());
                        }
                        if (isFound) {
                            return isFound;
                        }
                        Set<InsuredDependent> insuredDependentList = insured.getInsuredDependents();
                        for (InsuredDependent insuredDependentMap : insuredDependentList) {
                            FamilyId dependentFamilyId = insuredDependentMap.getFamilyId();
                            if (dependentFamilyId != null) {
                                return clientId.equals(dependentFamilyId.getFamilyId());
                            }
                        }
                    }
                    return false;
                }
            }).collect(Collectors.toList());
        }
        return Collections.EMPTY_LIST;
    }

    private List<GLClaimMandatoryDocumentDto> getAllMandatoryDocuments(List<GLClaimDocument> uploadedDocuments,List<ClaimMandatoryDocumentDto> mandatoryDocumentRequiredForSubmission) {
        List<GLClaimMandatoryDocumentDto> mandatoryDocumentList = Lists.newArrayList();
        if (isNotEmpty(mandatoryDocumentRequiredForSubmission)) {
            mandatoryDocumentList = mandatoryDocumentRequiredForSubmission.stream().map(new Function<ClaimMandatoryDocumentDto, GLClaimMandatoryDocumentDto>() {
                @Override
                public GLClaimMandatoryDocumentDto apply(ClaimMandatoryDocumentDto documentDto) {
                    GLClaimMandatoryDocumentDto mandatoryDocumentDto = new GLClaimMandatoryDocumentDto(documentDto.getDocumentCode(), documentDto.getDocumentName());
                    Optional<GLClaimDocument> claimDocumentOptional = uploadedDocuments.stream().filter(new Predicate<GLClaimDocument>() {
                        @Override
                        public boolean test(GLClaimDocument glClaimDocument) {
                            return documentDto.getDocumentCode().equals(glClaimDocument.getDocumentId());
                        }
                    }).findAny();
                    if (claimDocumentOptional.isPresent()) {
                        try {
                            if (isNotEmpty(claimDocumentOptional.get().getGridFsDocId())) {
                                GridFSDBFile gridFSDBFile = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(claimDocumentOptional.get().getGridFsDocId())));
                                mandatoryDocumentDto.setFileName(gridFSDBFile.getFilename());
                                mandatoryDocumentDto.setContentType(gridFSDBFile.getContentType());
                                mandatoryDocumentDto.setGridFsDocId(gridFSDBFile.getId().toString());
                                mandatoryDocumentDto.updateWithContent(IOUtils.toByteArray(gridFSDBFile.getInputStream()));
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    return mandatoryDocumentDto;
                }
            }).collect(Collectors.toList());
        }
        return mandatoryDocumentList;
    }


    public GLClaimDocument getAddedDocument(File file,String documentId,boolean mandatory)throws IOException{
      /*
      * @TODO pass the content type which is getting from multi part file
      * */
        File file1 = null;
        String contentType ="";
        GLClaimDocument addedDocument=null;
        String fileName = file != null ? file.getName() :contentType;
        String gridFsDocId = gridFsTemplate.store(FileUtils.openInputStream(file), fileName, contentType).getId().toString();
        addedDocument = new GLClaimDocument(documentId, fileName, gridFsDocId, "", mandatory);
        return addedDocument;
    }



    ////////////////////////////////////////////////////////////////////
    public boolean isClaimIntimationAvailable(String claimId){
        Map claimMap = glClaimFinder.findClaimById(claimId);
        return isNotEmpty(claimMap)?true:false;

    }



    public PolicyId getPolicyIdFromClaim(String claimId) {
        Map claimMap = glClaimFinder.findClaimById(claimId);
        Policy policy =claimMap != null ? (Policy) claimMap.get("policy") : null;
        PolicyId policyId = policy != null ? policy.getPolicyId() : null;
        return policyId;
    }

    public List<GLClaimMandatoryDocumentDto> findMandatoryDocuments(String claimId) {
       Map claimMap = glClaimFinder.findClaimById(claimId);
       PolicyId policyId = getPolicyIdFromClaim(claimId);
       Map policyMap = glPolicyFinder.findPolicyById(policyId.getPolicyId());
       List<Insured> insureds = (List<Insured>) policyMap.get("insureds");
       List<GLClaimDocument> uploadedDocuments = claimMap.get("claimDocuments") != null ? (List<GLClaimDocument>) claimMap.get("claimDocuments") : Lists.newArrayList();
       List<SearchDocumentDetailDto> documentDetailDtos = Lists.newArrayList();
       insureds.forEach(ghInsured -> {
           PlanPremiumDetail planPremiumDetail = ghInsured.getPlanPremiumDetail();
           SearchDocumentDetailDto searchDocumentDetailDto = new SearchDocumentDetailDto(planPremiumDetail.getPlanId());
           documentDetailDtos.add(searchDocumentDetailDto);
           if (isNotEmpty(ghInsured.getInsuredDependents())) {
               ghInsured.getInsuredDependents().forEach(insuredDependent -> {
                   PlanPremiumDetail dependentPlanPremiumDetail = insuredDependent.getPlanPremiumDetail();
                   documentDetailDtos.add(new SearchDocumentDetailDto(dependentPlanPremiumDetail.getPlanId()));
               });
           }
       });
       Set<ClientDocumentDto> mandatoryDocuments = underWriterAdapter.getMandatoryDocumentsForApproverApproval(documentDetailDtos, ProcessType.CLAIM);
       List<GLClaimMandatoryDocumentDto> mandatoryDocumentDtos = Lists.newArrayList();
       if (isNotEmpty(mandatoryDocuments)) {
           mandatoryDocumentDtos = mandatoryDocuments.stream().map(new Function<ClientDocumentDto, GLClaimMandatoryDocumentDto>() {
               @Override
               public GLClaimMandatoryDocumentDto apply(ClientDocumentDto clientDocumentDto) {
                   GLClaimMandatoryDocumentDto mandatoryDocumentDto = new GLClaimMandatoryDocumentDto(clientDocumentDto.getDocumentCode(), clientDocumentDto.getDocumentName());
                   Optional<GLClaimDocument> claimDocumentOptional = uploadedDocuments.stream().filter(new Predicate<GLClaimDocument>() {
                       @Override
                       public boolean test(GLClaimDocument glClaimDocument) {
                           return clientDocumentDto.getDocumentCode().equals(glClaimDocument.getDocumentId());
                       }
                   }).findAny();
                   if (claimDocumentOptional.isPresent()) {
                       try {
                           if (isNotEmpty(claimDocumentOptional.get().getGridFsDocId())) {
                               GridFSDBFile gridFSDBFile = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(claimDocumentOptional.get().getGridFsDocId())));
                               mandatoryDocumentDto.setFileName(gridFSDBFile.getFilename());
                               mandatoryDocumentDto.setContentType(gridFSDBFile.getContentType());
                               mandatoryDocumentDto.setGridFsDocId(gridFSDBFile.getId().toString());
                               mandatoryDocumentDto.updateWithContent(IOUtils.toByteArray(gridFSDBFile.getInputStream()));
                           }
                       } catch (IOException e) {
                           e.printStackTrace();
                       }
                   }
                   return mandatoryDocumentDto;
               }
           }).collect(Collectors.toList());
       }
       return mandatoryDocumentDtos;
   }


    public Set<GLClaimMandatoryDocumentDto> findAdditionalDocuments(String claimId) {
        Map claimMap = glClaimFinder.findClaimById(claimId);
        List<GLClaimDocument> uploadedDocuments = claimMap.get("claimDocuments") != null ? (List<GLClaimDocument>) claimMap.get("claimDocuments") : Lists.newArrayList();
        Set<GLClaimMandatoryDocumentDto> mandatoryDocumentDtos = Sets.newHashSet();
        if (isNotEmpty(uploadedDocuments)) {
            mandatoryDocumentDtos = uploadedDocuments.stream().filter(uploadedDocument -> !uploadedDocument.isMandatory()).map(new Function<GLClaimDocument, GLClaimMandatoryDocumentDto>() {
                @Override
                public GLClaimMandatoryDocumentDto apply(GLClaimDocument glClaimDocument) {
                    GLClaimMandatoryDocumentDto mandatoryDocumentDto = new GLClaimMandatoryDocumentDto(glClaimDocument.getDocumentId(), glClaimDocument.getDocumentName());
                    GridFSDBFile gridFSDBFile = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(glClaimDocument.getGridFsDocId())));
                    mandatoryDocumentDto.setFileName(gridFSDBFile.getFilename());
                    mandatoryDocumentDto.setContentType(gridFSDBFile.getContentType());
                    mandatoryDocumentDto.setGridFsDocId(gridFSDBFile.getId().toString());
                    try {
                        mandatoryDocumentDto.updateWithContent(IOUtils.toByteArray(gridFSDBFile.getInputStream()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return mandatoryDocumentDto;
                }
            }).collect(Collectors.toSet());
        }
        return mandatoryDocumentDtos;
    }

    public List<MandatoryDocumentDto> getAllMandatoryDocumentsForClaim(String planId){
        return glClaimFinder.getAllClaimMandatoryDocuments(planId, ProcessType.CLAIM);
    }

    public  UnderWriterRoutingLevel configuredForSelectedPlan(String planIdInString){

        String  processType="CLAIM";
        PlanId planId=new PlanId(planIdInString);
        UnderWriterRoutingLevelDetailDto underWriterRoutingLevelDetailDto=new UnderWriterRoutingLevelDetailDto(planId, LocalDate.now(),processType);
       // RoutingLevel routingLevel=underWriterAdapter.getRoutingLevel(underWriterRoutingLevelDetailDto);
        // return routingLevel != null?true:false;
        UnderWriterRoutingLevel underWriterRoutingLevel=underWriterFinder.findUnderWriterRoutingLevel(underWriterRoutingLevelDetailDto);
        return underWriterRoutingLevel;
    }

    public RoutingLevel configuredForPlan(String claimId){
        Map claimMap = glClaimFinder.findClaimById(claimId);
        PlanPremiumDetail planPremiumDetail = (PlanPremiumDetail) claimMap.get("planPremiumDetail");
        PlanId planId=planPremiumDetail.getPlanId();
        AssuredDetail assuredDetail = (AssuredDetail)claimMap.get("assuredDetail");

        List<CoveragePremiumDetail> coveragePremiumList = (List<CoveragePremiumDetail>) claimMap.get("coveragePremiumDetails");
        Set<CoveragePremiumDetail> coveragePremiumDetails = coveragePremiumList.stream().collect(Collectors.toSet());
        String  processType="CLAIM";
        //planId=new PlanId("564575ab17dcd947a102a9f0");

        UnderWriterRoutingLevelDetailDto underWriterRoutingLevelDetailDto=new UnderWriterRoutingLevelDetailDto(planId, LocalDate.now(),processType);
        RoutingLevel routingLevel=underWriterAdapter.getRoutingLevel(underWriterRoutingLevelDetailDto);
        // return rouringLevel != null?true:false;
        return routingLevel;
    }
    String getReminderStatus(String claimId){
        NotificationBuilder claimNotificationBuilder=new NotificationBuilder();


        return "reminder sent";
    }

    public List<GLClaimDocument> getUploadedMandatoryDocument(String claimId){
        Map claim = glClaimFinder.findClaimById(claimId);
        List<GLClaimDocument> uploadedDocuments = claim.get("claimDocuments") != null ? (List<GLClaimDocument>)claim.get("claimDocuments") : Lists.newArrayList();
        return uploadedDocuments;
    }

    public Set<ClientDocumentDto> getMandatoryDocumentRequiredForSubmission(String claimId){
        Map claim = glClaimFinder.findClaimById(claimId);
        List<Insured> insureds = (List<Insured>) claim.get("insureds");
        List<SearchDocumentDetailDto> documentDetailDtos = Lists.newArrayList();
        insureds.forEach(ghInsured -> {
            PlanPremiumDetail planPremiumDetail = ghInsured.getPlanPremiumDetail();
            SearchDocumentDetailDto searchDocumentDetailDto = new SearchDocumentDetailDto(planPremiumDetail.getPlanId());
            documentDetailDtos.add(searchDocumentDetailDto);
            if (isNotEmpty(ghInsured.getCoveragePremiumDetails())) {
                List<CoverageId> coverageIds = ghInsured.getCoveragePremiumDetails().stream().map(new Function<CoveragePremiumDetail, CoverageId>() {
                    @Override
                    public CoverageId apply(CoveragePremiumDetail coveragePremiumDetail) {
                        return coveragePremiumDetail.getCoverageId();
                    }
                }).collect(Collectors.toList());
                documentDetailDtos.add(new SearchDocumentDetailDto(planPremiumDetail.getPlanId(), coverageIds));
            }
            if (isNotEmpty(ghInsured.getInsuredDependents())) {
                ghInsured.getInsuredDependents().forEach(insuredDependent -> {
                    PlanPremiumDetail dependentPlanPremiumDetail = insuredDependent.getPlanPremiumDetail();
                    documentDetailDtos.add(new SearchDocumentDetailDto(dependentPlanPremiumDetail.getPlanId()));
                    if (isNotEmpty(insuredDependent.getCoveragePremiumDetails())) {
                        List<CoverageId> coverageIds = insuredDependent.getCoveragePremiumDetails().stream().map(new Function<CoveragePremiumDetail, CoverageId>() {
                            @Override
                            public CoverageId apply(CoveragePremiumDetail glCoveragePremiumDetail) {
                                return glCoveragePremiumDetail.getCoverageId();
                            }
                        }).collect(Collectors.toList());
                        documentDetailDtos.add(new SearchDocumentDetailDto(planPremiumDetail.getPlanId(), coverageIds));
                    }
                });
            }
        });
        Set<ClientDocumentDto> mandatoryDocuments = underWriterAdapter.getMandatoryDocumentsForApproverApproval(documentDetailDtos, ProcessType.CLAIM);
        return mandatoryDocuments;
    }

    public List<GLClaimDataDto> getApprovedClaimDetail (SearchClaimDto searchClaimDto,String[] statuses){
        List<Map> searchedClaimRecords = glClaimFinder.getApprovedClaimDetails(searchClaimDto.getClaimNumber(), searchClaimDto.getPolicyNumber(),
                searchClaimDto.getPolicyHolderName(), searchClaimDto.getAssuredName(),
                searchClaimDto.getAssuredClientId(), searchClaimDto.getAssuredNrcNumber(),statuses);
        if (isEmpty(searchedClaimRecords)) {
            return Lists.newArrayList();
        }
        return searchedClaimRecords.parallelStream().map(new Function<Map, GLClaimDataDto>() {
            @Override
            public GLClaimDataDto apply(Map map) {
                GLClaimDataDto claimDataDto = new GLClaimDataDto();
                String claimId = map.get("_id").toString();
                ClaimNumber claimNumber = (ClaimNumber) map.get("claimNumber");
                //ClaimType.valueOf((String)map.get("claimType"));
                ClaimStatus claimStatus = ClaimStatus.valueOf((String) map.get("claimStatus"));
                String claimNumberInString = claimNumber.getClaimNumber();
                claimDataDto.withClaimNumberAndClaimId(claimNumberInString, claimId);
                Policy policy = (Policy) map.get("policy");
                String policyNumber = policy.getPolicyNumber().getPolicyNumber();
                String policyHolderName = policy.getPolicyHolderName();
                AssuredDetail assuredDetail = (AssuredDetail) map.get("assuredDetail");
                String assuredName = assuredDetail.getFirstName();
                claimDataDto.setAssuredName(assuredName);
                claimDataDto.setClaimStatus(claimStatus.toString());
                claimDataDto.setPolicyNumber(policyNumber);
                claimDataDto.setPolicyHolderName(policyHolderName);

                //claimDataDto.setModifiedOn();
                return claimDataDto;
            }
        }).collect(Collectors.toList());

    }
    //get claim record for reopen


    public  List<GLClaimDataDto> getClaimRecordForReopen(SearchClaimDto searchClaimDto){

        List<Map> searchedClaimRecords = glClaimFinder.getReopenClaimDetails(searchClaimDto.getClaimNumber(), searchClaimDto.getPolicyNumber(),
                searchClaimDto.getPolicyHolderName(), searchClaimDto.getAssuredName(),
                searchClaimDto.getAssuredClientId(), searchClaimDto.getAssuredNrcNumber());

        if (isEmpty(searchedClaimRecords)) {
            return Lists.newArrayList();
        }
        return searchedClaimRecords.parallelStream().map(new Function<Map, GLClaimDataDto>() {
            @Override
            public GLClaimDataDto apply(Map map) {
                GLClaimDataDto claimDataDto = new GLClaimDataDto();
                String claimId = map.get("_id").toString();
                ClaimNumber claimNumber = (ClaimNumber) map.get("claimNumber");
                //ClaimType.valueOf((String)map.get("claimType"));
                ClaimStatus claimStatus = ClaimStatus.valueOf((String) map.get("claimStatus"));
                String claimNumberInString = claimNumber.getClaimNumber();
                claimDataDto.withClaimNumberAndClaimId(claimNumberInString, claimId);
                Policy policy = (Policy) map.get("policy");
                String policyNumber=policy.getPolicyNumber().getPolicyNumber();
                String policyHolderName=policy.getPolicyHolderName();
                AssuredDetail assuredDetail = (AssuredDetail) map.get("assuredDetail");
                String assuredName=assuredDetail.getFirstName();
                claimDataDto.setAssuredName(assuredName);
                claimDataDto.setClaimStatus(claimStatus.toString());
                claimDataDto.setPolicyNumber(policyNumber);
                claimDataDto.setPolicyHolderName(policyHolderName);
                //claimDataDto.setModifiedOn();
                return claimDataDto;
            }
        }).collect(Collectors.toList());

    }

  public List<GLClaimDataDto> getClaimRecordForAmendment(SearchClaimDto searchClaimDto){
      return null;
  }
}


/*
 public List<GLInsuredDetailDto> assuredSearches(GLAssuredSearchDto assuredSearchDto) {
        List<Map> assuredSearchList = glFinder.assuredSearch(assuredSearchDto.getPolicyNumber());
        List<InsuredDependent> insuredDependentList = Lists.newArrayList();
        for (Map assuredSearchMap : assuredSearchList) {
            List<Insured> insureds = (List<Insured>) assuredSearchMap.get("insureds");
            for (Insured insured : insureds) {
                Set<InsuredDependent> insuredDependents = insured.getInsuredDependents();
                for (InsuredDependent insuredDependent : insuredDependents) {
                    String firstName = insuredDependent.getFirstName() != null ? insuredDependent.getFirstName() : "";
                    String lastName = insuredDependent.getLastName() != null ? insuredDependent.getLastName() : "";
                    String nrcNumber = insuredDependent.getNrcNumber() != null ? insuredDependent.getNrcNumber() : "";
                    String manNumber = insuredDependent.getManNumber() != null ? insuredDependent.getManNumber() : "";
                    Gender gender = insuredDependent.getGender() != null ? insuredDependent.getGender() : null;
                    FamilyId familyId = insuredDependent.getFamilyId() != null ? insuredDependent.getFamilyId() : null;
                    LocalDate dateOfBirth = insuredDependent.getDateOfBirth() != null ? insuredDependent.getDateOfBirth() : null;
                    if (firstName.equals(assuredSearchDto.getFirstName()) && lastName.equals(assuredSearchDto.getSurName()) &&
                            nrcNumber.equals(assuredSearchDto.getNrcNumber()) && manNumber.equals(assuredSearchDto.getManNumber()) &&
                            gender.equals(assuredSearchDto.getGender()) && dateOfBirth.equals(assuredSearchDto.getDateOfBirth())) {
                        insuredDependentList.add(insuredDependent);
                    }
                }
            }
        }

         List<Insured> insureds = Lists.newArrayList();
        for (Map insuredMap : assuredSearchList) {
            List<Insured> insuredsList = (List<Insured>) insuredMap.get("insureds");
            for (Insured insured : insuredsList) {
               String category = insured.getCategory() != null ? insured.getCategory() : "";
                String relationship = insured.getRelationship() != null ? insured.getRelationship().description : Relationship.SELF.description;
                if (category.equals(assuredSearchDto.getAssuredSearchDto().getCategory()) && relationship.equals(assuredSearchDto.getAssuredSearchDto().getRelationShip())) {
                    insureds.add(insured);
                }
            }
        }

        List<GLInsuredDetailDto> searchList = Lists.newArrayList();
        for (InsuredDependent insuredDependent : insuredDependentList) {
            GLInsuredDetailDto glInsuredDetailDto = new GLInsuredDetailDto(insuredDependent.getFirstName(), insuredDependent.getLastName(),
                    insuredDependent.getDateOfBirth() != null ? insuredDependent.getDateOfBirth().toString(AppConstants.DD_MM_YYY_FORMAT) : "", insuredDependent.getGender() != null ? insuredDependent.getGender().name() : "", insuredDependent.getNrcNumber(),
                    insuredDependent.getManNumber(), insuredDependent.getFamilyId() != null ? insuredDependent.getFamilyId().getFamilyId() : "");
            searchList.add(glInsuredDetailDto);
        }
        for (Insured insured : insureds) {
            GLInsuredDetailDto glInsuredDetailDto = new GLInsuredDetailDto(insured.getFirstName(), insured.getLastName(),
                    insured.getDateOfBirth() != null ? insured.getDateOfBirth().toString(AppConstants.DD_MM_YYY_FORMAT) : "", insured.getGender() != null ? insured.getGender().name() : "", insured.getNrcNumber(),
                    insured.getManNumber(), insured.getFamilyId() != null ? insured.getFamilyId().getFamilyId() : "");
            searchList.add(glInsuredDetailDto);
        }
        return searchList;





        //return null;
    }
    }

 */
/*
 public List<GLProposerDocument> getUploadedMandatoryDocument(String proposalId){
        Map proposal = glProposalFinder.findProposalById(new ProposalId(proposalId));
        List<GLProposerDocument> uploadedDocuments = proposal.get("proposerDocuments") != null ? (List<GLProposerDocument>) proposal.get("proposerDocuments") : Lists.newArrayList();
        return uploadedDocuments;
    }
 */

/*
 public List<GLClaimMandatoryDocumentDto> findMandatoryDocuments(String claimId) {
        List<GLClaimDocument> uploadedDocuments = getUploadedMandatoryDocument(claimId);
        List<ClaimMandatoryDocumentDto> mandatoryDocuments = getMandatoryDocumentRequiredForSubmission(claimId);
        return getAllMandatoryDocuments(uploadedDocuments,mandatoryDocuments);
    }
    public List<GLClaimDocument> getUploadedMandatoryDocument(String claimId){
        Map claim = glClaimFinder.findClaimById(claimId);
        List<GLClaimDocument> uploadedDocuments = claim.get("claimDocuments") != null ? (List<GLClaimDocument>)claim.get("claimDocuments") : Lists.newArrayList();
        return uploadedDocuments;
    }

    public List<ClaimMandatoryDocumentDto> getMandatoryDocumentRequiredForSubmission(String claimId){
        Map claimMap = glClaimFinder.findClaimById(claimId);

        PlanPremiumDetail planPremiumDetail = (PlanPremiumDetail)claimMap.get("planPremiumDetail");
        List<CoveragePremiumDetail> coveragePremiumList = (List<CoveragePremiumDetail>) claimMap.get("coveragePremiumDetails");
        SearchDocumentDetailDto searchDocumentDetailDto = new SearchDocumentDetailDto(planPremiumDetail.getPlanId());

        List<ClaimMandatoryDocumentDto> mandatoryDocuments = glClaimFinder.getMandatoryDocuments(searchDocumentDetailDto, ProcessType.CLAIM);


        return mandatoryDocuments;
    }

 */