package com.pla.grouphealth.policy.presentation.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by Admin on 9/10/2015.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class GHPolicyMailDto {
    private String subject;

    private String mailContent;

    private String recipientMailAddress;

    private String policyId;

    private String policyNumber;

    public GHPolicyMailDto(String subject, String mailContent, String recipientMailAddress) {
        this.subject = subject;
        this.mailContent = mailContent;
        this.recipientMailAddress = recipientMailAddress;
    }

}
