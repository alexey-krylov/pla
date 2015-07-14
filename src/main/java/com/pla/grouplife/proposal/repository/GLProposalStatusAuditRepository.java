package com.pla.grouplife.proposal.repository;

import com.pla.grouplife.proposal.domain.model.GroupLifeProposalStatusAudit;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by Samir on 7/14/2015.
 */
public interface GLProposalStatusAuditRepository extends MongoRepository<GroupLifeProposalStatusAudit, ObjectId> {
}
