package com.pla.grouplife.proposal.application.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mongodb.gridfs.GridFSDBFile;
import com.pla.core.domain.model.agent.AgentId;
import com.pla.grouplife.proposal.application.command.GLRecalculatedInsuredPremiumCommand;
import com.pla.grouplife.proposal.domain.model.GroupLifeProposal;
import com.pla.grouplife.proposal.domain.model.GroupLifeProposalStatusAudit;
import com.pla.grouplife.proposal.presentation.dto.GLProposalApproverCommentDto;
import com.pla.grouplife.proposal.presentation.dto.GLProposalDto;
import com.pla.grouplife.proposal.presentation.dto.GLProposalMandatoryDocumentDto;
import com.pla.grouplife.proposal.presentation.dto.SearchGLProposalDto;
import com.pla.grouplife.proposal.query.GLProposalFinder;
import com.pla.grouplife.proposal.repository.GLProposalStatusAuditRepository;
import com.pla.grouplife.proposal.repository.GlProposalRepository;
import com.pla.grouplife.quotation.presentation.dto.PlanDetailDto;
import com.pla.grouplife.quotation.query.GLQuotationFinder;
import com.pla.grouplife.sharedresource.dto.*;
import com.pla.grouplife.sharedresource.model.vo.*;
import com.pla.grouplife.sharedresource.query.GLFinder;
import com.pla.grouplife.sharedresource.service.GLInsuredExcelGenerator;
import com.pla.grouplife.sharedresource.service.GLInsuredExcelParser;
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
 * Created by Nischitha on 6/24/2015.
 */
@Service
public class GLProposalService {

    private GLFinder glFinder;

    private GLProposalFinder glProposalFinder;

    private GLInsuredExcelGenerator glInsuredExcelGenerator;

    private IPlanAdapter planAdapter;

    private GlProposalRepository groupLifeProposalRepository;

    private GLInsuredExcelParser glInsuredExcelParser;

    private GLQuotationFinder glQuotationFinder;

    @Autowired
    private CommandGateway commandGateway;

    @Autowired
    private GLProposalStatusAuditRepository glProposalStatusAuditRepository;

    @Autowired
    private IUnderWriterAdapter underWriterAdapter;

    @Autowired
    private GridFsTemplate gridFsTemplate;


    @Autowired
    public GLProposalService(GLFinder glFinder, GLProposalFinder glProposalFinder, IPlanAdapter planAdapter,
                             GlProposalRepository groupLifeProposalRepository, GLInsuredExcelParser glInsuredExcelParser, GLInsuredExcelGenerator glInsuredExcelGenerator, GLQuotationFinder glQuotationFinder) {
        this.glFinder = glFinder;
        this.planAdapter = planAdapter;
        this.glProposalFinder = glProposalFinder;
        this.groupLifeProposalRepository = groupLifeProposalRepository;
        this.glInsuredExcelParser = glInsuredExcelParser;
        this.glInsuredExcelGenerator = glInsuredExcelGenerator;
        this.glQuotationFinder = glQuotationFinder;
    }

    public boolean hasProposalForQuotation(String quotationId) {
        Map quotationMap = glFinder.searchQuotationById(new QuotationId(quotationId));
        String quotationNumber = (String) quotationMap.get("quotationNumber");
        Map proposalMap = glProposalFinder.findProposalByQuotationNumber(quotationNumber);
        return proposalMap != null;
    }

    public boolean isAgentActive(String quotationId){
        Map quotationMap = glFinder.searchQuotationById(new QuotationId(quotationId));
        AgentId agentId = (AgentId) quotationMap.get("agentId");
        Map<String, Object> agentCount =  glFinder.getAgentById(agentId.getAgentId());
        return isNotEmpty(agentCount)?true:false;
    }

    public List<GlQuotationDto> searchGeneratedQuotation(String quotationNumber) {
        List<Map> allQuotations = glFinder.searchQuotation(quotationNumber, null, null, null, null, new String[]{"SHARED"});
        if (isEmpty(allQuotations)) {
            return Lists.newArrayList();
        }
        List glQuotationDtoList = allQuotations.stream().map(new TransformToGLQuotationDto()).collect(Collectors.toList());
        return glQuotationDtoList;
    }

    public AgentDetailDto getAgentDetail(String quotationId) {
        Map quotation = glFinder.getQuotationById(quotationId);
        AgentId agentMap = (AgentId) quotation.get("agentId");
        String agentId = agentMap.getAgentId();
        Map<String, Object> agentDetail = glFinder.getAgentById(agentId);
        AgentDetailDto agentDetailDto = new AgentDetailDto();
        agentDetailDto.setAgentId(agentId);
        agentDetailDto.setBranchName((String) agentDetail.get("branchName"));
        agentDetailDto.setTeamName((String) agentDetail.get("teamName"));
        agentDetailDto.setAgentName(agentDetail.get("firstName") + " " + (agentDetail.get("lastName") == null ? "" : (String) agentDetail.get("lastName")));
        agentDetailDto.setAgentMobileNumber(agentDetail.get("mobileNumber") != null ? (String) agentDetail.get("mobileNumber") : "");
        agentDetailDto.setAgentSalutation(agentDetail.get("title") != null ? (String) agentDetail.get("title") : "");
        return agentDetailDto;
    }

    public AgentDetailDto getAgentDetail(ProposalId proposalId) {
        Map proposal = glProposalFinder.findProposalById(proposalId);
        AgentId agentMap = (AgentId) proposal.get("agentId");
        String agentId = agentMap.getAgentId();
        Map<String, Object> agentDetail = glFinder.getAgentById(agentId);
        AgentDetailDto agentDetailDto = new AgentDetailDto();
        agentDetailDto.setAgentId(agentId);
        agentDetailDto.setBranchName((String) agentDetail.get("branchName"));
        agentDetailDto.setTeamName((String) agentDetail.get("teamName"));
        agentDetailDto.setAgentName(agentDetail.get("firstName") + " " + (agentDetail.get("lastName") == null ? "" : (String) agentDetail.get("lastName")));
        agentDetailDto.setAgentMobileNumber(agentDetail.get("mobileNumber") != null ? (String) agentDetail.get("mobileNumber") : "");
        agentDetailDto.setAgentSalutation(agentDetail.get("title") != null ? (String) agentDetail.get("title") : "");
        agentDetailDto.setActive("ACTIVE".equalsIgnoreCase((String) agentDetail.get("agentStatus")));
        return agentDetailDto;
    }

    public ProposerDto getProposerDetail(ProposalId proposalId) {
        Map proposal = glProposalFinder.findProposalById(proposalId);
        Proposer proposer = (Proposer) proposal.get("proposer");
        ProposerDto proposerDto = new ProposerDto(proposer);
        if (proposal.get("opportunityId") != null) {
            OpportunityId opportunityId = (OpportunityId) proposal.get("opportunityId");
            proposerDto.setOpportunityId(opportunityId.getOpportunityId());
        }
        if (proposal.get("industry") != null) {
            Industry industry = (Industry) proposal.get("industry");
            proposerDto.setIndustryId(industry.getIndustryId());
        }
        return proposerDto;
    }

    public List<GLProposalDto> searchProposal(SearchGLProposalDto searchGLProposalDto, String[] statuses) {
        List<Map> allQuotations = glProposalFinder.searchProposal(searchGLProposalDto.getProposalNumber(), searchGLProposalDto.getProposerName(), searchGLProposalDto.getAgentName(), searchGLProposalDto.getAgentCode(), searchGLProposalDto.getProposalId(), statuses);
        if (isEmpty(allQuotations)) {
            return Lists.newArrayList();
        }
        List<GLProposalDto> ghProposalDtoList = allQuotations.stream().map(new Function<Map, GLProposalDto>() {
            @Override
            public GLProposalDto apply(Map map) {
                String proposalId = map.get("_id").toString();
                AgentDetailDto agentDetailDto = getAgentDetail(new ProposalId(proposalId));
                DateTime submittedOn = map.get("submittedOn") != null ? new DateTime(map.get("submittedOn")) : null;
                String proposalStatus = map.get("proposalStatus") != null ? (String) map.get("proposalStatus") : "";
                String proposalNumber = map.get("proposalNumber") != null ? ((ProposalNumber) map.get("proposalNumber")).getProposalNumber() : "";
                Proposer proposerMap = map.get("proposer") != null ? (Proposer) map.get("proposer") : null;
                String proposerName = proposerMap != null ? proposerMap.getProposerName() : "";
                GLProposalDto glProposalDto = new GLProposalDto(proposalId, submittedOn, agentDetailDto.getAgentId(), agentDetailDto.getAgentName(), proposalStatus, proposalNumber, proposerName);
                return glProposalDto;
            }
        }).collect(Collectors.toList());
        return ghProposalDtoList;
    }

    public byte[] getPlanReadyReckoner(String proposalId) throws IOException, JRException {
        AgentDetailDto agentDetailDto = getAgentDetail(new ProposalId(proposalId));
        List<PlanId> planIds = getAgentAuthorizedPlans(agentDetailDto.getAgentId());
        List<PlanDetailDto> planDetailDtoList = PlanDetailDto.transformToPlanDetail(planAdapter.getPlanAndCoverageDetail(planIds));
        byte[] pdfData = PDFGeneratorUtils.createPDFReportByList(planDetailDtoList, "jasperpdf/template/grouplife/planReadyReckoner.jrxml");
        return pdfData;
    }

    private List<PlanId> getAgentAuthorizedPlans(String agentId) {
        List<Map<String, Object>> authorizedPlans = glProposalFinder.getAgentAuthorizedPlan(agentId);
        List<PlanId> planIds = authorizedPlans.stream().map(new Function<Map<String, Object>, PlanId>() {
            @Override
            public PlanId apply(Map<String, Object> authorizePlanMap) {
                String planId = (String) authorizePlanMap.get("planId");
                return new PlanId(planId);
            }
        }).collect(Collectors.toList());
        return planIds;
    }

    public PremiumDetailDto getPremiumDetail(ProposalId proposalId) {
        GroupLifeProposal groupLifeProposal = groupLifeProposalRepository.findOne(proposalId);
        PremiumDetailDto premiumDetailDto = getPremiumDetail(groupLifeProposal);
        return premiumDetailDto;
    }

    public PremiumDetailDto recalculatePremium(GLRecalculatedInsuredPremiumCommand glRecalculatedInsuredPremiumCommand) {
        GroupLifeProposal groupLifeProposal = commandGateway.sendAndWait(glRecalculatedInsuredPremiumCommand);
        PremiumDetailDto premiumDetailDto = getPremiumDetail(groupLifeProposal);
        return premiumDetailDto;
    }

    public HSSFWorkbook getInsuredTemplateExcel(String proposalId) throws IOException {
        AgentDetailDto agentDetailDto = getAgentDetail(new ProposalId(proposalId));
        List<PlanId> planIds = getAgentAuthorizedPlans(agentDetailDto.getAgentId());
        Map quotation = glProposalFinder.findProposalById(new ProposalId(proposalId));
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

    public boolean isValidInsuredTemplate(String proposalId, HSSFWorkbook insuredTemplateWorkbook, boolean samePlanForAllCategory, boolean samePlanForAllRelation) {
        AgentDetailDto agentDetailDto = getAgentDetail(new ProposalId(proposalId));
        List<PlanId> agentPlans = getAgentAuthorizedPlans(agentDetailDto.getAgentId());
        return glInsuredExcelParser.isValidInsuredExcel(insuredTemplateWorkbook, samePlanForAllCategory, samePlanForAllRelation, agentPlans);
    }

    public List<InsuredDto> transformToInsuredDto(HSSFWorkbook insuredTemplateWorkbook, String proposalId, boolean samePlanForAllCategory, boolean samePlanForAllRelation) {
        AgentDetailDto agentDetailDto = getAgentDetail(new ProposalId(proposalId));
        List<PlanId> agentAuthorizedPlans = getAgentAuthorizedPlans(agentDetailDto.getAgentId());
        List<InsuredDto> insuredDtoList = glInsuredExcelParser.transformToInsuredDto(insuredTemplateWorkbook, agentAuthorizedPlans);
        return insuredDtoList;
    }

    private PremiumDetailDto getPremiumDetail(GroupLifeProposal groupLifeProposal) {
        PremiumDetail premiumDetail = groupLifeProposal.getPremiumDetail();
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
        premiumDetailDto = premiumDetailDto.updateWithOptedFrequency(premiumDetail.getOptedFrequencyPremium() != null ? premiumDetail.getOptedFrequencyPremium().getPremiumFrequency() : null);
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

    public List<GLProposalApproverCommentDto> findApproverComments(String proposalId) {
        List<GroupLifeProposalStatusAudit> audits = glProposalStatusAuditRepository.findByProposalId(new ProposalId(proposalId));
        List<GLProposalApproverCommentDto> proposalApproverCommentsDtos = Lists.newArrayList();
        if (isNotEmpty(audits)) {
            proposalApproverCommentsDtos = audits.stream().map(new Function<GroupLifeProposalStatusAudit, GLProposalApproverCommentDto>() {
                @Override
                public GLProposalApproverCommentDto apply(GroupLifeProposalStatusAudit groupLifeProposalStatusAudit) {
                    GLProposalApproverCommentDto proposalApproverCommentsDto = new GLProposalApproverCommentDto();
                    try {
                        BeanUtils.copyProperties(proposalApproverCommentsDto, groupLifeProposalStatusAudit);
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

    public List<GLProposalMandatoryDocumentDto> findMandatoryDocuments(String proposalId) {
        List<GLProposerDocument> uploadedDocuments = getUploadedMandatoryDocument(proposalId);
        Set<ClientDocumentDto> mandatoryDocuments = getMandatoryDocumentRequiredForSubmission(proposalId);
        return getAllMandatoryDocuments(uploadedDocuments,mandatoryDocuments);
    }

    public List<GLProposerDocument> getUploadedMandatoryDocument(String proposalId){
        Map proposal = glProposalFinder.findProposalById(new ProposalId(proposalId));
        List<GLProposerDocument> uploadedDocuments = proposal.get("proposerDocuments") != null ? (List<GLProposerDocument>) proposal.get("proposerDocuments") : Lists.newArrayList();
        return uploadedDocuments;
    }

    public Set<ClientDocumentDto> getMandatoryDocumentRequiredForSubmission(String proposalId){
        Map proposal = glProposalFinder.findProposalById(new ProposalId(proposalId));
        List<Insured> insureds = (List<Insured>) proposal.get("insureds");
        List<SearchDocumentDetailDto> documentDetailDtos = Lists.newArrayList();
        insureds.forEach(ghInsured -> {
            PlanPremiumDetail planPremiumDetail = ghInsured.getPlanPremiumDetail();
            SearchDocumentDetailDto searchDocumentDetailDto = new SearchDocumentDetailDto(planPremiumDetail.getPlanId());
            documentDetailDtos.add(searchDocumentDetailDto);
            if (isNotEmpty(ghInsured.getInsuredDependents())) {
                ghInsured.getInsuredDependents().forEach(insuredDependent -> {
                    PlanPremiumDetail dependentPlanPremiumDetail = insuredDependent.getPlanPremiumDetail();
                    documentDetailDtos.add(new SearchDocumentDetailDto(dependentPlanPremiumDetail.getPlanId()));
                });
            }
        });
        Set<ClientDocumentDto> mandatoryDocuments = underWriterAdapter.getMandatoryDocumentsForApproverApproval(documentDetailDtos, ProcessType.ENROLLMENT);
        return mandatoryDocuments;
    }

    private List<GLProposalMandatoryDocumentDto> getAllMandatoryDocuments(List<GLProposerDocument> uploadedDocuments,Set<ClientDocumentDto> mandatoryDocumentRequiredForSubmission){
        List<GLProposalMandatoryDocumentDto> mandatoryDocumentDtos = Lists.newArrayList();
        if (isNotEmpty(mandatoryDocumentRequiredForSubmission)) {
            mandatoryDocumentDtos = mandatoryDocumentRequiredForSubmission.stream().map(new Function<ClientDocumentDto, GLProposalMandatoryDocumentDto>() {
                @Override
                public GLProposalMandatoryDocumentDto apply(ClientDocumentDto clientDocumentDto) {
                    GLProposalMandatoryDocumentDto mandatoryDocumentDto = new GLProposalMandatoryDocumentDto(clientDocumentDto.getDocumentCode(), clientDocumentDto.getDocumentName());
                    Optional<GLProposerDocument> proposerDocumentOptional = uploadedDocuments.stream().filter(new Predicate<GLProposerDocument>() {
                        @Override
                        public boolean test(GLProposerDocument glProposerDocument) {
                            return clientDocumentDto.getDocumentCode().equals(glProposerDocument.getDocumentId());
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
            }).collect(Collectors.toList());
        }
        return mandatoryDocumentDtos;
    }

    public Set<GLProposalMandatoryDocumentDto> findAdditionalDocuments(String proposalId) {
        Map proposal = glProposalFinder.findProposalById(new ProposalId(proposalId));
        List<GLProposerDocument> uploadedDocuments = proposal.get("proposerDocuments") != null ? (List<GLProposerDocument>) proposal.get("proposerDocuments") : Lists.newArrayList();
        Set<GLProposalMandatoryDocumentDto> mandatoryDocumentDtos = Sets.newHashSet();
        if (isNotEmpty(uploadedDocuments)) {
            mandatoryDocumentDtos = uploadedDocuments.stream().filter(uploadedDocument -> !uploadedDocument.isMandatory()).map(new Function<GLProposerDocument, GLProposalMandatoryDocumentDto>() {
                @Override
                public GLProposalMandatoryDocumentDto apply(GLProposerDocument glProposerDocument) {
                    GLProposalMandatoryDocumentDto mandatoryDocumentDto = new GLProposalMandatoryDocumentDto(glProposerDocument.getDocumentId(), glProposerDocument.getDocumentName());
                    GridFSDBFile gridFSDBFile = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(glProposerDocument.getGridFsDocId())));
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

        public GlQuotationDto apply(Map map) {
            String quotationId = map.get("_id").toString();
            AgentDetailDto agentDetailDto = getAgentDetail(new QuotationId(quotationId));
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
