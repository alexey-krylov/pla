package com.pla.grouphealth.claim.cashless.repository.claim;

import com.pla.grouphealth.claim.cashless.domain.model.claim.GroupHealthCashlessClaimBatchDetail;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Author - Mohan Sharma Created on 12/30/2015.
 */
public interface GroupHealthCashlessClaimBatchDetailRepository extends MongoRepository<GroupHealthCashlessClaimBatchDetail, String>{
}
