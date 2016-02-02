package com.pla.grouphealth.claim.cashless.presentation.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.joda.time.LocalDate;

/**
 * Created by Rudra on 2/1/2016.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class GHPreAuthorizationMailDto {
    private String subject;

    private String mailContent;

    private String plan;

    private String recipientMailAddress;

    private LocalDate  preAuthorizationDate;

    private String preAuthorizationId;

    private String policyNumber;

    public GHPreAuthorizationMailDto(String subject, String mailContent, String recipientMailAddress) {
        this.subject = subject;
        this.mailContent = mailContent;
        this.recipientMailAddress = recipientMailAddress;
    }
}
