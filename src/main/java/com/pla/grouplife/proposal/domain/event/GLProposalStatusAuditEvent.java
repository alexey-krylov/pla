package com.pla.grouplife.proposal.domain.event;

import com.pla.grouplife.sharedresource.model.vo.GLProposalStatus;
import com.pla.sharedkernel.identifier.ProposalId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;

/**
 * Created by Samir on 7/14/2015.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GLProposalStatusAuditEvent {

    private ProposalId proposalId;

    private GLProposalStatus status;

    private String actor;

    private String comments;

    private DateTime performedOn;
}
