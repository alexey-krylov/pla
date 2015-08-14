/*
 * Copyright (c) 3/11/15 3:51 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.domain.model;

import lombok.*;
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
@Getter(value = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@EqualsAndHashCode(of = {"branchBde", "fromDate"})
@Embeddable
public class BranchBdeFulfillment {

    @Embedded
    private BranchBde branchBde;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate fromDate;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate thruDate;

    public BranchBdeFulfillment(BranchBde branchBde, LocalDate fromDate) {
        this.branchBde = branchBde;
        this.fromDate = fromDate;
    }

    public BranchBdeFulfillment expireFulfillment(LocalDate thruDate) {
        this.thruDate = thruDate;
        return this;
    }

}
