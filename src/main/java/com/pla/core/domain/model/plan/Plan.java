package com.pla.core.domain.model.plan;

import com.pla.sharedkernel.identifier.PlanId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.ToString;
import org.axonframework.eventsourcing.annotation.AbstractAnnotatedAggregateRoot;
import org.nthdimenzion.utils.UtilValidator;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

/**
 * @author: pradyumna
 * @since 1.0 11/03/2015
 */
@ToString
@Getter(AccessLevel.PACKAGE)
@Entity
public class Plan extends AbstractAnnotatedAggregateRoot<PlanId> {

    @Id
    private PlanId planId;

    @Transient
    private PlanDetail planDetail;
    @OneToOne
    private SumAssured sumAssured;
    /**
     * Policy term can be a list of age with upper band
     * of maximum maturity age OR it could be list of age
     * of the insured.
     */
    @OneToOne
    private PolicyTerm policyTerm;
    @OneToOne
    private PlanPayment planPayment;

    @OneToMany(mappedBy = "plan")
    private Set<PlanCoverage> coverages = new HashSet<PlanCoverage>();

    protected Plan() {
    }

    Plan(PlanBuilder builder) {
        checkArgument(builder.planId != null);
        this.planId = builder.planId;

        checkArgument(UtilValidator.isNotEmpty(builder.coverages));
        for (PlanCoverage planCoverage : builder.coverages) {
            this.getCoverages().add(new PlanCoverage(this, planCoverage));
        }

        checkArgument(builder.planPayment != null);
        this.planPayment = builder.planPayment;

        builder.maturityAmountSet.forEach(amt -> this.planPayment.addMaturityAmount(amt));

        checkArgument(builder.policyTerm != null);
        this.policyTerm = builder.policyTerm;

        checkArgument(builder.planDetail != null);
        this.planDetail = builder.planDetail;

        checkArgument(builder.sumAssured != null);
        this.sumAssured = builder.sumAssured;

        PlanSpecification specification = new PlanSpecification();
        checkState(specification.isSatisfiedBy(this), " Premium Payment Term is greater than the Policy Term.");
    }

    public static PlanBuilder builder() {
        return new PlanBuilder();
    }

    public PlanId getIdentifier() {
        return planId;
    }

}
