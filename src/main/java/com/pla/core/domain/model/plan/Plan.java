package com.pla.core.domain.model.plan;

import com.pla.sharedkernel.identifier.PlanId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.ToString;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.axonframework.eventsourcing.annotation.AbstractAnnotatedAggregateRoot;
import org.axonframework.eventsourcing.annotation.AggregateIdentifier;
import org.nthdimenzion.utils.UtilValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class Plan extends AbstractAnnotatedAggregateRoot<PlanId> {

    private Logger logger = LoggerFactory.getLogger(Plan.class);

    @AggregateIdentifier
    private PlanId planId;
    private PlanDetail planDetail;
    private SumAssured sumAssured;
    /**
     * Policy term can be a list of age with upper band
     * of maximum maturity age OR it could be list of age
     * of the insured.
     */
    private PolicyTerm policyTerm;
    private PlanPayment planPayment;
    private Set<PlanCoverage> coverages = new HashSet<PlanCoverage>();

    @SuppressWarnings("UnusedDeclaration")
    protected Plan() {
    }

    Plan(PlanBuilder builder) {

        checkArgument(builder.planId != null);
        this.planId = builder.planId;
        apply(new PlanConfigured(builder.planId));

        checkArgument(builder.planDetail != null);
        apply(new PlanDetailConfigured(builder.planDetail));

        checkArgument(builder.policyTerm != null);
        apply(new PolicyTermConfigured(builder.policyTerm));

        checkArgument(builder.planPayment != null);
        apply(new PlanPaymentConfigured(builder.planPayment));

        checkArgument(builder.sumAssured != null);
        apply(new SumAssuredConfigured(builder.sumAssured));

        checkArgument(UtilValidator.isNotEmpty(builder.coverages));
        apply(new PlanCoverageConfigured(builder.coverages));

        PlanSpecification specification = new PlanSpecification();
        checkState(specification.isSatisfiedBy(this), " Premium Payment Term is greater than the Policy Term.");
    }

    public static PlanBuilder builder() {
        return new PlanBuilder();
    }

    public void updatePlanDetail(PlanDetail planDetail) {
        checkArgument(policyTerm != null);
        apply(new PlanDetailChanged(planDetail));
    }

    public void updatePolicyTerm(PolicyTerm policyTerm) {
        checkArgument(policyTerm != null);
        apply(new PolicyTermReconfigured(policyTerm));
    }

    public void updatePlanPayment(PlanPayment planPayment) {
        checkArgument(planPayment != null);
        apply(new PlanPaymentReconfigured(planPayment));
    }

    public void updateSumAssured(SumAssured sumAssured) {
        checkArgument(sumAssured != null);
        apply(new SumAssuredReconfigured(sumAssured));
    }

    public void updatePlanCoverages(Set<PlanCoverage> coverages) {
        checkArgument(UtilValidator.isNotEmpty(coverages));
        apply(new PlanCoverageReconfigured(coverages));
    }


    @EventHandler
    protected void onPlanCreated(PlanConfigured event) {
        if (logger.isTraceEnabled()) {
            logger.trace("Plan was created with Plan Id " + event.getPlanId());
        }
        this.planId = event.getPlanId();
    }

    @EventHandler
    protected void onPlanDetailConfigured(PlanDetailConfigured event) {
        if (logger.isTraceEnabled()) {
            logger.trace("Plan Detail configured");
        }
        this.planDetail = event.getPlanDetail();
    }

    @EventHandler
    protected void onPolicyTermConfigured(PolicyTermConfigured event) {
        if (logger.isTraceEnabled()) {
            logger.trace("Policy Term configured");
        }
        this.policyTerm = event.getPolicyTerm();
    }

    @EventHandler
    protected void onPlanPaymentConfigured(PlanPaymentConfigured event) {
        if (logger.isTraceEnabled()) {
            logger.trace("Plan Payment configured");
        }
        this.planPayment = event.getPlanPayment();
    }

    @EventHandler
    protected void onSumAssuredConfigured(SumAssuredConfigured event) {
        if (logger.isTraceEnabled()) {
            logger.trace("Sum Assured configured");
        }
        this.sumAssured = event.getSumAssured();
    }

    @EventHandler
    protected void onPlanCoverageConfigured(PlanCoverageConfigured event) {
        if (logger.isTraceEnabled()) {
            logger.trace("Plan Coverage configured");
        }
        this.coverages = event.getCoverages();
    }

    @EventHandler
    protected void onPlanDetailChanged(PlanDetailChanged event) {
        if (logger.isTraceEnabled()) {
            logger.trace("Plan Detail changed.");
        }
        this.planDetail = event.getPlanDetail();
    }

    @EventHandler
    protected void onPolicyTermReconfigured(PolicyTermReconfigured event) {
        if (logger.isTraceEnabled()) {
            logger.trace("Policy Term re-configured");
        }
        this.policyTerm = event.getPolicyTerm();
    }

    @EventHandler
    protected void onPlanPaymentReconfigured(PlanPaymentReconfigured event) {
        if (logger.isTraceEnabled()) {
            logger.trace("Plan Payment re-configured");
        }
        this.planPayment = event.getPlanPayment();
    }

    @EventHandler
    protected void onSumAssuredReconfigured(SumAssuredReconfigured event) {
        if (logger.isTraceEnabled()) {
            logger.trace("Sum Assured re-configured");
        }
        this.sumAssured = event.getSumAssured();
    }

    @EventHandler
    protected void onPlanCoverageReconfigured(PlanCoverageReconfigured event) {
        if (logger.isTraceEnabled()) {
            logger.trace("Plan Coverage re-configured");
        }
        this.coverages = event.getCoverages();
    }


    public PlanId getIdentifier() {
        return planId;
    }

}
