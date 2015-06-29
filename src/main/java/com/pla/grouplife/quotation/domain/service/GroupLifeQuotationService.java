package com.pla.grouplife.quotation.domain.service;

import com.pla.core.domain.model.agent.AgentId;
import com.pla.grouplife.quotation.domain.model.GLQuotationProcessor;
import com.pla.grouplife.quotation.domain.model.GroupLifeQuotation;
import com.pla.grouplife.quotation.domain.model.Policy;
import com.pla.grouplife.quotation.query.GLQuotationFinder;
import com.pla.grouplife.sharedresource.dto.PremiumDetailDto;
import com.pla.grouplife.sharedresource.dto.ProposerDto;
import com.pla.grouplife.sharedresource.model.vo.Insured;
import com.pla.grouplife.sharedresource.model.vo.PremiumDetail;
import com.pla.grouplife.sharedresource.model.vo.Proposer;
import com.pla.grouplife.sharedresource.model.vo.ProposerBuilder;
import com.pla.publishedlanguage.contract.IPremiumCalculator;
import com.pla.publishedlanguage.domain.model.BasicPremiumDto;
import com.pla.publishedlanguage.domain.model.ComputedPremiumDto;
import com.pla.publishedlanguage.domain.model.PremiumFrequency;
import com.pla.sharedkernel.identifier.OpportunityId;
import com.pla.sharedkernel.identifier.QuotationId;
import org.bson.types.ObjectId;
import org.joda.time.LocalDate;
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

import static com.pla.grouplife.quotation.domain.exception.QuotationException.raiseAgentIsInactiveException;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Created by Samir on 4/8/2015.
 */
@DomainService
public class GroupLifeQuotationService {

    private QuotationRoleAdapter quotationRoleAdapter;

    private QuotationNumberGenerator quotationNumberGenerator;

    private IPremiumCalculator premiumCalculator;

    private AgentIsActive agentIsActive;

    private GLQuotationFinder glQuotationFinder;

    @Autowired
    public GroupLifeQuotationService(QuotationRoleAdapter quotationRoleAdapter, QuotationNumberGenerator quotationNumberGenerator, IPremiumCalculator premiumCalculator, AgentIsActive agentIsActive, GLQuotationFinder glQuotationFinder) {
        this.quotationRoleAdapter = quotationRoleAdapter;
        this.quotationNumberGenerator = quotationNumberGenerator;
        this.premiumCalculator = premiumCalculator;
        this.agentIsActive = agentIsActive;
        this.glQuotationFinder = glQuotationFinder;
    }

    public GroupLifeQuotation createQuotation(String agentId, String proposerName, UserDetails userDetails) {
        GLQuotationProcessor glQuotationProcessor = quotationRoleAdapter.userToQuotationProcessor(userDetails);
        if (!agentIsActive.isSatisfiedBy(new AgentId(agentId))) {
            raiseAgentIsInactiveException();
        }
        QuotationId quotationId = new QuotationId(new ObjectId().toString());
        String quotationNumber = quotationNumberGenerator.getQuotationNumber("5", "1", GroupLifeQuotation.class, LocalDate.now());
        return glQuotationProcessor.createGroupLifeQuotation(quotationNumber, glQuotationProcessor.getUserName(), quotationId, new AgentId(agentId), proposerName);
    }

    public GroupLifeQuotation updateWithProposer(GroupLifeQuotation groupLifeQuotation, ProposerDto proposerDto, UserDetails userDetails) {
        GLQuotationProcessor glQuotationProcessor = quotationRoleAdapter.userToQuotationProcessor(userDetails);
        if (!agentIsActive.isSatisfiedBy(groupLifeQuotation.getAgentId())) {
            raiseAgentIsInactiveException();
        }
        groupLifeQuotation = checkQuotationNeedForVersioningAndGetQuotation(glQuotationProcessor, groupLifeQuotation);
        ProposerBuilder proposerBuilder = Proposer.getProposerBuilder(proposerDto.getProposerName(), proposerDto.getProposerCode());
        proposerBuilder.withContactDetail(proposerDto.getAddressLine1(), proposerDto.getAddressLine2(), proposerDto.getPostalCode(), proposerDto.getProvince(), proposerDto.getTown(), proposerDto.getEmailAddress())
                .withContactPersonDetail(proposerDto.getContactPersonName(), proposerDto.getContactPersonEmail(), proposerDto.getContactPersonMobileNumber(), proposerDto.getContactPersonWorkPhoneNumber());
        groupLifeQuotation = glQuotationProcessor.updateWithProposer(groupLifeQuotation, proposerBuilder.build());
        if (isNotEmpty(proposerDto.getOpportunityId())) {
            OpportunityId opportunityId = new OpportunityId(proposerDto.getOpportunityId());
            groupLifeQuotation = groupLifeQuotation.updateWithOpportunityId(opportunityId);
        }
        return groupLifeQuotation;
    }

    public GroupLifeQuotation updateWithAgent(GroupLifeQuotation groupLifeQuotation, String agentId, UserDetails userDetails) {
        if (!agentIsActive.isSatisfiedBy(groupLifeQuotation.getAgentId())) {
            raiseAgentIsInactiveException();
        }
        GLQuotationProcessor glQuotationProcessor = quotationRoleAdapter.userToQuotationProcessor(userDetails);
        groupLifeQuotation = checkQuotationNeedForVersioningAndGetQuotation(glQuotationProcessor, groupLifeQuotation);
        return glQuotationProcessor.updateWithAgentId(groupLifeQuotation, new AgentId(agentId));
    }

    public GroupLifeQuotation updateInsured(GroupLifeQuotation groupLifeQuotation, Set<Insured> insureds, UserDetails userDetails) {
        if (!agentIsActive.isSatisfiedBy(groupLifeQuotation.getAgentId())) {
            raiseAgentIsInactiveException();
        }
        GLQuotationProcessor glQuotationProcessor = quotationRoleAdapter.userToQuotationProcessor(userDetails);
        groupLifeQuotation = checkQuotationNeedForVersioningAndGetQuotation(glQuotationProcessor, groupLifeQuotation);
        return glQuotationProcessor.updateWithInsured(groupLifeQuotation, insureds);
    }

    private GroupLifeQuotation checkQuotationNeedForVersioningAndGetQuotation(GLQuotationProcessor glQuotationProcessor, GroupLifeQuotation currentQuotation) {
        if (!currentQuotation.requireVersioning()) {
            return currentQuotation;
        }
        String parentQuotationId = currentQuotation.getParentQuotationId() == null ? currentQuotation.getQuotationId().getQuotationId() : currentQuotation.getParentQuotationId().getQuotationId();
        List<Map> childQuotations = glQuotationFinder.getChildQuotations(parentQuotationId);
        int versionNumber = 1;
        if (isNotEmpty(childQuotations)) {
            versionNumber = versionNumber + childQuotations.size();
        }
        QuotationId quotationId = new QuotationId(new ObjectId().toString());
        return currentQuotation.cloneQuotation(currentQuotation.getQuotationNumber(), glQuotationProcessor.getUserName(), quotationId, versionNumber, new QuotationId(parentQuotationId));
    }

    public GroupLifeQuotation updateWithPremiumDetail(GroupLifeQuotation groupLifeQuotation, PremiumDetailDto premiumDetailDto, UserDetails userDetails) {
        if (!agentIsActive.isSatisfiedBy(groupLifeQuotation.getAgentId())) {
            raiseAgentIsInactiveException();
        }
        PremiumDetail premiumDetail = new PremiumDetail(premiumDetailDto.getAddOnBenefit(), premiumDetailDto.getProfitAndSolvencyLoading(), premiumDetailDto.getDiscounts(), premiumDetailDto.getPolicyTermValue());
        premiumDetail = premiumDetail.updateWithNetPremium(groupLifeQuotation.getNetAnnualPremiumPaymentAmount(premiumDetail));
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
        GLQuotationProcessor glQuotationProcessor = quotationRoleAdapter.userToQuotationProcessor(userDetails);
        groupLifeQuotation = glQuotationProcessor.updateWithPremiumDetail(groupLifeQuotation, premiumDetail);
        return groupLifeQuotation;
    }

}
