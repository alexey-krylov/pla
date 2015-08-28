package com.pla.individuallife.quotation.presentation.dto;

import com.pla.individuallife.sharedresource.dto.PlanDetailDto;
import com.pla.individuallife.sharedresource.dto.ProposedAssuredDto;
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
    private String proposerName;
    private String planName;
    private String proposerNrcNumber;
    private String agentName;
    private String agentCode;
    private ProposedAssuredDto proposedAssured;
    private com.pla.individuallife.sharedresource.dto.ProposerDto proposer;
    private PlanDetailDto planDetail;
    private String quotationStatus;
    private String quotationId;
}
