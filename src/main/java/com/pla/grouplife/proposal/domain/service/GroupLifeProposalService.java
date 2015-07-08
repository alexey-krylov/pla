/**
 * Created by User on 6/30/2015.
 */

package com.pla.grouplife.proposal.domain.service;

import com.google.common.collect.Lists;
import com.pla.core.domain.model.agent.AgentId;
import com.pla.grouplife.proposal.application.command.GLRecalculatedInsuredPremiumCommand;
import com.pla.grouplife.proposal.domain.model.GLProposalProcessor;
import com.pla.grouplife.proposal.domain.model.GroupLifeProposal;
import com.pla.grouplife.proposal.presentation.dto.GLProposalDto;
import com.pla.grouplife.proposal.presentation.dto.SearchGLProposalDto;
import com.pla.grouplife.proposal.query.GLProposalFinder;
import com.pla.grouplife.quotation.domain.service.AgentIsActive;
import com.pla.grouplife.sharedresource.dto.*;
import com.pla.grouplife.sharedresource.model.vo.*;
import com.pla.grouplife.sharedresource.query.GLFinder;
import com.pla.publishedlanguage.contract.IPlanAdapter;
import com.pla.publishedlanguage.contract.IPremiumCalculator;
import com.pla.publishedlanguage.domain.model.BasicPremiumDto;
import com.pla.publishedlanguage.domain.model.ComputedPremiumDto;
import com.pla.publishedlanguage.domain.model.PremiumFrequency;
import com.pla.sharedkernel.identifier.PlanId;
import com.pla.sharedkernel.identifier.ProposalId;
import com.pla.sharedkernel.identifier.QuotationId;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.bson.types.ObjectId;
import org.joda.time.LocalDate;
import org.nthdimenzion.common.AppConstants;
import org.nthdimenzion.ddd.domain.annotations.DomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.pla.grouplife.proposal.domain.exception.ProposalException.raiseAgentIsInactiveException;
import static org.nthdimenzion.presentation.AppUtils.getIntervalInDays;
import static org.nthdimenzion.utils.UtilValidator.isEmpty;

@DomainService
public class GroupLifeProposalService {

    private GLFinder glFinder;

    private GLProposalFinder glProposalFinder;

    private IPlanAdapter planAdapter;

    private AgentIsActive agentIsActive;

    private IPremiumCalculator premiumCalculator;


    private GroupLifeProposalRoleAdapter groupLifeProposalRoleAdapter;

    @Autowired
    public GroupLifeProposalService(GLFinder glFinder, GLProposalFinder glProposalFinder, IPlanAdapter planAdapter, AgentIsActive agentIsActive,
                                    GroupLifeProposalRoleAdapter groupLifeProposalRoleAdapter) {
        this.glFinder = glFinder;
        this.planAdapter = planAdapter;
        this.glProposalFinder = glProposalFinder;
        this.agentIsActive = agentIsActive;
        this.groupLifeProposalRoleAdapter = groupLifeProposalRoleAdapter;

    }

    public List<GlQuotationDto> searchGeneratedQuotation(String quotationNumber) {
        List<Map> allQuotations = glFinder.searchGeneratedQuotation(quotationNumber);
        if (isEmpty(allQuotations)) {
            return Lists.newArrayList();
        }
        List glQuotationDtoList = allQuotations.stream().map(new TransformToGLProposalDto()).collect(Collectors.toList());
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
        return agentDetailDto;
    }
    public GroupLifeProposal updateWithAgent(GroupLifeProposal groupLifeProposal, String agentId, UserDetails userDetails) {
        if (!agentIsActive.isSatisfiedBy(groupLifeProposal.getAgentId())) {
            raiseAgentIsInactiveException();
        }
        GLProposalProcessor glProposalProcessor = groupLifeProposalRoleAdapter.userToProposalProcessor(userDetails);
        return glProposalProcessor.updateWithAgentId(groupLifeProposal, new AgentId(agentId));
    }
    public GroupLifeProposal updateWithProposerDetail(GroupLifeProposal groupLifeProposal, ProposerDto proposerDto, UserDetails userDetails) {
        GLProposalProcessor glProposalProcessor = groupLifeProposalRoleAdapter.userToProposalProcessor(userDetails);
        ProposerBuilder proposerBuilder = Proposer.getProposerBuilder(proposerDto.getProposerName(), proposerDto.getProposerCode());
        proposerBuilder.withContactDetail(proposerDto.getAddressLine1(), proposerDto.getAddressLine2(), proposerDto.getPostalCode(), proposerDto.getProvince(), proposerDto.getTown(), proposerDto.getEmailAddress())
                .withContactPersonDetail(proposerDto.getContactPersonName(), proposerDto.getContactPersonEmail(), proposerDto.getContactPersonMobileNumber(), proposerDto.getContactPersonWorkPhoneNumber());
        groupLifeProposal = glProposalProcessor.updateWithProposer(groupLifeProposal, proposerBuilder.build());

        return groupLifeProposal;
    }


    public GroupLifeProposal updateInsured(GroupLifeProposal groupLifeProposal, Set<Insured> insureds, UserDetails userDetails) {
        if (!agentIsActive.isSatisfiedBy(groupLifeProposal.getAgentId())) {
            raiseAgentIsInactiveException();
        }
        GLProposalProcessor glProposalProcessor = groupLifeProposalRoleAdapter.userToProposalProcessor(userDetails);
        return glProposalProcessor.updateWithInsured(groupLifeProposal, insureds);
    }


    public GroupLifeProposal updateWithPremiumDetail(GroupLifeProposal groupLifeProposal, PremiumDetailDto premiumDetailDto, UserDetails userDetails) {
        if (!agentIsActive.isSatisfiedBy(groupLifeProposal.getAgentId())) {
            raiseAgentIsInactiveException();
        }
        GLProposalProcessor glProposalProcessor = groupLifeProposalRoleAdapter.userToProposalProcessor(userDetails);
        PremiumDetail premiumDetail = new PremiumDetail(premiumDetailDto.getAddOnBenefit(), premiumDetailDto.getProfitAndSolvencyLoading(), premiumDetailDto.getHivDiscount(), premiumDetailDto.getValuedClientDiscount(), premiumDetailDto.getLongTermDiscount(), premiumDetailDto.getPolicyTermValue());
        premiumDetail = premiumDetail.updateWithNetPremium(groupLifeProposal.getNetAnnualPremiumPaymentAmount(premiumDetail));
        if (premiumDetailDto.getPolicyTermValue() != null && premiumDetailDto.getPolicyTermValue() == 365) {
            List<ComputedPremiumDto> computedPremiumDtoList = premiumCalculator.calculateModalPremium(new BasicPremiumDto(PremiumFrequency.ANNUALLY, premiumDetail.getNetTotalPremium()));
            Set<Policy> policies = computedPremiumDtoList.stream().map(new Function<ComputedPremiumDto, Policy>() {
                @Override
                public Policy apply(ComputedPremiumDto computedPremiumDto) {
                    return new Policy(computedPremiumDto.getPremiumFrequency(), computedPremiumDto.getPremium().setScale(AppConstants.scale, AppConstants.roundingMode));
                }
            }).collect(Collectors.toSet());
            premiumDetail = premiumDetail.addPolicies(policies);
            premiumDetail = premiumDetail.nullifyPremiumInstallment();
        } else if (premiumDetailDto.getPolicyTermValue() != null && premiumDetailDto.getPolicyTermValue() > 0 && premiumDetailDto.getPolicyTermValue() != 365) {
            int noOfInstallment = premiumDetailDto.getPolicyTermValue() / 30;
            if ((premiumDetailDto.getPolicyTermValue() % 30) == 0) {
                noOfInstallment = noOfInstallment - 1;
            }
            for (int count = 1; count <= noOfInstallment; count++) {
                BigDecimal installmentAmount = premiumDetail.getNetTotalPremium().divide(new BigDecimal(count), 2, BigDecimal.ROUND_CEILING);
                premiumDetail = premiumDetail.addInstallments(count, installmentAmount);
            }
            if (premiumDetailDto.getPremiumInstallment() != null) {
                premiumDetail = premiumDetail.addChoosenPremiumInstallment(premiumDetailDto.getPremiumInstallment().getInstallmentNo(), premiumDetailDto.getPremiumInstallment().getInstallmentAmount());
            }
            premiumDetail = premiumDetail.nullifyFrequencyPremium();
        }
        groupLifeProposal = glProposalProcessor.updateWithPremiumDetail(groupLifeProposal, premiumDetail);
        return groupLifeProposal;
    }
    public List<GLProposalDto> searchProposal(SearchGLProposalDto searchGLProposalDto) {
        return null;
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
            AgentDetailDto agentDetailDto = getAgentDetail(quotationId);
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

