package com.pla.grouphealth.proposal.domain.event;

import com.pla.grouphealth.proposal.domain.model.GroupHealthProposalStatusAudit;
import com.pla.grouphealth.proposal.repository.GHProposalStatusAuditRepository;
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
public class GHProposalStatusAuditEventListener {

    private transient static final Logger LOGGER = LoggerFactory.getLogger(GHProposalStatusAuditEventListener.class);

    @Autowired
    private GHProposalStatusAuditRepository ghProposalStatusAuditRepository;

    @EventHandler
    public void handle(GHProposalStatusAuditEvent ghProposalStatusAuditEvent) {
        if (LOGGER.isDebugEnabled()){
            LOGGER.debug("Handling GH Proposal Status Audit Event .....", ghProposalStatusAuditEvent);
        }
        GroupHealthProposalStatusAudit groupHealthProposalStatusAudit = new GroupHealthProposalStatusAudit(ObjectId.get(), ghProposalStatusAuditEvent.getProposalId(), ghProposalStatusAuditEvent.getStatus(), ghProposalStatusAuditEvent.getPerformedOn(), ghProposalStatusAuditEvent.getActor(), ghProposalStatusAuditEvent.getComments());
        ghProposalStatusAuditRepository.save(groupHealthProposalStatusAudit);
    }

}
