package com.pla.grouphealth.quotation.application.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.pla.core.domain.model.agent.AgentId;
import com.pla.grouphealth.quotation.application.command.GLRecalculatedInsuredPremiumCommand;
import com.pla.grouphealth.quotation.application.command.SearchGlQuotationDto;
import com.pla.grouphealth.quotation.domain.model.*;
import com.pla.grouphealth.quotation.presentation.dto.GLQuotationDetailDto;
import com.pla.grouphealth.quotation.presentation.dto.GLQuotationMailDto;
import com.pla.grouphealth.quotation.presentation.dto.PlanDetailDto;
import com.pla.grouphealth.quotation.query.*;
import com.pla.grouphealth.quotation.repository.GHQuotationRepository;
import com.pla.publishedlanguage.contract.IPlanAdapter;
import com.pla.publishedlanguage.dto.PlanCoverageDetailDto;
import com.pla.sharedkernel.identifier.PlanId;
import com.pla.sharedkernel.identifier.QuotationId;
import com.pla.sharedkernel.util.PDFGeneratorUtils;
import net.sf.jasperreports.engine.JRException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.velocity.app.VelocityEngine;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.bson.types.ObjectId;
import org.joda.time.LocalDate;
import org.nthdimenzion.common.AppConstants;
import org.nthdimenzion.presentation.AppUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.velocity.VelocityEngineUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.nthdimenzion.presentation.AppUtils.getIntervalInDays;
import static org.nthdimenzion.utils.UtilValidator.isEmpty;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Created by Samir on 4/14/2015.
 */
@Service
public class GHQuotationService {

    private GHQuotationFinder ghQuotationFinder;

    private IPlanAdapter planAdapter;

    private GHInsuredExcelGenerator ghInsuredExcelGenerator;

    private GHInsuredExcelParser ghInsuredExcelParser;

    private GHQuotationRepository ghQuotationRepository;

    private VelocityEngine velocityEngine;

    private CommandGateway commandGateway;

    @Autowired
    public GHQuotationService(GHQuotationFinder ghQuotationFinder, IPlanAdapter planAdapter, GHInsuredExcelGenerator ghInsuredExcelGenerator, GHInsuredExcelParser ghInsuredExcelParser, GHQuotationRepository ghQuotationRepository, VelocityEngine velocityEngine, CommandGateway commandGateway) {
        this.ghQuotationFinder = ghQuotationFinder;
        this.planAdapter = planAdapter;
        this.ghInsuredExcelGenerator = ghInsuredExcelGenerator;
        this.ghInsuredExcelParser = ghInsuredExcelParser;
        this.ghQuotationRepository = ghQuotationRepository;
        this.velocityEngine = velocityEngine;
        this.commandGateway = commandGateway;
    }

    public byte[] getPlanReadyReckoner(String quotationId) throws IOException, JRException {
        AgentDetailDto agentDetailDto = getAgentDetail(new QuotationId(quotationId));
        List<PlanId> planIds = getAgentAuthorizedPlans(agentDetailDto.getAgentId());
        List<PlanDetailDto> planDetailDtoList = PlanDetailDto.transformToPlanDetail(planAdapter.getPlanAndCoverageDetail(planIds));
        byte[] pdfData = PDFGeneratorUtils.createPDFReportByList(planDetailDtoList, "jasperpdf/template/grouphealth/quotation/planReadyReckoner.jrxml");
        return pdfData;
    }

    public byte[] getQuotationPDF(String quotationId) throws IOException, JRException {
        GLQuotationDetailDto glQuotationDetailDto = getGlQuotationDetailForPDF(quotationId);
        byte[] pdfData = PDFGeneratorUtils.createPDFReportByList(Arrays.asList(glQuotationDetailDto), "jasperpdf/template/grouphealth/quotation/ghQuotation.jrxml");
        return pdfData;
    }

    public GLQuotationMailDto getPreScriptedEmail(String quotationId) {
        GroupHealthQuotation groupHealthQuotation = ghQuotationRepository.findOne(new QuotationId(quotationId));
        String subject = "PLA Insurance - Group Life - Quotation ID : " + groupHealthQuotation.getQuotationNumber();
        String mailAddress = groupHealthQuotation.getProposer().getContactDetail().getContactPersonDetail().getContactPersonEmail();
        mailAddress = isEmpty(mailAddress) ? "" : mailAddress;
        Map<String, Object> emailContent = Maps.newHashMap();
        emailContent.put("mailSentDate", groupHealthQuotation.getGeneratedOn().toString(AppConstants.DD_MM_YYY_FORMAT));
        emailContent.put("contactPersonName", groupHealthQuotation.getProposer().getContactDetail().getContactPersonDetail().getContactPersonName());
        emailContent.put("proposerName", groupHealthQuotation.getProposer().getProposerName());
        Map<String, Object> emailContentMap = Maps.newHashMap();
        emailContentMap.put("emailContent", emailContent);
        String emailBody = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "emailtemplate/grouphealth/quotation/grouphealthQuotationTemplate.vm", emailContentMap);
        GLQuotationMailDto dto = new GLQuotationMailDto(subject, emailBody, new String[]{mailAddress});
        dto.setQuotationId(quotationId);
        dto.setQuotationNumber(groupHealthQuotation.getQuotationNumber());
        return dto;
    }

    private GLQuotationDetailDto getGlQuotationDetailForPDF(String quotationId) {
        GLQuotationDetailDto glQuotationDetailDto = new GLQuotationDetailDto();
        GroupHealthQuotation quotation = ghQuotationRepository.findOne(new QuotationId(quotationId));
        AgentDetailDto agentDetailDto = getAgentDetail(new QuotationId(quotationId));
        glQuotationDetailDto.setAgentBranch(agentDetailDto.getBranchName());
        glQuotationDetailDto.setAgentCode(agentDetailDto.getAgentId());
        glQuotationDetailDto.setAgentName(agentDetailDto.getAgentSalutation() + "  " + agentDetailDto.getAgentName());
        glQuotationDetailDto.setAgentMobileNumber(agentDetailDto.getAgentMobileNumber());

        GHProposer proposer = quotation.getProposer();
        glQuotationDetailDto.setProposerName(proposer.getProposerName());
        GHProposerContactDetail proposerContactDetail = proposer.getContactDetail();
        glQuotationDetailDto.setProposerPhoneNumber(proposerContactDetail.getContactPersonDetail().getWorkPhoneNumber());
        glQuotationDetailDto.setProposerAddress(proposerContactDetail.toString());
        glQuotationDetailDto.setQuotationNumber(quotation.getQuotationNumber());

        GHPremiumDetail premiumDetail = quotation.getPremiumDetail();
        glQuotationDetailDto.setCoveragePeriod(premiumDetail.getPolicyTermValue() != null ? premiumDetail.getPolicyTermValue().toString() + "  days" : "");
        glQuotationDetailDto.setProfitAndSolvencyLoading(premiumDetail.getProfitAndSolvency() != null ? premiumDetail.getProfitAndSolvency().toString() + " %" : "");
        glQuotationDetailDto.setAdditionalDiscountLoading(premiumDetail.getDiscount() != null ? premiumDetail.getDiscount().toString() + " %" : "");
        glQuotationDetailDto.setAddOnBenefits(premiumDetail.getAddOnBenefit() != null ? premiumDetail.getAddOnBenefit().toString() + " %" : "");
        glQuotationDetailDto.setAddOnBenefitsPercentage(premiumDetail.getAddOnBenefit() != null ? premiumDetail.getAddOnBenefit().toString() + " %" : "");
        glQuotationDetailDto.setWaiverOfExcessLoadings("");
        glQuotationDetailDto.setWaiverOfExcessLoadingsPercentage("");
        BigDecimal totalPremiumAmount = quotation.getNetAnnualPremiumPaymentAmount(premiumDetail);
        totalPremiumAmount = totalPremiumAmount.setScale(2, BigDecimal.ROUND_CEILING);
        BigDecimal netPremiumOfInsured = quotation.getTotalBasicPremiumForInsured();
        netPremiumOfInsured = netPremiumOfInsured.setScale(2, BigDecimal.ROUND_CEILING);
        glQuotationDetailDto.setNetPremium(netPremiumOfInsured.toPlainString());
        glQuotationDetailDto.setTotalPremium(totalPremiumAmount.toPlainString());
        glQuotationDetailDto.setTotalLivesCovered(quotation.getTotalNoOfLifeCovered().toString());
        BigDecimal totalSumAssured = quotation.getTotalSumAssured();
        totalSumAssured = totalSumAssured.setScale(2, BigDecimal.ROUND_CEILING);
        glQuotationDetailDto.setTotalSumAssured(totalSumAssured.toPlainString());
        List<GLQuotationDetailDto.CoverDetail> coverDetails = Lists.newArrayList();
        List<GLQuotationDetailDto.Annexure> annexures = Lists.newArrayList();
        if (isNotEmpty(quotation.getInsureds())) {
            for (GHInsured insured : quotation.getInsureds()) {
                GHPlanPremiumDetail insuredPremiumDetail = insured.getPlanPremiumDetail();
                List<PlanCoverageDetailDto> planCoverageDetailDtoList = planAdapter.getPlanAndCoverageDetail(Arrays.asList(insuredPremiumDetail.getPlanId()));
                BigDecimal insuredPlanSA = insuredPremiumDetail.getSumAssured();
                insuredPlanSA = insuredPlanSA.setScale(2, BigDecimal.ROUND_CEILING);
                GLQuotationDetailDto.CoverDetail insuredPlanCoverDetail = glQuotationDetailDto.new CoverDetail(isEmpty(insured.getCategory()) ? "" : insured.getCategory(), "Self", planCoverageDetailDtoList.get(0).getPlanName(), insuredPlanSA.toPlainString());
                coverDetails.add(insuredPlanCoverDetail);
                if (isNotEmpty(insured.getCoveragePremiumDetails())) {
                    for (CoveragePremiumDetail coveragePremiumDetail : insured.getCoveragePremiumDetails()) {
                        Map<String, Object> coverageMap = ghQuotationFinder.getCoverageDetail(coveragePremiumDetail.getCoverageId().getCoverageId());
                        BigDecimal insuredCoverageSA = coveragePremiumDetail.getSumAssured();
                        insuredCoverageSA = insuredCoverageSA.setScale(2, BigDecimal.ROUND_CEILING);
                        GLQuotationDetailDto.CoverDetail insuredCoverageCoverDetail = glQuotationDetailDto.new CoverDetail(isEmpty(insured.getCategory()) ? "" : insured.getCategory(), "Self",
                                (String) coverageMap.get("coverageName"), insuredCoverageSA.toPlainString());
                        coverDetails.add(insuredCoverageCoverDetail);
                    }
                }
                BigDecimal insuredAnnualIncome = insured.getAnnualIncome();
                String insuredAnnualIncomeInString = "";
                if (insuredAnnualIncome != null) {
                    insuredAnnualIncome = insuredAnnualIncome.setScale(2, BigDecimal.ROUND_CEILING);
                    insuredAnnualIncomeInString = insuredAnnualIncome.toPlainString();
                }
                BigDecimal insuredBasicPremium = insuredPremiumDetail.getPremiumAmount();
                insuredBasicPremium = insuredBasicPremium.setScale(2, BigDecimal.ROUND_CEILING);
                GLQuotationDetailDto.Annexure insuredAnnexure = glQuotationDetailDto.new Annexure(insured.getFirstName() + " " + insured.getLastName()
                        , insured.getNrcNumber(), insured.getGender().name(), AppUtils.toString(insured.getDateOfBirth()),
                        isEmpty(insured.getCategory()) ? "" : insured.getCategory(), "Self", AppUtils.getAge(insured.getDateOfBirth()).toString(), insuredAnnualIncomeInString, insuredBasicPremium.toPlainString());
                annexures.add(insuredAnnexure);
                if (isNotEmpty(insured.getInsuredDependents())) {
                    for (GHInsuredDependent insuredDependent : insured.getInsuredDependents()) {
                        GHPlanPremiumDetail insuredDependentPremiumDetail = insuredDependent.getPlanPremiumDetail();
                        List<PlanCoverageDetailDto> dependentPlanCoverageDetailDtoList = planAdapter.getPlanAndCoverageDetail(Arrays.asList(insuredDependentPremiumDetail.getPlanId()));
                        BigDecimal insuredDependentPlanSA = insuredDependentPremiumDetail.getSumAssured();
                        insuredDependentPlanSA = insuredDependentPlanSA.setScale(2, BigDecimal.ROUND_CEILING);
                        GLQuotationDetailDto.CoverDetail insuredDependentPlanCoverDetail = glQuotationDetailDto.new CoverDetail(isEmpty(insuredDependent.getCategory()) ? "" : insuredDependent.getCategory(), insuredDependent.getRelationship().description, dependentPlanCoverageDetailDtoList.get(0).getPlanName(), insuredDependentPlanSA.toPlainString());
                        coverDetails.add(insuredDependentPlanCoverDetail);
                        if (isNotEmpty(insuredDependent.getCoveragePremiumDetails())) {
                            for (CoveragePremiumDetail dependentCoveragePremiumDetail : insuredDependent.getCoveragePremiumDetails()) {
                                BigDecimal insuredDependentCoverageSA = dependentCoveragePremiumDetail.getSumAssured();
                                insuredDependentCoverageSA = insuredDependentCoverageSA.setScale(2, BigDecimal.ROUND_CEILING);
                                Map<String, Object> coverageMap = ghQuotationFinder.getCoverageDetail(dependentCoveragePremiumDetail.getCoverageId().getCoverageId());
                                GLQuotationDetailDto.CoverDetail insuredDependentCoverageCoverDetail = glQuotationDetailDto.new CoverDetail(isEmpty(insuredDependent.getCategory()) ? "" : insuredDependent.getCategory(), insuredDependent.getRelationship().name(), (String) coverageMap.get("coverageName"), insuredDependentCoverageSA.toPlainString());
                                coverDetails.add(insuredDependentCoverageCoverDetail);
                            }
                        }
                        BigDecimal insuredDependentBasicPremium = insuredDependentPremiumDetail.getPremiumAmount();
                        insuredDependentBasicPremium = insuredDependentBasicPremium.setScale(2, BigDecimal.ROUND_CEILING);
                        GLQuotationDetailDto.Annexure annexure = glQuotationDetailDto.new Annexure(insuredDependent.getFirstName() + " " + insuredDependent.getLastName()
                                , insuredDependent.getNrcNumber(), insuredDependent.getGender().name(), AppUtils.toString(insuredDependent.getDateOfBirth()),
                                isEmpty(insuredDependent.getCategory()) ? "" : insuredDependent.getCategory(), insuredDependent.getRelationship().description, AppUtils.getAge(insuredDependent.getDateOfBirth()).toString(), "", insuredDependentBasicPremium.toPlainString());
                        annexures.add(annexure);
                    }
                }
            }
        }
        glQuotationDetailDto.setCoverDetails(coverDetails);
        glQuotationDetailDto.setAnnexure(annexures);
        return glQuotationDetailDto;
    }

    public boolean isValidInsuredTemplate(String quotationId, HSSFWorkbook insuredTemplateWorkbook, boolean samePlanForAllCategory, boolean samePlanForAllRelationship) {
        AgentDetailDto agentDetailDto = getAgentDetail(new QuotationId(quotationId));
        List<PlanId> agentPlans = getAgentAuthorizedPlans(agentDetailDto.getAgentId());
        return ghInsuredExcelParser.isValidInsuredExcel(insuredTemplateWorkbook, samePlanForAllCategory, samePlanForAllRelationship, agentPlans);
    }

    public List<InsuredDto> transformToInsuredDto(HSSFWorkbook workbook, String quotationId, boolean samePlanForAllCategory, boolean samePlanForAllRelations) {
        AgentDetailDto agentDetailDto = getAgentDetail(new QuotationId(quotationId));
        List<PlanId> agentAuthorizedPlans = getAgentAuthorizedPlans(agentDetailDto.getAgentId());
        List<InsuredDto> insuredDtoList = ghInsuredExcelParser.transformToInsuredDto(workbook, agentAuthorizedPlans);
        return insuredDtoList;
    }

    private List<PlanId> getAgentAuthorizedPlans(String agentId) {
        List<Map<String, Object>> authorizedPlans = ghQuotationFinder.getAgentAuthorizedPlan(agentId);
        List<PlanId> planIds = authorizedPlans.stream().map(new Function<Map<String, Object>, PlanId>() {
            @Override
            public PlanId apply(Map<String, Object> authorizePlanMap) {
                String planId = (String) authorizePlanMap.get("planId");
                return new PlanId(planId);
            }
        }).collect(Collectors.toList());
        return planIds;
    }

    public HSSFWorkbook getInsuredTemplateExcel(String quotationId) throws IOException {
        AgentDetailDto agentDetailDto = getAgentDetail(new QuotationId(quotationId));
        List<PlanId> planIds = getAgentAuthorizedPlans(agentDetailDto.getAgentId());
        Map quotation = ghQuotationFinder.getQuotationById(quotationId);
        List<GHInsured> insureds = (List<GHInsured>) quotation.get("insureds");
        List<InsuredDto> insuredDtoList = isNotEmpty(insureds) ? insureds.stream().map(new Function<GHInsured, InsuredDto>() {
            @Override
            public InsuredDto apply(GHInsured insured) {
                InsuredDto insuredDto = new InsuredDto();
                insuredDto.setCompanyName(insured.getCompanyName());
                insuredDto.setManNumber(insured.getManNumber());
                insuredDto.setNrcNumber(insured.getNrcNumber());
                insuredDto.setSalutation(insured.getSalutation());
                insuredDto.setFirstName(insured.getFirstName());
                insuredDto.setLastName(insured.getLastName());
                insuredDto.setDateOfBirth(insured.getDateOfBirth());
                insuredDto.setGender(insured.getGender());
                insuredDto.setCategory(insured.getCategory());
                insuredDto.setAnnualIncome(insured.getAnnualIncome());
                insuredDto.setOccupationClass(insured.getOccupationClass());
                insuredDto.setOccupationCategory(insured.getOccupationCategory());
                insuredDto.setNoOfAssured(insured.getNoOfAssured());
                GHPlanPremiumDetail planPremiumDetail = insured.getPlanPremiumDetail();
                InsuredDto.PlanPremiumDetailDto planPremiumDetailDto = new InsuredDto.PlanPremiumDetailDto(planPremiumDetail.getPlanId().getPlanId(), planPremiumDetail.getPlanCode(), planPremiumDetail.getPremiumAmount(), planPremiumDetail.getSumAssured());
                insuredDto = insuredDto.addPlanPremiumDetail(planPremiumDetailDto);
                List<InsuredDto.CoveragePremiumDetailDto> coveragePremiumDetailDtoList = isNotEmpty(insured.getCoveragePremiumDetails()) ? insured.getCoveragePremiumDetails().stream().map(new Function<CoveragePremiumDetail, InsuredDto.CoveragePremiumDetailDto>() {
                    @Override
                    public InsuredDto.CoveragePremiumDetailDto apply(CoveragePremiumDetail coveragePremiumDetail) {
                        InsuredDto.CoveragePremiumDetailDto coveragePremiumDetailDto = new InsuredDto.CoveragePremiumDetailDto(coveragePremiumDetail.getCoverageCode(),
                                coveragePremiumDetail.getCoverageId().getCoverageId(), coveragePremiumDetail.getPremium(), coveragePremiumDetail.getSumAssured());
                        return coveragePremiumDetailDto;
                    }
                }).collect(Collectors.toList()) : Lists.newArrayList();
                insuredDto = insuredDto.addCoveragePremiumDetails(coveragePremiumDetailDtoList);
                Set<InsuredDto.InsuredDependentDto> insuredDependentDtoList = isNotEmpty(insured.getInsuredDependents()) ? insured.getInsuredDependents().stream().map(new Function<GHInsuredDependent, InsuredDto.InsuredDependentDto>() {
                    @Override
                    public InsuredDto.InsuredDependentDto apply(GHInsuredDependent insuredDependent) {
                        InsuredDto.InsuredDependentDto insuredDependentDto = new InsuredDto.InsuredDependentDto();
                        insuredDependentDto.setCompanyName(insuredDependent.getCompanyName());
                        insuredDependentDto.setManNumber(insuredDependent.getManNumber());
                        insuredDependentDto.setNrcNumber(insuredDependent.getNrcNumber());
                        insuredDependentDto.setSalutation(insuredDependent.getSalutation());
                        insuredDependentDto.setFirstName(insuredDependent.getFirstName());
                        insuredDependentDto.setLastName(insuredDependent.getLastName());
                        insuredDependentDto.setDateOfBirth(insuredDependent.getDateOfBirth());
                        insuredDependentDto.setRelationship(insuredDependent.getRelationship());
                        insuredDependentDto.setGender(insuredDependent.getGender());
                        insuredDependentDto.setCategory(insuredDependent.getCategory());
                        insuredDependentDto.setOccupationClass(insuredDependent.getOccupationClass());
                        insuredDependentDto.setOccupationCategory(insuredDependent.getOccupationCategory());
                        GHPlanPremiumDetail planPremiumDetail = insuredDependent.getPlanPremiumDetail();
                        InsuredDto.PlanPremiumDetailDto planPremiumDetailDto = new InsuredDto.PlanPremiumDetailDto(planPremiumDetail.getPlanId().getPlanId(), planPremiumDetail.getPlanCode(), planPremiumDetail.getPremiumAmount(), planPremiumDetail.getSumAssured());
                        insuredDependentDto = insuredDependentDto.addPlanPremiumDetail(planPremiumDetailDto);
                        List<InsuredDto.CoveragePremiumDetailDto> dependentCoveragePremiumDetailDtoList = isNotEmpty(insuredDependent.getCoveragePremiumDetails()) ? insuredDependent.getCoveragePremiumDetails().stream().map(new Function<CoveragePremiumDetail, InsuredDto.CoveragePremiumDetailDto>() {
                            @Override
                            public InsuredDto.CoveragePremiumDetailDto apply(CoveragePremiumDetail coveragePremiumDetail) {
                                InsuredDto.CoveragePremiumDetailDto coveragePremiumDetailDto = new InsuredDto.CoveragePremiumDetailDto(coveragePremiumDetail.getCoverageCode(),
                                        coveragePremiumDetail.getCoverageId().getCoverageId(), coveragePremiumDetail.getPremium(), coveragePremiumDetail.getSumAssured());
                                return coveragePremiumDetailDto;
                            }
                        }).collect(Collectors.toList()) : Lists.newArrayList();
                        insuredDependentDto = insuredDependentDto.addCoveragePremiumDetails(dependentCoveragePremiumDetailDtoList);
                        return insuredDependentDto;
                    }
                }).collect(Collectors.toSet()) : Sets.newHashSet();
                insuredDto = insuredDto.addInsuredDependent(insuredDependentDtoList);
                return insuredDto;
            }
        }).collect(Collectors.toList()) : Lists.newArrayList();
        HSSFWorkbook hssfWorkbook = ghInsuredExcelGenerator.generateInsuredExcel(insuredDtoList, planIds);
        return hssfWorkbook;
    }

    public boolean isInsuredDataUpdated(String quotationId) {
        Map quotation = ghQuotationFinder.getQuotationById(quotationId);
        List<GHInsured> insureds = (List<GHInsured>) quotation.get("insureds");
        return isNotEmpty(insureds);
    }

    public PremiumDetailDto getPremiumDetail(QuotationId quotationId) {
        GroupHealthQuotation groupHealthQuotation = ghQuotationRepository.findOne(quotationId);
        PremiumDetailDto premiumDetailDto = getPremiumDetail(groupHealthQuotation);
        return premiumDetailDto;
    }

    public PremiumDetailDto recalculatePremium(GLRecalculatedInsuredPremiumCommand glRecalculatedInsuredPremiumCommand) {
        GroupHealthQuotation groupHealthQuotation = commandGateway.sendAndWait(glRecalculatedInsuredPremiumCommand);
        PremiumDetailDto premiumDetailDto = getPremiumDetail(groupHealthQuotation);
        return premiumDetailDto;
    }

    private PremiumDetailDto getPremiumDetail(GroupHealthQuotation groupHealthQuotation) {
        GHPremiumDetail premiumDetail = groupHealthQuotation.getPremiumDetail();
        if (premiumDetail == null) {
            return new PremiumDetailDto();
        }
        PremiumDetailDto premiumDetailDto = new PremiumDetailDto(premiumDetail.getAddOnBenefit(), premiumDetail.getProfitAndSolvency(), premiumDetail.getDiscount(), premiumDetail.getPolicyTermValue());
        GHPremiumDetail.PremiumInstallment premiumInstallment = premiumDetail.getPremiumInstallment();
        if (premiumInstallment != null) {
            premiumDetailDto = premiumDetailDto.addOptedInstallmentDetail(premiumInstallment.getNoOfInstallment(), premiumInstallment.getInstallmentAmount());
        }
        if (isNotEmpty(premiumDetail.getInstallments())) {
            for (GHPremiumDetail.PremiumInstallment installment : premiumDetail.getInstallments()) {
                premiumDetailDto = premiumDetailDto.addInstallments(installment.getNoOfInstallment(), installment.getInstallmentAmount());
            }
        }
        premiumDetailDto = premiumDetailDto.addFrequencyPremiumAmount(premiumDetail.getAnnualPremiumAmount(), premiumDetail.getSemiAnnualPremiumAmount(), premiumDetail.getQuarterlyPremiumAmount(), premiumDetail.getMonthlyPremiumAmount());
        premiumDetailDto = premiumDetailDto.addNetTotalPremiumAmount(premiumDetail.getNetTotalPremium());
        return premiumDetailDto;
    }

    public AgentDetailDto getAgentDetail(QuotationId quotationId) {
        Map quotation = ghQuotationFinder.getQuotationById(quotationId.getQuotationId());
        AgentId agentMap = (AgentId) quotation.get("agentId");
        String agentId = agentMap.getAgentId();
        Map<String, Object> agentDetail = ghQuotationFinder.getAgentById(agentId);
        AgentDetailDto agentDetailDto = new AgentDetailDto();
        agentDetailDto.setAgentId(agentId);
        agentDetailDto.setBranchName((String) agentDetail.get("branchName"));
        agentDetailDto.setTeamName((String) agentDetail.get("teamName"));
        agentDetailDto.setAgentName(agentDetail.get("firstName") + " " + (agentDetail.get("lastName") == null ? "" : (String) agentDetail.get("lastName")));
        agentDetailDto.setAgentMobileNumber(agentDetail.get("mobileNumber") != null ? (String) agentDetail.get("mobileNumber") : "");
        agentDetailDto.setAgentSalutation(agentDetail.get("title") != null ? (String) agentDetail.get("title") : "");
        return agentDetailDto;
    }

    public ProposerDto getProposerDetail(QuotationId quotationId) {
        Map quotation = ghQuotationFinder.getQuotationById(quotationId.getQuotationId());
        GHProposer proposer = (GHProposer) quotation.get("proposer");
        return new ProposerDto(proposer);
    }

    public List<GlQuotationDto> searchQuotation(SearchGlQuotationDto searchGlQuotationDto) {
        List<Map> allQuotations = ghQuotationFinder.searchQuotation(searchGlQuotationDto.getQuotationNumber(), searchGlQuotationDto.getAgentCode(), searchGlQuotationDto.getProposerName(), searchGlQuotationDto.getAgentName(), searchGlQuotationDto.getQuotationId());
        if (isEmpty(allQuotations)) {
            return Lists.newArrayList();
        }
        List<GlQuotationDto> glQuotationDtoList = allQuotations.stream().map(new TransformToGLQuotationDto()).collect(Collectors.toList());
        return glQuotationDtoList;
    }

    private class TransformToGLQuotationDto implements Function<Map, GlQuotationDto> {

        @Override
        public GlQuotationDto apply(Map map) {
            String quotationId = map.get("_id").toString();
            AgentDetailDto agentDetailDto = getAgentDetail(new QuotationId(quotationId));
            LocalDate generatedOn = map.get("generatedOn") != null ? new LocalDate((Date) map.get("generatedOn")) : null;
            String quotationStatus = map.get("quotationStatus") != null ? (String) map.get("quotationStatus") : "";
            String quotationNumber = map.get("quotationNumber") != null ? (String) map.get("quotationNumber") : "";
            ObjectId parentQuotationIdMap = map.get("parentQuotationId") != null ? (ObjectId) map.get("parentQuotationId") : null;
            GHProposer proposerMap = map.get("proposer") != null ? (GHProposer) map.get("proposer") : null;
            String proposerName = proposerMap != null ? proposerMap.getProposerName() : "";
            String parentQuotationId = parentQuotationIdMap != null ? parentQuotationIdMap.toString() : "";
            GlQuotationDto glQuotationDto = new GlQuotationDto(new QuotationId(quotationId), (Integer) map.get("versionNumber"), generatedOn, agentDetailDto.getAgentId(), agentDetailDto.getAgentName(), new QuotationId(parentQuotationId), quotationStatus, quotationNumber, proposerName, getIntervalInDays(generatedOn));
            return glQuotationDto;
        }
    }
}
