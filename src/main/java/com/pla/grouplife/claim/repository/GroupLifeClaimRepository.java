package com.pla.grouplife.claim.repository;

import com.pla.grouplife.claim.domain.model.ClaimStatus;
import com.pla.grouplife.claim.domain.model.GroupLifeClaim;
import com.pla.sharedkernel.domain.model.ClaimId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Created by ak
 */
public interface GroupLifeClaimRepository extends MongoRepository<GroupLifeClaim, ClaimId> {
    List<GroupLifeClaim> findAllByClaimStatusIn(List<ClaimStatus> statusList);
}
