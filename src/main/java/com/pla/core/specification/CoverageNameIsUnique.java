package com.pla.core.specification;

import com.pla.core.domain.model.CoverageName;
import com.pla.core.query.CoverageFinder;
import com.pla.sharedkernel.specification.ISpecification;
import org.nthdimenzion.ddd.domain.annotations.Specification;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created with IntelliJ IDEA.
 * User: Admin
 * Date: 3/23/15
 * Time: 10:24 AM
 * To change this template use File | Settings | File Templates.
 */
@Specification
public class CoverageNameIsUnique implements ISpecification<CoverageName> {

    private CoverageFinder coverageFinder;

    @Autowired
    public CoverageNameIsUnique(CoverageFinder coverageFinder) {
        this.coverageFinder = coverageFinder;

    }

    @Override
    public boolean isSatisfiedBy(CoverageName candidate) {
        int coverageCount = coverageFinder.getCoverageCountByCoverageName(candidate.toString());
        return coverageCount == 0;
    }
}
