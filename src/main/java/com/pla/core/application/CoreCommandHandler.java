/*
 * Copyright (c) 3/5/15 5:17 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.application;

import com.pla.core.domain.model.Benefit;
import com.pla.core.domain.service.BenefitFactory;
import org.axonframework.commandhandling.annotation.CommandHandler;
import org.nthdimenzion.common.service.JpaRepositoryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

/**
 * @author: Samir
 * @since 1.0 05/03/2015
 */
@Component
public class CoreCommandHandler {

    private JpaRepositoryFactory jpaRepositoryFactory;

    private BenefitFactory benefitFactory;

    private Logger logger = LoggerFactory.getLogger(CoreCommandHandler.class);

    @Autowired
    public CoreCommandHandler(JpaRepositoryFactory jpaRepositoryFactory, BenefitFactory benefitFactory) {
        this.jpaRepositoryFactory = jpaRepositoryFactory;
        this.benefitFactory = benefitFactory;
    }

    @CommandHandler
    public void createBenefitHandler(CreateBenefitCommand createBenefitCommand) {
        if (logger.isDebugEnabled()) {
            logger.debug("*****Command Received*****" + createBenefitCommand);
        }
        Benefit benefit = benefitFactory.createBenefit(createBenefitCommand);
        CrudRepository<Benefit, String> benefitRepository = jpaRepositoryFactory.getCrudRepository(Benefit.class);
        try {
            benefitRepository.save(benefit);
        } catch (RuntimeException e) {
            logger.error("*****Saving benefit failed*****", e);
            throw new RuntimeException(e.getMessage());
        }
    }
}
