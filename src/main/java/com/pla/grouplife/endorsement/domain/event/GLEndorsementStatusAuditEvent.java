package com.pla.grouplife.endorsement.domain.event;

import com.pla.sharedkernel.domain.model.EndorsementStatus;
import com.pla.sharedkernel.identifier.EndorsementId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;

/**
 * Created by Admin on 10/20/2015.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GLEndorsementStatusAuditEvent {

    private EndorsementId endorsementId;

    private EndorsementStatus status;

    private String actor;

    private String comments;

    private DateTime performedOn;

}
