/*
 * Copyright (c) 3/16/15 7:38 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.application.agent;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

/**
 * @author: Samir
 * @since 1.0 13/03/2015
 */
@Getter
@Setter
public class AgentProfileDto {

    private String title;

    @NotNull(message = "{First name cannot be null}")
    @NotEmpty(message = "{First name cannot be empty}")
    private String firstName;

    @NotNull(message = "{Last name cannot be null}")
    @NotEmpty(message = "{Last name cannot be empty}")
    private String lastName;

    private Integer nrcNumber;

    private String employeeId;

    private DesignationDto designationDto=new DesignationDto();

}
