/*
 * Copyright (c) 3/13/15 8:54 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.sharedkernel.domain.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

import javax.persistence.Embeddable;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkArgument;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * @author: Samir
 * @since 1.0 13/03/2015
 */
@ValueObject
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Embeddable
@Getter
public class EmailAddress {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("");

    private String email;

    public EmailAddress(String email) {
        checkArgument(isNotEmpty(email));
        this.email = email;
    }

    public boolean isValidEmail(String email) {
    return false;
    }
}
