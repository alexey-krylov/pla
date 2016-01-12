package com.pla.grouphealth.proposal.application.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Author - Mohan Sharma Created on 11/26/2015.
 */
@Getter
@AllArgsConstructor
public class GHProposalDocumentRemoveCommand {
    private String proposalId;
    private String gridFsDocId;
}
