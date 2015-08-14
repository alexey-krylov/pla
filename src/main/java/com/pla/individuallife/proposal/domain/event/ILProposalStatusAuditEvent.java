package com.pla.individuallife.proposal.domain.event;

import com.pla.individuallife.proposal.domain.model.ILProposalStatus;
import com.pla.sharedkernel.identifier.ProposalId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;

/**
 * Created by Admin on 7/29/2015.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ILProposalStatusAuditEvent {

    private ProposalId proposalId;

    private ILProposalStatus status;

    private String actor;

    private String comments;

    private DateTime performedOn;
}
