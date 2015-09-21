package com.pla.grouplife.claim.application.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.pla.grouplife.claim.presentation.dto.AssuredSearchDto;
import com.pla.grouplife.claim.presentation.dto.GLInsuredDetailDto;
import com.pla.grouplife.sharedresource.dto.GLPolicyDetailDto;
import com.pla.grouplife.sharedresource.dto.SearchGLPolicyDto;
import com.pla.grouplife.sharedresource.model.vo.Insured;
import com.pla.grouplife.sharedresource.model.vo.InsuredDependent;
import com.pla.grouplife.sharedresource.model.vo.Proposer;
import com.pla.grouplife.sharedresource.query.GLFinder;
import com.pla.sharedkernel.domain.model.FamilyId;
import com.pla.sharedkernel.domain.model.Gender;
import com.pla.sharedkernel.domain.model.PolicyNumber;
import com.pla.sharedkernel.domain.model.Relationship;
import org.joda.time.DateTime;
import org.nthdimenzion.common.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public List<GLPolicyDetailDto> searchPolicy(SearchGLPolicyDto searchGLPolicyDto) {
        List<Map> searchedPolices = glFinder.searchPolicy(searchGLPolicyDto.getPolicyNumber(),searchGLPolicyDto.getPolicyHolderName(),searchGLPolicyDto.getClientId(), new String[]{"IN_FORCE"},searchGLPolicyDto.getProposalNumber());
        if (isEmpty(searchedPolices)) {
            return Lists.newArrayList();
        }
        if (isNotEmpty(searchGLPolicyDto.getClientId())){
            searchedPolices =  searchedPolices.parallelStream().filter(new Predicate<Map>() {
                @Override
                public boolean test(Map searchedPolicyMap) {
                    boolean isFound = false;
                    List<Insured> insuredList = (List<Insured>) searchedPolicyMap.get("insureds");
                    for (Insured insured : insuredList) {
                        FamilyId familyId = insured.getFamilyId();
                        if (familyId != null) {
                            isFound = searchGLPolicyDto.getClientId().equals(familyId.getFamilyId());
                        }
                        if (isFound) {
                            return isFound;
                        }
                        Set<InsuredDependent> insuredDependentList = insured.getInsuredDependents();
                        for (InsuredDependent insuredDependentMap : insuredDependentList) {
                            FamilyId dependentFamilyId = insuredDependentMap.getFamilyId();
                            if (dependentFamilyId != null) {
                                return searchGLPolicyDto.getClientId().equals(dependentFamilyId.getFamilyId());
                            }
                        }
                    }
                    return false;
                }
            }).collect(Collectors.toList());
        }

        List<GLPolicyDetailDto> transformedPolicies = searchedPolices.stream().map(new Function<Map, GLPolicyDetailDto>() {
            @Override
            public GLPolicyDetailDto apply(Map map) {
                GLPolicyDetailDto policyDetailDto = transformToDto(map);
                return policyDetailDto;
            }
        }).collect(Collectors.toList());
        return transformedPolicies;
    }

    public List<GLInsuredDetailDto> assuredSearch(AssuredSearchDto assuredSearchDto){
        List<Map> assuredSearchList = glFinder.assuredSearch(assuredSearchDto.getPolicyNumber());
        List<InsuredDependent> insuredDependentList = null;
        for(Map assuredSearchMap  : assuredSearchList){
            List<Insured> insureds = (List<Insured>) assuredSearchMap.get("insureds");
            for (Insured insured :  insureds){
                Set<InsuredDependent> insuredDependents = insured.getInsuredDependents();
                insuredDependentList =  insuredDependents.parallelStream().filter(new Predicate<InsuredDependent>() {
                    @Override
                    public boolean test(InsuredDependent insuredDependent) {
                        String category = insuredDependent.getCategory() != null ? insuredDependent.getCategory() : "";
                        String relationship = insuredDependent.getRelationship() != null ? insuredDependent.getRelationship().description : "";
                        return (category.equals(assuredSearchDto.getCategory()) && relationship.equals(assuredSearchDto.getRelationShip()));
                    }
                }).collect(Collectors.toList());
            }
        }

        Set<Map> insureds = assuredSearchList.parallelStream().filter(new Predicate<Map>() {
            @Override
            public boolean test(Map insuredMap) {
                List<Insured> insureds = (List<Insured>) insuredMap.get("insureds");
                for (Insured insured : insureds) {
                    String category  = insured.getCategory()!=null?insured.getCategory():"";
                    String relationship  = insured.getRelationship()!=null?insured.getRelationship().description: Relationship.SELF.description;
                    return (category.equals(assuredSearchDto.getCategory()) && relationship.equals(assuredSearchDto.getRelationShip()));
                }
                return false;
            }
        }).collect(Collectors.toSet());

        List searchList = Lists.newArrayList();
        for (InsuredDependent insuredDependent : insuredDependentList){
            GLInsuredDetailDto glInsuredDetailDto = new GLInsuredDetailDto(insuredDependent.getFirstName(),insuredDependent.getLastName(),
                    insuredDependent.getDateOfBirth()!=null? insuredDependent.getDateOfBirth().toString(AppConstants.DD_MM_YYY_FORMAT):"",insuredDependent.getGender()!=null?insuredDependent.getGender().name():"",insuredDependent.getNrcNumber(),
                    insuredDependent.getManNumber(),insuredDependent.getFamilyId()!=null?insuredDependent.getFamilyId().getFamilyId():"");
            searchList.add(glInsuredDetailDto);
        }
        for (Map insured : insureds){
            String firstName = insured.get("firstName")!=null?insured.get("firstName").toString():"";
            String lastName = insured.get("lastName")!=null?insured.get("lastName").toString():"";
            String gender = insured.get("gender")!=null? Gender.valueOf(insured.get("gender").toString()).name():"";
            String nrcNumber = insured.get("nrcNumber")!=null?insured.get("nrcNumber").toString():"";
            String manNumber = insured.get("manNumber")!=null?insured.get("manNumber").toString():"";
            String clientId = insured.get("familyId")!=null?insured.get("familyId").toString():"";
            String dateOfBirth = insured.get("dateOfBirth")!=null?new DateTime(insured.get("dateOfBirth")).toString(AppConstants.DD_MM_YYY_FORMAT):"";
            GLInsuredDetailDto glInsuredDetailDto = new GLInsuredDetailDto(firstName,lastName,
                    dateOfBirth,gender,nrcNumber,manNumber,clientId);
            searchList.add(glInsuredDetailDto);
        }
        return searchList;
    }

    public Map<String, Object> getConfiguredRelationShipAndCategory(String policyId){
        Set<String> categoryList = Sets.newLinkedHashSet();
        Set<String> relationShipList = Sets.newLinkedHashSet();
        Map policy =  glFinder.findPolicyById(policyId);
        if (isEmpty(policy)){
            return Collections.EMPTY_MAP;
        }
        List<Insured> insuredList  = (List<Insured>) policy.get("insureds");
        for (Insured insured : insuredList){
            categoryList.add(insured.getCategory()!=null?insured.getCategory():"");
            relationShipList.add(insured.getRelationship()!=null?insured.getRelationship().name():"");
            Set<InsuredDependent> insuredDependentList = insured.getInsuredDependents();
            for (InsuredDependent insuredDependentMap  : insuredDependentList){
                categoryList.add(insuredDependentMap.getCategory()!=null?insuredDependentMap.getCategory():"");
                relationShipList.add(insuredDependentMap.getRelationship()!=null?insuredDependentMap.getRelationship().name():"");
            }
        }
        Map<String,Object> relationCategoryMap = Maps.newLinkedHashMap();
        relationCategoryMap.put("relationship",relationShipList);
        relationCategoryMap.put("category",categoryList);
        return relationCategoryMap;
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

}
