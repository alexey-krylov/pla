package com.pla.grouplife.proposal.domain.event;

import com.pla.grouplife.proposal.domain.model.GroupLifeProposalStatusAudit;
import com.pla.grouplife.proposal.repository.GLProposalStatusAuditRepository;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Admin on 9/2/2015.
 */
@Component
public class GLProposalStatusAuditEvenListener {

    private transient static final Logger LOGGER = LoggerFactory.getLogger(GLProposalStatusAuditEvenListener.class);

    @Autowired
    private GLProposalStatusAuditRepository glProposalStatusAuditRepository;

    @EventHandler
    public void handle(GLProposalStatusAuditEvent glProposalStatusAuditEvent) {
        if (LOGGER.isDebugEnabled()){
            LOGGER.debug("Handling GL Proposal Status Audit Event .....", glProposalStatusAuditEvent);
        }
        GroupLifeProposalStatusAudit groupLifeProposalStatusAudit = new GroupLifeProposalStatusAudit(ObjectId.get(), glProposalStatusAuditEvent.getProposalId(), glProposalStatusAuditEvent.getStatus(), glProposalStatusAuditEvent.getPerformedOn(), glProposalStatusAuditEvent.getActor(), glProposalStatusAuditEvent.getComments());
        glProposalStatusAuditRepository.save(groupLifeProposalStatusAudit);
    }
}
