/*
 * Copyright (c) 3/5/15 5:32 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.domain.model;

import com.pla.core.domain.exception.BenefitException;
import com.pla.core.specification.BenefitNameIsUnique;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

/**
 * @author: Samir
 * @since 1.0 05/03/2015
 */
@ValueObject
public class Admin {

    private String userName;

    public Admin(String userName) {
        this.userName = userName;

    }
    public Benefit createBenefit(BenefitNameIsUnique benefitNameIsUnique, String benefitId, String name) {
        BenefitName benefitName = new BenefitName(name);
        if (!benefitNameIsUnique.isSatisfiedBy(benefitName)) {
            BenefitException.raiseBenefitNameNotUniqueException();
        }
        Benefit benefit = new Benefit(benefitId, benefitName, true);
        return benefit;
    }

}
