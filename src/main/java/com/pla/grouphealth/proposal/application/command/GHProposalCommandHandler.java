package com.pla.grouphealth.proposal.application.command;

import com.pla.grouphealth.proposal.domain.model.GroupHealthProposal;
import com.pla.grouphealth.proposal.domain.service.GroupHealthProposalFactory;
import com.pla.grouphealth.proposal.query.GHProposalFinder;
import com.pla.grouphealth.sharedresource.query.GHFinder;
import com.pla.grouphealth.sharedresource.service.GHInsuredFactory;
import com.pla.sharedkernel.identifier.ProposalId;
import com.pla.sharedkernel.identifier.QuotationId;
import org.axonframework.commandhandling.annotation.CommandHandler;
import org.axonframework.repository.Repository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by Samir on 6/26/2015.
 */
@Component
public class GHProposalCommandHandler {

    private GroupHealthProposalFactory groupHealthProposalFactory;

    private Repository<GroupHealthProposal> ghProposalMongoRepository;

    private GHProposalFinder ghProposalFinder;

    private GHFinder ghFinder;

    private GHInsuredFactory ghInsuredFactory;

    @Autowired
    public GHProposalCommandHandler(GroupHealthProposalFactory groupHealthProposalFactory, Repository<GroupHealthProposal> ghProposalMongoRepository, GHProposalFinder ghProposalFinder, GHFinder ghFinder,GHInsuredFactory ghInsuredFactory) {
        this.groupHealthProposalFactory = groupHealthProposalFactory;
        this.ghProposalMongoRepository = ghProposalMongoRepository;
        this.ghProposalFinder = ghProposalFinder;
        this.ghFinder = ghFinder;
        this.ghInsuredFactory=ghInsuredFactory;
    }

    @CommandHandler
    public String createProposalFromQuotation(GHQuotationToProposalCommand ghQuotationToProposalCommand) {
        Map quotationMap = ghFinder.searchQuotationById(new QuotationId(ghQuotationToProposalCommand.getQuotationId()));
        String quotationNumber = (String) quotationMap.get("quotationNumber");
        Map proposalMap = ghProposalFinder.findProposalByQuotationNumber(quotationNumber);
        GroupHealthProposal groupHealthProposal = null;
        ProposalId proposalId = new ProposalId(ObjectId.get().toString());
        if (proposalMap != null) {
            proposalId = new ProposalId(proposalMap.get("_id").toString());
            groupHealthProposal = ghProposalMongoRepository.load(proposalId);
        }
        groupHealthProposal = groupHealthProposalFactory.createProposal(new QuotationId(ghQuotationToProposalCommand.getQuotationId()), proposalId);
        if (proposalMap == null) {
            ghProposalMongoRepository.add(groupHealthProposal);
        }
        return proposalId.getProposalId();
    }

}
