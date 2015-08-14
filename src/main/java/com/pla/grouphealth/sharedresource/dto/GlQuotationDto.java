package com.pla.grouphealth.sharedresource.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.pla.sharedkernel.identifier.QuotationId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.joda.time.LocalDate;
import org.nthdimenzion.presentation.LocalJodaDateDeserializer;
import org.nthdimenzion.presentation.LocalJodaDateSerializer;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * Created by Samir on 4/14/2015.
 */
@Getter
@Setter
@AllArgsConstructor
public class GlQuotationDto {

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

    @JsonSerialize(using = LocalJodaDateSerializer.class)
    @JsonDeserialize(using = LocalJodaDateDeserializer.class)
    @DateTimeFormat(pattern="dd/MM/yyyy")
    private LocalDate sharedOn;

   /* public String getQuotationNumber() {
        if (versionNumber != null && versionNumber > 0) {
            return quotationNumber + "/" + versionNumber;
        } else {
            return quotationNumber;
        }
    }*/
}
