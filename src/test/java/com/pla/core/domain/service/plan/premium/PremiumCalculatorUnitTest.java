package com.pla.core.domain.service.plan.premium;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pla.core.application.service.plan.premium.PremiumTemplateParser;
import com.pla.core.domain.exception.PremiumException;
import com.pla.core.domain.model.generalinformation.OrganizationGeneralInformation;
import com.pla.core.domain.model.plan.premium.Premium;
import com.pla.core.query.MasterFinder;
import com.pla.core.query.PremiumFinder;
import com.pla.core.repository.OrganizationGeneralInformationRepository;
import com.pla.publishedlanguage.domain.model.ComputedPremiumDto;
import com.pla.publishedlanguage.domain.model.PremiumCalculationDto;
import com.pla.publishedlanguage.domain.model.PremiumFrequency;
import com.pla.publishedlanguage.domain.model.PremiumInfluencingFactor;
import com.pla.sharedkernel.domain.model.*;
import com.pla.sharedkernel.identifier.PlanId;
import com.pla.sharedkernel.identifier.PremiumId;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Created by Samir on 4/26/2015.
 */
@RunWith(value = MockitoJUnitRunner.class)
@Ignore
public class PremiumCalculatorUnitTest {

    @Mock
    private MasterFinder masterFinder;

    @Mock
    private PremiumFinder premiumFinder;

    @Mock
    private OrganizationGeneralInformationRepository organizationGeneralInformationRepository;

    private OrganizationGeneralInformation organizationGeneralInformation;

    private PlanId planId;

    private PremiumCalculator premiumCalculator;

    private LocalDate premiumSetupDate = LocalDate.now().plusDays(5);

    List<Map<Map<PremiumInfluencingFactor, String>, Double>> premiumExcelLineItems;


    @Before
    public void setUp() {
        planId = new PlanId("1");
        organizationGeneralInformation = OrganizationGeneralInformation.createOrganizationGeneralInformation("1");
        Map<ModalFactorItem, BigDecimal> monthlyModalFactor = Maps.newHashMap();
        monthlyModalFactor.put(ModalFactorItem.MONTHLY, new BigDecimal("0.6789"));
        Map<ModalFactorItem, BigDecimal> quarterlyModalFactor = Maps.newHashMap();
        quarterlyModalFactor.put(ModalFactorItem.QUARTERLY, new BigDecimal("0.5432"));
        Map<ModalFactorItem, BigDecimal> semiAnnuallyModalFactor = Maps.newHashMap();
        semiAnnuallyModalFactor.put(ModalFactorItem.SEMI_ANNUAL, new BigDecimal("0.3124"));
        List<Map<ModalFactorItem, BigDecimal>> modalFactorList = Lists.newArrayList(monthlyModalFactor, quarterlyModalFactor, semiAnnuallyModalFactor);
        Map<DiscountFactorItem, BigDecimal> annualDiscountFactor = Maps.newHashMap();
        annualDiscountFactor.put(DiscountFactorItem.ANNUAL, new BigDecimal("0.4321"));
        Map<DiscountFactorItem, BigDecimal> quarterlyDiscountFactor = Maps.newHashMap();
        quarterlyDiscountFactor.put(DiscountFactorItem.QUARTERLY, new BigDecimal("0.2232"));
        Map<DiscountFactorItem, BigDecimal> semiAnnualDiscountFactor = Maps.newHashMap();
        semiAnnualDiscountFactor.put(DiscountFactorItem.SEMI_ANNUAL, new BigDecimal("0.3232"));
        List<Map<DiscountFactorItem, BigDecimal>> discountFactorList = Lists.newArrayList(annualDiscountFactor, semiAnnualDiscountFactor, quarterlyDiscountFactor);
        Map<Tax, BigDecimal> serviceTaxMap = Maps.newHashMap();
        serviceTaxMap.put(Tax.SERVICE_TAX, new BigDecimal(0.16));
        organizationGeneralInformation = organizationGeneralInformation.withDiscountFactorOrganizationInformation(discountFactorList);
        organizationGeneralInformation = organizationGeneralInformation.withModalFactorOrganizationInformation(modalFactorList);
        organizationGeneralInformation = organizationGeneralInformation.withServiceTaxOrganizationInformation(serviceTaxMap);
        premiumCalculator = new PremiumCalculator(premiumFinder, organizationGeneralInformationRepository);
    }

    /**
     * Given : Premium With PremiumFrequency As Yearly And PremiumFactor As FlatAmount
     * When  : Influencing Factors are SumAssured,Age,Gender,Premium Payment Term and Policy Term
     * Then  : Should calculate premium by Applying Modal Factor
     * Semi-Annual Modal Factor : 0.3124;Semi-Annual Premium = Annual Premium*Semi-Annual Modal Factor
     * Quarterly Modal Factor : 0.5432;Quarterly Premium = Annual Premium *Quarterly Modal Factor
     * Monthly Modal Factor : 0.6789;Monthly Premium = Annual Premium*Monthly Modal Factor
     */
    @Test
    public void givenPremiumWithFlatAmountItShouldCalculatePremiumWithPremiumInfluencingFactorSumAssuredAgePolicyTermByApplyingModalFactor() throws IOException {
        PremiumCalculationDto premiumCalculationDto = new PremiumCalculationDto(planId, premiumSetupDate.plusDays(1), PremiumFrequency.MONTHLY, 365);
        premiumCalculationDto = premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.SUM_ASSURED, "55000");
        premiumCalculationDto = premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.AGE, "22");
        premiumCalculationDto = premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.POLICY_TERM, "365");
        premiumCalculationDto = premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.PREMIUM_PAYMENT_TERM, "365");
        premiumCalculationDto = premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.GENDER, Gender.FEMALE.name());
        Premium premium = createPremium(PremiumFactor.FLAT_AMOUNT, PremiumRateFrequency.YEARLY, "premiumsetupdatawithflatpremium.xls");
        when(premiumFinder.findPremium(premiumCalculationDto)).thenReturn(premium);
        when(organizationGeneralInformationRepository.findAll()).thenReturn(Lists.newArrayList(organizationGeneralInformation));
        List<ComputedPremiumDto> computedPremiumDtoList = premiumCalculator.calculateBasicPremium(premiumCalculationDto);
        assertEquals(new BigDecimal("5000.5"), ComputedPremiumDto.getAnnualPremium(computedPremiumDtoList));
        assertEquals(new BigDecimal("1562.15620"), ComputedPremiumDto.getSemiAnnualPremium(computedPremiumDtoList));
        assertEquals(new BigDecimal("3394.83945"), ComputedPremiumDto.getMonthlyPremium(computedPremiumDtoList));
        assertEquals(new BigDecimal("2716.27160"), ComputedPremiumDto.getQuarterlyPremium(computedPremiumDtoList));

    }

    /**
     * Given : Premium With PremiumFrequency As Yearly And PremiumFactor As FlatAmount
     * When  : Influencing Factors are SumAssured,Age,Gender,Premium Payment Term and Policy Term and no of days!=365
     * Then  : Should calculate premium by Applying Modal Factor
     */
    @Test
    public void givenPremiumWithFlatAmountItShouldCalculatePremiumOnProrateBasisWithPremiumInfluencingFactorSumAssuredAgePolicyTermByApplyingModalFactor() throws IOException {
        PremiumCalculationDto premiumCalculationDto = new PremiumCalculationDto(planId, premiumSetupDate.plusDays(1), PremiumFrequency.MONTHLY, 270);
        premiumCalculationDto = premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.SUM_ASSURED, "55000");
        premiumCalculationDto = premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.AGE, "22");
        premiumCalculationDto = premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.POLICY_TERM, "365");
        premiumCalculationDto = premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.PREMIUM_PAYMENT_TERM, "365");
        premiumCalculationDto = premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.GENDER, Gender.FEMALE.name());
        Premium premium = createPremium(PremiumFactor.FLAT_AMOUNT, PremiumRateFrequency.YEARLY, "premiumsetupdatawithflatpremium.xls");
        when(premiumFinder.findPremium(premiumCalculationDto)).thenReturn(premium);
        when(organizationGeneralInformationRepository.findAll()).thenReturn(Lists.newArrayList(organizationGeneralInformation));
        List<ComputedPremiumDto> computedPremiumDtoList = premiumCalculator.calculateBasicPremium(premiumCalculationDto);
        assertEquals(new BigDecimal("3699.0000"), ComputedPremiumDto.getAnnualPremium(computedPremiumDtoList));
    }


    /**
     * Given : A Premium With PremiumFrequency As Yearly And PremiumFactor As Per Thousand
     * When  : Influencing Factors are SumAssured,Age,Gender,Premium Payment Term and Policy Term
     * Then  : Should calculate premium by Applying Modal Factor
     * Semi-Annual Modal Factor : 0.5012;Semi-Annual Premium = Annual Premium*Semi-Annual Modal Factor
     * Quarterly Modal Factor : 0.2552;Quarterly Premium = Annual Premium *Quarterly Modal Factor
     * Monthly Modal Factor : 0.0875;Monthly Premium = Annual Premium*Monthly Modal Factor
     */
    @Test
    public void givenPremiumWithPremiumFactorPerThousandItShouldCalculatePremiumWithPremiumInfluencingFactorSumAssuredAgePolicyTermByApplyingModalFactor() throws IOException {
        Map<ModalFactorItem, BigDecimal> monthlyModalFactor = Maps.newHashMap();
        monthlyModalFactor.put(ModalFactorItem.MONTHLY, new BigDecimal("0.0875"));
        Map<ModalFactorItem, BigDecimal> quarterlyModalFactor = Maps.newHashMap();
        quarterlyModalFactor.put(ModalFactorItem.QUARTERLY, new BigDecimal("0.2552"));
        Map<ModalFactorItem, BigDecimal> semiAnnuallyModalFactor = Maps.newHashMap();
        semiAnnuallyModalFactor.put(ModalFactorItem.SEMI_ANNUAL, new BigDecimal("0.5012"));
        List<Map<ModalFactorItem, BigDecimal>> modalFactorList = Lists.newArrayList(monthlyModalFactor, quarterlyModalFactor, semiAnnuallyModalFactor);
        organizationGeneralInformation = organizationGeneralInformation.withModalFactorOrganizationInformation(modalFactorList);
        PremiumCalculationDto premiumCalculationDto = new PremiumCalculationDto(planId, premiumSetupDate.plusDays(1), PremiumFrequency.MONTHLY, 365);
        premiumCalculationDto = premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.SUM_ASSURED, "55000");
        premiumCalculationDto = premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.AGE, "22");
        premiumCalculationDto = premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.POLICY_TERM, "365");
        premiumCalculationDto = premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.PREMIUM_PAYMENT_TERM, "365");
        premiumCalculationDto = premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.GENDER, Gender.FEMALE.name());
        Premium premium = createPremium(PremiumFactor.PER_THOUSAND, PremiumRateFrequency.YEARLY, "premiumsetupdatawithpremiumrate.xls");
        when(premiumFinder.findPremium(premiumCalculationDto)).thenReturn(premium);
        when(organizationGeneralInformationRepository.findAll()).thenReturn(Lists.newArrayList(organizationGeneralInformation));
        List<ComputedPremiumDto> computedPremiumDtoList = premiumCalculator.calculateBasicPremium(premiumCalculationDto);
        assertEquals(new BigDecimal("1237.50"), ComputedPremiumDto.getAnnualPremium(computedPremiumDtoList));
        assertEquals(new BigDecimal("620.235000"), ComputedPremiumDto.getSemiAnnualPremium(computedPremiumDtoList));
        assertEquals(new BigDecimal("315.810000"), ComputedPremiumDto.getQuarterlyPremium(computedPremiumDtoList));
        assertEquals(new BigDecimal("108.281250"), ComputedPremiumDto.getMonthlyPremium(computedPremiumDtoList));
    }

    /**
     * Given : Premium With PremiumFrequency As Monthly And PremiumFactor As FlatAmount
     * When  : Influencing Factors are SumAssured,Age,Gender,Premium Payment Term and Policy Term
     * Then  : Should calculate premium by Applying Discount Factor
     * Annual Discount Factor : 0.4321;Annual Premium = Monthly Premium *Annual Discount Factor
     * Semi-Annual Discount Factor : 0.3232;Semi-Annual Premium = Monthly Premium*Monthly Discount Factor
     * Quarterly Discount Factor : 0.2232;Quarterly Premium = Monthly Premium * Quarterly Discount Factor
     */
    @Test
    public void givenPremiumWithFlatAmountItShouldCalculatePremiumWithPremiumInfluencingFactorSumAssuredAgePolicyTermByApplyingDiscountFactor() throws IOException {
        PremiumCalculationDto premiumCalculationDto = new PremiumCalculationDto(planId, premiumSetupDate.plusDays(1), PremiumFrequency.MONTHLY, 365);
        premiumCalculationDto = premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.SUM_ASSURED, "55000");
        premiumCalculationDto = premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.AGE, "22");
        premiumCalculationDto = premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.POLICY_TERM, "365");
        premiumCalculationDto = premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.PREMIUM_PAYMENT_TERM, "365");
        premiumCalculationDto = premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.GENDER, Gender.FEMALE.name());
        Premium premium = createPremium(PremiumFactor.FLAT_AMOUNT, PremiumRateFrequency.MONTHLY, "premiumsetupdatawithflatpremium.xls");
        when(premiumFinder.findPremium(premiumCalculationDto)).thenReturn(premium);
        when(organizationGeneralInformationRepository.findAll()).thenReturn(Lists.newArrayList(organizationGeneralInformation));
        List<ComputedPremiumDto> computedPremiumDtoList = premiumCalculator.calculateBasicPremium(premiumCalculationDto);
        assertEquals(new BigDecimal("2160.71605"), ComputedPremiumDto.getAnnualPremium(computedPremiumDtoList));
        assertEquals(new BigDecimal("1616.16160"), ComputedPremiumDto.getSemiAnnualPremium(computedPremiumDtoList));
        assertEquals(new BigDecimal("5000.5"), ComputedPremiumDto.getMonthlyPremium(computedPremiumDtoList));
        assertEquals(new BigDecimal("1116.11160"), ComputedPremiumDto.getQuarterlyPremium(computedPremiumDtoList));

    }


    /**
     * Given : Premium With PremiumFrequency As Monthly And PremiumFactor As FlatAmount
     * When  : Influencing Factors are SumAssured,Age,Gender,Premium Payment Term and Policy Term And No of Days!=365
     * Then  : Should calculate premium by Applying Discount Factor
     * Annual Discount Factor : 0.4321;Annual Premium = Monthly Premium *Annual Discount Factor
     * Semi-Annual Discount Factor : 0.3232;Semi-Annual Premium = Monthly Premium*Monthly Discount Factor
     * Quarterly Discount Factor : 0.2232;Quarterly Premium = Monthly Premium * Quarterly Discount Factor
     */
    @Test
    public void givenPremiumWithFlatAmountItShouldCalculatePremiumOnProrateBasisWithPremiumInfluencingFactorSumAssuredAgePolicyTermByApplyingDiscountFactor() throws IOException {
        PremiumCalculationDto premiumCalculationDto = new PremiumCalculationDto(planId, premiumSetupDate.plusDays(1), PremiumFrequency.MONTHLY, 275);
        premiumCalculationDto = premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.SUM_ASSURED, "55000");
        premiumCalculationDto = premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.AGE, "22");
        premiumCalculationDto = premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.POLICY_TERM, "365");
        premiumCalculationDto = premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.PREMIUM_PAYMENT_TERM, "365");
        premiumCalculationDto = premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.GENDER, Gender.FEMALE.name());
        Premium premium = createPremium(PremiumFactor.FLAT_AMOUNT, PremiumRateFrequency.MONTHLY, "premiumsetupdatawithflatpremium.xls");
        when(premiumFinder.findPremium(premiumCalculationDto)).thenReturn(premium);
        when(organizationGeneralInformationRepository.findAll()).thenReturn(Lists.newArrayList(organizationGeneralInformation));
        List<ComputedPremiumDto> computedPremiumDtoList = premiumCalculator.calculateBasicPremium(premiumCalculationDto);
        assertEquals(new BigDecimal("45837.9350"), ComputedPremiumDto.getMonthlyPremium(computedPremiumDtoList));
    }


    /**
     * Given : Premium With PremiumFrequency As Monthly And PremiumFactor As Per Thousand
     * When  : Influencing Factors are SumAssured,Age,Gender,Premium Payment Term and Policy Term
     * Then  : Should calculate premium by Applying Discount Factor
     * Annual Discount Factor : 0.9205;Annual Premium = Monthly Premium *Annual Discount Factor
     * Semi-Annual Discount Factor : 0.9427;Semi-Annual Premium = Monthly Premium*Monthly Discount Factor
     * Quarterly Discount Factor : 0.9576;Quarterly Premium = Monthly Premium * Quarterly Discount Factor
     */
    @Test
    public void givenPremiumWithPremiumFactorPerThousandFlatAmountItShouldCalculatePremiumWithPremiumInfluencingFactorSumAssuredAgePolicyTermByApplyingDiscountFactor() throws IOException {
        Map<DiscountFactorItem, BigDecimal> annualDiscountFactor = Maps.newHashMap();
        annualDiscountFactor.put(DiscountFactorItem.ANNUAL, new BigDecimal("0.9205"));
        Map<DiscountFactorItem, BigDecimal> quarterlyDiscountFactor = Maps.newHashMap();
        quarterlyDiscountFactor.put(DiscountFactorItem.QUARTERLY, new BigDecimal("0.9576"));
        Map<DiscountFactorItem, BigDecimal> semiAnnualDiscountFactor = Maps.newHashMap();
        semiAnnualDiscountFactor.put(DiscountFactorItem.SEMI_ANNUAL, new BigDecimal("0.9427"));
        List<Map<DiscountFactorItem, BigDecimal>> discountFactorList = Lists.newArrayList(annualDiscountFactor, semiAnnualDiscountFactor, quarterlyDiscountFactor);
        organizationGeneralInformation = organizationGeneralInformation.withDiscountFactorOrganizationInformation(discountFactorList);
        PremiumCalculationDto premiumCalculationDto = new PremiumCalculationDto(planId, premiumSetupDate.plusDays(1), PremiumFrequency.MONTHLY, 365);
        premiumCalculationDto = premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.SUM_ASSURED, "55000");
        premiumCalculationDto = premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.AGE, "22");
        premiumCalculationDto = premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.POLICY_TERM, "365");
        premiumCalculationDto = premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.PREMIUM_PAYMENT_TERM, "365");
        premiumCalculationDto = premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.GENDER, Gender.FEMALE.name());
        Premium premium = createPremium(PremiumFactor.PER_THOUSAND, PremiumRateFrequency.MONTHLY, "premiumsetupdatawithpremiumrate.xls");
        when(premiumFinder.findPremium(premiumCalculationDto)).thenReturn(premium);
        when(organizationGeneralInformationRepository.findAll()).thenReturn(Lists.newArrayList(organizationGeneralInformation));
        List<ComputedPremiumDto> computedPremiumDtoList = premiumCalculator.calculateBasicPremium(premiumCalculationDto);
        assertEquals(new BigDecimal("1139.118750"), ComputedPremiumDto.getAnnualPremium(computedPremiumDtoList));
        assertEquals(new BigDecimal("1185.030000"), ComputedPremiumDto.getQuarterlyPremium(computedPremiumDtoList));
        assertEquals(new BigDecimal("1166.591250"), ComputedPremiumDto.getSemiAnnualPremium(computedPremiumDtoList));
        assertEquals(new BigDecimal("1237.50"), ComputedPremiumDto.getMonthlyPremium(computedPremiumDtoList));

    }

    /**
     * Given : Premium With PremiumFrequency As Yearly And PremiumFactor As FlatAmount
     * When  : No premium found for Influencing Factors such as SumAssured,Age,Premium Payment Term and Policy Term
     * Then  : Should throw {@link com.pla.core.domain.exception.PremiumException#raisePremiumNotFoundException()}
     */
    @Test(expected = PremiumException.class)
    public void givenPremiumItShouldCalculatePremiumWithNoPremiumFoundItShouldThrowException() throws IOException {
        PremiumCalculationDto premiumCalculationDto = new PremiumCalculationDto(planId, premiumSetupDate.plusDays(1), PremiumFrequency.MONTHLY, 365);
        premiumCalculationDto = premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.SUM_ASSURED, "550001");
        premiumCalculationDto = premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.AGE, "23232");
        premiumCalculationDto = premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.POLICY_TERM, "36335");
        premiumCalculationDto = premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.PREMIUM_PAYMENT_TERM, "36335");
        premiumCalculationDto = premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.GENDER, "36335");
        Premium premium = createPremium(PremiumFactor.FLAT_AMOUNT, PremiumRateFrequency.YEARLY, "premiumsetupdatawithflatpremium.xls");
        when(premiumFinder.findPremium(premiumCalculationDto)).thenReturn(premium);
        when(organizationGeneralInformationRepository.findAll()).thenReturn(Lists.newArrayList(organizationGeneralInformation));
        premiumCalculator.calculateBasicPremium(premiumCalculationDto);
    }

    /**
     * Given : Premium With Influencing factors {@link com.pla.publishedlanguage.domain.model.PremiumInfluencingFactor#SUM_ASSURED,
     *
     * @link com.pla.publishedlanguage.domain.model.PremiumInfluencingFactor#AGE,
     * @link com.pla.publishedlanguage.domain.model.PremiumInfluencingFactor#GENDER,
     * @link com.pla.publishedlanguage.domain.model.PremiumInfluencingFactor#POLICY_TERM,
     * @link com.pla.publishedlanguage.domain.model.PremiumInfluencingFactor#PREMIUM_PAYMENT_TERM}
     * <p/>
     * When  : Influencing factors for premium calculation are {@link com.pla.publishedlanguage.domain.model.PremiumInfluencingFactor#SUM_ASSURED,
     * @link com.pla.publishedlanguage.domain.model.PremiumInfluencingFactor#AGE,
     * @link com.pla.publishedlanguage.domain.model.PremiumInfluencingFactor#GENDER,
     * @link com.pla.publishedlanguage.domain.model.PremiumInfluencingFactor#POLICY_TERM}
     * <p/>
     * Then  : Should throw {@link com.pla.core.domain.exception.PremiumException#raiseInfluencingFactorMismatchException()} }
     */
    @Test(expected = PremiumException.class)
    public void givenPremiumWithInfluencingFactorsWhenInfluencingFactorForPremiumCalculationMismatchItShouldThrowInfluencingFactorMismatchException() throws IOException {
        PremiumCalculationDto premiumCalculationDto = new PremiumCalculationDto(planId, premiumSetupDate.plusDays(1), PremiumFrequency.MONTHLY, 365);
        premiumCalculationDto = premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.SUM_ASSURED, "550001");
        premiumCalculationDto = premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.AGE, "22");
        premiumCalculationDto = premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.POLICY_TERM, "365");
        premiumCalculationDto = premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.GENDER, "365");
        Premium premium = createPremium(PremiumFactor.FLAT_AMOUNT, PremiumRateFrequency.YEARLY, "premiumsetupdatawithflatpremium.xls");
        when(premiumFinder.findPremium(premiumCalculationDto)).thenReturn(premium);
        when(organizationGeneralInformationRepository.findAll()).thenReturn(Lists.newArrayList(organizationGeneralInformation));
        premiumCalculator.calculateBasicPremium(premiumCalculationDto);
    }

    private Premium createPremium(PremiumFactor premiumFactor, PremiumRateFrequency premiumRateFrequency, String premiumSetUpFileName) throws IOException {
        PremiumTemplateParser premiumTemplateParser = new PremiumTemplateParser(masterFinder);
        InputStream inputStream = PremiumCalculatorUnitTest.class.getClassLoader().getResourceAsStream("testdata/endtoend/premium/" + premiumSetUpFileName);
        List<PremiumInfluencingFactor> premiumInfluencingFactorList = Lists.newArrayList(PremiumInfluencingFactor.SUM_ASSURED, PremiumInfluencingFactor.AGE, PremiumInfluencingFactor.POLICY_TERM, PremiumInfluencingFactor.PREMIUM_PAYMENT_TERM, PremiumInfluencingFactor.GENDER);
        premiumExcelLineItems = premiumTemplateParser.parseAndTransformToPremiumData(new HSSFWorkbook(inputStream), premiumInfluencingFactorList);
        PremiumId premiumId = new PremiumId("1");
        return Premium.createPremiumWithPlan(premiumId, planId, premiumSetupDate, premiumExcelLineItems, premiumFactor, premiumRateFrequency, premiumInfluencingFactorList);

    }
}
