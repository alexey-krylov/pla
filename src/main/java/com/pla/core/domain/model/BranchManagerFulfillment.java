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
import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;


/**
 * @author: Nischitha
 * @since 1.0 20/03/2015
 */
@ValueObject
@Getter(value = AccessLevel.PUBLIC)
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@EqualsAndHashCode(of = {"branchManager", "fromDate"})
@Embeddable
public class BranchManagerFulfillment {

    @Embedded
    private BranchManager branchManager;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate fromDate;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate thruDate;

    public BranchManagerFulfillment(BranchManager branchManager, LocalDate fromDate) {
        this.branchManager = branchManager;
        this.fromDate = fromDate;
    }

    public BranchManagerFulfillment expireFulfillment(LocalDate thruDate) {
        this.thruDate = thruDate;
        return this;
    }

}
