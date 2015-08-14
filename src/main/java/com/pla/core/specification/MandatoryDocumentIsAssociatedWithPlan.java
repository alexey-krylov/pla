package com.pla.core.specification;

import com.pla.core.dto.MandatoryDocumentDto;
import com.pla.core.query.MandatoryDocumentFinder;
import com.pla.sharedkernel.specification.ISpecification;
import org.nthdimenzion.ddd.domain.annotations.Specification;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Admin on 4/29/2015.
 */
@Specification
public class MandatoryDocumentIsAssociatedWithPlan implements ISpecification<MandatoryDocumentDto> {

    private MandatoryDocumentFinder mandatoryDocumentFinder;

    @Autowired
    public MandatoryDocumentIsAssociatedWithPlan(MandatoryDocumentFinder mandatoryDocumentFinder) {
        this.mandatoryDocumentFinder = mandatoryDocumentFinder;
    }

    @Override
    public boolean isSatisfiedBy(MandatoryDocumentDto candidate) {
        int count = mandatoryDocumentFinder.getMandatoryDocumentCountBy(candidate.getPlanId(), candidate.getProcess(),candidate.getCoverageId());
        return count == 0;
    }
}
