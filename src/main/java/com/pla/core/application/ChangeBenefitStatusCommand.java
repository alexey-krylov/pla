/*
 * Copyright (c) 3/10/15 12:37 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.application;

import com.pla.sharedkernel.domain.model.BenefitStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

/**
 * @author: Samir
 * @since 1.0 10/03/2015
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ChangeBenefitStatusCommand {

    @NotNull(message = "{benefit id cannot be null}")
    @NotEmpty(message = "{benefit id cannot be empty}")
    private String benefitId;

    @NotNull(message = "{status cannot be null}")
    private BenefitStatus status;
}
