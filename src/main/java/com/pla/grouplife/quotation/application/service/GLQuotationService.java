package com.pla.grouplife.quotation.application.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.pla.core.domain.model.agent.AgentId;
import com.pla.grouplife.quotation.application.command.GLRecalculatedInsuredPremiumCommand;
import com.pla.grouplife.quotation.application.command.SearchGlQuotationDto;
import com.pla.grouplife.quotation.domain.model.GroupLifeQuotation;
import com.pla.grouplife.quotation.presentation.dto.GLQuotationDetailDto;
import com.pla.grouplife.quotation.presentation.dto.GLQuotationMailDto;
import com.pla.grouplife.quotation.presentation.dto.PlanDetailDto;
import com.pla.grouplife.quotation.query.GLQuotationFinder;
import com.pla.grouplife.quotation.repository.GlQuotationRepository;
import com.pla.grouplife.sharedresource.dto.*;
import com.pla.grouplife.sharedresource.dto.InsuredDto;
import com.pla.grouplife.sharedresource.dto.PremiumDetailDto;
import com.pla.grouplife.sharedresource.dto.ProposerDto;
import com.pla.grouplife.sharedresource.model.vo.*;
import com.pla.grouplife.sharedresource.service.*;
import com.pla.grouplife.sharedresource.service.GLInsuredExcelParser;
import com.pla.publishedlanguage.contract.IPlanAdapter;
import com.pla.publishedlanguage.dto.PlanCoverageDetailDto;
import com.pla.sharedkernel.identifier.LineOfBusinessEnum;
import com.pla.sharedkernel.identifier.OpportunityId;
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
public class GLQuotationService {

    private GLQuotationFinder glQuotationFinder;

    private IPlanAdapter planAdapter;

    private GLInsuredExcelGenerator glInsuredExcelGenerator;

    private GLInsuredExcelParser glInsuredExcelParser;

    private GlQuotationRepository glQuotationRepository;

    private VelocityEngine velocityEngine;

    private CommandGateway commandGateway;

    @Autowired
    public GLQuotationService(GLQuotationFinder glQuotationFinder, IPlanAdapter planAdapter, GLInsuredExcelGenerator glInsuredExcelGenerator, GLInsuredExcelParser glInsuredExcelParser, GlQuotationRepository glQuotationRepository, VelocityEngine velocityEngine, CommandGateway commandGateway) {
        this.glQuotationFinder = glQuotationFinder;
        this.planAdapter = planAdapter;
        this.glInsuredExcelGenerator = glInsuredExcelGenerator;
        this.glInsuredExcelParser = glInsuredExcelParser;
        this.glQuotationRepository = glQuotationRepository;
        this.velocityEngine = velocityEngine;
        this.commandGateway = commandGateway;
    }

    public byte[] getPlanReadyReckoner(String quotationId) throws IOException, JRException {
        AgentDetailDto agentDetailDto = getAgentDetail(new QuotationId(quotationId));
        List<PlanId> planIds = getActivePlanByAgentId(agentDetailDto.getAgentId());
        List<PlanDetailDto> planDetailDtoList = PlanDetailDto.transformToPlanDetail(planAdapter.getPlanAndCoverageDetail(planIds));
        byte[] pdfData = PDFGeneratorUtils.createPDFReportByList(planDetailDtoList, "jasperpdf/template/grouplife/planReadyReckoner.jrxml");
        return pdfData;
    }

    public byte[] getQuotationPDF(String quotationId, boolean withOutSplit) throws IOException, JRException {
        GLQuotationDetailDto glQuotationDetailDto = getGlQuotationDetailForPDF(quotationId, withOutSplit);
        byte[] pdfData = PDFGeneratorUtils.createPDFReportByList(Arrays.asList(glQuotationDetailDto), "jasperpdf/template/grouplife/glQuotation.jrxml");
        return pdfData;
    }

    public GLQuotationMailDto getPreScriptedEmail(String quotationId) {
        GroupLifeQuotation groupLifeQuotation = glQuotationRepository.findOne(new QuotationId(quotationId));
        String subject = "PLA Insurance - Group Life - Quotation ID : " + groupLifeQuotation.getQuotationNumber();
        String mailAddress = groupLifeQuotation.getProposer().getContactDetail().getContactPersonDetail().getContactPersonEmail();
        mailAddress = isEmpty(mailAddress) ? "" : mailAddress;
        Map<String, Object> emailContent = Maps.newHashMap();
        emailContent.put("mailSentDate", groupLifeQuotation.getGeneratedOn().toString(AppConstants.DD_MM_YYY_FORMAT));
        emailContent.put("contactPersonName", groupLifeQuotation.getProposer().getContactDetail().getContactPersonDetail().getContactPersonName());
        emailContent.put("proposerName", groupLifeQuotation.getProposer().getProposerName());
        Map<String, Object> emailContentMap = Maps.newHashMap();
        emailContentMap.put("emailContent", emailContent);
        String emailBody = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "emailtemplate/grouplife/quotation/grouplifeQuotationTemplate.vm", emailContentMap);
        GLQuotationMailDto dto = new GLQuotationMailDto(subject, emailBody, new String[]{mailAddress});
        dto.setQuotationId(quotationId);
        dto.setQuotationNumber(groupLifeQuotation.getQuotationNumber());
        return dto;
    }

    //TODO Need to change the JASPER Field Key as per the object property and then use BeanUtils to copy object properties
    private GLQuotationDetailDto getGlQuotationDetailForPDF(String quotationId, boolean withOutSplit) {
        GLQuotationDetailDto glQuotationDetailDto = new GLQuotationDetailDto();
        glQuotationDetailDto.setShowLoading(!withOutSplit);
        GroupLifeQuotation quotation = glQuotationRepository.findOne(new QuotationId(quotationId));
        AgentDetailDto agentDetailDto = getActiveInactiveAgentDetail(new QuotationId(quotationId));
        glQuotationDetailDto.setAgentBranch(isNotEmpty(agentDetailDto.getBranchName()) ? agentDetailDto.getBranchName() : "");
        glQuotationDetailDto.setAgentCode(agentDetailDto.getAgentId());
        glQuotationDetailDto.setAgentName(agentDetailDto.getAgentSalutation() + "  " + agentDetailDto.getAgentName());
        glQuotationDetailDto.setAgentMobileNumber(agentDetailDto.getAgentMobileNumber());

        Proposer proposer = quotation.getProposer();
        glQuotationDetailDto.setProposerName(proposer.getProposerName());
        ProposerContactDetail proposerContactDetail = proposer.getContactDetail();
        glQuotationDetailDto.setProposerPhoneNumber(proposerContactDetail.getContactPersonDetail().getWorkPhoneNumber());
        Map<String, Object> provinceGeoMap = glQuotationFinder.findGeoDetail(proposerContactDetail.getProvince());
        Map<String, Object> townGeoMap = glQuotationFinder.findGeoDetail(proposerContactDetail.getTown());
        glQuotationDetailDto.setProposerAddress(proposerContactDetail.getAddress((String) townGeoMap.get("geoName"), (String) provinceGeoMap.get("geoName")));
        glQuotationDetailDto.setQuotationNumber(quotation.getQuotationNumber() + "/" + quotation.getVersionNumber());

        PremiumDetail premiumDetail = quotation.getPremiumDetail();
        glQuotationDetailDto.setCoveragePeriod(premiumDetail.getPolicyTermValue() != null ? premiumDetail.getPolicyTermValue().toString() + "  days" : "");
        glQuotationDetailDto.setProfitAndSolvencyLoading((premiumDetail.getProfitAndSolvency() != null && !withOutSplit) ? premiumDetail.getProfitAndSolvency().toString() + " %" : "");
        glQuotationDetailDto.setHivDiscount(premiumDetail.getHivDiscount() != null ? premiumDetail.getHivDiscount().toString() + " %" : "");
        glQuotationDetailDto.setValuedClientDiscount(premiumDetail.getValuedClientDiscount() != null ? premiumDetail.getValuedClientDiscount().toString() + " %" : "");
        glQuotationDetailDto.setLongTermDiscount(premiumDetail.getLongTermDiscount() != null ? premiumDetail.getLongTermDiscount().toString() + " %" : "");
        glQuotationDetailDto.setAddOnBenefits((premiumDetail.getAddOnBenefit() != null && !withOutSplit) ? premiumDetail.getAddOnBenefit().toString() + " %" : "");
        glQuotationDetailDto.setAddOnBenefitsPercentage((premiumDetail.getAddOnBenefit() != null && !withOutSplit) ? premiumDetail.getAddOnBenefit().toString() + " %" : "");
        glQuotationDetailDto.setWaiverOfExcessLoadings("");
        glQuotationDetailDto.setWaiverOfExcessLoadingsPercentage("");
        BigDecimal totalPremiumAmount = quotation.getNetAnnualPremiumPaymentAmount(premiumDetail);
        totalPremiumAmount = totalPremiumAmount.setScale(2, BigDecimal.ROUND_CEILING);
        BigDecimal netPremiumOfInsured = withOutSplit ? quotation.getNetAnnualPremiumPaymentAmountWithoutDiscount(premiumDetail) : quotation.getTotalBasicPremiumForInsured();
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
            for (Insured insured : quotation.getInsureds()) {
                PlanPremiumDetail insuredPremiumDetail = insured.getPlanPremiumDetail();
                List<PlanCoverageDetailDto> planCoverageDetailDtoList = planAdapter.getPlanAndCoverageDetail(Arrays.asList(insuredPremiumDetail.getPlanId()));
                BigDecimal insuredPlanSA = insuredPremiumDetail.getSumAssured();
                insuredPlanSA = insuredPlanSA.setScale(2, BigDecimal.ROUND_CEILING);
                GLQuotationDetailDto.CoverDetail insuredPlanCoverDetail = glQuotationDetailDto.new CoverDetail(isEmpty(insured.getCategory()) ? "" : insured.getCategory(), "Self", planCoverageDetailDtoList.get(0).getPlanName(), insuredPlanSA);
                coverDetails.add(insuredPlanCoverDetail);
                if (isNotEmpty(insured.getCoveragePremiumDetails())) {
                    for (CoveragePremiumDetail coveragePremiumDetail : insured.getCoveragePremiumDetails()) {
                        Map<String, Object> coverageMap = glQuotationFinder.getCoverageDetail(coveragePremiumDetail.getCoverageId().getCoverageId());
                        BigDecimal insuredCoverageSA = coveragePremiumDetail.getSumAssured();
                        insuredCoverageSA = insuredCoverageSA.setScale(2, BigDecimal.ROUND_CEILING);
                        GLQuotationDetailDto.CoverDetail insuredCoverageCoverDetail = glQuotationDetailDto.new CoverDetail(isEmpty(insured.getCategory()) ? "" : insured.getCategory(), "Self",
                                (String) coverageMap.get("coverageName"), insuredCoverageSA);
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
                        , insured.getNrcNumber(), insured.getGender() != null ? insured.getGender().name() : "", AppUtils.toString(insured.getDateOfBirth()),
                        isEmpty(insured.getCategory()) ? "" : insured.getCategory(), "Self", insured.getDateOfBirth() == null ? "" : AppUtils.getAgeOnNextBirthDate(insured.getDateOfBirth()).toString(), insuredAnnualIncomeInString, insuredBasicPremium.toPlainString());
                annexures.add(insuredAnnexure);
                if (isNotEmpty(insured.getInsuredDependents())) {
                    for (InsuredDependent insuredDependent : insured.getInsuredDependents()) {
                        PlanPremiumDetail insuredDependentPremiumDetail = insuredDependent.getPlanPremiumDetail();
                        List<PlanCoverageDetailDto> dependentPlanCoverageDetailDtoList = planAdapter.getPlanAndCoverageDetail(Arrays.asList(insuredDependentPremiumDetail.getPlanId()));
                        BigDecimal insuredDependentPlanSA = insuredDependentPremiumDetail.getSumAssured();
                        insuredDependentPlanSA = insuredDependentPlanSA.setScale(2, BigDecimal.ROUND_CEILING);
                        GLQuotationDetailDto.CoverDetail insuredDependentPlanCoverDetail = glQuotationDetailDto.new CoverDetail(isEmpty(insuredDependent.getCategory()) ? "" : insuredDependent.getCategory(), insuredDependent.getRelationship().description, dependentPlanCoverageDetailDtoList.get(0).getPlanName(), insuredDependentPlanSA);
                        coverDetails.add(insuredDependentPlanCoverDetail);
                        if (isNotEmpty(insuredDependent.getCoveragePremiumDetails())) {
                            for (CoveragePremiumDetail dependentCoveragePremiumDetail : insuredDependent.getCoveragePremiumDetails()) {
                                BigDecimal insuredDependentCoverageSA = dependentCoveragePremiumDetail.getSumAssured();
                                insuredDependentCoverageSA = insuredDependentCoverageSA.setScale(2, BigDecimal.ROUND_CEILING);
                                Map<String, Object> coverageMap = glQuotationFinder.getCoverageDetail(dependentCoveragePremiumDetail.getCoverageId().getCoverageId());
                                GLQuotationDetailDto.CoverDetail insuredDependentCoverageCoverDetail = glQuotationDetailDto.new CoverDetail(isEmpty(insuredDependent.getCategory()) ? "" : insuredDependent.getCategory(), insuredDependent.getRelationship().name(), (String) coverageMap.get("coverageName"), insuredDependentCoverageSA);
                                coverDetails.add(insuredDependentCoverageCoverDetail);
                            }
                        }
                        BigDecimal insuredDependentBasicPremium = insuredDependentPremiumDetail.getPremiumAmount();
                        insuredDependentBasicPremium = insuredDependentBasicPremium.setScale(2, BigDecimal.ROUND_CEILING);
                        GLQuotationDetailDto.Annexure annexure = glQuotationDetailDto.new Annexure(insuredDependent.getFirstName() + " " + insuredDependent.getLastName()
                                , insuredDependent.getNrcNumber(), insuredDependent.getGender() != null ? insuredDependent.getGender().name() : "",
                                insuredDependent.getDateOfBirth() == null ? "" : AppUtils.toString(insuredDependent.getDateOfBirth()),
                                isEmpty(insuredDependent.getCategory()) ? "" : insuredDependent.getCategory(), insuredDependent.getRelationship().description, AppUtils.getAgeOnNextBirthDate(insuredDependent.getDateOfBirth()).toString(), "", insuredDependentBasicPremium.toPlainString());
                        annexures.add(annexure);
                    }
                }
            }
        }
        List<GLQuotationDetailDto.CoverDetail> unifiedCoverDetails = unifyCoverDetails(coverDetails, glQuotationDetailDto);
        glQuotationDetailDto.setCoverDetails(unifiedCoverDetails);
        glQuotationDetailDto.setAnnexure(annexures);
        return glQuotationDetailDto;
    }

    private List<GLQuotationDetailDto.CoverDetail> unifyCoverDetails(List<GLQuotationDetailDto.CoverDetail> coverDetails, GLQuotationDetailDto glQuotationDetailDto) {
        Map<GLQuotationDetailDto.CoverDetail, List<GLQuotationDetailDto.CoverDetail>> coverDetailsMap = Maps.newLinkedHashMap();
        for (GLQuotationDetailDto.CoverDetail coverDetail : coverDetails) {
            if (coverDetailsMap.get(coverDetail) == null) {
                coverDetailsMap.put(coverDetail, new ArrayList<>());
            } else {
                List<GLQuotationDetailDto.CoverDetail> coverDetailList = coverDetailsMap.get(coverDetail);
                coverDetailList.add(coverDetail);
                coverDetailsMap.put(coverDetail, coverDetailList);
            }
        }
        List<GLQuotationDetailDto.CoverDetail> unifiedCoverDetails = coverDetailsMap.entrySet().stream().map(new Function<Map.Entry<GLQuotationDetailDto.CoverDetail, List<GLQuotationDetailDto.CoverDetail>>, GLQuotationDetailDto.CoverDetail>() {
            @Override
            public GLQuotationDetailDto.CoverDetail apply(Map.Entry<GLQuotationDetailDto.CoverDetail, List<GLQuotationDetailDto.CoverDetail>> coverDetailListEntry) {
                GLQuotationDetailDto.CoverDetail coverDetailKey = coverDetailListEntry.getKey();
                GLQuotationDetailDto.CoverDetail coverDetail = glQuotationDetailDto.new CoverDetail(coverDetailKey.getCategory(), coverDetailKey.getRelationship(), coverDetailKey.getPlanCoverageName());
                BigDecimal totalSA = BigDecimal.ZERO;
                for (GLQuotationDetailDto.CoverDetail valueCoverDetail : coverDetailListEntry.getValue()) {
                    totalSA = totalSA.add(valueCoverDetail.getSumAssured());
                }
                totalSA = totalSA.add(coverDetailKey.getSumAssured());
                coverDetail = coverDetail.addSumAssured(totalSA.toPlainString());
                return coverDetail;
            }
        }).collect(Collectors.toList());
        return unifiedCoverDetails;
    }

    public boolean isValidInsuredTemplate(String quotationId, HSSFWorkbook insuredTemplateWorkbook, boolean samePlanForAllCategory, boolean samePlanForAllRelationship) {
        AgentDetailDto agentDetailDto = getAgentDetail(new QuotationId(quotationId));
        List<PlanId> agentPlans = getAgentAuthorizedPlans(agentDetailDto.getAgentId());
        return glInsuredExcelParser.isValidInsuredExcel(insuredTemplateWorkbook, samePlanForAllCategory, samePlanForAllRelationship, agentPlans);
    }

    public List<InsuredDto> transformToInsuredDto(HSSFWorkbook workbook, String quotationId, boolean samePlanForAllCategory, boolean samePlanForAllRelations) {
        AgentDetailDto agentDetailDto = getAgentDetail(new QuotationId(quotationId));
        List<PlanId> agentAuthorizedPlans = getAgentAuthorizedPlans(agentDetailDto.getAgentId());
        List<InsuredDto> insuredDtoList = glInsuredExcelParser.transformToInsuredDto(workbook, agentAuthorizedPlans);
        return insuredDtoList;
    }

    private List<PlanId> getAgentAuthorizedPlans(String agentId) {
        List<Map<String, Object>> authorizedPlans = glQuotationFinder.getAgentAuthorizedPlan(agentId);
        List<PlanId> planIds = authorizedPlans.stream().map(new Function<Map<String, Object>, PlanId>() {
            @Override
            public PlanId apply(Map<String, Object> authorizePlanMap) {
                String planId = (String) authorizePlanMap.get("planId");
                return new PlanId(planId);
            }
        }).collect(Collectors.toList());
        return planIds;
    }

    private List<PlanId> getActivePlanByAgentId(String agentId) {
        List<Map<String, Object>> authorizedPlans = glQuotationFinder.getActivePlanTagToAgentById(agentId, LineOfBusinessEnum.GROUP_LIFE.getDescription());
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
        Map quotation = glQuotationFinder.getQuotationById(quotationId);
        List<Insured> insureds = (List<Insured>) quotation.get("insureds");
        List<InsuredDto> insuredDtoList = isNotEmpty(insureds) ? insureds.stream().map(new Function<Insured, InsuredDto>() {
            @Override
            public InsuredDto apply(Insured insured) {
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
                insuredDto.setOccupationCategory(insured.getCategory());
                insuredDto.setNoOfAssured(insured.getNoOfAssured());
                PlanPremiumDetail planPremiumDetail = insured.getPlanPremiumDetail();
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
                Set<InsuredDto.InsuredDependentDto> insuredDependentDtoList = isNotEmpty(insured.getInsuredDependents()) ? insured.getInsuredDependents().stream().map(new Function<InsuredDependent, InsuredDto.InsuredDependentDto>() {
                    @Override
                    public InsuredDto.InsuredDependentDto apply(InsuredDependent insuredDependent) {
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
                        insuredDependentDto.setOccupationCategory(insuredDependent.getCategory());
                        insuredDependentDto.setNoOfAssured(insuredDependent.getNoOfAssured());
                        PlanPremiumDetail planPremiumDetail = insuredDependent.getPlanPremiumDetail();
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
        HSSFWorkbook hssfWorkbook = glInsuredExcelGenerator.generateInsuredExcel(insuredDtoList, planIds);
        return hssfWorkbook;
    }

    public boolean isInsuredDataUpdated(String quotationId) {
        Map quotation = glQuotationFinder.getQuotationById(quotationId);
        List<Insured> insureds = (List<Insured>) quotation.get("insureds");
        return isNotEmpty(insureds);
    }

    public PremiumDetailDto getPremiumDetail(QuotationId quotationId) {
        GroupLifeQuotation groupLifeQuotation = glQuotationRepository.findOne(quotationId);
        PremiumDetailDto premiumDetailDto = getPremiumDetail(groupLifeQuotation);
        return premiumDetailDto;
    }

    public PremiumDetailDto recalculatePremium(GLRecalculatedInsuredPremiumCommand glRecalculatedInsuredPremiumCommand) {
        GroupLifeQuotation groupLifeQuotation = commandGateway.sendAndWait(glRecalculatedInsuredPremiumCommand);
        PremiumDetailDto premiumDetailDto = getPremiumDetail(groupLifeQuotation);
        return premiumDetailDto;
    }

    private PremiumDetailDto getPremiumDetail(GroupLifeQuotation groupLifeQuotation) {
        PremiumDetail premiumDetail = groupLifeQuotation.getPremiumDetail();
        if (premiumDetail == null) {
            return new PremiumDetailDto();
        }
        PremiumDetailDto premiumDetailDto = new PremiumDetailDto(premiumDetail.getAddOnBenefit(), premiumDetail.getProfitAndSolvency(), premiumDetail.getHivDiscount(), premiumDetail.getValuedClientDiscount(), premiumDetail.getLongTermDiscount(), premiumDetail.getPolicyTermValue());
        PremiumDetail.PremiumInstallment premiumInstallment = premiumDetail.getPremiumInstallment();
        if (premiumInstallment != null) {
            premiumDetailDto = premiumDetailDto.addOptedInstallmentDetail(premiumInstallment.getNoOfInstallment(), premiumInstallment.getInstallmentAmount());
        }
        if (isNotEmpty(premiumDetail.getInstallments())) {
            for (PremiumDetail.PremiumInstallment installment : premiumDetail.getInstallments()) {
                premiumDetailDto = premiumDetailDto.addInstallments(installment.getNoOfInstallment(), installment.getInstallmentAmount());
            }
        }
        premiumDetailDto = premiumDetailDto.addFrequencyPremiumAmount(premiumDetail.getAnnualPremiumAmount(), premiumDetail.getSemiAnnualPremiumAmount(), premiumDetail.getQuarterlyPremiumAmount(), premiumDetail.getMonthlyPremiumAmount());
        premiumDetailDto = premiumDetailDto.addNetTotalPremiumAmount(premiumDetail.getNetTotalPremium());
        return premiumDetailDto;
    }

    public AgentDetailDto getAgentDetail(QuotationId quotationId) {
        Map quotation = glQuotationFinder.getQuotationById(quotationId.getQuotationId());
        AgentId agentMap = (AgentId) quotation.get("agentId");
        String agentId = agentMap.getAgentId();
        Map<String, Object> agentDetail = glQuotationFinder.getAgentById(agentId);
        AgentDetailDto agentDetailDto = new AgentDetailDto();
        agentDetailDto.setAgentId(agentId);
        agentDetailDto.setBranchName((String) agentDetail.get("branchName"));
        agentDetailDto.setTeamName((String) agentDetail.get("teamName"));
        agentDetailDto.setAgentName(agentDetail.get("firstName") + " " + (agentDetail.get("lastName") == null ? "" : (String) agentDetail.get("lastName")));
        agentDetailDto.setAgentMobileNumber(agentDetail.get("mobileNumber") != null ? (String) agentDetail.get("mobileNumber") : "");
        agentDetailDto.setAgentSalutation(agentDetail.get("title") != null ? (String) agentDetail.get("title") : "");
        return agentDetailDto;
    }

    public AgentDetailDto getActiveInactiveAgentDetail(QuotationId quotationId) {
        Map quotation = glQuotationFinder.getQuotationById(quotationId.getQuotationId());
        AgentId agentMap = (AgentId) quotation.get("agentId");
        String agentId = agentMap.getAgentId();
        Map<String, Object> agentDetail = glQuotationFinder.getActiveInactiveAgentById(agentId);
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
        Map quotation = glQuotationFinder.getQuotationById(quotationId.getQuotationId());
        boolean samePlanForAllRelation = quotation.get("samePlanForAllRelation") != null ? (boolean) quotation.get("samePlanForAllRelation") : false;
        boolean samePlanForAllCategory = quotation.get("samePlanForAllCategory") != null ? (boolean) quotation.get("samePlanForAllCategory") : false;
        Proposer proposer = (Proposer) quotation.get("proposer");
        ProposerDto proposerDto = new ProposerDto(proposer);
        if (quotation.get("opportunityId") != null) {
            OpportunityId opportunityId = (OpportunityId) quotation.get("opportunityId");
            proposerDto.setOpportunityId(opportunityId.getOpportunityId());
        }
        if (quotation.get("industry") != null) {
            Industry industry = (Industry) quotation.get("industry");
            proposerDto.setIndustryId(industry.getIndustryId());
        }
        proposerDto.setSamePlanForAllCategory(samePlanForAllCategory);
        proposerDto.setSamePlanForAllRelation(samePlanForAllRelation);
        return proposerDto;
    }

    public List<GlQuotationDto> searchQuotation(SearchGlQuotationDto searchGlQuotationDto) {
        List<Map> allQuotations = glQuotationFinder.searchQuotation(searchGlQuotationDto.getQuotationNumber(), searchGlQuotationDto.getAgentCode(), searchGlQuotationDto.getProposerName(), searchGlQuotationDto.getAgentName(), searchGlQuotationDto.getQuotationId());
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
          /*
          * get only agent name
          * */
            AgentDetailDto agentDetailDto = getActiveInactiveAgentDetail(new QuotationId(quotationId));
            LocalDate generatedOn = map.get("generatedOn") != null ? new LocalDate(map.get("generatedOn")) : null;
            LocalDate sharedOn = map.get("sharedOn") != null ? new LocalDate(map.get("sharedOn")) : null;
            String quotationStatus = map.get("quotationStatus") != null ? (String) map.get("quotationStatus") : "";
            String quotationNumber = map.get("quotationNumber") != null ? (String) map.get("quotationNumber") : "";
            ObjectId parentQuotationIdMap = map.get("parentQuotationId") != null ? (ObjectId) map.get("parentQuotationId") : null;
            Proposer proposerMap = map.get("proposer") != null ? (Proposer) map.get("proposer") : null;
            String proposerName = proposerMap != null ? proposerMap.getProposerName() : "";
            String parentQuotationId = parentQuotationIdMap != null ? parentQuotationIdMap.toString() : "";
            GlQuotationDto glQuotationDto = new GlQuotationDto(new QuotationId(quotationId), (Integer) map.get("versionNumber"), generatedOn, agentDetailDto.getAgentId(), agentDetailDto.getAgentName(), new QuotationId(parentQuotationId), quotationStatus, quotationNumber, proposerName, getIntervalInDays(sharedOn), sharedOn);
            return glQuotationDto;
        }
    }
}
