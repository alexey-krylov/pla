package com.pla.grouphealth.policy.application.service;

import com.google.common.collect.Lists;
import com.pla.grouphealth.policy.presentation.dto.PolicyDetailDto;
import com.pla.grouphealth.policy.presentation.dto.SearchGHPolicyDto;
import com.pla.grouphealth.policy.query.GHPolicyFinder;
import com.pla.grouphealth.sharedresource.model.vo.GHProposer;
import com.pla.sharedkernel.domain.model.PolicyNumber;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.nthdimenzion.utils.UtilValidator.isEmpty;

/**
 * Created by Samir on 7/9/2015.
 */
@Service
public class GHPolicyService {

    @Autowired
    private GHPolicyFinder ghPolicyFinder;


    public List<PolicyDetailDto> findAllPolicy() {
        List<Map> allPolicies = ghPolicyFinder.findAllPolicy();
        if (isEmpty(allPolicies)) {
            return Lists.newArrayList();
        }
        List<PolicyDetailDto> policies = allPolicies.stream().map(new Function<Map, PolicyDetailDto>() {
            @Override
            public PolicyDetailDto apply(Map map) {
                PolicyDetailDto policyDetailDto = transformToDto(map);
                return policyDetailDto;
            }
        }).collect(Collectors.toList());
        return policies;
    }

    public PolicyDetailDto getPolicyDetail(String policyId) {
        Map policyMap = ghPolicyFinder.findPolicyById(policyId);
        PolicyDetailDto policyDetailDto = transformToDto(policyMap);
        return policyDetailDto;
    }

    public List<PolicyDetailDto> searchPolicy(SearchGHPolicyDto searchGHPolicyDto) {
        List<Map> searchedPolices = ghPolicyFinder.searchPolicy(searchGHPolicyDto.getPolicyNumber(), searchGHPolicyDto.getPolicyHolderName());
        if (isEmpty(searchedPolices)) {
            return Lists.newArrayList();
        }
        List<PolicyDetailDto> transformedPolicies = searchedPolices.stream().map(new Function<Map, PolicyDetailDto>() {
            @Override
            public PolicyDetailDto apply(Map map) {
                PolicyDetailDto policyDetailDto = transformToDto(map);
                return policyDetailDto;
            }
        }).collect(Collectors.toList());
        return transformedPolicies;
    }

    private PolicyDetailDto transformToDto(Map policyMap) {
        DateTime inceptionDate = policyMap.get("inceptionOn") != null ? new DateTime((Date) policyMap.get("inceptionOn")) : null;
        DateTime expiryDate = policyMap.get("expiredOn") != null ? new DateTime((Date) policyMap.get("expiredOn")) : null;
        GHProposer ghProposer = policyMap.get("proposer") != null ? (GHProposer) policyMap.get("proposer") : null;
        PolicyNumber policyNumber = policyMap.get("policyNumber") != null ? (PolicyNumber) policyMap.get("policyNumber") : null;//
        PolicyDetailDto policyDetailDto = new PolicyDetailDto();
        policyDetailDto.setPolicyId(policyMap.get("_id").toString());
        policyDetailDto.setInceptionDate(inceptionDate);
        policyDetailDto.setExpiryDate(expiryDate);
        policyDetailDto.setPolicyHolderName(ghProposer != null ? ghProposer.getProposerName() : "");
        policyDetailDto.setPolicyNumber(policyNumber.getPolicyNumber());
        policyDetailDto.setStatus((String) policyMap.get("status"));
        return policyDetailDto;
    }
}
