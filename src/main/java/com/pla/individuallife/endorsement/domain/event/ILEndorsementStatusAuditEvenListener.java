package com.pla.individuallife.endorsement.domain.event;

import com.pla.individuallife.endorsement.domain.model.IndividualLifeEndorsementStatusAudit;
import com.pla.individuallife.endorsement.repository.ILEndorsementStatusAuditRepository;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Admin on 10/20/2015.
 */
@Component
public class ILEndorsementStatusAuditEvenListener {

    private transient static final Logger LOGGER = LoggerFactory.getLogger(ILEndorsementStatusAuditEvenListener.class);

    @Autowired
    private ILEndorsementStatusAuditRepository ilEndorsementStatusAuditRepository;

    @EventHandler
    public void handle(ILEndorsementStatusAuditEvent ilEndorsementStatusAuditEvent) {
        if (LOGGER.isDebugEnabled()){
            LOGGER.debug("Handling GL Endorsement Status Audit Event .....", ilEndorsementStatusAuditEvent);
        }
        IndividualLifeEndorsementStatusAudit individualLifeProposalStatusAudit = new IndividualLifeEndorsementStatusAudit(ObjectId.get(), ilEndorsementStatusAuditEvent.getEndorsementId(), ilEndorsementStatusAuditEvent.getStatus(), ilEndorsementStatusAuditEvent.getPerformedOn(), ilEndorsementStatusAuditEvent.getActor(), ilEndorsementStatusAuditEvent.getComments());
        ilEndorsementStatusAuditRepository.save(individualLifeProposalStatusAudit);
    }
}