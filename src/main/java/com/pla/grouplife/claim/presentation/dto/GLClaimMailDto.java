package com.pla.grouplife.claim.presentation.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by ak 11/12/2015.
 */

@Getter
@Setter
@NoArgsConstructor

public class GLClaimMailDto {

    private String subject;

    private String mailContent;

    private String[] recipientMailAddress;

    private String claimId;

    private String claimNumber;

    public GLClaimMailDto(String subject, String mailContent, String[] recipientMailAddress) {

        this.subject = subject;
        this.mailContent = mailContent;
        this.recipientMailAddress = recipientMailAddress;
    }
}

