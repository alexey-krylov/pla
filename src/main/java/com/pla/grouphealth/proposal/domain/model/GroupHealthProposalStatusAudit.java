package com.pla.grouphealth.proposal.domain.model;

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
@Document(collection = "group_health_proposal_status")
@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class GroupHealthProposalStatusAudit {

    @Id
    private ProposalId proposalId;

    private GHProposalStatus proposalStatus;

    private DateTime modifiedOn;

    private String modifiedBy;

    public GroupHealthProposalStatusAudit(ProposalId proposalId, GHProposalStatus proposalStatus, DateTime modifiedOn, String modifiedBy) {
        this.proposalId = proposalId;
        this.proposalStatus = proposalStatus;
        this.modifiedOn = modifiedOn;
        this.modifiedBy = modifiedBy;
    }
}
