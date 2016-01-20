package com.pla.grouplife.sharedresource.util;

import com.google.common.collect.Lists;
import com.pla.core.domain.model.generalinformation.ModelFactorOrganizationInformation;
import com.pla.core.domain.model.generalinformation.ProductLineGeneralInformation;
import com.pla.grouplife.sharedresource.dto.InsuredDto;
import com.pla.grouplife.sharedresource.dto.PremiumDetailDto;
import com.pla.grouplife.sharedresource.model.vo.*;
import com.pla.grouplife.sharedresource.query.GLFinder;
import com.pla.publishedlanguage.contract.IGeneralInformationProvider;
import com.pla.publishedlanguage.contract.IPremiumCalculator;
import com.pla.publishedlanguage.contract.IProcessInfoAdapter;
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
import org.nthdimenzion.common.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
public class GLInsuredFactory {

    private IPremiumCalculator premiumCalculator;

    private IProcessInfoAdapter processInfoAdapter;

    private IGeneralInformationProvider generalInformationProvider;

    private GLFinder glFinder;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    public GLInsuredFactory(IPremiumCalculator premiumCalculator, IProcessInfoAdapter processInfoAdapter, IGeneralInformationProvider generalInformationProvider, GLFinder glFinder) {
        this.premiumCalculator = premiumCalculator;
        this.processInfoAdapter = processInfoAdapter;
        this.generalInformationProvider = generalInformationProvider;
        this.glFinder = glFinder;
    }

    public Set<Insured> createInsuredDetail(List<InsuredDto> insuredDtos) {
        Set<Insured> insureds = insuredDtos.stream().map(new Function<InsuredDto, Insured>() {
            @Override
            public Insured apply(InsuredDto insuredDto) {
                InsuredDto.PlanPremiumDetailDto premiumDetail = insuredDto.getPlanPremiumDetail();
                List<ComputedPremiumDto> computedPremiumDtos = Lists.newArrayList();
                String occupationClass = glFinder.getOccupationClass(insuredDto.getOccupationClass());
                BigDecimal basicAnnualPremium = premiumDetail.getPremiumAmount() != null ? premiumDetail.getPremiumAmount() :
                        computePlanBasicAnnualPremium(premiumDetail.getPlanId(), premiumDetail.getSumAssured().toPlainString(), String.valueOf(getAgeOnNextBirthDate(insuredDto.getDateOfBirth())), occupationClass, insuredDto.getGender().name(), 365, null,computedPremiumDtos);
                if (insuredDto.getNoOfAssured() == null && insuredDto.getDateOfBirth() != null  && premiumDetail.getPremiumAmount()== null) {
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
                final InsuredBuilder[] insuredBuilder = {Insured.getInsuredBuilder(new PlanId(premiumDetail.getPlanId()), premiumDetail.getPlanCode(), basicAnnualPremium, premiumDetail.getSumAssured(),premiumDetail.getIncomeMultiplier() ,computedPremiumDtos)};
                insuredBuilder[0].withCategory(insuredDto.getOccupationCategory()).withInsuredName(insuredDto.getSalutation(), insuredDto.getFirstName(), insuredDto.getLastName())
                        .withAnnualIncome(insuredDto.getAnnualIncome()).withOccupation(insuredDto.getOccupationClass()).
                        withInsuredNrcNumber(insuredDto.getNrcNumber()).withCompanyName(insuredDto.getCompanyName())
                        .withManNumber(insuredDto.getManNumber()).withDateOfBirth(insuredDto.getDateOfBirth()).withGender(insuredDto.getGender()).withNoOfAssured(insuredDto.getNoOfAssured()).withPremiumType(insuredDto.getPremiumType()).withRateOfPremium(insuredDto.getRateOfPremium());
                List<ComputedPremiumDto> computedPremiumDtosCov = Lists.newArrayList();
                insuredDto.getCoveragePremiumDetails().forEach(coveragePremiumDetail -> {
                    BigDecimal coverageBasicPremium = coveragePremiumDetail.getPremium() == null ? computePlanBasicAnnualPremium(premiumDetail.getPlanId(), coveragePremiumDetail.getSumAssured().toPlainString(), String.valueOf(getAgeOnNextBirthDate(insuredDto.getDateOfBirth())), null, insuredDto.getGender().name(), 365, coveragePremiumDetail.getCoverageId(),computedPremiumDtosCov) : coveragePremiumDetail.getPremium();
                    if (insuredDto.getNoOfAssured() == null && insuredDto.getDateOfBirth() != null  && premiumDetail.getPremiumAmount()== null) {
                        int ageOnNextBirthDate = getAgeOnNextBirthDate(insuredDto.getDateOfBirth());
                        coverageBasicPremium = computePremiumByApplyingAgeLoadingFactor(ageOnNextBirthDate, coverageBasicPremium);
                    }
                    insuredBuilder[0] = insuredBuilder[0].withCoveragePremiumDetail(coveragePremiumDetail.getCoverageName(), coveragePremiumDetail.getCoverageCode(), coveragePremiumDetail.getCoverageId(), coverageBasicPremium, coveragePremiumDetail.getSumAssured(),computedPremiumDtosCov);
                });
                Set<InsuredDependent> insuredDependents = getInsuredDependent(insuredDto.getInsuredDependents());
                insuredBuilder[0] = insuredBuilder[0].withDependents(insuredDependents);
                return insuredBuilder[0].build();
            }
        }).collect(Collectors.toSet());
        return insureds;
    }

    public Set<ModelFactorOrganizationInformation> getModalFactor(){
        Criteria policyCriteria = Criteria.where("productLine").is(LineOfBusinessEnum.GROUP_LIFE.name());
        Query query = new Query(policyCriteria);
        List<ProductLineGeneralInformation> productLineInformation = mongoTemplate.find(query, ProductLineGeneralInformation.class);
        checkArgument(productLineInformation != null, "Product Line Information SetUp not available");
        Set<ModelFactorOrganizationInformation> modelFactorItems = productLineInformation.get(0).getModalFactorProcessInformation().getModelFactorItems();
        return modelFactorItems;
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
        List<ComputedPremiumDto> computedPremiums = premiumCalculator.calculateBasicPremium(premiumCalculationDto,new BigDecimal(sumAssured).setScale(0,BigDecimal.ROUND_FLOOR), LineOfBusinessEnum.GROUP_LIFE, Boolean.FALSE, StringUtils.EMPTY);
        computedPremiums.parallelStream().map(computePremium->{
            computedPremiumDtos.add(computePremium);
            return computePremium;
        }).collect(Collectors.toList());
        BigDecimal annualBasicPremium = ComputedPremiumDto.getAnnualPremium(computedPremiums);
        return annualBasicPremium;
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
        BigDecimal installmentPremium = premiumCalculator.computeProratePremium(premiumCalculationDto,new BigDecimal(sumAssured).setScale(0,BigDecimal.ROUND_FLOOR));
        return installmentPremium;
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

    private BigDecimal computePremiumByApplyingAgeLoadingFactor(int age, BigDecimal basicPremium) {
        AgentLoadingFactorDto agentLoadingFactorDto = generalInformationProvider.getAgeLoadingFactor(LineOfBusinessEnum.GROUP_LIFE);
        if (age > agentLoadingFactorDto.getAge()) {
            BigDecimal loadingFactor = agentLoadingFactorDto.getLoadingFactor() == null ? BigDecimal.ZERO : basicPremium.multiply((agentLoadingFactorDto.getLoadingFactor().divide(new BigDecimal(100))));
            basicPremium = basicPremium.add(loadingFactor);
        }
        return basicPremium;
    }

    private Set<InsuredDependent> getInsuredDependent(Set<InsuredDto.InsuredDependentDto> insuredDependentDtos) {
        Set<InsuredDependent> insuredDependents = insuredDependentDtos.stream().map(new Function<InsuredDto.InsuredDependentDto, InsuredDependent>() {
            @Override
            public InsuredDependent apply(InsuredDto.InsuredDependentDto insuredDependentDto) {
                InsuredDto.PlanPremiumDetailDto premiumDetail = insuredDependentDto.getPlanPremiumDetail();
                List<ComputedPremiumDto> computedPremiumDtos = Lists.newArrayList();
                String occupationClass = glFinder.getOccupationClass(insuredDependentDto.getOccupationClass());
                BigDecimal basicAnnualPremium = premiumDetail.getPremiumAmount() != null ? premiumDetail.getPremiumAmount() : computePlanBasicAnnualPremium(premiumDetail.getPlanId(), premiumDetail.getSumAssured().toPlainString(),
                        String.valueOf(getAgeOnNextBirthDate(insuredDependentDto.getDateOfBirth()))
                        , isNotEmpty(occupationClass) ? occupationClass : null, insuredDependentDto.getGender().name(), 365, null,computedPremiumDtos);
                if (insuredDependentDto.getNoOfAssured() == null && insuredDependentDto.getDateOfBirth() != null) {
                    int ageOnNextBirthDate = getAgeOnNextBirthDate(insuredDependentDto.getDateOfBirth());
                    basicAnnualPremium = computePremiumByApplyingAgeLoadingFactor(ageOnNextBirthDate, basicAnnualPremium);
                }
                if (insuredDependentDto.getNoOfAssured() != null && isEmpty(computedPremiumDtos)){
                    Set<ModelFactorOrganizationInformation> modelFactorItems =  getModalFactor();
                    ComputedPremiumDto semiAnnualPremium = new ComputedPremiumDto(PremiumFrequency.SEMI_ANNUALLY,basicAnnualPremium.multiply(ModelFactorOrganizationInformation.getSemiAnnualModalFactor(modelFactorItems)));
                    ComputedPremiumDto quarterlyPremium = new ComputedPremiumDto(PremiumFrequency.QUARTERLY,basicAnnualPremium.multiply(ModelFactorOrganizationInformation.getQuarterlyModalFactor(modelFactorItems)));
                    ComputedPremiumDto monthlyPremium = new ComputedPremiumDto(PremiumFrequency.MONTHLY,basicAnnualPremium.multiply(ModelFactorOrganizationInformation.getMonthlyModalFactor(modelFactorItems)));
                    computedPremiumDtos.add(semiAnnualPremium);
                    computedPremiumDtos.add(quarterlyPremium);
                    computedPremiumDtos.add(monthlyPremium);
                }
                final InsuredDependentBuilder[] insuredDependentBuilder = {InsuredDependent.getInsuredDependentBuilder(new PlanId(premiumDetail.getPlanId()), premiumDetail.getPlanCode(), basicAnnualPremium,computedPremiumDtos,premiumDetail.getSumAssured())};
                insuredDependentBuilder[0].withCategory(insuredDependentDto.getOccupationCategory()).withInsuredName(insuredDependentDto.getSalutation(), insuredDependentDto.getFirstName(), insuredDependentDto.getLastName())
                        .withInsuredNrcNumber(insuredDependentDto.getNrcNumber()).withCompanyName(insuredDependentDto.getCompanyName()).withOccupationClass(insuredDependentDto.getOccupationClass())
                        .withDateOfBirth(insuredDependentDto.getDateOfBirth()).withGender(insuredDependentDto.getGender())
                        .withRelationship(insuredDependentDto.getRelationship()).withNoOfAssured(insuredDependentDto.getNoOfAssured());
                List<ComputedPremiumDto> computedPremiumDtosCov = Lists.newArrayList();
                insuredDependentDto.getCoveragePremiumDetails().forEach(coveragePremiumDetail -> {
                    BigDecimal coverageBasicPremium = coveragePremiumDetail.getPremium() == null ? computePlanBasicAnnualPremium(premiumDetail.getPlanId(), coveragePremiumDetail.getSumAssured().toPlainString(), String.valueOf(getAgeOnNextBirthDate(insuredDependentDto.getDateOfBirth())), null, insuredDependentDto.getGender().name(), 365, coveragePremiumDetail.getCoverageId(),computedPremiumDtosCov) : coveragePremiumDetail.getPremium();
                    if (insuredDependentDto.getNoOfAssured() == null && insuredDependentDto.getDateOfBirth() != null) {
                        int ageOnNextBirthDate = getAgeOnNextBirthDate(insuredDependentDto.getDateOfBirth());
                        coverageBasicPremium = computePremiumByApplyingAgeLoadingFactor(ageOnNextBirthDate, coverageBasicPremium);
                    }
                    insuredDependentBuilder[0] = insuredDependentBuilder[0].withCoveragePremiumDetail(coveragePremiumDetail.getCoverageName(), coveragePremiumDetail.getCoverageCode(), coveragePremiumDetail.getCoverageId(), coverageBasicPremium, coveragePremiumDetail.getSumAssured(),computedPremiumDtosCov);
                });
                return insuredDependentBuilder[0].build();
            }
        }).collect(Collectors.toSet());
        return insuredDependents;
    }


    private List<PremiumInfluencingFactor> getPremiumInfluencingFactors(String planId, String coverageId, LocalDate effectiveDate) {
        if (isEmpty(coverageId)) {
            return premiumCalculator.getPremiumInfluencingFactors(new PlanId(planId), effectiveDate);

        }
        return premiumCalculator.getPremiumInfluencingFactors(new PlanId(planId), new CoverageId(coverageId), effectiveDate);
    }

    public Set<Insured> recalculateProratePremiumForInsureds(PremiumDetailDto premiumDetailDto, Set<Insured> insureds) {
        for (Insured insured : insureds) {//computePlanBasicAnnualPremium
            PlanPremiumDetail planPremiumDetail = insured.getPlanPremiumDetail();
            List<ComputedPremiumDto> computedPremiumDtos = Lists.newArrayList();
            String occupationClass = glFinder.getOccupationClass(insured.getOccupationClass());
            BigDecimal insuredPlanProratePremium = insured.getNoOfAssured() != null ? planPremiumDetail.getPremiumAmount() :
                    premiumDetailDto.getPolicyTermValue() != 365 ?
                            computeBasicProratePremium(planPremiumDetail.getPlanId().getPlanId(), planPremiumDetail.getSumAssured().toPlainString(),
                                    getAgeOnNextBirthDate(insured.getDateOfBirth()).toString(), occupationClass, insured.getGender().name(), premiumDetailDto.getPolicyTermValue(), null) :
                            computePlanBasicAnnualPremium(planPremiumDetail.getPlanId().getPlanId(), planPremiumDetail.getSumAssured().toPlainString(), getAgeOnNextBirthDate(insured.getDateOfBirth()).toString(), occupationClass, insured.getGender().name(), premiumDetailDto.getPolicyTermValue(), null,computedPremiumDtos);
            if (insured.getNoOfAssured() == null && insured.getDateOfBirth() != null && insured.getPlanPremiumDetail().getPremiumAmount() == null) {
                int ageOnNextBirthDate = getAgeOnNextBirthDate(insured.getDateOfBirth());
                insuredPlanProratePremium = computePremiumByApplyingAgeLoadingFactor(ageOnNextBirthDate, insuredPlanProratePremium);
            }
            if (insured.getNoOfAssured() != null && isEmpty(computedPremiumDtos)){
                Set<ModelFactorOrganizationInformation> modelFactorItems =  getModalFactor();
                ComputedPremiumDto semiAnnualPremium = new ComputedPremiumDto(PremiumFrequency.SEMI_ANNUALLY,insuredPlanProratePremium.multiply(ModelFactorOrganizationInformation.getSemiAnnualModalFactor(modelFactorItems)));
                ComputedPremiumDto quarterlyPremium = new ComputedPremiumDto(PremiumFrequency.QUARTERLY,insuredPlanProratePremium.multiply(ModelFactorOrganizationInformation.getQuarterlyModalFactor(modelFactorItems)));
                ComputedPremiumDto monthlyPremium = new ComputedPremiumDto(PremiumFrequency.MONTHLY,insuredPlanProratePremium.multiply(ModelFactorOrganizationInformation.getMonthlyModalFactor(modelFactorItems)));
                computedPremiumDtos.add(semiAnnualPremium);
                computedPremiumDtos.add(quarterlyPremium);
                computedPremiumDtos.add(monthlyPremium);
            }
            if (premiumDetailDto.getPolicyTermValue() == 365 && isNotEmpty(computedPremiumDtos)){
                planPremiumDetail.updatePremiumAmount(ComputedPremiumDto.getSemiAnnualPremium(computedPremiumDtos),ComputedPremiumDto.getQuarterlyPremium(computedPremiumDtos),ComputedPremiumDto.getMonthlyPremium(computedPremiumDtos));
            }
            if (premiumDetailDto.getPolicyTermValue() != 365 && insured.getNoOfAssured() != null){
                insuredPlanProratePremium =  insuredPlanProratePremium.divide(new BigDecimal(365),2, AppConstants.roundingMode).multiply(new BigDecimal(premiumDetailDto.getPolicyTermValue()));
            }

            insured.updatePlanPremiumAmount(insuredPlanProratePremium);
            if (isNotEmpty(insured.getCoveragePremiumDetails())) {
                Set<CoveragePremiumDetail> coveragePremiumDetails = insured.getCoveragePremiumDetails();
                List<ComputedPremiumDto> computedPremiumDtosCov = Lists.newArrayList();
                for (CoveragePremiumDetail coveragePremiumDetail : coveragePremiumDetails) {
                    BigDecimal insuredCoveragePremiumDetail = insured.getNoOfAssured() != null ? coveragePremiumDetail.getPremium() :
                            premiumDetailDto.getPolicyTermValue() != 365 ? computeBasicProratePremium(planPremiumDetail.getPlanId().getPlanId(),
                                    coveragePremiumDetail.getSumAssured().toPlainString(), getAgeOnNextBirthDate(insured.getDateOfBirth()).toString(),
                                    occupationClass, insured.getGender().name(), premiumDetailDto.getPolicyTermValue(), coveragePremiumDetail.getCoverageId().getCoverageId()) :
                                    computePlanBasicAnnualPremium(planPremiumDetail.getPlanId().getPlanId(),
                                            coveragePremiumDetail.getSumAssured().toPlainString(), getAgeOnNextBirthDate(insured.getDateOfBirth()).toString(),
                                            occupationClass, insured.getGender().name(), premiumDetailDto.getPolicyTermValue(), coveragePremiumDetail.getCoverageId().getCoverageId(),computedPremiumDtosCov);
                    if (insured.getNoOfAssured() == null && insured.getDateOfBirth() != null && insured.getPlanPremiumDetail().getPremiumAmount() == null) {
                        int ageOnNextBirthDate = getAgeOnNextBirthDate(insured.getDateOfBirth());
                        insuredCoveragePremiumDetail = computePremiumByApplyingAgeLoadingFactor(ageOnNextBirthDate, insuredCoveragePremiumDetail);
                    }
                    coveragePremiumDetail.updateWithPremium(insuredCoveragePremiumDetail);
                }
            }
            for (InsuredDependent insuredDependent : insured.getInsuredDependents()) {
                PlanPremiumDetail insuredDependentPlanPremiumDetail = insuredDependent.getPlanPremiumDetail();
                List<ComputedPremiumDto> computedPremiumDtosDep = Lists.newArrayList();
                String dependentOccupationClass = glFinder.getOccupationClass(insuredDependent.getOccupationClass());
                BigDecimal insuredDependentPlanProratePremium = insuredDependent.getNoOfAssured() != null ? insuredDependentPlanPremiumDetail.getPremiumAmount() :
                        premiumDetailDto.getPolicyTermValue() != 365 ? computeBasicProratePremium(insuredDependentPlanPremiumDetail.getPlanId().getPlanId(), insuredDependentPlanPremiumDetail.getSumAssured().toPlainString(), getAgeOnNextBirthDate(insuredDependent.getDateOfBirth()).toString(), dependentOccupationClass, insuredDependent.getGender().name(), premiumDetailDto.getPolicyTermValue(), null) :
                                computePlanBasicAnnualPremium(insuredDependentPlanPremiumDetail.getPlanId().getPlanId(), insuredDependentPlanPremiumDetail.getSumAssured().toPlainString(), getAgeOnNextBirthDate(insuredDependent.getDateOfBirth()).toString(), dependentOccupationClass, insuredDependent.getGender().name(), premiumDetailDto.getPolicyTermValue(), null,computedPremiumDtosDep);
                if (insuredDependent.getNoOfAssured() == null && insuredDependent.getDateOfBirth() != null) {
                    int ageOnNextBirthDate = getAgeOnNextBirthDate(insuredDependent.getDateOfBirth());
                    insuredDependentPlanProratePremium = computePremiumByApplyingAgeLoadingFactor(ageOnNextBirthDate, insuredDependentPlanProratePremium);
                }
                if (insuredDependent.getNoOfAssured() != null && isEmpty(computedPremiumDtosDep)){
                    Set<ModelFactorOrganizationInformation> modelFactorItems =  getModalFactor();
                    ComputedPremiumDto semiAnnualPremium = new ComputedPremiumDto(PremiumFrequency.SEMI_ANNUALLY,insuredDependentPlanProratePremium.multiply(ModelFactorOrganizationInformation.getSemiAnnualModalFactor(modelFactorItems)));
                    ComputedPremiumDto quarterlyPremium = new ComputedPremiumDto(PremiumFrequency.QUARTERLY,insuredDependentPlanProratePremium.multiply(ModelFactorOrganizationInformation.getQuarterlyModalFactor(modelFactorItems)));
                    ComputedPremiumDto monthlyPremium = new ComputedPremiumDto(PremiumFrequency.MONTHLY,insuredDependentPlanProratePremium.multiply(ModelFactorOrganizationInformation.getMonthlyModalFactor(modelFactorItems)));
                    computedPremiumDtosDep.add(semiAnnualPremium);
                    computedPremiumDtosDep.add(quarterlyPremium);
                    computedPremiumDtosDep.add(monthlyPremium);
                }
                if (premiumDetailDto.getPolicyTermValue() != 365 && insured.getNoOfAssured() != null){
                    insuredDependentPlanProratePremium =  insuredDependentPlanProratePremium.divide(new BigDecimal(365),2,AppConstants.roundingMode).multiply(new BigDecimal(premiumDetailDto.getPolicyTermValue()));
                }
                insuredDependent.updatePlanPremiumAmount(insuredDependentPlanProratePremium);
                if (premiumDetailDto.getPolicyTermValue() == 365 && isNotEmpty(computedPremiumDtosDep)){
                    insuredDependentPlanPremiumDetail.updatePremiumAmount(ComputedPremiumDto.getSemiAnnualPremium(computedPremiumDtosDep),ComputedPremiumDto.getQuarterlyPremium(computedPremiumDtosDep),ComputedPremiumDto.getMonthlyPremium(computedPremiumDtosDep));
                }
                Set<CoveragePremiumDetail> insuredDependentCoveragePremiumDetails = insuredDependent.getCoveragePremiumDetails();
                if (isNotEmpty(insuredDependentCoveragePremiumDetails)) {
                    List<ComputedPremiumDto> computedPremiumDtosDepCov = Lists.newArrayList();
                    for (CoveragePremiumDetail insuredDependentCoveragePremiumDetail : insuredDependentCoveragePremiumDetails) {
                        BigDecimal insuredCoveragePremiumDetail = insuredDependent.getNoOfAssured() != null ? insuredDependentCoveragePremiumDetail.getPremium() :
                                premiumDetailDto.getPolicyTermValue() != 365 ?
                                        computeBasicProratePremium(insuredDependentPlanPremiumDetail.getPlanId().getPlanId(), insuredDependentCoveragePremiumDetail.getSumAssured().toPlainString(), getAgeOnNextBirthDate(insuredDependent.getDateOfBirth()).toString(), dependentOccupationClass, insuredDependent.getGender().name(), premiumDetailDto.getPolicyTermValue(), insuredDependentCoveragePremiumDetail.getCoverageId().getCoverageId()) :
                                        computePlanBasicAnnualPremium(insuredDependentPlanPremiumDetail.getPlanId().getPlanId(), insuredDependentCoveragePremiumDetail.getSumAssured().toPlainString(), getAgeOnNextBirthDate(insuredDependent.getDateOfBirth()).toString(), dependentOccupationClass, insuredDependent.getGender().name(), premiumDetailDto.getPolicyTermValue(), insuredDependentCoveragePremiumDetail.getCoverageId().getCoverageId(),computedPremiumDtosDepCov);
                        if (insuredDependent.getNoOfAssured() == null && insuredDependent.getDateOfBirth() != null) {
                            int ageOnNextBirthDate = getAgeOnNextBirthDate(insuredDependent.getDateOfBirth());
                            insuredCoveragePremiumDetail = computePremiumByApplyingAgeLoadingFactor(ageOnNextBirthDate, insuredCoveragePremiumDetail);
                        }
                        insuredDependentCoveragePremiumDetail.updateWithPremium(insuredCoveragePremiumDetail);
                    }
                }
            }
        }
        return insureds;
    }


    public Set<Insured> calculateProratePremiumForInsureds(PremiumDetailDto premiumDetailDto, Set<Insured> insureds, int policyTerm, int endorsementDuration, boolean isMemberPromotion) {
        for (Insured insured : insureds) {//computePlanBasicAnnualPremium
            PlanPremiumDetail planPremiumDetail = insured.getPlanPremiumDetail();
            if (planPremiumDetail!=null && planPremiumDetail.getPlanId()!=null) {
                String occupationClass = glFinder.getOccupationClass(insured.getOccupationClass());
                BigDecimal insuredPlanProratePremium = insured.getNoOfAssured() != null ? planPremiumDetail.getPremiumAmount() :
                        premiumDetailDto.getPolicyTermValue() != 365 ?
                                computeBasicProratePremium(planPremiumDetail.getPlanId().getPlanId(), planPremiumDetail.getSumAssured().toPlainString(),
                                        getAgeOnNextBirthDate(insured.getDateOfBirth()).toString(), occupationClass, insured.getGender().name(), premiumDetailDto.getPolicyTermValue(), null) :
                                computePlanBasicAnnualPremium(planPremiumDetail.getPlanId().getPlanId(), planPremiumDetail.getSumAssured().toPlainString(), getAgeOnNextBirthDate(insured.getDateOfBirth()).toString(), occupationClass, insured.getGender().name(), premiumDetailDto.getPolicyTermValue(), null,Lists.newArrayList());
                if (insured.getNoOfAssured() == null && insured.getDateOfBirth() != null && insured.getPlanPremiumDetail().getPremiumAmount() == null) {
                    int ageOnNextBirthDate = getAgeOnNextBirthDate(insured.getDateOfBirth());
                    insuredPlanProratePremium = computePremiumByApplyingAgeLoadingFactor(ageOnNextBirthDate, insuredPlanProratePremium);
                }
                if (insured.getNoOfAssured() != null) {
                    insuredPlanProratePremium = insuredPlanProratePremium.divide(new BigDecimal(policyTerm), 2, RoundingMode.HALF_UP).multiply(new BigDecimal(endorsementDuration));
                }
                if (isMemberPromotion){
                    BigDecimal oldAnnualIncomePremiumAmount = insured.getPlanPremiumDetail().getPremiumAmount()!=null?insured.getPlanPremiumDetail().getPremiumAmount():BigDecimal.ONE;
                    Integer noOfDaysOver = policyTerm - endorsementDuration;
                    BigDecimal oldAnnualIncomeProratePremium = oldAnnualIncomePremiumAmount.divide(new BigDecimal(policyTerm), 2, RoundingMode.HALF_UP).multiply(new BigDecimal(noOfDaysOver));
                    BigDecimal newAnnualIncomePremiumPremiumAmount = oldAnnualIncomeProratePremium.add(insuredPlanProratePremium);
                    BigDecimal proratePremium = newAnnualIncomePremiumPremiumAmount.subtract(insured.getPlanPremiumDetail().getPremiumAmount());
                    insuredPlanProratePremium = proratePremium;
                }
                insured.updatePlanPremiumAmount(insuredPlanProratePremium);
                if (isNotEmpty(insured.getCoveragePremiumDetails())) {
                    Set<CoveragePremiumDetail> coveragePremiumDetails = insured.getCoveragePremiumDetails();
                    for (CoveragePremiumDetail coveragePremiumDetail : coveragePremiumDetails) {
                        BigDecimal insuredCoveragePremiumDetail = insured.getNoOfAssured() != null ? coveragePremiumDetail.getPremium() :
                                premiumDetailDto.getPolicyTermValue() != 365 ? computeBasicProratePremium(planPremiumDetail.getPlanId().getPlanId(),
                                        coveragePremiumDetail.getSumAssured().toPlainString(), getAgeOnNextBirthDate(insured.getDateOfBirth()).toString(),
                                        occupationClass, insured.getGender().name(), premiumDetailDto.getPolicyTermValue(), coveragePremiumDetail.getCoverageId().getCoverageId()) :
                                        computePlanBasicAnnualPremium(planPremiumDetail.getPlanId().getPlanId(),
                                                coveragePremiumDetail.getSumAssured().toPlainString(), getAgeOnNextBirthDate(insured.getDateOfBirth()).toString(),
                                                occupationClass, insured.getGender().name(), premiumDetailDto.getPolicyTermValue(), coveragePremiumDetail.getCoverageId().getCoverageId(),Lists.newArrayList());
                        if (insured.getNoOfAssured() == null && insured.getDateOfBirth() != null && insured.getPlanPremiumDetail().getPremiumAmount() == null) {
                            int ageOnNextBirthDate = getAgeOnNextBirthDate(insured.getDateOfBirth());
                            insuredCoveragePremiumDetail = computePremiumByApplyingAgeLoadingFactor(ageOnNextBirthDate, insuredCoveragePremiumDetail);
                        }
                        coveragePremiumDetail.updateWithPremium(insuredCoveragePremiumDetail);
                    }
                }
            }
            for (InsuredDependent insuredDependent : insured.getInsuredDependents()) {
                PlanPremiumDetail insuredDependentPlanPremiumDetail = insuredDependent.getPlanPremiumDetail();
                String dependentOccupationClass = glFinder.getOccupationClass(insuredDependent.getOccupationClass());
                BigDecimal insuredDependentPlanProratePremium = insuredDependent.getNoOfAssured() != null ? insuredDependentPlanPremiumDetail.getPremiumAmount() :
                        premiumDetailDto.getPolicyTermValue() != 365 ? computeBasicProratePremium(insuredDependentPlanPremiumDetail.getPlanId().getPlanId(), insuredDependentPlanPremiumDetail.getSumAssured().toPlainString(), getAgeOnNextBirthDate(insuredDependent.getDateOfBirth()).toString(), dependentOccupationClass, insuredDependent.getGender().name(), premiumDetailDto.getPolicyTermValue(), null) :
                                computePlanBasicAnnualPremium(insuredDependentPlanPremiumDetail.getPlanId().getPlanId(), insuredDependentPlanPremiumDetail.getSumAssured().toPlainString(), getAgeOnNextBirthDate(insuredDependent.getDateOfBirth()).toString(), dependentOccupationClass, insuredDependent.getGender().name(), premiumDetailDto.getPolicyTermValue(), null,Lists.newArrayList());
                if (insuredDependent.getNoOfAssured() == null && insuredDependent.getDateOfBirth() != null) {
                    int ageOnNextBirthDate = getAgeOnNextBirthDate(insuredDependent.getDateOfBirth());
                    insuredDependentPlanProratePremium = computePremiumByApplyingAgeLoadingFactor(ageOnNextBirthDate, insuredDependentPlanProratePremium);
                }
                if (insuredDependent.getNoOfAssured() != null) {
                    insuredDependentPlanProratePremium = insuredDependentPlanProratePremium.divide(new BigDecimal(policyTerm), 2, RoundingMode.HALF_UP).multiply(new BigDecimal(endorsementDuration));
                }
                insuredDependent.updatePlanPremiumAmount(insuredDependentPlanProratePremium);
                Set<CoveragePremiumDetail> insuredDependentCoveragePremiumDetails = insuredDependent.getCoveragePremiumDetails();
                if (isNotEmpty(insuredDependentCoveragePremiumDetails)) {
                    for (CoveragePremiumDetail insuredDependentCoveragePremiumDetail : insuredDependentCoveragePremiumDetails) {
                        BigDecimal insuredCoveragePremiumDetail = insuredDependent.getNoOfAssured() != null ? insuredDependentCoveragePremiumDetail.getPremium() :
                                premiumDetailDto.getPolicyTermValue() != 365 ?
                                        computeBasicProratePremium(insuredDependentPlanPremiumDetail.getPlanId().getPlanId(), insuredDependentCoveragePremiumDetail.getSumAssured().toPlainString(), getAgeOnNextBirthDate(insuredDependent.getDateOfBirth()).toString(), dependentOccupationClass, insuredDependent.getGender().name(), premiumDetailDto.getPolicyTermValue(), insuredDependentCoveragePremiumDetail.getCoverageId().getCoverageId()) :
                                        computePlanBasicAnnualPremium(insuredDependentPlanPremiumDetail.getPlanId().getPlanId(), insuredDependentCoveragePremiumDetail.getSumAssured().toPlainString(), getAgeOnNextBirthDate(insuredDependent.getDateOfBirth()).toString(), dependentOccupationClass, insuredDependent.getGender().name(), premiumDetailDto.getPolicyTermValue(), insuredDependentCoveragePremiumDetail.getCoverageId().getCoverageId(),Lists.newArrayList());
                        if (insuredDependent.getNoOfAssured() == null && insuredDependent.getDateOfBirth() != null) {
                            int ageOnNextBirthDate = getAgeOnNextBirthDate(insuredDependent.getDateOfBirth());
                            insuredCoveragePremiumDetail = computePremiumByApplyingAgeLoadingFactor(ageOnNextBirthDate, insuredCoveragePremiumDetail);
                        }
                        insuredDependentCoveragePremiumDetail.updateWithPremium(insuredCoveragePremiumDetail);
                    }
                }
            }
        }
        return insureds;
    }

    public GLEndorsementInsured calculateFCLProratePremium(PremiumDetailDto premiumDetailDto, GLEndorsementInsured glEndorsementInsured) {
            PlanPremiumDetail planPremiumDetail = glEndorsementInsured.getPlanPremiumDetail();
            if (planPremiumDetail!=null && planPremiumDetail.getPlanId()!=null) {
                String occupationClass = glFinder.getOccupationClass(glEndorsementInsured.getOccupationClass());
                BigDecimal insuredPlanProratePremium = premiumDetailDto.getPolicyTermValue() != 365 ?
                                computeBasicProratePremium(planPremiumDetail.getPlanId().getPlanId(), planPremiumDetail.getSumAssured().toPlainString(),
                                        getAgeOnNextBirthDate(glEndorsementInsured.getDateOfBirth()).toString(), occupationClass, glEndorsementInsured.getGender().name(), premiumDetailDto.getPolicyTermValue(), null) :
                                computePlanBasicAnnualPremium(planPremiumDetail.getPlanId().getPlanId(), planPremiumDetail.getSumAssured().toPlainString(), getAgeOnNextBirthDate(glEndorsementInsured.getDateOfBirth()).toString(), occupationClass, glEndorsementInsured.getGender().name(), premiumDetailDto.getPolicyTermValue(), null,Lists.newArrayList());
                if (glEndorsementInsured.getNoOfAssured() == null && glEndorsementInsured.getDateOfBirth() != null && glEndorsementInsured.getPlanPremiumDetail().getPremiumAmount() == null) {
                    int ageOnNextBirthDate = getAgeOnNextBirthDate(glEndorsementInsured.getDateOfBirth());
                    insuredPlanProratePremium = computePremiumByApplyingAgeLoadingFactor(ageOnNextBirthDate, insuredPlanProratePremium);
                }

                glEndorsementInsured.updatePlanPremiumAmount(insuredPlanProratePremium);
                if (isNotEmpty(glEndorsementInsured.getCoveragePremiumDetails())) {
                    Set<CoveragePremiumDetail> coveragePremiumDetails = glEndorsementInsured.getCoveragePremiumDetails();
                    for (CoveragePremiumDetail coveragePremiumDetail : coveragePremiumDetails) {
                        BigDecimal insuredCoveragePremiumDetail = premiumDetailDto.getPolicyTermValue() != 365 ? computeBasicProratePremium(planPremiumDetail.getPlanId().getPlanId(),
                                        coveragePremiumDetail.getSumAssured().toPlainString(), getAgeOnNextBirthDate(glEndorsementInsured.getDateOfBirth()).toString(),
                                        occupationClass, glEndorsementInsured.getGender().name(), premiumDetailDto.getPolicyTermValue(), coveragePremiumDetail.getCoverageId().getCoverageId()) :
                                        computePlanBasicAnnualPremium(planPremiumDetail.getPlanId().getPlanId(),
                                                coveragePremiumDetail.getSumAssured().toPlainString(), getAgeOnNextBirthDate(glEndorsementInsured.getDateOfBirth()).toString(),
                                                occupationClass, glEndorsementInsured.getGender().name(), premiumDetailDto.getPolicyTermValue(), coveragePremiumDetail.getCoverageId().getCoverageId(),Lists.newArrayList());
                        if (glEndorsementInsured.getNoOfAssured() == null && glEndorsementInsured.getDateOfBirth() != null && glEndorsementInsured.getPlanPremiumDetail().getPremiumAmount() == null) {
                            int ageOnNextBirthDate = getAgeOnNextBirthDate(glEndorsementInsured.getDateOfBirth());
                            insuredCoveragePremiumDetail = computePremiumByApplyingAgeLoadingFactor(ageOnNextBirthDate, insuredCoveragePremiumDetail);
                        }
                        coveragePremiumDetail.updateWithPremium(insuredCoveragePremiumDetail);
                    }
                }
            }

        return glEndorsementInsured;
    }

}
