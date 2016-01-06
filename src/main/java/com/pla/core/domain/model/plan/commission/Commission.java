package com.pla.core.domain.model.plan.commission;

import com.pla.core.domain.exception.CommissionDomainException;
import com.pla.individuallife.sharedresource.model.vo.PremiumPaymentMethod;
import com.pla.sharedkernel.domain.model.CommissionDesignation;
import com.pla.sharedkernel.domain.model.CommissionType;
import com.pla.sharedkernel.domain.model.PremiumFee;
import com.pla.sharedkernel.domain.model.PremiumType;
import com.pla.sharedkernel.identifier.CommissionId;
import com.pla.sharedkernel.identifier.PlanId;
import lombok.*;
import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;
import org.nthdimenzion.common.crud.ICrudEntity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.*;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Created by User on 3/31/2015.
 */
@Entity
@Table(name = "commission")
@EqualsAndHashCode(of = {"planId", "availableFor", "fromDate"})
@ToString(of = {"planId", "fromDate"})
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Getter(value = AccessLevel.PACKAGE)
@NamedQuery(name = "findAllCommissionByPlanIdAndDesignationId", query = " FROM Commission WHERE planId=:planId AND availableFor =:availableFor AND commissionType=:commissionType AND thruDate IS NULL AND premiumPaymentType=:premiumPaymentType ")
public class Commission implements ICrudEntity {

    @EmbeddedId
    private CommissionId commissionId;

    private PlanId planId;

    @Enumerated(EnumType.STRING)
    private CommissionDesignation availableFor;

    @Enumerated(EnumType.STRING)
    private CommissionType commissionType;


    @Enumerated(EnumType.STRING)
    private PremiumFee premiumFee;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate fromDate;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate thruDate;

    @ElementCollection
    @OrderColumn
    @JoinTable(name = "COMMISSION_COMMISSION_TERM", joinColumns = @JoinColumn(name = "COMMISSION_ID"))
    private Set<CommissionTerm> commissionTerms;

    @Enumerated(EnumType.STRING)
    private PremiumPaymentType premiumPaymentType;

    Commission(CommissionId commissionId, PlanId planId, CommissionDesignation availableFor, CommissionType commissionType, PremiumFee premiumFee, LocalDate fromDate,PremiumPaymentType premiumPaymentType) {
        checkNotNull(commissionId);
        checkNotNull(planId);
        checkArgument(availableFor != null);
        checkArgument(commissionType != null);
        checkArgument(premiumFee != null);
        checkArgument(fromDate != null);
        checkState(fromDate.isAfter(LocalDate.now().minusDays(1)));
        this.commissionId = commissionId;
        this.planId = planId;
        this.availableFor = availableFor;
        this.commissionType = commissionType;
        this.premiumFee = premiumFee;
        this.fromDate = fromDate;
        this.premiumFee = premiumFee;
        this.premiumPaymentType  = premiumPaymentType;
    }

    public static Commission createCommission(CommissionId commissionId, PlanId planId, CommissionDesignation availableFor, CommissionType commissionType, PremiumFee premiumFee, LocalDate fromDate,PremiumPaymentType premiumPaymentType) {
        return new Commission(commissionId, planId, availableFor, commissionType, premiumFee, fromDate,premiumPaymentType);
    }

    public boolean validateOverLappingYears(Set<CommissionTerm> commissionTerms) {
        List<CommissionTerm> commissionTermList = new ArrayList(commissionTerms);
        for (int i = 0; i < commissionTermList.size(); i++) {
            final CommissionTerm commissionTermI = commissionTermList.get(i);
            for (int j = 0; j < commissionTermList.size(); j++) {
                final CommissionTerm commissionTermJ = commissionTermList.get(j);
                if (i != j) {
                    if (commissionTermJ.getStartYear() < commissionTermI.getEndYear()) {
                        if (!(commissionTermJ.getEndYear() < commissionTermI.getStartYear())) {
                            return false;
                        }
                    } else {
                        if (!(commissionTermJ.getEndYear() > commissionTermI.getStartYear())) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;

    }

    public Commission updateWithCommissionTerms(Set<CommissionTerm> commissionTerms, List<Integer> policyTerms) {
        checkArgument(isNotEmpty(policyTerms));
        if (!isWithinPlanPolicyTerms(commissionTerms, policyTerms))
            throw new CommissionDomainException("Not Within Plan Maturity Age!!");
        if (!validateOverLappingYears(commissionTerms))
            throw new CommissionDomainException("Overlapping Years!!");
        this.commissionTerms = commissionTerms;
        return this;
    }

    public Commission expireCommission(LocalDate expireDate) {
        this.thruDate = expireDate;
        return this;
    }

    public Commission addCommissionTerm(Set<CommissionTerm> commissionTerms, List<Integer> policyTerms) {
        checkArgument(isNotEmpty(policyTerms));
        if (!isWithinPlanPolicyTerms(commissionTerms, policyTerms))
            throw new CommissionDomainException("Not Within Plan Policy Terms!!");
        if (!validateOverLappingYears(commissionTerms))
            throw new CommissionDomainException("Overlapping Years!!");
        this.commissionTerms = commissionTerms;

        return this;


    }

    boolean isWithinPlanPolicyTerms(Set<CommissionTerm> commissionTerms, List<Integer> policyTerms) {
        Collections.sort(policyTerms);
        Integer minimumPolicyTerm = policyTerms.get(0);
        Integer maximumPolicyTerm = policyTerms.get(policyTerms.size() - 1);
        if (minimumPolicyTerm == maximumPolicyTerm) {
            minimumPolicyTerm = 0;
        }
        List<CommissionTerm> commissionTermList = commissionTerms.stream().filter(new CommissionTermsWithinPolicyTerms(minimumPolicyTerm, maximumPolicyTerm)).collect(Collectors.toList());
        return commissionTerms.size() == commissionTermList.size();
    }


    private class CommissionTermsWithinPolicyTerms implements Predicate<CommissionTerm> {
        private Integer minMaturity;
        private Integer maxMaturity;

        private CommissionTermsWithinPolicyTerms(Integer minMaturity, Integer maxMaturity) {
            this.minMaturity = minMaturity;
            this.maxMaturity = maxMaturity;
        }

        @Override
        public boolean test(CommissionTerm commissionTerm) {
            return commissionTerm.getEndYear() <= maxMaturity;
        }
    }

    public void validateNewCommissionPeriodForAPlanAndDesignation(LocalDate fromDate) {
        try {
            checkArgument(fromDate.isAfter(this.fromDate));
        } catch (IllegalArgumentException e) {
            String message = CommissionType.NORMAL.equals(this.commissionType) ? "Commission has already been associated with this product and designation for this period" :
                    "Over-ride Commission has already been associated with this product and designation for this period";
            throw new CommissionDomainException(message);
        }
    }
}



