package com.pla.core.SBCM.application.command;

import com.pla.sharedkernel.identifier.PlanId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Mohan Sharma on 12/24/2015.
 */
@Getter
@NoArgsConstructor
@Setter
public class CreateSBCMCommand {
    private String planId;
    private String coverage;
    private String benefit;
    private String service;
}
