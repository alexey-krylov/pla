/*
 * Copyright (c) 3/10/15 2:56 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.application;

import com.pla.sharedkernel.domain.model.BenefitStatus;
import lombok.*;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.security.core.userdetails.UserDetails;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;

/**
 * @author: Samir
 * @since 1.0 10/03/2015
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
@AllArgsConstructor
public class InactivateBenefitCommand {

    @NotNull(message = "{benefit id cannot be null}")
    @NotEmpty(message = "{benefit id cannot be empty}")
    private String benefitId;

    private UserDetails userDetails;
}
