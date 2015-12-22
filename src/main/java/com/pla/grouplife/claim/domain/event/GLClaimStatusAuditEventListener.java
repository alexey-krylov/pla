package com.pla.grouplife.claim.domain.event;

import com.pla.grouplife.claim.domain.model.GroupLifeClaimStatusAudit;
import com.pla.grouplife.claim.repository.GLClaimStatusAuditRepository;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by ak
 */
@Component

public class GLClaimStatusAuditEventListener {
    private transient static final Logger LOGGER = LoggerFactory.getLogger(GLClaimStatusAuditEventListener.class);
    @Autowired
    private GLClaimStatusAuditRepository  glClaimStatusAuditRepository;

    @EventHandler
    public void handle(GLClaimStatusAuditEvent glClaimStatusAuditEvent) {
        if (LOGGER.isDebugEnabled()){
            LOGGER.debug("Handling GL Claim Status Audit Event .....", glClaimStatusAuditEvent);
        }
        GroupLifeClaimStatusAudit groupLifeClaimStatusAudit = new GroupLifeClaimStatusAudit(ObjectId.get(), glClaimStatusAuditEvent.getClaimId(), glClaimStatusAuditEvent.getStatus(), glClaimStatusAuditEvent.getPerformedOn(),glClaimStatusAuditEvent.getActor(), glClaimStatusAuditEvent.getComments());
        glClaimStatusAuditRepository.save(groupLifeClaimStatusAudit);
    }
}

