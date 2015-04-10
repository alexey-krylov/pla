package com.pla.core.specification;

import com.pla.core.query.CoverageFinder;
import com.pla.sharedkernel.specification.ISpecification;
import org.nthdimenzion.ddd.domain.annotations.Specification;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Admin on 4/10/2015.
 */
@Specification
public class CoverageCodeIsUnique implements ISpecification<String> {

    private CoverageFinder coverageFinder;

    @Autowired
    public CoverageCodeIsUnique(CoverageFinder coverageFinder) {
        this.coverageFinder = coverageFinder;

    }

    @Override
    public boolean isSatisfiedBy(String coverageCode) {
        int coverageCount = coverageFinder.getCoverageCountByCoverageCode(coverageCode);
        return coverageCount == 0;
    }
}
