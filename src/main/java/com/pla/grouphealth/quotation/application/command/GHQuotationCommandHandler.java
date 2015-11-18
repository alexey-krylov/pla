package com.pla.grouphealth.quotation.application.command;

import com.pla.grouphealth.quotation.domain.model.GroupHealthQuotation;
import com.pla.grouphealth.quotation.domain.service.GroupHealthQuotationService;
import com.pla.grouphealth.quotation.query.GHQuotationFinder;
import com.pla.grouphealth.quotation.repository.GHQuotationRepository;
import com.pla.grouphealth.sharedresource.dto.GHInsuredDto;
import com.pla.grouphealth.sharedresource.dto.GHPremiumDetailDto;
import com.pla.grouphealth.sharedresource.model.vo.GHInsured;
import com.pla.grouphealth.sharedresource.service.GHInsuredFactory;
import com.pla.publishedlanguage.contract.IGeneralInformationProvider;
import com.pla.publishedlanguage.contract.IPremiumCalculator;
import com.pla.publishedlanguage.contract.IProcessInfoAdapter;
import com.pla.sharedkernel.identifier.QuotationId;
import org.axonframework.commandhandling.annotation.CommandHandler;
import org.axonframework.repository.Repository;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

/**
 * Created by Samir on 4/15/2015.
 */
@Component
public class GHQuotationCommandHandler {

    private Repository<GroupHealthQuotation> ghQuotationMongoRepository;

    private GroupHealthQuotationService groupHealthQuotationService;

    private IPremiumCalculator premiumCalculator;

    private GHQuotationFinder ghQuotationFinder;

    private GHQuotationRepository ghQuotationRepository;

    private IProcessInfoAdapter processInfoAdapter;

    private IGeneralInformationProvider generalInformationProvider;

    private GHInsuredFactory ghInsuredFactory;


    @Autowired
    public GHQuotationCommandHandler(Repository<GroupHealthQuotation> ghQuotationMongoRepository, GroupHealthQuotationService groupHealthQuotationService, IPremiumCalculator premiumCalculator, GHQuotationFinder ghQuotationFinder, GHQuotationRepository ghQuotationRepository, IProcessInfoAdapter processInfoAdapter, IGeneralInformationProvider generalInformationProvider, GHInsuredFactory insuredFactory) {
        this.ghQuotationMongoRepository = ghQuotationMongoRepository;
        this.groupHealthQuotationService = groupHealthQuotationService;
        this.ghQuotationFinder = ghQuotationFinder;
        this.premiumCalculator = premiumCalculator;
        this.ghQuotationRepository = ghQuotationRepository;
        this.processInfoAdapter = processInfoAdapter;
        this.generalInformationProvider = generalInformationProvider;
        this.ghInsuredFactory = insuredFactory;
    }

    @CommandHandler
    public String createQuotation(CreateGLQuotationCommand createGLQuotationCommand) {
        GroupHealthQuotation groupHealthQuotation = groupHealthQuotationService.createQuotation(createGLQuotationCommand.getAgentId(), createGLQuotationCommand.getProposerName(), createGLQuotationCommand.getUserDetails());
        ghQuotationMongoRepository.add(groupHealthQuotation);
        return groupHealthQuotation.getIdentifier().getQuotationId();
    }

    @CommandHandler
    public String updateWithProposer(UpdateGHQuotationWithProposerCommand updateGHQuotationWithProposerCommand) {
        GroupHealthQuotation groupHealthQuotation = ghQuotationMongoRepository.load(new QuotationId(updateGHQuotationWithProposerCommand.getQuotationId()));
        boolean isVersioningRequire = groupHealthQuotation.requireVersioning();
        groupHealthQuotation = groupHealthQuotationService.updateWithProposer(groupHealthQuotation, updateGHQuotationWithProposerCommand.getProposerDto(), updateGHQuotationWithProposerCommand.getUserDetails());
        if (isVersioningRequire) {
            ghQuotationMongoRepository.add(groupHealthQuotation);
        }
        return groupHealthQuotation.getIdentifier().getQuotationId();
    }

    @CommandHandler
    public String updateWithAgentDetail(UpdateGLQuotationWithAgentCommand updateGLQuotationWithAgentCommand) {
        GroupHealthQuotation groupHealthQuotation = ghQuotationMongoRepository.load(new QuotationId(updateGLQuotationWithAgentCommand.getQuotationId()));
        boolean isVersioningRequire = groupHealthQuotation.requireVersioning();
        groupHealthQuotation = groupHealthQuotationService.updateWithAgent(groupHealthQuotation, updateGLQuotationWithAgentCommand.getAgentId(), updateGLQuotationWithAgentCommand.getUserDetails());
        if (isVersioningRequire) {
            ghQuotationMongoRepository.add(groupHealthQuotation);
        }
        return groupHealthQuotation.getIdentifier().getQuotationId();
    }

    @CommandHandler
    public String generateQuotation(GenerateGLQuotationCommand generateGLQuotationCommand) {
        GroupHealthQuotation groupHealthQuotation = ghQuotationMongoRepository.load(new QuotationId(generateGLQuotationCommand.getQuotationId()));
        groupHealthQuotation.generateQuotation(LocalDate.now());
        return groupHealthQuotation.getIdentifier().getQuotationId();
    }

    @CommandHandler
    public String updateWithInsureDetail(UpdateGLQuotationWithInsuredCommand updateGLQuotationWithInsuredCommand) {
        List<GHInsuredDto> insuredDtos = updateGLQuotationWithInsuredCommand.getInsuredDtos();
        Set<GHInsured> insureds = ghInsuredFactory.createInsuredDetail(insuredDtos);
        GroupHealthQuotation groupHealthQuotation = ghQuotationMongoRepository.load(new QuotationId(updateGLQuotationWithInsuredCommand.getQuotationId()));
        boolean isVersioningRequire = groupHealthQuotation.requireVersioning();
        groupHealthQuotation = groupHealthQuotationService.updateInsured(groupHealthQuotation, insureds, updateGLQuotationWithInsuredCommand.getUserDetails());
        GHPremiumDetailDto premiumDetailDto = new GHPremiumDetailDto(BigDecimal.valueOf(20), 365, BigDecimal.valueOf(15), processInfoAdapter.getServiceTaxAmount());
        groupHealthQuotation = groupHealthQuotationService.updateWithPremiumDetail(groupHealthQuotation, premiumDetailDto, updateGLQuotationWithInsuredCommand.getUserDetails());
        groupHealthQuotation = groupHealthQuotation.updateWithMoratoriumPeriod(updateGLQuotationWithInsuredCommand.isConsiderMoratoriumPeriod());
        groupHealthQuotation = groupHealthQuotation.updateFlagSamePlanForAllRelation(updateGLQuotationWithInsuredCommand.isSamePlanForAllRelation());
        groupHealthQuotation = groupHealthQuotation.updateFlagSamePlanForAllCategory(updateGLQuotationWithInsuredCommand.isSamePlanForAllCategory());
        if (isVersioningRequire) {
            ghQuotationMongoRepository.add(groupHealthQuotation);
        }
        return groupHealthQuotation.getIdentifier().getQuotationId();
    }

    @CommandHandler
    public String updateWithPremiumDetail(UpdateGHQuotationWithPremiumDetailCommand updateGLQuotationWithPremiumDetailCommand) {
        GroupHealthQuotation groupHealthQuotation = ghQuotationMongoRepository.load(new QuotationId(updateGLQuotationWithPremiumDetailCommand.getQuotationId()));
        boolean isVersioningRequire = groupHealthQuotation.requireVersioning();
        groupHealthQuotation = populateAnnualBasicPremiumOfInsured(groupHealthQuotation, updateGLQuotationWithPremiumDetailCommand.getUserDetails(), updateGLQuotationWithPremiumDetailCommand.getPremiumDetailDto());
        if (isVersioningRequire) {
            ghQuotationMongoRepository.add(groupHealthQuotation);
        }
        return groupHealthQuotation.getIdentifier().getQuotationId();
    }


    @CommandHandler
    public GroupHealthQuotation recalculatePremium(GHRecalculatedInsuredPremiumCommand glRecalculatedInsuredPremiumCommand) {
        GroupHealthQuotation groupHealthQuotation = ghQuotationRepository.findOne(new QuotationId(glRecalculatedInsuredPremiumCommand.getQuotationId()));
        groupHealthQuotation = populateAnnualBasicPremiumOfInsured(groupHealthQuotation, glRecalculatedInsuredPremiumCommand.getUserDetails(), glRecalculatedInsuredPremiumCommand.getPremiumDetailDto());
        return groupHealthQuotation;

    }

    @CommandHandler
    public void purgeGHQuotation(GHPurgeGLQuotationCommand purgeGLQuotationCommand) {
        GroupHealthQuotation groupHealthQuotation = ghQuotationMongoRepository.load(purgeGLQuotationCommand.getQuotationId());
        groupHealthQuotation.purgeQuotation();
    }

    @CommandHandler
    public void closureGHQuotation(GHClosureGLQuotationCommand closureGLQuotationCommand) {
        GroupHealthQuotation groupHealthQuotation = ghQuotationMongoRepository.load(closureGLQuotationCommand.getQuotationId());
        groupHealthQuotation.declineQuotation();
    }

    @CommandHandler
    public void shareGHQuotation(GHSharedQuotationCommand sharedQuotationCommand) {
        GroupHealthQuotation groupHealthQuotation = ghQuotationMongoRepository.load(sharedQuotationCommand.getQuotationId());
        groupHealthQuotation.shareQuotation(LocalDate.now());
    }


    @CommandHandler
    public void convertedGHQuotation(GHQuotationConvertedCommand ghQuotationConvertedCommand) {
        GroupHealthQuotation groupHealthQuotation = ghQuotationMongoRepository.load(ghQuotationConvertedCommand.getQuotationId());
        groupHealthQuotation.closeQuotation();
    }

    private GroupHealthQuotation populateAnnualBasicPremiumOfInsured(GroupHealthQuotation groupHealthQuotation, UserDetails userDetails, GHPremiumDetailDto premiumDetailDto) {
        Set<GHInsured> insureds = groupHealthQuotation.getInsureds();
        insureds = ghInsuredFactory.recalculateProratePremiumForInsureds(premiumDetailDto, insureds);
        groupHealthQuotation = groupHealthQuotationService.updateInsured(groupHealthQuotation, insureds, userDetails);
        groupHealthQuotation = groupHealthQuotationService.updateWithPremiumDetail(groupHealthQuotation, premiumDetailDto, userDetails);
        return groupHealthQuotation;
    }
}
