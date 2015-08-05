package com.pla.individuallife.policy.domain.event;

import com.pla.individuallife.policy.application.ILProposalToPolicyCommand;
import com.pla.individuallife.sharedresource.event.ILProposalToPolicyEvent;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Admin on 8/3/2015.
 */
@Component
public class ILPolicyEventHandler {

    @Autowired
    private CommandGateway commandGateway;

    @EventHandler
    public void handle(ILProposalToPolicyEvent event) {
        commandGateway.send(new ILProposalToPolicyCommand(event.getProposalId()));
    }
}
