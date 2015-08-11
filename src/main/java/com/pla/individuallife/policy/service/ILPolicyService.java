package com.pla.individuallife.policy.service;

import com.google.common.collect.Lists;
import com.pla.individuallife.policy.finder.ILPolicyFinder;
import com.pla.individuallife.policy.presentation.dto.PolicyDetailDto;
import com.pla.individuallife.policy.presentation.dto.SearchILPolicyDto;
import com.pla.individuallife.sharedresource.model.vo.Proposer;
import com.pla.sharedkernel.domain.model.PolicyNumber;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.nthdimenzion.utils.UtilValidator.isEmpty;

/**
 * Created by Admin on 8/4/2015.
 */
@Service
public class ILPolicyService {

    @Autowired
    private ILPolicyFinder ilPolicyFinder;


    private PolicyDetailDto transformToDto(Map policyMap) {
        DateTime inceptionDate = policyMap.get("inceptionOn") != null ? new DateTime(policyMap.get("inceptionOn")) : null;
        DateTime expiryDate = policyMap.get("expiredOn") != null ? new DateTime(policyMap.get("expiredOn")) : null;
        Proposer ghProposer = policyMap.get("proposer") != null ? (Proposer) policyMap.get("proposer") : null;
        PolicyNumber policyNumber = policyMap.get("policyNumber") != null ? (PolicyNumber) policyMap.get("policyNumber") : null;//
        PolicyDetailDto policyDetailDto = new PolicyDetailDto();
        policyDetailDto.setPolicyId(policyMap.get("_id").toString());
        policyDetailDto.setInceptionDate(inceptionDate);
        policyDetailDto.setExpiryDate(expiryDate);
        policyDetailDto.setPolicyHolderName(ghProposer != null ? ghProposer.getFirstName() : "");
        policyDetailDto.setPolicyNumber(policyNumber.getPolicyNumber());
        policyDetailDto.setStatus((String) policyMap.get("policyStatus"));
        return policyDetailDto;
    }

    public PolicyDetailDto getPolicyDetail(String policyId) {
        Map policyMap = ilPolicyFinder.findPolicyById(policyId);
        PolicyDetailDto policyDetailDto = transformToDto(policyMap);
        return policyDetailDto;
    }


    public List<PolicyDetailDto> searchPolicy(SearchILPolicyDto searchILPolicyDto) {
        List<Map> searchedPolices = ilPolicyFinder.searchPolicy(searchILPolicyDto.getPolicyNumber(), searchILPolicyDto.getPolicyHolderName(),searchILPolicyDto.getProposalNumber());
        if (isEmpty(searchedPolices)) {
            return Lists.newArrayList();
        }
        List<PolicyDetailDto> transformedPolicies = searchedPolices.stream().map(new Function<Map,PolicyDetailDto>() {
            @Override
            public PolicyDetailDto apply(Map map) {
                PolicyDetailDto policyDetailDto = transformToDto(map);
                return policyDetailDto;
            }
        }).collect(Collectors.toList());
        return transformedPolicies;
    }

}
