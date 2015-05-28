package com.pla.individuallife.quotation.query;

import com.pla.core.domain.model.CoverageName;
import com.pla.sharedkernel.identifier.CoverageId;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Set;

/**
 * Created by Karunakar on 5/13/2015.
 */
@Getter
@Setter
@NoArgsConstructor
public class PremiumDetailDto {


    private Integer policyTermValue;

    private BigDecimal planAnnualPremium;

    private BigDecimal semiannualPremium;

    private BigDecimal quarterlyPremium;

    private BigDecimal monthlyPremium;

    private BigDecimal totalPremium;

    private Set<RiderPremiumDto> riderPremiumDtos;

    @Getter
    @Setter
    @NoArgsConstructor
    public class RiderPremiumDto {

        private CoverageId coverageId;

        private CoverageName coverageName;

        private BigDecimal annualPremium;
    }

}
