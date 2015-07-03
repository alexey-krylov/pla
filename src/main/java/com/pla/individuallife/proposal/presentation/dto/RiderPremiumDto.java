package com.pla.individuallife.proposal.presentation.dto;

import com.pla.core.domain.model.CoverageName;
import com.pla.sharedkernel.identifier.CoverageId;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Created by Karunakar on 7/3/2015.
 */
@Getter
@Setter
@NoArgsConstructor
public class RiderPremiumDto {

    private CoverageId coverageId;

    private CoverageName coverageName;

    private BigDecimal annualPremium;
}
