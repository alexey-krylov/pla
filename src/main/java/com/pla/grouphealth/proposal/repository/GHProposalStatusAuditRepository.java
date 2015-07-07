package com.pla.grouphealth.proposal.repository;

import com.pla.grouphealth.proposal.domain.model.GroupHealthProposalStatusAudit;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by Samir on 7/6/2015.
 */
public interface GHProposalStatusAuditRepository extends MongoRepository<GroupHealthProposalStatusAudit, ObjectId> {
}
