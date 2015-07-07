package com.pla.grouphealth.proposal.domain.event;

import com.pla.grouphealth.sharedresource.model.vo.ProposalStatus;
import com.pla.sharedkernel.identifier.ProposalId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;

/**
 * Created by Samir on 7/6/2015.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GHProposalStatusAuditEvent {

    private ProposalId proposalId;

    private ProposalStatus status;

    private String actor;

    private String comments;

    private DateTime performedOn;

}
