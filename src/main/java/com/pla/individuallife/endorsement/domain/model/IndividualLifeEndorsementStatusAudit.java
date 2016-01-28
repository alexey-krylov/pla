package com.pla.individuallife.endorsement.domain.model;

import com.pla.sharedkernel.domain.model.EndorsementStatus;
import com.pla.sharedkernel.identifier.EndorsementId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by Admin on 10/20/2015.
 */
@Document(collection = "individual_life_endorsement_status")
@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class IndividualLifeEndorsementStatusAudit {
    @Id
    private ObjectId id;

    private EndorsementId endorsementId;

    private EndorsementStatus status;

    private DateTime modifiedOn;

    private String modifiedBy;

    private String comment;

    public IndividualLifeEndorsementStatusAudit(ObjectId id, EndorsementId endorsementId, EndorsementStatus status, DateTime modifiedOn, String modifiedBy, String comment) {
        this.id = id;
        this.endorsementId = endorsementId;
        this.status = status;
        this.modifiedOn = modifiedOn;
        this.modifiedBy = modifiedBy;
        this.comment = comment;
    }
}
