package com.pla.grouphealth.quotation.application.command;

import com.pla.grouphealth.quotation.domain.model.*;
import com.pla.grouphealth.quotation.domain.service.GroupHealthQuotationService;
import com.pla.grouphealth.quotation.query.GHQuotationFinder;
import com.pla.grouphealth.quotation.query.InsuredDto;
import com.pla.grouphealth.quotation.query.PremiumDetailDto;
import com.pla.grouphealth.quotation.repository.GHQuotationRepository;
import com.pla.publishedlanguage.contract.IPremiumCalculator;
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
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.nthdimenzion.presentation.AppUtils.getAge;
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


    @Autowired
    public GHQuotationCommandHandler(Repository<GroupHealthQuotation> ghQuotationMongoRepository, GroupHealthQuotationService groupHealthQuotationService, IPremiumCalculator premiumCalculator, GHQuotationFinder ghQuotationFinder, GHQuotationRepository ghQuotationRepository) {
        this.ghQuotationMongoRepository = ghQuotationMongoRepository;
        this.groupHealthQuotationService = groupHealthQuotationService;
        this.ghQuotationFinder = ghQuotationFinder;
        this.premiumCalculator = premiumCalculator;
        this.ghQuotationRepository = ghQuotationRepository;
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
        List<InsuredDto> insuredDtos = updateGLQuotationWithInsuredCommand.getInsuredDtos();
        Set<GHInsured> insureds = insuredDtos.stream().map(new Function<InsuredDto, GHInsured>() {
            @Override
            public GHInsured apply(InsuredDto insuredDto) {
                InsuredDto.PlanPremiumDetailDto premiumDetail = insuredDto.getPlanPremiumDetail();
                String occupationClass = ghQuotationFinder.getOccupationClass(insuredDto.getOccupationClass());
                BigDecimal basicAnnualPremium = premiumDetail.getPremiumAmount() != null ? premiumDetail.getPremiumAmount() :
                        computePlanBasicAnnualPremium(premiumDetail.getPlanId(), premiumDetail.getSumAssured().toPlainString(), String.valueOf(getAge(insuredDto.getDateOfBirth())), occupationClass, insuredDto.getGender().name(), 365, null);
                final GHInsuredBuilder[] insuredBuilder = {GHInsured.getInsuredBuilder(new PlanId(premiumDetail.getPlanId()), premiumDetail.getPlanCode(), basicAnnualPremium, premiumDetail.getSumAssured())};
                insuredBuilder[0].withCategory(insuredDto.getCategory()).withInsuredName(insuredDto.getSalutation(), insuredDto.getFirstName(), insuredDto.getLastName())
                        .withAnnualIncome(insuredDto.getAnnualIncome()).withOccupation(insuredDto.getOccupationClass()).withInsuredNrcNumber(insuredDto.getNrcNumber()).withCompanyName(insuredDto.getCompanyName())
                        .withManNumber(insuredDto.getManNumber()).withDateOfBirth(insuredDto.getDateOfBirth()).withGender(insuredDto.getGender());
                insuredDto.getCoveragePremiumDetails().forEach(coveragePremiumDetail -> {
                    BigDecimal coverageBasicPremium = computePlanBasicAnnualPremium(premiumDetail.getPlanId(), coveragePremiumDetail.getSumAssured().toPlainString(), String.valueOf(getAge(insuredDto.getDateOfBirth())), null, insuredDto.getGender().name(), 365, coveragePremiumDetail.getCoverageId());
                    insuredBuilder[0] = insuredBuilder[0].withCoveragePremiumDetail(coveragePremiumDetail.getCoverageName(), coveragePremiumDetail.getCoverageCode(), coveragePremiumDetail.getCoverageId(), coverageBasicPremium);
                });
                Set<GHInsuredDependent> insuredDependents = getInsuredDependent(insuredDto.getInsuredDependents());
                insuredBuilder[0] = insuredBuilder[0].withDependents(insuredDependents);
                return insuredBuilder[0].build();
            }
        }).collect(Collectors.toSet());
        GroupHealthQuotation groupHealthQuotation = ghQuotationMongoRepository.load(new QuotationId(updateGLQuotationWithInsuredCommand.getQuotationId()));
        boolean isVersioningRequire = groupHealthQuotation.requireVersioning();
        groupHealthQuotation = groupHealthQuotationService.updateInsured(groupHealthQuotation, insureds, updateGLQuotationWithInsuredCommand.getUserDetails());
        PremiumDetailDto premiumDetailDto = new PremiumDetailDto(BigDecimal.valueOf(20), 365);
        groupHealthQuotation = groupHealthQuotationService.updateWithPremiumDetail(groupHealthQuotation, premiumDetailDto, updateGLQuotationWithInsuredCommand.getUserDetails());
        if (isVersioningRequire) {
            ghQuotationMongoRepository.add(groupHealthQuotation);
        }
        return groupHealthQuotation.getIdentifier().getQuotationId();
    }


    @CommandHandler
    public String updateWithPremiumDetail(com.pla.grouphealth.quotation.application.command.UpdateGLQuotationWithPremiumDetailCommand updateGLQuotationWithPremiumDetailCommand) {
        GroupHealthQuotation groupHealthQuotation = ghQuotationMongoRepository.load(new QuotationId(updateGLQuotationWithPremiumDetailCommand.getQuotationId()));
        boolean isVersioningRequire = groupHealthQuotation.requireVersioning();
        groupHealthQuotation = populateAnnualBasicPremiumOfInsured(groupHealthQuotation, updateGLQuotationWithPremiumDetailCommand.getUserDetails(), updateGLQuotationWithPremiumDetailCommand.getPremiumDetailDto());
        if (isVersioningRequire) {
            ghQuotationMongoRepository.add(groupHealthQuotation);
        }
        return groupHealthQuotation.getIdentifier().getQuotationId();
    }


    private GroupHealthQuotation populateAnnualBasicPremiumOfInsured(GroupHealthQuotation groupHealthQuotation, UserDetails userDetails, PremiumDetailDto premiumDetailDto) {
        if (premiumDetailDto.getPolicyTermValue() != 365) {
            Set<GHInsured> insureds = groupHealthQuotation.getInsureds();
            for (GHInsured insured : insureds) {
                GHPlanPremiumDetail planPremiumDetail = insured.getPlanPremiumDetail();
                String occupationClass = ghQuotationFinder.getOccupationClass(insured.getOccupationClass());
                BigDecimal insuredPlanProratePremium = computeBasicProratePremium(planPremiumDetail.getPlanId().getPlanId(), planPremiumDetail.getSumAssured().toPlainString(), getAge(insured.getDateOfBirth()).toString(), occupationClass, insured.getGender().name(), premiumDetailDto.getPolicyTermValue(), null);
                insured.updatePlanPremiumAmount(insuredPlanProratePremium);
                if (isNotEmpty(insured.getCoveragePremiumDetails())) {
                    Set<CoveragePremiumDetail> coveragePremiumDetails = insured.getCoveragePremiumDetails();
                    for (CoveragePremiumDetail coveragePremiumDetail : coveragePremiumDetails) {
                        BigDecimal insuredCoveragePremiumDetail = computeBasicProratePremium(planPremiumDetail.getPlanId().getPlanId(), coveragePremiumDetail.getSumAssured().toPlainString(), getAge(insured.getDateOfBirth()).toString(), occupationClass, insured.getGender().name(), premiumDetailDto.getPolicyTermValue(), coveragePremiumDetail.getCoverageId().getCoverageId());
                        coveragePremiumDetail.updateWithPremium(insuredCoveragePremiumDetail);
                    }
                }
                for (GHInsuredDependent insuredDependent : insured.getInsuredDependents()) {
                    GHPlanPremiumDetail insuredDependentPlanPremiumDetail = insuredDependent.getPlanPremiumDetail();
                    String dependentOccupationClass = ghQuotationFinder.getOccupationClass(insuredDependent.getOccupationClass());
                    BigDecimal insuredDependentPlanProratePremium = computeBasicProratePremium(insuredDependentPlanPremiumDetail.getPlanId().getPlanId(), insuredDependentPlanPremiumDetail.getSumAssured().toPlainString(), getAge(insuredDependent.getDateOfBirth()).toString(), dependentOccupationClass, insuredDependent.getGender().name(), premiumDetailDto.getPolicyTermValue(), null);
                    insuredDependent.updatePlanPremiumAmount(insuredDependentPlanProratePremium);
                    Set<CoveragePremiumDetail> insuredDependentCoveragePremiumDetails = insuredDependent.getCoveragePremiumDetails();
                    if (isNotEmpty(insuredDependentCoveragePremiumDetails)) {
                        for (CoveragePremiumDetail insuredDependentCoveragePremiumDetail : insuredDependentCoveragePremiumDetails) {
                            BigDecimal insuredCoveragePremiumDetail = computeBasicProratePremium(insuredDependentPlanPremiumDetail.getPlanId().getPlanId(), insuredDependentCoveragePremiumDetail.getSumAssured().toPlainString(), getAge(insuredDependent.getDateOfBirth()).toString(), dependentOccupationClass, insuredDependent.getGender().name(), premiumDetailDto.getPolicyTermValue(), insuredDependentCoveragePremiumDetail.getCoverageId().getCoverageId());
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
    public GroupHealthQuotation recalculatePremium(GLRecalculatedInsuredPremiumCommand glRecalculatedInsuredPremiumCommand) {
        GroupHealthQuotation groupHealthQuotation = ghQuotationRepository.findOne(new QuotationId(glRecalculatedInsuredPremiumCommand.getQuotationId()));
        groupHealthQuotation = populateAnnualBasicPremiumOfInsured(groupHealthQuotation, glRecalculatedInsuredPremiumCommand.getUserDetails(), glRecalculatedInsuredPremiumCommand.getPremiumDetailDto());
        return groupHealthQuotation;

    }

    private Set<GHInsuredDependent> getInsuredDependent(Set<InsuredDto.InsuredDependentDto> insuredDependentDtos) {
        Set<GHInsuredDependent> insuredDependents = insuredDependentDtos.stream().map(new Function<InsuredDto.InsuredDependentDto, GHInsuredDependent>() {
            @Override
            public GHInsuredDependent apply(InsuredDto.InsuredDependentDto insuredDependentDto) {
                InsuredDto.PlanPremiumDetailDto premiumDetail = insuredDependentDto.getPlanPremiumDetail();
                BigDecimal basicAnnualPremium = computePlanBasicAnnualPremium(premiumDetail.getPlanId(), premiumDetail.getSumAssured().toPlainString(),
                        String.valueOf(getAge(insuredDependentDto.getDateOfBirth()))
                        , null, insuredDependentDto.getGender().name(), 365, null);
                final GHInsuredDependentBuilder[] insuredDependentBuilder = {GHInsuredDependent.getInsuredDependentBuilder(new PlanId(premiumDetail.getPlanId()), premiumDetail.getPlanCode(), basicAnnualPremium, premiumDetail.getSumAssured())};
                insuredDependentBuilder[0].withCategory(insuredDependentDto.getCategory()).withInsuredName(insuredDependentDto.getSalutation(), insuredDependentDto.getFirstName(), insuredDependentDto.getLastName())
                        .withInsuredNrcNumber(insuredDependentDto.getNrcNumber()).withCompanyName(insuredDependentDto.getCompanyName())
                        .withDateOfBirth(insuredDependentDto.getDateOfBirth()).withGender(insuredDependentDto.getGender()).withRelationship(insuredDependentDto.getRelationship());
                insuredDependentDto.getCoveragePremiumDetails().forEach(coveragePremiumDetail -> {
                    BigDecimal coverageBasicPremium = computePlanBasicAnnualPremium(premiumDetail.getPlanId(), coveragePremiumDetail.getSumAssured().toPlainString(), String.valueOf(getAge(insuredDependentDto.getDateOfBirth())), null, insuredDependentDto.getGender().name(), 365, coveragePremiumDetail.getCoverageId());
                    insuredDependentBuilder[0] = insuredDependentBuilder[0].withCoveragePremiumDetail(coveragePremiumDetail.getCoverageName(), coveragePremiumDetail.getCoverageCode(), coveragePremiumDetail.getCoverageId(), coverageBasicPremium);
                });
                return insuredDependentBuilder[0].build();
            }
        }).collect(Collectors.toSet());
        return insuredDependents;
    }

    private BigDecimal computePlanBasicAnnualPremium(String planId, String sumAssured, String age, String occupationClass, String gender, int noOfDays, String coverageId) {
        PremiumCalculationDto premiumCalculationDto = new PremiumCalculationDto(new PlanId(planId), LocalDate.now(), PremiumFrequency.ANNUALLY, noOfDays);
        premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.SUM_ASSURED, sumAssured);
        premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.AGE, age);
        if (isNotEmpty(occupationClass)) {
            premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.OCCUPATION_CLASS, occupationClass);
        }
        premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.GENDER, gender);
        premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.POLICY_TERM, String.valueOf(1));
        if (isNotEmpty(coverageId)) {
            premiumCalculationDto = premiumCalculationDto.addCoverage(new CoverageId(coverageId));
        }
        List<ComputedPremiumDto> computedPremiums = premiumCalculator.calculateBasicPremium(premiumCalculationDto);
        BigDecimal annualBasicPremium = ComputedPremiumDto.getAnnualPremium(computedPremiums);
        return annualBasicPremium;
    }

    private BigDecimal computeBasicProratePremium(String planId, String sumAssured, String age, String occupationClass, String gender, int noOfDays, String coverageId) {
        PremiumCalculationDto premiumCalculationDto = new PremiumCalculationDto(new PlanId(planId), LocalDate.now(), PremiumFrequency.ANNUALLY, noOfDays);
        premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.SUM_ASSURED, sumAssured);
        premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.AGE, age);
        if (isNotEmpty(occupationClass)) {
            premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.OCCUPATION_CLASS, occupationClass);
        }
        premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.GENDER, gender);
        premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.POLICY_TERM, String.valueOf(1));
        if (isNotEmpty(coverageId)) {
            premiumCalculationDto = premiumCalculationDto.addCoverage(new CoverageId(coverageId));
        }
        BigDecimal installmentPremium = premiumCalculator.computeProratePremium(premiumCalculationDto);
        return installmentPremium;
    }

    @CommandHandler
    public void purgeGLQuotation(com.pla.grouphealth.quotation.application.command.PurgeGLQuotationCommand purgeGLQuotationCommand) {
        GroupHealthQuotation groupHealthQuotation = ghQuotationMongoRepository.load(purgeGLQuotationCommand.getQuotationId());
        groupHealthQuotation.purgeQuotation();
    }

    @CommandHandler
    public void closureGLQuotation(com.pla.grouphealth.quotation.application.command.ClosureGLQuotationCommand closureGLQuotationCommand) {
        GroupHealthQuotation groupHealthQuotation = ghQuotationMongoRepository.load(closureGLQuotationCommand.getQuotationId());
        groupHealthQuotation.declineQuotation();
    }
}
