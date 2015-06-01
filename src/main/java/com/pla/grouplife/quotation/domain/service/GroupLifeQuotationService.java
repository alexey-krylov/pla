package com.pla.grouplife.quotation.domain.service;

import com.pla.core.domain.model.agent.AgentId;
import com.pla.grouplife.quotation.domain.model.*;
import com.pla.grouplife.quotation.query.PremiumDetailDto;
import com.pla.grouplife.quotation.query.ProposerDto;
import com.pla.publishedlanguage.contract.IPremiumCalculator;
import com.pla.publishedlanguage.domain.model.BasicPremiumDto;
import com.pla.publishedlanguage.domain.model.ComputedPremiumDto;
import com.pla.publishedlanguage.domain.model.PremiumFrequency;
import com.pla.sharedkernel.identifier.QuotationId;
import org.bson.types.ObjectId;
import org.joda.time.LocalDate;
import org.nthdimenzion.common.AppConstants;
import org.nthdimenzion.ddd.domain.annotations.DomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.pla.grouplife.quotation.domain.exception.QuotationException.raiseAgentIsInactiveException;

/**
 * Created by Samir on 4/8/2015.
 */
@DomainService
public class GroupLifeQuotationService {

    private QuotationRoleAdapter quotationRoleAdapter;

    private QuotationNumberGenerator quotationNumberGenerator;

    private IPremiumCalculator premiumCalculator;

    private AgentIsActive agentIsActive;

    @Autowired
    public GroupLifeQuotationService(QuotationRoleAdapter quotationRoleAdapter, QuotationNumberGenerator quotationNumberGenerator, IPremiumCalculator premiumCalculator, AgentIsActive agentIsActive) {
        this.quotationRoleAdapter = quotationRoleAdapter;
        this.quotationNumberGenerator = quotationNumberGenerator;
        this.premiumCalculator = premiumCalculator;
        this.agentIsActive = agentIsActive;
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
        return glQuotationProcessor.updateWithProposer(groupLifeQuotation, proposerBuilder.build());
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

    private GroupLifeQuotation checkQuotationNeedForVersioningAndGetQuotation(GLQuotationProcessor glQuotationProcessor, GroupLifeQuotation groupLifeQuotation) {
        if (!groupLifeQuotation.requireVersioning()) {
            return groupLifeQuotation;
        }
        String quotationNumber = quotationNumberGenerator.getQuotationNumber("5", "1", GroupLifeQuotation.class,LocalDate.now());
        QuotationId quotationId = new QuotationId(new ObjectId().toString());
        return groupLifeQuotation.cloneQuotation(quotationNumber, glQuotationProcessor.getUserName(), quotationId);
    }

    public GroupLifeQuotation updateWithPremiumDetail(GroupLifeQuotation groupLifeQuotation, PremiumDetailDto premiumDetailDto, UserDetails userDetails) {
        if (!agentIsActive.isSatisfiedBy(groupLifeQuotation.getAgentId())) {
            raiseAgentIsInactiveException();
        }
        PremiumDetail premiumDetail = new PremiumDetail(premiumDetailDto.getAddOnBenefit(), premiumDetailDto.getProfitAndSolvencyLoading(), premiumDetailDto.getDiscounts(), premiumDetailDto.getPolicyTermValue());
        if (premiumDetailDto.getPolicyTermValue() != null && premiumDetailDto.getPolicyTermValue() == 365) {
            List<ComputedPremiumDto> computedPremiumDtoList = premiumCalculator.calculateModalPremium(new BasicPremiumDto(PremiumFrequency.ANNUALLY, groupLifeQuotation.getTotalBasicPremiumForInsured()));
            Set<Policy> policies = computedPremiumDtoList.stream().map(new Function<ComputedPremiumDto, Policy>() {
                @Override
                public Policy apply(ComputedPremiumDto computedPremiumDto) {
                    return new Policy(computedPremiumDto.getPremiumFrequency(), computedPremiumDto.getPremium().setScale(AppConstants.scale, AppConstants.roundingMode));
                }
            }).collect(Collectors.toSet());
            premiumDetail = premiumDetail.addPolicies(policies);
        } else if (premiumDetailDto.getPolicyTermValue() != null && premiumDetailDto.getPolicyTermValue() > 0 && premiumDetailDto.getPolicyTermValue() != 365) {
            int noOfInstallment = premiumDetailDto.getPolicyTermValue() / 30;
            BigDecimal installmentPremium = groupLifeQuotation.getTotalBasicPremiumForInsured();
            premiumDetail = premiumDetail.addPremiumInstallment(noOfInstallment, installmentPremium);
        }
        GLQuotationProcessor glQuotationProcessor = quotationRoleAdapter.userToQuotationProcessor(userDetails);
        groupLifeQuotation = checkQuotationNeedForVersioningAndGetQuotation(glQuotationProcessor, groupLifeQuotation);
        premiumDetail = premiumDetail.updateWithNetPremium(groupLifeQuotation.getNetAnnualPremiumPaymentAmount(premiumDetail, groupLifeQuotation.getTotalBasicPremiumForInsured()));
        groupLifeQuotation = glQuotationProcessor.updateWithPremiumDetail(groupLifeQuotation, premiumDetail);
        return groupLifeQuotation;
    }

}
