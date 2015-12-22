package com.pla.grouplife.claim.repository;

import com.pla.grouplife.claim.domain.model.GLClaimSettlement;
import com.pla.sharedkernel.domain.model.ClaimSettlementId;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by nthdimensioncompany on 21/12/2015.
 */
public interface GLClaimSettlementRepository extends MongoRepository<GLClaimSettlement,ClaimSettlementId> {
}
