/*
 * Copyright (c) 3/13/15 8:54 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.sharedkernel.domain.model;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

import javax.persistence.Embeddable;

/**
 * @author: Samir
 * @since 1.0 13/03/2015
 */
@ValueObject
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Embeddable
@Getter
@EqualsAndHashCode(of = "email")
public class EmailAddress {

    private String email;

    public EmailAddress(String email) {
        this.email = email;
    }

}
