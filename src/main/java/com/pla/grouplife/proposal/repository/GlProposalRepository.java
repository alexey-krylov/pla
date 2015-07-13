package com.pla.grouplife.proposal.repository;

import com.pla.grouplife.proposal.domain.model.GroupLifeProposal;
import com.pla.sharedkernel.identifier.ProposalId;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by Samir on 5/26/2015.
 */
public interface GlProposalRepository extends MongoRepository<GroupLifeProposal, ProposalId> {

}
