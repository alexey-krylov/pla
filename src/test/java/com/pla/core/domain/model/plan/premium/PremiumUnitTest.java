package com.pla.core.domain.model.plan.premium;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.pla.core.domain.model.generalinformation.DiscountFactorOrganizationInformation;
import com.pla.core.domain.model.generalinformation.ModelFactorOrganizationInformation;
import com.pla.publishedlanguage.domain.model.PremiumInfluencingFactor;
import com.pla.sharedkernel.domain.model.DiscountFactorItem;
import com.pla.sharedkernel.domain.model.ModalFactorItem;
import com.pla.sharedkernel.domain.model.PremiumFactor;
import com.pla.sharedkernel.domain.model.PremiumRateFrequency;
import com.pla.sharedkernel.identifier.CoverageId;
import com.pla.sharedkernel.identifier.PlanId;
import com.pla.sharedkernel.identifier.PremiumId;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.springframework.test.util.ReflectionTestUtils.invokeGetterMethod;

/**
 * Created by Admin on 4/16/2015.
 */
public class PremiumUnitTest {
    PlanId planId;
    PremiumId premiumId;
    CoverageId coverageId;
    Premium premium;
    PremiumItem premiumItem;
    List<Map<Map<PremiumInfluencingFactor, String>, Double>> premiumExcelLineItems;
    List<PremiumInfluencingFactor> premiumInfluencingFactors;
    Set<PremiumItem> premiumItems;
    Set<DiscountFactorOrganizationInformation> discountFactorItems;
    Set<ModelFactorOrganizationInformation> modelFactorItems;

    @Before
    public void setUp() {
        planId = new PlanId("P001");
        premiumId = new PremiumId("PR001");
        coverageId = new CoverageId("C001");
        premiumItems = Sets.newLinkedHashSet();

        premiumExcelLineItems = Lists.newArrayList();
        Map<Map<PremiumInfluencingFactor, String>, Double> premiumExcelItemMap = Maps.newLinkedHashMap();
        Map<PremiumInfluencingFactor, String> premiumInfluencingFactorMap = Maps.newLinkedHashMap();
        premiumInfluencingFactorMap.put(PremiumInfluencingFactor.AGE, "Age");
        premiumInfluencingFactorMap.put(PremiumInfluencingFactor.POLICY_TERM, "Policy Term");
        premiumInfluencingFactorMap.put(PremiumInfluencingFactor.DESIGNATION, "Designation");
        premiumInfluencingFactorMap.put(PremiumInfluencingFactor.SMOKING_STATUS, "Smoking Status");
        premiumInfluencingFactorMap.put(PremiumInfluencingFactor.SUM_ASSURED, "Sum Assured");
        premiumExcelItemMap.put(premiumInfluencingFactorMap, 100.00);
        premiumExcelLineItems.add(premiumExcelItemMap);
        premiumItem = PremiumItem.createCoveragePremiumItem(premiumExcelItemMap);
        premiumItems.add(premiumItem);

        premiumInfluencingFactors = Lists.newArrayList();
        premiumInfluencingFactors.add(PremiumInfluencingFactor.AGE);
        premiumInfluencingFactors.add(PremiumInfluencingFactor.DESIGNATION);
        premiumInfluencingFactors.add(PremiumInfluencingFactor.POLICY_TERM);
        premiumInfluencingFactors.add(PremiumInfluencingFactor.SMOKING_STATUS);
        premiumInfluencingFactors.add(PremiumInfluencingFactor.SUM_ASSURED);

        premium = Premium.createPremiumWithPlan(premiumId, planId, new LocalDate("2015-04-20"), premiumExcelLineItems, PremiumFactor.FLAT_AMOUNT, PremiumRateFrequency.MONTHLY,
                premiumInfluencingFactors);

        discountFactorItems = Sets.newLinkedHashSet();
        DiscountFactorOrganizationInformation discountFactorOrganizationInformation = new DiscountFactorOrganizationInformation(DiscountFactorItem.ANNUAL, new BigDecimal(1234.867744));
        discountFactorItems.add(discountFactorOrganizationInformation);
        discountFactorOrganizationInformation = new DiscountFactorOrganizationInformation(DiscountFactorItem.SEMI_ANNUAL, new BigDecimal(55555.864989));
        discountFactorItems.add(discountFactorOrganizationInformation);
        discountFactorOrganizationInformation = new DiscountFactorOrganizationInformation(DiscountFactorItem.QUARTERLY, new BigDecimal(44444.884648493));
        discountFactorItems.add(discountFactorOrganizationInformation);

        modelFactorItems = Sets.newLinkedHashSet();
        ModelFactorOrganizationInformation modelFactorOrganizationInformation = new ModelFactorOrganizationInformation(ModalFactorItem.SEMI_ANNUAL, new BigDecimal(1000.99194));
        modelFactorItems.add(modelFactorOrganizationInformation);
        modelFactorOrganizationInformation = new ModelFactorOrganizationInformation(ModalFactorItem.QUARTERLY, new BigDecimal(1001.99994));
        modelFactorItems.add(modelFactorOrganizationInformation);
        modelFactorOrganizationInformation = new ModelFactorOrganizationInformation(ModalFactorItem.MONTHLY, new BigDecimal(1002.99899));
        modelFactorItems.add(modelFactorOrganizationInformation);
    }


    @Test
    public void givenTheNeededInformationToCreateThePremium_whenAllTheInformationAreCorrect_thenItShouldCreateThePremiumWithPlan() {
        Premium premium = Premium.createPremiumWithPlan(premiumId, planId, new LocalDate("2015-04-20"), premiumExcelLineItems, PremiumFactor.FLAT_AMOUNT, PremiumRateFrequency.MONTHLY,
                premiumInfluencingFactors);
        assertNotNull(premium);
        assertEquals(new LocalDate("2015-04-20"), invokeGetterMethod(premium, "effectiveFrom"));
        assertEquals(new PlanId("P001"), invokeGetterMethod(premium, "PlanId"));
    }

    @Test
    public void givenTheInformationToCreateAPremiumPlan_whenAllTheInformationAreCorrect_thenItShouldCreateThePremiumWithPlanAndCoverage() {
        Premium premiumWithPlanAndCoverage = Premium.createPremiumWithPlanAndCoverage(premiumId, planId, coverageId, new LocalDate("2015-04-20"), premiumExcelLineItems, PremiumFactor.PER_THOUSAND, PremiumRateFrequency.MONTHLY, premiumInfluencingFactors);
        assertNotNull(premiumWithPlanAndCoverage);
        assertEquals(new LocalDate("2015-04-20"), invokeGetterMethod(premiumWithPlanAndCoverage, "effectiveFrom"));
        assertEquals(new PlanId("P001"), invokeGetterMethod(premiumWithPlanAndCoverage, "planId"));
        assertEquals(new CoverageId("C001"), invokeGetterMethod(premiumWithPlanAndCoverage, "coverageId"));
    }

    @Test
    public void givenAPremiumPlan_whenPremiumPlanAssignedWithValidTillDate_thenItShouldReturnThePremiumPlanWithAssignedValidTillDate() {
        premium = premium.expirePremium(new LocalDate("2015-06-15"));
        assertEquals(new LocalDate("2015-06-15"), invokeGetterMethod(premium, "validTill"));
    }

    @Test
    public void givenPremiumItemAndDiscountFactor_thenItShouldReturnTheAnnualDiscountFactor() {
        BigDecimal expectedAnnualDiscountFactor = new BigDecimal(123486.7744).setScale(4, BigDecimal.ROUND_CEILING);
        BigDecimal annualDiscountFactor = premium.getAnnualPremium(premiumItem, discountFactorItems, 365);
        annualDiscountFactor = annualDiscountFactor.setScale(4, BigDecimal.ROUND_CEILING);
        assertThat(expectedAnnualDiscountFactor, is(annualDiscountFactor));

    }

    @Test
    public void givenPremiumItemAndDiscountFactor_thenItShouldReturnTheSemiAnnualDiscountFactor() {
        BigDecimal expectedSemiAnnualDiscountFactor = new BigDecimal(5555586.4990).setScale(4, BigDecimal.ROUND_CEILING);
        BigDecimal semiAnnualDiscountFactor = premium.getSemiAnnuallyPremium(premiumItem, modelFactorItems, discountFactorItems, 365);
        semiAnnualDiscountFactor = semiAnnualDiscountFactor.setScale(4, BigDecimal.ROUND_CEILING);
        assertThat(expectedSemiAnnualDiscountFactor, is(semiAnnualDiscountFactor));

    }

    /*
    * set scale for premium amount for which Premium Rate frequency is Monthly
    * */
    @Test
    public void givenPremiumItemAndModalFactorItem_whenModalFactorItemIsMonthly_thenItShouldReturnThePremiumAmount() {
        BigDecimal expectedMonthlyPremiumFactor = new BigDecimal(100.00).setScale(1, BigDecimal.ROUND_HALF_UP);
        BigDecimal monthlyPremium = premium.getMonthlyPremium(premiumItem, modelFactorItems, 365);
        assertThat(expectedMonthlyPremiumFactor, is(monthlyPremium));
    }

    @Test
    public void givenPremiumItemAndModalFactorItem_whenModalFactorItemIsNotMonthly_thenItShouldReturnTheMonthlyPremiumAmount() {
        BigDecimal expectedMonthlyPremiumFactor = new BigDecimal(100299.8991).setScale(4, BigDecimal.ROUND_CEILING);
        premium.setPremiumRateFrequency(PremiumRateFrequency.YEARLY);
        BigDecimal monthlyPremium = premium.getMonthlyPremium(premiumItem, modelFactorItems, 365);
        monthlyPremium = monthlyPremium.setScale(4, BigDecimal.ROUND_CEILING);
        assertThat(expectedMonthlyPremiumFactor, is(monthlyPremium));
    }

    @Test
    public void givenPremiumItemAndModalFactorItem_whenModalFactorItemIsNotMonthly_thenItShouldReturnTheQuarterlyPremiumAmount() {
        BigDecimal expectedQuarterlyPremiumFactor = new BigDecimal(100199.9941).setScale(4, BigDecimal.ROUND_CEILING);
        premium.setPremiumRateFrequency(PremiumRateFrequency.YEARLY);
        BigDecimal quarterlyPremium = premium.getQuarterlyPremium(premiumItem, modelFactorItems, discountFactorItems, 365);
        quarterlyPremium = quarterlyPremium.setScale(4,BigDecimal.ROUND_CEILING);
        assertThat(expectedQuarterlyPremiumFactor, is(quarterlyPremium));
    }

}
