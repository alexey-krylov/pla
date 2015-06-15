package com.pla.grouphealth.quotation.application.command;

import com.pla.grouphealth.quotation.domain.model.*;
import com.pla.grouphealth.quotation.domain.service.GroupHealthQuotationService;
import com.pla.grouphealth.quotation.query.GHInsuredDto;
import com.pla.grouphealth.quotation.query.GHQuotationFinder;
import com.pla.grouphealth.quotation.query.GHPremiumDetailDto;
import com.pla.grouphealth.quotation.repository.GHQuotationRepository;
import com.pla.publishedlanguage.contract.IPremiumCalculator;
import com.pla.publishedlanguage.contract.IProcessInfoAdapter;
import com.pla.publishedlanguage.domain.model.ComputedPremiumDto;
import com.pla.publishedlanguage.domain.model.PremiumCalculationDto;
import com.pla.publishedlanguage.domain.model.PremiumFrequency;
import com.pla.publishedlanguage.domain.model.PremiumInfluencingFactor;
import com.pla.sharedkernel.identifier.CoverageId;
import com.pla.sharedkernel.identifier.PlanId;
import com.pla.sharedkernel.identifier.QuotationId;
import org.axonframework.commandhandling.annotation.CommandHandler;
import org.axonframework.repository.Repository;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.nthdimenzion.presentation.AppUtils.getAge;
import static org.nthdimenzion.utils.UtilValidator.isEmpty;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Created by Samir on 4/15/2015.
 */
@Component
public class GHQuotationCommandHandler {

    private Repository<GroupHealthQuotation> ghQuotationMongoRepository;

    private GroupHealthQuotationService groupHealthQuotationService;

    private IPremiumCalculator premiumCalculator;

    private GHQuotationFinder ghQuotationFinder;

    private GHQuotationRepository ghQuotationRepository;

    private IProcessInfoAdapter processInfoAdapter;


    @Autowired
    public GHQuotationCommandHandler(Repository<GroupHealthQuotation> ghQuotationMongoRepository, GroupHealthQuotationService groupHealthQuotationService, IPremiumCalculator premiumCalculator, GHQuotationFinder ghQuotationFinder, GHQuotationRepository ghQuotationRepository, IProcessInfoAdapter processInfoAdapter) {
        this.ghQuotationMongoRepository = ghQuotationMongoRepository;
        this.groupHealthQuotationService = groupHealthQuotationService;
        this.ghQuotationFinder = ghQuotationFinder;
        this.premiumCalculator = premiumCalculator;
        this.ghQuotationRepository = ghQuotationRepository;
        this.processInfoAdapter = processInfoAdapter;
    }

    @CommandHandler
    public String createQuotation(CreateGLQuotationCommand createGLQuotationCommand) {
        GroupHealthQuotation groupHealthQuotation = groupHealthQuotationService.createQuotation(createGLQuotationCommand.getAgentId(), createGLQuotationCommand.getProposerName(), createGLQuotationCommand.getUserDetails());
        ghQuotationMongoRepository.add(groupHealthQuotation);
        return groupHealthQuotation.getIdentifier().getQuotationId();
    }

    @CommandHandler
    public String updateWithProposer(UpdateGLQuotationWithProposerCommand updateGLQuotationWithProposerCommand) {
        GroupHealthQuotation groupHealthQuotation = ghQuotationMongoRepository.load(new QuotationId(updateGLQuotationWithProposerCommand.getQuotationId()));
        boolean isVersioningRequire = groupHealthQuotation.requireVersioning();
        groupHealthQuotation = groupHealthQuotationService.updateWithProposer(groupHealthQuotation, updateGLQuotationWithProposerCommand.getProposerDto(), updateGLQuotationWithProposerCommand.getUserDetails());
        if (isVersioningRequire) {
            ghQuotationMongoRepository.add(groupHealthQuotation);
        }
        return groupHealthQuotation.getIdentifier().getQuotationId();
    }

    @CommandHandler
    public String updateWithAgentDetail(com.pla.grouphealth.quotation.application.command.UpdateGLQuotationWithAgentCommand updateGLQuotationWithAgentCommand) {
        GroupHealthQuotation groupHealthQuotation = ghQuotationMongoRepository.load(new QuotationId(updateGLQuotationWithAgentCommand.getQuotationId()));
        boolean isVersioningRequire = groupHealthQuotation.requireVersioning();
        groupHealthQuotation = groupHealthQuotationService.updateWithAgent(groupHealthQuotation, updateGLQuotationWithAgentCommand.getAgentId(), updateGLQuotationWithAgentCommand.getUserDetails());
        if (isVersioningRequire) {
            ghQuotationMongoRepository.add(groupHealthQuotation);
        }
        return groupHealthQuotation.getIdentifier().getQuotationId();
    }

    @CommandHandler
    public String generateQuotation(com.pla.grouphealth.quotation.application.command.GenerateGLQuotationCommand generateGLQuotationCommand) {
        GroupHealthQuotation groupHealthQuotation = ghQuotationMongoRepository.load(new QuotationId(generateGLQuotationCommand.getQuotationId()));
        groupHealthQuotation.generateQuotation(LocalDate.now());
        return groupHealthQuotation.getIdentifier().getQuotationId();
    }

    @CommandHandler
    public String updateWithInsureDetail(UpdateGLQuotationWithInsuredCommand updateGLQuotationWithInsuredCommand) {
        List<GHInsuredDto> insuredDtos = updateGLQuotationWithInsuredCommand.getInsuredDtos();
        Set<GHInsured> insureds = insuredDtos.stream().map(new Function<GHInsuredDto, GHInsured>() {
            @Override
            public GHInsured apply(GHInsuredDto insuredDto) {
                GHInsuredDto.GHPlanPremiumDetailDto premiumDetail = insuredDto.getPlanPremiumDetail();
                String occupationClass = ghQuotationFinder.getOccupationClass(insuredDto.getOccupationClass());
                BigDecimal basicAnnualPremium = premiumDetail.getPremiumAmount() != null ? premiumDetail.getPremiumAmount() :
                        computePlanBasicAnnualPremium(premiumDetail.getPlanId(), premiumDetail.getSumAssured().toPlainString(),
                                String.valueOf(getAge(insuredDto.getDateOfBirth())), occupationClass, insuredDto.getGender().name(), 365, null);
                final GHInsuredBuilder[] insuredBuilder = {GHInsured.getInsuredBuilder(new PlanId(premiumDetail.getPlanId()), premiumDetail.getPlanCode(), basicAnnualPremium, premiumDetail.getSumAssured())};
                insuredBuilder[0].withCategory(insuredDto.getOccupationCategory()).withInsuredName(insuredDto.getSalutation(), insuredDto.getFirstName(), insuredDto.getLastName())
                        .withOccupation(insuredDto.getOccupationClass()).withInsuredNrcNumber(insuredDto.getNrcNumber()).withCompanyName(insuredDto.getCompanyName())
                        .withManNumber(insuredDto.getManNumber()).withDateOfBirth(insuredDto.getDateOfBirth()).
                        withGender(insuredDto.getGender()).withMinAndMaxAge(insuredDto.getMinAgeEntry(), insuredDto.getMaxAgeEntry()).withExistingIllness(insuredDto.getExistingIllness()).withNoOfAssured(insuredDto.getNoOfAssured());
                insuredDto.getCoveragePremiumDetails().forEach(coveragePremiumDetail -> {
                    BigDecimal coverageBasicPremium = coveragePremiumDetail.getPremium() == null ? computePlanBasicAnnualPremium(premiumDetail.getPlanId(), coveragePremiumDetail.getSumAssured().toPlainString(), String.valueOf(getAge(insuredDto.getDateOfBirth())), occupationClass, insuredDto.getGender().name(), 365, coveragePremiumDetail.getCoverageId()) : coveragePremiumDetail.getPremium();
                    final GHCoveragePremiumDetailBuilder[] ghCoveragePremiumDetailBuilder = {new GHCoveragePremiumDetailBuilder(coveragePremiumDetail.getCoverageCode(), coveragePremiumDetail.getCoverageId(), coveragePremiumDetail.getCoverageName(), coverageBasicPremium, coveragePremiumDetail.getPremiumVisibility())};
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
        GroupHealthQuotation groupHealthQuotation = ghQuotationMongoRepository.load(new QuotationId(updateGLQuotationWithInsuredCommand.getQuotationId()));
        boolean isVersioningRequire = groupHealthQuotation.requireVersioning();
        groupHealthQuotation = groupHealthQuotationService.updateInsured(groupHealthQuotation, insureds, updateGLQuotationWithInsuredCommand.getUserDetails());
        GHPremiumDetailDto premiumDetailDto = new GHPremiumDetailDto(BigDecimal.valueOf(20), 365, BigDecimal.valueOf(15), processInfoAdapter.getServiceTaxAmount());
        groupHealthQuotation = groupHealthQuotationService.updateWithPremiumDetail(groupHealthQuotation, premiumDetailDto, updateGLQuotationWithInsuredCommand.getUserDetails());
        if (isVersioningRequire) {
            ghQuotationMongoRepository.add(groupHealthQuotation);
        }
        return groupHealthQuotation.getIdentifier().getQuotationId();
    }


    @CommandHandler
    public String updateWithPremiumDetail(UpdateGHQuotationWithPremiumDetailCommand updateGLQuotationWithPremiumDetailCommand) {
        GroupHealthQuotation groupHealthQuotation = ghQuotationMongoRepository.load(new QuotationId(updateGLQuotationWithPremiumDetailCommand.getQuotationId()));
        boolean isVersioningRequire = groupHealthQuotation.requireVersioning();
        groupHealthQuotation = populateAnnualBasicPremiumOfInsured(groupHealthQuotation, updateGLQuotationWithPremiumDetailCommand.getUserDetails(), updateGLQuotationWithPremiumDetailCommand.getPremiumDetailDto());
        if (isVersioningRequire) {
            ghQuotationMongoRepository.add(groupHealthQuotation);
        }
        return groupHealthQuotation.getIdentifier().getQuotationId();
    }


    private GroupHealthQuotation populateAnnualBasicPremiumOfInsured(GroupHealthQuotation groupHealthQuotation, UserDetails userDetails, GHPremiumDetailDto premiumDetailDto) {
        if (premiumDetailDto.getPolicyTermValue() != 365) {
            Set<GHInsured> insureds = groupHealthQuotation.getInsureds();
            for (GHInsured insured : insureds) {
                GHPlanPremiumDetail planPremiumDetail = insured.getPlanPremiumDetail();
                String occupationClass = ghQuotationFinder.getOccupationClass(insured.getOccupationClass());
                BigDecimal insuredPlanProratePremium = insured.getNoOfAssured() != null ? insured.getPlanPremiumDetail().getPremiumAmount() : computeBasicProratePremium(planPremiumDetail.getPlanId().getPlanId(), planPremiumDetail.getSumAssured().toPlainString(), getAge(insured.getDateOfBirth()).toString(), occupationClass, insured.getGender().name(), premiumDetailDto.getPolicyTermValue(), null);
                insured.updatePlanPremiumAmount(insuredPlanProratePremium);
                if (isNotEmpty(insured.getPlanPremiumDetail().getCoveragePremiumDetails())) {
                    List<GHCoveragePremiumDetail> coveragePremiumDetails = insured.getPlanPremiumDetail().getCoveragePremiumDetails();
                    for (GHCoveragePremiumDetail coveragePremiumDetail : coveragePremiumDetails) {
                        BigDecimal insuredCoveragePremiumDetail = insured.getNoOfAssured() != null ? coveragePremiumDetail.getPremium() : computeBasicProratePremium(planPremiumDetail.getPlanId().getPlanId(), coveragePremiumDetail.getSumAssured().toPlainString(), getAge(insured.getDateOfBirth()).toString(), occupationClass, insured.getGender().name(), premiumDetailDto.getPolicyTermValue(), coveragePremiumDetail.getCoverageId().getCoverageId());
                        coveragePremiumDetail.updateWithPremium(insuredCoveragePremiumDetail);
                    }
                }
                for (GHInsuredDependent insuredDependent : insured.getInsuredDependents()) {
                    GHPlanPremiumDetail insuredDependentPlanPremiumDetail = insuredDependent.getPlanPremiumDetail();
                    String dependentOccupationClass = ghQuotationFinder.getOccupationClass(insuredDependent.getOccupationClass());
                    BigDecimal insuredDependentPlanProratePremium = insuredDependent.getNoOfAssured() != null ? insuredDependent.getPlanPremiumDetail().getPremiumAmount() : computeBasicProratePremium(insuredDependentPlanPremiumDetail.getPlanId().getPlanId(), insuredDependentPlanPremiumDetail.getSumAssured().toPlainString(), getAge(insuredDependent.getDateOfBirth()).toString(), dependentOccupationClass, insuredDependent.getGender().name(), premiumDetailDto.getPolicyTermValue(), null);
                    insuredDependent.updatePlanPremiumAmount(insuredDependentPlanProratePremium);
                    List<GHCoveragePremiumDetail> insuredDependentCoveragePremiumDetails = insuredDependent.getPlanPremiumDetail().getCoveragePremiumDetails();
                    if (isNotEmpty(insuredDependentCoveragePremiumDetails)) {
                        for (GHCoveragePremiumDetail insuredDependentCoveragePremiumDetail : insuredDependentCoveragePremiumDetails) {
                            BigDecimal insuredCoveragePremiumDetail = insuredDependent.getNoOfAssured() != null ? insuredDependentCoveragePremiumDetail.getPremium() : computeBasicProratePremium(insuredDependentPlanPremiumDetail.getPlanId().getPlanId(), insuredDependentCoveragePremiumDetail.getSumAssured().toPlainString(), getAge(insuredDependent.getDateOfBirth()).toString(), dependentOccupationClass, insuredDependent.getGender().name(), premiumDetailDto.getPolicyTermValue(), insuredDependentCoveragePremiumDetail.getCoverageId().getCoverageId());
                            insuredDependentCoveragePremiumDetail.updateWithPremium(insuredCoveragePremiumDetail);
                        }
                    }
                }
            }
            groupHealthQuotation = groupHealthQuotationService.updateInsured(groupHealthQuotation, insureds, userDetails);
        }
        groupHealthQuotation = groupHealthQuotationService.updateWithPremiumDetail(groupHealthQuotation, premiumDetailDto, userDetails);
        return groupHealthQuotation;
    }

    @CommandHandler
    public GroupHealthQuotation recalculatePremium(GHRecalculatedInsuredPremiumCommand glRecalculatedInsuredPremiumCommand) {
        GroupHealthQuotation groupHealthQuotation = ghQuotationRepository.findOne(new QuotationId(glRecalculatedInsuredPremiumCommand.getQuotationId()));
        groupHealthQuotation = populateAnnualBasicPremiumOfInsured(groupHealthQuotation, glRecalculatedInsuredPremiumCommand.getUserDetails(), glRecalculatedInsuredPremiumCommand.getPremiumDetailDto());
        return groupHealthQuotation;

    }

    private Set<GHInsuredDependent> getInsuredDependent(Set<GHInsuredDto.GHInsuredDependentDto> insuredDependentDtos) {
        Set<GHInsuredDependent> insuredDependents = insuredDependentDtos.stream().map(new Function<GHInsuredDto.GHInsuredDependentDto, GHInsuredDependent>() {
            @Override
            public GHInsuredDependent apply(GHInsuredDto.GHInsuredDependentDto insuredDependentDto) {
                GHInsuredDto.GHPlanPremiumDetailDto premiumDetail = insuredDependentDto.getPlanPremiumDetail();
                String occupationClass = ghQuotationFinder.getOccupationClass(insuredDependentDto.getOccupationClass());
                BigDecimal basicAnnualPremium = premiumDetail.getPremiumAmount() != null ? premiumDetail.getPremiumAmount() : computePlanBasicAnnualPremium(premiumDetail.getPlanId(), premiumDetail.getSumAssured().toPlainString(),
                        String.valueOf(getAge(insuredDependentDto.getDateOfBirth()))
                        , occupationClass, insuredDependentDto.getGender().name(), 365, null);
                final GHInsuredDependentBuilder[] insuredDependentBuilder = {GHInsuredDependent.getInsuredDependentBuilder(new PlanId(premiumDetail.getPlanId()), premiumDetail.getPlanCode(), basicAnnualPremium, premiumDetail.getSumAssured())};
                insuredDependentBuilder[0].withCategory(insuredDependentDto.getOccupationCategory()).withInsuredName(insuredDependentDto.getSalutation(), insuredDependentDto.getFirstName(), insuredDependentDto.getLastName())
                        .withInsuredNrcNumber(insuredDependentDto.getNrcNumber()).withCompanyName(insuredDependentDto.getCompanyName())
                        .withDateOfBirth(insuredDependentDto.getDateOfBirth()).withGender(insuredDependentDto.getGender()).
                        withRelationship(insuredDependentDto.getRelationship()).withOccupationClass(insuredDependentDto.getOccupationClass()).withMinAndMaxAge(insuredDependentDto.getMinAgeEntry(), insuredDependentDto.getMaxAgeEntry()).withExistingIllness(insuredDependentDto.getExistingIllness()).withNoOfAssured(insuredDependentDto.getNoOfAssured());
                insuredDependentDto.getCoveragePremiumDetails().forEach(coveragePremiumDetail -> {
                    BigDecimal coverageBasicPremium = coveragePremiumDetail.getPremium() == null ? computePlanBasicAnnualPremium(premiumDetail.getPlanId(), coveragePremiumDetail.getSumAssured().toPlainString(), String.valueOf(getAge(insuredDependentDto.getDateOfBirth())), occupationClass, insuredDependentDto.getGender().name(), 365, coveragePremiumDetail.getCoverageId()) : coveragePremiumDetail.getPremium();
                    final GHCoveragePremiumDetailBuilder[] ghCoveragePremiumDetailBuilder = {new GHCoveragePremiumDetailBuilder(coveragePremiumDetail.getCoverageCode(), coveragePremiumDetail.getCoverageId(), coveragePremiumDetail.getCoverageName(), coverageBasicPremium, coveragePremiumDetail.getPremiumVisibility())};
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

    private BigDecimal computePlanBasicAnnualPremium(String planId, String sumAssured, String age, String occupationClass, String gender, int noOfDays, String coverageId) {
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
        List<ComputedPremiumDto> computedPremiums = premiumCalculator.calculateBasicPremium(premiumCalculationDto);
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
        BigDecimal installmentPremium = premiumCalculator.computeProratePremium(premiumCalculationDto);
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

    private List<PremiumInfluencingFactor> getPremiumInfluencingFactors(String planId, String coverageId, LocalDate effectiveDate) {
        if (isEmpty(coverageId)) {
            return premiumCalculator.getPremiumInfluencingFactors(new PlanId(planId), effectiveDate);

        }
        return premiumCalculator.getPremiumInfluencingFactors(new PlanId(planId), new CoverageId(coverageId), effectiveDate);
    }

    @CommandHandler
    public void purgeGLQuotation(GHPurgeGLQuotationCommand purgeGLQuotationCommand) {
        GroupHealthQuotation groupHealthQuotation = ghQuotationMongoRepository.load(purgeGLQuotationCommand.getQuotationId());
        groupHealthQuotation.purgeQuotation();
    }

    @CommandHandler
    public void closureGLQuotation(GHClosureGLQuotationCommand closureGLQuotationCommand) {
        GroupHealthQuotation groupHealthQuotation = ghQuotationMongoRepository.load(closureGLQuotationCommand.getQuotationId());
        groupHealthQuotation.declineQuotation();
    }
}
