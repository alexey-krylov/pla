package com.pla.grouplife.quotation.application.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.pla.core.domain.model.agent.AgentId;
import com.pla.grouplife.quotation.domain.model.*;
import com.pla.publishedlanguage.contract.IPlanAdapter;
import com.pla.publishedlanguage.dto.PlanCoverageDetailDto;
import com.pla.grouplife.quotation.application.command.SearchGlQuotationDto;
import com.pla.grouplife.quotation.presentation.dto.GLQuotationDetailDto;
import com.pla.grouplife.quotation.presentation.dto.PlanDetailDto;
import com.pla.grouplife.quotation.query.*;
import com.pla.grouplife.quotation.repository.GlQuotationRepository;
import com.pla.sharedkernel.identifier.PlanId;
import com.pla.sharedkernel.identifier.QuotationId;
import com.pla.sharedkernel.util.PDFGeneratorUtils;
import net.sf.jasperreports.engine.JRException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.bson.types.ObjectId;
import org.nthdimenzion.presentation.AppUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    @Autowired
    public GLQuotationService(GLQuotationFinder glQuotationFinder, IPlanAdapter planAdapter, GLInsuredExcelGenerator glInsuredExcelGenerator, GLInsuredExcelParser glInsuredExcelParser, GlQuotationRepository glQuotationRepository) {
        this.glQuotationFinder = glQuotationFinder;
        this.planAdapter = planAdapter;
        this.glInsuredExcelGenerator = glInsuredExcelGenerator;
        this.glInsuredExcelParser = glInsuredExcelParser;
        this.glQuotationRepository = glQuotationRepository;
    }

    public byte[] getPlanReadyReckoner(String quotationId) throws IOException, JRException {
        AgentDetailDto agentDetailDto = getAgentDetail(new QuotationId(quotationId));
        List<PlanId> planIds = getAgentAuthorizedPlans(agentDetailDto.getAgentId());
        List<PlanDetailDto> planDetailDtoList = PlanDetailDto.transformToPlanDetail(planAdapter.getPlanAndCoverageDetail(planIds));
        byte[] pdfData = PDFGeneratorUtils.createPDFReportByList(planDetailDtoList, "jasperpdf/template/grouplife/planReadyReckoner.jrxml");
        return pdfData;
    }

    public byte[] getQuotationPDF(String quotationId) throws IOException, JRException {
        GLQuotationDetailDto glQuotationDetailDto = getGlQuotationDetailForPDF(quotationId);
        byte[] pdfData = PDFGeneratorUtils.createPDFReportByList(Arrays.asList(glQuotationDetailDto), "jasperpdf/template/grouplife/glQuotation.jrxml");
        return pdfData;
    }

    private GLQuotationDetailDto getGlQuotationDetailForPDF(String quotationId) {
        GLQuotationDetailDto glQuotationDetailDto = new GLQuotationDetailDto();
        GroupLifeQuotation quotation = glQuotationRepository.findOne(new QuotationId(quotationId));
        AgentDetailDto agentDetailDto = getAgentDetail(new QuotationId(quotationId));
        glQuotationDetailDto.setAgentBranch(agentDetailDto.getBranchName());
        glQuotationDetailDto.setAgentCode(agentDetailDto.getAgentId());
        glQuotationDetailDto.setAgentName(agentDetailDto.getAgentName());
        glQuotationDetailDto.setAgentSalutation(agentDetailDto.getAgentSalutation());
        glQuotationDetailDto.setAgentMobileNumber(agentDetailDto.getAgentMobileNumber());

        Proposer proposer = quotation.getProposer();
        glQuotationDetailDto.setProposerName(proposer.getProposerName());
        ProposerContactDetail proposerContactDetail = proposer.getContactDetail();
        glQuotationDetailDto.setProposerPhoneNumber(proposerContactDetail.getContactPersonDetail().getWorkPhoneNumber());
        glQuotationDetailDto.setProposerAddress(proposerContactDetail.toString());
        glQuotationDetailDto.setQuotationNumber(quotation.getQuotationNumber());

        PremiumDetail premiumDetail = quotation.getPremiumDetail();
        glQuotationDetailDto.setCoveragePeriod(premiumDetail.getPolicyTermValue() != null ? premiumDetail.getPolicyTermValue().toString() : "");
        glQuotationDetailDto.setProfitAndSolvencyLoading(premiumDetail.getProfitAndSolvency() != null ? premiumDetail.getProfitAndSolvency().toString() : "");
        glQuotationDetailDto.setAdditionalDiscountLoading(premiumDetail.getDiscount() != null ? premiumDetail.getDiscount().toString() : "");
        glQuotationDetailDto.setAddOnBenefits(premiumDetail.getAddOnBenefit() != null ? premiumDetail.getAddOnBenefit().toString() : "");
        glQuotationDetailDto.setAddOnBenefitsPercentage(premiumDetail.getAddOnBenefit() != null ? premiumDetail.getAddOnBenefit().toString() : "");
        glQuotationDetailDto.setWaiverOfExcessLoadings("");
        glQuotationDetailDto.setWaiverOfExcessLoadingsPercentage("");
        BigDecimal netPremiumAmount = quotation.getNetAnnualPremiumPaymentAmount(premiumDetail, quotation.getTotalBasicPremiumForInsured());
        glQuotationDetailDto.setNetPremium(netPremiumAmount != null ? netPremiumAmount.toString() : "");
        glQuotationDetailDto.setTotalPremium(netPremiumAmount != null ? netPremiumAmount.toString() : "");
        glQuotationDetailDto.setTotalLivesCovered(quotation.getTotalNoOfLifeCovered().toString());
        glQuotationDetailDto.setTotalSumAssured(quotation.getTotalSumAssured().toString());
        List<GLQuotationDetailDto.CoverDetail> coverDetails = Lists.newArrayList();
        List<GLQuotationDetailDto.Annexure> annexures = Lists.newArrayList();
        if (isNotEmpty(quotation.getInsureds())) {
            for (Insured insured : quotation.getInsureds()) {
                PlanPremiumDetail insuredPremiumDetail = insured.getPlanPremiumDetail();
                List<PlanCoverageDetailDto> planCoverageDetailDtoList = planAdapter.getPlanAndCoverageDetail(Arrays.asList(insuredPremiumDetail.getPlanId()));
                GLQuotationDetailDto.CoverDetail insuredPlanCoverDetail = glQuotationDetailDto.new CoverDetail(insured.getCategory(), "Self", planCoverageDetailDtoList.get(0).getPlanName(), insuredPremiumDetail.getSumAssured().toString());
                coverDetails.add(insuredPlanCoverDetail);
                if (isNotEmpty(insured.getCoveragePremiumDetails())) {
                    for (CoveragePremiumDetail coveragePremiumDetail : insured.getCoveragePremiumDetails()) {
                        Map<String, Object> coverageMap = glQuotationFinder.getCoverageDetail(coveragePremiumDetail.getCoverageId().getCoverageId());
                        GLQuotationDetailDto.CoverDetail insuredCoverageCoverDetail = glQuotationDetailDto.new CoverDetail(insured.getCategory(), "Self",
                                (String) coverageMap.get("coverageName"), coveragePremiumDetail.getSumAssured().toString());
                        coverDetails.add(insuredCoverageCoverDetail);
                    }
                }
                GLQuotationDetailDto.Annexure insuredAnnexure = glQuotationDetailDto.new Annexure(insured.getFirstName() + " " + insured.getLastName()
                        , insured.getNrcNumber(), insured.getGender().name(), AppUtils.toString(insured.getDateOfBirth()),
                        insured.getCategory(), "Self", AppUtils.getAge(insured.getDateOfBirth()).toString(), insured.getAnnualIncome() != null ? insured.getAnnualIncome().toString() : "", insuredPremiumDetail.getPremiumAmount() != null ? insuredPremiumDetail.getPremiumAmount().toString() : "");
                annexures.add(insuredAnnexure);
                if (isNotEmpty(insured.getInsuredDependents())) {
                    for (InsuredDependent insuredDependent : insured.getInsuredDependents()) {
                        PlanPremiumDetail insuredDependentPremiumDetail = insuredDependent.getPlanPremiumDetail();
                        List<PlanCoverageDetailDto> dependentPlanCoverageDetailDtoList = planAdapter.getPlanAndCoverageDetail(Arrays.asList(insuredDependentPremiumDetail.getPlanId()));
                        GLQuotationDetailDto.CoverDetail insuredDependentPlanCoverDetail = glQuotationDetailDto.new CoverDetail(insuredDependent.getCategory(), insuredDependent.getRelationship().description, dependentPlanCoverageDetailDtoList.get(0).getPlanName(), insuredDependentPremiumDetail.getSumAssured().toString());
                        coverDetails.add(insuredDependentPlanCoverDetail);
                        if (isNotEmpty(insuredDependent.getCoveragePremiumDetails())) {
                            for (CoveragePremiumDetail dependentCoveragePremiumDetail : insuredDependent.getCoveragePremiumDetails()) {
                                Map<String, Object> coverageMap = glQuotationFinder.getCoverageDetail(dependentCoveragePremiumDetail.getCoverageId().getCoverageId());
                                GLQuotationDetailDto.CoverDetail insuredDependentCoverageCoverDetail = glQuotationDetailDto.new CoverDetail(insuredDependent.getCategory(), insuredDependent.getRelationship().name(), (String) coverageMap.get("coverageName"), dependentCoveragePremiumDetail.getSumAssured().toString());
                                coverDetails.add(insuredDependentCoverageCoverDetail);
                            }
                        }
                        GLQuotationDetailDto.Annexure annexure = glQuotationDetailDto.new Annexure(insuredDependent.getFirstName() + " " + insuredDependent.getLastName()
                                , insuredDependent.getNrcNumber(), insuredDependent.getGender().name(), AppUtils.toString(insuredDependent.getDateOfBirth()),
                                insuredDependent.getCategory(), insuredDependent.getRelationship().description, AppUtils.getAge(insuredDependent.getDateOfBirth()).toString(), "", insuredDependentPremiumDetail.getPremiumAmount() != null ? insuredDependentPremiumDetail.getPremiumAmount().toString() : "");
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
                insuredDto.setOccupationCategory(insured.getOccupationCategory());
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
                        insuredDependentDto.setOccupationCategory(insuredDependent.getOccupationCategory());
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
        Map quotation = glQuotationFinder.getQuotationById(quotationId.getQuotationId());
        PremiumDetail premiumDetail = (PremiumDetail) quotation.get("premiumDetail");
        if (premiumDetail == null) {
            return new PremiumDetailDto();
        }
        PremiumDetailDto premiumDetailDto = new PremiumDetailDto(premiumDetail.getAddOnBenefit(), premiumDetail.getProfitAndSolvency(), premiumDetail.getDiscount(), premiumDetail.getPolicyTermValue());
        PremiumDetail.PremiumInstallment premiumInstallment = premiumDetail.getPremiumInstallment();
        if (premiumInstallment != null) {
            premiumDetailDto = premiumDetailDto.addInstallmentDetail(premiumInstallment.getNoOfInstallment(), premiumInstallment.getInstallmentAmount());
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
        agentDetailDto.setAgentName(agentDetail.get("firstName") + " " + agentDetail.get("lastName"));
        return agentDetailDto;
    }

    public ProposerDto getProposerDetail(QuotationId quotationId) {
        Map quotation = glQuotationFinder.getQuotationById(quotationId.getQuotationId());
        Proposer proposer = (Proposer) quotation.get("proposer");
        return new ProposerDto(proposer);
    }

    public List<GlQuotationDto> searchQuotation(SearchGlQuotationDto searchGlQuotationDto) {
        List<Map> allQuotations = glQuotationFinder.searchQuotation(searchGlQuotationDto.getQuotationNumber(), searchGlQuotationDto.getAgentCode(), searchGlQuotationDto.getProposerName());
        List<GlQuotationDto> glQuotationDtoList = allQuotations.stream().map(new TransformToGLQuotationDto()).collect(Collectors.toList());
        return glQuotationDtoList;
    }

    private class TransformToGLQuotationDto implements Function<Map, GlQuotationDto> {

        @Override
        public GlQuotationDto apply(Map map) {
            String quotationId = map.get("_id").toString();
            String quotationStatus = map.get("quotationStatus") != null ? (String) map.get("quotationStatus") : "";
            String quotationNumber = map.get("quotationNumber") != null ? (String) map.get("quotationNumber") : "";
            ObjectId parentQuotationIdMap = map.get("parentQuotationId") != null ? (ObjectId) map.get("parentQuotationId") : null;
            Proposer proposerMap = map.get("proposer") != null ? (Proposer) map.get("proposer") : null;
            String proposerName = proposerMap != null ? proposerMap.getProposerName() : "";
            String parentQuotationId = parentQuotationIdMap != null ? parentQuotationIdMap.toString() : "";
            GlQuotationDto glQuotationDto = new GlQuotationDto(new QuotationId(quotationId), (Integer) map.get("versionNumber"), null, null, null, new QuotationId(parentQuotationId), quotationStatus, quotationNumber, proposerName, null);
            return glQuotationDto;
        }
    }
}
