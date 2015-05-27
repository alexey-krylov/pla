package com.pla.grouplife.quotation.presentation.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Samir on 5/27/2015.
 */
@Getter
@Setter
@NoArgsConstructor
public class GLQuotationMailDto {

    private String subject;

    private String mailContent;

    private String[] recipientMailAddress;

    private String quotationId;

    private String quotationNumber;

    public GLQuotationMailDto(String subject, String mailContent, String[] recipientMailAddress) {
        this.subject = subject;
        this.mailContent = mailContent;
        this.recipientMailAddress = recipientMailAddress;
    }

}
