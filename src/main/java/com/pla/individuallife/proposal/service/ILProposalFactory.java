package com.pla.individuallife.proposal.service;

import com.pla.core.domain.model.agent.AgentId;
import com.pla.core.query.PlanFinder;
import com.pla.individuallife.proposal.application.command.ILCreateProposalCommand;
import com.pla.individuallife.proposal.domain.model.ILProposalAggregate;
import com.pla.individuallife.proposal.domain.service.ProposalNumberGenerator;
import com.pla.individuallife.proposal.presentation.dto.AgentDetailDto;
import com.pla.individuallife.sharedresource.dto.PlanDetailDto;
import com.pla.individuallife.sharedresource.dto.RiderDetailDto;
import com.pla.individuallife.sharedresource.dto.ILQuotationDto;
import com.pla.individuallife.quotation.query.ILQuotationFinder;
import com.pla.individuallife.sharedresource.dto.ProposedAssuredDto;
import com.pla.individuallife.sharedresource.model.vo.*;
import com.pla.sharedkernel.identifier.PlanId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by Admin on 7/29/2015.
 */
@Component
public class ILProposalFactory {


    @Autowired
    private ILQuotationFinder quotationFinder;

    @Autowired
    private PlanFinder planFinder;

    @Autowired
    private ProposalNumberGenerator proposalNumberGenerator;

    private static final Logger logger = LoggerFactory.getLogger(ILProposalFactory.class);

    public ILProposalAggregate createProposal(ILCreateProposalCommand cmd){
        AgentCommissionShareModel agentCommissionShareModel = withAgentCommissionShareModel(cmd.getAgentCommissionDetails());
        ProposedAssured proposedAssured =   null;
        Proposer proposer = null;
        ILProposalAggregate aggregate;
        String proposalNumber = proposalNumberGenerator.getProposalNumber();
        if (cmd.getQuotationId() != null) {
            ILQuotationDto dto = quotationFinder.getQuotationById(cmd.getQuotationId());
            ProposedAssuredDto proposedAssuredDto  = cmd.getProposedAssured();
            ProposedAssuredDto proposedAssuredDtoQuotation  = dto.getProposedAssured();
            proposedAssuredDtoQuotation.setEmployment(proposedAssuredDto.getEmployment());
            proposedAssuredDtoQuotation.setResidentialAddress(proposedAssuredDto.getResidentialAddress());
            proposedAssuredDtoQuotation.setMaritalStatus(proposedAssuredDto.getMaritalStatus());
            proposedAssuredDtoQuotation.setOtherName(proposedAssuredDto.getOtherName());
            proposedAssuredDtoQuotation.setSpouse(proposedAssuredDto.getSpouse());
            proposedAssured =  withProposedAssure(proposedAssuredDtoQuotation);
            if (dto.isAssuredTheProposer()) {
                proposer = ProposerBuilder.getProposerBuilder(dto.getProposedAssured()).createProposer();
            }
            Map plan = planFinder.findPlanByPlanId(new PlanId(dto.getPlanId()));
            Map planDetail = (HashMap) plan.get("planDetail");
            int minAge = (int) planDetail.get("minEntryAge");
            int maxAge = (int) planDetail.get("maxEntryAge");
            dto.setPlanDetail(planDetail);
            ProposalPlanDetail proposalPlanDetail = withProposalPlanDetail(dto.getPlanDetailDto());
            aggregate = new ILProposalAggregate(cmd.getProposalId(), proposalNumber, proposedAssured, agentCommissionShareModel,proposer, dto.getQuotationNumber(),dto.getVersionNumber(),
                    dto.getQuotationId().getQuotationId(),proposalPlanDetail, minAge,maxAge);
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug(" ProposedAssured :: " + proposedAssured);
            }
            agentCommissionShareModel = withAgentCommissionShareModel(cmd.getAgentCommissionDetails());
            proposedAssured =  withProposedAssure(cmd.getProposedAssured());
            aggregate = new ILProposalAggregate(cmd.getProposalId(), proposalNumber, proposedAssured,agentCommissionShareModel);
        }
        return aggregate;
    }

    private AgentCommissionShareModel withAgentCommissionShareModel(Set<AgentDetailDto> agentCommissionDetails){
        AgentCommissionShareModel agentCommissionShareModel = new AgentCommissionShareModel();
        agentCommissionDetails.forEach(agentCommission -> agentCommissionShareModel.addAgentCommission(new AgentId(agentCommission.getAgentId()), agentCommission.getCommission()));
        return agentCommissionShareModel;
    }


    private ProposedAssured withProposedAssure(ProposedAssuredDto proposedAssure){
        return  ProposedAssuredBuilder.getProposedAssuredBuilder(proposedAssure).createProposedAssured();
    }

    private ProposalPlanDetail withProposalPlanDetail(PlanDetailDto proposalPlanDetail){
        Set<RiderDetailDto> riderDetail = proposalPlanDetail.getRiderDetails();
        Set<ILRiderDetail> riderDetails =  riderDetail.parallelStream().map(new Function<RiderDetailDto, ILRiderDetail>() {
            @Override
            public ILRiderDetail apply(RiderDetailDto riderDetailDto) {
                return new ILRiderDetail(riderDetailDto.getCoverageId(),riderDetailDto.getSumAssured(),riderDetailDto.getCoverTerm(),riderDetailDto.getWaiverOfPremium(),"");
            }
        }).collect(Collectors.toSet());
        ProposalPlanDetail planDetail = new ProposalPlanDetail(proposalPlanDetail.getPlanId(),"",proposalPlanDetail.getPolicyTerm(),proposalPlanDetail.getPremiumPaymentTerm(),proposalPlanDetail.getSumAssured(),riderDetails);
        return planDetail;
    }

}
