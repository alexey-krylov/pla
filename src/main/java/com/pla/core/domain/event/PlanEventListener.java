package com.pla.core.domain.event;

import com.pla.core.domain.model.plan.PlanDetail;
import com.pla.core.domain.model.plan.PlanDetailChanged;
import com.pla.core.domain.model.plan.PlanDetailConfigured;
import com.pla.core.domain.model.plan.PlanEntry;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.nthdimenzion.common.service.JpaRepositoryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

/**
 * @author: pradyumna
 * @since 1.0 19/03/2015
 */
@Component
public class PlanEventListener {

    private JpaRepository<PlanEntry, String> planEntryRepository;
    private Logger logger = LoggerFactory.getLogger(PlanEventListener.class);

    @Autowired
    public PlanEventListener(JpaRepositoryFactory jpaRepositoryFactory) {
        planEntryRepository = jpaRepositoryFactory.getCrudRepository(PlanEntry.class);
    }

    @EventHandler
    public void onPlanDetailConfigured(PlanDetailConfigured event) {
        if (logger.isDebugEnabled()) {
            logger.debug(" inserting record into Plan Entry.");
        }

        PlanDetail planDetail = event.getPlanDetail();
        PlanEntry planEntry = new PlanEntry();
        planEntry.setClientType(planDetail.getClientType());
        planEntry.setApplicableRelationships(planDetail.getApplicableRelationships());
        planEntry.setEndorsementTypes(planDetail.getEndorsementTypes());
        planEntry.setIdentifier(event.getPlanId().toString());
        planEntry.setLaunchDate(planDetail.getLaunchDate());
        planEntry.setWithdrawalDate(planDetail.getWithdrawalDate());
        planEntry.setPlanCode(planDetail.getPlanCode());
        planEntry.setPlanName(planDetail.getPlanName());
        planEntry.setLineOfBusinessId(planDetail.getLineOfBusinessId().toString());
        planEntry.setPlanType(planDetail.getPlanType());
        planEntryRepository.save(planEntry);
    }

    @EventHandler
    public void onPlanDetailChanged(PlanDetailChanged event) {
        if (logger.isDebugEnabled()) {
            logger.debug(" updating record into Plan Entry.");
        }
        PlanEntry planEntry = planEntryRepository.findOne(event.getPlanId().toString());
        PlanDetail planDetail = event.getPlanDetail();
        planEntry.setClientType(planDetail.getClientType());
        planEntry.setApplicableRelationships(planDetail.getApplicableRelationships());
        planEntry.setEndorsementTypes(planDetail.getEndorsementTypes());
        planEntry.setLaunchDate(planDetail.getLaunchDate());
        planEntry.setWithdrawalDate(planDetail.getWithdrawalDate());
        planEntry.setPlanCode(planDetail.getPlanCode());
        planEntry.setPlanName(planDetail.getPlanName());
        planEntry.setLineOfBusinessId(planDetail.getLineOfBusinessId().toString());
        planEntry.setPlanType(planDetail.getPlanType());
        planEntryRepository.save(planEntry);
    }

}
