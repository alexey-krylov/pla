package com.pla.grouplife.proposal.repository;

import com.pla.grouphealth.proposal.domain.model.GroupHealthProposalStatusAudit;
import com.pla.grouplife.proposal.domain.model.GroupLifeProposalStatusAudit;
import com.pla.sharedkernel.identifier.ProposalId;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Created by Samir on 7/14/2015.
 */
public interface GLProposalStatusAuditRepository extends MongoRepository<GroupLifeProposalStatusAudit, ObjectId> {

    public List<GroupLifeProposalStatusAudit> findByProposalId(ProposalId proposalId);

}
