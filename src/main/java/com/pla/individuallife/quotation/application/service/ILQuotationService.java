package com.pla.individuallife.quotation.application.service;

import com.pla.core.domain.model.CoverageName;
import com.pla.core.domain.model.plan.premium.Premium;
import com.pla.core.query.PremiumFinder;
import com.pla.individuallife.quotation.presentation.dto.ILSearchQuotationDto;
import com.pla.individuallife.quotation.presentation.dto.ProposedAssuredDto;
import com.pla.individuallife.quotation.presentation.dto.ProposerDto;
import com.pla.individuallife.quotation.query.ILQuotationDto;
import com.pla.individuallife.quotation.query.ILQuotationFinder;
import com.pla.individuallife.quotation.query.PremiumDetailDto;
import com.pla.individuallife.quotation.query.RiderPremiumDto;
import com.pla.publishedlanguage.contract.IPremiumCalculator;
import com.pla.publishedlanguage.domain.model.*;
import com.pla.sharedkernel.identifier.CoverageId;
import com.pla.sharedkernel.identifier.PlanId;
import com.pla.sharedkernel.identifier.QuotationId;
import org.joda.time.LocalDate;
import org.joda.time.Years;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by Karunakar on 5/13/2015.
 */
@Service
public class ILQuotationService {


    private ILQuotationFinder ilQuotationFinder;

    private IPremiumCalculator premiumCalculator;

    private PremiumFinder premiumFinder;

    @Autowired
    public ILQuotationService(ILQuotationFinder ilQuotationFinder, IPremiumCalculator premiumCalculator, PremiumFinder premiumFinder) {
        this.ilQuotationFinder = ilQuotationFinder;
        this.premiumCalculator = premiumCalculator;
        this.premiumFinder = premiumFinder;
    }


    public PremiumDetailDto getPremiumDetail(QuotationId quotationId) {

        PremiumDetailDto premiumDetailDto = new PremiumDetailDto();

        Set<RiderPremiumDto> riderPremiumDtoSet = new HashSet<RiderPremiumDto>();

        Map quotation = ilQuotationFinder.getQuotationforPremiumById(quotationId.getQuotationId());

        PremiumCalculationDto premiumCalculationDto = new PremiumCalculationDto(new PlanId(quotation.get("PLANID").toString()), LocalDate.now(), PremiumFrequency.ANNUALLY, 365);

        LocalDate dob = new LocalDate((Date) quotation.get("ASSURED_DOB"));
        Integer age = Years.yearsBetween(dob, LocalDate.now()).getYears() + 1;

        Premium premium = premiumFinder.findPremium(premiumCalculationDto);
        List<PremiumInfluencingFactor> premiumInfluencingFactors = premium.getPremiumInfluencingFactors();

        for (PremiumInfluencingFactor premiumInfluencingFactor : premiumInfluencingFactors) {
            if(premiumInfluencingFactor.name().equalsIgnoreCase(String.valueOf(PremiumInfluencingFactor.SUM_ASSURED)))
                premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.SUM_ASSURED, quotation.get("SUMASSURED").toString());
            if(premiumInfluencingFactor.name().equalsIgnoreCase(String.valueOf(PremiumInfluencingFactor.GENDER)))
                premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.GENDER, quotation.get("ASSURED_GENDER").toString());
            if(premiumInfluencingFactor.name().equalsIgnoreCase(String.valueOf(PremiumInfluencingFactor.AGE)))
                premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.AGE, String.valueOf(age));
            if(premiumInfluencingFactor.name().equalsIgnoreCase(String.valueOf(PremiumInfluencingFactor.POLICY_TERM)))
                premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.POLICY_TERM, quotation.get("POLICYTERM").toString());
            if(premiumInfluencingFactor.name().equalsIgnoreCase(String.valueOf(PremiumInfluencingFactor.PREMIUM_PAYMENT_TERM)))
                premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.PREMIUM_PAYMENT_TERM, quotation.get("PREMIUMPAYMENT_TERM").toString());
            if(premiumInfluencingFactor.name().equalsIgnoreCase(String.valueOf(PremiumInfluencingFactor.OCCUPATION_CLASS)))
                premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.OCCUPATION_CLASS, quotation.get("ASSURED_OCCUPATION").toString());
        }

        List<ComputedPremiumDto> computedPremiums = premiumCalculator.calculateBasicPremium(premiumCalculationDto);

        premiumDetailDto.setPlanAnnualPremium(ComputedPremiumDto.getAnnualPremium(computedPremiums));

        List<Map<String, Object>> riderList = ilQuotationFinder.getQuotationforPremiumWithRiderById(quotationId.getQuotationId());

        if(riderList != null) {
            for (Map  rider : riderList){
                premiumCalculationDto = new PremiumCalculationDto(new PlanId(quotation.get("PLANID").toString()), LocalDate.now(), PremiumFrequency.ANNUALLY, 365);
                premiumCalculationDto = premiumCalculationDto.addCoverage(new CoverageId(rider.get("COVERAGEID").toString()));
                premiumInfluencingFactors = premiumFinder.findPremium(premiumCalculationDto).getPremiumInfluencingFactors();

                for (PremiumInfluencingFactor premiumInfluencingFactor : premiumInfluencingFactors) {
                    if(premiumInfluencingFactor.name().equalsIgnoreCase(String.valueOf(PremiumInfluencingFactor.SUM_ASSURED)))
                        premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.SUM_ASSURED, rider.get("RIDER_SA").toString());
                    if(premiumInfluencingFactor.name().equalsIgnoreCase(String.valueOf(PremiumInfluencingFactor.GENDER)))
                        premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.GENDER, quotation.get("ASSURED_GENDER").toString());
                    if(premiumInfluencingFactor.name().equalsIgnoreCase(String.valueOf(PremiumInfluencingFactor.AGE)))
                        premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.AGE, String.valueOf(age));
                    if(premiumInfluencingFactor.name().equalsIgnoreCase(String.valueOf(PremiumInfluencingFactor.POLICY_TERM)))
                        premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.POLICY_TERM, rider.get("COVERTERM").toString());
                    if(premiumInfluencingFactor.name().equalsIgnoreCase(String.valueOf(PremiumInfluencingFactor.PREMIUM_PAYMENT_TERM)))
                        premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.PREMIUM_PAYMENT_TERM, rider.get("RIDER_PREMIUM_WAIVER").toString());
                    if(premiumInfluencingFactor.name().equalsIgnoreCase(String.valueOf(PremiumInfluencingFactor.OCCUPATION_CLASS)))
                        premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.OCCUPATION_CLASS, quotation.get("ASSURED_OCCUPATION").toString());
                }
                computedPremiums = premiumCalculator.calculateBasicPremium(premiumCalculationDto);
                RiderPremiumDto rd = new RiderPremiumDto();
                rd.setCoverageId(new CoverageId (rider.get("COVERAGEID").toString()));
                if(rider.get("COVERAGENAME") != null )
                    rd.setCoverageName(new CoverageName (rider.get("COVERAGENAME").toString()));
                computedPremiums = premiumCalculator.calculateBasicPremium(premiumCalculationDto);
                rd.setAnnualPremium(ComputedPremiumDto.getAnnualPremium(computedPremiums));
                riderPremiumDtoSet.add(rd);
            }
        }

        BigDecimal totalPremium = premiumDetailDto.getPlanAnnualPremium();
        if(riderPremiumDtoSet.size() > 0) {
            premiumDetailDto.setRiderPremiumDtos(riderPremiumDtoSet);
            totalPremium = totalPremium.add(riderPremiumDtoSet.stream().map(RiderPremiumDto::getAnnualPremium).reduce(BigDecimal::add).get());
        }
        premiumDetailDto.setTotalPremium(totalPremium);


        List<ComputedPremiumDto> computedPremiums1 = premiumCalculator.calculateModalPremium(new BasicPremiumDto(PremiumFrequency.ANNUALLY, totalPremium));
        premiumDetailDto.setMonthlyPremium(ComputedPremiumDto.getMonthlyPremium(computedPremiums1));
        premiumDetailDto.setQuarterlyPremium(ComputedPremiumDto.getQuarterlyPremium(computedPremiums1));
        premiumDetailDto.setSemiannualPremium(ComputedPremiumDto.getSemiAnnualPremium(computedPremiums1));

        return premiumDetailDto;
    }

    public PremiumDetailDto getReCalculatePremium(PremiumDetailDto premiumDetailDto) {
        return new PremiumDetailDto();
    }


    public ProposerDto getProposerDetail(QuotationId quotationId) {
//        Map quotation = ilQuotationFinder.getQuotationById(quotationId.getQuotationId());
//        Proposer proposer = (Proposer) quotation.get("proposer");
        return new ProposerDto(null);
    }

    public ProposedAssuredDto getAssuredDetail(QuotationId quotationId) {
       /* Map quotation = ilQuotationFinder.getQuotationById(quotationId.getQuotationId());
        ProposedAssured assured = (ProposedAssured) quotation.get("proposedAssured");*/
        return new ProposedAssuredDto(null);
    }


    public List<ILQuotationDto> getAllQuotation() {
        List<Map> allQuotations = ilQuotationFinder.getAllQuotation();
        List<ILQuotationDto> ILQuotationDtoList = allQuotations.stream().map(new TransformToILQuotationDto()).collect(Collectors.toList());
        return ILQuotationDtoList;
    }

    public List<ILQuotationDto> searchQuotation(ILSearchQuotationDto searchGlQuotationDto) {
        return getAllQuotation();
    }


    private class TransformToILQuotationDto implements Function<Map, ILQuotationDto> {

        @Override
        public ILQuotationDto apply(Map map) {
            return null;
        }
    }
}
