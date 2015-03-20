package com.pla.core.domain.model.plan;

import com.google.common.collect.Sets;
import com.pla.core.domain.event.PlanEventListener;
import com.pla.sharedkernel.domain.model.ClientType;
import com.pla.sharedkernel.domain.model.PlanType;
import com.pla.sharedkernel.domain.model.Relationship;
import com.pla.sharedkernel.identifier.CoverageId;
import com.pla.sharedkernel.identifier.LineOfBusinessId;
import com.pla.sharedkernel.identifier.PlanId;
import org.axonframework.repository.Repository;
import org.axonframework.unitofwork.DefaultUnitOfWork;
import org.axonframework.unitofwork.SpringTransactionManager;
import org.axonframework.unitofwork.UnitOfWork;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * @author: pradyumna
 * @since 1.0 18/03/2015
 */
/*
,
        "classpath:queryTestContext.xml"
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/META-INF/spring/db-context.xml",
        "classpath:/META-INF/spring/eventstore-jpa-test-context.xml"})
public class PlanEventStoreTest {
    LocalDate launchDate = LocalDate.now().plusDays(10);
    PlanId planId = new PlanId(UUID.randomUUID().toString());
    Set<Relationship> relationshipSet = new HashSet<>(Arrays.asList(Relationship.BROTHER, Relationship.DAUGHTER));
    Set<EndorsementType> endorsementTypes = new HashSet<>(Arrays.asList(EndorsementType.ADDRESS, EndorsementType.NAME));
    LocalDate withdrawalDate = LocalDate.now().plusDays(30);

    @Autowired
    private PlatformTransactionManager txManager;

    @Autowired
    private PlanEventListener planEventListener;

    @Autowired
    @Qualifier(("planRepository"))
    private Repository<Plan> repository;

    private PlanDetail planDetail;
    private PlanCoverage planCoverage;
    private CoverageId coverageId = new CoverageId("1");

    @Before
    public void setUp() {
        PlanDetail.PlanDetailBuilder builder = PlanDetail.builder();

        planDetail = builder.withPlanName("Plan 1")
                .withPlanCode("0001900")
                .withLaunchDate(launchDate)
                .withWithdrawalDate(withdrawalDate)
                .withMinEntryAge(21)
                .withMaxEntryAge(60)
                .withFreeLookPeriod(15)
                .withSurrenderAfter(5)
                .withClientType(ClientType.INDIVIDUAL)
                .withLineOfBusinessId(LineOfBusinessId.INDIVIDUAL_INSURANCE)
                .withPlanType(PlanType.NON_INVESTMENT)
                .withApplicableRelationships(relationshipSet)
                .withEndorsementTypes(endorsementTypes)
                .withTaxApplicable(false)
                .build();

        PlanCoverageBuilder planCoverageBuilder = PlanCoverage.builder();
        planCoverage = planCoverageBuilder.withCoverage(coverageId)
                .withCoverageCover(CoverageCover.ACCELERATED)
                .withTaxApplicable(false)
                .withCoverageType(CoverageType.BASE)
                .withMinAndMaxAge(21, 45)
                .withDeductibleAsPercentage(new BigDecimal(100))
                .withWaitingPeriod(5)
                .build();

    }

    @Test
    public void testPersistence() {
        PlanBuilder builder = Plan.builder();
        Plan plan = builder.withPlanId(planId)
                .withPlanDetail(planDetail)
                .withSumAssuredBasedOnValue(Sets.newHashSet(new BigDecimal(10000000), new BigDecimal(50000000), new BigDecimal(50000000)))
                .withPaymentTermBasedOnValue(Sets.newHashSet(30, 35, 40, 45, 50, 55, 60))
                .withCoverages(Sets.newHashSet(planCoverage))
                .withMaturityAmount(5, new BigDecimal(15))
                .withPolicyTermBasedOnValue(Sets.newHashSet(30, 35, 40, 45, 50, 55, 60), 60)
                .build();
        UnitOfWork uw = DefaultUnitOfWork.startAndGet(new SpringTransactionManager(txManager));
        repository.add(plan);
        uw.commit();

        System.out.println("********** SAVED*********");


        /*uw = DefaultUnitOfWork.startAndGet(new SpringTransactionManager(txManager));
        Plan savedPlan = repository.load(planId);
        System.out.println("********** LOADED *********");

        PlanCoverageBuilder planCoverageBuilder = PlanCoverage.builder();

        PlanCoverage planCoverage = planCoverageBuilder.withCoverage(new CoverageId("200"))
                .withCoverageCover(CoverageCover.ACCELERATED)
                .withTaxApplicable(false)
                .withCoverageType(CoverageType.BASE)
                .withMinAndMaxAge(45, 65)
                .withDeductibleAsPercentage(new BigDecimal(55))
                .withWaitingPeriod(10)
                .build();

        planDetail = PlanDetail.builder().withPlanName("Plan 1")
                .withPlanCode("0001900")
                .withLaunchDate(launchDate)
                .withWithdrawalDate(withdrawalDate)
                .withMinEntryAge(35)
                .withMaxEntryAge(65)
                .withFreeLookPeriod(15)
                .withSurrenderAfter(5)
                .withClientType(ClientType.INDIVIDUAL)
                .withLineOfBusinessId(LineOfBusinessId.INDIVIDUAL_INSURANCE)
                .withPlanType(PlanType.NON_INVESTMENT)
                .withApplicableRelationships(relationshipSet)
                .withEndorsementTypes(endorsementTypes)
                .withTaxApplicable(false)
                .build();

        savedPlan.updatePlanDetail(planDetail);
        PolicyTerm policyTerm = new PolicyTerm(65);
        savedPlan.updatePolicyTerm(policyTerm);
        savedPlan.updatePlanPayment(new PlanPayment(new PremiumPayment(65)));
        savedPlan.updateSumAssured(new SumAssuredByRange(new BigDecimal(1000000), new BigDecimal(50000000), 10000));
        savedPlan.updatePlanCoverages(Sets.newHashSet(planCoverage));
        uw.commit();
        System.out.println("**********UPDATED*********");*/
    }

}
