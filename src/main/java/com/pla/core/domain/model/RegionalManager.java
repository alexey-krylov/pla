package com.pla.core.domain.model;

import lombok.*;
import org.hibernate.annotations.Immutable;
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
class RegionalManager
{

    private String employeeId;

    private String firstName;

    private String lastName;

     RegionalManager(String employeeId, String firstName, String lastName) {
        this.employeeId = employeeId;
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
