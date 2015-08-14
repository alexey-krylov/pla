package com.pla.grouphealth.policy.repository;

import com.pla.grouphealth.policy.domain.model.GroupHealthPolicy;
import com.pla.sharedkernel.identifier.PolicyId;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by Samir on 7/28/2015.
 */
public interface GHPolicyRepository extends MongoRepository<GroupHealthPolicy, PolicyId> {
}
