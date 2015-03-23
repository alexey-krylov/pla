package com.pla.core.domain.event;

import com.pla.core.domain.query.PlanCoverageCompositeKey;
import com.pla.core.domain.query.PlanCoverageEntity;
import com.pla.core.domain.query.PlanEntry;
import com.pla.sharedkernel.domain.event.*;
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
    private SimpleJpaRepository<PlanCoverageEntity, PlanCoverageCompositeKey> planCoverageRepository;

    private Logger logger = LoggerFactory.getLogger(PlanEventListener.class);

    @EventHandler
    public void onPlanDetailConfigured(PlanDetailConfigured event) {
        if (logger.isDebugEnabled()) {
            logger.debug(" inserting record into Plan Entry.");
        }
        PlanEntry planEntry = new PlanEntry();
        planEntry.setClientType(event.getClientType());
        planEntry.setApplicableRelationships(event.getApplicableRelationships());
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
    public void handle(PlanDetailChanged event) {
        if (logger.isDebugEnabled()) {
            logger.debug(" updating record into Plan Entry.");
        }
        PlanEntry planEntry = planEntryRepository.findOne(event.getPlanId().toString());
        planEntry.setClientType(event.getClientType());
        planEntry.setApplicableRelationships(event.getApplicableRelationships());
        planEntry.setLaunchDate(event.getLaunchDate());
        planEntry.setWithdrawalDate(event.getWithdrawalDate());
        planEntry.setPlanCode(event.getPlanCode());
        planEntry.setPlanName(event.getPlanName());
        planEntry.setLineOfBusinessId(event.getLineOfBusinessId().toString());
        planEntry.setPlanType(event.getPlanType());
        planEntryRepository.save(planEntry);
    }

    @EventHandler
    public void handle(PolicyTermConfigured event) {
        if (logger.isDebugEnabled()) {
            logger.debug(" updating record into Plan Entry.");
        }
        PlanEntry planEntry = planEntryRepository.findOne(event.getPlanId().toString());
        planEntry.setPolicyTermType(event.getPolicyTermType());
        planEntry.setPolicyTerm(event.getValidTerms());
        planEntryRepository.save(planEntry);
    }


    @EventHandler
    public void handle(PlanCoverageTermConfigured event) {
        if (logger.isDebugEnabled()) {
            logger.debug(" Updating Plan Coverage Term .");
        }
        PlanEntry planEntry = planEntryRepository.findOne(event.getPlanId().toString());
        planEntry.setCoverageTermType(event.getCoverageTermType());
        planEntry.setCoverageTerm(event.getValidTerms());
        planEntryRepository.save(planEntry);
    }

    @EventHandler
    public void handle(PlanCoverageRegularTermConfigured event) {
        if (logger.isDebugEnabled()) {
            logger.debug(" Updating Plan Coverage Term .");
        }
        PlanEntry planEntry = planEntryRepository.findOne(event.getPlanId().toString());
        planEntry.setCoverageTermType(event.getCoverageTermType());
        planEntry.setCoverageTerm(event.getValidTerms());
        planEntryRepository.save(planEntry);
    }

    @EventHandler
    public void handle(PremiumTermConfigured event) {
        if (logger.isDebugEnabled()) {
            logger.debug(" updating record into Plan Entry.");
        }
        PlanEntry planEntry = planEntryRepository.findOne(event.getPlanId().toString());
        planEntry.setPremiumTermType(event.getTermType());
        planEntry.setPremiumTerm(event.getValidTerms());
        planEntryRepository.save(planEntry);
    }

    @EventHandler
    public void handle(SumAssuredConfigured event) {
        if (logger.isDebugEnabled()) {
            logger.debug(" updating record into Plan Entry.");
        }
        PlanEntry planEntry = planEntryRepository.findOne(event.getPlanId().toString());
        planEntry.setSumAssuredType(event.getSumAssuredType());
        planEntry.setSumAssured(event.getAssuredValues());
        planEntry.setMinSumAssured(event.getMinSumAssuredAmount());
        planEntry.setMaxSumAssured(event.getMaxSumAssuredAmount());
        planEntry.setMultiplesOf(event.getMultiplesOf());
        planEntryRepository.save(planEntry);
    }


    @EventHandler
    public void handle(PlanCoverageSumAssuredConfigured event) {
        if (logger.isDebugEnabled()) {
            logger.debug(" updating record into Plan Entry.");
        }
        PlanCoverageEntity entry = planCoverageRepository.findOne(new PlanCoverageCompositeKey(event.getPlanId().toString(),
                event.getCoverageId().toString()));
        if (entry == null) {
            entry = new PlanCoverageEntity(event.getPlanId().toString(),
                    event.getCoverageId().toString(), event.getSumAssuredType(),
                    event.getMinSumAssuredAmount(), event.getMaxSumAssuredAmount(), event.getMultiplesOf(),
                    event.getAssuredValues(), event.getPercentage());
        } else {
            entry.setMultiplesOf(event.getMultiplesOf());
            entry.setMinSumAssured(event.getMinSumAssuredAmount());
            entry.setMaxSumAssured(event.getMaxSumAssuredAmount());
            entry.setSumAssuredType(event.getSumAssuredType());
            entry.setSumAssured(event.getAssuredValues());
            entry.setPercentage(event.getPercentage());
        }
        planCoverageRepository.save(entry);
    }

    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        planEntryRepository = new SimpleJpaRepository<PlanEntry, String>(PlanEntry.class, entityManager);
        planCoverageRepository = new SimpleJpaRepository<PlanCoverageEntity,
                PlanCoverageCompositeKey>
                (PlanCoverageEntity.class, entityManager);

    }

}