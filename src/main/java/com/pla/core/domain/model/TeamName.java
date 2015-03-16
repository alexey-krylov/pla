/*
 * Copyright (c) 3/11/15 4:00 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.domain.model;

import com.google.common.base.Preconditions;
import lombok.*;
import org.hibernate.annotations.Immutable;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;
import org.nthdimenzion.utils.UtilValidator;

import javax.persistence.Embeddable;



/**
 * @author: Samir
 * @since 1.0 11/03/2015
 */
@ValueObject
@Embeddable
@Immutable
@Getter
@EqualsAndHashCode(of = "teamName")
@ToString
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TeamName {

    private String teamName;

    public TeamName(String teamName) {
        this.teamName = teamName;
        Preconditions.checkState(!UtilValidator.isEmpty(teamName));
    }

}
