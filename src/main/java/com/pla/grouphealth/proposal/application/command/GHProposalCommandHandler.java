package com.pla.grouphealth.proposal.application.command;

import com.pla.grouphealth.proposal.domain.model.GroupHealthProposal;
import com.pla.grouphealth.proposal.domain.service.GroupHealthProposalFactory;
import com.pla.grouphealth.proposal.domain.service.GroupHealthProposalService;
import com.pla.grouphealth.proposal.query.GHProposalFinder;
import com.pla.grouphealth.proposal.repository.GHProposalRepository;
import com.pla.grouphealth.sharedresource.dto.GHInsuredDto;
import com.pla.grouphealth.sharedresource.dto.GHPremiumDetailDto;
import com.pla.grouphealth.sharedresource.model.vo.GHInsured;
import com.pla.grouphealth.sharedresource.query.GHFinder;
import com.pla.grouphealth.sharedresource.service.GHInsuredFactory;
import com.pla.publishedlanguage.contract.IProcessInfoAdapter;
import com.pla.sharedkernel.identifier.ProposalId;
import com.pla.sharedkernel.identifier.QuotationId;
import org.axonframework.commandhandling.annotation.CommandHandler;
import org.axonframework.repository.Repository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Samir on 6/26/2015.
 */
@Component
public class GHProposalCommandHandler {

    private GroupHealthProposalFactory groupHealthProposalFactory;

    private Repository<GroupHealthProposal> ghProposalMongoRepository;

    private GHProposalFinder ghProposalFinder;

    private GHFinder ghFinder;

    private GHInsuredFactory ghInsuredFactory;

    private GroupHealthProposalService groupHealthProposalService;

    private IProcessInfoAdapter processInfoAdapter;

    private GHProposalRepository ghProposalRepository;

    @Autowired
    public GHProposalCommandHandler(GroupHealthProposalFactory groupHealthProposalFactory, Repository<GroupHealthProposal> ghProposalMongoRepository, GHProposalFinder ghProposalFinder, GHFinder ghFinder, GHInsuredFactory ghInsuredFactory, GroupHealthProposalService groupHealthProposalService, IProcessInfoAdapter processInfoAdapter, GHProposalRepository ghProposalRepository) {
        this.groupHealthProposalFactory = groupHealthProposalFactory;
        this.ghProposalMongoRepository = ghProposalMongoRepository;
        this.ghProposalFinder = ghProposalFinder;
        this.ghFinder = ghFinder;
        this.ghInsuredFactory = ghInsuredFactory;
        this.groupHealthProposalService = groupHealthProposalService;
        this.processInfoAdapter = processInfoAdapter;
        this.ghProposalRepository = ghProposalRepository;
    }

    @CommandHandler
    public String createProposalFromQuotation(GHQuotationToProposalCommand ghQuotationToProposalCommand) {
        Map quotationMap = ghFinder.searchQuotationById(new QuotationId(ghQuotationToProposalCommand.getQuotationId()));
        String quotationNumber = (String) quotationMap.get("quotationNumber");
        Map proposalMap = ghProposalFinder.findProposalByQuotationNumber(quotationNumber);
        GroupHealthProposal groupHealthProposal = null;
        ProposalId proposalId = new ProposalId(ObjectId.get().toString());
        if (proposalMap != null) {
            proposalId = new ProposalId(proposalMap.get("_id").toString());
            groupHealthProposal = ghProposalMongoRepository.load(proposalId);
        }
        groupHealthProposal = groupHealthProposalFactory.createProposal(new QuotationId(ghQuotationToProposalCommand.getQuotationId()), proposalId);
        if (proposalMap == null) {
            ghProposalMongoRepository.add(groupHealthProposal);
        }
        return proposalId.getProposalId();
    }

    @CommandHandler
    public String updateWithAgentDetail(UpdateGHProposalWithAgentCommand updateGHProposalWithAgentCommand) {
        GroupHealthProposal groupHealthProposal = ghProposalMongoRepository.load(new ProposalId(updateGHProposalWithAgentCommand.getProposalId()));
        groupHealthProposal = groupHealthProposalService.updateWithAgent(groupHealthProposal, updateGHProposalWithAgentCommand.getAgentId(), updateGHProposalWithAgentCommand.getUserDetails());
        return groupHealthProposal.getIdentifier().getProposalId();
    }

    @CommandHandler
    public String updateWithAgentDetail(UpdateGHProposalWithProposerCommand updateGHProposalWithProposerCommand) {
        GroupHealthProposal groupHealthProposal = ghProposalMongoRepository.load(new ProposalId(updateGHProposalWithProposerCommand.getProposalId()));
        groupHealthProposal = groupHealthProposalService.updateWithProposer(groupHealthProposal, updateGHProposalWithProposerCommand.getProposerDto(), updateGHProposalWithProposerCommand.getUserDetails());
        return groupHealthProposal.getIdentifier().getProposalId();
    }

    @CommandHandler
    public String updateWithInsureDetail(UpdateGHProposalWithInsuredCommand updateGHProposalWithInsuredCommand) {
        List<GHInsuredDto> insuredDtos = updateGHProposalWithInsuredCommand.getInsuredDtos();
        Set<GHInsured> insureds = ghInsuredFactory.createInsuredDetail(insuredDtos);
        GroupHealthProposal groupHealthProposal = ghProposalMongoRepository.load(new ProposalId(updateGHProposalWithInsuredCommand.getProposalId()));
        groupHealthProposal = groupHealthProposalService.updateInsured(groupHealthProposal, insureds, updateGHProposalWithInsuredCommand.getUserDetails());
        GHPremiumDetailDto premiumDetailDto = new GHPremiumDetailDto(BigDecimal.valueOf(20), 365, BigDecimal.valueOf(15), processInfoAdapter.getServiceTaxAmount());
        groupHealthProposal = groupHealthProposalService.updateWithPremiumDetail(groupHealthProposal, premiumDetailDto, updateGHProposalWithInsuredCommand.getUserDetails());
        return groupHealthProposal.getIdentifier().getProposalId();
    }

    @CommandHandler
    public GroupHealthProposal recalculatePremium(GHProposalRecalculatedInsuredPremiumCommand ghProposalRecalculatedInsuredPremiumCommand) {
        GroupHealthProposal groupHealthProposal = ghProposalRepository.findOne(new ProposalId(ghProposalRecalculatedInsuredPremiumCommand.getProposalId()));
        groupHealthProposal = populateAnnualBasicPremiumOfInsured(groupHealthProposal, ghProposalRecalculatedInsuredPremiumCommand.getUserDetails(), ghProposalRecalculatedInsuredPremiumCommand.getPremiumDetailDto());
        return groupHealthProposal;

    }

    @CommandHandler
    public String updateWithPremiumDetail(UpdateGHProposalWithPremiumDetailCommand updateGHProposalWithPremiumDetailCommand) {
        GroupHealthProposal groupHealthProposal = ghProposalMongoRepository.load(new ProposalId(updateGHProposalWithPremiumDetailCommand.getProposalId()));
        groupHealthProposal = populateAnnualBasicPremiumOfInsured(groupHealthProposal, updateGHProposalWithPremiumDetailCommand.getUserDetails(), updateGHProposalWithPremiumDetailCommand.getPremiumDetailDto());
        return groupHealthProposal.getIdentifier().getProposalId();
    }

    private GroupHealthProposal populateAnnualBasicPremiumOfInsured(GroupHealthProposal groupHealthProposal, UserDetails userDetails, GHPremiumDetailDto premiumDetailDto) {
        if (premiumDetailDto.getPolicyTermValue() != 365) {
            Set<GHInsured> insureds = groupHealthProposal.getInsureds();
            insureds = ghInsuredFactory.recalculateProratePremiumForInsureds(premiumDetailDto, insureds);
            groupHealthProposal = groupHealthProposalService.updateInsured(groupHealthProposal, insureds, userDetails);
        }
        groupHealthProposal = groupHealthProposalService.updateWithPremiumDetail(groupHealthProposal, premiumDetailDto, userDetails);
        return groupHealthProposal;
    }

}
