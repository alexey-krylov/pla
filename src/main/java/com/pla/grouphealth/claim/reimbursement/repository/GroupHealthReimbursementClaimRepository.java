package com.pla.grouphealth.claim.reimbursement.repository;

import com.pla.grouphealth.claim.reimbursement.domain.model.GroupHealthReimbursementClaim;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Author - Mohan Sharma Created on 12/30/2015.
 */
public interface GroupHealthReimbursementClaimRepository extends MongoRepository<GroupHealthReimbursementClaim, String>{
}
