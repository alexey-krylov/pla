package com.pla.grouplife.proposal.application.command;

import com.pla.grouplife.proposal.domain.model.GroupLifeProposal;
import com.pla.grouplife.proposal.domain.service.GroupLifeProposalService;
import com.pla.grouplife.sharedresource.util.GLInsuredFactory;
import org.axonframework.commandhandling.annotation.CommandHandler;
import org.axonframework.repository.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by User on 6/25/2015.
 */
@Component
public class GroupLifeProposalCommandHandler {

    private GroupLifeProposalService groupLifeProposalService;

    private Repository<GroupLifeProposal> groupLifeProposalRepository;

    private GLInsuredFactory glInsuredFactory;


    @Autowired
    GroupLifeProposalCommandHandler(GroupLifeProposalService groupLifeProposalService,
                                    Repository<GroupLifeProposal> groupLifeProposalRepository, GLInsuredFactory glInsuredFactory) {
        this.groupLifeProposalService = groupLifeProposalService;
        this.groupLifeProposalRepository = groupLifeProposalRepository;
        this.glInsuredFactory = glInsuredFactory;
    }

    @CommandHandler
    public String createProposal(GLQuotationToProposalCommand glQuotationToProposalCommand) {
        GroupLifeProposal groupLifeProposal = groupLifeProposalService.createProposal(glQuotationToProposalCommand.getQuotationId(), glQuotationToProposalCommand.getUserDetails());
        groupLifeProposalRepository.add(groupLifeProposal);
        return groupLifeProposal.getIdentifier().getProposalId();
    }
}
