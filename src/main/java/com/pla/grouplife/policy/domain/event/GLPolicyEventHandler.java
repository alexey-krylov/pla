package com.pla.grouplife.policy.domain.event;

import com.pla.grouplife.sharedresource.event.GLPolicyInsuredDeleteEvent;
import com.pla.grouplife.policy.application.command.GLProposalToPolicyCommand;
import com.pla.grouplife.policy.application.command.GroupLifePolicyMemberDeletionCommand;
import com.pla.grouplife.sharedresource.event.GLProposalToPolicyEvent;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Samir on 7/9/2015.
 */
@Component
public class GLPolicyEventHandler {

    @Autowired
    private CommandGateway commandGateway;

    @EventHandler
    public void handle(GLProposalToPolicyEvent event) {
        commandGateway.send(new GLProposalToPolicyCommand(event.getProposalId()));
    }

    @EventHandler
    public void handle(GLPolicyInsuredDeleteEvent event) {
        commandGateway.send(new GroupLifePolicyMemberDeletionCommand(event.getPolicyId(),event.getDeletedFamilyIds()));
    }

}
