package com.pla.core.domain.model.plan;

import com.google.common.collect.Sets;
import com.pla.core.domain.query.PlanCoverageCompositeKey;
import com.pla.core.domain.query.PlanCoverageEntity;
import com.pla.core.specification.PlanCodeSpecification;
import com.pla.sharedkernel.domain.model.*;
import com.pla.sharedkernel.identifier.CoverageId;
import com.pla.sharedkernel.identifier.LineOfBusinessId;
import com.pla.sharedkernel.identifier.PlanId;
import org.axonframework.repository.Repository;
import org.axonframework.unitofwork.DefaultUnitOfWork;
import org.axonframework.unitofwork.SpringTransactionManager;
import org.axonframework.unitofwork.UnitOfWork;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * @author: pradyumna
 * @since 1.0 18/03/2015
 */
/*
,
        "classpath:queryTestContext.xml"
 */
@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "classpath:/META-INF/spring/db-context.xml",
        "classpath:/META-INF/spring/eventstore-jpa-test-context.xml"})
//@Transactional
public class PlanEventStoreTest {
    LocalDate launchDate = LocalDate.now().plusDays(10);
    PlanId planId = new PlanId(UUID.randomUUID().toString());
    Set<Relationship> relationshipSet = new HashSet<>(Arrays.asList(Relationship.BROTHER, Relationship.DAUGHTER));
    Set<EndorsementType> endorsementTypes = new HashSet<>(Arrays.asList(EndorsementType.ADDRESS, EndorsementType.NAME));
    LocalDate withdrawalDate = LocalDate.now().plusDays(30);
    @PersistenceContext
    EntityManager entityManager;
    @Autowired
    @Qualifier(("planRepository"))
    private Repository<Plan> repository;
    @Autowired
    private PlatformTransactionManager txManager;
    @Autowired
    private PlanCodeSpecification planCodeSpecification;

    private PlanDetail planDetail;
    private PlanCoverage planCoverage;
    private CoverageId coverageId = new CoverageId("1");

    @Before
    public void setUp() {
        PlanDetailBuilder builder = PlanDetail.builder();

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
    public void should_persist_plan_with_plan_coverage_and_sum_assured() {
        SimpleJpaRepository planCoverageRepository = new SimpleJpaRepository<PlanCoverageEntity,
                PlanCoverageCompositeKey>
                (PlanCoverageEntity.class, entityManager);

        Plan plan = new Plan(planId, planDetail);
        plan.configureCoverages(Sets.newHashSet(planCoverage));
        plan.configureSumAssuredForPlanCoverage(coverageId,
                SumAssuredType.RANGE, new BigDecimal(100000), new BigDecimal(500000), 1000, null, 0);
        UnitOfWork uw = DefaultUnitOfWork.startAndGet(new SpringTransactionManager(txManager));
        repository.add(plan);
        uw.commit();
        PlanCoverageEntity entry =
                (PlanCoverageEntity) planCoverageRepository.findOne(new PlanCoverageCompositeKey(planId.toString(), coverageId.toString()));
        assertEquals(entry.getSumAssuredType(), SumAssuredType.RANGE);
        assertEquals("Min Sum Assured Mismatch", entry.getMinSumAssured().compareTo(new BigDecimal(100000)), 0);
        assertEquals("Max Sum Assured Mismatch", entry.getMaxSumAssured().compareTo(new BigDecimal(500000)), 0);
        assertEquals("MultipleOf mismatch", entry.getMultiplesOf(), 1000);

        //Change Plan Coverage Sum Assured to Derived
        uw = DefaultUnitOfWork.startAndGet(new SpringTransactionManager(txManager));
        Plan savedPlan = repository.load(planId);
        savedPlan.configureSumAssuredForPlanCoverage(coverageId,
                SumAssuredType.DERIVED, null, new BigDecimal(500000), 0, null, 12);
        uw.commit();
        entry =
                (PlanCoverageEntity) planCoverageRepository.findOne(new PlanCoverageCompositeKey(planId.toString(), coverageId.toString()));
        assertEquals(entry.getSumAssuredType(), SumAssuredType.DERIVED);
        assertEquals("Max Sum Assured Mismatch", entry.getMaxSumAssured().compareTo(new BigDecimal(500000)), 0);
        assertEquals("Percentage mismatch", entry.getPercentage(), 12);

        //Change Plan Coverage Sum Assured to Specified Values
        uw = DefaultUnitOfWork.startAndGet(new SpringTransactionManager(txManager));
        savedPlan = repository.load(planId);
        Set<BigDecimal> listOfValues = Sets.newHashSet(new BigDecimal(100000), new BigDecimal(500000));
        savedPlan.configureSumAssuredForPlanCoverage(coverageId,
                SumAssuredType.SPECIFIED_VALUES, null, new BigDecimal(500000), 0, listOfValues, 12);
        uw.commit();
        entry =
                (PlanCoverageEntity) planCoverageRepository.findOne(new PlanCoverageCompositeKey(planId.toString(), coverageId.toString()));
        assertEquals(entry.getSumAssuredType(), SumAssuredType.SPECIFIED_VALUES);
        assertEquals("Mismatch in Values", new TreeSet(entry.getSumAssured()), new TreeSet(listOfValues));

    }

    @Test
    public void testPersistence() {

        Plan plan = new Plan(planId, planDetail);

        plan.configureSumAssured(SumAssuredType.SPECIFIED_VALUES, null, new BigDecimal(50000000), 0,
                Sets.newHashSet(new BigDecimal(10000000),
                        new BigDecimal(50000000), new BigDecimal(50000000)), 0);
        plan.configurePremiumPayment(PremiumTermType.SPECIFIED_VALUES,
                Sets.newHashSet(30, 35, 40, 45, 50, 55, 60), 60);
        plan.configureCoverages(Sets.newHashSet(planCoverage));
        plan.configureMaturityAmount(Sets.newHashSet(new MaturityAmount(5, new BigDecimal(15000))));
        plan.configurePolicyTerm(PolicyTermType.SPECIFIED_VALUES, Sets.newHashSet(30, 35, 40, 45, 50, 55, 60), 60);
        UnitOfWork uw = DefaultUnitOfWork.startAndGet(new SpringTransactionManager(txManager));
        repository.add(plan);
        uw.commit();


        System.out.println("********** SAVED*********");


        uw = DefaultUnitOfWork.startAndGet(new SpringTransactionManager(txManager));
        Plan savedPlan = repository.load(planId);
        System.out.println("********** LOADED *********");
        assertEquals(plan, savedPlan);
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
        savedPlan.configurePolicyTerm(PolicyTermType.MATURITY_AGE_DEPENDENT, Sets.newHashSet(45, 50, 55), -1);
        savedPlan.configurePremiumPayment(PremiumTermType.SPECIFIED_AGES,
                Sets.newHashSet(65), -1);

        PlanCoverageBuilder planCoverageBuilder = PlanCoverage.builder();
        PlanCoverage planCoverage = planCoverageBuilder.withCoverage(new CoverageId("200"))
                .withCoverageCover(CoverageCover.ACCELERATED)
                .withTaxApplicable(false)
                .withCoverageType(CoverageType.BASE)
                .withMinAndMaxAge(45, 65)
                .withDeductibleAsPercentage(new BigDecimal(55))
                .withWaitingPeriod(10)
                .build();
        savedPlan.configureCoverages(Sets.newHashSet(planCoverage));
        savedPlan.configureTermForPlanCoverage(new CoverageId("200"), CoverageTermType.POLICY_TERM, null, 0);
        assertEquals(plan.getPlanDetail().getPolicyTerm().validTerms, Sets.newHashSet(30, 35, 40, 45, 50, 55, 60));
        savedPlan.configureSumAssuredForPlanCoverage(new CoverageId("200"), SumAssuredType.RANGE, new BigDecimal(100000), new BigDecimal(500000), 1000, null, 0);
        savedPlan.configureSumAssured(SumAssuredType.RANGE, new BigDecimal(1000000), new BigDecimal(50000000), 10000, null, 0);
        uw.commit();
        System.out.println("**********UPDATED*********");
    }

    @Test
    public void should_not_create_plan_with_same_plan_code() {
        Plan plan = new Plan(new PlanId(), planDetail);
        boolean isSatisfied = planCodeSpecification.isSatisfiedBy(new PlanId().toString(), "0001900");
        assertFalse(isSatisfied);
    }


}
