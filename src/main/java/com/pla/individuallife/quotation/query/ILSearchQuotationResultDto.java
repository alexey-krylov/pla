package com.pla.individuallife.quotation.query;

import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

/**
 * Created by pradyumna on 23-06-2015.
 */
@Getter
@Setter
public class ILSearchQuotationResultDto {

    private String agentName;
    private String proposerName;
    private String proposedName;
    private String quotationId;
    private String quotationNumber;
    private String versionNumber;
    private Date generatedOn;
    private String quotationStatus;

}
