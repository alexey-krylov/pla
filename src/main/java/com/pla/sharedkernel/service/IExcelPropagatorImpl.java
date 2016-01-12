package com.pla.sharedkernel.service;

import com.pla.grouphealth.policy.domain.model.GroupHealthPolicy;
import com.pla.grouphealth.policy.repository.GHPolicyRepository;
import com.pla.publishedlanguage.contract.IExcelPropagator;
import com.pla.sharedkernel.domain.model.PolicyNumber;
import com.pla.sharedkernel.identifier.PolicyId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Author - Mohan Sharma Created on 1/3/2016.
 */
@Service
public class IExcelPropagatorImpl implements IExcelPropagator{

    @Autowired
    GHPolicyRepository ghPolicyRepository;

    @Override
    public GroupHealthPolicy findPolicyByPolicyNumber(String policyNumber) {
        return ghPolicyRepository.findPolicyByPolicyNumber(policyNumber);
    }
}
