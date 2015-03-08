/*
 * Copyright (c) 3/5/15 5:17 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.application;

import com.pla.core.domain.model.Admin;
import com.pla.core.domain.model.Benefit;
import com.pla.core.domain.service.AdminRoleAdapter;
import org.axonframework.commandhandling.annotation.CommandHandler;
import org.nthdimenzion.common.service.JpaRepositoryFactory;
import org.nthdimenzion.object.utils.IIdGenerator;
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

    private AdminRoleAdapter adminRoleAdapter;

    private IIdGenerator idGenerator;

    private Logger logger = LoggerFactory.getLogger(CoreCommandHandler.class);

    @Autowired
    public CoreCommandHandler(JpaRepositoryFactory jpaRepositoryFactory, AdminRoleAdapter adminRoleAdapter, IIdGenerator idGenerator) {
        this.jpaRepositoryFactory = jpaRepositoryFactory;
        this.adminRoleAdapter = adminRoleAdapter;
        this.idGenerator = idGenerator;

    }

    @CommandHandler
    public String createBenefitCommandHandler(CreateBenefitCommand createBenefitCommand) {
        if (logger.isDebugEnabled()) {
            logger.debug("**** \nCommand Received****" + createBenefitCommand);
        }
        Admin admin = adminRoleAdapter.userToAdmin(createBenefitCommand.getUserDetails());
        String benefitId = idGenerator.nextId();
        Benefit benefit = admin.createBenefit(benefitId, createBenefitCommand.getBenefitName());
        CrudRepository<Benefit, String> benefitRepository = jpaRepositoryFactory.getCrudRepository(Benefit.class);
        try {
            benefitRepository.save(benefit);
        } catch (RuntimeException e) {
            logger.error("******* Saving benefit failed\n", e);
            return null;
        }
        return benefitId;
    }
}
