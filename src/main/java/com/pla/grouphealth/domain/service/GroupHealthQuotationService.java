package com.pla.grouphealth.domain.service;

import com.pla.core.domain.model.agent.AgentId;
import com.pla.grouphealth.domain.model.quotation.GHQuotationProcessor;
import com.pla.grouphealth.domain.model.quotation.GroupHealthQuotation;
import com.pla.grouphealth.domain.model.quotation.Proposer;
import com.pla.grouphealth.domain.model.quotation.ProposerBuilder;
import com.pla.grouphealth.query.ProposerDto;
import com.pla.sharedkernel.identifier.QuotationId;
import org.bson.types.ObjectId;
import org.nthdimenzion.ddd.domain.annotations.DomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Created by Karunakar on 4/30/2015.
 */
@DomainService
public class GroupHealthQuotationService {

    private GHQuotationRoleAdapter ghQuotationRoleAdapter;

    private GHQuotationNumberGenerator ghQuotationNumberGenerator;

    @Autowired
    public GroupHealthQuotationService(GHQuotationRoleAdapter quotationRoleAdapter, GHQuotationNumberGenerator quotationNumberGenerator) {
        this.ghQuotationRoleAdapter = quotationRoleAdapter;
        this.ghQuotationNumberGenerator = quotationNumberGenerator;
    }

    public GroupHealthQuotation createQuotation(String agentId, String proposerName, UserDetails userDetails) {
        GHQuotationProcessor ghQuotationProcessor = ghQuotationRoleAdapter.userToQuotationProcessor(userDetails);
        QuotationId quotationId = new QuotationId(new ObjectId().toString());
        String quotationNumber = ghQuotationNumberGenerator.getQuotationNumber("5", "4", GroupHealthQuotation.class);
        return ghQuotationProcessor.createGroupLifeQuotation(quotationNumber, ghQuotationProcessor.getUserName(), quotationId, new AgentId(agentId), proposerName);
    }

    public GroupHealthQuotation updateWithProposer(GroupHealthQuotation groupHealthQuotation, ProposerDto proposerDto, UserDetails userDetails) {
        GHQuotationProcessor ghQuotationProcessor = ghQuotationRoleAdapter.userToQuotationProcessor(userDetails);
        groupHealthQuotation = checkQuotationNeedForVersioningAndGetQuotation(ghQuotationProcessor, groupHealthQuotation);
        ProposerBuilder proposerBuilder = Proposer.getProposerBuilder(proposerDto.getProposerName(), proposerDto.getProposerCode());
        proposerBuilder.withContactDetail(proposerDto.getAddressLine1(), proposerDto.getAddressLine2(), proposerDto.getPostalCode(), proposerDto.getProvince(), proposerDto.getTown(), proposerDto.getEmailAddress())
                .withContactPersonDetail(proposerDto.getContactPersonName(), proposerDto.getContactPersonEmail(), proposerDto.getContactPersonMobileNumber(), proposerDto.getContactPersonWorkPhoneNumber());
        return ghQuotationProcessor.updateWithProposer(groupHealthQuotation, proposerBuilder.build());
    }

    public GroupHealthQuotation updateWithAgent(GroupHealthQuotation groupHealthQuotation, String agentId, UserDetails userDetails) {
        GHQuotationProcessor ghQuotationProcessor = ghQuotationRoleAdapter.userToQuotationProcessor(userDetails);
        groupHealthQuotation = checkQuotationNeedForVersioningAndGetQuotation(ghQuotationProcessor, groupHealthQuotation);
        return ghQuotationProcessor.updateWithAgentId(groupHealthQuotation, new AgentId(agentId));
    }

    private GroupHealthQuotation checkQuotationNeedForVersioningAndGetQuotation(GHQuotationProcessor GHQuotationProcessor, GroupHealthQuotation groupHealthQuotation) {
        if (!groupHealthQuotation.requireVersioning()) {
            return groupHealthQuotation;
        }
        String quotationNumber = ghQuotationNumberGenerator.getQuotationNumber("5", "4", GroupHealthQuotation.class);
        QuotationId quotationId = new QuotationId(new ObjectId().toString());
        return groupHealthQuotation.cloneQuotation(quotationNumber, GHQuotationProcessor.getUserName(), quotationId);
    }


}
