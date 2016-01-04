package com.pla.grouplife.endorsement.domain.event;

import com.pla.grouplife.endorsement.application.command.GLCreateFLCEndorsementCommand;
import com.pla.grouplife.sharedresource.event.CreateFreeCoverLimitEndorsementEvent;
import com.pla.grouplife.sharedresource.model.GLEndorsementType;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Admin on 28-Dec-15.
 */
@Component
public class GroupLifeEndorsementEventListener {

    private transient static final Logger LOGGER = LoggerFactory.getLogger(GroupLifeEndorsementEventListener.class);

    @Autowired
    private CommandGateway commandGateway;


    @EventHandler
    public void handle(CreateFreeCoverLimitEndorsementEvent createFreeCoverLimitEndorsementEvent) {
        if (LOGGER.isDebugEnabled()){
            LOGGER.debug("Handling GL Endorsement createFreeCoverLimitEndorsementEvent .....", createFreeCoverLimitEndorsementEvent);
        }
        /*
        * Creating the Free Cover Limit For Insureds(Main Assured)
        * */
        createFreeCoverLimitEndorsementEvent.getInsureds().forEach(insured->{
            commandGateway.sendAndWait(new GLCreateFLCEndorsementCommand(GLEndorsementType.FREE_COVER_LIMIT,createFreeCoverLimitEndorsementEvent.getPolicyId().getPolicyId(),insured,null,createFreeCoverLimitEndorsementEvent.getFreeCoverLimit(),Boolean.TRUE));
        });

        /*
        * Creating the Free Cover Limit For Insureds dependents
        * */
        createFreeCoverLimitEndorsementEvent.getInsuredDependents().forEach(insuredDependent->{
            commandGateway.sendAndWait(new GLCreateFLCEndorsementCommand(GLEndorsementType.FREE_COVER_LIMIT,createFreeCoverLimitEndorsementEvent.getPolicyId().getPolicyId(),null,insuredDependent,createFreeCoverLimitEndorsementEvent.getFreeCoverLimit(),Boolean.FALSE));
        });
    }
}
