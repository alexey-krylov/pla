package com.pla.grouphealth.claim.cashless.repository;

import com.pla.grouphealth.claim.cashless.domain.model.PreAuthorization;
import com.pla.grouphealth.claim.cashless.domain.model.PreAuthorizationId;
import com.pla.grouphealth.claim.cashless.domain.model.PreAuthorizationRequest;
import com.pla.grouphealth.claim.cashless.domain.model.PreAuthorizationRequestId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

/**
 * Author - Mohan Sharma Created on 12/30/2015.
 */
public interface PreAuthorizationRequestRepository extends MongoRepository<PreAuthorizationRequest, PreAuthorizationRequestId>{

    @Query("{'preAuthorizationRequestId.preAuthorizationRequestId' : ?0}")
    PreAuthorizationRequest findByPreAuthorizationRequestId(String preAuthorizationRequestId);

}
