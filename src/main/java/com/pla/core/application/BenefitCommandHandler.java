/*
 * Copyright (c) 3/5/15 5:17 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.application;

import com.pla.core.application.exception.BenefitApplicationException;
import com.pla.core.domain.model.Benefit;
import com.pla.sharedkernel.identifier.BenefitId;
import com.pla.core.domain.service.BenefitService;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(BenefitCommandHandler.class);

    @Autowired
    public BenefitCommandHandler(JpaRepositoryFactory jpaRepositoryFactory, BenefitService benefitService) {
        this.jpaRepositoryFactory = jpaRepositoryFactory;
        this.benefitService = benefitService;
    }

    @CommandHandler
    public void createBenefitHandler(CreateBenefitCommand createBenefitCommand) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("*****Command Received*****" + createBenefitCommand);
        }
        JpaRepository<Benefit, String> benefitRepository = jpaRepositoryFactory.getCrudRepository(Benefit.class);
        try {
            Benefit benefit = benefitService.createBenefit(createBenefitCommand.getBenefitName(), createBenefitCommand.getUserDetails());
            benefitRepository.save(benefit);
        } catch (RuntimeException e) {
            LOGGER.error("*****Saving benefit failed*****", e);
            throw new BenefitApplicationException(e.getMessage());
        }
    }

    @CommandHandler
    public void updateBenefitHandler(UpdateBenefitCommand updateBenefitCommand) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("*****Command Received*****" + updateBenefitCommand);
        }
        JpaRepository<Benefit, BenefitId> benefitRepository = jpaRepositoryFactory.getCrudRepository(Benefit.class);
        BenefitId benefitId = new BenefitId(updateBenefitCommand.getBenefitId());
        Benefit benefit = benefitRepository.findOne(benefitId);
        try {
            benefit = benefitService.updateBenefit(benefit, updateBenefitCommand.getBenefitName(), updateBenefitCommand.getUserDetails());
            benefitRepository.save(benefit);
        } catch (RuntimeException e) {
            LOGGER.error("*****Updating benefit failed*****", e);
            throw new BenefitApplicationException(e.getMessage());
        }
    }

    @CommandHandler
    public void markBenefitAsUsedHandler(MarkBenefitAsUsedCommand markBenefitAsUsedCommand) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("*****Command Received*****" + markBenefitAsUsedCommand);
        }
        CrudRepository<Benefit, BenefitId> benefitRepository = jpaRepositoryFactory.getCrudRepository(Benefit.class);
        BenefitId benefitId = new BenefitId(markBenefitAsUsedCommand.getBenefitId());
        Benefit benefit = benefitRepository.findOne(benefitId);
        try {
            benefit = benefitService.markBenefitAsUsed(benefit);
            benefitRepository.save(benefit);
        } catch (RuntimeException e) {
            LOGGER.error("*****Marking benefit as used failed*****", e);
            throw new BenefitApplicationException(e.getMessage());
        }
    }

    @CommandHandler
    public void inactivateBenefitHandler(InactivateBenefitCommand inactivateBenefitCommand) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("*****Inactivate Benefit Status Command  Received*****" + inactivateBenefitCommand);
        }
        CrudRepository<Benefit, BenefitId> benefitRepository = jpaRepositoryFactory.getCrudRepository(Benefit.class);
        BenefitId benefitId = new BenefitId(inactivateBenefitCommand.getBenefitId());
        Benefit benefit = benefitRepository.findOne(benefitId);
        try {
            benefit = benefitService.inactivateBenefit(benefit, inactivateBenefitCommand.getUserDetails());
            benefitRepository.save(benefit);
        } catch (RuntimeException e) {
            LOGGER.error("*****Inactivating benefit failed*****", e);
            throw new BenefitApplicationException(e.getMessage());
        }
    }
}
