package com.pla.grouphealth.quotation.application.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.pla.core.domain.model.agent.AgentId;
import com.pla.core.domain.model.generalinformation.ProductLineGeneralInformation;
import com.pla.grouphealth.quotation.application.command.GHRecalculatedInsuredPremiumCommand;
import com.pla.grouphealth.quotation.application.command.SearchGlQuotationDto;
import com.pla.grouphealth.quotation.domain.model.GroupHealthQuotation;
import com.pla.grouphealth.quotation.presentation.dto.GHQuotationDetailDto;
import com.pla.grouphealth.quotation.presentation.dto.GLQuotationMailDto;
import com.pla.grouphealth.quotation.presentation.dto.PlanDetailDto;
import com.pla.grouphealth.quotation.query.GHQuotationFinder;
import com.pla.grouphealth.quotation.repository.GHQuotationRepository;
import com.pla.grouphealth.sharedresource.dto.*;
import com.pla.grouphealth.sharedresource.model.vo.*;
import com.pla.grouphealth.sharedresource.service.GHInsuredExcelGenerator;
import com.pla.grouphealth.sharedresource.service.GHInsuredExcelParser;
import com.pla.grouphealth.sharedresource.service.QuotationProposalUtilityService;
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
        List<PlanId> planIds = getActivePlanByAgentId(agentDetailDto.getAgentId());
        List<PlanDetailDto> planDetailDtoList = PlanDetailDto.transformToPlanDetail(planAdapter.getPlanAndCoverageDetail(planIds));
        byte[] pdfData = PDFGeneratorUtils.createPDFReportByList(planDetailDtoList, "jasperpdf/template/grouphealth/quotation/planReadyReckoner.jrxml");
        return pdfData;
    }

    private List<PlanId> getActivePlanByAgentId(String agentId) {
        List<Map<String, Object>> authorizedPlans = ghQuotationFinder.getActivePlanTagToAgentById(agentId, LineOfBusinessEnum.GROUP_HEALTH.getDescription());
        List<PlanId> planIds = authorizedPlans.stream().map(new Function<Map<String, Object>, PlanId>() {
            @Override
            public PlanId apply(Map<String, Object> authorizePlanMap) {
                String planId = (String) authorizePlanMap.get("planId");
                return new PlanId(planId);
            }
        }).collect(Collectors.toList());
        return planIds;
    }

    public byte[] getQuotationPDF(String quotationId, boolean withOutSplit) throws IOException, JRException {
        GHQuotationDetailDto ghQuotationDetailDto = getGlQuotationDetailForPDF(quotationId, withOutSplit);
        byte[] pdfData = PDFGeneratorUtils.createPDFReportByList(Arrays.asList(ghQuotationDetailDto), "jasperpdf/template/grouphealth/quotation/ghQuotation.jrxml");
        return pdfData;
    }

    public GLQuotationMailDto getPreScriptedEmail(String quotationId) {
        GroupHealthQuotation groupHealthQuotation = ghQuotationRepository.findOne(new QuotationId(quotationId));
        String subject = "PLA Insurance - Group Health - Quotation ID : " + groupHealthQuotation.getQuotationNumber();
        String mailAddress = groupHealthQuotation.getProposer().getContactDetail() != null ?isNotEmpty( groupHealthQuotation.getProposer().getContactDetail().getContactPersonDetail())? groupHealthQuotation.getProposer().getContactDetail().getContactPersonDetail().get(0).getContactPersonEmail() : "":"";
        mailAddress = isEmpty(mailAddress) ? "" : mailAddress;
        Map<String, Object> emailContent = Maps.newHashMap();
        emailContent.put("mailSentDate", groupHealthQuotation.getGeneratedOn().toString(AppConstants.DD_MM_YYY_FORMAT));
        emailContent.put("contactPersonName",
                groupHealthQuotation.getProposer().getContactDetail() != null ? isNotEmpty(groupHealthQuotation.getProposer().getContactDetail().getContactPersonDetail())?groupHealthQuotation.getProposer().getContactDetail().getContactPersonDetail().get(0).getContactPersonName() :"": "");
        emailContent.put("proposerName", groupHealthQuotation.getProposer().getProposerName());
        Map<String, Object> emailContentMap = Maps.newHashMap();
        emailContentMap.put("emailContent", emailContent);
        String emailBody = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "emailtemplate/grouphealth/quotation/grouphealthQuotationTemplate.vm", emailContentMap);
        GLQuotationMailDto dto = new GLQuotationMailDto(subject, emailBody, new String[]{mailAddress});
        dto.setQuotationId(quotationId);
        dto.setQuotationNumber(groupHealthQuotation.getQuotationNumber());
        return dto;
    }

    //TODO Need to change the JASPER Field Key as per the object property and then use BeanUtils to copy object properties
    private GHQuotationDetailDto getGlQuotationDetailForPDF(String quotationId, boolean withOutSplit) {
        GHQuotationDetailDto ghQuotationDetailDto = new GHQuotationDetailDto();
        ghQuotationDetailDto.setShowLoading(!withOutSplit);
        GroupHealthQuotation quotation = ghQuotationRepository.findOne(new QuotationId(quotationId));
        AgentDetailDto agentDetailDto = getActiveInactiveAgentDetail(new QuotationId(quotationId));
        ghQuotationDetailDto.setAgentBranch(isEmpty(agentDetailDto.getBranchName()) ? "" : agentDetailDto.getBranchName());
        ghQuotationDetailDto.setAgentCode(agentDetailDto.getAgentId());
        ghQuotationDetailDto.setAgentName(agentDetailDto.getAgentSalutation() + "  " + agentDetailDto.getAgentName());
        ghQuotationDetailDto.setAgentMobileNumber(agentDetailDto.getAgentMobileNumber());
        ghQuotationDetailDto.setQuotationDate(quotation.getGeneratedOn().toString(AppConstants.DD_MM_YYY_FORMAT));

        GHProposer proposer = quotation.getProposer();
        ghQuotationDetailDto.setProposerName(proposer.getProposerName());
        GHProposerContactDetail proposerContactDetail = proposer.getContactDetail();
        if (proposerContactDetail != null) {
            ghQuotationDetailDto.setProposerPhoneNumber(isNotEmpty(proposerContactDetail.getContactPersonDetail()) ? proposerContactDetail.getContactPersonDetail().get(0).getWorkPhoneNumber() : "");
            Map<String, Object> provinceGeoMap = ghQuotationFinder.findGeoDetail(proposerContactDetail.getProvince());
            Map<String, Object> townGeoMap = ghQuotationFinder.findGeoDetail(proposerContactDetail.getTown());
            ghQuotationDetailDto.setProposerAddress(proposerContactDetail.getAddress((String) townGeoMap.get("geoName"), (String) provinceGeoMap.get("geoName")));
        } else {
            ghQuotationDetailDto.setProposerAddress("");
            ghQuotationDetailDto.setProposerPhoneNumber("");
        }
        ghQuotationDetailDto.setQuotationNumber(quotation.getQuotationNumber() + "/" + quotation.getVersionNumber());

        GHPremiumDetail premiumDetail = quotation.getPremiumDetail();
        ghQuotationDetailDto.setCoveragePeriod(premiumDetail.getPolicyTermValue() != null ? premiumDetail.getPolicyTermValue().toString() + "  days" : "");
        ghQuotationDetailDto.setProfitAndSolvencyLoading((!withOutSplit && premiumDetail.getProfitAndSolvency() != null) ? premiumDetail.getProfitAndSolvency().toString() + "%" : "");
        ghQuotationDetailDto.setAdditionalDiscountLoading(premiumDetail.getDiscount() != null ? premiumDetail.getDiscount().toString() + "%" : "");
        ghQuotationDetailDto.setAddOnBenefits((!withOutSplit && premiumDetail.getAddOnBenefit() != null) ? premiumDetail.getAddOnBenefit().toString() + "%" : "");
        ghQuotationDetailDto.setAddOnBenefitsPercentage((!withOutSplit && premiumDetail.getAddOnBenefit() != null) ? premiumDetail.getAddOnBenefit().toString() + "%" : "");
        ghQuotationDetailDto.setWaiverOfExcessLoadings((!withOutSplit && premiumDetail.getWaiverOfExcessLoading() != null) ? premiumDetail.getWaiverOfExcessLoading().toString() + "%" : "");
        ghQuotationDetailDto.setServiceTax(premiumDetail.getVat() != null ? premiumDetail.getVat().toString() + "%" : "");
        ghQuotationDetailDto.setWaiverOfExcessLoadingsPercentage("");
        BigDecimal totalPremiumAmount = quotation.getNetAnnualPremiumPaymentAmount(premiumDetail);
        totalPremiumAmount = totalPremiumAmount.setScale(2, BigDecimal.ROUND_CEILING);
        BigDecimal netPremiumOfInsured = !withOutSplit ? quotation.getTotalBasicPremiumForInsured() : quotation.getNetAnnualPremiumPaymentAmountWithOutDiscountAndVAT(premiumDetail);
        netPremiumOfInsured = netPremiumOfInsured.setScale(2, BigDecimal.ROUND_CEILING);
        ghQuotationDetailDto.setNetPremium(netPremiumOfInsured.toPlainString());
        ghQuotationDetailDto.setTotalPremium(totalPremiumAmount.toPlainString());
        ghQuotationDetailDto.setTotalLivesCovered(quotation.getTotalNoOfLifeCovered().toString());
        BigDecimal totalSumAssured = quotation.getTotalSumAssured();
        totalSumAssured = totalSumAssured.setScale(2, BigDecimal.ROUND_CEILING);
        ghQuotationDetailDto.setTotalSumAssured(totalSumAssured.toPlainString());
        List<GHQuotationDetailDto.CoverDetail> coverDetails = Lists.newArrayList();
        List<GHQuotationDetailDto.Annexure> annexures = Lists.newArrayList();
        if (isNotEmpty(quotation.getInsureds())) {
            for (GHInsured insured : quotation.getInsureds()) {
                GHPlanPremiumDetail insuredPremiumDetail = insured.getPlanPremiumDetail();
                List<PlanCoverageDetailDto> planCoverageDetailDtoList = planAdapter.getPlanAndCoverageDetail(Arrays.asList(insuredPremiumDetail.getPlanId()));
                BigDecimal insuredPlanSA = insuredPremiumDetail.getSumAssured();
                insuredPlanSA = insuredPlanSA.setScale(2, BigDecimal.ROUND_CEILING);
                GHQuotationDetailDto.CoverDetail insuredPlanCoverDetail = ghQuotationDetailDto.new CoverDetail(isEmpty(insured.getCategory()) ? "" : insured.getCategory(), "Self", planCoverageDetailDtoList.get(0).getPlanName(), insuredPlanSA);
                coverDetails.add(insuredPlanCoverDetail);
                if (isNotEmpty(insured.getPlanPremiumDetail().getCoveragePremiumDetails())) {
                    for (GHCoveragePremiumDetail coveragePremiumDetail : insured.getPlanPremiumDetail().getCoveragePremiumDetails()) {
                        Map<String, Object> coverageMap = ghQuotationFinder.getCoverageDetail(coveragePremiumDetail.getCoverageId().getCoverageId());
                        BigDecimal insuredCoverageSA = coveragePremiumDetail.getSumAssured();
                        insuredCoverageSA = insuredCoverageSA.setScale(2, BigDecimal.ROUND_CEILING);
                        GHQuotationDetailDto.CoverDetail insuredCoverageCoverDetail = ghQuotationDetailDto.new CoverDetail(isEmpty(insured.getCategory()) ? "" : insured.getCategory(), "Self",
                                (String) coverageMap.get("coverageName"), insuredCoverageSA);
                        coverDetails.add(insuredCoverageCoverDetail);
                    }
                }
                BigDecimal insuredBasicPremium = insured.getBasicAnnualPlanPremiumIncludingNonVisibleCoveragePremium().add(insured.getInsuredBasicAnnualVisibleCoveragePremium());
                insuredBasicPremium = insuredBasicPremium.setScale(2, BigDecimal.ROUND_CEILING);
                GHQuotationDetailDto.Annexure insuredAnnexure = ghQuotationDetailDto.new Annexure(insured.getFirstName() + " " + insured.getLastName()
                        , insured.getNrcNumber(), insured.getGender() != null ? insured.getGender().name() : "", AppUtils.toString(insured.getDateOfBirth()),
                        isEmpty(insured.getCategory()) ? "" : insured.getCategory(), "Self", insured.getDateOfBirth() == null ? "" : AppUtils.getAgeOnNextBirthDate(insured.getDateOfBirth()).toString(), "",
                        insuredBasicPremium.toPlainString(), insured.getBasicAnnualPlanPremiumIncludingNonVisibleCoveragePremium().toPlainString(), insured.getInsuredBasicAnnualVisibleCoveragePremium().toPlainString());
                annexures.add(insuredAnnexure);
                if (isNotEmpty(insured.getInsuredDependents())) {
                    for (GHInsuredDependent insuredDependent : insured.getInsuredDependents()) {
                        GHPlanPremiumDetail insuredDependentPremiumDetail = insuredDependent.getPlanPremiumDetail();
                        List<PlanCoverageDetailDto> dependentPlanCoverageDetailDtoList = planAdapter.getPlanAndCoverageDetail(Arrays.asList(insuredDependentPremiumDetail.getPlanId()));
                        BigDecimal insuredDependentPlanSA = insuredDependentPremiumDetail.getSumAssured();
                        insuredDependentPlanSA = insuredDependentPlanSA.setScale(2, BigDecimal.ROUND_CEILING);
                        GHQuotationDetailDto.CoverDetail insuredDependentPlanCoverDetail = ghQuotationDetailDto.new CoverDetail(isEmpty(insuredDependent.getCategory()) ? "" : insuredDependent.getCategory(), insuredDependent.getRelationship().description, dependentPlanCoverageDetailDtoList.get(0).getPlanName(), insuredDependentPlanSA);
                        coverDetails.add(insuredDependentPlanCoverDetail);
                        if (isNotEmpty(insuredDependent.getPlanPremiumDetail().getCoveragePremiumDetails())) {
                            for (GHCoveragePremiumDetail dependentCoveragePremiumDetail : insuredDependent.getPlanPremiumDetail().getCoveragePremiumDetails()) {
                                BigDecimal insuredDependentCoverageSA = dependentCoveragePremiumDetail.getSumAssured();
                                insuredDependentCoverageSA = insuredDependentCoverageSA.setScale(2, BigDecimal.ROUND_CEILING);
                                Map<String, Object> coverageMap = ghQuotationFinder.getCoverageDetail(dependentCoveragePremiumDetail.getCoverageId().getCoverageId());
                                GHQuotationDetailDto.CoverDetail insuredDependentCoverageCoverDetail = ghQuotationDetailDto.new CoverDetail(isEmpty(insuredDependent.getCategory()) ? "" : insuredDependent.getCategory(), insuredDependent.getRelationship().name(), (String) coverageMap.get("coverageName"), insuredDependentCoverageSA);
                                coverDetails.add(insuredDependentCoverageCoverDetail);
                            }
                        }
                        BigDecimal insuredDependentBasicPremium = insuredDependent.getBasicAnnualPlanPremiumIncludingNonVisibleCoveragePremiumForDependent().add(insuredDependent.getBasicAnnualVisibleCoveragePremiumForDependent());
                        insuredDependentBasicPremium = insuredDependentBasicPremium.setScale(2, BigDecimal.ROUND_CEILING);
                        GHQuotationDetailDto.Annexure annexure = ghQuotationDetailDto.new Annexure(insuredDependent.getFirstName() + " " + insuredDependent.getLastName()
                                , insuredDependent.getNrcNumber(), insuredDependent.getGender() != null ? insuredDependent.getGender().name() : "", AppUtils.toString(insuredDependent.getDateOfBirth()),
                                isEmpty(insuredDependent.getCategory()) ? "" : insuredDependent.getCategory(), insuredDependent.getRelationship().description, insuredDependent.getDateOfBirth() == null ? "" : AppUtils.getAgeOnNextBirthDate(insuredDependent.getDateOfBirth()).toString(), "",
                                insuredDependentBasicPremium.toPlainString(), insuredDependent.getBasicAnnualPlanPremiumIncludingNonVisibleCoveragePremiumForDependent().toPlainString(), insuredDependent.getBasicAnnualVisibleCoveragePremiumForDependent().toPlainString());
                        annexures.add(annexure);
                    }
                }
            }
        }
        List<GHQuotationDetailDto.CoverDetail> unifiedCoverDetails = unifyCoverDetails(coverDetails, ghQuotationDetailDto);
        ghQuotationDetailDto.setCoverDetails(unifiedCoverDetails);
        ghQuotationDetailDto.setAnnexure(annexures);
        return ghQuotationDetailDto;
    }

    private List<GHQuotationDetailDto.CoverDetail> unifyCoverDetails(List<GHQuotationDetailDto.CoverDetail> coverDetails, GHQuotationDetailDto ghQuotationDetailDto) {
        Map<GHQuotationDetailDto.CoverDetail, List<GHQuotationDetailDto.CoverDetail>> coverDetailsMap = Maps.newLinkedHashMap();
        for (GHQuotationDetailDto.CoverDetail coverDetail : coverDetails) {
            if (coverDetailsMap.get(coverDetail) == null) {
                coverDetailsMap.put(coverDetail, new ArrayList<>());
            } else {
                List<GHQuotationDetailDto.CoverDetail> coverDetailList = coverDetailsMap.get(coverDetail);
                coverDetailList.add(coverDetail);
                coverDetailsMap.put(coverDetail, coverDetailList);
            }
        }
        List<GHQuotationDetailDto.CoverDetail> unifiedCoverDetails = coverDetailsMap.entrySet().stream().map(new Function<Map.Entry<GHQuotationDetailDto.CoverDetail, List<GHQuotationDetailDto.CoverDetail>>, GHQuotationDetailDto.CoverDetail>() {
            @Override
            public GHQuotationDetailDto.CoverDetail apply(Map.Entry<GHQuotationDetailDto.CoverDetail, List<GHQuotationDetailDto.CoverDetail>> coverDetailListEntry) {
                GHQuotationDetailDto.CoverDetail coverDetailKey = coverDetailListEntry.getKey();
                GHQuotationDetailDto.CoverDetail coverDetail = ghQuotationDetailDto.new CoverDetail(coverDetailKey.getCategory(), coverDetailKey.getRelationship(), coverDetailKey.getPlanCoverageName());
                BigDecimal totalSA = BigDecimal.ZERO;
                for (GHQuotationDetailDto.CoverDetail valueCoverDetail : coverDetailListEntry.getValue()) {
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
        return ghInsuredExcelParser.isValidInsuredExcel(insuredTemplateWorkbook, samePlanForAllCategory, samePlanForAllRelationship, agentPlans);
    }

    public List<GHInsuredDto> transformToInsuredDto(HSSFWorkbook workbook, String quotationId, boolean samePlanForAllCategory, boolean samePlanForAllRelations) {
        AgentDetailDto agentDetailDto = getAgentDetail(new QuotationId(quotationId));
        List<PlanId> agentAuthorizedPlans = getAgentAuthorizedPlans(agentDetailDto.getAgentId());
        List<GHInsuredDto> insuredDtoList = ghInsuredExcelParser.transformToInsuredDto(workbook, agentAuthorizedPlans);
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
        List<GHInsuredDto> insuredDtoList = isNotEmpty(insureds) ? insureds.stream().map(new Function<GHInsured, GHInsuredDto>() {
            @Override
            public GHInsuredDto apply(GHInsured insured) {
                GHInsuredDto insuredDto = new GHInsuredDto();
                insuredDto.setCompanyName(insured.getCompanyName());
                insuredDto.setManNumber(insured.getManNumber());
                insuredDto.setNrcNumber(insured.getNrcNumber());
                insuredDto.setSalutation(insured.getSalutation());
                insuredDto.setFirstName(insured.getFirstName());
                insuredDto.setLastName(insured.getLastName());
                insuredDto.setDateOfBirth(insured.getDateOfBirth());
                insuredDto.setGender(insured.getGender());
                insuredDto.setPremiumType(insured.getPremiumType());
                insuredDto.setCategory(insured.getCategory());
                insuredDto.setOccupationClass(insured.getOccupationClass());
                insuredDto.setOccupationCategory(insured.getOccupationCategory());
                insuredDto.setNoOfAssured(insured.getNoOfAssured());
                insuredDto.setMinAgeEntry(insured.getMinAgeEntry());
                insuredDto.setMaxAgeEntry(insured.getMaxAgeEntry());
                insuredDto.setExistingIllness(insured.getExistingIllness());
                insuredDto.setRateOfPremium(insured.getRateOfPremium());
                GHPlanPremiumDetail planPremiumDetail = insured.getPlanPremiumDetail();
                GHInsuredDto.GHPlanPremiumDetailDto planPremiumDetailDto = new GHInsuredDto.GHPlanPremiumDetailDto(planPremiumDetail.getPlanId().getPlanId(), planPremiumDetail.getPlanCode(), planPremiumDetail.getPremiumAmount(), planPremiumDetail.getSumAssured());
                insuredDto = insuredDto.addPlanPremiumDetail(planPremiumDetailDto);
                List<GHInsuredDto.GHCoveragePremiumDetailDto> coveragePremiumDetailDtoList = isNotEmpty(insured.getPlanPremiumDetail().getCoveragePremiumDetails()) ? insured.getPlanPremiumDetail().getCoveragePremiumDetails().stream().map(new Function<GHCoveragePremiumDetail, GHInsuredDto.GHCoveragePremiumDetailDto>() {
                    @Override
                    public GHInsuredDto.GHCoveragePremiumDetailDto apply(GHCoveragePremiumDetail coveragePremiumDetail) {
                        final GHInsuredDto.GHCoveragePremiumDetailDto[] coveragePremiumDetailDto = {new GHInsuredDto.GHCoveragePremiumDetailDto(coveragePremiumDetail.getCoverageCode(),
                                coveragePremiumDetail.getCoverageId().getCoverageId(), coveragePremiumDetail.getPremium(), coveragePremiumDetail.getSumAssured(), coveragePremiumDetail.getPremiumVisibility())};
                        if (isNotEmpty(coveragePremiumDetail.getBenefitPremiumLimits())) {
                            coveragePremiumDetail.getBenefitPremiumLimits().forEach(benefitPremiumLimit -> {
                                GHInsuredDto.GHCoveragePremiumDetailDto.GHCoverageBenefitDetailDto ghCoverageBenefitDetailDto = new GHInsuredDto.GHCoveragePremiumDetailDto.GHCoverageBenefitDetailDto();
                                ghCoverageBenefitDetailDto.setBenefitCode(benefitPremiumLimit.getBenefitCode());
                                ghCoverageBenefitDetailDto.setBenefitId(benefitPremiumLimit.getBenefitId() != null ? benefitPremiumLimit.getBenefitId().getBenefitId() : "");
                                ghCoverageBenefitDetailDto.setBenefitLimit(benefitPremiumLimit.getBenefitLimit());
                                coveragePremiumDetailDto[0] = coveragePremiumDetailDto[0].addBenefit(ghCoverageBenefitDetailDto);
                            });
                        }
                        return coveragePremiumDetailDto[0];
                    }
                }).collect(Collectors.toList()) : Lists.newArrayList();
                insuredDto = insuredDto.addCoveragePremiumDetails(coveragePremiumDetailDtoList);
                Set<GHInsuredDto.GHInsuredDependentDto> insuredDependentDtoList = isNotEmpty(insured.getInsuredDependents()) ? insured.getInsuredDependents().stream().map(new Function<GHInsuredDependent, GHInsuredDto.GHInsuredDependentDto>() {
                    @Override
                    public GHInsuredDto.GHInsuredDependentDto apply(GHInsuredDependent insuredDependent) {
                        GHInsuredDto.GHInsuredDependentDto insuredDependentDto = new GHInsuredDto.GHInsuredDependentDto();
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
                        insuredDependentDto.setExistingIllness(insuredDependent.getExistingIllness());
                        insuredDependentDto.setMinAgeEntry(insuredDependent.getMinAgeEntry());
                        insuredDependentDto.setMaxAgeEntry(insuredDependent.getMaxAgeEntry());
                        insuredDependentDto.setOccupationClass(insuredDependent.getOccupationClass());
                        insuredDependentDto.setOccupationCategory(insuredDependent.getOccupationCategory());
                        insuredDependentDto.setNoOfAssured(insuredDependent.getNoOfAssured());
                        GHPlanPremiumDetail planPremiumDetail = insuredDependent.getPlanPremiumDetail();
                        GHInsuredDto.GHPlanPremiumDetailDto planPremiumDetailDto = new GHInsuredDto.GHPlanPremiumDetailDto(planPremiumDetail.getPlanId().getPlanId(), planPremiumDetail.getPlanCode(), planPremiumDetail.getPremiumAmount(), planPremiumDetail.getSumAssured());
                        insuredDependentDto = insuredDependentDto.addPlanPremiumDetail(planPremiumDetailDto);
                        List<GHInsuredDto.GHCoveragePremiumDetailDto> dependentCoveragePremiumDetailDtoList = isNotEmpty(insuredDependent.getPlanPremiumDetail().getCoveragePremiumDetails()) ? insuredDependent.getPlanPremiumDetail().getCoveragePremiumDetails().stream().map(new Function<GHCoveragePremiumDetail, GHInsuredDto.GHCoveragePremiumDetailDto>() {
                            @Override
                            public GHInsuredDto.GHCoveragePremiumDetailDto apply(GHCoveragePremiumDetail coveragePremiumDetail) {
                                final GHInsuredDto.GHCoveragePremiumDetailDto[] coveragePremiumDetailDto = {new GHInsuredDto.GHCoveragePremiumDetailDto(coveragePremiumDetail.getCoverageCode(),
                                        coveragePremiumDetail.getCoverageId().getCoverageId(), coveragePremiumDetail.getPremium(), coveragePremiumDetail.getSumAssured(), coveragePremiumDetail.getPremiumVisibility())};
                                if (isNotEmpty(coveragePremiumDetail.getBenefitPremiumLimits())) {
                                    coveragePremiumDetail.getBenefitPremiumLimits().forEach(benefitPremiumLimit -> {
                                        GHInsuredDto.GHCoveragePremiumDetailDto.GHCoverageBenefitDetailDto ghCoverageBenefitDetailDto = new GHInsuredDto.GHCoveragePremiumDetailDto.GHCoverageBenefitDetailDto();
                                        ghCoverageBenefitDetailDto.setBenefitCode(benefitPremiumLimit.getBenefitCode());
                                        ghCoverageBenefitDetailDto.setBenefitId(benefitPremiumLimit.getBenefitId() != null ? benefitPremiumLimit.getBenefitId().getBenefitId() : "");
                                        ghCoverageBenefitDetailDto.setBenefitLimit(benefitPremiumLimit.getBenefitLimit());
                                        coveragePremiumDetailDto[0] = coveragePremiumDetailDto[0].addBenefit(ghCoverageBenefitDetailDto);
                                    });
                                }
                                return coveragePremiumDetailDto[0];
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

    public GHPremiumDetailDto getPremiumDetail(QuotationId quotationId) {
        GroupHealthQuotation groupHealthQuotation = ghQuotationRepository.findOne(quotationId);
        GHPremiumDetailDto premiumDetailDto = getPremiumDetail(groupHealthQuotation);
        premiumDetailDto.updateWithStatus(groupHealthQuotation.getQuotationStatus());
        return premiumDetailDto;
    }

    public GHPremiumDetailDto recalculatePremium(GHRecalculatedInsuredPremiumCommand glRecalculatedInsuredPremiumCommand) {
        GroupHealthQuotation groupHealthQuotation = commandGateway.sendAndWait(glRecalculatedInsuredPremiumCommand);
        GHPremiumDetailDto premiumDetailDto = getPremiumDetail(groupHealthQuotation);
        premiumDetailDto.updateWithStatus(groupHealthQuotation.getQuotationStatus());
        return premiumDetailDto;
    }

    private GHPremiumDetailDto getPremiumDetail(GroupHealthQuotation groupHealthQuotation) {
        GHPremiumDetail premiumDetail = groupHealthQuotation.getPremiumDetail();
        if (premiumDetail == null) {
            return new GHPremiumDetailDto();
        }

        GHPremiumDetailDto premiumDetailDto = new GHPremiumDetailDto(premiumDetail.getAddOnBenefit(), premiumDetail.getProfitAndSolvency(), premiumDetail.getDiscount(), premiumDetail.getWaiverOfExcessLoading(), premiumDetail.getVat(), premiumDetail.getPolicyTermValue());
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

    public AgentDetailDto getActiveInactiveAgentDetail(QuotationId quotationId) {
        Map quotation = ghQuotationFinder.getQuotationById(quotationId.getQuotationId());
        AgentId agentMap = (AgentId) quotation.get("agentId");
        String agentId = agentMap.getAgentId();
        Map<String, Object> agentDetail = ghQuotationFinder.getActiveInactiveAgentById(agentId);
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
        boolean moratoriumPeriodApplicable = quotation.get("moratoriumPeriodApplicable") != null ? (boolean) quotation.get("moratoriumPeriodApplicable") : false;
        boolean samePlanForAllRelation = quotation.get("samePlanForAllRelation") != null ? (boolean) quotation.get("samePlanForAllRelation") : false;
        boolean samePlanForAllCategory = quotation.get("samePlanForAllCategory") != null ? (boolean) quotation.get("samePlanForAllCategory") : false;
        String schemeName = quotation.get("schemeName") != null ? (String) quotation.get("schemeName") : "";
        GHProposer proposer = (GHProposer) quotation.get("proposer");
        ProposerDto proposerDto = new ProposerDto(proposer);
        if (quotation.get("opportunityId") != null) {
            OpportunityId opportunityId = (OpportunityId) quotation.get("opportunityId");
            proposerDto.setOpportunityId(opportunityId.getOpportunityId());
        }
        proposerDto.setConsiderMoratoriumPeriod(moratoriumPeriodApplicable);
        proposerDto.setSamePlanForAllRelation(samePlanForAllRelation);
        proposerDto.setSamePlanForAllCategory(samePlanForAllCategory);
        proposerDto.setSchemeName(schemeName);
        return proposerDto;
    }

    public List<GlQuotationDto> searchQuotation(SearchGlQuotationDto searchGlQuotationDto) {
        List<Map> allQuotations = ghQuotationFinder.searchQuotation(searchGlQuotationDto.getQuotationNumber(), searchGlQuotationDto.getAgentCode(), searchGlQuotationDto.getProposerName(), searchGlQuotationDto.getAgentName(), searchGlQuotationDto.getQuotationId());
        if (isEmpty(allQuotations)) {
            return Lists.newArrayList();
        }
        List<GlQuotationDto> glQuotationDtoList = allQuotations.stream().map(new TransformToGLQuotationDto()).collect(Collectors.toList());
        return glQuotationDtoList;
    }

    public boolean validateIfLessThanMinimumNoOfPersonsForGHQuotation(QuotationId quotationId) {
        GroupHealthQuotation groupHealthQuotation = ghQuotationRepository.findOne(quotationId);
        ProductLineGeneralInformation productLineInformation = ghQuotationFinder.getGHProductLineInformation();
        return QuotationProposalUtilityService.validateIfLessThanMinimumNoOfPersonsForGHQuotation(groupHealthQuotation, productLineInformation);
    }


    public boolean validateIfLessThanMinimumPremiumForGHQuotation(QuotationId quotationId) {
        GroupHealthQuotation groupHealthQuotation = ghQuotationRepository.findOne(quotationId);
        ProductLineGeneralInformation productLineInformation = ghQuotationFinder.getGHProductLineInformation();
        return QuotationProposalUtilityService.validateIfLessThanMinimumPremiumForGHQuotation(groupHealthQuotation, productLineInformation);
    }

    private class TransformToGLQuotationDto implements Function<Map, GlQuotationDto> {

        @Override
        public GlQuotationDto apply(Map map) {
            String quotationId = map.get("_id").toString();
            AgentDetailDto agentDetailDto = getActiveInactiveAgentDetail(new QuotationId(quotationId));
            LocalDate generatedOn = map.get("generatedOn") != null ? new LocalDate((Date) map.get("generatedOn")) : null;
            LocalDate sharedOn = map.get("sharedOn") != null ? new LocalDate((Date) map.get("sharedOn")) : null;
            String quotationStatus = map.get("quotationStatus") != null ? (String) map.get("quotationStatus") : "";
            String quotationNumber = map.get("quotationNumber") != null ? (String) map.get("quotationNumber") : "";
            ObjectId parentQuotationIdMap = map.get("parentQuotationId") != null ? (ObjectId) map.get("parentQuotationId") : null;
            GHProposer proposerMap = map.get("proposer") != null ? (GHProposer) map.get("proposer") : null;
            String proposerName = proposerMap != null ? proposerMap.getProposerName() : "";
            String parentQuotationId = parentQuotationIdMap != null ? parentQuotationIdMap.toString() : "";
            GlQuotationDto glQuotationDto = new GlQuotationDto(new QuotationId(quotationId), (Integer) map.get("versionNumber"), generatedOn, agentDetailDto.getAgentId(), agentDetailDto.getAgentName(), new QuotationId(parentQuotationId), quotationStatus, quotationNumber, proposerName, getIntervalInDays(sharedOn), sharedOn);
            return glQuotationDto;
        }
    }
}
