package com.pla.grouphealth.proposal.presentation.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.DateTime;

/**
 * Created by Samir on 6/24/2015.
 */
@Getter
@Setter
@NoArgsConstructor
public class GHProposalDto {

    private String proposalId;

    private DateTime submittedOn;

    private String agentId;

    private String agentName;

    private String proposalStatus;

    private String proposalNumber;

    private String proposerName;

    public GHProposalDto(String proposalId, DateTime submittedOn, String agentId, String agentName, String proposalStatus, String proposalNumber, String proposerName) {
        this.proposalId = proposalId;
        this.submittedOn = submittedOn;
        this.agentId = agentId;
        this.agentName = agentName;
        this.proposalStatus = proposalStatus;
        this.proposalNumber = proposalNumber;
        this.proposerName = proposerName;
    }
}
