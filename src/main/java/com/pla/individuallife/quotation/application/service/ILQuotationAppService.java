package com.pla.individuallife.quotation.application.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pla.core.domain.model.CoverageName;
import com.pla.core.domain.model.agent.AgentId;
import com.pla.core.domain.model.plan.premium.Premium;
import com.pla.core.query.PlanFinder;
import com.pla.core.query.PremiumFinder;
import com.pla.individuallife.quotation.presentation.dto.*;
import com.pla.individuallife.quotation.query.*;
import com.pla.publishedlanguage.contract.IPremiumCalculator;
import com.pla.publishedlanguage.domain.model.ComputedPremiumDto;
import com.pla.publishedlanguage.domain.model.PremiumCalculationDto;
import com.pla.publishedlanguage.domain.model.PremiumFrequency;
import com.pla.publishedlanguage.domain.model.PremiumInfluencingFactor;
import com.pla.sharedkernel.identifier.CoverageId;
import com.pla.sharedkernel.identifier.PlanId;
import com.pla.sharedkernel.identifier.QuotationId;
import com.pla.sharedkernel.util.PDFGeneratorUtils;
import net.sf.jasperreports.engine.JRException;
import org.apache.velocity.app.VelocityEngine;
import org.joda.time.LocalDate;
import org.joda.time.Years;
import org.nthdimenzion.common.AppConstants;
import org.nthdimenzion.presentation.AppUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.velocity.VelocityEngineUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

import static org.nthdimenzion.utils.UtilValidator.isEmpty;

/**
 * Created by Karunakar on 5/13/2015.
 */
@Service
public class ILQuotationAppService {


    private ILQuotationFinder ilQuotationFinder;

    private IPremiumCalculator premiumCalculator;

    private PremiumFinder premiumFinder;

    @Autowired
    private VelocityEngine velocityEngine;

    @Autowired
    private PlanFinder planFinder;

    @Autowired
    public ILQuotationAppService(ILQuotationFinder ilQuotationFinder, IPremiumCalculator premiumCalculator, PremiumFinder premiumFinder) {
        this.ilQuotationFinder = ilQuotationFinder;
        this.premiumCalculator = premiumCalculator;
        this.premiumFinder = premiumFinder;
    }

    public PremiumDetailDto getPremiumDetail(QuotationId quotationId) {

        PremiumDetailDto premiumDetailDto = new PremiumDetailDto();

        Set<RiderPremiumDto> riderPremiumDtoSet = new HashSet<RiderPremiumDto>();

        Map quotation = ilQuotationFinder.getQuotationforPremiumById(quotationId.getQuotationId());

        PremiumCalculationDto premiumCalculationDto = new PremiumCalculationDto(new PlanId(quotation.get("PLANID").toString()), LocalDate.now(), PremiumFrequency.ANNUALLY, 365);

        LocalDate dob = new LocalDate(quotation.get("ASSURED_DOB"));
        Integer age = Years.yearsBetween(dob, LocalDate.now()).getYears() + 1;

        Premium premium = premiumFinder.findPremium(premiumCalculationDto);
        List<PremiumInfluencingFactor> premiumInfluencingFactors = premium.getPremiumInfluencingFactors();

        for (PremiumInfluencingFactor premiumInfluencingFactor : premiumInfluencingFactors) {
            if(premiumInfluencingFactor.name().equalsIgnoreCase(String.valueOf(PremiumInfluencingFactor.SUM_ASSURED)))
                premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.SUM_ASSURED, ((Integer) ((BigDecimal) quotation.get("SUMASSURED")).intValue()).toString());
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

        BigDecimal totalPremium = premiumDetailDto.getPlanAnnualPremium();
        BigDecimal semiAnnualPremium = ComputedPremiumDto.getSemiAnnualPremium(computedPremiums);
        BigDecimal quarterlyPremium = ComputedPremiumDto.getQuarterlyPremium(computedPremiums);
        BigDecimal monthlyPremium = ComputedPremiumDto.getMonthlyPremium(computedPremiums);

        List<Map<String, Object>> riderList = ilQuotationFinder.getQuotationforPremiumWithRiderById(quotationId.getQuotationId());
        if(riderList != null) {
            for (Map  rider : riderList) {
                if ( (new BigDecimal(rider.get("RIDER_SA").toString()).compareTo(new BigDecimal("0.0")) != 0) || (Integer.parseInt(rider.get("COVERTERM").toString()) != 0) ) {
                    premiumCalculationDto = new PremiumCalculationDto(new PlanId(quotation.get("PLANID").toString()), LocalDate.now(), PremiumFrequency.ANNUALLY, 365);
                    premiumCalculationDto = premiumCalculationDto.addCoverage(new CoverageId(rider.get("COVERAGEID").toString()));
                    premiumInfluencingFactors = premiumFinder.findPremium(premiumCalculationDto).getPremiumInfluencingFactors();

                    for (PremiumInfluencingFactor premiumInfluencingFactor : premiumInfluencingFactors) {
                        if (premiumInfluencingFactor.name().equalsIgnoreCase(String.valueOf(PremiumInfluencingFactor.SUM_ASSURED)))
                            premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.SUM_ASSURED, ((Integer) ((BigDecimal)  rider.get("RIDER_SA")).intValue()).toString());
                        if (premiumInfluencingFactor.name().equalsIgnoreCase(String.valueOf(PremiumInfluencingFactor.GENDER)))
                            premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.GENDER, quotation.get("ASSURED_GENDER").toString());
                        if (premiumInfluencingFactor.name().equalsIgnoreCase(String.valueOf(PremiumInfluencingFactor.AGE)))
                            premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.AGE, String.valueOf(age));
                        if (premiumInfluencingFactor.name().equalsIgnoreCase(String.valueOf(PremiumInfluencingFactor.POLICY_TERM)))
                            premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.POLICY_TERM, rider.get("COVERTERM").toString());
                        if (premiumInfluencingFactor.name().equalsIgnoreCase(String.valueOf(PremiumInfluencingFactor.PREMIUM_PAYMENT_TERM)))
                            premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.PREMIUM_PAYMENT_TERM, rider.get("RIDER_PREMIUM_WAIVER").toString());
                        if (premiumInfluencingFactor.name().equalsIgnoreCase(String.valueOf(PremiumInfluencingFactor.OCCUPATION_CLASS)))
                            premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.OCCUPATION_CLASS, quotation.get("ASSURED_OCCUPATION").toString());
                    }
                    RiderPremiumDto rd = new RiderPremiumDto();
                    rd.setCoverageId(new CoverageId(rider.get("COVERAGEID").toString()));
                    if (rider.get("COVERAGENAME") != null)
                        rd.setCoverageName(new CoverageName(rider.get("COVERAGENAME").toString()));
                    computedPremiums = premiumCalculator.calculateBasicPremium(premiumCalculationDto);
                    rd.setAnnualPremium(ComputedPremiumDto.getAnnualPremium(computedPremiums));
                    totalPremium = totalPremium.add(ComputedPremiumDto.getAnnualPremium(computedPremiums));
                    semiAnnualPremium = semiAnnualPremium.add(ComputedPremiumDto.getSemiAnnualPremium(computedPremiums));
                    quarterlyPremium = quarterlyPremium.add(ComputedPremiumDto.getQuarterlyPremium(computedPremiums));
                    monthlyPremium = monthlyPremium.add(ComputedPremiumDto.getMonthlyPremium(computedPremiums));
                    riderPremiumDtoSet.add(rd);
                }
            }
        }
        premiumDetailDto.setRiderPremiumDtos(riderPremiumDtoSet);
        premiumDetailDto.setTotalPremium(totalPremium);
        premiumDetailDto.setPlanName(planFinder.getPlanName(new PlanId(quotation.get("PLANID").toString())));
        premiumDetailDto.setAnnualPremium(totalPremium);
        premiumDetailDto.setMonthlyPremium(monthlyPremium);
        premiumDetailDto.setQuarterlyPremium(quarterlyPremium);
        premiumDetailDto.setSemiannualPremium(semiAnnualPremium);
        return premiumDetailDto;
    }

    public List<ILSearchQuotationResultDto> searchQuotation(ILSearchQuotationDto searchIlQuotationDto) {
        List<ILSearchQuotationResultDto> searchQuotations = ilQuotationFinder.searchQuotation(searchIlQuotationDto.getQuotationNumber(),
                searchIlQuotationDto.getProposerName(), searchIlQuotationDto.getProposerNrcNumber(), searchIlQuotationDto.getAgentCode(),
                searchIlQuotationDto.getQuotationStatus());
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
        ilQuotationDetailDto.setAgentName(agentDetailDto.getAgentName());
        ilQuotationDetailDto.setAgentMobileNumber(agentDetailDto.getAgentMobileNumber());

        ProposerDto proposerDto = quotationMap.getProposer();
        ilQuotationDetailDto.setProposerName(proposerDto.getTitle() + " " + proposerDto.getFirstName() + " " + proposerDto.getSurname());
        String emailAddress = proposerDto.getEmailAddress() != null ? proposerDto.getEmailAddress() : "-";
        ilQuotationDetailDto.setProposerEmailAddress(emailAddress);
        ilQuotationDetailDto.setProposerMobileNumber(proposerDto.getMobileNumber());

        ilQuotationDetailDto.setQuotationNumber(quotationMap.getQuotationNumber());

        ProposedAssuredDto proposedAssuredDto = quotationMap.getProposedAssured();
        ilQuotationDetailDto.setProposedAssuredName(proposedAssuredDto.getTitle() + " " + proposedAssuredDto.getFirstName() + " " + proposedAssuredDto.getSurname());
        ilQuotationDetailDto.setProposedAssuredDob(AppUtils.toString(proposedAssuredDto.getDateOfBirth()));
        ilQuotationDetailDto.setProposedAssuredMobileNumber(proposedAssuredDto.getMobileNumber());

        PlanDetailDto planDetailDto = quotationMap.getPlanDetailDto();
        List<ILQuotationDetailDto.CoverDetail> coverDetails = Lists.newArrayList();
        ILQuotationDetailDto.CoverDetail assuredPlanCoverDetail = ilQuotationDetailDto.new CoverDetail(planFinder.getPlanName(new PlanId(planDetailDto.getPlanId())), planDetailDto.getSumAssured().setScale(2, BigDecimal.ROUND_CEILING).toPlainString(), planDetailDto.getPolicyTerm());
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

    /*public AgentDetailDto getAgentDetail(AgentId  agentId) {
        Map<String, Object> agentDetail = ilQuotationFinder.getAgentById(agentId.toString());
        AgentDetailDto agentDetailDto = new AgentDetailDto();
        agentDetailDto.setAgentId(agentId.toString());
        agentDetailDto.setBranchName((String) agentDetail.get("branchName"));
        agentDetailDto.setTeamName((String) agentDetail.get("teamName"));
        agentDetailDto.setAgentName(agentDetail.get("firstName") + " " +(agentDetail.get("lastName") == null ? "" : (String) agentDetail.get("lastName")));
        agentDetailDto.setAgentMobileNumber(agentDetail.get("mobileNumber") != null ? (String) agentDetail.get("mobileNumber") : "");
        agentDetailDto.setAgentSalutation(agentDetail.get("title") != null ? (String) agentDetail.get("title") : "");
        return agentDetailDto;
    }*/

    public ILQuotationMailDto getPreScriptedEmail(String quotationId) {
        ILQuotationDto ilQuotationDto = ilQuotationFinder.getQuotationById(quotationId);
        String subject = "PLA Insurance - Individual Life - Quotation ID : " + ilQuotationDto.getQuotationNumber();
        String mailAddress = ilQuotationDto.getProposer().getEmailAddress();
        mailAddress = isEmpty(mailAddress) ? "" : mailAddress;
        Map<String, Object> emailContent = Maps.newHashMap();
        emailContent.put("mailSentDate", ilQuotationDto.getQuotationGeneratedOn().toString(AppConstants.DD_MM_YYY_FORMAT));
        emailContent.put("contactPersonName", ilQuotationDto.getProposer().getFirstName());
        emailContent.put("proposerName", ilQuotationDto.getProposer().getFirstName());
        Map<String, Object> emailContentMap = Maps.newHashMap();
        emailContentMap.put("emailContent", emailContent);
        String emailBody = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "emailtemplate/individuallife/quotation/individuallifeQuotationTemplate.vm", emailContentMap);
        ILQuotationMailDto dto = new ILQuotationMailDto(subject, emailBody, new String[]{mailAddress});
        dto.setQuotationId(quotationId);
        dto.setQuotationNumber(ilQuotationDto.getQuotationNumber());
        return dto;
    }

    public AgentDetailDto getAgentDetail(AgentId  agentId) {
        Map<String, Object> agentDetail = ilQuotationFinder.getAgentById(agentId.toString());
        AgentDetailDto agentDetailDto = new AgentDetailDto();
        agentDetailDto.setAgentId(agentId.toString());
        agentDetailDto.setBranchName((String) agentDetail.get("branchName"));
        agentDetailDto.setTeamName((String) agentDetail.get("teamName"));
        agentDetailDto.setAgentName((String)agentDetail.get("firstName"));
       // agentDetailDto.setAgentName(agentDetail.get("firstName") + " " +(agentDetail.get("lastName") == null ? "" : (String) agentDetail.get("lastName")));
        agentDetailDto.setAgentMobileNumber(agentDetail.get("mobileNumber") != null ? (String) agentDetail.get("mobileNumber") : "");
        //agentDetailDto.setAgentSalutation(agentDetail.get("title") != null ? (String) agentDetail.get("title") : "");
        return agentDetailDto;
    }
}
