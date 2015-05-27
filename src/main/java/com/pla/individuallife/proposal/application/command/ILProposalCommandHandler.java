package com.pla.individuallife.proposal.application.command;

import com.pla.core.domain.exception.PlanValidationException;
import org.axonframework.commandhandling.annotation.CommandHandler;
import org.springframework.stereotype.Component;

/**
 * Created by Prasant on 26-May-15.
 */
@Component
public class ILProposalCommandHandler {

    @CommandHandler
    public void handle(CreateProposalCommand proposalCommand) throws PlanValidationException {

        // invoice Proposal Service.createProposal
//           proposalCommand.getProposedAssured();
//           PlanBuilder planBuilder = planBuilder(proposalCommand);
//        Plan plan = planBuilder.build(command.getPlanId());
//            planMongoRepository.add(plan);
    }
}
