package com.pla.core.domain.model.plan;

import lombok.Getter;
import lombok.ToString;
import org.nthdimenzion.utils.UtilValidator;

import java.math.BigDecimal;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author: pradyumna
 * @since 1.0 12/03/2015
 */
@ToString
@Getter
class SumAssured {

    private Set<BigDecimal> sumInsuredValues;

    protected SumAssured() {
    }

    SumAssured(Set<BigDecimal> sumInsuredValues) {
        checkArgument(UtilValidator.isNotEmpty(sumInsuredValues));
        this.sumInsuredValues = sumInsuredValues;
    }
}
