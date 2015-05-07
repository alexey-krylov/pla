package com.pla.grouphealth.query;

import com.pla.sharedkernel.identifier.QuotationId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.joda.time.LocalDate;

/**
 * Created by Karunakar on 4/30/2015.
 */
@Getter
@Setter
@AllArgsConstructor
public class GHQuotationDto {

    private QuotationId quotationId;

    private Integer versionNumber;

    private LocalDate quotationGeneratedOn;

    private String agentCode;

    private String agentName;

    private QuotationId parentQuotationId;

    private String quotationStatus;

    private String quotationNumber;

    private String proposeName;

    private Integer ageing;
}
