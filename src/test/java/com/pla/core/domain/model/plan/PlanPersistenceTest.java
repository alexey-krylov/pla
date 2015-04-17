package com.pla.core.domain.model.plan;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.mongodb.Mongo;
import com.pla.core.presentation.command.CreatePlanCommand;
import com.pla.core.presentation.command.PlanCommandHandler;
import com.pla.core.presentation.command.UpdatePlanCommand;
import com.pla.core.repository.ObjectIdToPlanIdConverter;
import com.pla.core.repository.PlanIdToObjectIdConverter;
import com.pla.sharedkernel.domain.model.*;
import com.pla.sharedkernel.identifier.CoverageId;
import com.pla.sharedkernel.identifier.LineOfBusinessId;
import com.pla.sharedkernel.identifier.PlanId;
import org.axonframework.repository.Repository;
import org.axonframework.unitofwork.DefaultUnitOfWork;
import org.axonframework.unitofwork.NoTransactionManager;
import org.axonframework.unitofwork.UnitOfWork;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.nthdimenzion.axonframework.repository.GenericMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.data.authentication.UserCredentials;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.convert.CustomConversions;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author: pradyumna
 * @since 1.0 23/03/2015
 */

/*@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("mongodb")
@ContextConfiguration(locations = {"classpath*:META-INF/spring/cqrs-infrastructure-context.xml"})
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class})*/
public class PlanPersistenceTest {


    @Autowired
    Repository<Plan> planMongoRepository;
    PlanId planId;
    @Autowired
    private ApplicationContext applicationContext;
    private PlanDetail planDetail;
    private PlanCoverage planCoverage;
    private CoverageId coverageId = new CoverageId("1");

   /* @Rule
    public MongoDbRule mongoDbRule = newMongoDbRule().defaultSpringMongoDb("pla");*/

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

//    @ShouldMatchDataSet(location = "classpath:plan-expected.json")
    public void storePlan() throws FileNotFoundException {
/*
        PlanBuilder builder = Plan.builder();
        builder.withPlanDetail(planDetail);
        builder.withPlanSumAssured(SumAssuredType.RANGE, new BigDecimal(10000000), new BigDecimal(40000000), 100000, null, 0);
        builder.withPremiumTerm(PremiumTermType.SPECIFIED_VALUES,
                Sets.newHashSet(30, 35, 40, 45, 50, 55, 60, 70, 80), 80);
        builder.withPlanCoverages(Sets.newHashSet(planCoverage));
        builder.withPolicyTerm(PolicyTermType.SPECIFIED_VALUES,
                Sets.newHashSet(30, 35, 40, 45, 50, 55, 60, 70, 80), 80);
        Plan plan = builder.build(planId);
        springMongoTemplate.save(plan);

        BasicDBObject query = new BasicDBObject();
        query.put("planId", planId);
        DBCollection table = springMongoTemplate.getCollection("PLAN");
        DBObject savedPlan = table.findOne();
        Assert.assertNotNull(savedPlan);*/
    }

    @Test
    public void testMongoTemplate() {
        try {
            Mongo mongo = new Mongo("localhost", 33016);
            UserCredentials credentials = new UserCredentials("pla", "pla");
            SimpleMongoDbFactory mongoDbFactory = new SimpleMongoDbFactory(mongo, "pla", credentials);
            MongoTemplate mongoTemplate = new MongoTemplate(mongoDbFactory);

            MappingMongoConverter mappingMongoConverter = (MappingMongoConverter) mongoTemplate.getConverter();
            mappingMongoConverter.setTypeMapper(new DefaultMongoTypeMapper(null));
            GenericConversionService conversionService = (GenericConversionService) mappingMongoConverter.getConversionService();
            CustomConversions conversions = new CustomConversions(Lists.newArrayList(new ObjectIdToPlanIdConverter(), new PlanIdToObjectIdConverter()));
            mappingMongoConverter.setCustomConversions(conversions);
            conversionService.addConverter(new ObjectIdToPlanIdConverter());
            conversionService.addConverter(new PlanIdToObjectIdConverter());
            ObjectMapper objectMapper = new ObjectMapper();
            CreatePlanCommand command = null;
            try {
                command = objectMapper.readValue(new FileInputStream(new File("C://workspace//plan.txt")), CreatePlanCommand.class);
            } catch (Exception e) {
            }

            UpdatePlanCommand updateCmd = null;
            try {
                updateCmd = objectMapper.readValue(new FileInputStream(new File("C://workspace//plan.txt")), UpdatePlanCommand.class);
            } catch (Exception e) {
            }
//            Repository<Plan> repository = (Repository<Plan>)applicationContext.getBean("planMongoRepository");
/*            UnitOfWork uom = new DefaultUnitOfWork(new NoTransactionManager());
            uom.start();
            planMongoRepository = new GenericMongoRepository<>(mongoTemplate, Plan.class);
            PlanCommandHandler planCommandHandler = new PlanCommandHandler(planMongoRepository);
            planCommandHandler.handle(command);
            uom.commit();

            uom = new DefaultUnitOfWork(new NoTransactionManager());
            uom.start();
            planMongoRepository = new GenericMongoRepository<>(mongoTemplate, Plan.class);
            planCommandHandler.handle(updateCmd);
            uom.commit();*/

          /*  PlanBuilder builder = Plan.builder();
            builder.withPlanDetail(planDetail);
            builder.withPlanSumAssured(SumAssuredType.RANGE, new BigDecimal(10000000), new BigDecimal(40000000), 100000, null, 0);
            builder.withPremiumTerm(PremiumTermType.SPECIFIED_VALUES,
                    Sets.newHashSet(30, 35, 40, 45, 50, 55, 60, 70, 80), 80);
            builder.withPlanCoverages(Sets.newHashSet(planCoverage));
            builder.withPolicyTerm(PolicyTermType.SPECIFIED_VALUES,
                    Sets.newHashSet(30, 35, 40, 45, 50, 55, 60, 70, 80), 80);
            Plan plan = builder.build(planId);
            plan.hashCode();

            mongoTemplate.insert(plan);*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
