package com.pla.grouphealth.claim.cashless.repository;

import com.pla.grouphealth.claim.cashless.domain.model.PreAuthorization;
import com.pla.grouphealth.claim.cashless.domain.model.PreAuthorizationId;
import com.pla.grouphealth.claim.cashless.domain.model.PreAuthorizationRequest;
import com.pla.grouphealth.claim.cashless.domain.model.PreAuthorizationRequestId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.ArrayList;
import java.util.List;

/**
 * Author - Mohan Sharma Created on 12/30/2015.
 */
public interface PreAuthorizationRequestRepository extends MongoRepository<PreAuthorizationRequest, String>{

    @Query("{'preAuthorizationRequestId' : ?0}")
    PreAuthorizationRequest findByPreAuthorizationRequestId(String preAuthorizationRequestId);

    Page<PreAuthorizationRequest> findAllByBatchUploaderUserIdAndStatusIn(String batchUploaderUserId, ArrayList<PreAuthorizationRequest.Status> statusList, Pageable p);

    @Query("{'preAuthorizationUnderWriterUserId' : ?0, 'status' : ?1}")
    List<PreAuthorizationRequest> findAllByPreAuthorizationUnderWriterUserIdAndStatus(String preAuthorizationUnderWriterUserId, PreAuthorizationRequest.Status status);

    List<PreAuthorizationRequest> findAllByStatusAndPreAuthorizationUnderWriterUserIdIn(PreAuthorizationRequest.Status status, List<String> users);
}
