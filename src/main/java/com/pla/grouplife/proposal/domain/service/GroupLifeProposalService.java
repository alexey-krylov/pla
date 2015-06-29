package com.pla.grouplife.proposal.domain.service;

import com.google.common.collect.Lists;
import com.pla.core.domain.model.agent.AgentId;
import com.pla.grouplife.proposal.domain.model.GLProposalProcessor;
import com.pla.grouplife.proposal.domain.model.GLProposalStatus;
import com.pla.grouplife.proposal.domain.model.GroupLifeProposal;
import com.pla.grouplife.proposal.presentation.dto.GLProposalDto;
import com.pla.grouplife.proposal.presentation.dto.SearchGLProposalDto;
import com.pla.grouplife.proposal.query.GLProposalFinder;
import com.pla.grouplife.quotation.application.command.GLRecalculatedInsuredPremiumCommand;
import com.pla.grouplife.quotation.domain.model.GroupLifeQuotation;
import com.pla.grouplife.quotation.domain.service.AgentIsActive;
import com.pla.grouplife.quotation.presentation.dto.PlanDetailDto;
import com.pla.grouplife.sharedresource.dto.*;
import com.pla.grouplife.sharedresource.model.vo.Proposer;
import com.pla.grouplife.sharedresource.query.GLFinder;
import com.pla.publishedlanguage.contract.IPlanAdapter;
import com.pla.sharedkernel.identifier.PlanId;
import com.pla.sharedkernel.identifier.ProposalId;
import com.pla.sharedkernel.identifier.QuotationId;
import com.pla.sharedkernel.util.PDFGeneratorUtils;
import net.sf.jasperreports.engine.JRException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.axonframework.repository.Repository;
import org.bson.types.ObjectId;
import org.joda.time.LocalDate;
import org.nthdimenzion.ddd.domain.annotations.DomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.nthdimenzion.presentation.AppUtils.getIntervalInDays;
import static org.nthdimenzion.utils.UtilValidator.isEmpty;

/**
 * Created by Samir on 4/8/2015.
 */
@DomainService
public class GroupLifeProposalService {

    private GLFinder glFinder;

    private GLProposalFinder glProposalFinder;

    private IPlanAdapter planAdapter;

    private AgentIsActive agentIsActive;

    private Repository<GroupLifeQuotation> glQuotationMongoRepository;

    private GLProposalNumberGenerator glProposalNumberGenerator;

    private ProposalRoleAdapter proposalRoleAdapter;

    @Autowired
    public GroupLifeProposalService(GLFinder glFinder, GLProposalFinder glProposalFinder, IPlanAdapter planAdapter, AgentIsActive agentIsActive,
                             Repository<GroupLifeQuotation> glQuotationMongoRepository, GLProposalNumberGenerator glProposalNumberGenerator) {
        this.glFinder = glFinder;
        this.planAdapter = planAdapter;
        this.glProposalFinder = glProposalFinder;
        this.agentIsActive = agentIsActive;
        this.glQuotationMongoRepository = glQuotationMongoRepository;
        this.glProposalNumberGenerator = glProposalNumberGenerator;

    }

    //TODO implement
    public boolean hasProposalForQuotation(String quotationId) {
        return false;
    }

    public List<GlQuotationDto> searchGeneratedQuotation(String quotationNumber) {
        List<Map> allQuotations = glFinder.searchGeneratedQuotation(quotationNumber);
        if (isEmpty(allQuotations)) {
            return Lists.newArrayList();
        }
        List glQuotationDtoList = allQuotations.stream().map(new TransformToGLProposalDto()).collect(Collectors.toList());
        return glQuotationDtoList;
    }

    public GroupLifeProposal createProposal(String quotationId, UserDetails userDetails) {
        GLProposalProcessor glProposalProcessor = proposalRoleAdapter.userToProposalProcessor(userDetails);
          /* AgentSpecification shared
           if (!agentIsActive.isSatisfiedBy(new AgentId(agentId))) {
                raiseAgentIsInactiveException();
            }
            Repository common
            */
        GroupLifeQuotation groupLifeQuotation = glQuotationMongoRepository.load(new QuotationId(quotationId));
        ProposalId proposalId = new ProposalId(new ObjectId().toString());
        String proposalNumber = glProposalNumberGenerator.getProposalNumber("6", "1", GroupLifeQuotation.class, LocalDate.now());
        return GroupLifeProposal.createGroupLifeProposal(proposalId,groupLifeQuotation.getQuotationId(),proposalNumber,
                groupLifeQuotation.getAgentId(),groupLifeQuotation.getProposer(),groupLifeQuotation.getInsureds(),groupLifeQuotation.getPremiumDetail(),
                GLProposalStatus.PENDING_FIRST_PREMIUM);

    }

    public AgentDetailDto getAgentDetail(ProposalId proposalId) {
        return null;
    }

    public AgentDetailDto getAgentDetail(QuotationId quotationId) {
        Map quotation = glFinder.getQuotationById(quotationId.getQuotationId());
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


    public List<GLProposalDto> searchProposal(SearchGLProposalDto searchGLProposalDto) {
        return null;
    }

    public byte[] getPlanReadyReckoner(String proposalId) throws IOException, JRException {
        AgentDetailDto agentDetailDto = getAgentDetail(new ProposalId(proposalId));
        List<PlanId> planIds = getAgentAuthorizedPlans(agentDetailDto.getAgentId());
        List<PlanDetailDto> planDetailDtoList = PlanDetailDto.transformToPlanDetail(planAdapter.getPlanAndCoverageDetail(planIds));
        byte[] pdfData = PDFGeneratorUtils.createPDFReportByList(planDetailDtoList, "jasperpdf/template/grouplife/quotation/planReadyReckoner.jrxml");
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

    public ProposerDto getProposerDetail(ProposalId proposalId) {
        return null;
    }

    public PremiumDetailDto getPremiumDetail(QuotationId quotationId) {
        return null;
    }

    public PremiumDetailDto recalculatePremium(GLRecalculatedInsuredPremiumCommand glRecalculatedInsuredPremiumCommand) {
        return null;
    }

    public HSSFWorkbook getInsuredTemplateExcel(String proposalId) {
        return null;
    }

    public boolean isValidInsuredTemplate(String proposalId, HSSFWorkbook insuredTemplateWorkbook, boolean samePlanForAllCategory, boolean samePlanForAllRelation) {
        return false;
    }

    public List<InsuredDto> transformToInsuredDto(HSSFWorkbook insuredTemplateWorkbook, String proposalId, boolean samePlanForAllCategory, boolean samePlanForAllRelation) {
        return null;
    }


    private class TransformToGLProposalDto implements Function<Map, GLProposalDto> {

        @Override
        public GLProposalDto apply(Map map) {
            String quotationId = map.get("_id").toString();
            AgentDetailDto agentDetailDto = getAgentDetail(new QuotationId(quotationId));
            LocalDate generatedOn = map.get("generatedOn") != null ? new LocalDate((Date) map.get("generatedOn")) : null;
            String quotationStatus = map.get("quotationStatus") != null ? (String) map.get("quotationStatus") : "";
            String quotationNumber = map.get("quotationNumber") != null ? (String) map.get("quotationNumber") : "";
            ObjectId parentQuotationIdMap = map.get("parentQuotationId") != null ? (ObjectId) map.get("parentQuotationId") : null;
            Proposer proposerMap = map.get("proposer") != null ? (Proposer) map.get("proposer") : null;
            String proposerName = proposerMap != null ? proposerMap.getProposerName() : "";
            String parentQuotationId = parentQuotationIdMap != null ? parentQuotationIdMap.toString() : "";
            GLProposalDto glQuotationDto = new GLProposalDto(new QuotationId(quotationId), (Integer) map.get("versionNumber"), generatedOn, agentDetailDto.getAgentId(), agentDetailDto.getAgentName(), new QuotationId(parentQuotationId), quotationStatus, quotationNumber, proposerName, getIntervalInDays(generatedOn));
            return glQuotationDto;
        }
    }
}
