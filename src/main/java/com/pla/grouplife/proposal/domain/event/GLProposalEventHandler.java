package com.pla.grouplife.proposal.domain.event;

import com.pla.grouplife.proposal.domain.model.GroupLifeProposalStatusAudit;
import com.pla.grouplife.proposal.repository.GLProposalStatusAuditRepository;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Samir on 7/14/2015.
 */
@Component
public class GLProposalEventHandler {

    @Autowired
    private GLProposalStatusAuditRepository glProposalStatusAuditRepository;

    @EventHandler
    public void handle(GLProposalStatusAuditEvent glProposalStatusAuditEvent) {
        GroupLifeProposalStatusAudit groupLifeProposalStatusAudit = new GroupLifeProposalStatusAudit(ObjectId.get(), glProposalStatusAuditEvent.getProposalId(), glProposalStatusAuditEvent.getStatus(), glProposalStatusAuditEvent.getPerformedOn(), glProposalStatusAuditEvent.getActor(), glProposalStatusAuditEvent.getComments());
        glProposalStatusAuditRepository.save(groupLifeProposalStatusAudit);
    }
}
