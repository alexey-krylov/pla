package com.pla.core.application.plan.premium;

import com.pla.core.domain.model.plan.premium.Premium;
import com.pla.core.repository.PremiumRepository;
import com.pla.sharedkernel.identifier.CoverageId;
import com.pla.sharedkernel.identifier.PlanId;
import com.pla.sharedkernel.identifier.PremiumId;
import org.axonframework.commandhandling.annotation.CommandHandler;
import org.nthdimenzion.object.utils.IIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.nthdimenzion.utils.UtilValidator.isEmpty;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Created by Samir on 4/4/2015.
 */
@Component
public class PremiumCommandHandler {

    private PremiumRepository premiumRepository;

    private IIdGenerator idGenerator;

    @Autowired
    public PremiumCommandHandler(PremiumRepository premiumRepository, IIdGenerator idGenerator) {
        this.premiumRepository = premiumRepository;
        this.idGenerator = idGenerator;
    }

    @CommandHandler
    public void createPremiumHandler(CreatePremiumCommand createPremiumCommand) {
        Premium premium = null;
        if (isNotEmpty(createPremiumCommand.getPlanId()) && isNotEmpty(createPremiumCommand.getCoverageId())) {
            premium = premiumRepository.findByPlanAndCoverageId(new PlanId(createPremiumCommand.getPlanId()), new CoverageId(createPremiumCommand.getCoverageId()));
        } else if (isNotEmpty(createPremiumCommand.getPlanId()) && isEmpty(createPremiumCommand.getCoverageId())) {
            premium = premiumRepository.findByPlanId(new PlanId(createPremiumCommand.getPlanId()));
        }
        if (premium != null) {
            premium = premium.expirePremium(createPremiumCommand.getEffectiveFrom().minusDays(1));
            premiumRepository.save(premium);
        }

        PremiumId premiumId = new PremiumId(idGenerator.nextId());
        PlanId planId = new PlanId(createPremiumCommand.getPlanId());
        CoverageId coverageId = new CoverageId(createPremiumCommand.getCoverageId());
        Premium newPremium = isNotEmpty(createPremiumCommand.getCoverageId()) ? Premium.createPremiumWithPlanAndCoverage(premiumId, planId, coverageId, createPremiumCommand.getEffectiveFrom(), createPremiumCommand.getPremiumLineItem(), createPremiumCommand.getPremiumFactor(), createPremiumCommand.getPremiumRate())
                : Premium.createPremiumWithPlan(premiumId, planId, createPremiumCommand.getEffectiveFrom(), createPremiumCommand.getPremiumLineItem(), createPremiumCommand.getPremiumFactor(), createPremiumCommand.getPremiumRate());
        premiumRepository.save(newPremium);
    }

}
