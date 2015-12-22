package com.pla.grouplife.claim.domain.model;

import com.pla.sharedkernel.domain.model.ClaimId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by ak
 */
@Document(collection = "group_life_claim_status")
@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)

public class GroupLifeClaimStatusAudit {
    @Id
    private ObjectId id;

    private ClaimId claimId;

    private ClaimStatus claimStatus;

    private DateTime modifiedOn;

    private String modifiedBy;

    private String comment;


    public GroupLifeClaimStatusAudit(ObjectId id, ClaimId claimId, ClaimStatus claimStatus, DateTime modifiedOn, String modifiedBy, String comment) {
        this.id = id;
        this.claimId = claimId;
        this.claimStatus = claimStatus;
        this.modifiedOn = modifiedOn;
        this.modifiedBy = modifiedBy;
        this.comment = comment;
    }


}
