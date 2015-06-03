/*
 * Copyright (c) 3/5/15 5:24 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.application;

import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.security.core.userdetails.UserDetails;

import javax.validation.constraints.NotNull;

/**
 * @author: Samir
 * @since 1.0 05/03/2015
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
@AllArgsConstructor
public class CreateBenefitCommand {

    private UserDetails userDetails;

    @NotNull(message = "{Benefit name cannot be null}")
    @NotEmpty(message = "{Benefit name cannot be empty}")
    @Length(max = 100, min = 1,message = "{Benefit name length should be between 1-100}")
    private String benefitName;

    private String benefitCode;
}
