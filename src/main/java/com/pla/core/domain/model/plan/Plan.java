package com.pla.core.domain.model.plan;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.pla.sharedkernel.domain.model.CoverageTermType;
import com.pla.sharedkernel.domain.model.PolicyTermType;
import com.pla.sharedkernel.domain.model.PremiumTermType;
import com.pla.sharedkernel.domain.model.SumAssuredType;
import com.pla.sharedkernel.identifier.CoverageId;
import com.pla.sharedkernel.identifier.PlanId;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.axonframework.domain.DomainEventMessage;
import org.axonframework.eventsourcing.EventSourcedEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import javax.persistence.Transient;
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
@EqualsAndHashCode(exclude = {"logger", "specification"}, callSuper = false)
public class Plan {

    private static final Logger logger = LoggerFactory.getLogger(Plan.class);

    @Id
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

    //@Override
    protected Iterable<? extends EventSourcedEntity> getChildEntities() {
        return null;
    }

    //@Override
    protected void handle(DomainEventMessage event) {

    }

    Plan(PlanId planId, PlanBuilder planBuilder) {

        this.planId = planId;
        Preconditions.checkArgument(planBuilder != null);
        this.planDetail = planBuilder.getPlanDetail();
        this.sumAssured = planBuilder.getSumAssured();
        this.policyTermType = planBuilder.getPolicyTermType();
        this.premiumTermType = planBuilder.getPremiumTermType();
        this.premiumTerm = planBuilder.getPolicyTerm();
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
    }

    public static PlanBuilder builder() {
        return new PlanBuilder();
    }


    // TODO : Write test
    public Set<Integer> getAllowedPolicyTerm() {
        if (PolicyTermType.SPECIFIED_VALUES.equals(this.policyTermType)) {
            return this.policyTerm.getValidTerms();
        }
        return this.policyTerm.getMaturityAges();
    }

    // TODO : Write test
    public Set<Integer> getAllowedCoverageTerm(CoverageId coverageId) {
        PlanCoverage planCoverage = getPlanCoverageFor(coverageId);
        if (CoverageTermType.POLICY_TERM.equals(planCoverage.getCoverageTermType())) {
            return getAllowedPolicyTerm();
        }
        return planCoverage.getAllowedCoverageTerm();
    }

    // TODO : Write test
    public boolean isValidPolicyTerm(Integer policyTerm) {
        Set<Integer> policyTerms = getAllowedPolicyTerm();
        return policyTerms.contains(policyTerm);
    }

    // TODO : Write test
    public boolean isValidCoverageTerm(Integer coverageTerm, CoverageId coverageId) {
        Set<Integer> coverageTerms = getAllowedCoverageTerm(coverageId);
        return coverageTerms.contains(coverageTerm);
    }

    // TODO : Write test
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

    // TODO : Write test
    public boolean isValidPremiumTerm(Integer premiumTerm) {
        Set<Integer> premiumTerms = getAllowedPremiumTerms();
        return premiumTerms.contains(premiumTerm);
    }

    // TODO : Write test
    public List<BigDecimal> getAllowedCoverageSumAssuredValues(CoverageId coverageId) {
        PlanCoverage planCoverage = getPlanCoverageFor(coverageId);
        return planCoverage.getAllowedCoverageSumAssuredValues();
    }

    // TODO : Write test
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

    // TODO : Write test
    public boolean isValidSumAssured(BigDecimal sumAssured) {
        List<BigDecimal> sumAssuredValues = getAllowedSumAssuredValues();
        return sumAssuredValues.contains(sumAssured);
    }

    // TODO : Write test
    public boolean isValidCoverageSumAssured(BigDecimal sumAssured, CoverageId coverageId) {
        List<BigDecimal> coverageSumAssuredValues = getAllowedCoverageSumAssuredValues(coverageId);
        return coverageSumAssuredValues.contains(sumAssured);
    }
    // TODO : Write test
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

    // TODO : Write test
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

    // TODO : Write test
    public List<Integer> getAllowedCoverageAges(CoverageId coverageId) {
        PlanCoverage planCoverage = getPlanCoverageFor(coverageId);
        return planCoverage.getAllowedAges();
    }

    // TODO : Write test
    public boolean isValidAge(Integer age) {
        List<Integer> validAges = getAllowedAges();
        return validAges.contains(age);
    }
    // TODO : Write test
    public boolean isValidCoverageAge(Integer age, CoverageId coverageId) {
        List<Integer> validAges = getAllowedCoverageAges(coverageId);
        return validAges.contains(age);
    }

    // TODO : Write test
    private PlanCoverage getPlanCoverageFor(CoverageId coverageId) {
        List<PlanCoverage> planCoverages = this.coverages.stream().filter(new Predicate<PlanCoverage>() {
            @Override
            public boolean test(PlanCoverage planCoverage) {
                return planCoverage.getCoverageId().equals(coverageId);
            }
        }).collect(Collectors.toList());
        return planCoverages.get(0);
    }

    //@Override
    public Object getIdentifier() {
        return null;
    }
}
