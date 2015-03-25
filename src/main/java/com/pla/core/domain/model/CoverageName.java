/*
 * Copyright (c) 3/10/15 8:53 AM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.domain.model;

import com.google.common.base.Preconditions;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Immutable;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * @author: Samir
 * @since 1.0 10/03/2015
 */
@ValueObject
@Immutable
@Embeddable
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@ToString(of = "coverageName")
@EqualsAndHashCode(of = "coverageName")
public class CoverageName {

    @Column(length = 50)
    private String coverageName;

    public CoverageName(String coverageName) {
        Preconditions.checkNotNull(coverageName);
        this.coverageName = coverageName;

    }
}
