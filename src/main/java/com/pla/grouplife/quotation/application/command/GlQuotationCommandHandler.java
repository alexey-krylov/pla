package com.pla.grouplife.quotation.application.command;

import com.pla.grouplife.quotation.domain.model.GroupLifeQuotation;
import com.pla.grouplife.quotation.domain.service.GroupLifeQuotationService;
import com.pla.grouplife.quotation.repository.GlQuotationRepository;
import com.pla.grouplife.sharedresource.dto.InsuredDto;
import com.pla.grouplife.sharedresource.dto.PremiumDetailDto;
import com.pla.grouplife.sharedresource.model.vo.Insured;
import com.pla.grouplife.sharedresource.util.GLInsuredFactory;
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
public class GlQuotationCommandHandler {

    private Repository<GroupLifeQuotation> glQuotationMongoRepository;

    private GroupLifeQuotationService groupLifeQuotationService;

    private GlQuotationRepository glQuotationRepository;

    private GLInsuredFactory glInsuredFactory;


    @Autowired
    public GlQuotationCommandHandler(Repository<GroupLifeQuotation> glQuotationMongoRepository, GroupLifeQuotationService groupLifeQuotationService, GlQuotationRepository glQuotationRepository, GLInsuredFactory glInsuredFactory) {
        this.glQuotationMongoRepository = glQuotationMongoRepository;
        this.groupLifeQuotationService = groupLifeQuotationService;
        this.glQuotationRepository = glQuotationRepository;
        this.glInsuredFactory = glInsuredFactory;
    }

    @CommandHandler
    public String createQuotation(CreateGLQuotationCommand createGLQuotationCommand) {
        GroupLifeQuotation groupLifeQuotation = groupLifeQuotationService.createQuotation(createGLQuotationCommand.getAgentId(), createGLQuotationCommand.getProposerName(), createGLQuotationCommand.getUserDetails());
        glQuotationMongoRepository.add(groupLifeQuotation);
        return groupLifeQuotation.getIdentifier().getQuotationId();
    }

    @CommandHandler
    public String updateWithProposer(UpdateGLQuotationWithProposerCommand updateGLQuotationWithProposerCommand) {
        GroupLifeQuotation groupLifeQuotation = glQuotationMongoRepository.load(new QuotationId(updateGLQuotationWithProposerCommand.getQuotationId()));
        boolean isVersioningRequire = groupLifeQuotation.requireVersioning();
        groupLifeQuotation = groupLifeQuotationService.updateWithProposer(groupLifeQuotation, updateGLQuotationWithProposerCommand.getProposerDto(), updateGLQuotationWithProposerCommand.getUserDetails());
        if (isVersioningRequire) {
            glQuotationMongoRepository.add(groupLifeQuotation);
        }
        return groupLifeQuotation.getIdentifier().getQuotationId();
    }

    @CommandHandler
    public String updateWithAgentDetail(UpdateGLQuotationWithAgentCommand updateGLQuotationWithAgentCommand) {
        GroupLifeQuotation groupLifeQuotation = glQuotationMongoRepository.load(new QuotationId(updateGLQuotationWithAgentCommand.getQuotationId()));
        boolean isVersioningRequire = groupLifeQuotation.requireVersioning();
        groupLifeQuotation = groupLifeQuotationService.updateWithAgent(groupLifeQuotation, updateGLQuotationWithAgentCommand.getAgentId(), updateGLQuotationWithAgentCommand.getUserDetails());
        if (isVersioningRequire) {
            glQuotationMongoRepository.add(groupLifeQuotation);
        }
        return groupLifeQuotation.getIdentifier().getQuotationId();
    }

    @CommandHandler
    public String generateQuotation(GenerateGLQuotationCommand generateGLQuotationCommand) {
        GroupLifeQuotation groupLifeQuotation = glQuotationMongoRepository.load(new QuotationId(generateGLQuotationCommand.getQuotationId()));
        groupLifeQuotation.generateQuotation(LocalDate.now());
        return groupLifeQuotation.getIdentifier().getQuotationId();
    }

    @CommandHandler
    public String updateWithInsureDetail(UpdateGLQuotationWithInsuredCommand updateGLQuotationWithInsuredCommand) {
        List<InsuredDto> insuredDtos = updateGLQuotationWithInsuredCommand.getInsuredDtos();
        Set<Insured> insureds = glInsuredFactory.createInsuredDetail(insuredDtos);
        GroupLifeQuotation groupLifeQuotation = glQuotationMongoRepository.load(new QuotationId(updateGLQuotationWithInsuredCommand.getQuotationId()));
        boolean isVersioningRequire = groupLifeQuotation.requireVersioning();
        groupLifeQuotation = groupLifeQuotationService.updateInsured(groupLifeQuotation, insureds, updateGLQuotationWithInsuredCommand.getUserDetails());
        PremiumDetailDto premiumDetailDto = new PremiumDetailDto(BigDecimal.valueOf(20), 365, BigDecimal.valueOf(5), BigDecimal.valueOf(5), BigDecimal.valueOf(5));
        groupLifeQuotation = groupLifeQuotationService.updateWithPremiumDetail(groupLifeQuotation, premiumDetailDto, updateGLQuotationWithInsuredCommand.getUserDetails());
        if (isVersioningRequire) {
            glQuotationMongoRepository.add(groupLifeQuotation);
        }
        return groupLifeQuotation.getIdentifier().getQuotationId();
    }


    @CommandHandler
    public String updateWithPremiumDetail(UpdateGLQuotationWithPremiumDetailCommand updateGLQuotationWithPremiumDetailCommand) {
        GroupLifeQuotation groupLifeQuotation = glQuotationMongoRepository.load(new QuotationId(updateGLQuotationWithPremiumDetailCommand.getQuotationId()));
        boolean isVersioningRequire = groupLifeQuotation.requireVersioning();
        groupLifeQuotation = populateAnnualBasicPremiumOfInsured(groupLifeQuotation, updateGLQuotationWithPremiumDetailCommand.getUserDetails(), updateGLQuotationWithPremiumDetailCommand.getPremiumDetailDto());
        if (isVersioningRequire) {
            glQuotationMongoRepository.add(groupLifeQuotation);
        }
        return groupLifeQuotation.getIdentifier().getQuotationId();
    }


    private GroupLifeQuotation populateAnnualBasicPremiumOfInsured(GroupLifeQuotation groupLifeQuotation, UserDetails userDetails, PremiumDetailDto premiumDetailDto) {
        if (premiumDetailDto.getPolicyTermValue() != 365) {
            Set<Insured> insureds = groupLifeQuotation.getInsureds();
            insureds = glInsuredFactory.recalculateProratePremiumForInsureds(premiumDetailDto, insureds);
            groupLifeQuotation = groupLifeQuotationService.updateInsured(groupLifeQuotation, insureds, userDetails);
        }
        groupLifeQuotation = groupLifeQuotationService.updateWithPremiumDetail(groupLifeQuotation, premiumDetailDto, userDetails);
        return groupLifeQuotation;
    }

    @CommandHandler
    public GroupLifeQuotation recalculatePremium(GLRecalculatedInsuredPremiumCommand glRecalculatedInsuredPremiumCommand) {
        GroupLifeQuotation groupLifeQuotation = glQuotationRepository.findOne(new QuotationId(glRecalculatedInsuredPremiumCommand.getQuotationId()));
        groupLifeQuotation = populateAnnualBasicPremiumOfInsured(groupLifeQuotation, glRecalculatedInsuredPremiumCommand.getUserDetails(), glRecalculatedInsuredPremiumCommand.getPremiumDetailDto());
        return groupLifeQuotation;

    }


    @CommandHandler
    public void purgeGLQuotation(PurgeGLQuotationCommand purgeGLQuotationCommand) {
        GroupLifeQuotation groupLifeQuotation = glQuotationMongoRepository.load(purgeGLQuotationCommand.getQuotationId());
        groupLifeQuotation.purgeQuotation();
    }

    @CommandHandler
    public void closureGLQuotation(GLQuotationClosureCommand closureGLQuotationCommand) {
        GroupLifeQuotation groupLifeQuotation = glQuotationMongoRepository.load(closureGLQuotationCommand.getQuotationId());
        groupLifeQuotation.declineQuotation();
    }

    @CommandHandler
    public void convertedGLQuotation(GLQuotationConvertedCommand ghQuotationConvertedCommand) {
        GroupLifeQuotation groupLifeQuotation = glQuotationMongoRepository.load(ghQuotationConvertedCommand.getQuotationId());
        groupLifeQuotation.closeQuotation();
    }

    @CommandHandler
    public void shareGLQuotation(ShareGLQuotationCommand shareGLQuotationCommand) {
        GroupLifeQuotation groupLifeQuotation = glQuotationMongoRepository.load(shareGLQuotationCommand.getQuotationId());
        groupLifeQuotation.shareQuotation(LocalDate.now());
    }
}
