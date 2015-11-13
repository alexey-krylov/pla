package com.pla.individuallife.sharedresource.dto;

import com.pla.sharedkernel.identifier.QuotationId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.LocalDate;

import java.util.Map;

/**
 * Created by Karunakar on 4/30/2015.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ILQuotationDto {

    private QuotationId quotationId;

    private Integer versionNumber;

    private LocalDate quotationGeneratedOn;

    private String agentId;

    private String planId;

    private String parentQuotationId;

    private String quotationStatus;

    private String quotationNumber;

    private String opportunityId;

    private ProposerDto proposer;
    private ProposedAssuredDto proposedAssured;
    private Map<String, Object> agentDetail;
    private Map<String, Object> planDetail;
    private PlanDetailDto planDetailDto;
    private boolean assuredTheProposer;

    public String getQuotationNumber() {
        if (versionNumber > 0) {
            return quotationNumber + "/" + versionNumber;
        }
        return quotationNumber;
    }

}
