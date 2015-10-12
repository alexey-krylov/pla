package com.pla.individuallife.policy.presentation.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Admin on 9/10/2015.
 */
@Getter
@Setter
@NoArgsConstructor
public class ILPolicyMailDto {

    private String subject;

    private String mailContent;

    private String recipientMailAddress;

    private String policyId;

    private String policyNumber;

    public ILPolicyMailDto(String subject, String mailContent, String recipientMailAddress) {
        this.subject = subject;
        this.mailContent = mailContent;
        this.recipientMailAddress = recipientMailAddress;
    }

}
