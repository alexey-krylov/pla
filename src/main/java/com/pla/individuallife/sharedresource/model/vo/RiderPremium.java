package com.pla.individuallife.sharedresource.model.vo;

import com.pla.core.domain.model.CoverageName;
import com.pla.sharedkernel.identifier.CoverageId;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Created by Admin on 8/6/2015.
 */
@Setter
@Getter
public class RiderPremium {
    private CoverageId coverageId;
    private CoverageName coverageName;
    private BigDecimal annualPremium;
    private BigDecimal monthlyPremium;
    private BigDecimal SemiAnnualPremium;
    private BigDecimal QuarterlyPremium;
}
