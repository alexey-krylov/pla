package com.pla.grouphealth.claim.cashless.application.service;

import com.pla.core.hcp.domain.model.HCP;
import com.pla.core.hcp.domain.model.HCPCode;
import com.pla.core.hcp.query.HCPFinder;
import com.pla.grouphealth.claim.cashless.domain.model.PreAuthorization;
import com.pla.grouphealth.claim.cashless.domain.model.PreAuthorizationDetail;
import com.pla.grouphealth.claim.cashless.domain.model.PreAuthorizationId;
import com.pla.grouphealth.claim.cashless.presentation.dto.ClaimantHCPDetailDto;
import com.pla.grouphealth.claim.cashless.presentation.dto.ClaimantPolicyDetailDto;
import com.pla.grouphealth.claim.cashless.presentation.dto.PreAuthorizationClaimantDetailDto;
import com.pla.grouphealth.claim.cashless.repository.PreAuthorizationRepository;
import com.pla.grouphealth.policy.domain.model.GroupHealthPolicy;
import com.pla.grouphealth.policy.repository.GHPolicyRepository;
import com.pla.sharedkernel.identifier.PolicyId;
import lombok.NoArgsConstructor;
import org.nthdimenzion.ddd.domain.annotations.DomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;

import static org.nthdimenzion.utils.UtilValidator.*;
import static org.springframework.util.Assert.*;

/**
 * Created by Mohan Sharma on 1/6/2016.
 */
@DomainService
@NoArgsConstructor
public class PreAuthorizationRequestService {
    @Autowired
    private PreAuthorizationRepository preAuthorizationRepository;
    @Autowired
    private HCPFinder hcpFinder;
    @Autowired
    GHPolicyRepository ghPolicyRepository;

    public PreAuthorizationClaimantDetailDto getPreAuthorizationById(PreAuthorizationId preAuthorizationId) {
        PreAuthorization preAuthorization = preAuthorizationRepository.findOne(preAuthorizationId);
        if(isNotEmpty(preAuthorization)){
            return constructPreAuthorizationClaimantDetailDtoFromPreAuthorization(preAuthorization);
        }
        return PreAuthorizationClaimantDetailDto.getInstance();
    }

    private PreAuthorizationClaimantDetailDto constructPreAuthorizationClaimantDetailDtoFromPreAuthorization(PreAuthorization preAuthorization) {
        PreAuthorizationDetail preAuthorizationDetail = preAuthorization.getPreAuthorizationDetails().iterator().next();
        notNull(preAuthorizationDetail, "PreAuthorizationDetail cannot be null");
        PreAuthorizationClaimantDetailDto preAuthorizationClaimantDetailDto = PreAuthorizationClaimantDetailDto.getInstance()
                .updateWithBatchNumber(preAuthorization.getBatchNumber())
                .updateWithPreAuthorizationId(preAuthorization.getPreAuthorizationId().getPreAuthorizationId())
                .updateWithPreAuthorizationDate(preAuthorization.getBatchDate())
                .updateWithClaimantHCPDetailDto(constructClaimantHCPDetailDto(preAuthorization.getHcpCode(), preAuthorizationDetail.getHospitalizationEvent()))
                .updateWithClaimantPolicyDetailDto(constructClaimantPolicyDetailDto(preAuthorizationDetail.getPolicyNumber()));
        return null;
    }

    private ClaimantPolicyDetailDto constructClaimantPolicyDetailDto(String policyNumber) {
        GroupHealthPolicy groupHealthPolicy = ghPolicyRepository.findPolicyByPolicyNumber(policyNumber);
        if(isNotEmpty(groupHealthPolicy)){
            ClaimantPolicyDetailDto claimantPolicyDetailDto = ClaimantPolicyDetailDto.getInstance();
                   // .updateWith;

        }
        return ClaimantPolicyDetailDto.getInstance();
    }

    private ClaimantHCPDetailDto constructClaimantHCPDetailDto(HCPCode hcpCode, String hospitalizationEvent) {
        HCP hcp = hcpFinder.getHCPByHCPCode(hcpCode.getHcpCode());
        if(isNotEmpty(hcp)){
            ClaimantHCPDetailDto claimantHCPDetailDto = ClaimantHCPDetailDto.getInstance()
                    .updateWithHospitalizationEvent(hospitalizationEvent)
                    .updateWithAddress(hcp.getHcpAddress())
                    .updateWithHCPName(hcp.getHcpName())
                    .updateWithHCPCode(hcp.getHcpCode());
            return claimantHCPDetailDto;
        }
        return ClaimantHCPDetailDto.getInstance();
    }

    public Map<String, Object> getPolicyByPreAuthorizationId(PreAuthorizationId preAuthorizationId) {

        return null;
    }
}
