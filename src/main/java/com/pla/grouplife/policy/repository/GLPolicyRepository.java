package com.pla.grouplife.policy.repository;

import com.pla.grouplife.policy.domain.model.GroupLifePolicy;
import com.pla.sharedkernel.identifier.PolicyId;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by Samir on 7/29/2015.
 */
public interface GLPolicyRepository extends MongoRepository<GroupLifePolicy, PolicyId> {
}
