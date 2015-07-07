package com.pla.grouphealth.proposal.domain.service;

import com.pla.core.domain.model.agent.AgentId;
import com.pla.grouphealth.proposal.domain.model.GHProposalProcessor;
import com.pla.grouphealth.proposal.domain.model.GroupHealthProposal;
import com.pla.grouphealth.sharedresource.dto.GHPremiumDetailDto;
import com.pla.grouphealth.sharedresource.dto.ProposerDto;
import com.pla.grouphealth.sharedresource.model.vo.*;
import com.pla.grouphealth.sharedresource.service.AgentIsActive;
import com.pla.publishedlanguage.contract.IPremiumCalculator;
import com.pla.publishedlanguage.domain.model.BasicPremiumDto;
import com.pla.publishedlanguage.domain.model.ComputedPremiumDto;
import com.pla.publishedlanguage.domain.model.PremiumFrequency;
import com.pla.sharedkernel.identifier.OpportunityId;
import org.nthdimenzion.common.AppConstants;
import org.nthdimenzion.ddd.domain.annotations.DomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.pla.grouphealth.proposal.domain.exception.GHProposalException.raiseAgentIsInactiveException;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Created by Samir on 6/25/2015.
 */
@DomainService
public class GroupHealthProposalService {

    @Qualifier(value = "ghAgentIsActive")
    private AgentIsActive agentIsActive;

    private GHProposalRoleAdapter ghProposalRoleAdapter;

    private IPremiumCalculator premiumCalculator;

    @Autowired
    public GroupHealthProposalService(AgentIsActive agentIsActive, GHProposalRoleAdapter ghProposalRoleAdapter, IPremiumCalculator premiumCalculator) {
        this.agentIsActive = agentIsActive;
        this.ghProposalRoleAdapter = ghProposalRoleAdapter;
        this.premiumCalculator = premiumCalculator;
    }

    public GroupHealthProposal updateWithAgent(GroupHealthProposal groupHealthProposal, String agentId, UserDetails userDetails) {
        if (!agentIsActive.isSatisfiedBy(groupHealthProposal.getAgentId())) {
            raiseAgentIsInactiveException();
        }
        GHProposalProcessor ghProposalProcessor = ghProposalRoleAdapter.userToProposalProcessor(userDetails);
        return ghProposalProcessor.updateWithAgentId(groupHealthProposal, new AgentId(agentId));
    }

    public GroupHealthProposal updateWithProposer(GroupHealthProposal groupHealthProposal, ProposerDto proposerDto, UserDetails userDetails) {
        GHProposalProcessor ghProposalProcessor = ghProposalRoleAdapter.userToProposalProcessor(userDetails);
        if (!agentIsActive.isSatisfiedBy(groupHealthProposal.getAgentId())) {
            raiseAgentIsInactiveException();
        }
        GHProposerBuilder proposerBuilder = GHProposer.getProposerBuilder(proposerDto.getProposerName(), proposerDto.getProposerCode());
        proposerBuilder.withContactDetail(proposerDto.getAddressLine1(), proposerDto.getAddressLine2(), proposerDto.getPostalCode(), proposerDto.getProvince(), proposerDto.getTown(), proposerDto.getEmailAddress())
                .withContactPersonDetail(proposerDto.getContactPersonName(), proposerDto.getContactPersonEmail(), proposerDto.getContactPersonMobileNumber(), proposerDto.getContactPersonWorkPhoneNumber());
        groupHealthProposal = ghProposalProcessor.updateWithProposer(groupHealthProposal, proposerBuilder.build());
        if (isNotEmpty(proposerDto.getOpportunityId())) {
            OpportunityId opportunityId = new OpportunityId(proposerDto.getOpportunityId());
            groupHealthProposal = groupHealthProposal.updateWithOpportunityId(opportunityId);
        }
        return groupHealthProposal;
    }

    public GroupHealthProposal updateInsured(GroupHealthProposal groupHealthProposal, Set<GHInsured> insureds, UserDetails userDetails) {
        if (!agentIsActive.isSatisfiedBy(groupHealthProposal.getAgentId())) {
            raiseAgentIsInactiveException();
        }
        GHProposalProcessor ghProposalProcessor = ghProposalRoleAdapter.userToProposalProcessor(userDetails);
        return ghProposalProcessor.updateWithInsured(groupHealthProposal, insureds);
    }

    public GroupHealthProposal updateWithPremiumDetail(GroupHealthProposal groupHealthProposal, GHPremiumDetailDto premiumDetailDto, UserDetails userDetails) {
        if (!agentIsActive.isSatisfiedBy(groupHealthProposal.getAgentId())) {
            raiseAgentIsInactiveException();
        }
        GHProposalProcessor ghProposalProcessor = ghProposalRoleAdapter.userToProposalProcessor(userDetails);
        GHPremiumDetail premiumDetail = new GHPremiumDetail(premiumDetailDto.getAddOnBenefit(), premiumDetailDto.getProfitAndSolvencyLoading(),
                premiumDetailDto.getDiscounts(), premiumDetailDto.getWaiverOfExcessLoading(), premiumDetailDto.getVat(), premiumDetailDto.getPolicyTermValue());
        premiumDetail = premiumDetail.updateWithNetPremium(groupHealthProposal.getNetAnnualPremiumPaymentAmount(premiumDetail));
        if (premiumDetailDto.getPolicyTermValue() != null && premiumDetailDto.getPolicyTermValue() == 365) {
            List<ComputedPremiumDto> computedPremiumDtoList = premiumCalculator.calculateModalPremium(new BasicPremiumDto(PremiumFrequency.ANNUALLY, premiumDetail.getNetTotalPremium()));
            Set<GHFrequencyPremium> policies = computedPremiumDtoList.stream().map(new Function<ComputedPremiumDto, GHFrequencyPremium>() {
                @Override
                public GHFrequencyPremium apply(ComputedPremiumDto computedPremiumDto) {
                    return new GHFrequencyPremium(computedPremiumDto.getPremiumFrequency(), computedPremiumDto.getPremium().setScale(AppConstants.scale, AppConstants.roundingMode));
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
        premiumDetail = premiumDetail.updateWithOptedFrequencyPremium(premiumDetailDto.getOptedPremiumFrequency());
        groupHealthProposal = ghProposalProcessor.updateWithPremiumDetail(groupHealthProposal, premiumDetail);
        return groupHealthProposal;
    }


}
