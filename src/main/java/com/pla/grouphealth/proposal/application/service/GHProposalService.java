package com.pla.grouphealth.proposal.application.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mongodb.gridfs.GridFSDBFile;
import com.pla.core.domain.model.agent.AgentId;
import com.pla.grouphealth.proposal.application.command.GHProposalRecalculatedInsuredPremiumCommand;
import com.pla.grouphealth.proposal.domain.model.GroupHealthProposal;
import com.pla.grouphealth.proposal.domain.model.GroupHealthProposalStatusAudit;
import com.pla.grouphealth.proposal.presentation.dto.GHProposalDto;
import com.pla.grouphealth.proposal.presentation.dto.GHProposalMandatoryDocumentDto;
import com.pla.grouphealth.proposal.presentation.dto.ProposalApproverCommentsDto;
import com.pla.grouphealth.proposal.presentation.dto.SearchGHProposalDto;
import com.pla.grouphealth.proposal.query.GHProposalFinder;
import com.pla.grouphealth.proposal.repository.GHProposalRepository;
import com.pla.grouphealth.proposal.repository.GHProposalStatusAuditRepository;
import com.pla.grouphealth.quotation.presentation.dto.PlanDetailDto;
import com.pla.grouphealth.quotation.query.GHQuotationFinder;
import com.pla.grouphealth.sharedresource.dto.*;
import com.pla.grouphealth.sharedresource.model.vo.*;
import com.pla.grouphealth.sharedresource.query.GHFinder;
import com.pla.grouphealth.sharedresource.service.GHInsuredExcelGenerator;
import com.pla.grouphealth.sharedresource.service.GHInsuredExcelParser;
import com.pla.publishedlanguage.contract.IPlanAdapter;
import com.pla.publishedlanguage.dto.ClientDocumentDto;
import com.pla.publishedlanguage.dto.SearchDocumentDetailDto;
import com.pla.publishedlanguage.underwriter.contract.IUnderWriterAdapter;
import com.pla.sharedkernel.domain.model.ProcessType;
import com.pla.sharedkernel.identifier.*;
import com.pla.sharedkernel.util.PDFGeneratorUtils;
import net.sf.jasperreports.engine.JRException;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.nthdimenzion.presentation.AppUtils.getIntervalInDays;
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
    private GHProposalStatusAuditRepository ghProposalStatusAuditRepository;

    @Autowired
    private GHQuotationFinder ghQuotationFinder;

    @Autowired
    private IUnderWriterAdapter underWriterAdapter;

    @Autowired
    private GridFsTemplate gridFsTemplate;

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
        String quotationNumber = (String) quotationMap.get("quotationNumber");
        Map proposalMap = ghProposalFinder.findProposalByQuotationNumber(quotationNumber);
        return proposalMap != null;
    }

    public boolean isAgentActive(String quotationId){
        Map quotationMap = ghFinder.searchQuotationById(new QuotationId(quotationId));
        AgentId agentId = (AgentId) quotationMap.get("agentId");
        Map<String, Object> agentCount =  ghFinder.getAgentById(agentId.getAgentId());
        return isNotEmpty(agentCount)?true:false;
    }

    public List<GlQuotationDto> searchGeneratedQuotation(String quotationNumber) {
        List<Map> allQuotations = ghFinder.searchQuotation(quotationNumber, null, null, null, null, new String[]{"SHARED"});
        if (isEmpty(allQuotations)) {
            return Lists.newArrayList();
        }
        List<GlQuotationDto> glQuotationDtoList = allQuotations.stream().map(new TransformToGLQuotationDto()).collect(Collectors.toList());
        return glQuotationDtoList;
    }

    public AgentDetailDto getAgentDetail(ProposalId proposalId) {
        Map proposalMap = ghProposalFinder.findProposalById(proposalId.getProposalId());
        AgentId agentMap = (AgentId) proposalMap.get("agentId");
        String agentId = agentMap.getAgentId();
        Map<String, Object> agentDetail = ghProposalFinder.getAgentById(agentId);
        AgentDetailDto agentDetailDto = new AgentDetailDto();
        agentDetailDto.setAgentId(agentId);
        agentDetailDto.setActive("ACTIVE".equalsIgnoreCase((String) agentDetail.get("agentStatus")));
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
                DateTime submittedOn = map.get("submittedOn") != null ? new DateTime(map.get("submittedOn")) : null;
                String proposalStatus = map.get("proposalStatus") != null ? ProposalStatus.valueOf((String) map.get("proposalStatus")).getDescription() : "";
                String proposalNumber = map.get("proposalNumber") != null ? ((ProposalNumber) map.get("proposalNumber")).getProposalNumber() : "";
                GHProposer proposerMap = map.get("proposer") != null ? (GHProposer) map.get("proposer") : null;
                String proposerName = proposerMap != null ? proposerMap.getProposerName() : "";
                GHProposalDto ghProposalDto = new GHProposalDto(proposalId, submittedOn, agentDetailDto.getAgentId(), agentDetailDto.getAgentName(), proposalStatus, proposalNumber, proposerName);
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
        Map proposal = ghProposalFinder.findProposalById(proposalId.getProposalId());
        GHProposer proposer = (GHProposer) proposal.get("proposer");
        ProposerDto proposerDto = new ProposerDto(proposer);
        if (proposal.get("opportunityId") != null) {
            OpportunityId opportunityId = (OpportunityId) proposal.get("opportunityId");
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
        premiumDetailDto = premiumDetailDto.updateWithOptedFrequency(premiumDetail.getOptedFrequencyPremium() != null ? premiumDetail.getOptedFrequencyPremium().getPremiumFrequency() : null);
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

    public List<ProposalApproverCommentsDto> findApproverComments(String proposalId) {
        List<GroupHealthProposalStatusAudit> audits = ghProposalStatusAuditRepository.findByProposalId(new ProposalId(proposalId));
        List<ProposalApproverCommentsDto> proposalApproverCommentsDtos = Lists.newArrayList();
        if (isNotEmpty(audits)) {
            proposalApproverCommentsDtos = audits.stream().map(new Function<GroupHealthProposalStatusAudit, ProposalApproverCommentsDto>() {
                @Override
                public ProposalApproverCommentsDto apply(GroupHealthProposalStatusAudit groupHealthProposalStatusAudit) {
                    ProposalApproverCommentsDto proposalApproverCommentsDto = new ProposalApproverCommentsDto();
                    try {
                        BeanUtils.copyProperties(proposalApproverCommentsDto, groupHealthProposalStatusAudit);
                        proposalApproverCommentsDto.setStatus(proposalApproverCommentsDto.getProposalStatus().getDescription());
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                    return proposalApproverCommentsDto;
                }
            }).collect(Collectors.toList());
        }
        return proposalApproverCommentsDtos;
    }

    public Set<GHProposalMandatoryDocumentDto> findMandatoryDocuments(String proposalId) {
        List<GHProposerDocument> uploadedDocuments = getUploadedMandatoryDocument(proposalId);
        Set<ClientDocumentDto> mandatoryDocuments = getMandatoryDocumentRequiredForSubmission(proposalId);
        return getAllMandatoryDocument(uploadedDocuments,mandatoryDocuments);
    }

    public List<GHProposerDocument> getUploadedMandatoryDocument(String proposalId){
        Map proposal = ghProposalFinder.findProposalById(proposalId);
        List<GHProposerDocument> uploadedDocuments = proposal.get("proposerDocuments") != null ? (List<GHProposerDocument>) proposal.get("proposerDocuments") : Lists.newArrayList();
        return uploadedDocuments;
    }

    public Set<ClientDocumentDto> getMandatoryDocumentRequiredForSubmission(String proposalId){
        Map proposal = ghProposalFinder.findProposalById(proposalId);
        List<GHInsured> insureds = (List<GHInsured>) proposal.get("insureds");
        List<SearchDocumentDetailDto> documentDetailDtos = Lists.newArrayList();
        insureds.forEach(ghInsured -> {
            GHPlanPremiumDetail planPremiumDetail = ghInsured.getPlanPremiumDetail();
            SearchDocumentDetailDto searchDocumentDetailDto = new SearchDocumentDetailDto(planPremiumDetail.getPlanId());
            documentDetailDtos.add(searchDocumentDetailDto);
            if (isNotEmpty(planPremiumDetail.getCoveragePremiumDetails())) {
                List<CoverageId> coverageIds = planPremiumDetail.getCoveragePremiumDetails().stream().map(new Function<GHCoveragePremiumDetail, CoverageId>() {
                    @Override
                    public CoverageId apply(GHCoveragePremiumDetail ghCoveragePremiumDetail) {
                        return ghCoveragePremiumDetail.getCoverageId();
                    }
                }).collect(Collectors.toList());
                documentDetailDtos.add(new SearchDocumentDetailDto(planPremiumDetail.getPlanId(), coverageIds));
            }
            if (isNotEmpty(ghInsured.getInsuredDependents())) {
                ghInsured.getInsuredDependents().forEach(insuredDependent -> {
                    GHPlanPremiumDetail dependentPlanPremiumDetail = insuredDependent.getPlanPremiumDetail();
                    documentDetailDtos.add(new SearchDocumentDetailDto(dependentPlanPremiumDetail.getPlanId()));
                    if (isNotEmpty(dependentPlanPremiumDetail.getCoveragePremiumDetails())) {
                        List<CoverageId> coverageIds = dependentPlanPremiumDetail.getCoveragePremiumDetails().stream().map(new Function<GHCoveragePremiumDetail, CoverageId>() {
                            @Override
                            public CoverageId apply(GHCoveragePremiumDetail ghCoveragePremiumDetail) {
                                return ghCoveragePremiumDetail.getCoverageId();
                            }
                        }).collect(Collectors.toList());
                        documentDetailDtos.add(new SearchDocumentDetailDto(planPremiumDetail.getPlanId(), coverageIds));
                    }
                });
            }
        });
        return underWriterAdapter.getMandatoryDocumentsForApproverApproval(documentDetailDtos, ProcessType.ENROLLMENT);
    }

    private Set<GHProposalMandatoryDocumentDto> getAllMandatoryDocument(List<GHProposerDocument> uploadedDocument,Set<ClientDocumentDto> mandatoryDocuments){
        Set<GHProposalMandatoryDocumentDto> mandatoryDocumentDtos = Sets.newHashSet();
        if (isNotEmpty(mandatoryDocuments)) {
            mandatoryDocumentDtos = mandatoryDocuments.stream().map(new Function<ClientDocumentDto, GHProposalMandatoryDocumentDto>() {
                @Override
                public GHProposalMandatoryDocumentDto apply(ClientDocumentDto clientDocumentDto) {
                    GHProposalMandatoryDocumentDto mandatoryDocumentDto = new GHProposalMandatoryDocumentDto(clientDocumentDto.getDocumentCode(), clientDocumentDto.getDocumentName());
                    Optional<GHProposerDocument> proposerDocumentOptional = uploadedDocument.stream().filter(new Predicate<GHProposerDocument>() {
                        @Override
                        public boolean test(GHProposerDocument ghProposerDocument) {
                            return clientDocumentDto.getDocumentCode().equals(ghProposerDocument.getDocumentId());
                        }
                    }).findAny();
                    if (proposerDocumentOptional.isPresent()) {
                        try {
                            if (isNotEmpty(proposerDocumentOptional.get().getGridFsDocId())) {
                                GridFSDBFile gridFSDBFile = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(proposerDocumentOptional.get().getGridFsDocId())));
                                mandatoryDocumentDto.setFileName(gridFSDBFile.getFilename());
                                mandatoryDocumentDto.setContentType(gridFSDBFile.getContentType());
                                mandatoryDocumentDto.setGridFsDocId(gridFSDBFile.getId().toString());
                                mandatoryDocumentDto.updateWithContent(IOUtils.toByteArray(gridFSDBFile.getInputStream()));
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    return mandatoryDocumentDto;
                }
            }).collect(Collectors.toSet());
        }
        return mandatoryDocumentDtos;
    }


    public Set<GHProposalMandatoryDocumentDto> findAdditionalDocuments(String proposalId) {
        Map proposal = ghProposalFinder.findProposalById(proposalId);
        List<GHProposerDocument> uploadedDocuments = proposal.get("proposerDocuments") != null ? (List<GHProposerDocument>) proposal.get("proposerDocuments") : Lists.newArrayList();
        Set<GHProposalMandatoryDocumentDto> mandatoryDocumentDtos = Sets.newHashSet();
        if (isNotEmpty(uploadedDocuments)) {
            mandatoryDocumentDtos = uploadedDocuments.stream().filter(uploadedDocument -> !uploadedDocument.isMandatory()).map(new Function<GHProposerDocument, GHProposalMandatoryDocumentDto>() {
                @Override
                public GHProposalMandatoryDocumentDto apply(GHProposerDocument ghProposerDocument) {
                    GHProposalMandatoryDocumentDto mandatoryDocumentDto = new GHProposalMandatoryDocumentDto(ghProposerDocument.getDocumentId(), ghProposerDocument.getDocumentName());
                    GridFSDBFile gridFSDBFile = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(ghProposerDocument.getGridFsDocId())));
                    mandatoryDocumentDto.setFileName(gridFSDBFile.getFilename());
                    mandatoryDocumentDto.setContentType(gridFSDBFile.getContentType());
                    mandatoryDocumentDto.setGridFsDocId(gridFSDBFile.getId().toString());
                    try {
                        mandatoryDocumentDto.updateWithContent(IOUtils.toByteArray(gridFSDBFile.getInputStream()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return mandatoryDocumentDto;
                }
            }).collect(Collectors.toSet());
        }
        return mandatoryDocumentDtos;
    }


    private class TransformToGLQuotationDto implements Function<Map, GlQuotationDto> {

        @Override
        public GlQuotationDto apply(Map map) {
            String quotationId = map.get("_id").toString();
            AgentDetailDto agentDetailDto = getAgentDetail(new QuotationId(quotationId));
            LocalDate generatedOn = map.get("generatedOn") != null ? new LocalDate(map.get("generatedOn")) : null;
            LocalDate sharedOn = map.get("sharedOn") != null ? new LocalDate(map.get("sharedOn")) : null;
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
