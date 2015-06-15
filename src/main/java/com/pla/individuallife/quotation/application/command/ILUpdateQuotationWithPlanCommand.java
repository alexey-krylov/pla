package com.pla.individuallife.quotation.application.command;

import com.pla.individuallife.quotation.presentation.dto.PlanDetailDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Created by Karunakar on 5/26/2015.
 */
@Getter
@Setter
@NoArgsConstructor
public class ILUpdateQuotationWithPlanCommand {

    private String quotationId;

    private UserDetails userDetails;

    private PlanDetailDto planDetailDto;
}
