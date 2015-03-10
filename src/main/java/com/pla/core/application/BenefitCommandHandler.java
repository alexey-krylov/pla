/*
 * Copyright (c) 3/5/15 5:17 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.application;

import com.google.common.base.Preconditions;
import com.pla.core.domain.model.Benefit;
import com.pla.core.domain.service.BenefitService;
import com.pla.sharedkernel.domain.model.BenefitStatus;
import org.axonframework.commandhandling.annotation.CommandHandler;
import org.nthdimenzion.common.service.JpaRepositoryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

/**
 * @author: Samir
 * @since 1.0 05/03/2015
 */
@Component
public class BenefitCommandHandler {

    private JpaRepositoryFactory jpaRepositoryFactory;

    private BenefitService benefitService;

    private Logger logger = LoggerFactory.getLogger(BenefitCommandHandler.class);

    @Autowired
    public BenefitCommandHandler(JpaRepositoryFactory jpaRepositoryFactory, BenefitService benefitService) {
        this.jpaRepositoryFactory = jpaRepositoryFactory;
        this.benefitService = benefitService;
    }

    @CommandHandler
    public void createBenefitHandler(CreateBenefitCommand createBenefitCommand) {
        if (logger.isDebugEnabled()) {
            logger.debug("*****Command Received*****" + createBenefitCommand);
        }
        Benefit benefit = benefitService.createBenefit(createBenefitCommand);
        JpaRepository<Benefit, String> benefitRepository = jpaRepositoryFactory.getCrudRepository(Benefit.class);
        try {
            benefitRepository.save(benefit);
        } catch (RuntimeException e) {
            logger.error("*****Saving benefit failed*****", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @CommandHandler
    public void updateBenefitHandler(UpdateBenefitCommand updateBenefitCommand) {
        if (logger.isDebugEnabled()) {
            logger.debug("*****Command Received*****" + updateBenefitCommand);
        }
        Benefit benefit = benefitService.updateBenefit(updateBenefitCommand);
        JpaRepository<Benefit, String> benefitRepository = jpaRepositoryFactory.getCrudRepository(Benefit.class);
        try {
            benefitRepository.save(benefit);
        } catch (RuntimeException e) {
            logger.error("*****Updating benefit failed*****", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @CommandHandler
    public void markBenefitAsUsedHandler(MarkBenefitAsUsedCommand markBenefitAsUsedCommand) {
        if (logger.isDebugEnabled()) {
            logger.debug("*****Command Received*****" + markBenefitAsUsedCommand);
        }
        Benefit benefit = benefitService.markBenefitAsUsed(markBenefitAsUsedCommand);
        CrudRepository<Benefit, String> benefitRepository = jpaRepositoryFactory.getCrudRepository(Benefit.class);
        try {
            benefitRepository.save(benefit);
        } catch (RuntimeException e) {
            logger.error("*****Marking benefit as used failed*****", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @CommandHandler
    public void inactivateBenefitHandler(InactivateBenefitStatusCommand inactivateBenefitStatusCommand) {
        if (logger.isDebugEnabled()) {
            logger.debug("*****Inactivate Benefit Status Command  Received*****" + inactivateBenefitStatusCommand);
        }
        Preconditions.checkState(BenefitStatus.INACTIVE.equals(inactivateBenefitStatusCommand.getStatus()));
        Benefit benefit = benefitService.inactivateBenefit(inactivateBenefitStatusCommand);
        CrudRepository<Benefit, String> benefitRepository = jpaRepositoryFactory.getCrudRepository(Benefit.class);
        try {
            benefitRepository.save(benefit);
        } catch (RuntimeException e) {
            logger.error("*****Inactivating benefit failed*****", e);
            throw new RuntimeException(e.getMessage());
        }
    }
}
