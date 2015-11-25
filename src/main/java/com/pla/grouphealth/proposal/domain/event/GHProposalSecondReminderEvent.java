package com.pla.grouphealth.proposal.domain.event;

import com.pla.sharedkernel.identifier.ProposalId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Created by Admin on 25-Nov-15.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class GHProposalSecondReminderEvent implements Serializable {
    private ProposalId proposalId;
}
