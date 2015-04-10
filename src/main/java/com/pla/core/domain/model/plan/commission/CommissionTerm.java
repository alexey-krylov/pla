package com.pla.core.domain.model.plan.commission;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Embeddable;
import java.math.BigDecimal;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Created by User on 3/31/2015.
 */
@ValueObject
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@EqualsAndHashCode(of = {"fromDate", "startYear", "endYear"})
@Getter
@Embeddable
public class CommissionTerm {
    private static final Logger logger = LoggerFactory.getLogger(CommissionTerm.class);

    private Integer startYear;

    private Integer endYear;

    private BigDecimal commissionPercentage;

    CommissionTerm(Integer startYear, Integer endYear, BigDecimal commissionPercentage) {
        checkArgument(startYear != null);
        checkArgument(endYear != null);
        checkArgument(startYear > 0);
        checkArgument(endYear > 0);
        checkArgument(endYear >= startYear);
        this.startYear = startYear;
        this.endYear = endYear;
        this.commissionPercentage = commissionPercentage;
    }

    public static CommissionTerm createCommissionTerm(Integer startYear, Integer endYear, BigDecimal commissionPercentage) {
        return new CommissionTerm(startYear, endYear, commissionPercentage);
    }

}
