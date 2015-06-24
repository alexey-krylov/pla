package com.pla.grouphealth.sharedresource.model.vo;

import com.pla.sharedkernel.identifier.BenefitId;
import lombok.*;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

import java.math.BigDecimal;

/**
 * Created by Samir on 6/4/2015.
 */
@ValueObject
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
@Setter(value = AccessLevel.PACKAGE)
@EqualsAndHashCode(of = "benefitCode")
public class BenefitPremiumLimit {

    private String benefitCode;

    private BenefitId benefitId;

    private BigDecimal benefitLimit;

    BenefitPremiumLimit(String benefitCode, BenefitId benefitId, BigDecimal benefitLimit) {
        this.benefitCode = benefitCode;
        this.benefitId = benefitId;
        this.benefitLimit = benefitLimit;
    }
}
