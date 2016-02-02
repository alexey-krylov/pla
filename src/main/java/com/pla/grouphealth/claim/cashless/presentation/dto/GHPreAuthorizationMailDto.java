package com.pla.grouphealth.claim.cashless.presentation.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.LocalDate;

import javax.validation.constraints.NotNull;

/**
 * Created by Rudra on 2/1/2016.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class GHPreAuthorizationMailDto {
    @NotNull(message = "Subject cannot be empty")
    @NotEmpty(message = "Subject cannot be empty")
    private String subject;
    @NotNull(message = "mailContent cannot be empty")
    @NotEmpty(message = "mailContent cannot be empty")
    private String mailContent;
    private String plan;
    @NotNull(message = "recipientMailAddress cannot be empty")
    @NotEmpty(message = "recipientMailAddress cannot be empty")
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
