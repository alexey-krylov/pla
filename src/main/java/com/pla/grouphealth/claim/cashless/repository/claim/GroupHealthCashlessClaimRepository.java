package com.pla.grouphealth.claim.cashless.repository.claim;

import com.pla.grouphealth.claim.cashless.domain.model.claim.GroupHealthCashlessClaim;
import com.pla.grouphealth.claim.cashless.domain.model.preauthorization.PreAuthorizationRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.ArrayList;
import java.util.List;

import static com.pla.grouphealth.claim.cashless.domain.model.claim.GroupHealthCashlessClaim.*;

/**
 * Author - Mohan Sharma Created on 12/30/2015.
 */
public interface GroupHealthCashlessClaimRepository extends MongoRepository<GroupHealthCashlessClaim, String>{

    Page<GroupHealthCashlessClaim> findAllByClaimProcessorUserIdInAndStatusIn(List<String> users, List<Status> statusList, Pageable p);

}
