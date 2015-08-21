package com.pla.grouphealth.proposal.repository;

import com.pla.grouphealth.proposal.domain.model.GroupHealthProposal;
import com.pla.sharedkernel.identifier.ProposalId;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by Samir on 7/6/2015.
 */
public interface GHProposalRepository extends MongoRepository<GroupHealthProposal,ProposalId>{
}
