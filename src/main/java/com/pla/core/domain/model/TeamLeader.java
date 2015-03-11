/*
 * Copyright (c) 3/10/15 9:54 AM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.domain.model;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

import javax.persistence.Embeddable;

/**
 * @author: Samir
 * @since 1.0 10/03/2015
 */
@ValueObject
@Immutable
@Embeddable
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = "employeeId")
public class TeamLeader {

    private String employeeId;

    private String firstName;

    private String lastName;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate fromDate;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate thruDate;

    TeamLeader(String employeeId, LocalDate fromDate, LocalDate thruDate, String firstName, String lastName) {
        this.employeeId = employeeId;
        this.fromDate = fromDate;
        this.thruDate = thruDate;
        this.firstName = firstName;
        this.lastName = lastName;
    }

}
