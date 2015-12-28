package com.pla.core.SBCM.application.command;

import com.pla.sharedkernel.identifier.PlanId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * Created by Mohan Sharma on 12/24/2015.
 */
@Getter
@NoArgsConstructor
@Setter
public class CreateSBCMCommand {
    private String ServiceBenefitCoverageMappingId;
    @NotNull(message = "planId must not be null")
    private String planCode;
    @NotNull(message = "coverage must not be null")
    private String coverageId;
    @NotNull(message = "benefit must not be null")
    private String benefitId;
    @NotNull(message = "service must not be null")
    private String service;
    private String status;
}
