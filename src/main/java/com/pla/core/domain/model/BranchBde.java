package com.pla.core.domain.model;

import jdk.nashorn.internal.ir.annotations.Immutable;
import lombok.*;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

import javax.persistence.Embeddable;

/**
 * Created by User on 3/20/2015.
 */
@ValueObject
@Immutable
@Embeddable
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@EqualsAndHashCode(of = "employeeId")
@Getter(value = AccessLevel.PACKAGE)
@Setter(value = AccessLevel.PRIVATE)
class BranchBde
{
    private String employeeId;

    private String firstName;

    private String lastName;

    BranchBde(String employeeId, String firstName, String lastName) {
        this.employeeId = employeeId;
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
