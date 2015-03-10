/*
 * Copyright (c) 3/9/15 10:51 AM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.domain.service;

import com.pla.core.application.CreateBenefitCommand;
import com.pla.core.domain.model.Admin;
import com.pla.core.domain.model.Benefit;
import com.pla.core.specification.BenefitNameIsUnique;
import org.nthdimenzion.ddd.domain.AbstractDomainFactory;
import org.nthdimenzion.ddd.domain.annotations.DomainFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author: Samir
 * @since 1.0 09/03/2015
 */
@DomainFactory
public class BenefitFactory extends AbstractDomainFactory {


    private AdminRoleAdapter adminRoleAdapter;

    private BenefitNameIsUnique benefitNameIsUnique;

    private Logger logger = LoggerFactory.getLogger(AdminRoleAdapter.class);

    @Autowired
    public BenefitFactory(AdminRoleAdapter adminRoleAdapter, BenefitNameIsUnique benefitNameIsUnique) {
        this.adminRoleAdapter = adminRoleAdapter;
        this.benefitNameIsUnique = benefitNameIsUnique;
    }

    public Benefit createBenefit(CreateBenefitCommand createBenefitCommand) {
        if (logger.isDebugEnabled()) {
            logger.debug("Benefit command received" + createBenefitCommand);
        }
        String benefitId = idGenerator.nextId();
        Admin admin = adminRoleAdapter.userToAdmin(createBenefitCommand.getUserDetails());
        Benefit benefit = admin.createBenefit(benefitNameIsUnique, benefitId, createBenefitCommand.getBenefitName());
        return benefit;
    }
}
