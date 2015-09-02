package com.pla.individuallife.proposal.domain.event;

import com.pla.individuallife.proposal.domain.model.ILProposalStatusAudit;
import com.pla.individuallife.proposal.repository.ILProposalStatusAuditRepository;
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
public class ILProposalStatusAuditEventListener {

    private transient static final Logger LOGGER = LoggerFactory.getLogger(ILProposalStatusAuditEventListener.class);

    @Autowired
    private ILProposalStatusAuditRepository ilProposalStatusAuditRepository;

    @EventHandler
    public void handle(ILProposalStatusAuditEvent ilProposalStatusAuditEvent) {
        if (LOGGER.isDebugEnabled()){
            LOGGER.debug("Handling IL Proposal Status Audit Event .....", ilProposalStatusAuditEvent);
        }
        ILProposalStatusAudit groupHealthProposalStatusAudit = new ILProposalStatusAudit(ObjectId.get(), ilProposalStatusAuditEvent.getProposalId(), ilProposalStatusAuditEvent.getStatus(), ilProposalStatusAuditEvent.getPerformedOn(), ilProposalStatusAuditEvent.getActor(), ilProposalStatusAuditEvent.getComments());
        ilProposalStatusAuditRepository.save(groupHealthProposalStatusAudit);
    }
}
