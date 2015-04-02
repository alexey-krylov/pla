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

/**
 * @author: Nischitha
 * @since 1.0 27/03/2015
 */

@NoArgsConstructor
@Getter
@Setter
@ToString
public class UpdateBranchManagerCommand {

    private String branchCode;

    private String branchManagerEmployeeId;

    private String branchManagerFirstName;

    private String branchManagerLastName;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate branchManagerFromDate;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate branchManagerThruDate;

    private String branchBDEEmployeeId;

    private String branchBDEFirstName;

    private String branchBDELastName;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate branchBDEFromDate;



}
