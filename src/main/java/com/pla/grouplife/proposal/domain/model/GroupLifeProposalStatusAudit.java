package com.pla.grouplife.proposal.domain.model;

import com.pla.sharedkernel.identifier.ProposalId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by Samir on 6/24/2015.
 */
@Document(collection = "group_life_proposal_status")
@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class GroupLifeProposalStatusAudit {

    @Id
    private ProposalId proposalId;

    private GLProposalStatus proposalStatus;

    private DateTime modifiedOn;

    private String modifiedBy;

    public GroupLifeProposalStatusAudit(ProposalId proposalId, GLProposalStatus proposalStatus, DateTime modifiedOn, String modifiedBy) {
        this.proposalId = proposalId;
        this.proposalStatus = proposalStatus;
        this.modifiedOn = modifiedOn;
        this.modifiedBy = modifiedBy;
    }
}
