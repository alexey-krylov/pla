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
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.LocalDate;
import org.springframework.security.core.userdetails.UserDetails;

import javax.validation.constraints.NotNull;

/**
 * @author: Nischitha
 * @since 1.0 10/03/2015
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
public class CreateTeamCommand {

    @NotNull(message = "{Team name cannot be null}")
    @NotEmpty(message = "{Team name cannot be empty}")
    @Length(max = 100, min = 1,message = "{Team name length should be between 1-100}")
    private String teamName;

    @NotNull(message = "{Team code cannot be null}")
    @NotEmpty(message = "{Team code cannot be empty}")
    @Length(max = 100, min = 1,message = "{Team code length should be between 1-100}")
    private String teamCode;

    private String employeeId;

    private String firstName;

    private String lastName;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate fromDate;

    private UserDetails userDetails;

    private String regionCode;

    private String branchCode;


}
