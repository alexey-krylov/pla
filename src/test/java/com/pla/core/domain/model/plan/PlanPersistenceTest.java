package com.pla.core.domain.model.plan;

import com.google.common.collect.Sets;
import com.lordofthejars.nosqlunit.mongodb.MongoDbRule;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.pla.sharedkernel.domain.model.*;
import com.pla.sharedkernel.identifier.CoverageId;
import com.pla.sharedkernel.identifier.LineOfBusinessId;
import com.pla.sharedkernel.identifier.PlanId;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static com.lordofthejars.nosqlunit.mongodb.MongoDbRule.MongoDbRuleBuilder.newMongoDbRule;

/**
 * @author: pradyumna
 * @since 1.0 23/03/2015
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("mongodb")
@ContextConfiguration(locations = {"classpath:META-INF/spring/persistence-infrastructure-test-context.xml"})
public class PlanPersistenceTest {


    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    MongoTemplate springMongoTemplate;
    PlanId planId;
    private PlanDetail planDetail;
    private PlanCoverage planCoverage;
    private CoverageId coverageId = new CoverageId("1");

    @Rule
    public MongoDbRule mongoDbRule = newMongoDbRule().defaultSpringMongoDb("pla");

    @Before
    public void setup() {
        PlanDetailBuilder builder = PlanDetail.builder();
        LocalDate launchDate = LocalDate.now().plusDays(10);
        LocalDate withdrawalDate = LocalDate.now().plusDays(30);
        Set<Relationship> relationshipSet = new HashSet<>(Arrays.asList(Relationship.BROTHER, Relationship.DAUGHTER));
        Set<EndorsementType> endorsementTypes = new HashSet<>(Arrays.asList(EndorsementType.ADDRESS, EndorsementType.NAME));
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
                .withWaitingPeriod(5)
                .build();

        planId = new PlanId();
    }

    @Test
    //@ShouldMatchDataSet(location = "testdata/endtoend/pla/plan.json")
    public void storePlan() {

        PlanBuilder builder = Plan.builder();
        builder.withPlanDetail(planDetail);
        builder.withPlanSumAssured(SumAssuredType.RANGE, new BigDecimal(10000000), new BigDecimal(40000000), 10000, null, 0);
        builder.withPremiumTerm(PremiumTermType.SPECIFIED_VALUES,
                Sets.newHashSet(30, 35, 40, 45, 50, 55, 60), 60);
        builder.withPlanCoverages(Sets.newHashSet(planCoverage));
        builder.withPolicyTerm(PolicyTermType.SPECIFIED_VALUES,
                Sets.newHashSet(30, 35, 40, 45, 50, 55, 60), 60);
        Plan plan = builder.build(planId);
        springMongoTemplate.save(plan);

        BasicDBObject query = new BasicDBObject();
        query.put("planId", planId);
        DBCollection table = springMongoTemplate.getCollection("PLAN");
        DBObject savedPlan = table.findOne();
        Assert.assertNotNull(savedPlan);
    }

}
