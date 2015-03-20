package com.pla.core.domain.event;

import com.pla.core.domain.model.plan.PlanDetailChanged;
import com.pla.core.domain.model.plan.PlanDetailConfigured;
import com.pla.core.domain.model.plan.PlanEntry;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * @author: pradyumna
 * @since 1.0 19/03/2015
 */
@Component
public class PlanEventListener {


    private SimpleJpaRepository<PlanEntry, String> planEntryRepository;
    private Logger logger = LoggerFactory.getLogger(PlanEventListener.class);

    @EventHandler
    public void onPlanDetailConfigured(PlanDetailConfigured event) {
        if (logger.isDebugEnabled()) {
            logger.debug(" inserting record into Plan Entry.");
        }

        PlanEntry planEntry = new PlanEntry();
        planEntry.setClientType(event.getClientType());
        planEntry.setApplicableRelationships(event.getApplicableRelationships());
        planEntry.setEndorsementTypes(event.getEndorsementTypes());
        planEntry.setIdentifier(event.getPlanId().toString());
        planEntry.setLaunchDate(event.getLaunchDate());
        planEntry.setWithdrawalDate(event.getWithdrawalDate());
        planEntry.setPlanCode(event.getPlanCode());
        planEntry.setPlanName(event.getPlanName());
        planEntry.setLineOfBusinessId(event.getLineOfBusinessId().toString());
        planEntry.setPlanType(event.getPlanType());
        planEntryRepository.save(planEntry);
    }

    @EventHandler
    public void onPlanDetailChanged(PlanDetailChanged event) {
        if (logger.isDebugEnabled()) {
            logger.debug(" updating record into Plan Entry.");
        }
        PlanEntry planEntry = planEntryRepository.findOne(event.getPlanId().toString());
        planEntry.setClientType(event.getClientType());
        planEntry.setApplicableRelationships(event.getApplicableRelationships());
        planEntry.setEndorsementTypes(event.getEndorsementTypes());
        planEntry.setLaunchDate(event.getLaunchDate());
        planEntry.setWithdrawalDate(event.getWithdrawalDate());
        planEntry.setPlanCode(event.getPlanCode());
        planEntry.setPlanName(event.getPlanName());
        planEntry.setLineOfBusinessId(event.getLineOfBusinessId().toString());
        planEntry.setPlanType(event.getPlanType());
        planEntryRepository.save(planEntry);
    }

    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        planEntryRepository = new SimpleJpaRepository<PlanEntry, String>(PlanEntry.class, entityManager);
    }

}
