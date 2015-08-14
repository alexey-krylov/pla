package com.pla.individuallife.quotation.query;

import com.pla.core.domain.model.CoverageName;
import com.pla.sharedkernel.identifier.CoverageId;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Created by Karunakar on 6/1/2015.
 */
@Getter
@Setter
@NoArgsConstructor
public class RiderPremiumDto {

    private CoverageId coverageId;

    private CoverageName coverageName;

    private BigDecimal annualPremium;
}
