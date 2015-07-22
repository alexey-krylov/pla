package com.pla.grouphealth.proposal.repository;

import com.pla.grouphealth.proposal.domain.model.GroupHealthProposalStatusAudit;
import com.pla.sharedkernel.identifier.ProposalId;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Created by Samir on 7/6/2015.
 */
public interface GHProposalStatusAuditRepository extends MongoRepository<GroupHealthProposalStatusAudit, ObjectId> {


    public List<GroupHealthProposalStatusAudit> findByProposalId(ProposalId proposalId);
}
