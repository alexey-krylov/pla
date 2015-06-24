package com.pla.individuallife.quotation.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Karunakar on 24-06-2015.
 */
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class ILSearchDto {

    private String quotationNumber;
    private String quotationStatus;
    private String quotationId;
    private String proposerName;
    private String proposerNrcNumber;
    private String agentName;
    private String agentCode;
    private String createdOn;
    private String version;
}
