package com.pla.core.SBCM.application.command;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

/**
 * Created by Rudra on 12/29/2015.
 */
@Setter
@Getter
@NoArgsConstructor
 public class UpdateSBCMCommand {
    @NotEmpty(message = "serviceBenefitCoverageMappingId should not be empty")
    @NotNull(message = "serviceBenefitCoverageMappingId should not be not null")
     private String serviceBenefitCoverageMappingId;
    @NotEmpty(message = "Status should not be empty")
    @NotNull(message = "Status should not be not null")
    private String status;
    private String planName;
    private String benefitName;
    private String coverageName;
    private String planCode;
    private String serviceName;

    public UpdateSBCMCommand(String serviceBenefitCoverageMappingId, String planName, String planCode, String benefitName, String coverageName, String serviceName, String status) {
        this.serviceBenefitCoverageMappingId = serviceBenefitCoverageMappingId;
        this.planName = planName;
        this.planCode = planCode;
        this.benefitName = benefitName;
        this.coverageName = coverageName;
        this.serviceName = serviceName;
        this.status = status;
    }
}
