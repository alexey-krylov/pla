package com.pla.grouplife.proposal.presentation.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Samir on 5/27/2015.
 */
@Getter
@Setter
@NoArgsConstructor
public class GLProposalMailDto {

    private String subject;

    private String mailContent;

    private String[] recipientMailAddress;

    private String proposalId;

    private String proposalNumber;

    public GLProposalMailDto(String subject, String mailContent, String[] recipientMailAddress) {
        this.subject = subject;
        this.mailContent = mailContent;
        this.recipientMailAddress = recipientMailAddress;
    }

}
