/*
 * Copyright (c) 3/5/15 5:32 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.domain.model;

import com.pla.core.domain.exception.BenefitDomainException;
import com.pla.core.specification.BenefitIsUpdatable;
import com.pla.core.specification.BenefitNameIsUnique;
import com.pla.sharedkernel.domain.model.BenefitStatus;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

/**
 * @author: Samir
 * @since 1.0 05/03/2015
 */
@ValueObject
public class Admin {


    public Benefit createBenefit(BenefitNameIsUnique benefitNameIsUnique, String benefitId, String name) {
        BenefitName benefitName = new BenefitName(name);
        if (!benefitNameIsUnique.isSatisfiedBy(benefitName)) {
            throw new BenefitDomainException("Benefit name already satisfied");
        }
        return new Benefit(benefitId, benefitName, BenefitStatus.ACTIVE);
    }

    public Benefit updateBenefit(Benefit benefit, String name, BenefitNameIsUnique benefitNameIsUnique, BenefitIsUpdatable benefitIsUpdatable) {
        BenefitName benefitName = new BenefitName(name);
        if (!benefitIsUpdatable.isSatisfiedBy(benefit.getBenefitId(), benefitName)) {
            throw new BenefitDomainException("Benefit name cannot be updated. New name is required");
        }
        if (!benefitIsUpdatable.isGeneralizationOf(benefitNameIsUnique, benefitName)) {
            throw new BenefitDomainException("Benefit name already satisfied");
        }
        Benefit updatedBenefit = benefit.updateBenefitName(benefitName);
        return updatedBenefit;
    }

    public Benefit inactivateBenefit(Benefit benefit) {
        Benefit updatedBenefit = benefit.inActivate();
        return updatedBenefit;
    }
}
