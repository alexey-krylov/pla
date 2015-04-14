package com.pla.core.domain.model.plan.commission;

import com.pla.sharedkernel.domain.model.CommissionTermType;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.math.BigDecimal;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Created by User on 3/31/2015.
 */
@ValueObject
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@EqualsAndHashCode(of = {"startYear", "endYear", "commissionPercentage", "commissionTermType"})
@Getter
@Embeddable
public class CommissionTerm {
    private static final Logger logger = LoggerFactory.getLogger(CommissionTerm.class);

    @Enumerated(EnumType.STRING)
    private CommissionTermType commissionTermType;

    private Integer startYear;

    private Integer endYear;

    private BigDecimal commissionPercentage;

    CommissionTerm(Integer startYear, Integer endYear, BigDecimal commissionPercentage, CommissionTermType commissionTermType) {
        checkArgument(commissionTermType != null);
        checkArgument(startYear != null);
        checkArgument(startYear > 0);
        this.commissionTermType = commissionTermType;
        this.startYear = startYear;
        this.endYear = endYear;
        this.commissionPercentage = commissionPercentage;
    }

    public static CommissionTerm createCommissionTerm(Integer startYear, Integer endYear, BigDecimal commissionPercentage, CommissionTermType commissionTermType) {
        return new CommissionTerm(startYear, endYear, commissionPercentage, commissionTermType);
    }
}
