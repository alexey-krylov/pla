package com.pla.core.specification;

import com.pla.core.dto.BenefitDto;
import com.pla.core.query.BenefitFinder;
import com.pla.sharedkernel.specification.CompositeSpecification;
import org.nthdimenzion.ddd.domain.annotations.Specification;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Admin on 6/2/2015.
 */
@Specification
public class BenefitCodeIsUnique extends CompositeSpecification<BenefitDto> {

    private BenefitFinder benefitFinder;

    @Autowired
    public BenefitCodeIsUnique(BenefitFinder benefitFinder) {
        this.benefitFinder = benefitFinder;
    }

    @Override
    public boolean isSatisfiedBy(BenefitDto benefitDto) {
        int benefitCount = benefitFinder.getBenefitCountByBenefitCode(benefitDto.getBenefitCode(), benefitDto.getBenefitId());
        return benefitCount == 0;
    }
}