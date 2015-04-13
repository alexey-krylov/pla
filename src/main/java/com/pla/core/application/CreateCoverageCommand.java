/*
 * Copyright (c) 3/12/15 6:32 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.application;

import com.pla.core.domain.model.BenefitId;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.security.core.userdetails.UserDetails;

import javax.validation.constraints.NotNull;
import java.util.Set;

/**
 * @author: Samir
 * @since 1.0 12/03/2015
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
@AllArgsConstructor
public class CreateCoverageCommand {

    private UserDetails userDetails;

    @NotNull(message = "{Coverage name cannot be null}")
    @NotEmpty(message = "{Coverage name cannot be empty}")
    @Length(max = 100, min = 1,message = "{Coverage name length should be between 1-100}")
    private String coverageName;

    private String coverageCode;
    private String description;

    private Set<BenefitId> benefitIds;
}
