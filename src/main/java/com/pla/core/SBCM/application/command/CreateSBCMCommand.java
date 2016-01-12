package com.pla.core.SBCM.application.command;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

/**
 * Author - Mohan Sharma Created on 12/24/2015.
 */
@Getter
@NoArgsConstructor
@Setter
public class CreateSBCMCommand {
    private String serviceBenefitCoverageMappingId;
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

    public CreateSBCMCommand updateWithPlanCode(String planCode) {
        this.planCode = planCode;
        return this;
    }

    public CreateSBCMCommand updateWithPlanName(String planName) {
        this.planName = planName;
        return this;
    }

    public CreateSBCMCommand updateWithBenefitName(String benefitName) {
        this.benefitName = benefitName;
        return this;
    }

    public CreateSBCMCommand updateWithCoverageName(String coverageName) {
        this.coverageName = coverageName;
        return this;
    }

    public CreateSBCMCommand updateWithBenefitId(String benefitId) {
        this.benefitId = benefitId;
        return this;
    }

    public CreateSBCMCommand updateWithCoverageId(String coverageId) {
        this.coverageId = coverageId;
        return this;
    }

    public CreateSBCMCommand updateWithService(String service) {
        this.service = service;
        return this;
    }

    public CreateSBCMCommand updateWithStatus(String status) {
        this.status = status;
        return this;
    }
}
