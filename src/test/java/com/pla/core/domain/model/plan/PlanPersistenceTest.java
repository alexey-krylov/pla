package com.pla.core.domain.model.plan;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;
import com.google.common.collect.Sets;
import com.pla.sharedkernel.domain.model.ClientType;
import com.pla.sharedkernel.domain.model.PlanType;
import com.pla.sharedkernel.domain.model.Relationship;
import com.pla.sharedkernel.identifier.CoverageId;
import com.pla.sharedkernel.identifier.LineOfBusinessId;
import com.pla.sharedkernel.identifier.PlanId;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author: pradyumna
 * @since 1.0 17/03/2015
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:queryTestContext.xml")
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class})
public class PlanPersistenceTest {

    @Autowired
    private EntityManagerFactory entityManagerFactory;


    @Test
    @ExpectedDatabase(value = "classpath:testdata/endtoend/plan/expectedplan_1.xml", assertionMode = DatabaseAssertionMode.NON_STRICT)
    public void savePlan() {
        PlanBuilder builder = Plan.builder();
        LocalDate launchDate = LocalDate.now().plusDays(10);
        LocalDate withdrawalDate = LocalDate.now().plusDays(30);
        Set<Relationship> relationshipSet = new HashSet<>(Arrays.asList(Relationship.BROTHER, Relationship.DAUGHTER));
        Set<EndorsementType> endorsementTypes = new HashSet<>(Arrays.asList(EndorsementType.ADDRESS, EndorsementType.NAME));
        PlanDetail.PlanDetailBuilder builder2 = PlanDetail.builder();

        PlanCoverageBuilder planCoverageBuilder = PlanCoverage.builder();
        PlanCoverage planCoverage = planCoverageBuilder.withCoverage(new CoverageId("111111"))
                .withCoverageCover(CoverageCover.ACCELERATED)
                .withTaxApplicable(false)
                .withCoverageType(CoverageType.BASE)
                .withMinAndMaxAge(21, 45)
                .withDeductibleAsPercentage(new BigDecimal(100))
                .withWaitingPeriod(5)
                .build();


        PlanDetail planDetail = builder2.withPlanName("Plan 1")
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
        Plan plan = builder.withPlanId(new PlanId("100"))
                .withPlanDetail(planDetail)
                .withSumAssuredByRange(new BigDecimal(10000000), new BigDecimal(40000000), 1000)
                .withPaymentTermBasedOnValue(Sets.newHashSet(30, 35, 40, 45, 50, 55, 60))
                .withCoverages(Sets.newHashSet(planCoverage))
                .withMaturityAmount(5, new BigDecimal(15))
                .withPolicyTermBasedOnValue(Sets.newHashSet(30, 35, 40, 45, 50, 55, 60), 60)
                .build();

        EntityManager em = entityManagerFactory.createEntityManager();
        EntityTransaction txn = em.getTransaction();
        txn.begin();
        em.persist(plan);
        txn.commit();
    }
}
