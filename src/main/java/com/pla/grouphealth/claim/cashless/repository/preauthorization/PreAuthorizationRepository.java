package com.pla.grouphealth.claim.cashless.repository.preauthorization;

import com.pla.grouphealth.claim.cashless.domain.model.preauthorization.PreAuthorization;
import com.pla.grouphealth.claim.cashless.domain.model.preauthorization.PreAuthorizationId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

/**
 * Author - Mohan Sharma Created on 12/30/2015.
 */
public interface PreAuthorizationRepository extends MongoRepository<PreAuthorization, PreAuthorizationId>{
    @Query("{'preAuthorizationDetails.clientId' : ?0, 'preAuthorizationDetails.service' : ?1 }")
    List<PreAuthorization> findAllPreAuthorizationByServiceAndClientId(String clientId, String service);
    @Query("{'batchNumber' : ?0}")
    List<PreAuthorization> findAllPreAuthorizationByBatchNumber(String batchNumber);
}
