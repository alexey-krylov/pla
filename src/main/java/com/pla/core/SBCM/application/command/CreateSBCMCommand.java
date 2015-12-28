package com.pla.core.SBCM.application.command;

import com.pla.sharedkernel.identifier.PlanId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

/**
 * Created by Mohan Sharma on 12/24/2015.
 */
@Getter
@Setter
public class CreateSBCMCommand {
    private String ServiceBenefitCoverageMappingId;
    @NotNull(message = "planId must not be null")
    @NotEmpty(message = "{planId cannot be null}")
    private String planCode;
    @NotEmpty(message = "{coverageId cannot be null}")
    @NotNull(message = "coverageId must not be null")
    private String coverageId;
    @NotEmpty(message = "{benefitId cannot be null}")
    @NotNull(message = "benefitId must not be null")
    private String benefitId;
    @NotEmpty(message = "{service cannot be null}")
    @NotNull(message = "service must not be null")
    private String service;
    private String status;
}
