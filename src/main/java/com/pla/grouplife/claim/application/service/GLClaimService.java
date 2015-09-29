package com.pla.grouplife.claim.application.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.pla.grouplife.claim.presentation.dto.AssuredSearchDto;
import com.pla.grouplife.claim.presentation.dto.ClaimIntimationDetailDto;
import com.pla.grouplife.claim.presentation.dto.GLInsuredDetailDto;
import com.pla.grouplife.sharedresource.dto.GLPolicyDetailDto;
import com.pla.grouplife.sharedresource.dto.SearchGLPolicyDto;
import com.pla.grouplife.sharedresource.model.vo.Insured;
import com.pla.grouplife.sharedresource.model.vo.InsuredDependent;
import com.pla.grouplife.sharedresource.model.vo.Proposer;
import com.pla.grouplife.sharedresource.query.GLFinder;
import com.pla.sharedkernel.domain.model.FamilyId;
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

    public List<ClaimIntimationDetailDto> getClaimIntimationDetail(String policyNumber){
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
                    String relationship = insuredDependent.getRelationship() != null ? insuredDependent.getRelationship().description : "";
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
        relationCategoryMap.put("policyNumber",policyNumber);
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
}
