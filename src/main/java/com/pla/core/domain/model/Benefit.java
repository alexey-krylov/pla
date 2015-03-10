/*
 * Copyright (c) 3/3/15 5:52 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.domain.model;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.nthdimenzion.common.crud.ICrudEntity;

import javax.persistence.*;

/**
 * @author: Samir
 * @since 1.0 03/03/2015
 */
@Entity
@Table(name = "benefit", uniqueConstraints = {@UniqueConstraint(name = "UNQ_BENEFIT_NAME", columnNames = "benefitName")})
@EqualsAndHashCode(of = {"benefitName", "benefitId"})
@ToString(of = "benefitName")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Benefit implements ICrudEntity {

    @Id
    private String benefitId;

    @Embedded
    private BenefitName benefitName;

    @Enumerated(EnumType.STRING)
    private BenefitStatus status;

    Benefit(String benefitId, BenefitName benefitName, BenefitStatus benefitStatus) {
        this.benefitId = benefitId;
        this.benefitName = benefitName;
        this.status = benefitStatus;
    }

}
