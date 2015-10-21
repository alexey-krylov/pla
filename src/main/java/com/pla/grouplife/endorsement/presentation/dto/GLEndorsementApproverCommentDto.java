package com.pla.grouplife.endorsement.presentation.dto;

import com.pla.sharedkernel.domain.model.EndorsementStatus;
import com.pla.sharedkernel.identifier.EndorsementId;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;

/**
 * Created by Admin on 10/20/2015.
 */
@Getter
@Setter
public class GLEndorsementApproverCommentDto {

    private ObjectId id;

    private EndorsementId endorsementId;

    private EndorsementStatus status;

    private DateTime modifiedOn;

    private String modifiedBy;

    private String comment;
}
