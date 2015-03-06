/*
 * Copyright (c) 3/5/15 3:49 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.specification;

import com.pla.core.query.BenefitFinder;
import com.pla.sharedkernel.specification.ISpecification;
import lombok.NoArgsConstructor;
import org.nthdimenzion.ddd.domain.annotations.Specification;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author: Samir
 * @since 1.0 05/03/2015
 */
@Specification
public class BenefitNameIsUnique implements ISpecification<String> {

    private BenefitFinder benefitFinder;

    @Autowired
    public BenefitNameIsUnique(BenefitFinder benefitFinder) {
        this.benefitFinder = benefitFinder;

    }

    @Override
    public boolean isSatisfiedBy(String benefitName) {
        int benefitCount = benefitFinder.getBenefitCountByBenefitName(benefitName);
        return (benefitCount == 0);
    }

}
