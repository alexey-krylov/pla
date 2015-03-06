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
@EqualsAndHashCode(of = "benefitName")
@ToString(of = "benefitName")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class Benefit implements ICrudEntity {

    @Id
    private String benefitId;

    @Column(length = 100)
    private String benefitName;

    private boolean active;

    Benefit(String benefitId, String benefitName, boolean active) {
        this.benefitId = benefitId;
        this.benefitName = benefitName;
        this.active = active;
    }


    public Benefit updateBenefitWithName(String benefitName) {
        if (!this.active) {
            throw new RuntimeException("Cannot update an inactive benefit");
        }
        Benefit benefit = new Benefit(this.benefitId, benefitName, true);
        return benefit;
    }

    public Benefit deactivate() {
        Benefit benefit = new Benefit(this.benefitId, this.benefitName, false);
        return benefit;
    }

}
