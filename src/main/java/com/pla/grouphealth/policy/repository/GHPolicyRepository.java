package com.pla.grouphealth.policy.repository;

import com.pla.grouphealth.policy.domain.model.GroupHealthPolicy;
import com.pla.grouphealth.policy.domain.model.PolicyStatus;
import com.pla.sharedkernel.domain.model.FamilyId;
import com.pla.sharedkernel.domain.model.PolicyNumber;
import com.pla.sharedkernel.identifier.PolicyId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

/**
 * Created by Samir on 7/28/2015.
 * Modified By Mohan Sharma to fetch By familyId
 */
public interface GHPolicyRepository extends MongoRepository<GroupHealthPolicy, PolicyId> {
    @Query("{'policyNumber.policyNumber' : ?0 }")
    GroupHealthPolicy findPolicyByPolicyNumber(String policyNumber);
    @Query("{'insureds.familyId' : ?0, 'status':?1}")
    GroupHealthPolicy findDistinctPolicyByFamilyId(FamilyId familyId, PolicyStatus inForce);
    @Query("{'insureds.insuredDependents.familyId' : ?0 ,'status':?1}")
    GroupHealthPolicy findDistinctPolicyByDependentFamilyId(FamilyId familyId, PolicyStatus inForce);
}
