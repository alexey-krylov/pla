package com.pla.individuallife.query;

import com.pla.core.domain.model.agent.AgentId;
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
public class ILQuotationDto {

    private QuotationId quotationId;

    private Integer versionNumber;

    private LocalDate quotationGeneratedOn;

    private AgentId agentId;

    private QuotationId parentQuotationId;

    private String quotationStatus;

    private String quotationNumber;

    private String proposeName;

}
