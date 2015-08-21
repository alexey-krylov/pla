package com.pla.individuallife.proposal.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Prasant on 17-Jun-15.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ILSearchProposalDto {

    private String proposalNumber;

    private String proposerName;

    private String proposerNrcNumber;

    private String agentName;

    private String agentCode;

    private String proposalId;

    private String createdOn;

    private String ProposalStatus;
}
