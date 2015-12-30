package com.pla.grouphealth.claim.cashless.repository;

import com.pla.grouphealth.claim.cashless.domain.model.GHCashlessClaim;
import com.pla.grouphealth.claim.cashless.domain.model.GHCashlessClaimId;
import com.pla.grouphealth.claim.cashless.domain.model.PreAuthorizationDetail;
import com.pla.grouphealth.claim.cashless.domain.model.PreAuthorizationDetailId;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by Mohan Sharma on 12/30/2015.
 */
public interface PreAuthorizationRepository extends MongoRepository<PreAuthorizationDetail, PreAuthorizationDetailId>{
}
