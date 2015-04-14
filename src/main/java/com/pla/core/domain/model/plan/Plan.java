package com.pla.core.domain.model.plan;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.pla.core.domain.event.PlanCreatedEvent;
import com.pla.core.domain.event.PlanDeletedEvent;
import com.pla.sharedkernel.domain.model.CoverageTermType;
import com.pla.sharedkernel.domain.model.PolicyTermType;
import com.pla.sharedkernel.domain.model.PremiumTermType;
import com.pla.sharedkernel.domain.model.SumAssuredType;
import com.pla.sharedkernel.identifier.CoverageId;
import com.pla.sharedkernel.identifier.PlanId;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.axonframework.domain.AbstractAggregateRoot;
import org.axonframework.domain.DomainEventMessage;
import org.axonframework.eventsourcing.EventSourcedEntity;
import org.axonframework.eventsourcing.annotation.AggregateIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author: pradyumna
 * @since 1.0 11/03/2015
 */

@Document(collection = "PLAN")
@Getter
@ToString(exclude = {"logger"})
@EqualsAndHashCode(exclude = {"logger", "specification"}, callSuper = false, doNotUseGetters = true)
public class Plan extends AbstractAggregateRoot<PlanId> {

    private static final Logger logger = LoggerFactory.getLogger(Plan.class);

    @Id
    @AggregateIdentifier
    @JsonSerialize(using = ToStringSerializer.class)
    private PlanId planId;
    private PlanDetail planDetail;
    @Transient
    @JsonIgnore
    private PlanSpecification specification = new PlanSpecification();
    private SumAssured sumAssured;
    private PolicyTermType policyTermType;
    private PremiumTermType premiumTermType;
    private Term premiumTerm;
    private Term policyTerm;
    private Set<PlanCoverage> coverages = new HashSet<PlanCoverage>();

    Plan() {

    }

    Plan(PlanId planId, PlanBuilder planBuilder) {

        this.planId = planId;
        Preconditions.checkArgument(planBuilder != null);
        this.planDetail = planBuilder.getPlanDetail();
        this.sumAssured = planBuilder.getSumAssured();
        this.policyTermType = planBuilder.getPolicyTermType();
        this.premiumTermType = planBuilder.getPremiumTermType();
        this.premiumTerm = planBuilder.getPremiumTerm();
        this.policyTerm = planBuilder.getPolicyTerm();
        this.coverages = planBuilder.getCoverages();
        Preconditions.checkState(specification.isSatisfiedBy(this));

        Collection<Term> allTerms = new LinkedList<>();
        if (this.premiumTermType == PremiumTermType.SPECIFIED_VALUES)
            allTerms.add(this.premiumTerm);
        this.coverages.forEach(planCoverage -> {
            Term term = planCoverage.getCoverageTerm();
            if (term != null) allTerms.add(term);

        });
        Preconditions.checkState(specification.checkCoverageTerm(this, allTerms));
        super.registerEvent(new PlanCreatedEvent(planId));
    }

    public static PlanBuilder builder() {
        return new PlanBuilder();
    }

    //@Override
    protected Iterable<? extends EventSourcedEntity> getChildEntities() {
        return null;
    }

    //@Override
    protected void handle(DomainEventMessage event) {

    }


    public Set<Integer> getAllowedPolicyTerm() {
        if (PolicyTermType.SPECIFIED_VALUES.equals(this.policyTermType)) {
            return this.policyTerm.getValidTerms();
        }
        return this.policyTerm.getMaturityAges();
    }


    public Set<Integer> getAllowedCoverageTerm(CoverageId coverageId) {
        PlanCoverage planCoverage = getPlanCoverageFor(coverageId);
        if (CoverageTermType.POLICY_TERM.equals(planCoverage.getCoverageTermType())) {
            return getAllowedPolicyTerm();
        }
        return planCoverage.getAllowedCoverageTerm();
    }


    public boolean isValidPolicyTerm(Integer policyTerm) {
        Set<Integer> policyTerms = getAllowedPolicyTerm();
        return policyTerms.contains(policyTerm);
    }


    public boolean isValidCoverageTerm(Integer coverageTerm, CoverageId coverageId) {
        Set<Integer> coverageTerms = getAllowedCoverageTerm(coverageId);
        return coverageTerms.contains(coverageTerm);
    }


    public Set<Integer> getAllowedPremiumTerms() {
        if (PremiumTermType.SPECIFIED_VALUES.equals(this.premiumTermType)) {
            return this.premiumTerm.getValidTerms();
        } else if (PremiumTermType.SPECIFIED_AGES.equals(this.premiumTermType)) {
            return this.premiumTerm.getMaturityAges();
        } else if (PremiumTermType.REGULAR.equals(this.premiumTermType)) {
            return getAllowedPolicyTerm();
        }
        return Sets.newHashSet(1);
    }


    public boolean isValidPremiumTerm(Integer premiumTerm) {
        Set<Integer> premiumTerms = getAllowedPremiumTerms();
        return premiumTerms.contains(premiumTerm);
    }


    public List<BigDecimal> getAllowedCoverageSumAssuredValues(CoverageId coverageId) {
        PlanCoverage planCoverage = getPlanCoverageFor(coverageId);
        return planCoverage.getAllowedCoverageSumAssuredValues();
    }


    public List<BigDecimal> getAllowedSumAssuredValues() {
        List<BigDecimal> allowedValues = Lists.newArrayList();
        if (SumAssuredType.SPECIFIED_VALUES.equals(this.sumAssured.getSumAssuredType())) {
            allowedValues.addAll(this.sumAssured.getSumAssuredValue());
            return allowedValues;
        } else if (SumAssuredType.RANGE.equals(this.sumAssured.getSumAssuredType())) {
            BigDecimal minimumSumAssuredValue = this.sumAssured.getMinSumInsured();
            BigDecimal maximumSumAssuredValue = this.sumAssured.getMaxSumInsured();
            while (minimumSumAssuredValue.compareTo(maximumSumAssuredValue) == -1) {
                allowedValues.add(minimumSumAssuredValue);
                minimumSumAssuredValue = minimumSumAssuredValue.add(new BigDecimal(this.sumAssured.getMultiplesOf()));
            }
            allowedValues.add(this.sumAssured.getMaxSumInsured());
        }
        return allowedValues;
    }


    public boolean isValidSumAssured(BigDecimal sumAssured) {
        List<BigDecimal> sumAssuredValues = getAllowedSumAssuredValues();
        return sumAssuredValues.contains(sumAssured);
    }


    public boolean isValidCoverageSumAssured(BigDecimal sumAssured, CoverageId coverageId) {
        List<BigDecimal> coverageSumAssuredValues = getAllowedCoverageSumAssuredValues(coverageId);
        return coverageSumAssuredValues.contains(sumAssured);
    }


    public int getMaximumMaturityAge() {
        int maximumMaturityAge = 0;
        if (PolicyTermType.SPECIFIED_VALUES.equals(this.policyTermType)) {
            maximumMaturityAge = this.policyTerm.getMaxMaturityAge();
        } else if (PolicyTermType.MATURITY_AGE_DEPENDENT.equals(this.policyTermType)) {
            List<Integer> maturityAges = new ArrayList<Integer>();
            maturityAges.addAll(this.policyTerm.getMaturityAges());
            Collections.sort(maturityAges);
            maximumMaturityAge = maturityAges.get(maturityAges.size() - 1);
        }
        return maximumMaturityAge;
    }


    public List<Integer> getAllowedAges() {
        List<Integer> allowedAges = new ArrayList<>();
        int maxAge = getMaximumMaturityAge();
        int minAge = this.getPlanDetail().minEntryAge;
        while (minAge <= maxAge) {
            allowedAges.add(minAge);
            minAge = minAge + 1;
        }
        return allowedAges;
    }


    public List<Integer> getAllowedCoverageAges(CoverageId coverageId) {
        PlanCoverage planCoverage = getPlanCoverageFor(coverageId);
        return planCoverage.getAllowedAges();
    }


    public boolean isValidAge(Integer age) {
        List<Integer> validAges = getAllowedAges();
        return validAges.contains(age);
    }


    public boolean isValidCoverageAge(Integer age, CoverageId coverageId) {
        List<Integer> validAges = getAllowedCoverageAges(coverageId);
        return validAges.contains(age);
    }

    PlanCoverage getPlanCoverageFor(CoverageId coverageId) {
        List<PlanCoverage> planCoverages = this.coverages.stream().filter(new Predicate<PlanCoverage>() {
            @Override
            public boolean test(PlanCoverage planCoverage) {
                return planCoverage.getCoverageId().equals(coverageId);
            }
        }).collect(Collectors.toList());
        return planCoverages.get(0);
    }

    @Override
    public PlanId getIdentifier() {
        return planId;
    }

    public void delete() {
        super.markDeleted();
        super.registerEvent(new PlanDeletedEvent(this.planId));
    }
}
