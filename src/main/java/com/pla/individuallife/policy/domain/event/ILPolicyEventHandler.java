package com.pla.individuallife.policy.domain.event;

import com.pla.individuallife.policy.application.ILProposalToPolicyCommand;
import com.pla.individuallife.sharedresource.event.ILProposalToPolicyEvent;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Admin on 8/3/2015.
 */
@Component
public class ILPolicyEventHandler  {

    private transient static final Logger LOGGER = LoggerFactory.getLogger(ILPolicyEventHandler.class);

    @Autowired
    private transient CommandGateway commandGateway;


    @EventHandler
    public void handle(ILProposalToPolicyEvent event) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Handling IL Policy Create Event .....", event);
        }
        commandGateway.send(new ILProposalToPolicyCommand(event.getProposalId()));
    }
}
