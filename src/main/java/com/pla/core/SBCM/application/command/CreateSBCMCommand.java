package com.pla.core.SBCM.application.command;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

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
    @NotEmpty(message = "{planId cannot be null}")
    private String planCode;
    private String planName;
    @NotEmpty(message = "{coverageId cannot be null}")
    @NotNull(message = "coverageId must not be null")
    private String coverageId;
    private String coverageName;
    @NotEmpty(message = "{benefitId cannot be null}")
    @NotNull(message = "benefitId must not be null")
    private String benefitId;
    private String benefitName;
    @NotEmpty(message = "{service cannot be null}")
    @NotNull(message = "service must not be null")
    private String service;
    private String status;

    public CreateSBCMCommand(String planName, String benefitName, String coverageName, String service,String planCode) {
        this.planName = planName;
        this.benefitName = benefitName;
        this.coverageName = coverageName;
        this.service = service;
        this.planCode = planCode;
    }
}
