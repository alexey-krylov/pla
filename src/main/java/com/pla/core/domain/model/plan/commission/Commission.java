package com.pla.core.domain.model.plan.commission;

import com.pla.core.domain.exception.CommissionDomainException;
import com.pla.core.domain.model.plan.Plan;
import com.pla.sharedkernel.domain.model.CommissionDesignation;
import com.pla.sharedkernel.domain.model.CommissionType;
import com.pla.sharedkernel.domain.model.PremiumFee;
import com.pla.sharedkernel.identifier.CommissionId;
import com.pla.sharedkernel.identifier.PlanId;
import lombok.*;
import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;
import org.nthdimenzion.common.crud.ICrudEntity;
import org.nthdimenzion.utils.UtilValidator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.*;

/**
 * Created by User on 3/31/2015.
 */
@Entity
@Table(name = "commission")
@EqualsAndHashCode(of = {"planId", "availableFor", "fromDate"})
@ToString(of = {"planId", "fromDate"})
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Getter(value = AccessLevel.PACKAGE)
@NamedQuery(name = "findAllCommissionByPlanIdAndDesignationId", query = " FROM Commission WHERE planId=:planId AND availableFor =:availableFor AND thruDate IS NULL")
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

    Commission(CommissionId commissionId, PlanId planId, CommissionDesignation availableFor, CommissionType commissionType, PremiumFee premiumFee, LocalDate fromDate) {
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

    }

    public static Commission createCommission(CommissionId commissionId, PlanId planId, CommissionDesignation availableFor, CommissionType commissionType, PremiumFee premiumFee, LocalDate fromDate) {
        return new Commission(commissionId, planId, availableFor, commissionType, premiumFee, fromDate);
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

    public Commission updateWithCommissionTerms(Set<CommissionTerm> commissionTerms, Plan plan) {
        if (!isWithinPlanMaturityAge(commissionTerms, plan))
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

    public Commission addCommissionTerm(Set<CommissionTerm> commissionTerms, Plan plan) {
        if (!isWithinPlanMaturityAge(commissionTerms, plan))
            throw new CommissionDomainException("Not Within Plan Maturity Age!!");
        if (!validateOverLappingYears(commissionTerms))
            throw new CommissionDomainException("Overlapping Years!!");
        this.commissionTerms = commissionTerms;

        return this;


    }

    boolean isWithinPlanMaturityAge(Set<CommissionTerm> commissionTerms, Plan plan) {

        return (isLesserThanMaxPlanMaturityAge(commissionTerms, this.getMaximumPlanMaturityAge(plan)) && ((isGreaterThanMinPlanMaturityAge(commissionTerms, this.getMinimumPlanMaturityAge(plan)))));
    }

    Integer getMaximumPlanMaturityAge(Plan plan) {
        List<Integer> maturityAges = new ArrayList<Integer>();
        maturityAges.addAll(plan.getAllowedPolicyTerm());
        Collections.sort(maturityAges);
        return maturityAges.get(maturityAges.size() - 1);
    }

    Integer getMinimumPlanMaturityAge(Plan plan) {
        List<Integer> maturityAges = new ArrayList<Integer>();
        maturityAges.addAll(plan.getAllowedPolicyTerm());
        Collections.sort(maturityAges);
        return maturityAges.get(0);
    }


    boolean isGreaterThanMinPlanMaturityAge(Set<CommissionTerm> commissionTerms, Integer maxMaturity) {
        List<CommissionTerm> commissionTermsGreaterThanMaximumMaturityAge = commissionTerms.stream().filter(new CommissionTermsGreaterThanMinimumMaturityAge(commissionTerms, maxMaturity)).collect(Collectors.toList());
        return UtilValidator.isEmpty(commissionTermsGreaterThanMaximumMaturityAge);
    }

    boolean isLesserThanMaxPlanMaturityAge(Set<CommissionTerm> commissionTerms, Integer maxMaturity) {
        List<CommissionTerm> commissionTermsGreaterThanMaximumMaturityAge = commissionTerms.stream().filter(new CommissionTermsLesserThanMaximumMaturityAge(commissionTerms, maxMaturity)).collect(Collectors.toList());
        return UtilValidator.isEmpty(commissionTermsGreaterThanMaximumMaturityAge);
    }


    private class CommissionTermsGreaterThanMinimumMaturityAge implements Predicate<CommissionTerm> {

        private Set<CommissionTerm> commissionTerms;
        private Integer minMaturity;

        private CommissionTermsGreaterThanMinimumMaturityAge(Set<CommissionTerm> commissionTerms, Integer minMaturity) {
            this.commissionTerms = commissionTerms;
            this.minMaturity = minMaturity;
        }

        @Override
        public boolean test(CommissionTerm commissionTerm) {
            List<CommissionTerm> commissionTermsGreaterThanMinimumMaturityAge = commissionTerms.stream().filter(new FilterCommissionTermsGreaterThanMinimumMaturityAge(commissionTerm, this.minMaturity)).collect(Collectors.toList());
            return UtilValidator.isEmpty(commissionTermsGreaterThanMinimumMaturityAge);
        }
    }

    private class FilterCommissionTermsGreaterThanMinimumMaturityAge implements Predicate<CommissionTerm> {

        private Integer minMaturity;
        private CommissionTerm currentCommissionTerm;

        private FilterCommissionTermsGreaterThanMinimumMaturityAge(CommissionTerm currentCommissionTerm, Integer minMaturity) {
            this.minMaturity = minMaturity;
            this.currentCommissionTerm = currentCommissionTerm;
        }

        @Override
        public boolean test(CommissionTerm otherCommissionTerm) {
            return (currentCommissionTerm.getStartYear() >= minMaturity);
        }
    }

    private class CommissionTermsLesserThanMaximumMaturityAge implements Predicate<CommissionTerm> {

        private Set<CommissionTerm> commissionTerms;
        private Integer maxMaturity;

        private CommissionTermsLesserThanMaximumMaturityAge(Set<CommissionTerm> commissionTerms, Integer maxMaturity) {
            this.commissionTerms = commissionTerms;
            this.maxMaturity = maxMaturity;
        }

        @Override
        public boolean test(CommissionTerm commissionTerm) {
            List<CommissionTerm> commissionTermsGreaterThanMaximumMaturityAge = commissionTerms.stream().filter(new FilterCommissionTermsLesserThanMaximumMaturityAge(commissionTerm, maxMaturity)).collect(Collectors.toList());
            return UtilValidator.isEmpty(commissionTermsGreaterThanMaximumMaturityAge);
        }
    }

    private class FilterCommissionTermsLesserThanMaximumMaturityAge implements Predicate<CommissionTerm> {

        private Integer maxMaturity;
        private CommissionTerm currentCommissionTerm;

        private FilterCommissionTermsLesserThanMaximumMaturityAge(CommissionTerm currentCommissionTerm, Integer maxMaturity) {
            this.maxMaturity = maxMaturity;
            this.currentCommissionTerm = currentCommissionTerm;
        }

        @Override
        public boolean test(CommissionTerm otherCommissionTerm) {
            return (currentCommissionTerm.getEndYear() <= maxMaturity);
        }
    }

    private class CommissionTermOverlapCheckPredicate implements Predicate<CommissionTerm> {

        private Set<CommissionTerm> commissionTerms;

        private CommissionTermOverlapCheckPredicate(Set<CommissionTerm> commissionTerms) {
            this.commissionTerms = commissionTerms;
        }

        @Override
        public boolean test(CommissionTerm commissionTerm) {
            List<CommissionTerm> overlappedCommissionTerms = commissionTerms.stream().filter(new FilterNotOverlappingCommissionTermPredicate(commissionTerm)).collect(Collectors.toList());
            return UtilValidator.isNotEmpty(overlappedCommissionTerms);
        }
    }

    private class FilterNotOverlappingCommissionTermPredicate implements Predicate<CommissionTerm> {

        private CommissionTerm currentCommissionTerm;

        private FilterNotOverlappingCommissionTermPredicate(CommissionTerm currentCommissionTerm) {
            this.currentCommissionTerm = currentCommissionTerm;
        }

        @Override
        public boolean test(CommissionTerm otherCommissionTerm) {
            return !(currentCommissionTerm.getStartYear() < otherCommissionTerm.getStartYear() && currentCommissionTerm.getEndYear() < otherCommissionTerm.getStartYear()
                    || currentCommissionTerm.getStartYear() > otherCommissionTerm.getEndYear() && currentCommissionTerm.getEndYear() > otherCommissionTerm.getEndYear());
        }
    }
}



