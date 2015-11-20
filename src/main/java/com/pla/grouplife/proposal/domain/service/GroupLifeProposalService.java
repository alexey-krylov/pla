/**
 * Created by User on 6/30/2015.
 */

package com.pla.grouplife.proposal.domain.service;

import com.pla.core.domain.model.agent.AgentId;
import com.pla.grouplife.proposal.domain.model.GLProposalProcessor;
import com.pla.grouplife.proposal.domain.model.GroupLifeProposal;
import com.pla.grouplife.quotation.domain.service.AgentIsActive;
import com.pla.grouplife.sharedresource.dto.PremiumDetailDto;
import com.pla.grouplife.sharedresource.dto.ProposerDto;
import com.pla.grouplife.sharedresource.model.vo.*;
import com.pla.grouplife.sharedresource.query.GLFinder;
import com.pla.publishedlanguage.contract.IPremiumCalculator;
import com.pla.publishedlanguage.domain.model.BasicPremiumDto;
import com.pla.publishedlanguage.domain.model.ComputedPremiumDto;
import com.pla.publishedlanguage.domain.model.PremiumFrequency;
import com.pla.sharedkernel.identifier.LineOfBusinessEnum;
import org.nthdimenzion.common.AppConstants;
import org.nthdimenzion.ddd.domain.annotations.DomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.pla.grouplife.proposal.domain.exception.ProposalException.raiseAgentIsInactiveException;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

@DomainService
public class GroupLifeProposalService {


    private AgentIsActive agentIsActive;

    private IPremiumCalculator premiumCalculator;

    private GroupLifeProposalRoleAdapter groupLifeProposalRoleAdapter;

    @Autowired
    private GLFinder glFinder;

    @Autowired
    public GroupLifeProposalService(AgentIsActive agentIsActive, GroupLifeProposalRoleAdapter groupLifeProposalRoleAdapter, IPremiumCalculator premiumCalculator) {
        this.agentIsActive = agentIsActive;
        this.groupLifeProposalRoleAdapter = groupLifeProposalRoleAdapter;
        this.premiumCalculator = premiumCalculator;
    }

    public GroupLifeProposal updateWithAgent(GroupLifeProposal groupLifeProposal, String agentId, UserDetails userDetails,BigDecimal agentCommissionPercentage,Boolean isCommissionOverridden) {
        if (!agentIsActive.isSatisfiedBy(new AgentId(agentId))){
            raiseAgentIsInactiveException();
        }
        GLProposalProcessor glProposalProcessor = groupLifeProposalRoleAdapter.userToProposalProcessor(userDetails);
        return glProposalProcessor.updateWithAgentId(groupLifeProposal, new AgentId(agentId),isCommissionOverridden?agentCommissionPercentage:null,isCommissionOverridden);
    }

    public GroupLifeProposal updateWithProposerDetail(GroupLifeProposal groupLifeProposal, ProposerDto proposerDto, UserDetails userDetails) {
        GLProposalProcessor glProposalProcessor = groupLifeProposalRoleAdapter.userToProposalProcessor(userDetails);
        ProposerBuilder proposerBuilder = Proposer.getProposerBuilder(proposerDto.getProposerName(), proposerDto.getProposerCode());
        proposerBuilder.withContactDetail(proposerDto.getAddressLine1(), proposerDto.getAddressLine2(), proposerDto.getPostalCode(), proposerDto.getProvince(), proposerDto.getTown(), proposerDto.getEmailAddress())
                .withContactPersonDetail(proposerDto.getContactPersonName(), proposerDto.getContactPersonEmail(), proposerDto.getContactPersonMobileNumber(), proposerDto.getContactPersonWorkPhoneNumber());
        groupLifeProposal = glProposalProcessor.updateWithProposer(groupLifeProposal, proposerBuilder.build());
        Map<String, Object> industryMap = glFinder.findIndustryById(proposerDto.getIndustryId());
        if (industryMap != null) {
            Industry industry = new Industry((String) industryMap.get("industryId"), (String) industryMap.get("industryName"), (BigDecimal) industryMap.get("industryFactor"));
            groupLifeProposal = groupLifeProposal.updateWithIndustry(industry);
        }
        return groupLifeProposal;
    }


    public GroupLifeProposal updateInsured(GroupLifeProposal groupLifeProposal, Set<Insured> insureds, UserDetails userDetails) {
        if (!agentIsActive.isSatisfiedBy(groupLifeProposal.getAgentId())) {
            raiseAgentIsInactiveException();
        }
        GLProposalProcessor glProposalProcessor = groupLifeProposalRoleAdapter.userToProposalProcessor(userDetails);
        Industry industry = groupLifeProposal.getIndustry();
        groupLifeProposal = groupLifeProposal.updateWithIndustry(industry);
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
            List<ComputedPremiumDto> computedPremiumDtoList = premiumCalculator.calculateModalPremium(new BasicPremiumDto(PremiumFrequency.ANNUALLY, premiumDetail.getNetTotalPremium(), LineOfBusinessEnum.GROUP_LIFE));
            Set<GLFrequencyPremium> policies = computedPremiumDtoList.stream().map(new Function<ComputedPremiumDto, GLFrequencyPremium>() {
                @Override
                public GLFrequencyPremium apply(ComputedPremiumDto computedPremiumDto) {
                    return new GLFrequencyPremium(computedPremiumDto.getPremiumFrequency(), computedPremiumDto.getPremium().setScale(AppConstants.scale, AppConstants.roundingMode));
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
        if (premiumDetailDto.getOptedPremiumFrequency() != null && isNotEmpty(premiumDetail.getFrequencyPremiums())) {
            premiumDetail = premiumDetail.updateWithOptedFrequencyPremium(premiumDetailDto.getOptedPremiumFrequency());
        }
        groupLifeProposal = glProposalProcessor.updateWithPremiumDetail(groupLifeProposal, premiumDetail);
        return groupLifeProposal;
    }

}

