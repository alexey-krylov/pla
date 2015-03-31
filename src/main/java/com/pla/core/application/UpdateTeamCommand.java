/*
 * Copyright (c) 3/10/15 1:39 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.application;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * @author: Nischitha
 * @since 1.0 12/03/2015
 */

@NoArgsConstructor
@Getter
@Setter
@ToString
public class UpdateTeamCommand {

    private String teamId;

    private String employeeId;

    private String firstName;

    private String lastName;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate fromDate;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate thruDate;

    private UserDetails userDetails;

}
