package com.pla.grouphealth.proposal.domain.event;

import com.pla.grouphealth.proposal.domain.model.GroupHealthProposalStatusAudit;
import com.pla.grouphealth.proposal.repository.GHProposalStatusAuditRepository;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Samir on 7/6/2015.
 */
@Component
public class GHProposalEventHandler {

    @Autowired
    private GHProposalStatusAuditRepository ghProposalStatusAuditRepository;

    @EventHandler
    public void handle(GHProposalStatusAuditEvent ghProposalStatusAuditEvent) {
        GroupHealthProposalStatusAudit groupHealthProposalStatusAudit = new GroupHealthProposalStatusAudit(ObjectId.get(), ghProposalStatusAuditEvent.getProposalId(), ghProposalStatusAuditEvent.getStatus(), ghProposalStatusAuditEvent.getPerformedOn(), ghProposalStatusAuditEvent.getActor(), ghProposalStatusAuditEvent.getComments());
        ghProposalStatusAuditRepository.save(groupHealthProposalStatusAudit);
    }
}
