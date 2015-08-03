package com.pla.individuallife.proposal.domain.model;

import com.pla.sharedkernel.identifier.ProposalId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by Admin on 8/3/2015.
 */
@Document(collection = "individual_life_proposal_status")
@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class ILProposalStatusAudit {

    @Id
    private ObjectId id;

    private ProposalId proposalId;

    private ILProposalStatus proposalStatus;

    private DateTime modifiedOn;

    private String modifiedBy;

    private String comment;

    public ILProposalStatusAudit(ObjectId id, ProposalId proposalId, ILProposalStatus proposalStatus, DateTime modifiedOn, String modifiedBy, String comment) {
        this.id=id;
        this.proposalId = proposalId;
        this.proposalStatus = proposalStatus;
        this.modifiedOn = modifiedOn;
        this.modifiedBy = modifiedBy;
        this.comment = comment;
    }

}
