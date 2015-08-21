/*
 * Copyright (c) 3/10/15 9:54 AM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.domain.model;

import lombok.*;
import org.hibernate.annotations.Immutable;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

import javax.persistence.Embeddable;

/**
 * @author: Nischitha
 * @since 1.0 10/03/2015
 */
@ValueObject
@Immutable
@Embeddable
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@EqualsAndHashCode(of = "employeeId")
@Getter(value = AccessLevel.PACKAGE)
@Setter(value = AccessLevel.PRIVATE)
class TeamLeader {

    private String employeeId;

    private String firstName;

    private String lastName;

    TeamLeader(String employeeId, String firstName, String lastName) {
        this.employeeId = employeeId;
        this.firstName = firstName;
        this.lastName = lastName;
    }

}
