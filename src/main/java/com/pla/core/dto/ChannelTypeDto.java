/*
 * Copyright (c) 3/16/15 7:47 PM .NthDimenzion,Inc - All Rights Reserved
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
 * @since 1.0 16/03/2015
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChannelTypeDto {

    @NotNull(message = "{Channel code cannot be null}")
    @NotEmpty(message = "{Channel code cannot be empty}")
    private String channelCode;

    @NotNull(message = "{Channel Name cannot be null}")
    @NotEmpty(message = "{Channel Name cannot be empty}")
    private String channelName;



}
