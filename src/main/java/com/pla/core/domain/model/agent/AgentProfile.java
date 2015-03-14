/*
 * Copyright (c) 3/13/15 8:27 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.domain.model.agent;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Type;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

import javax.persistence.Embeddable;
import java.time.LocalDate;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * @author: Samir
 * @since 1.0 13/03/2015
 */
@ValueObject
@Immutable
@Embeddable
@Getter(value = AccessLevel.PACKAGE)
@EqualsAndHashCode(of = {"nrcNumber", "employeeId"})
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class AgentProfile {

    private String title;

    private String firstName;

    private String lastName;

    private Integer nrcNumber;

    private String employeeId;

    private Designation designation;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate trainingCompleteOn;


    AgentProfile(String firstName, String lastName, LocalDate trainingCompleteOn, Designation designation) {
        checkArgument(isNotEmpty(firstName));
        checkArgument(isNotEmpty(lastName));
        checkArgument(trainingCompleteOn != null);
        checkArgument(designation != null);
        checkState(trainingCompleteOn.isBefore(LocalDate.now().plusDays(1)));
        this.firstName = firstName;
        this.lastName = lastName;
        this.trainingCompleteOn = trainingCompleteOn;
        this.designation = designation;
    }

    public AgentProfile addTitle(String title) {
        checkArgument(isNotEmpty(title));
        this.title = title;
        return this;
    }

    public AgentProfile addNrcNumber(Integer nrcNumber) {
        checkArgument(nrcNumber != null);
        checkArgument(nrcNumber.toString().length() < 9);
        this.nrcNumber = nrcNumber;
        return this;
    }

    public AgentProfile addEmployeeId(String employeeId) {
        checkArgument(isNotEmpty(employeeId));
        this.employeeId = employeeId;
        return this;
    }
}
