/*
 * Copyright (c) 3/16/15 7:43 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

/**
 * @author: Samir
 * @since 1.0 13/03/2015
 */
@Getter
@Setter
@NoArgsConstructor
public class TeamDetailDto {

    @NotNull(message = "{Team id cannot be null}")
    @NotEmpty(message = "{Team id cannot be empty}")
    private String teamId;


}
