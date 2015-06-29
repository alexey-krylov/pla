package com.pla.grouplife.proposal.presentation.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.pla.sharedkernel.identifier.QuotationId;
import lombok.AllArgsConstructor;
import org.joda.time.LocalDate;
import org.nthdimenzion.presentation.LocalJodaDateDeserializer;
import org.nthdimenzion.presentation.LocalJodaDateSerializer;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * Created by Samir on 6/24/2015.
 */
@AllArgsConstructor
public class GLProposalDto {
    private QuotationId quotationId;

    private Integer versionNumber;

    @JsonSerialize(using = LocalJodaDateSerializer.class)
    @JsonDeserialize(using = LocalJodaDateDeserializer.class)
    @DateTimeFormat(pattern="dd/MM/yyyy")
    private LocalDate quotationGeneratedOn;

    private String agentCode;

    private String agentName;

    private QuotationId parentQuotationId;

    private String quotationStatus;

    private String quotationNumber;

    private String proposeName;

    private Integer ageing;
}
