package org.nthdimenzion.mongorepository;

import com.google.common.collect.Sets;
import com.pla.core.domain.model.plan.*;
import com.pla.sharedkernel.domain.model.*;
import com.pla.sharedkernel.identifier.CoverageId;
import com.pla.sharedkernel.identifier.LineOfBusinessId;
import org.axonframework.repository.Repository;
import org.axonframework.unitofwork.DefaultUnitOfWork;
import org.axonframework.unitofwork.SpringTransactionManager;
import org.axonframework.unitofwork.UnitOfWork;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nthdimenzion.application.Application;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.transaction.PlatformTransactionManager;

import javax.transaction.TransactionManager;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by pradyumna on 01-04-2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@TestExecutionListeners(DependencyInjectionTestExecutionListener.class)
public class MongoAggregateTest {

    @Autowired
    private Repository<Plan> planRepository;

    @Test
    public void should_find_plan_repository_bean() {
        Assert.assertNotNull(planRepository);
        Assert.assertNotNull(txManager);
    }

    private Plan createPlan() {
        PlanDetailBuilder builder = PlanDetail.builder();
        LocalDate launchDate = LocalDate.now().plusDays(10);
        LocalDate withdrawalDate = LocalDate.now().plusDays(30);
        Set<Relationship> relationshipSet = new HashSet<>(Arrays.asList(Relationship.BROTHER, Relationship.DAUGHTER));
        Set<EndorsementType> endorsementTypes = new HashSet<>(Arrays.asList(EndorsementType.ADDRESS, EndorsementType.NAME));

        PlanDetail planDetail = builder.withPlanName("Plan 1")
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
        CoverageId coverageId = new CoverageId("1");
        PlanCoverageBuilder planCoverageBuilder = PlanCoverage.builder();
        PlanCoverage planCoverage = planCoverageBuilder.withCoverage(coverageId)
                .withCoverageCover(CoverageCover.ACCELERATED)
                .withTaxApplicable(false)
                .withCoverageType(CoverageType.BASE)
                .withMinAndMaxAge(21, 45)
                .withWaitingPeriod(5)
                .build();
        PlanBuilder planBuilder = Plan.builder();
        planBuilder.withPlanDetail(planDetail);
        planBuilder.withPlanSumAssured(SumAssuredType.SPECIFIED_VALUES, null, new BigDecimal(50000000), 0,
                Sets.newTreeSet(Sets.newHashSet(new BigDecimal(10000000), new BigDecimal(50000000), new BigDecimal(50000000))), 0);
        planBuilder.withPremiumTerm(PremiumTermType.SPECIFIED_AGES, Sets.newHashSet(45, 55), -1);
        planBuilder.withPlanCoverages(Sets.newHashSet(planCoverage));
        planBuilder.withPolicyTerm(PolicyTermType.MATURITY_AGE_DEPENDENT,
                Sets.newHashSet(60, 65), -1);
        Plan plan = planBuilder.build();
        return plan;
    }

    @Autowired
    PlatformTransactionManager txManager;
    @Autowired
    private MongoTemplate springMongoTemplate;

    @Test
    public void should_add_to_repository() {
        UnitOfWork uom = new DefaultUnitOfWork(new SpringTransactionManager(txManager));
        uom.start();
        planRepository.add(createPlan());
        uom.commit();
//        springMongoTemplate.save(createPlan());
    }
}
