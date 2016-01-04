package com.pla.grouphealth.claim.cashless.repository;

import com.pla.grouphealth.claim.cashless.domain.model.PreAuthorization;
import com.pla.grouphealth.claim.cashless.domain.model.PreAuthorizationId;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by Mohan Sharma on 12/30/2015.
 */
public interface PreAuthorizationRepository extends MongoRepository<PreAuthorization, PreAuthorizationId>{
}
