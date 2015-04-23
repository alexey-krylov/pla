package com.pla.quotation.query;

import com.pla.sharedkernel.identifier.QuotationId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.joda.time.LocalDate;

/**
 * Created by Samir on 4/14/2015.
 */
@Getter
@Setter
@AllArgsConstructor
public class GlQuotationDto {

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
