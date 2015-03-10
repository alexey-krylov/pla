/*
 * Copyright (c) 3/5/15 5:24 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.application;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
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
public class CreateBenefitCommand {

    @NotNull(message = "{User details cannot be null}")
    private UserDetails userDetails;

    @NotNull(message = "{Benefit name details cannot be null}")
    @NotEmpty(message = "{Benefit name cannot be empty}")
    private String benefitName;
}
