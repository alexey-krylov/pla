package com.pla.core.presentation.command;

import com.pla.core.domain.model.plan.Plan;
import com.pla.core.domain.model.plan.PlanBuilder;
import com.pla.core.domain.model.plan.PlanDetail;
import com.pla.core.domain.model.plan.PlanDetailBuilder;
import org.axonframework.commandhandling.annotation.CommandHandler;
import org.springframework.stereotype.Component;

/**
 * @author: pradyumna
 * @since 1.0 23/03/2015
 */
@Component
public class PlanCommandHandler {

    @CommandHandler
    public void handle(CreatePlanCommand command) {
        PlanDetailBuilder pdBuilder = PlanDetail.builder();
        Detail dtl = command.getPlanDetail();
        pdBuilder.withPlanName(dtl.getPlanName())
                .withTaxApplicable(dtl.isTaxApplicable())
                .withApplicableRelationships(dtl.getApplicableRelationships())
                .withClientType(dtl.getClientType())
                .withEndorsementTypes(dtl.getEndorsementTypes())
                .withFreeLookPeriod(dtl.getFreeLookPeriod())
                .withLaunchDate(dtl.getLaunchDate())
                .withWithdrawalDate(dtl.getWithdrawalDate())
                .withLineOfBusinessId(dtl.getLineOfBusinessId())
                .withMaxEntryAge(dtl.getMaxEntryAge())
                .withMinEntryAge(dtl.getMinEntryAge())
                .withPlanCode(dtl.getPlanCode())
                .withPlanType(dtl.getPlanType())
                .withSurrenderAfter(dtl.getSurrenderAfter());
        PlanDetail pd = pdBuilder.build();
        System.out.println(pd);
        PlanBuilder planBuilder = Plan.builder();
        planBuilder.withPlanDetail(pd);
    }
}
