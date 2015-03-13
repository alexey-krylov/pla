/*
 * Copyright (c) 3/3/15 5:52 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.domain.model;

import com.google.common.base.Preconditions;
import com.pla.core.domain.exception.BenefitDomainException;
import com.pla.sharedkernel.domain.model.BenefitStatus;
import lombok.*;
import org.nthdimenzion.common.crud.ICrudEntity;

import javax.persistence.*;

/**
 * @author: Samir
 * @since 1.0 03/03/2015
 */
@Entity
@Table(name = "benefit", uniqueConstraints = {@UniqueConstraint(name = "UNQ_BENEFIT_NAME", columnNames = "benefitName")})
@EqualsAndHashCode(of = {"benefitName"})
@ToString(of = "benefitName")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter(value = AccessLevel.PACKAGE)
public class BenefitId implements ICrudEntity {

    @Id
    private String benefitId;

    @Embedded
    private BenefitName benefitName;

    @Enumerated(EnumType.STRING)
    private BenefitStatus status;

    BenefitId(String benefitId, BenefitName benefitName, BenefitStatus benefitStatus) {
        Preconditions.checkNotNull(benefitId);
        Preconditions.checkNotNull(benefitName);
        Preconditions.checkState(benefitStatus.equals(BenefitStatus.ACTIVE));
        this.benefitId = benefitId;
        this.benefitName = benefitName;
        this.status = benefitStatus;
    }

    public BenefitId updateBenefitName(BenefitName benefitName) {
        Preconditions.checkNotNull(benefitName);
        Preconditions.checkState(isUpdatable());
        this.benefitName = benefitName;
        return this;
    }

    public BenefitId markAsUsed() {
        if (BenefitStatus.INACTIVE.equals(this.status)) {
            throw new BenefitDomainException("Benefit cannot be marked as used as it is in inactive state");
        }
        this.status = BenefitStatus.INUSE;
        return this;
    }

    public BenefitId inActivate() {
        Preconditions.checkState(isUpdatable());
        this.status = BenefitStatus.INACTIVE;
        return this;
    }

    public boolean isUpdatable() {
        if (BenefitStatus.INACTIVE.equals(this.status) || BenefitStatus.INUSE.equals(this.status)) {
            throw new BenefitDomainException("Benefit name cannot be updated as it has been used in coverage/in inactive state");
        }
        return true;
    }

}
