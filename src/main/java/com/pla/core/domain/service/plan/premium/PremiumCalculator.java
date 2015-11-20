package com.pla.core.domain.service.plan.premium;

import com.google.common.collect.Lists;
import com.pla.core.domain.model.generalinformation.*;
import com.pla.core.domain.model.plan.premium.Premium;
import com.pla.core.domain.model.plan.premium.PremiumInfluencingFactorLineItem;
import com.pla.core.domain.model.plan.premium.PremiumItem;
import com.pla.core.query.PremiumFinder;
import com.pla.core.repository.OrganizationGeneralInformationRepository;
import com.pla.core.repository.ProductLineGeneralInformationRepository;
import com.pla.publishedlanguage.contract.IPremiumCalculator;
import com.pla.publishedlanguage.domain.model.*;
import com.pla.sharedkernel.identifier.CoverageId;
import com.pla.sharedkernel.identifier.LineOfBusinessEnum;
import com.pla.sharedkernel.identifier.PlanId;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static com.pla.core.domain.exception.PremiumException.raiseInfluencingFactorMismatchException;
import static com.pla.core.domain.exception.PremiumException.raisePremiumNotFoundException;
import static org.nthdimenzion.utils.UtilValidator.isEmpty;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Created by Samir on 4/10/2015.
 */
@Component(value = "premiumCalculator")
public class PremiumCalculator implements IPremiumCalculator {

    private PremiumFinder premiumFinder;

    private OrganizationGeneralInformationRepository organizationGeneralInformationRepository;

    @Autowired
    private ProductLineGeneralInformationRepository productLineGeneralInformationRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    public PremiumCalculator(PremiumFinder premiumFinder, OrganizationGeneralInformationRepository organizationGeneralInformationRepository) {
        this.premiumFinder = premiumFinder;
        this.organizationGeneralInformationRepository = organizationGeneralInformationRepository;
    }

    @Override
    public List<ComputedPremiumDto> calculateBasicPremium(PremiumCalculationDto premiumCalculationDto, BigDecimal sumAssured, LineOfBusinessEnum lineOfBusinessEnum) {
        Premium premium = premiumFinder.findPremium(premiumCalculationDto);
        boolean hasAllInfluencingFactor = premium.hasAllInfluencingFactor(premiumCalculationDto.getInfluencingFactors());
        if (!hasAllInfluencingFactor) {
            raiseInfluencingFactorMismatchException();
        }
        Set<PremiumItem> premiumItems = premium.getPremiumItems();
        List<OrganizationGeneralInformation> organizationGeneralInformations = organizationGeneralInformationRepository.findAll();
        checkArgument(isNotEmpty(organizationGeneralInformations), "Configure Organizational Level Information");
        PremiumItem premiumItem = findPremiumItem(premiumItems, premiumCalculationDto.getPremiumCalculationInfluencingFactorItems());
        if (LineOfBusinessEnum.INDIVIDUAL_LIFE.equals(lineOfBusinessEnum)) {

            return computePremium(premium, premiumItem, organizationGeneralInformations.get(0), premiumCalculationDto.getNoOfDays(), sumAssured);
        }
        else {
            return computeGroupPremium(premium,premiumItem, premiumCalculationDto.getNoOfDays(), sumAssured,lineOfBusinessEnum);
        }
    }

    @Override
    public List<ComputedPremiumDto> calculateBasicPremiumWithPolicyFee(PremiumCalculationDto premiumCalculationDto, BigDecimal sumAssured) {
        Premium premium = premiumFinder.findPremium(premiumCalculationDto);
        boolean hasAllInfluencingFactor = premium.hasAllInfluencingFactor(premiumCalculationDto.getInfluencingFactors());
        if (!hasAllInfluencingFactor) {
            raiseInfluencingFactorMismatchException();
        }
        Set<PremiumItem> premiumItems = premium.getPremiumItems();
        PremiumItem premiumItem = findPremiumItem(premiumItems, premiumCalculationDto.getPremiumCalculationInfluencingFactorItems());
        List<OrganizationGeneralInformation> organizationGeneralInformations = organizationGeneralInformationRepository.findAll();
        checkArgument(isNotEmpty(organizationGeneralInformations));
        return computePremiumWithPolicyFee(premium, premiumItem, organizationGeneralInformations.get(0), premiumCalculationDto.getNoOfDays(),sumAssured);
    }

    @Override
    public List<ComputedPremiumDto> calculateModalPremium(BasicPremiumDto basicPremiumDto) {
        ProductLineGeneralInformation productLineGeneralInformation = productLineGeneralInformationRepository.findByProductLine(basicPremiumDto.getLineOfBusinessEnum());
        checkArgument(productLineGeneralInformation != null, "Product Line Information SetUp not available");
        Set<ModelFactorOrganizationInformation> modelFactorItems = productLineGeneralInformation.getModalFactorProcessInformation().getModelFactorItems();
        ComputedPremiumDto annualPremium = new ComputedPremiumDto(PremiumFrequency.ANNUALLY, basicPremiumDto.getBasicPremium());
        ComputedPremiumDto semiAnnualPremium = new ComputedPremiumDto(PremiumFrequency.SEMI_ANNUALLY, basicPremiumDto.getBasicPremium().multiply(ModelFactorOrganizationInformation.getSemiAnnualModalFactor(modelFactorItems)));
        ComputedPremiumDto quarterlyPremium = new ComputedPremiumDto(PremiumFrequency.QUARTERLY, basicPremiumDto.getBasicPremium().multiply(ModelFactorOrganizationInformation.getQuarterlyModalFactor(modelFactorItems)));
        ComputedPremiumDto monthlyPremium = new ComputedPremiumDto(PremiumFrequency.MONTHLY, basicPremiumDto.getBasicPremium().multiply(ModelFactorOrganizationInformation.getMonthlyModalFactor(modelFactorItems)));
        List<ComputedPremiumDto> computedPremiumDtoList = Lists.newArrayList();
        computedPremiumDtoList.add(annualPremium);
        computedPremiumDtoList.add(semiAnnualPremium);
        computedPremiumDtoList.add(quarterlyPremium);
        computedPremiumDtoList.add(monthlyPremium);
        return computedPremiumDtoList;
    }

    @Override
    public BigDecimal computeProratePremium(PremiumCalculationDto premiumCalculationDto, BigDecimal sumAssured) {
        Premium premium = premiumFinder.findPremium(premiumCalculationDto);
        boolean hasAllInfluencingFactor = premium.hasAllInfluencingFactor(premiumCalculationDto.getInfluencingFactors());
        if (!hasAllInfluencingFactor) {
            raiseInfluencingFactorMismatchException();
        }
        Set<PremiumItem> premiumItems = premium.getPremiumItems();
        PremiumItem premiumItem = findPremiumItem(premiumItems, premiumCalculationDto.getPremiumCalculationInfluencingFactorItems());
        return computeProratePremium(premium, premiumItem, premiumCalculationDto.getNoOfDays(), sumAssured);
    }

    @Override
    public List<PremiumInfluencingFactor> getPremiumInfluencingFactors(PlanId planId, LocalDate calculateDate) {
        Premium premium = premiumFinder.findPremium(planId, null, calculateDate);
        return premium.getPremiumInfluencingFactors();
    }

    @Override
    public List<PremiumInfluencingFactor> getPremiumInfluencingFactors(PlanId planId, CoverageId coverageId, LocalDate calculateDate) {
        Premium premium = premiumFinder.findPremium(planId, coverageId, calculateDate);
        return premium.getPremiumInfluencingFactors();
    }


    public List<ComputedPremiumDto> computePremium(Premium premium, PremiumItem premiumItem, OrganizationGeneralInformation organizationGeneralInformation, int noOfDays, BigDecimal sumAssured) {
        ComputedPremiumDto annualPremium = new ComputedPremiumDto(PremiumFrequency.ANNUALLY, premium.getAnnualPremium(premiumItem, organizationGeneralInformation.getDiscountFactorItems(), noOfDays, sumAssured));
        ComputedPremiumDto semiAnnualPremium = new ComputedPremiumDto(PremiumFrequency.SEMI_ANNUALLY, premium.getSemiAnnuallyPremium(premiumItem, organizationGeneralInformation.getModelFactorItems(), organizationGeneralInformation.getDiscountFactorItems(), noOfDays,sumAssured ));
        ComputedPremiumDto quarterlyPremium = new ComputedPremiumDto(PremiumFrequency.QUARTERLY, premium.getQuarterlyPremium(premiumItem, organizationGeneralInformation.getModelFactorItems(), organizationGeneralInformation.getDiscountFactorItems(), noOfDays,sumAssured ));
        ComputedPremiumDto monthlyPremium = new ComputedPremiumDto(PremiumFrequency.MONTHLY, premium.getMonthlyPremium(premiumItem, organizationGeneralInformation.getModelFactorItems(), noOfDays, sumAssured));
        List<ComputedPremiumDto> computedPremiumDtoList = Lists.newArrayList();
        computedPremiumDtoList.add(annualPremium);
        computedPremiumDtoList.add(semiAnnualPremium);
        computedPremiumDtoList.add(quarterlyPremium);
        computedPremiumDtoList.add(monthlyPremium);
        return computedPremiumDtoList;
    }

    public List<ComputedPremiumDto> computeGroupPremium(Premium premium, PremiumItem premiumItem,int noOfDays, BigDecimal sumAssured,LineOfBusinessEnum lineOfBusinessEnum) {
        Criteria policyCriteria = Criteria.where("productLine").is(lineOfBusinessEnum.name());
        Query query = new Query(policyCriteria);
        List<ProductLineGeneralInformation> productLineInformation = mongoTemplate.find(query, ProductLineGeneralInformation.class);
        checkArgument(productLineInformation != null, "Product Line Information SetUp not available");
        Set<DiscountFactorOrganizationInformation> discountFactorItems = productLineInformation.get(0).getDiscountFactorProcessInformation().getDiscountFactorItems();
        Set<ModelFactorOrganizationInformation> modelFactorItems = productLineInformation.get(0).getModalFactorProcessInformation().getModelFactorItems();
        ComputedPremiumDto annualPremium = new ComputedPremiumDto(PremiumFrequency.ANNUALLY, premium.getAnnualPremium(premiumItem,discountFactorItems, noOfDays, sumAssured));
        ComputedPremiumDto semiAnnualPremium = new ComputedPremiumDto(PremiumFrequency.SEMI_ANNUALLY, premium.getSemiAnnuallyPremium(premiumItem, modelFactorItems,discountFactorItems, noOfDays, sumAssured));
        ComputedPremiumDto quarterlyPremium = new ComputedPremiumDto(PremiumFrequency.QUARTERLY, premium.getQuarterlyPremium(premiumItem,modelFactorItems,discountFactorItems, noOfDays, sumAssured));
        ComputedPremiumDto monthlyPremium = new ComputedPremiumDto(PremiumFrequency.MONTHLY, premium.getMonthlyPremium(premiumItem,modelFactorItems, noOfDays, sumAssured));
        List<ComputedPremiumDto> computedPremiumDtoList = Lists.newArrayList();
        computedPremiumDtoList.add(annualPremium);
        computedPremiumDtoList.add(semiAnnualPremium);
        computedPremiumDtoList.add(quarterlyPremium);
        computedPremiumDtoList.add(monthlyPremium);
        return computedPremiumDtoList;
    }

    public List<ComputedPremiumDto> computePremiumWithPolicyFee(Premium premium, PremiumItem premiumItem, OrganizationGeneralInformation organizationGeneralInformation, int noOfDays, BigDecimal sumAssured) {

        Criteria policyCriteria = Criteria.where("productLine").is("INDIVIDUAL_LIFE");
        Query query = new Query(policyCriteria);
        List<ProductLineGeneralInformation> productLineInformation = mongoTemplate.find(query, ProductLineGeneralInformation.class);
        PolicyFeeProcessInformation policyFeeProcessInformation = productLineInformation.get(0).getPolicyFeeProcessInformation();

        int annual = policyFeeProcessInformation.getPolicyFeeProcessItems().stream().filter(p -> p.getPolicyFeeProcessType().name().equals("ANNUAL")).collect(Collectors.toList()).get(0).getPolicyFee();
        int semiannual = policyFeeProcessInformation.getPolicyFeeProcessItems().stream().filter(p -> p.getPolicyFeeProcessType().name().equals("SEMI_ANNUAL")).collect(Collectors.toList()).get(0).getPolicyFee();
        int quarterly = policyFeeProcessInformation.getPolicyFeeProcessItems().stream().filter(p -> p.getPolicyFeeProcessType().name().equals("QUARTERLY")).collect(Collectors.toList()).get(0).getPolicyFee();
        int monthly = policyFeeProcessInformation.getPolicyFeeProcessItems().stream().filter(p -> p.getPolicyFeeProcessType().name().equals("MONTHLY")).collect(Collectors.toList()).get(0).getPolicyFee();

        ComputedPremiumDto annualPremium = new ComputedPremiumDto(PremiumFrequency.ANNUALLY, premium.getAnnualPremium(premiumItem, organizationGeneralInformation.getDiscountFactorItems(), noOfDays,sumAssured ),BigDecimal.valueOf(annual));
        ComputedPremiumDto semiAnnualPremium = new ComputedPremiumDto(PremiumFrequency.SEMI_ANNUALLY, premium.getSemiAnnuallyPremium(premiumItem, organizationGeneralInformation.getModelFactorItems(), organizationGeneralInformation.getDiscountFactorItems(), noOfDays,sumAssured ),BigDecimal.valueOf(semiannual));
        ComputedPremiumDto quarterlyPremium = new ComputedPremiumDto(PremiumFrequency.QUARTERLY, premium.getQuarterlyPremium(premiumItem, organizationGeneralInformation.getModelFactorItems(), organizationGeneralInformation.getDiscountFactorItems(), noOfDays,sumAssured),BigDecimal.valueOf(quarterly));
        ComputedPremiumDto monthlyPremium = new ComputedPremiumDto(PremiumFrequency.MONTHLY, premium.getMonthlyPremium(premiumItem, organizationGeneralInformation.getModelFactorItems(), noOfDays, sumAssured),BigDecimal.valueOf(monthly));


        List<ComputedPremiumDto> computedPremiumDtoList = Lists.newArrayList();
        computedPremiumDtoList.add(annualPremium);
        computedPremiumDtoList.add(semiAnnualPremium);
        computedPremiumDtoList.add(quarterlyPremium);
        computedPremiumDtoList.add(monthlyPremium);
        return computedPremiumDtoList;
    }


    public BigDecimal computeProratePremium(Premium premium, PremiumItem premiumItem, int noOfDays, BigDecimal sumAssured) {
        return premium.getProratePremium(premiumItem, noOfDays,sumAssured);
    }


    public PremiumItem findPremiumItem(Set<PremiumItem> premiumItems, Set<PremiumCalculationDto.PremiumCalculationInfluencingFactorItem> premiumCalculationInfluencingFactorItems) {
        List<PremiumItem> premiumItemList = premiumItems.stream().filter(new FilterPremiumItemPredicate(premiumCalculationInfluencingFactorItems)).collect(Collectors.toList());
        if (isEmpty(premiumItemList)) {
            raisePremiumNotFoundException();
        }
        checkArgument(premiumItemList.size() == 1);
        return premiumItemList.get(0);
    }

    private boolean isMatchesInfluencingFactorAndValue(PremiumInfluencingFactorLineItem premiumInfluencingFactorLineItem, Set<PremiumCalculationDto.PremiumCalculationInfluencingFactorItem> premiumCalculationInfluencingFactorItems) {
        for (PremiumCalculationDto.PremiumCalculationInfluencingFactorItem premiumCalculationInfluencingFactorItem : premiumCalculationInfluencingFactorItems) {
            if (premiumCalculationInfluencingFactorItem.getPremiumInfluencingFactor().equals(premiumInfluencingFactorLineItem.getPremiumInfluencingFactor())
                    && premiumCalculationInfluencingFactorItem.getValue().equals(premiumInfluencingFactorLineItem.getValue())) {
                return true;
            }
        }
        return false;
    }

    private class FilterPremiumItemPredicate implements Predicate<PremiumItem> {

        private Set<PremiumCalculationDto.PremiumCalculationInfluencingFactorItem> premiumCalculationInfluencingFactorItems;

        FilterPremiumItemPredicate(Set<PremiumCalculationDto.PremiumCalculationInfluencingFactorItem> premiumCalculationInfluencingFactorItems) {
            this.premiumCalculationInfluencingFactorItems = premiumCalculationInfluencingFactorItems;
        }

        @Override
        public boolean test(PremiumItem premiumItem) {
            int noOfMatch = 0;
            for (PremiumInfluencingFactorLineItem premiumInfluencingFactorLineItem : premiumItem.getPremiumInfluencingFactorLineItems()) {
                if (isMatchesInfluencingFactorAndValue(premiumInfluencingFactorLineItem, premiumCalculationInfluencingFactorItems)) {
                    noOfMatch = noOfMatch + 1;
                    continue;
                }
            }
            return premiumCalculationInfluencingFactorItems.size() == noOfMatch;
        }
    }


}
