package com.pla.grouphealth.sharedresource.service;

import com.google.common.collect.Lists;
import com.pla.core.domain.model.generalinformation.ModelFactorOrganizationInformation;
import com.pla.core.domain.model.generalinformation.ProductLineGeneralInformation;
import com.pla.grouphealth.sharedresource.dto.GHInsuredDto;
import com.pla.grouphealth.sharedresource.dto.GHPremiumDetailDto;
import com.pla.grouphealth.sharedresource.model.vo.*;
import com.pla.grouphealth.sharedresource.query.GHFinder;
import com.pla.publishedlanguage.contract.IGeneralInformationProvider;
import com.pla.publishedlanguage.contract.IPremiumCalculator;
import com.pla.publishedlanguage.domain.model.ComputedPremiumDto;
import com.pla.publishedlanguage.domain.model.PremiumCalculationDto;
import com.pla.publishedlanguage.domain.model.PremiumFrequency;
import com.pla.publishedlanguage.domain.model.PremiumInfluencingFactor;
import com.pla.publishedlanguage.dto.AgentLoadingFactorDto;
import com.pla.sharedkernel.identifier.CoverageId;
import com.pla.sharedkernel.identifier.LineOfBusinessEnum;
import com.pla.sharedkernel.identifier.PlanId;
import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static org.nthdimenzion.presentation.AppUtils.getAgeOnNextBirthDate;
import static org.nthdimenzion.utils.UtilValidator.isEmpty;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Created by Samir on 6/24/2015.
 */
@Component
public class GHInsuredFactory {

    private IPremiumCalculator premiumCalculator;

    private IGeneralInformationProvider generalInformationProvider;

    private GHFinder ghFinder;

    @Autowired
    private MongoTemplate mongoTemplate;


    @Autowired
    public GHInsuredFactory(IPremiumCalculator premiumCalculator, IGeneralInformationProvider generalInformationProvider, GHFinder ghFinder) {
        this.premiumCalculator = premiumCalculator;
        this.generalInformationProvider = generalInformationProvider;
        this.ghFinder = ghFinder;
    }

    public Set<GHInsured> createInsuredDetail(List<GHInsuredDto> insuredDtos) {
        Set<GHInsured> insureds = insuredDtos.stream().map(new Function<GHInsuredDto, GHInsured>() {
            @Override
            public GHInsured apply(GHInsuredDto insuredDto) {
                GHInsuredDto.GHPlanPremiumDetailDto premiumDetail = insuredDto.getPlanPremiumDetail();
                List<ComputedPremiumDto> computedPremiumDtos = Lists.newArrayList();
                String occupationClass = ghFinder.getOccupationClass(insuredDto.getOccupationClass());
                BigDecimal basicAnnualPremium = premiumDetail.getPremiumAmount() != null ? premiumDetail.getPremiumAmount() :
                        computePlanBasicAnnualPremium(premiumDetail.getPlanId(), premiumDetail.getSumAssured().toPlainString(),
                                String.valueOf(getAgeOnNextBirthDate(insuredDto.getDateOfBirth())), occupationClass, insuredDto.getGender().name(), 365, null,computedPremiumDtos);
                if (insuredDto.getNoOfAssured() == null && insuredDto.getDateOfBirth() != null && premiumDetail.getPremiumAmount()== null) {
                    int ageOnNextBirthDate = getAgeOnNextBirthDate(insuredDto.getDateOfBirth());
                    basicAnnualPremium = computePremiumByApplyingAgeLoadingFactor(ageOnNextBirthDate, basicAnnualPremium);
                }
                if (insuredDto.getNoOfAssured() != null && isEmpty(computedPremiumDtos)){
                    Set<ModelFactorOrganizationInformation> modelFactorItems =  getModalFactor();
                    ComputedPremiumDto semiAnnualPremium = new ComputedPremiumDto(PremiumFrequency.SEMI_ANNUALLY,basicAnnualPremium.multiply(ModelFactorOrganizationInformation.getSemiAnnualModalFactor(modelFactorItems)));
                    ComputedPremiumDto quarterlyPremium = new ComputedPremiumDto(PremiumFrequency.QUARTERLY,basicAnnualPremium.multiply(ModelFactorOrganizationInformation.getQuarterlyModalFactor(modelFactorItems)));
                    ComputedPremiumDto monthlyPremium = new ComputedPremiumDto(PremiumFrequency.MONTHLY,basicAnnualPremium.multiply(ModelFactorOrganizationInformation.getMonthlyModalFactor(modelFactorItems)));
                    computedPremiumDtos.add(semiAnnualPremium);
                    computedPremiumDtos.add(quarterlyPremium);
                    computedPremiumDtos.add(monthlyPremium);
                }
                final GHInsuredBuilder[] insuredBuilder = {GHInsured.getInsuredBuilder(new PlanId(premiumDetail.getPlanId()), premiumDetail.getPlanCode(), basicAnnualPremium, premiumDetail.getSumAssured(),computedPremiumDtos)};
                insuredBuilder[0].withCategory(insuredDto.getOccupationCategory()).withInsuredName(insuredDto.getSalutation(), insuredDto.getFirstName(), insuredDto.getLastName())
                        .withOccupation(insuredDto.getOccupationClass()).withInsuredNrcNumber(insuredDto.getNrcNumber()).withCompanyName(insuredDto.getCompanyName())
                        .withManNumber(insuredDto.getManNumber()).withDateOfBirth(insuredDto.getDateOfBirth()).
                        withGender(insuredDto.getGender()).withMinAndMaxAge(insuredDto.getMinAgeEntry(), insuredDto.getMaxAgeEntry())
                        .withExistingIllness(insuredDto.getExistingIllness()).withNoOfAssured(insuredDto.getNoOfAssured()).withPremiumType(insuredDto.getPremiumType()).withRateOfPremium(insuredDto.getRateOfPremium());
                insuredDto.getCoveragePremiumDetails().forEach(coveragePremiumDetail -> {
                    BigDecimal coverageBasicPremium = coveragePremiumDetail.getPremium() == null ? computePlanBasicAnnualPremium(premiumDetail.getPlanId(), coveragePremiumDetail.getSumAssured().toPlainString(), String.valueOf(getAgeOnNextBirthDate(insuredDto.getDateOfBirth())), occupationClass, insuredDto.getGender().name(), 365, coveragePremiumDetail.getCoverageId(),null) : coveragePremiumDetail.getPremium();
                    if (insuredDto.getNoOfAssured() == null && insuredDto.getDateOfBirth() != null  && premiumDetail.getPremiumAmount()== null) {
                        int ageOnNextBirthDate = getAgeOnNextBirthDate(insuredDto.getDateOfBirth());
                        coverageBasicPremium = computePremiumByApplyingAgeLoadingFactor(ageOnNextBirthDate, coverageBasicPremium);
                    }
                    final GHCoveragePremiumDetailBuilder[] ghCoveragePremiumDetailBuilder = {new GHCoveragePremiumDetailBuilder(coveragePremiumDetail.getCoverageCode(), coveragePremiumDetail.getCoverageId(), coveragePremiumDetail.getCoverageName(), coverageBasicPremium, coveragePremiumDetail.getPremiumVisibility(), coveragePremiumDetail.getSumAssured())};
                    if (isNotEmpty(coveragePremiumDetail.getBenefitDetails())) {
                        coveragePremiumDetail.getBenefitDetails().forEach(benefitDetail -> {
                            ghCoveragePremiumDetailBuilder[0] = ghCoveragePremiumDetailBuilder[0].withBenefit(benefitDetail.getBenefitCode(), benefitDetail.getBenefitId(), benefitDetail.getBenefitLimit());
                        });
                    }
                    insuredBuilder[0] = insuredBuilder[0].withCoveragePremiumDetail(ghCoveragePremiumDetailBuilder[0]);

                });
                Set<GHInsuredDependent> insuredDependents = getInsuredDependent(insuredDto.getInsuredDependents());
                insuredBuilder[0] = insuredBuilder[0].withDependents(insuredDependents);
                return insuredBuilder[0].build();
            }
        }).collect(Collectors.toSet());
        return insureds;
    }

    public Set<ModelFactorOrganizationInformation> getModalFactor(){
        Criteria policyCriteria = Criteria.where("productLine").is(LineOfBusinessEnum.GROUP_HEALTH.name());
        Query query = new Query(policyCriteria);
        List<ProductLineGeneralInformation> productLineInformation = mongoTemplate.find(query, ProductLineGeneralInformation.class);
        checkArgument(productLineInformation != null, "Product Line Information SetUp not available");
        Set<ModelFactorOrganizationInformation> modelFactorItems = productLineInformation.get(0).getModalFactorProcessInformation().getModelFactorItems();
        return modelFactorItems;
    }

    public Set<GHInsured> recalculateProratePremiumForInsureds(GHPremiumDetailDto premiumDetailDto, Set<GHInsured> insureds) {
        for (GHInsured insured : insureds) {
            List<ComputedPremiumDto> computedPremiumDtos = Lists.newArrayList();
            GHPlanPremiumDetail planPremiumDetail = insured.getPlanPremiumDetail();
            String occupationClass = ghFinder.getOccupationClass(insured.getOccupationClass());
            BigDecimal insuredPlanProratePremium = insured.getNoOfAssured() != null ? insured.getPlanPremiumDetail().getPremiumAmount() :
                    premiumDetailDto.getPolicyTermValue() != 365 ?
                            computeBasicProratePremium(planPremiumDetail.getPlanId().getPlanId(), planPremiumDetail.getSumAssured().toPlainString(),
                                    getAgeOnNextBirthDate(insured.getDateOfBirth()).toString(), occupationClass, insured.getGender().name(), premiumDetailDto.getPolicyTermValue(), null) : computePlanBasicAnnualPremium(planPremiumDetail.getPlanId().getPlanId(), planPremiumDetail.getSumAssured().toPlainString(),
                            getAgeOnNextBirthDate(insured.getDateOfBirth()).toString(), occupationClass, insured.getGender().name(), premiumDetailDto.getPolicyTermValue(), null,computedPremiumDtos);
            if (insured.getNoOfAssured() == null && insured.getDateOfBirth() != null && insured.getPlanPremiumDetail().getPremiumAmount() == null) {
                insuredPlanProratePremium = computePremiumByApplyingAgeLoadingFactor(getAgeOnNextBirthDate(insured.getDateOfBirth()), insuredPlanProratePremium);
            }
            if (insured.getNoOfAssured() != null && isEmpty(computedPremiumDtos)){
                Set<ModelFactorOrganizationInformation> modelFactorItems =  getModalFactor();
                planPremiumDetail.updatePremiumAmount(insuredPlanProratePremium.multiply(ModelFactorOrganizationInformation.getSemiAnnualModalFactor(modelFactorItems)),insuredPlanProratePremium.multiply(ModelFactorOrganizationInformation.getSemiAnnualModalFactor(modelFactorItems)),insuredPlanProratePremium.multiply(ModelFactorOrganizationInformation.getMonthlyModalFactor(modelFactorItems)));
            }
            if (premiumDetailDto.getPolicyTermValue() == 365 && isNotEmpty(computedPremiumDtos)){
                planPremiumDetail.updatePremiumAmount(ComputedPremiumDto.getSemiAnnualPremium(computedPremiumDtos),ComputedPremiumDto.getQuarterlyPremium(computedPremiumDtos),ComputedPremiumDto.getMonthlyPremium(computedPremiumDtos));
            }
            insured.updatePlanPremiumAmount(insuredPlanProratePremium);
            if (isNotEmpty(insured.getPlanPremiumDetail().getCoveragePremiumDetails())) {
                List<GHCoveragePremiumDetail> coveragePremiumDetails = insured.getPlanPremiumDetail().getCoveragePremiumDetails();
                for (GHCoveragePremiumDetail coveragePremiumDetail : coveragePremiumDetails) {
                    BigDecimal insuredCoveragePremiumDetail = insured.getNoOfAssured() != null ? coveragePremiumDetail.getPremium() : premiumDetailDto.getPolicyTermValue() != 365 ? computeBasicProratePremium(planPremiumDetail.getPlanId().getPlanId(),
                            coveragePremiumDetail.getSumAssured().toPlainString(), getAgeOnNextBirthDate(insured.getDateOfBirth()).toString(),
                            occupationClass, insured.getGender().name(), premiumDetailDto.getPolicyTermValue(), coveragePremiumDetail.getCoverageId().getCoverageId()) : computePlanBasicAnnualPremium(planPremiumDetail.getPlanId().getPlanId(), planPremiumDetail.getSumAssured().toPlainString(),
                            getAgeOnNextBirthDate(insured.getDateOfBirth()).toString(), occupationClass, insured.getGender().name(), premiumDetailDto.getPolicyTermValue(), null,null);
                    if (insured.getNoOfAssured() == null && insured.getDateOfBirth() != null && insured.getPlanPremiumDetail().getPremiumAmount() == null) {
                        insuredCoveragePremiumDetail = computePremiumByApplyingAgeLoadingFactor(getAgeOnNextBirthDate(insured.getDateOfBirth()), insuredCoveragePremiumDetail);
                    }
                    coveragePremiumDetail.updateWithPremium(insuredCoveragePremiumDetail);
                }
            }
            for (GHInsuredDependent insuredDependent : insured.getInsuredDependents()) {
                GHPlanPremiumDetail insuredDependentPlanPremiumDetail = insuredDependent.getPlanPremiumDetail();
                List<ComputedPremiumDto> computedPremiumDtosDep  = Lists.newArrayList();
                String dependentOccupationClass = ghFinder.getOccupationClass(insuredDependent.getOccupationClass());
                BigDecimal insuredDependentPlanProratePremium = insuredDependent.getNoOfAssured() != null ? insuredDependent.getPlanPremiumDetail().getPremiumAmount() :  premiumDetailDto.getPolicyTermValue() != 365 ?
                        computeBasicProratePremium(insuredDependentPlanPremiumDetail.getPlanId().getPlanId(), insuredDependentPlanPremiumDetail.getSumAssured().toPlainString(),
                                getAgeOnNextBirthDate(insuredDependent.getDateOfBirth()).toString(), dependentOccupationClass, insuredDependent.getGender().name(),
                                premiumDetailDto.getPolicyTermValue(), null):computePlanBasicAnnualPremium(insuredDependentPlanPremiumDetail.getPlanId().getPlanId(), insuredDependentPlanPremiumDetail.getSumAssured().toPlainString(),
                        getAgeOnNextBirthDate(insuredDependent.getDateOfBirth()).toString(), dependentOccupationClass, insuredDependent.getGender().name(),
                        premiumDetailDto.getPolicyTermValue(), null,computedPremiumDtosDep);
                if (insuredDependent.getNoOfAssured() == null && insuredDependent.getDateOfBirth() != null) {
                    insuredDependentPlanProratePremium = computePremiumByApplyingAgeLoadingFactor(getAgeOnNextBirthDate(insuredDependent.getDateOfBirth()), insuredDependentPlanProratePremium);
                }
                insuredDependent.updatePlanPremiumAmount(insuredDependentPlanProratePremium);
                if (insured.getNoOfAssured() != null){
                    Set<ModelFactorOrganizationInformation> modelFactorItems =  getModalFactor();
                    insuredDependentPlanPremiumDetail.updatePremiumAmount(insuredDependentPlanProratePremium.multiply(ModelFactorOrganizationInformation.getSemiAnnualModalFactor(modelFactorItems)),insuredDependentPlanProratePremium.multiply(ModelFactorOrganizationInformation.getQuarterlyModalFactor(modelFactorItems)),insuredDependentPlanProratePremium.multiply(ModelFactorOrganizationInformation.getMonthlyModalFactor(modelFactorItems)));
                }
                if (premiumDetailDto.getPolicyTermValue() == 365){
                    insuredDependentPlanPremiumDetail.updatePremiumAmount(ComputedPremiumDto.getSemiAnnualPremium(computedPremiumDtosDep),ComputedPremiumDto.getQuarterlyPremium(computedPremiumDtosDep),ComputedPremiumDto.getMonthlyPremium(computedPremiumDtosDep));
                }
                List<GHCoveragePremiumDetail> insuredDependentCoveragePremiumDetails = insuredDependent.getPlanPremiumDetail().getCoveragePremiumDetails();
                if (isNotEmpty(insuredDependentCoveragePremiumDetails)) {
                    for (GHCoveragePremiumDetail insuredDependentCoveragePremiumDetail : insuredDependentCoveragePremiumDetails) {
                        BigDecimal insuredCoveragePremiumDetail = insuredDependent.getNoOfAssured() != null ? insuredDependentCoveragePremiumDetail.getPremium() :
                                premiumDetailDto.getPolicyTermValue() != 365 ?computeBasicProratePremium(insuredDependentPlanPremiumDetail.getPlanId().getPlanId(), insuredDependentCoveragePremiumDetail.getSumAssured().toPlainString(), getAgeOnNextBirthDate(insuredDependent.getDateOfBirth()).toString(), dependentOccupationClass, insuredDependent.getGender().name(), premiumDetailDto.getPolicyTermValue(), insuredDependentCoveragePremiumDetail.getCoverageId().getCoverageId()):
                                        computePlanBasicAnnualPremium(insuredDependentPlanPremiumDetail.getPlanId().getPlanId(), insuredDependentCoveragePremiumDetail.getSumAssured().toPlainString(), getAgeOnNextBirthDate(insuredDependent.getDateOfBirth()).toString(), dependentOccupationClass, insuredDependent.getGender().name(), premiumDetailDto.getPolicyTermValue(), insuredDependentCoveragePremiumDetail.getCoverageId().getCoverageId(),null);
                        if (insuredDependent.getNoOfAssured() == null && insuredDependent.getDateOfBirth() != null) {
                            insuredCoveragePremiumDetail = computePremiumByApplyingAgeLoadingFactor(getAgeOnNextBirthDate(insuredDependent.getDateOfBirth()), insuredCoveragePremiumDetail);
                        }
                        insuredDependentCoveragePremiumDetail.updateWithPremium(insuredCoveragePremiumDetail);
                    }
                }
            }
        }
        return insureds;
    }

    private BigDecimal computeBasicProratePremium(String planId, String sumAssured, String age, String occupationClass, String gender, int noOfDays, String coverageId) {
        List<PremiumInfluencingFactor> setUpPremiumInfluencingFactors = getPremiumInfluencingFactors(planId, coverageId, LocalDate.now());
        PremiumCalculationDto premiumCalculationDto = new PremiumCalculationDto(new PlanId(planId), LocalDate.now(), PremiumFrequency.ANNUALLY, noOfDays);
        if (findPremiumInfluencingFactor(PremiumInfluencingFactor.SUM_ASSURED, setUpPremiumInfluencingFactors) != null) {
            premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.SUM_ASSURED, sumAssured);
        }
        if (findPremiumInfluencingFactor(PremiumInfluencingFactor.AGE, setUpPremiumInfluencingFactors) != null) {
            premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.AGE, age);
        }
        if (isNotEmpty(occupationClass) && findPremiumInfluencingFactor(PremiumInfluencingFactor.OCCUPATION_CLASS, setUpPremiumInfluencingFactors) != null) {
            premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.OCCUPATION_CLASS, occupationClass);
        }
        if (findPremiumInfluencingFactor(PremiumInfluencingFactor.GENDER, setUpPremiumInfluencingFactors) != null) {
            premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.GENDER, gender);
        }
        if (findPremiumInfluencingFactor(PremiumInfluencingFactor.POLICY_TERM, setUpPremiumInfluencingFactors) != null) {
            premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.POLICY_TERM, String.valueOf(1));
        }
        if (findPremiumInfluencingFactor(PremiumInfluencingFactor.PREMIUM_PAYMENT_TERM, setUpPremiumInfluencingFactors) != null) {
            premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.PREMIUM_PAYMENT_TERM, String.valueOf(1));
        }
        if (isNotEmpty(coverageId)) {
            premiumCalculationDto = premiumCalculationDto.addCoverage(new CoverageId(coverageId));
        }
        BigDecimal installmentPremium = premiumCalculator.computeProratePremium(premiumCalculationDto,new BigDecimal(sumAssured));
        return installmentPremium;
    }


    private BigDecimal computePlanBasicAnnualPremium(String planId, String sumAssured, String age, String occupationClass, String gender, int noOfDays, String coverageId,List<ComputedPremiumDto> computedPremiumDtos) {
        List<PremiumInfluencingFactor> setUpPremiumInfluencingFactors = getPremiumInfluencingFactors(planId, coverageId, LocalDate.now());
        PremiumCalculationDto premiumCalculationDto = new PremiumCalculationDto(new PlanId(planId), LocalDate.now(), PremiumFrequency.ANNUALLY, noOfDays);
        if (findPremiumInfluencingFactor(PremiumInfluencingFactor.SUM_ASSURED, setUpPremiumInfluencingFactors) != null) {
            premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.SUM_ASSURED, sumAssured);
        }
        if (findPremiumInfluencingFactor(PremiumInfluencingFactor.AGE, setUpPremiumInfluencingFactors) != null) {
            premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.AGE, age);
        }
        if (isNotEmpty(occupationClass) && findPremiumInfluencingFactor(PremiumInfluencingFactor.OCCUPATION_CLASS, setUpPremiumInfluencingFactors) != null) {
            premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.OCCUPATION_CLASS, occupationClass);
        }
        if (findPremiumInfluencingFactor(PremiumInfluencingFactor.GENDER, setUpPremiumInfluencingFactors) != null) {
            premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.GENDER, gender);
        }
        if (findPremiumInfluencingFactor(PremiumInfluencingFactor.POLICY_TERM, setUpPremiumInfluencingFactors) != null) {
            premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.POLICY_TERM, String.valueOf(1));
        }
        if (findPremiumInfluencingFactor(PremiumInfluencingFactor.PREMIUM_PAYMENT_TERM, setUpPremiumInfluencingFactors) != null) {
            premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.PREMIUM_PAYMENT_TERM, String.valueOf(1));
        }
        if (isNotEmpty(coverageId)) {
            premiumCalculationDto = premiumCalculationDto.addCoverage(new CoverageId(coverageId));
        }
        List<ComputedPremiumDto> computedPremiums = premiumCalculator.calculateBasicPremium(premiumCalculationDto, new BigDecimal(sumAssured).setScale(0,BigDecimal.ROUND_FLOOR),LineOfBusinessEnum.GROUP_HEALTH, Boolean.FALSE, StringUtils.EMPTY);
        computedPremiums.parallelStream().map(computePremium->{
            computedPremiumDtos.add(computePremium);
            return computePremium;
        }).collect(Collectors.toList());
        BigDecimal annualBasicPremium = ComputedPremiumDto.getAnnualPremium(computedPremiums);
        return annualBasicPremium;
    }

    private Set<GHInsuredDependent> getInsuredDependent(Set<GHInsuredDto.GHInsuredDependentDto> insuredDependentDtos) {
        Set<GHInsuredDependent> insuredDependents = insuredDependentDtos.stream().map(new Function<GHInsuredDto.GHInsuredDependentDto, GHInsuredDependent>() {
            @Override
            public GHInsuredDependent apply(GHInsuredDto.GHInsuredDependentDto insuredDependentDto) {
                GHInsuredDto.GHPlanPremiumDetailDto premiumDetail = insuredDependentDto.getPlanPremiumDetail();
                List<ComputedPremiumDto> computedPremiumDtos = Lists.newArrayList();
                String occupationClass = ghFinder.getOccupationClass(insuredDependentDto.getOccupationClass());
                BigDecimal basicAnnualPremium = premiumDetail.getPremiumAmount() != null ? premiumDetail.getPremiumAmount() : computePlanBasicAnnualPremium(premiumDetail.getPlanId(), premiumDetail.getSumAssured().toPlainString(),
                        String.valueOf(getAgeOnNextBirthDate(insuredDependentDto.getDateOfBirth()))
                        , occupationClass, insuredDependentDto.getGender().name(), 365, null,computedPremiumDtos);
                if (insuredDependentDto.getNoOfAssured() == null && insuredDependentDto.getDateOfBirth() != null) {
                    int ageOnNextBirthDate = getAgeOnNextBirthDate(insuredDependentDto.getDateOfBirth());
                    basicAnnualPremium = computePremiumByApplyingAgeLoadingFactor(ageOnNextBirthDate, basicAnnualPremium);
                }
                if (insuredDependentDto.getNoOfAssured() != null && isEmpty(computedPremiumDtos)){
                    Set<ModelFactorOrganizationInformation> modelFactorItems =  getModalFactor();
                    ComputedPremiumDto semiAnnualPremium = new ComputedPremiumDto(PremiumFrequency.SEMI_ANNUALLY,basicAnnualPremium.multiply(ModelFactorOrganizationInformation.getSemiAnnualModalFactor(modelFactorItems)));
                    ComputedPremiumDto quarterlyPremium = new ComputedPremiumDto(PremiumFrequency.QUARTERLY,basicAnnualPremium.multiply(ModelFactorOrganizationInformation.getQuarterlyModalFactor(modelFactorItems)));
                    computedPremiumDtos.add(semiAnnualPremium);
                    computedPremiumDtos.add(quarterlyPremium);
                    computedPremiumDtos.add(quarterlyPremium);
                }
                final GHInsuredDependentBuilder[] insuredDependentBuilder = {GHInsuredDependent.getInsuredDependentBuilder(new PlanId(premiumDetail.getPlanId()), premiumDetail.getPlanCode(), basicAnnualPremium,computedPremiumDtos,premiumDetail.getSumAssured())};
                insuredDependentBuilder[0].withCategory(insuredDependentDto.getOccupationCategory()).withInsuredName(insuredDependentDto.getSalutation(), insuredDependentDto.getFirstName(), insuredDependentDto.getLastName())
                        .withInsuredNrcNumber(insuredDependentDto.getNrcNumber()).withCompanyName(insuredDependentDto.getCompanyName())
                        .withDateOfBirth(insuredDependentDto.getDateOfBirth()).withGender(insuredDependentDto.getGender()).
                        withRelationship(insuredDependentDto.getRelationship()).withOccupationClass(insuredDependentDto.getOccupationClass()).withMinAndMaxAge(insuredDependentDto.getMinAgeEntry(), insuredDependentDto.getMaxAgeEntry()).withExistingIllness(insuredDependentDto.getExistingIllness()).withNoOfAssured(insuredDependentDto.getNoOfAssured());
                insuredDependentDto.getCoveragePremiumDetails().forEach(coveragePremiumDetail -> {
                    BigDecimal coverageBasicPremium = coveragePremiumDetail.getPremium() == null ? computePlanBasicAnnualPremium(premiumDetail.getPlanId(), coveragePremiumDetail.getSumAssured().toPlainString(), String.valueOf(getAgeOnNextBirthDate(insuredDependentDto.getDateOfBirth())), occupationClass, insuredDependentDto.getGender().name(), 365, coveragePremiumDetail.getCoverageId(),null) : coveragePremiumDetail.getPremium();
                    if (insuredDependentDto.getNoOfAssured() == null && insuredDependentDto.getDateOfBirth() != null) {
                        int ageOnNextBirthDate = getAgeOnNextBirthDate(insuredDependentDto.getDateOfBirth());
                        coverageBasicPremium = computePremiumByApplyingAgeLoadingFactor(ageOnNextBirthDate, coverageBasicPremium);
                    }
                    final GHCoveragePremiumDetailBuilder[] ghCoveragePremiumDetailBuilder = {new GHCoveragePremiumDetailBuilder(coveragePremiumDetail.getCoverageCode(), coveragePremiumDetail.getCoverageId(), coveragePremiumDetail.getCoverageName(), coverageBasicPremium, coveragePremiumDetail.getPremiumVisibility(), coveragePremiumDetail.getSumAssured())};
                    if (isNotEmpty(coveragePremiumDetail.getBenefitDetails())) {
                        coveragePremiumDetail.getBenefitDetails().forEach(benefitDetail -> {
                            ghCoveragePremiumDetailBuilder[0] = ghCoveragePremiumDetailBuilder[0].withBenefit(benefitDetail.getBenefitCode(), benefitDetail.getBenefitId(), benefitDetail.getBenefitLimit());
                        });
                    }
                    insuredDependentBuilder[0] = insuredDependentBuilder[0].withCoveragePremiumDetail(ghCoveragePremiumDetailBuilder[0]);
                });
                return insuredDependentBuilder[0].build();
            }
        }).collect(Collectors.toSet());
        return insuredDependents;
    }

    private BigDecimal computePremiumByApplyingAgeLoadingFactor(int age, BigDecimal basicPremium) {
        AgentLoadingFactorDto agentLoadingFactorDto = generalInformationProvider.getAgeLoadingFactor(LineOfBusinessEnum.GROUP_HEALTH);
        if (age > agentLoadingFactorDto.getAge()) {
            BigDecimal loadingFactor = agentLoadingFactorDto.getLoadingFactor() == null ? BigDecimal.ZERO : basicPremium.multiply((agentLoadingFactorDto.getLoadingFactor().divide(new BigDecimal(100))));
            basicPremium = basicPremium.add(loadingFactor);
        }
        return basicPremium;
    }


    private PremiumInfluencingFactor findPremiumInfluencingFactor(PremiumInfluencingFactor premiumInfluencingFactor, List<PremiumInfluencingFactor> premiumInfluencingFactors) {
        Optional<PremiumInfluencingFactor> premiumInfluencingFactorOptional = premiumInfluencingFactors.stream().filter(new Predicate<PremiumInfluencingFactor>() {
            @Override
            public boolean test(PremiumInfluencingFactor candidate) {
                return premiumInfluencingFactor.equals(candidate);
            }
        }).findAny();
        return premiumInfluencingFactorOptional.isPresent() ? premiumInfluencingFactorOptional.get() : null;
    }

    private List<PremiumInfluencingFactor> getPremiumInfluencingFactors(String planId, String coverageId, LocalDate effectiveDate) {
        if (isEmpty(coverageId)) {
            return premiumCalculator.getPremiumInfluencingFactors(new PlanId(planId), effectiveDate);

        }
        return premiumCalculator.getPremiumInfluencingFactors(new PlanId(planId), new CoverageId(coverageId), effectiveDate);
    }
}
