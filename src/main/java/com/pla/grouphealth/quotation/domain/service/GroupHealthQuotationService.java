package com.pla.grouphealth.quotation.domain.service;

import com.pla.core.domain.model.agent.AgentId;
import com.pla.grouphealth.quotation.domain.model.*;
import com.pla.grouphealth.sharedresource.dto.GHPremiumDetailDto;
import com.pla.grouphealth.quotation.query.GHQuotationFinder;
import com.pla.grouphealth.sharedresource.dto.ProposerDto;
import com.pla.grouphealth.sharedresource.model.vo.*;
import com.pla.grouphealth.sharedresource.service.AgentIsActive;
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
import org.springframework.beans.factory.annotation.Qualifier;
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
public class GroupHealthQuotationService {

    private GHQuotationRoleAdapter quotationRoleAdapter;

    @Qualifier(value = "ghQuotationNumberGenerator")
    private GHQuotationNumberGenerator quotationNumberGenerator;

    private IPremiumCalculator premiumCalculator;

    @Qualifier(value = "ghAgentIsActive")
    private AgentIsActive agentIsActive;

    private GHQuotationFinder ghQuotationFinder;

    @Autowired
    public GroupHealthQuotationService(GHQuotationRoleAdapter quotationRoleAdapter, GHQuotationNumberGenerator quotationNumberGenerator, IPremiumCalculator premiumCalculator, AgentIsActive agentIsActive, GHQuotationFinder ghQuotationFinder) {
        this.quotationRoleAdapter = quotationRoleAdapter;
        this.quotationNumberGenerator = quotationNumberGenerator;
        this.premiumCalculator = premiumCalculator;
        this.agentIsActive = agentIsActive;
        this.ghQuotationFinder = ghQuotationFinder;
    }

    public GroupHealthQuotation createQuotation(String agentId, String proposerName, UserDetails userDetails) {
        GHQuotationProcessor ghQuotationProcessor = quotationRoleAdapter.userToQuotationProcessor(userDetails);
        if (!agentIsActive.isSatisfiedBy(new AgentId(agentId))) {
            raiseAgentIsInactiveException();
        }
        QuotationId quotationId = new QuotationId(new ObjectId().toString());
        String quotationNumber = quotationNumberGenerator.getQuotationNumber("5", "4", GroupHealthQuotation.class, LocalDate.now());
        return ghQuotationProcessor.createGroupHealthQuotation(quotationNumber, ghQuotationProcessor.getUserName(), quotationId, new AgentId(agentId), proposerName);
    }

    public GroupHealthQuotation updateWithProposer(GroupHealthQuotation groupHealthQuotation, ProposerDto proposerDto, UserDetails userDetails) {
        GHQuotationProcessor ghQuotationProcessor = quotationRoleAdapter.userToQuotationProcessor(userDetails);
        if (!agentIsActive.isSatisfiedBy(groupHealthQuotation.getAgentId())) {
            raiseAgentIsInactiveException();
        }
        groupHealthQuotation = checkQuotationNeedForVersioningAndGetQuotation(ghQuotationProcessor, groupHealthQuotation);
        GHProposerBuilder proposerBuilder = GHProposer.getProposerBuilder(proposerDto.getProposerName(), proposerDto.getProposerCode());
        proposerBuilder.withContactDetail(proposerDto.getAddressLine1(), proposerDto.getAddressLine2(), proposerDto.getPostalCode(), proposerDto.getProvince(), proposerDto.getTown(), proposerDto.getEmailAddress())
                .withContactPersonDetail(proposerDto.getContactPersonName(), proposerDto.getContactPersonEmail(), proposerDto.getContactPersonMobileNumber(), proposerDto.getContactPersonWorkPhoneNumber());
        groupHealthQuotation = ghQuotationProcessor.updateWithProposer(groupHealthQuotation, proposerBuilder.build());
        if (isNotEmpty(proposerDto.getOpportunityId())) {
            OpportunityId opportunityId = new OpportunityId(proposerDto.getOpportunityId());
            groupHealthQuotation = groupHealthQuotation.updateWithOpportunityId(opportunityId);
        }
        return groupHealthQuotation;
    }

    public GroupHealthQuotation updateWithAgent(GroupHealthQuotation groupHealthQuotation, String agentId, UserDetails userDetails) {
        if (!agentIsActive.isSatisfiedBy(groupHealthQuotation.getAgentId())) {
            raiseAgentIsInactiveException();
        }
        GHQuotationProcessor ghQuotationProcessor = quotationRoleAdapter.userToQuotationProcessor(userDetails);
        groupHealthQuotation = checkQuotationNeedForVersioningAndGetQuotation(ghQuotationProcessor, groupHealthQuotation);
        return ghQuotationProcessor.updateWithAgentId(groupHealthQuotation, new AgentId(agentId));
    }

    public GroupHealthQuotation updateInsured(GroupHealthQuotation groupHealthQuotation, Set<GHInsured> insureds, UserDetails userDetails) {
        if (!agentIsActive.isSatisfiedBy(groupHealthQuotation.getAgentId())) {
            raiseAgentIsInactiveException();
        }
        GHQuotationProcessor ghQuotationProcessor = quotationRoleAdapter.userToQuotationProcessor(userDetails);
        groupHealthQuotation = checkQuotationNeedForVersioningAndGetQuotation(ghQuotationProcessor, groupHealthQuotation);
        return ghQuotationProcessor.updateWithInsured(groupHealthQuotation, insureds);
    }

    private GroupHealthQuotation checkQuotationNeedForVersioningAndGetQuotation(GHQuotationProcessor ghQuotationProcessor, GroupHealthQuotation currentQuotation) {
        if (!currentQuotation.requireVersioning()) {
            return currentQuotation;
        }
        String parentQuotationId = currentQuotation.getParentQuotationId() == null ? currentQuotation.getQuotationId().getQuotationId() : currentQuotation.getParentQuotationId().getQuotationId();
        List<Map> childQuotations = ghQuotationFinder.getChildQuotations(parentQuotationId);
        int versionNumber = 1;
        if (isNotEmpty(childQuotations)) {
            versionNumber = versionNumber + childQuotations.size();
        }
        QuotationId quotationId = new QuotationId(new ObjectId().toString());
        return currentQuotation.cloneQuotation(currentQuotation.getQuotationNumber(), ghQuotationProcessor.getUserName(), quotationId, versionNumber, new QuotationId(parentQuotationId));
    }

    public GroupHealthQuotation updateWithPremiumDetail(GroupHealthQuotation groupHealthQuotation, GHPremiumDetailDto premiumDetailDto, UserDetails userDetails) {
        if (!agentIsActive.isSatisfiedBy(groupHealthQuotation.getAgentId())) {
            raiseAgentIsInactiveException();
        }
        GHQuotationProcessor ghQuotationProcessor = quotationRoleAdapter.userToQuotationProcessor(userDetails);
        groupHealthQuotation = checkQuotationNeedForVersioningAndGetQuotation(ghQuotationProcessor, groupHealthQuotation);
        GHPremiumDetail premiumDetail = new GHPremiumDetail(premiumDetailDto.getAddOnBenefit(), premiumDetailDto.getProfitAndSolvencyLoading(),
                premiumDetailDto.getDiscounts(), premiumDetailDto.getWaiverOfExcessLoading(), premiumDetailDto.getVat(), premiumDetailDto.getPolicyTermValue());
        premiumDetail = premiumDetail.updateWithNetPremium(groupHealthQuotation.getNetAnnualPremiumPaymentAmount(premiumDetail));
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
        groupHealthQuotation = ghQuotationProcessor.updateWithPremiumDetail(groupHealthQuotation, premiumDetail);
        return groupHealthQuotation;
    }

}
