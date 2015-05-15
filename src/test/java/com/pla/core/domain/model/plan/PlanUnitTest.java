package com.pla.core.domain.model.plan;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.pla.sharedkernel.domain.model.*;
import com.pla.sharedkernel.identifier.CoverageId;
import com.pla.sharedkernel.identifier.LineOfBusinessId;
import com.pla.sharedkernel.identifier.PlanId;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

/**
 * Created by Admin on 4/8/2015.
 */
public class PlanUnitTest {

    Plan plan;
    PlanId planId;
    PlanBuilder builder;
    PlanCoverageBuilder planCoverageBuilder;
    private PlanDetail planDetail;
    private PlanCoverage planCoverage;
    private CoverageId coverageId = new CoverageId("1");

    @Before
    public void setup() {
        PlanDetailBuilder planDetailBuilder = PlanDetail.builder();
        LocalDate launchDate = LocalDate.now().plusDays(10);
        LocalDate withdrawalDate = LocalDate.now().plusDays(30);
        Set<Relationship> relationshipSet = new HashSet<>(Arrays.asList(Relationship.BROTHER, Relationship.DAUGHTER));
        Set<EndorsementType> endorsementTypes = new HashSet<>(Arrays.asList(EndorsementType.IND_CHANGE_ADDRESS, EndorsementType.IND_CHANGE_NAME));
        planDetail = planDetailBuilder.withPlanName("Plan 1")
                .withPlanCode("0001900")
                .withLaunchDate(launchDate)
                .withWithdrawalDate(withdrawalDate)
                .withMinEntryAge(21)
                .withMaxEntryAge(60)
                .withFreeLookPeriod(15)
                .withSurrenderAfter(5)
                .withClientType(ClientType.INDIVIDUAL)
                .withLineOfBusinessId(LineOfBusinessId.INDIVIDUAL_LIFE)
                .withPlanType(PlanType.NON_INVESTMENT)
                .withApplicableRelationships(relationshipSet)
                .withEndorsementTypes(endorsementTypes)
                .withTaxApplicable(false)
                .build();
        planCoverageBuilder = PlanCoverage.builder();
        planCoverage = planCoverageBuilder.withCoverage(coverageId)
                .withCoverageCover(CoverageCover.ACCELERATED)
                .withTaxApplicable(false)
                .withCoverageType(CoverageType.BASE)
                .withCoverageTerm(CoverageTermType.SPECIFIED_VALUES, Sets.newHashSet(30, 35, 40, 45, 50, 55, 60, 70, 80), 80)
                .withSumAssuredForPlanCoverage(SumAssuredType.SPECIFIED_VALUES,new BigDecimal(10000),new BigDecimal(100000),1000,Sets.newHashSet(new BigDecimal(100),new BigDecimal(200)),10)
                .withMinAndMaxAge(21, 45)
                .withWaitingPeriod(5)
                .build();

        planId = new PlanId();

        builder = Plan.builder();
        builder.withPlanDetail(planDetail);
        builder.withPlanSumAssured(SumAssuredType.SPECIFIED_VALUES, new BigDecimal(10000), new BigDecimal(40000), 10000, Sets.newHashSet(new BigDecimal(500),new BigDecimal(1000)), 0);
        builder.withPlanCoverages(Sets.newHashSet(planCoverage));
        builder.withPolicyTerm(PolicyTermType.SPECIFIED_VALUES,
                Sets.newHashSet(30, 35, 40, 45, 50, 55, 60, 70, 80), 80,null);
        plan = builder.build(planId);
    }

    @Test
    public void givenAPlanWithPolicyTermType_whenPolicyTermTypeIsSpecifiedValues_thenItShouldReturnSetOfValidTerm(){
        Set<Integer> expectedValidTerm = Sets.newHashSet(30, 35, 40, 45, 50, 55, 60, 70, 80);
        Set<Integer> validTerms = plan.getAllowedPolicyTerm();
        assertThat(expectedValidTerm, is(validTerms));
        assertThat(plan.getPolicyTerm().getMaxMaturityAge(),is(80));
    }

    @Test
    public void givenAPlanWithPolicyTermType_whenPolicyTermTypeIsMaturityAgeDependent_thenItShouldReturnSetOfMaturityAges(){
        Set<Integer> expectedMaturityAges = Sets.newHashSet(30, 35, 40, 45, 50, 55, 60, 70, 80);

        builder.withPolicyTerm(PolicyTermType.MATURITY_AGE_DEPENDENT,
                Sets.newHashSet(30, 35, 40, 45, 50, 55, 60, 70, 80), 80,null);
        plan = builder.build();
        Set<Integer> maturityAges = plan.getAllowedPolicyTerm();
        assertThat(expectedMaturityAges, is(maturityAges));
        assertThat(plan.getPolicyTerm().getMaxMaturityAge(),is(0));
    }

    @Test
    public void givenCoverageId_whenPlanCoverageHasPlanTermAsSpecifiedValue_thenItShouldReturnSetOfValidTerm(){
        Set<Integer> expectedValidTerm = Sets.newHashSet(30, 35, 40, 45, 50, 55, 60, 70, 80);
        Set<Integer> allowedCoverageTerm = plan.getAllowedCoverageTerm(coverageId);
        assertThat(expectedValidTerm,is(allowedCoverageTerm));
        assertThat(plan.getPolicyTerm().maxMaturityAge,is(80));

    }

    @Test
    public void givenPlanWithCoverageTermTypeAndPolicyTermType_whenCoverageTermTypeIsPolicyTermAndPolicyTermTypeIsMaturityAgeDependent_thenItShouldReturnSetOfMaturityAges(){
        planCoverageBuilder.withCoverageTerm(CoverageTermType.POLICY_TERM, Sets.newHashSet(30, 35, 40, 45, 50, 55, 60, 70, 80), 80);
        builder.withPolicyTerm(PolicyTermType.MATURITY_AGE_DEPENDENT,
                Sets.newHashSet(30, 35, 40, 45, 50, 55, 60, 70, 80), 80,null);
        plan = builder.build();
        Set<Integer> expectedMaturityAges = Sets.newHashSet(30, 35, 40, 45, 50, 55, 60, 70, 80);
        Set<Integer> maturityAges = plan.getAllowedCoverageTerm(coverageId);
        assertThat(expectedMaturityAges,is(maturityAges));
        assertThat(plan.getPolicyTerm().maxMaturityAge,is(0));
    }

    @Test
    public void givenPolicyTerm_whenPolicyTermTypeIsSpecifiedValuesAndValidTermContainsTheGivenValue_thenItShouldReturnTrue(){
        Boolean isValidTerm  = plan.isValidPolicyTerm(30);
        assertTrue(isValidTerm);
    }

    @Test
    public void givenPolicyTerm_whenPolicyTermTypeIsSpecifiedValuesAndValidTermDoesNotContainsTheGivenValue_thenItShouldReturnFalse(){
        Boolean isValidTerm  = plan.isValidPolicyTerm(10);
        assertFalse(isValidTerm);
    }

    @Test
    public void givenPolicyTerm_whenPolicyTermTypeIsAgeDependentAndSetOfMaturityAgeContainsTheGivenValue_thenItShouldReturnTrue(){
        builder.withPolicyTerm(PolicyTermType.MATURITY_AGE_DEPENDENT,
                Sets.newHashSet(30, 35, 40, 45, 50, 55, 60, 70), 80,null);
        plan = builder.build();
        Boolean isValidTerm  = plan.isValidPolicyTerm(70);
        assertTrue(isValidTerm);
    }

    @Test
    public void givenPolicyTerm_whenPolicyTermTypeIsAgeDependentAndSetOfMaturityAgeDoesNotContainTheGivenValue_thenItShouldReturnFalse(){
        builder.withPolicyTerm(PolicyTermType.MATURITY_AGE_DEPENDENT,
                Sets.newHashSet(30, 35, 40, 45, 50, 55, 60, 70, 80), 80,null);
        plan = builder.build();
        Boolean isValidTerm  = plan.isValidPolicyTerm(85);
        assertFalse(isValidTerm);
    }

    @Test
    public void givenCoverageTermAndCoverageId_whenCoverageTermTypeIsPolicyTermAndPolicyTermTypeIsSpecifiedValuesAndValidTermContainTheCoverageTerm_thenItShouldReturnTrue(){
        Boolean isValidCoverageTerm = plan.isValidCoverageTerm(30, coverageId);
        assertTrue(isValidCoverageTerm);
    }

    @Test
    public void givenCoverageTermAndCoverageId_whenCoverageTermTypeIsPolicyTermAndPolicyTermTypeIsSpecifiedValuesAndValidTermDoesNotContainTheCoverageTerm_thenItShouldReturnFalse(){
        Boolean isValidCoverageTerm = plan.isValidCoverageTerm(90, coverageId);
        assertFalse(isValidCoverageTerm);
    }


    @Test
    public void givenCoverageTermAndCoverageId_whenPolicyTermTypeIsMaturityAgeDependentAndMaturityAgesSetContainTheCoverageTerm_thenItShouldReturnTrue(){
        builder.withPolicyTerm(PolicyTermType.MATURITY_AGE_DEPENDENT,
                Sets.newHashSet(30, 35, 40, 45, 50, 55, 60, 70, 80), 80,null);
        plan = builder.build();
        Boolean isValidCoverageTerm = plan.isValidCoverageTerm(40, coverageId);
        assertTrue(isValidCoverageTerm);
    }

    @Test
    public void givenCoverageTermAndCoverageId_whenPolicyTermTypeIsMaturityAgeDependentAndMaturityAgesSetDoesNotContainTheCoverageTerm_thenItShouldReturnFalse(){
        builder.withPolicyTerm(PolicyTermType.MATURITY_AGE_DEPENDENT,
                Sets.newHashSet(30, 35, 40, 45, 50, 55, 60, 70, 80), 80,null);
        plan = builder.build();
        Boolean isValidCoverageTerm = plan.isValidCoverageTerm(15, coverageId);
        assertFalse(isValidCoverageTerm);
    }

    @Test
    public void givenPreConfiguredPlan_whenPremiumTermTypeIsSpecifiedValues_thenItShouldReturnSetOfValidTerm(){
        Set<Integer> expectedPremiumPlan  = Sets.newHashSet(30, 35, 40, 45, 50, 55, 60, 70, 80);

        builder.withPremiumTerm(PremiumTermType.SPECIFIED_VALUES,
                Sets.newHashSet(30, 35, 40, 45, 50, 55, 60, 70, 80), 80);
        plan = builder.build();
        Set<Integer> allowedPremiumPlan =  plan.getAllowedPremiumTerms();
        assertThat(expectedPremiumPlan,is(allowedPremiumPlan));
        assertThat(plan.getPolicyTerm().getMaxMaturityAge(),is(80));
    }

    @Test
    public void givenPreConfiguredPlan_whenPremiumTermTypeIsSpecifiedAges_thenItShouldReturnSetOfMaturityAges(){
        Set<Integer> expectedPremiumPlan  =  Sets.newHashSet(20,25,30, 35, 40, 45, 50, 55, 60, 70, 80);
        builder.withPremiumTerm(PremiumTermType.SPECIFIED_AGES,
                Sets.newHashSet(20,25,30, 35, 40, 45, 50, 55, 60, 70, 80),80);
        plan = builder.build();
        Set<Integer> allowedPremiumPlan =  plan.getAllowedPremiumTerms();
        assertThat(expectedPremiumPlan,is(allowedPremiumPlan));
        assertThat(plan.getPremiumTerm().getMaxMaturityAge(),is(0));
    }

    @Test
    public void givenPreConfiguredPlan_whenPremiumTermTypeIsRegularAndPolicyTermTypeIsSpecifiedValues_thenItShouldReturnSetOfValidTerms(){
        Set<Integer> expectedPolicyValidTerm  =  Sets.newHashSet(30, 35, 40, 45, 50, 55, 60, 70, 80);
        builder.withPremiumTerm(PremiumTermType.REGULAR,
                Sets.newHashSet(30, 35, 40, 45, 50, 55, 60, 70, 80),80);
        plan = builder.build();
        Set<Integer> allowedPremiumPlan =  plan.getAllowedPremiumTerms();
        assertThat(expectedPolicyValidTerm,is(allowedPremiumPlan));
        assertThat(plan.getPremiumTerm().getMaxMaturityAge(),is(80));
    }

    @Test
    public void givenPreConfiguredPlan_whenPremiumTermTypeIsRegularAndPolicyTermTypeIsAgeDependent_thenItShouldReturnSetOfMaturityAges(){
        Set<Integer> expectedPolicyValidTerm  =  Sets.newHashSet(30, 35, 40, 45);

        builder.withPolicyTerm(PolicyTermType.MATURITY_AGE_DEPENDENT,
                Sets.newHashSet(30, 35, 40, 45), 80,null);

        builder.withPremiumTerm(PremiumTermType.REGULAR,
                Sets.newHashSet(30, 35, 40, 45, 50, 55, 60, 70, 80),80);
        plan = builder.build();
        Set<Integer> allowedPremiumPlan =  plan.getAllowedPremiumTerms();
        assertThat(expectedPolicyValidTerm,is(allowedPremiumPlan));
        assertThat(plan.getPremiumTerm().getMaxMaturityAge(),is(0));
    }

    @Test
    public void givenPremiumTerm_whenPremiumTermTypeIsSpecifiedValuesAndValidTermContainsTheGivenPremiumTerm_thenItShouldReturnTrue(){
        builder.withPremiumTerm(PremiumTermType.SPECIFIED_VALUES,
                Sets.newHashSet(20,25,30, 35, 40, 45, 50, 55, 60, 70, 80),80);
        plan = builder.build();
        Boolean isValidPremium = plan.isValidPremiumTerm(25);
        assertTrue(isValidPremium);
    }

    @Test
    public void givenPremiumTerm_whenPremiumTermTypeIsSpecifiedValuesAndValidTermDoesNotContainsTheGivenPremiumTerm_thenItShouldReturnFalse(){
        builder.withPremiumTerm(PremiumTermType.SPECIFIED_VALUES,
                Sets.newHashSet(20,25,30, 35, 40, 45, 50, 55, 60, 70, 80),80);
        plan = builder.build();
        Boolean isValidPremium = plan.isValidPremiumTerm(65);
        assertFalse(isValidPremium);
    }

    @Test
    public void givenPremiumTerm_whenPremiumTermTypeIsSpecifiedAgesAndSetMaturityAgesContainsTheGivenPremiumTerm_thenItShouldReturnTrue(){
        builder.withPremiumTerm(PremiumTermType.SPECIFIED_AGES,
                Sets.newHashSet(20,25,30, 35, 40, 45, 50, 55, 60, 70, 80),80);
        plan = builder.build();
        Boolean isValidPremium = plan.isValidPremiumTerm(25);
        assertTrue(isValidPremium);
    }

    @Test
    public void givenPremiumTerm_whenPremiumTermTypeIsSpecifiedAgesAndSetMaturityAgesDoesNotContainsTheGivenPremiumTerm_thenItShouldReturnFalse(){
        builder.withPremiumTerm(PremiumTermType.SPECIFIED_AGES,
                Sets.newHashSet(20,25,30, 35, 40, 45, 50, 55, 60, 70, 80),80);
        plan = builder.build();
        Boolean isValidPremium = plan.isValidPremiumTerm(65);
        assertFalse(isValidPremium);
    }

    @Test
    public void givenPremiumTerm_whenPremiumTermTypeIsRegularAndPolicyTermTypeIsSpecifiedValuesAndValidTermContainsTheGivenPremiumTerm_thenItShouldReturnTrue(){
        builder.withPremiumTerm(PremiumTermType.REGULAR,
                Sets.newHashSet(20,25,30, 35, 40, 45, 50, 55, 60, 70, 80),80);
        plan = builder.build();
        Boolean isValidPremium = plan.isValidPremiumTerm(30);
        assertTrue(isValidPremium);
    }

    @Test
    public void givenPremiumTerm_whenPremiumTermTypeIsRegularAndPolicyTermTypeIsSpecifiedValuesAndValidTermDoesNotContainsTheGivenPremiumTerm_thenItShouldReturnFalse(){
        builder.withPremiumTerm(PremiumTermType.REGULAR,
                Sets.newHashSet(20,25,30, 35, 40, 45, 50, 55, 60, 70, 80),80);
        plan = builder.build();
        Boolean isValidPremium = plan.isValidPremiumTerm(75);
        assertFalse(isValidPremium);
    }

    @Test
    public void givenPremiumTerm_whenPremiumTermTypeIsRegularAndPolicyTermTypeIsAgeDependentAndSetMaturityAgeContainsTheGivenPremiumTerm_thenItShouldReturnTrue(){
        builder.withPolicyTerm(PolicyTermType.MATURITY_AGE_DEPENDENT,
                Sets.newHashSet(30, 35, 40, 45), 80,null);
        builder.withPremiumTerm(PremiumTermType.REGULAR,
                Sets.newHashSet(20,25,30, 35, 40, 45, 50, 55, 60, 70, 80),80);
        plan = builder.build();
        Boolean isValidPremium = plan.isValidPremiumTerm(35);
        assertTrue(isValidPremium);
    }

    @Test
    public void givenPremiumTerm_whenPremiumTermTypeIsRegularAndPolicyTermTypeIsAgeDependentAndSetMaturityAgeDoesNotContainsTheGivenPremiumTerm_thenItShouldReturnFalse(){
        builder.withPolicyTerm(PolicyTermType.MATURITY_AGE_DEPENDENT,
                Sets.newHashSet(30, 35, 40, 45), 80,null);
        builder.withPremiumTerm(PremiumTermType.REGULAR,
                Sets.newHashSet(20,25,30, 35, 40, 45, 50, 55, 60, 70, 80),80);
        plan = builder.build();

        Boolean isValidPremium = plan.isValidPremiumTerm(65);
        assertFalse(isValidPremium);
    }

    @Test
    public void givenCoverageId_whenAssuredTypeIsSpecifiedValues_thenItShouldReturnTheSetOfCoverageSumAssuredValues(){
        List<BigDecimal> setOfAssuredValues = Lists.newArrayList(new BigDecimal(100), new BigDecimal(200));
        List<BigDecimal> sumAssuredValues = plan.getAllowedCoverageSumAssuredValues(coverageId);
        assertThat(setOfAssuredValues,is(sumAssuredValues));
    }

    @Test
    public void givenCoverageId_whenAssuredTypeIsRange_thenItShouldReturnTheSetOfMinAndMaxAssuredValues(){
        planCoverage  = planCoverageBuilder.withSumAssuredForPlanCoverage(SumAssuredType.RANGE, new BigDecimal(10000), new BigDecimal(50000), 10000, Sets.newHashSet(new BigDecimal(100), new BigDecimal(200)), 10).build();
        builder.withPlanCoverages(Sets.newHashSet(planCoverage));
        plan = builder.build();
        List<BigDecimal> setOfAssuredValues = Lists.newArrayList(new BigDecimal(10000), new BigDecimal(20000),new BigDecimal(30000), new BigDecimal(40000),new BigDecimal(50000));
        List<BigDecimal> sumAssuredValues = plan.getAllowedCoverageSumAssuredValues(coverageId);
        assertThat(setOfAssuredValues,is(sumAssuredValues));
    }

    @Test
    public void givenPreDefinedPlan_whenAssuredTypeIsSpecifiedValues_thenItShouldReturnTheSetOfCoverageSumAssuredValues(){
        List<BigDecimal> setOfAssuredValues = Lists.newArrayList(new BigDecimal(500), new BigDecimal(1000));
        List<BigDecimal> sumAssuredValues = plan.getAllowedSumAssuredValues();
        assertThat(setOfAssuredValues,is(sumAssuredValues));
    }

    @Test
    public void givenPreDefinedPlan_whenAssuredTypeIsRange_thenItShouldReturnTheSetOfMinAndMaxAssuredValues(){
        builder.withPlanSumAssured(SumAssuredType.RANGE, new BigDecimal(10000), new BigDecimal(40000), 10000, Sets.newHashSet(new BigDecimal(500), new BigDecimal(1000)), 10);
        plan = builder.build();
        List<BigDecimal> setOfAssuredValues = Lists.newArrayList(new BigDecimal(10000), new BigDecimal(20000),new BigDecimal(30000), new BigDecimal(40000));
        List<BigDecimal> sumAssuredValues = plan.getAllowedSumAssuredValues();
        assertThat(setOfAssuredValues,is(sumAssuredValues));
    }

    @Test
    public void givenSumAssuredValue_whenAssuredTypeIsSpecifiedValuesAndSumAssuredSetContainsTheGivenAssuredValue_thenItShouldReturnTrue(){
        Boolean isValidSumAssured =  plan.isValidSumAssured(new BigDecimal(1000));
        assertTrue(isValidSumAssured);
    }

    @Test
    public void givenSumAssuredValue_whenAssuredTypeIsSpecifiedValuesAndSumAssuredSetDoesNotContainsTheGivenAssuredValue_thenItShouldReturnFalse(){
        Boolean isValidSumAssured =  plan.isValidSumAssured(new BigDecimal(100));
        assertFalse(isValidSumAssured);
    }

    @Test
    public void givenSumAssuredValue_whenAssuredTypeIsRangeValuesAndSumAssuredSetContainsTheGivenAssuredValue_thenItShouldReturnTrue(){
        builder.withPlanSumAssured(SumAssuredType.RANGE, new BigDecimal(10000), new BigDecimal(40000), 10000, Sets.newHashSet(new BigDecimal(500), new BigDecimal(1000)), 10);
        plan = builder.build();
        Boolean isValidSumAssured =  plan.isValidSumAssured(new BigDecimal(10000));
        assertTrue(isValidSumAssured);
    }

    @Test
    public void givenSumAssuredValue_whenAssuredTypeIsRangeValuesAndSumAssuredSetDoesNotContainsTheGivenAssuredValue_thenItShouldReturnFalse(){
        builder.withPlanSumAssured(SumAssuredType.RANGE, new BigDecimal(10000), new BigDecimal(40000), 10000, Sets.newHashSet(new BigDecimal(500), new BigDecimal(1000)), 10);
        plan = builder.build();
        Boolean isValidSumAssured =  plan.isValidSumAssured(new BigDecimal(100));
        assertFalse(isValidSumAssured);
    }

    @Test
    public void givenSumAssuredValueAndCoverageId_whenAssuredTypeIsSpecifiedValuesAndSumAssuredSetContainsTheGivenAssuredValue_thenItShouldReturnTrue(){
        Boolean isValidCoverageSumAssured  = plan.isValidCoverageSumAssured(new BigDecimal(100), coverageId);
        assertTrue(isValidCoverageSumAssured);
    }

    @Test
    public void givenSumAssuredValueAndCoverageId_whenAssuredTypeIsSpecifiedValuesAndSumAssuredSetContainsTheGivenAssuredValue_thenItShouldReturnFalse(){
        Boolean isValidCoverageSumAssured  = plan.isValidCoverageSumAssured(new BigDecimal(1000), coverageId);
        assertFalse(isValidCoverageSumAssured);
    }


    @Test
    public void givenSumAssuredValueAndCoverageId_whenAssuredTypeIsRangeAndSumAssuredSetContainsTheGivenAssuredValue_thenItShouldReturnTrue(){
        planCoverage  = planCoverageBuilder.withSumAssuredForPlanCoverage(SumAssuredType.RANGE, new BigDecimal(10000), new BigDecimal(50000), 10000, Sets.newHashSet(new BigDecimal(100), new BigDecimal(200)), 10).build();
        builder.withPlanCoverages(Sets.newHashSet(planCoverage));
        plan = builder.build();
        Boolean isValidCoverageSumAssured  = plan.isValidCoverageSumAssured(new BigDecimal(10000), coverageId);
        assertTrue(isValidCoverageSumAssured);
    }

    @Test
    public void givenSumAssuredValueAndCoverageId_whenAssuredTypeIsRangeAndSumAssuredSetContainsTheGivenAssuredValue_thenItShouldReturnFalse(){
        planCoverage  = planCoverageBuilder.withSumAssuredForPlanCoverage(SumAssuredType.RANGE, new BigDecimal(10000), new BigDecimal(50000), 10000, Sets.newHashSet(new BigDecimal(100), new BigDecimal(200)), 10).build();
        builder.withPlanCoverages(Sets.newHashSet(planCoverage));
        plan = builder.build();
        Boolean isValidCoverageSumAssured  = plan.isValidCoverageSumAssured(new BigDecimal(100), coverageId);
        assertFalse(isValidCoverageSumAssured);
    }

    @Test
    public void givenAPlan_whenPolicyTermTypeIsSpecifiedValues_thenItShouldReturnTheMaximumMaturityAge(){
        int maximumMaturityAge = plan.getMaximumMaturityAge();
        assertThat(maximumMaturityAge,is(80));
        builder.withPolicyTerm(PolicyTermType.SPECIFIED_VALUES,
                Sets.newHashSet(30, 35, 40, 45, 50, 55, 60, 70, 80), 80,null);
    }

    @Test
    public void givenAPlan_whenPolicyTermTypeIsAgeDependent_thenItShouldReturnTheGreaterValueAmongTheSetOfMaturityAges(){
        builder.withPolicyTerm(PolicyTermType.MATURITY_AGE_DEPENDENT,
                Sets.newHashSet(30, 35, 40, 45, 50, 55), 80,null);
        plan = builder.build();
        int maximumMaturityAge = plan.getMaximumMaturityAge();
        assertThat(maximumMaturityAge,is(55));
    }

    @Test
    public void givenCoverageId_whenMinimumAgeIsLessThanMaxAge_thenItShouldReturnListOfCoveragesBetweenTheMinAndMaxAge(){
        List<Integer> expectedCoverageAges = Lists.newArrayList(21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45);
        List<Integer>  allowedCoverageAges =   plan.getAllowedCoverageAges(coverageId);
        assertThat(expectedCoverageAges,is(allowedCoverageAges));
    }

    @Test
    public void givenAge_whenTheAgeFallsUnderTheMinAndMaxAge_thenItShouldReturnTrue(){
        Boolean isValidAge  = plan.isValidAge(29);
        assertTrue(isValidAge);
    }

    @Test
    public void givenAge_whenTheAgeDoesNotFallsUnderTheMinAndMaxAge_thenItShouldReturnTrue(){
        Boolean isValidAge  = plan.isValidAge(50);
        assertTrue(isValidAge);
    }

    @Test
    public void givenAgeAndCoverageId_whenTheAgeFallsUnderTheMinAndMaxAge_thenItShouldReturnTrue(){
        Boolean isValidCoverageAge = plan.isValidCoverageAge(30, coverageId);
        assertTrue(isValidCoverageAge);
    }


    @Test
    public void givenAgeAndCoverageId_whenTheAgeDoesNotFallsUnderTheMinAndMaxAge_thenItShouldReturnFalse(){
        Boolean isValidCoverageAge = plan.isValidCoverageAge(80, coverageId);
        assertFalse(isValidCoverageAge);
    }

    @Test
    public void givenACoverageId_thenItShouldReturnThePlanCoverageForTheGivenCoverageId(){
        PlanCoverage planCoverage = plan.getPlanCoverageFor(coverageId);
        assertThat(CoverageCover.ACCELERATED,is(planCoverage.getCoverageCover()));
        assertThat(Sets.newHashSet(30, 35, 40, 45, 50, 55, 60, 70, 80),is(planCoverage.getCoverageTerm().validTerms));
    }
}
