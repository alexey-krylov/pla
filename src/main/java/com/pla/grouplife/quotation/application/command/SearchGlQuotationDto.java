package com.pla.grouplife.quotation.application.command;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Samir on 4/17/2015.
 */
@NoArgsConstructor
@Getter
@Setter
public class SearchGlQuotationDto {

    private String quotationNumber;

    private String proposerName;

    private String agentName;

    private String agentCode;
}
