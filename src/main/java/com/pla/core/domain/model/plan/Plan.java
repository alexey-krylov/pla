package com.pla.core.domain.model.plan;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Preconditions;
import com.pla.sharedkernel.domain.model.PolicyTermType;
import com.pla.sharedkernel.domain.model.PremiumTermType;
import com.pla.sharedkernel.domain.model.SumAssuredType;
import com.pla.sharedkernel.identifier.PlanId;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import javax.persistence.Transient;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

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
    private SumAssuredType sumAssuredType;
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

}