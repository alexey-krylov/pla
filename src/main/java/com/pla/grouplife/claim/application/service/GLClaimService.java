package com.pla.grouplife.claim.application.service;

import com.google.common.collect.Lists;
import com.pla.grouplife.sharedresource.dto.GLPolicyDetailDto;
import com.pla.grouplife.sharedresource.dto.SearchGLPolicyDto;
import com.pla.grouplife.sharedresource.model.vo.Proposer;
import com.pla.grouplife.sharedresource.query.GLFinder;
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
 * Created by Mirror on 8/26/2015.
 */
@Service
public class GLClaimService {

    @Autowired
    private GLFinder glFinder;

    public List<GLPolicyDetailDto> searchPolicy(SearchGLPolicyDto searchGLPolicyDto) {
        List<Map> searchedPolices = glFinder.searchPolicy(searchGLPolicyDto.getPolicyNumber(), searchGLPolicyDto.getPolicyHolderName(), new String[]{"IN_FORCE"});
        if (isEmpty(searchedPolices)) {
            return Lists.newArrayList();
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

    private GLPolicyDetailDto transformToDto(Map policyMap) {
        DateTime inceptionDate = policyMap.get("inceptionOn") != null ? new DateTime((Date) policyMap.get("inceptionOn")) : null;
        DateTime expiryDate = policyMap.get("expiredOn") != null ? new DateTime((Date) policyMap.get("expiredOn")) : null;
        Proposer glProposer = policyMap.get("proposer") != null ? (Proposer) policyMap.get("proposer") : null;
        PolicyNumber policyNumber = policyMap.get("policyNumber") != null ? (PolicyNumber) policyMap.get("policyNumber") : null;//
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
