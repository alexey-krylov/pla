/*
 * Copyright (c) 3/5/15 5:17 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.application;

import com.pla.core.domain.model.plan.commission.Commission;
import com.pla.core.domain.service.CommissionService;
import com.pla.sharedkernel.identifier.CommissionId;
import org.axonframework.commandhandling.annotation.CommandHandler;
import org.nthdimenzion.common.service.JpaRepositoryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

/**
 * @author: Samir
 * @since 1.0 05/03/2015
 */
@Component
public class CommissionCommandHandler {

    private JpaRepositoryFactory jpaRepositoryFactory;

    private CommissionService commissionService;

    private static final Logger LOGGER = LoggerFactory.getLogger(CommissionCommandHandler.class);

    @Autowired
    public CommissionCommandHandler(JpaRepositoryFactory jpaRepositoryFactory, CommissionService commissionService) {
        this.jpaRepositoryFactory = jpaRepositoryFactory;
        this.commissionService = commissionService;
    }

    @CommandHandler
    public void createCommissionHandler(CreateCommissionCommand createCommissionCommand) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("*****Create Commission Command Received*****" + createCommissionCommand);
        }
        Commission commission = commissionService.createCommission(createCommissionCommand.getPlanId(), createCommissionCommand.getAvailableFor(), createCommissionCommand.getCommissionType(), createCommissionCommand.getPremiumFee(), createCommissionCommand.getFromDate(), createCommissionCommand.getCommissionTermSet(), createCommissionCommand.getUserDetails());
        JpaRepository<Commission, CommissionId> commissionRepository = jpaRepositoryFactory.getCrudRepository(Commission.class);
        try {
            commissionRepository.save(commission);
        } catch (RuntimeException e) {
            LOGGER.error("*****Saving Commission failed*****", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @CommandHandler
    public void updateCommissionHandler(UpdateCommissionCommand updateCommissionCommand) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("*****Create Commission Command Received*****" + updateCommissionCommand);
        }
        JpaRepository<Commission, CommissionId> commissionRepository = jpaRepositoryFactory.getCrudRepository(Commission.class);
        Commission commission = commissionRepository.findOne(updateCommissionCommand.getCommissionId());
        Commission updatedCommission = commissionService.updateCommissionTerm(updateCommissionCommand.getPlanId(), commission, updateCommissionCommand.getCommissionTermSet(), updateCommissionCommand.getUserDetails());
        try {
            commissionRepository.save(updatedCommission);
        } catch (RuntimeException e) {
            LOGGER.error("*****Saving Commission failed*****", e);
            throw new RuntimeException(e.getMessage());
        }
    }

}
