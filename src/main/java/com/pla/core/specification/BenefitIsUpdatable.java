/*
 * Copyright (c) 3/11/15 9:35 AM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.specification;

import com.pla.core.domain.model.BenefitId;
import com.pla.core.domain.model.BenefitName;
import com.pla.core.query.BenefitFinder;
import com.pla.sharedkernel.specification.CompositeSpecification;
import org.nthdimenzion.ddd.domain.annotations.Specification;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author: Samir
 * @since 1.0 11/03/2015
 */
@Specification
public class BenefitIsUpdatable extends CompositeSpecification<BenefitId, BenefitName> {


    private BenefitFinder benefitFinder;

    @Autowired
    public BenefitIsUpdatable(BenefitFinder benefitFinder) {
        this.benefitFinder = benefitFinder;

    }

    @Override
    public boolean isSatisfiedBy(BenefitId candidate) {
        int noOfBenefit = benefitFinder.getBenefitCountAssociatedWithActiveCoverage(candidate.getBenefitId());
        return noOfBenefit == 0;
    }
}
