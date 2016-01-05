package com.pla.publishedlanguage.contract;

import com.pla.grouphealth.policy.domain.model.GroupHealthPolicy;
import com.pla.sharedkernel.identifier.PolicyId;

/**
 * Created by Mohan Sharma on 1/3/2016.
 */
public interface IExcelPropagator {
    GroupHealthPolicy findPolicyByPolicyNumber(String policyNumber);
}
