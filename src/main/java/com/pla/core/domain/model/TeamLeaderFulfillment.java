/*
 * Copyright (c) 3/11/15 3:51 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.domain.model;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;


/**
 * @author: Samir
 * @since 1.0 11/03/2015
 */
@ValueObject
@Getter(value = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = {"teamLeader", "fromDate"})
@Embeddable
public class TeamLeaderFulfillment {

    @Embedded
    private TeamLeader teamLeader;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate fromDate;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate thruDate;

    TeamLeaderFulfillment(TeamLeader teamLeader, LocalDate fromDate) {
        this.teamLeader = teamLeader;
        this.fromDate = fromDate;
    }

    public TeamLeaderFulfillment expireFulfillment(LocalDate thruDate) {
        this.thruDate = thruDate;
        return this;
    }
}
