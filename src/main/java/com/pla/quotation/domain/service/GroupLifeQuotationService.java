package com.pla.quotation.domain.service;

import com.pla.core.domain.model.agent.AgentId;
import com.pla.publishedlanguage.contract.IPremiumCalculator;
import com.pla.quotation.domain.model.grouplife.GLQuotationProcessor;
import com.pla.quotation.domain.model.grouplife.GroupLifeQuotation;
import com.pla.quotation.domain.model.grouplife.Proposer;
import com.pla.quotation.domain.model.grouplife.ProposerBuilder;
import com.pla.quotation.query.ProposerDto;
import com.pla.sharedkernel.identifier.QuotationId;
import org.nthdimenzion.ddd.domain.annotations.DomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Created by Samir on 4/8/2015.
 */
@DomainService
public class GroupLifeQuotationService {

    private QuotationRoleAdapter quotationRoleAdapter;

    private QuotationNumberGenerator quotationNumberGenerator;

    private IPremiumCalculator premiumCalculator;

    @Autowired
    public GroupLifeQuotationService(QuotationRoleAdapter quotationRoleAdapter, QuotationNumberGenerator quotationNumberGenerator, IPremiumCalculator premiumCalculator) {
        this.quotationRoleAdapter = quotationRoleAdapter;
        this.quotationNumberGenerator = quotationNumberGenerator;
        this.premiumCalculator = premiumCalculator;
    }

    public GroupLifeQuotation createQuotation(AgentId agentId, String proposerName, UserDetails userDetails) {
        GLQuotationProcessor glQuotationProcessor = quotationRoleAdapter.userToQuotationProcessor(userDetails);
        QuotationId quotationId = new QuotationId(quotationNumberGenerator.getQuotationNumber("5", "1", GroupLifeQuotation.class));
        return glQuotationProcessor.createGroupLifeQuotation(glQuotationProcessor.getUserName(), quotationId, agentId, proposerName);
    }

    public GroupLifeQuotation updateWithProposer(GroupLifeQuotation groupLifeQuotation, ProposerDto proposerDto, UserDetails userDetails) {
        GLQuotationProcessor glQuotationProcessor = quotationRoleAdapter.userToQuotationProcessor(userDetails);
        groupLifeQuotation = checkQuotationNeedForVersioningAndGetQuotation(glQuotationProcessor, groupLifeQuotation);
        ProposerBuilder proposerBuilder = Proposer.getProposerBuilder(proposerDto.getProposerName(), proposerDto.getProposerCode());
        proposerBuilder.withContactDetail(proposerDto.getAddressLine1(), proposerDto.getAddressLine2(), proposerDto.getPostalCode(), proposerDto.getProvince(), proposerDto.getTown(), proposerDto.getEmailAddress())
                .withContactPersonDetail(proposerDto.getContactPersonName(), proposerDto.getContactPersonEmail(), proposerDto.getContactPersonMobileNumber(), proposerDto.getContactPersonWorkPhoneNumber());
        return glQuotationProcessor.updateWithProposer(groupLifeQuotation, proposerBuilder.build());
    }

    public GroupLifeQuotation updateWithAgent(GroupLifeQuotation groupLifeQuotation, AgentId agentId, UserDetails userDetails) {
        GLQuotationProcessor glQuotationProcessor = quotationRoleAdapter.userToQuotationProcessor(userDetails);
        groupLifeQuotation = checkQuotationNeedForVersioningAndGetQuotation(glQuotationProcessor, groupLifeQuotation);
        return glQuotationProcessor.updateWithAgentId(groupLifeQuotation, agentId);
    }

    private GroupLifeQuotation checkQuotationNeedForVersioningAndGetQuotation(GLQuotationProcessor glQuotationProcessor, GroupLifeQuotation groupLifeQuotation) {
        if (!groupLifeQuotation.requireVersioning()) {
            return groupLifeQuotation;
        }
        QuotationId quotationId = new QuotationId(quotationNumberGenerator.getQuotationNumber("5", "1", GroupLifeQuotation.class));
        return groupLifeQuotation.cloneQuotation(glQuotationProcessor.getUserName(), quotationId);
    }


}
