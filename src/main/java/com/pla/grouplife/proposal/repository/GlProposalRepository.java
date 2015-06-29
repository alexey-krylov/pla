package com.pla.grouplife.proposal.repository;

import com.pla.grouplife.proposal.domain.model.GroupLifeProposal;
import com.pla.sharedkernel.identifier.ProposalId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

/**
 * Created by Samir on 5/26/2015.
 */
public interface GlProposalRepository extends MongoRepository<GroupLifeProposal, ProposalId> {

    @Query(value = "{'proposalNumber' : ?0,'proposalStatus' : ?2, 'proposalId' : {'$ne' : ?1}}")
    List<GroupLifeProposal> findProposalByPropNumberAndStatusByExcludingGivenPropId(String proposalNumber, ProposalId proposalId, String proposalStatus);
}
