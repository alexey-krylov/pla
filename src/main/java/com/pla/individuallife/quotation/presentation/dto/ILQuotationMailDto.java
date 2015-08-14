package com.pla.individuallife.quotation.presentation.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Karunakar on 6/11/2015.
 */
@Getter
@Setter
@NoArgsConstructor
public class ILQuotationMailDto {

    private String subject;

    private String mailContent;

    private String[] recipientMailAddress;

    private String quotationId;

    private String quotationNumber;

    public ILQuotationMailDto(String subject, String mailContent, String[] recipientMailAddress) {
        this.subject = subject;
        this.mailContent = mailContent;
        this.recipientMailAddress = recipientMailAddress;
    }

}
