package com.pla.individuallife.quotation.application.service;

import com.google.common.collect.Lists;
import com.pla.core.domain.model.CoverageName;
import com.pla.core.domain.model.agent.AgentId;
import com.pla.core.domain.model.plan.premium.Premium;
import com.pla.core.query.PlanFinder;
import com.pla.core.query.PremiumFinder;
import com.pla.grouplife.quotation.query.AgentDetailDto;
import com.pla.individuallife.quotation.presentation.dto.*;
import com.pla.individuallife.quotation.query.ILQuotationDto;
import com.pla.individuallife.quotation.query.ILQuotationFinder;
import com.pla.individuallife.quotation.query.PremiumDetailDto;
import com.pla.individuallife.quotation.query.RiderPremiumDto;
import com.pla.publishedlanguage.contract.IPremiumCalculator;
import com.pla.publishedlanguage.domain.model.*;
import com.pla.sharedkernel.identifier.CoverageId;
import com.pla.sharedkernel.identifier.PlanId;
import com.pla.sharedkernel.identifier.QuotationId;
import com.pla.sharedkernel.util.PDFGeneratorUtils;
import net.sf.jasperreports.engine.JRException;
import org.joda.time.LocalDate;
import org.joda.time.Years;
import org.nthdimenzion.presentation.AppUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created by Karunakar on 5/13/2015.
 */
@Service
public class ILQuotationService {


    private ILQuotationFinder ilQuotationFinder;

    private IPremiumCalculator premiumCalculator;

    private PremiumFinder premiumFinder;

    @Autowired
    private PlanFinder planFinder;

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

        List<ComputedPremiumDto> computedPremiums = premiumCalculator.calculateBasicPremiumWithPolicyFee(premiumCalculationDto);

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
                computedPremiums = premiumCalculator.calculateBasicPremiumWithPolicyFee(premiumCalculationDto);
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


        //TODO Do not call this modal. As this is already factored.
        List<ComputedPremiumDto> computedPremiums1 = premiumCalculator.calculateModalPremium(new BasicPremiumDto(PremiumFrequency.ANNUALLY, totalPremium));
        premiumDetailDto.setMonthlyPremium(ComputedPremiumDto.getMonthlyPremium(computedPremiums1));
        premiumDetailDto.setQuarterlyPremium(ComputedPremiumDto.getQuarterlyPremium(computedPremiums1));
        premiumDetailDto.setSemiannualPremium(ComputedPremiumDto.getSemiAnnualPremium(computedPremiums1));

        return premiumDetailDto;
    }

    public List<ILQuotationDto> getAllQuotation() {
        List<ILQuotationDto> allQuotations = ilQuotationFinder.getAllQuotation();
        return allQuotations;
    }

    public List<ILQuotationDto> searchQuotation(ILSearchQuotationDto searchIlQuotationDto) {
        List<ILQuotationDto> searchQuotations = ilQuotationFinder.searchQuotation(searchIlQuotationDto.getQuotationNumber(), searchIlQuotationDto.getProposerFirstName(), searchIlQuotationDto.getProposerNrcNumber(), searchIlQuotationDto.getAgentCode());
        return searchQuotations;
    }


    public byte[] getQuotationPDF(String quotationId) throws IOException, JRException {

        ILQuotationDetailDto ilQuotationDetailDto = getIlQuotationDetailForPDF(quotationId);
        byte[] pdfData = PDFGeneratorUtils.createPDFReportByList(Arrays.asList(ilQuotationDetailDto), "jasperpdf/template/individuallife/quotation/ilQuotation.jrxml");

        return pdfData;
    }

    private ILQuotationDetailDto getIlQuotationDetailForPDF(String quotationId) {

        ILQuotationDetailDto ilQuotationDetailDto = new ILQuotationDetailDto();
        ILQuotationDto quotationMap = ilQuotationFinder.getQuotationById(quotationId);

        AgentDetailDto agentDetailDto = getAgentDetail(new AgentId(quotationMap.getAgentId()));
        ilQuotationDetailDto.setAgentBranch(agentDetailDto.getBranchName());
        ilQuotationDetailDto.setAgentCode(agentDetailDto.getAgentId());
        ilQuotationDetailDto.setAgentName(agentDetailDto.getAgentSalutation() + "  " + agentDetailDto.getAgentName());
        ilQuotationDetailDto.setAgentMobileNumber(agentDetailDto.getAgentMobileNumber());

        ProposerDto proposerDto = quotationMap.getProposer();
        ilQuotationDetailDto.setProposerName(proposerDto.getFirstName());
        ilQuotationDetailDto.setProposerEmailAddress(proposerDto.getEmailAddress());
        ilQuotationDetailDto.setProposerMobileNumber(proposerDto.getMobileNumber());

        ilQuotationDetailDto.setQuotationNumber(quotationMap.getQuotationNumber());

        ProposedAssuredDto proposedAssuredDto = quotationMap.getProposedAssured();
        ilQuotationDetailDto.setProposedAssuredName(proposedAssuredDto.getFirstName());
        ilQuotationDetailDto.setProposedAssuredDob(AppUtils.toString(proposedAssuredDto.getDateOfBirth()));
        ilQuotationDetailDto.setProposedAssuredMobileNumber(proposedAssuredDto.getMobileNumber());

        PlanDetailDto planDetailDto = quotationMap.getPlanDetailDto();
        List<ILQuotationDetailDto.CoverDetail> coverDetails = Lists.newArrayList();
        ILQuotationDetailDto.CoverDetail assuredPlanCoverDetail = ilQuotationDetailDto.new CoverDetail(planFinder.getPlanName(new PlanId(planDetailDto.getPlanId())), planDetailDto.getSumAssured().setScale(2, BigDecimal.ROUND_CEILING).toPlainString(), planDetailDto.getPolicyTerm());;
        coverDetails.add(assuredPlanCoverDetail);
        ilQuotationDetailDto.setProposedCoverPeriod(planDetailDto.getPolicyTerm() + " Years");


        for (RiderDetailDto riderDetailDto : quotationMap.getPlanDetailDto().getRiderDetails()) {
            assuredPlanCoverDetail = ilQuotationDetailDto.new CoverDetail(riderDetailDto.getCoverageName(), riderDetailDto.getSumAssured().setScale(2, BigDecimal.ROUND_CEILING).toPlainString(), riderDetailDto.getCoverTerm());
            coverDetails.add(assuredPlanCoverDetail);
        }
        ilQuotationDetailDto.setCoverDetails(coverDetails);

        PremiumDetailDto premiumDetailDto = getPremiumDetail(new QuotationId(quotationId));
        ilQuotationDetailDto.setNetAnnualPremium(premiumDetailDto.getTotalPremium().setScale(2, BigDecimal.ROUND_CEILING).toPlainString());
        ilQuotationDetailDto.setNetSemiAnnualPremium(premiumDetailDto.getSemiannualPremium().setScale(2, BigDecimal.ROUND_CEILING).toPlainString());
        ilQuotationDetailDto.setNetQuarterlyPremium(premiumDetailDto.getQuarterlyPremium().setScale(2, BigDecimal.ROUND_CEILING).toPlainString());
        ilQuotationDetailDto.setNetMonthlyPremium(premiumDetailDto.getMonthlyPremium().setScale(2, BigDecimal.ROUND_CEILING).toPlainString());

        return ilQuotationDetailDto;
    }

    public AgentDetailDto getAgentDetail(AgentId  agentId) {
        Map<String, Object> agentDetail = ilQuotationFinder.getAgentById(agentId.toString());
        AgentDetailDto agentDetailDto = new AgentDetailDto();
        agentDetailDto.setAgentId(agentId.toString());
        agentDetailDto.setBranchName((String) agentDetail.get("branchName"));
        agentDetailDto.setTeamName((String) agentDetail.get("teamName"));
        agentDetailDto.setAgentName(agentDetail.get("firstName") + " " +(agentDetail.get("lastName") == null ? "" : (String) agentDetail.get("lastName")));
        agentDetailDto.setAgentMobileNumber(agentDetail.get("mobileNumber") != null ? (String) agentDetail.get("mobileNumber") : "");
        agentDetailDto.setAgentSalutation(agentDetail.get("title") != null ? (String) agentDetail.get("title") : "");
        return agentDetailDto;
    }
}
