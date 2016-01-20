package com.pla.publishedlanguage.contract;

import com.pla.grouphealth.policy.domain.model.GroupHealthPolicy;
import com.pla.sharedkernel.identifier.PolicyId;

/**
 * Author - Mohan Sharma Created on 1/3/2016.
 */
public interface IExcelPropagator {

    GroupHealthPolicy findPolicyByPolicyNumber(String policyNumber);

    boolean checkIfClientBelongsToTheGivenPolicy(String clientId, String policyNumber);

    String  checkServiceAndDrugCoverdUnderThePolicy(String clientId, String policyNumber, String service);

    String  compareHcpRateByHcpService(String hcpCode, String value);
}
