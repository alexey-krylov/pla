/*
 * Copyright (c) 3/9/15 10:55 AM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.domain.model;

import com.google.common.base.Preconditions;
import lombok.*;
import org.hibernate.annotations.Immutable;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * @author: Samir
 * @since 1.0 09/03/2015
 */
@Embeddable
@ValueObject
@Getter
@ToString(of = "benefitName")
@EqualsAndHashCode(of = "benefitName")
@Immutable
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BenefitName {

    @Column(length = 100)
    private String benefitName;

    public BenefitName(String benefitName) {
        Preconditions.checkNotNull(benefitName);
        this.benefitName = benefitName;
    }
}
