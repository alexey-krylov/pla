package com.pla.individuallife.proposal.application.command;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Karunakar on 6/25/2015.
 */
@NoArgsConstructor
@Getter
@Setter
public class ILSearchProposalDto {

    private String proposalNumber;

    private String proposerName;

    private String proposerNrcNumber;

    private String agentName;

    private String agentCode;

    private String proposalId;

    private String createdOn;

    private String version;

    private String ProposalStatus;
}
