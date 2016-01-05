package com.pla.grouphealth.policy.repository;

import com.pla.grouphealth.policy.domain.model.GroupHealthPolicy;
import com.pla.sharedkernel.domain.model.PolicyNumber;
import com.pla.sharedkernel.identifier.PolicyId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

/**
 * Created by Samir on 7/28/2015.
 */
public interface GHPolicyRepository extends MongoRepository<GroupHealthPolicy, PolicyId> {
    @Query("{'policyNumber.policyNumber' : ?0 }")
    GroupHealthPolicy findPolicyByPolicyNumber(String policyNumber);
}
