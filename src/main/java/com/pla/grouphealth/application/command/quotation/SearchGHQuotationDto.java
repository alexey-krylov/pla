package com.pla.grouphealth.application.command.quotation;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Karunakar on 4/30/2015.
 */
@NoArgsConstructor
@Getter
@Setter
public class SearchGHQuotationDto {

    private String quotationNumber;

    private String proposerName;

    private String agentName;

    private String agentCode;
}
