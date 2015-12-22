package com.pla.grouplife.claim.repository;

import com.pla.grouplife.claim.domain.model.GroupLifeClaim;
import com.pla.sharedkernel.domain.model.ClaimId;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by ak
 */
public interface GroupLifeClaimRepository extends MongoRepository<GroupLifeClaim,ClaimId> {
}
