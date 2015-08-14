package com.pla.core.specification;

import com.pla.core.dto.CoverageDto;
import com.pla.core.query.PlanFinder;
import com.pla.sharedkernel.identifier.CoverageId;
import com.pla.sharedkernel.specification.ISpecification;
import org.nthdimenzion.ddd.domain.annotations.Specification;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by Admin on 4/22/2015.
 */
@Specification
public class CoverageIsAssociatedWithPlan implements ISpecification<CoverageDto> {

    private PlanFinder planFinder;

    @Autowired
    public CoverageIsAssociatedWithPlan(PlanFinder planFinder){
        this.planFinder = planFinder;
    }

    @Override
    public boolean isSatisfiedBy(CoverageDto candidate) {
        List<CoverageId> coverageAssociatedWithPlan = planFinder.getAllCoverageAssociatedWithPlan();
        return coverageAssociatedWithPlan.contains(new CoverageId(candidate.getCoverageId()));
    }

}
