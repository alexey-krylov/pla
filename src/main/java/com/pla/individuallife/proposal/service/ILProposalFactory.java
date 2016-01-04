package com.pla.individuallife.proposal.service;

import com.pla.core.domain.model.agent.AgentId;
import com.pla.core.query.PlanFinder;
import com.pla.individuallife.proposal.application.command.ILCreateProposalCommand;
import com.pla.individuallife.proposal.domain.model.ILProposalAggregate;
import com.pla.individuallife.proposal.domain.service.ProposalNumberGenerator;
import com.pla.individuallife.quotation.query.ILQuotationFinder;
import com.pla.individuallife.sharedresource.dto.*;
import com.pla.individuallife.sharedresource.model.vo.*;
import com.pla.sharedkernel.identifier.OpportunityId;
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


    private static final Logger logger = LoggerFactory.getLogger(ILProposalFactory.class);
    @Autowired
    private ILQuotationFinder quotationFinder;
    @Autowired
    private PlanFinder planFinder;
    @Autowired
    private ProposalNumberGenerator proposalNumberGenerator;

    public ILProposalAggregate createProposal(ILCreateProposalCommand cmd){
        AgentCommissionShareModel agentCommissionShareModel = withAgentCommissionShareModel(cmd.getAgentCommissionDetails());
        ProposedAssured proposedAssured =   null;
        Proposer proposer = null;
        ILProposalAggregate aggregate;
        String proposalNumber = proposalNumberGenerator.getProposalNumber();
        if (cmd.getQuotationId() != null) {
            ILQuotationDto dto = quotationFinder.getQuotationById(cmd.getQuotationId());
            ProposerDto proposerDto  = cmd.getProposer();
            ProposerDto quotationProposerDto  = dto.getProposer();
            quotationProposerDto.setEmployment(proposerDto.getEmployment());
            quotationProposerDto.setResidentialAddress(proposerDto.getResidentialAddress());
            quotationProposerDto.setMaritalStatus(proposerDto.getMaritalStatus());
            quotationProposerDto.setOtherName(proposerDto.getOtherName());
            quotationProposerDto.setSpouse(proposerDto.getSpouse());
            quotationProposerDto.setIsProposedAssured(cmd.getProposer().getIsProposedAssured());
            proposer = ProposerBuilder.getProposerBuilder(quotationProposerDto).createProposer();
            ProposedAssuredDto proposedAssuredDto = dto.getProposedAssured();

           /*
           * Given empty object as it routed from quotation to proposal...
           * */
            Address address  = new Address("","","","","");
            EmploymentDetail employmentDetail = new EmploymentDetail("",proposedAssuredDto.getOccupation(),"",null,"","",address);
            proposedAssured = new ProposedAssured(proposedAssuredDto.getTitle(),proposedAssuredDto.getFirstName(),proposedAssuredDto.getSurname(),proposedAssuredDto.getNrc(),proposedAssuredDto.getDateOfBirth(),
                    proposedAssuredDto.getGender(),proposedAssuredDto.getMobileNumber(),proposedAssuredDto.getEmailAddress(),proposedAssuredDto.getMaritalStatus(),proposedAssuredDto.getOccupation(),null,null,null,employmentDetail,null,proposedAssuredDto.getOtherName(),proposedAssuredDto.getRelationshipId(),proposedAssuredDto.getClientId());
            if (dto.isAssuredTheProposer()) {
                proposedAssured =  withProposedAssure(quotationProposerDto);
            }
            Map plan = planFinder.findPlanByPlanId(new PlanId(dto.getPlanId()));
            Map planDetail = (HashMap) plan.get("planDetail");
            int minAge = (int) planDetail.get("minEntryAge");
            int maxAge = (int) planDetail.get("maxEntryAge");
            dto.setPlanDetail(planDetail);
            ProposalPlanDetail proposalPlanDetail = withProposalPlanDetail(dto.getPlanDetailDto());
            aggregate = new ILProposalAggregate(cmd.getProposalId(), proposalNumber, proposedAssured, agentCommissionShareModel,proposer, dto.getQuotationNumber(),dto.getVersionNumber(),
                    dto.getQuotationId().getQuotationId(),proposalPlanDetail, minAge,maxAge,new OpportunityId(dto.getOpportunityId()));
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug(" ProposedAssured :: " + proposedAssured);
            }
            agentCommissionShareModel = withAgentCommissionShareModel(cmd.getAgentCommissionDetails());
            proposer =  withProposer(cmd.getProposer());
            aggregate = new ILProposalAggregate(cmd.getProposalId(), proposalNumber, proposer,agentCommissionShareModel,cmd.getPlanDetail());
        }
        return aggregate;
    }

    private AgentCommissionShareModel withAgentCommissionShareModel(Set<AgentDetailDto> agentCommissionDetails){
        AgentCommissionShareModel agentCommissionShareModel = new AgentCommissionShareModel();
        agentCommissionDetails.forEach(agentCommission -> agentCommissionShareModel.addAgentCommission(new AgentId(agentCommission.getAgentId()), agentCommission.getCommission()));
        return agentCommissionShareModel;
    }


    private ProposedAssured withProposedAssure(ProposerDto proposerDto){
        return  ProposedAssuredBuilder.getProposedAssuredBuilder(proposerDto).createProposedAssured();
    }

    private Proposer withProposer(ProposerDto proposedAssure){
        return  ProposerBuilder.getProposerBuilder(proposedAssure).createProposer();
    }

    private ProposalPlanDetail withProposalPlanDetail(PlanDetailDto proposalPlanDetail){
        Set<RiderDetailDto> riderDetail = proposalPlanDetail.getRiderDetails();
        Set<ILRiderDetail> riderDetails =  riderDetail.parallelStream().map(new Function<RiderDetailDto, ILRiderDetail>() {
            @Override
            public ILRiderDetail apply(RiderDetailDto riderDetailDto) {
                return new ILRiderDetail(riderDetailDto.getCoverageId(),riderDetailDto.getSumAssured(),riderDetailDto.getCoverTerm(),riderDetailDto.getWaiverOfPremium(),"");
            }
        }).collect(Collectors.toSet());
        ProposalPlanDetail planDetail = new ProposalPlanDetail(proposalPlanDetail.getPlanId(),"",proposalPlanDetail.getPolicyTerm(), proposalPlanDetail.getPremiumPaymentType(), proposalPlanDetail.getPremiumPaymentTerm(),proposalPlanDetail.getSumAssured(),riderDetails,
                proposalPlanDetail.getAnnualFee(),proposalPlanDetail.getSemiAnnualFee(),proposalPlanDetail.getQuarterlyFee(),proposalPlanDetail.getMonthlyFee());
        return planDetail;
    }

}
