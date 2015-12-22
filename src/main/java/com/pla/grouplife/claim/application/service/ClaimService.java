package com.pla.grouplife.claim.application.service;

import com.google.common.collect.Lists;
import com.pla.grouplife.claim.presentation.dto.GLAssuredSearchDto;
import com.pla.grouplife.claim.presentation.dto.GLInsuredDetailDto;
import com.pla.grouplife.claim.presentation.dto.SearchPolicyDto;
import com.pla.grouplife.claim.query.GLClaimFinder;
import com.pla.grouplife.sharedresource.dto.GLPolicyDetailDto;
import com.pla.grouplife.sharedresource.model.vo.Insured;
import com.pla.grouplife.sharedresource.model.vo.InsuredDependent;
import com.pla.grouplife.sharedresource.model.vo.Proposer;
import com.pla.grouplife.sharedresource.query.GLFinder;
import com.pla.sharedkernel.domain.model.FamilyId;
import com.pla.sharedkernel.domain.model.Gender;
import com.pla.sharedkernel.domain.model.PolicyNumber;
import com.pla.sharedkernel.domain.model.Relationship;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.nthdimenzion.common.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.nthdimenzion.utils.UtilValidator.isEmpty;

/**
 * Created by ak
 */
@Service
public class ClaimService {

    @Autowired
    private GLClaimFinder glClaimFinder;
    @Autowired
    private GLFinder glFinder;

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

    public List<GLPolicyDetailDto> searchGLPolicy(PolicyNumber policyNumber) {

        List<Map> searchedPolices = glClaimFinder.searchPolicy(policyNumber.getPolicyNumber());

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

    public List<GLInsuredDetailDto> assuredSearch(GLAssuredSearchDto assuredSearchDto) {
        List<Map> assureSearchMap = glClaimFinder.assuredSearchDetail(assuredSearchDto.getFirstName(), assuredSearchDto.getSurName(), assuredSearchDto.getDateOfBirth(), assuredSearchDto.getClientId(),
                assuredSearchDto.getNrcNumber(), assuredSearchDto.getManNumber(), assuredSearchDto.getGender());
        return null;
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
//}



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




