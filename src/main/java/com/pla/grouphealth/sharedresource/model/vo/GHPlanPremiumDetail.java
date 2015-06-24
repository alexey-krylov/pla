package com.pla.grouphealth.sharedresource.model.vo;

import com.google.common.collect.Lists;
import com.pla.sharedkernel.identifier.PlanId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

import java.math.BigDecimal;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static org.nthdimenzion.utils.UtilValidator.isEmpty;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Created by Samir on 4/29/2015.
 */
@ValueObject
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
@Setter(value = AccessLevel.PACKAGE)
public class GHPlanPremiumDetail {

    private PlanId planId;

    private String planCode;

    private BigDecimal premiumAmount;

    private BigDecimal sumAssured;

    private List<GHCoveragePremiumDetail> coveragePremiumDetails;

    GHPlanPremiumDetail(PlanId planId, String planCode, BigDecimal premiumAmount, BigDecimal sumAssured) {
        checkArgument(planId != null);
        checkArgument(isNotEmpty(planCode));
        checkArgument(premiumAmount != null);
        checkArgument(sumAssured != null);
        this.planId = planId;
        this.planCode = planCode;
        this.premiumAmount = premiumAmount;
        this.sumAssured = sumAssured;
    }

    public GHPlanPremiumDetail updatePremiumAmount(BigDecimal premiumAmount) {
        this.premiumAmount = premiumAmount;
        return this;
    }

    public GHPlanPremiumDetail addAllCoveragePremiumDetail(List<GHCoveragePremiumDetail> ghCoveragePremiumDetails) {
        if (isEmpty(this.coveragePremiumDetails)) {
            this.coveragePremiumDetails = Lists.newArrayList();
        }
        this.coveragePremiumDetails.addAll(ghCoveragePremiumDetails);
        return this;
    }
}
