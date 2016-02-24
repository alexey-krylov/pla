package com.pla.grouphealth.claim.cashless.repository.claim;

import com.pla.grouphealth.claim.cashless.domain.model.claim.GroupHealthCashlessClaim;
import com.pla.grouphealth.claim.cashless.domain.model.preauthorization.PreAuthorizationRequest;
import com.pla.sharedkernel.domain.model.PolicyNumber;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.pla.grouphealth.claim.cashless.domain.model.claim.GroupHealthCashlessClaim.*;

/**
 * Author - Mohan Sharma Created on 12/30/2015.
 */
public interface GroupHealthCashlessClaimRepository extends MongoRepository<GroupHealthCashlessClaim, String>{

    Page<GroupHealthCashlessClaim> findAllByClaimProcessorUserIdInAndStatusIn(List<String> users, List<Status> statusList, Pageable p);

    List<GroupHealthCashlessClaim> findAllByStatusAndClaimUnderWriterUserIdIn(Status status, List<String> users);

    List<GroupHealthCashlessClaim> findAllByGroupHealthCashlessClaimPolicyDetailPolicyNumberAndGroupHealthCashlessClaimPolicyDetailAssuredDetailClientIdAndStatus(PolicyNumber policyNumber, String clientId, Status status);

    List<GroupHealthCashlessClaim> findAllByGroupHealthCashlessClaimPolicyDetailAssuredDetailClientIdAndGroupHealthCashlessClaimDrugServicesServiceNameInAndStatus(String clientId, Set<String> services, Status status);

    List<GroupHealthCashlessClaim> findAllByStatusAndServiceMismatchProcessorIdIn(Status status, List<String> userNames);

    List<GroupHealthCashlessClaim> findAllByStatusAndBillMismatchProcessorIdIn(Status status, List<String> userNames);

    Page<GroupHealthCashlessClaim> findAllByClaimReopenProcessorUserIdInAndStatusIn(List<String> users, List<Status> statusList, Pageable p);

    Page<GroupHealthCashlessClaim> findAllByClaimAmendmentProcessorUserIdInAndStatusIn(List<String> users, List<Status> statusList, Pageable p);

    List<GroupHealthCashlessClaim> findAllByBatchNumber(String batchNumber);
}
