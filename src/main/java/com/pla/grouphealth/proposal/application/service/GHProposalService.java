package com.pla.grouphealth.proposal.application.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.pla.core.domain.model.agent.AgentId;
import com.pla.grouphealth.proposal.application.command.GHProposalRecalculatedInsuredPremiumCommand;
import com.pla.grouphealth.proposal.domain.model.GroupHealthProposal;
import com.pla.grouphealth.proposal.presentation.dto.GHProposalDto;
import com.pla.grouphealth.proposal.presentation.dto.SearchGHProposalDto;
import com.pla.grouphealth.proposal.query.GHProposalFinder;
import com.pla.grouphealth.proposal.repository.GHProposalRepository;
import com.pla.grouphealth.quotation.presentation.dto.PlanDetailDto;
import com.pla.grouphealth.sharedresource.dto.AgentDetailDto;
import com.pla.grouphealth.sharedresource.dto.GHInsuredDto;
import com.pla.grouphealth.sharedresource.dto.GHPremiumDetailDto;
import com.pla.grouphealth.sharedresource.dto.ProposerDto;
import com.pla.grouphealth.sharedresource.model.vo.*;
import com.pla.grouphealth.sharedresource.query.GHFinder;
import com.pla.grouphealth.sharedresource.service.GHInsuredExcelGenerator;
import com.pla.grouphealth.sharedresource.service.GHInsuredExcelParser;
import com.pla.publishedlanguage.contract.IPlanAdapter;
import com.pla.sharedkernel.domain.model.ProposalNumber;
import com.pla.sharedkernel.identifier.OpportunityId;
import com.pla.sharedkernel.identifier.PlanId;
import com.pla.sharedkernel.identifier.ProposalId;
import com.pla.sharedkernel.identifier.QuotationId;
import com.pla.sharedkernel.util.PDFGeneratorUtils;
import net.sf.jasperreports.engine.JRException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.nthdimenzion.utils.UtilValidator.isEmpty;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Created by Samir on 6/24/2015.
 */
@Service
public class GHProposalService {

    private GHProposalFinder ghProposalFinder;

    private IPlanAdapter planAdapter;

    private GHFinder ghFinder;

    private GHInsuredExcelGenerator ghInsuredExcelGenerator;

    private GHInsuredExcelParser ghInsuredExcelParser;

    private GHProposalRepository ghProposalRepository;

    private CommandGateway commandGateway;

    @Autowired
    public GHProposalService(GHProposalFinder ghProposalFinder, IPlanAdapter planAdapter, GHFinder ghFinder, GHInsuredExcelGenerator ghInsuredExcelGenerator, GHInsuredExcelParser ghInsuredExcelParser, GHProposalRepository ghProposalRepository, CommandGateway commandGateway) {
        this.ghProposalFinder = ghProposalFinder;
        this.planAdapter = planAdapter;
        this.ghFinder = ghFinder;
        this.ghInsuredExcelGenerator = ghInsuredExcelGenerator;
        this.ghInsuredExcelParser = ghInsuredExcelParser;
        this.ghProposalRepository = ghProposalRepository;
        this.commandGateway = commandGateway;
    }

    public boolean hasProposalForQuotation(String quotationId) {
        Map quotationMap = ghFinder.searchQuotationById(new QuotationId(quotationId));
        String quotationNumber = (String) quotationMap.get("");
        Map proposalMap = ghProposalFinder.findProposalByQuotationNumber(quotationNumber);
        return proposalMap != null;
    }

    public List<Map> searchGeneratedQuotation(String quotationNumber) {
        return ghFinder.searchQuotation(quotationNumber, null, null, null, null, new String[]{"GENERATED", "SHARED"});
    }

    public AgentDetailDto getAgentDetail(ProposalId proposalId) {
        Map proposalMap = ghProposalFinder.findProposalById(proposalId.getProposalId());
        AgentId agentMap = (AgentId) proposalMap.get("agentId");
        String agentId = agentMap.getAgentId();
        Map<String, Object> agentDetail = ghFinder.getAgentById(agentId);
        AgentDetailDto agentDetailDto = new AgentDetailDto();
        agentDetailDto.setAgentId(agentId);
        agentDetailDto.setBranchName((String) agentDetail.get("branchName"));
        agentDetailDto.setTeamName((String) agentDetail.get("teamName"));
        agentDetailDto.setAgentName(agentDetail.get("firstName") + " " + (agentDetail.get("lastName") == null ? "" : (String) agentDetail.get("lastName")));
        agentDetailDto.setAgentMobileNumber(agentDetail.get("mobileNumber") != null ? (String) agentDetail.get("mobileNumber") : "");
        agentDetailDto.setAgentSalutation(agentDetail.get("title") != null ? (String) agentDetail.get("title") : "");
        return agentDetailDto;
    }

    public List<GHProposalDto> searchProposal(SearchGHProposalDto searchGHProposalDto, String[] statuses) {
        List<Map> allQuotations = ghProposalFinder.searchProposal(searchGHProposalDto.getProposalNumber(), searchGHProposalDto.getProposerName(), searchGHProposalDto.getAgentName(), searchGHProposalDto.getAgentCode(), searchGHProposalDto.getProposalId(), statuses);
        if (isEmpty(allQuotations)) {
            return Lists.newArrayList();
        }
        List<GHProposalDto> ghProposalDtoList = allQuotations.stream().map(new Function<Map, GHProposalDto>() {
            @Override
            public GHProposalDto apply(Map map) {
                String proposalId = map.get("_id").toString();
                AgentDetailDto agentDetailDto = getAgentDetail(new ProposalId(proposalId));
                DateTime submittedOn = map.get("submittedOn") != null ? new DateTime((Date) map.get("submittedOn")) : null;
                String quotationStatus = map.get("quotationStatus") != null ? (String) map.get("quotationStatus") : "";
                String proposalNumber = map.get("proposalNumber") != null ? ((ProposalNumber) map.get("proposalNumber")).getProposalNumber() : "";
                GHProposer proposerMap = map.get("proposer") != null ? (GHProposer) map.get("proposer") : null;
                String proposerName = proposerMap != null ? proposerMap.getProposerName() : "";
                GHProposalDto ghProposalDto = new GHProposalDto(new ProposalId(proposalId), submittedOn, agentDetailDto.getAgentId(), agentDetailDto.getAgentName(), quotationStatus, proposalNumber, proposerName);
                return ghProposalDto;
            }
        }).collect(Collectors.toList());
        return ghProposalDtoList;
    }

    public byte[] getPlanReadyReckoner(String proposalId) throws IOException, JRException {
        AgentDetailDto agentDetailDto = getAgentDetail(new ProposalId(proposalId));
        List<PlanId> planIds = getAgentAuthorizedPlans(agentDetailDto.getAgentId());
        List<PlanDetailDto> planDetailDtoList = PlanDetailDto.transformToPlanDetail(planAdapter.getPlanAndCoverageDetail(planIds));
        byte[] pdfData = PDFGeneratorUtils.createPDFReportByList(planDetailDtoList, "jasperpdf/template/grouphealth/quotation/planReadyReckoner.jrxml");
        return pdfData;
    }

    private List<PlanId> getAgentAuthorizedPlans(String agentId) {
        List<Map<String, Object>> authorizedPlans = ghProposalFinder.getAgentAuthorizedPlan(agentId);
        List<PlanId> planIds = authorizedPlans.stream().map(new Function<Map<String, Object>, PlanId>() {
            @Override
            public PlanId apply(Map<String, Object> authorizePlanMap) {
                String planId = (String) authorizePlanMap.get("planId");
                return new PlanId(planId);
            }
        }).collect(Collectors.toList());
        return planIds;
    }

    public ProposerDto getProposerDetail(ProposalId proposalId) {
        Map quotation = ghProposalFinder.findProposalById(proposalId.getProposalId());
        GHProposer proposer = (GHProposer) quotation.get("proposer");
        ProposerDto proposerDto = new ProposerDto(proposer);
        if (quotation.get("opportunityId") != null) {
            OpportunityId opportunityId = (OpportunityId) quotation.get("opportunityId");
            proposerDto.setOpportunityId(opportunityId.getOpportunityId());
        }
        return proposerDto;
    }

    public GHPremiumDetailDto getPremiumDetail(ProposalId proposalId) {
        GroupHealthProposal groupHealthProposal = ghProposalRepository.findOne(proposalId);
        GHPremiumDetailDto premiumDetailDto = getPremiumDetail(groupHealthProposal);
        return premiumDetailDto;
    }

    private GHPremiumDetailDto getPremiumDetail(GroupHealthProposal groupHealthProposal) {
        GHPremiumDetail premiumDetail = groupHealthProposal.getPremiumDetail();
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

    public GHPremiumDetailDto recalculatePremium(GHProposalRecalculatedInsuredPremiumCommand ghProposalRecalculatedInsuredPremiumCommand) {
        GroupHealthProposal groupHealthProposal = commandGateway.sendAndWait(ghProposalRecalculatedInsuredPremiumCommand);
        GHPremiumDetailDto premiumDetailDto = getPremiumDetail(groupHealthProposal);
        return premiumDetailDto;
    }

    public HSSFWorkbook getInsuredTemplateExcel(String proposalId) throws IOException {
        AgentDetailDto agentDetailDto = getAgentDetail(new ProposalId(proposalId));
        List<PlanId> planIds = getAgentAuthorizedPlans(agentDetailDto.getAgentId());
        Map quotation = ghProposalFinder.findProposalById(proposalId);
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
                insuredDto.setCategory(insured.getCategory());
                insuredDto.setOccupationClass(insured.getOccupationClass());
                insuredDto.setOccupationCategory(insured.getOccupationCategory());
                insuredDto.setNoOfAssured(insured.getNoOfAssured());
                insuredDto.setMinAgeEntry(insured.getMinAgeEntry());
                insuredDto.setMaxAgeEntry(insured.getMaxAgeEntry());
                insuredDto.setExistingIllness(insured.getExistingIllness());
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

    public boolean isValidInsuredTemplate(String proposalId, HSSFWorkbook insuredTemplateWorkbook, boolean samePlanForAllCategory, boolean samePlanForAllRelation) {
        AgentDetailDto agentDetailDto = getAgentDetail(new ProposalId(proposalId));
        List<PlanId> agentPlans = getAgentAuthorizedPlans(agentDetailDto.getAgentId());
        return ghInsuredExcelParser.isValidInsuredExcel(insuredTemplateWorkbook, samePlanForAllCategory, samePlanForAllRelation, agentPlans);
    }

    public List<GHInsuredDto> transformToInsuredDto(HSSFWorkbook insuredTemplateWorkbook, String proposalId, boolean samePlanForAllCategory, boolean samePlanForAllRelation) {
        AgentDetailDto agentDetailDto = getAgentDetail(new ProposalId(proposalId));
        List<PlanId> agentAuthorizedPlans = getAgentAuthorizedPlans(agentDetailDto.getAgentId());
        List<GHInsuredDto> insuredDtoList = ghInsuredExcelParser.transformToInsuredDto(insuredTemplateWorkbook, agentAuthorizedPlans);
        return insuredDtoList;
    }
}
