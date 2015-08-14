package com.pla.grouphealth.policy.domain.event;

import com.pla.grouphealth.policy.application.command.GHProposalToPolicyCommand;
import com.pla.grouphealth.sharedresource.event.GHProposalToPolicyEvent;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Samir on 7/9/2015.
 */
@Component
public class GHPolicyEventHandler {

    @Autowired
    private CommandGateway commandGateway;

    @EventHandler
    public void handle(GHProposalToPolicyEvent event) {
        commandGateway.send(new GHProposalToPolicyCommand(event.getProposalId()));
    }
}
