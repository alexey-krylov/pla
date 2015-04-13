package com.pla.core.specification;

import com.pla.core.dto.CoverageDto;
import com.pla.core.query.CoverageFinder;
import com.pla.sharedkernel.specification.CompositeSpecification;
import org.nthdimenzion.ddd.domain.annotations.Specification;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Admin on 4/10/2015.
 */
@Specification
public class CoverageCodeIsUnique extends CompositeSpecification<CoverageDto>{

    private CoverageFinder coverageFinder;

    @Autowired
    public CoverageCodeIsUnique(CoverageFinder coverageFinder) {
        this.coverageFinder = coverageFinder;
    }

    @Override
    public boolean isSatisfiedBy(CoverageDto candidate) {
        int noOfCoverage = coverageFinder.getCoverageCountByCoverageCode(candidate.getCoverageCode(),candidate.getCoverageId());
        return noOfCoverage==0;
    }
}
