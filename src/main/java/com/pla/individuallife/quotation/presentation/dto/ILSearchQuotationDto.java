package com.pla.individuallife.quotation.presentation.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by pradyumna on 28-05-2015.
 */
@NoArgsConstructor
@Getter
@Setter
public class ILSearchQuotationDto {

    private String quotationNumber;
    private String proposerFirstName;
    private String proposerSurname;
    private String agentName;
    private String agentCode;
    private ProposedAssuredDto proposedAssured;
    private ProposerDto proposer;
    private PlanDetailDto planDetail;
}
