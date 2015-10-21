package com.pla.grouplife.endorsement.domain.event;

import com.pla.grouplife.endorsement.domain.model.GroupLifeEndorsementStatusAudit;
import com.pla.grouplife.endorsement.repository.GLEndorsementStatusAuditRepository;
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
public class GLEndorsementStatusAuditEvenListener {

    private transient static final Logger LOGGER = LoggerFactory.getLogger(GLEndorsementStatusAuditEvenListener.class);

    @Autowired
    private GLEndorsementStatusAuditRepository glEndorsementStatusAuditRepository;

    @EventHandler
    public void handle(GLEndorsementStatusAuditEvent glEndorsementStatusAuditEvent) {
        if (LOGGER.isDebugEnabled()){
            LOGGER.debug("Handling GL Endorsement Status Audit Event .....", glEndorsementStatusAuditEvent);
        }
        GroupLifeEndorsementStatusAudit groupLifeProposalStatusAudit = new GroupLifeEndorsementStatusAudit(ObjectId.get(), glEndorsementStatusAuditEvent.getEndorsementId(), glEndorsementStatusAuditEvent.getStatus(), glEndorsementStatusAuditEvent.getPerformedOn(), glEndorsementStatusAuditEvent.getActor(), glEndorsementStatusAuditEvent.getComments());
        glEndorsementStatusAuditRepository.save(groupLifeProposalStatusAudit);
    }
}