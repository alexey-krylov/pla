package com.pla.sharedkernel.service;

import com.pla.grouphealth.claim.cashless.application.service.preauthorization.PreAuthorizationRequestService;
import com.pla.grouphealth.policy.domain.model.GroupHealthPolicy;
import com.pla.grouphealth.policy.repository.GHPolicyRepository;
import com.pla.publishedlanguage.contract.IExcelPropagator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Author - Mohan Sharma Created on 1/3/2016.
 */
@Service
public class IExcelPropagatorImpl implements IExcelPropagator{

    @Autowired
    GHPolicyRepository ghPolicyRepository;
    @Autowired
    PreAuthorizationRequestService preAuthorizationRequestService;

    @Override
    public GroupHealthPolicy findPolicyByPolicyNumber(String policyNumber) {
        return ghPolicyRepository.findPolicyByPolicyNumber(policyNumber);
    }

    @Override
    public boolean checkIfClientBelongsToTheGivenPolicy(String clientId, String policyNumber) {
        return preAuthorizationRequestService.doesClientBelongToTheGivenPolicy(clientId, policyNumber);
    }

    public String checkServiceAndDrugCoverdUnderThePolicy(String clientId, String policyNumber, String service) {
        return preAuthorizationRequestService.checkServiceAndDrugCoveredUnderThePolicy(clientId, policyNumber, service);
    }

    @Override
    public String compareHcpRateByHcpService(String hcpCode, String service) {
        return preAuthorizationRequestService.compareHcpRateByHcpService(hcpCode, service);
    }
}
