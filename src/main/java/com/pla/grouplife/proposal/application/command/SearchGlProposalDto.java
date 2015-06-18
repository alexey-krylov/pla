package com.pla.grouplife.proposal.application.command;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Samir on 4/17/2015.
 */
@NoArgsConstructor
@Getter
@Setter
public class SearchGlProposalDto {

    private String quotationNumber;

    private String proposerName;

    private String agentName;

    private String agentCode;

    private String quotationId;
}
