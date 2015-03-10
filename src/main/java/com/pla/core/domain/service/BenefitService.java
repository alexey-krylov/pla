/*
 * Copyright (c) 3/9/15 10:51 AM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.domain.service;

import com.pla.core.application.InactivateBenefitStatusCommand;
import com.pla.core.application.MarkBenefitAsUsedCommand;
import com.pla.core.application.CreateBenefitCommand;
import com.pla.core.application.UpdateBenefitCommand;
import com.pla.core.domain.model.Admin;
import com.pla.core.domain.model.Benefit;
import com.pla.core.specification.BenefitNameIsUnique;
import org.nthdimenzion.common.service.JpaRepositoryFactory;
import org.nthdimenzion.ddd.domain.AbstractDomainFactory;
import org.nthdimenzion.ddd.domain.annotations.DomainFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;

/**
 * @author: Samir
 * @since 1.0 09/03/2015
 */
@DomainFactory
public class BenefitService extends AbstractDomainFactory {


    private AdminRoleAdapter adminRoleAdapter;

    private BenefitNameIsUnique benefitNameIsUnique;

    private JpaRepositoryFactory jpaRepositoryFactory;

    private Logger logger = LoggerFactory.getLogger(AdminRoleAdapter.class);

    @Autowired
    public BenefitService(AdminRoleAdapter adminRoleAdapter, BenefitNameIsUnique benefitNameIsUnique, JpaRepositoryFactory jpaRepositoryFactory) {
        this.adminRoleAdapter = adminRoleAdapter;
        this.benefitNameIsUnique = benefitNameIsUnique;
        this.jpaRepositoryFactory = jpaRepositoryFactory;
    }

    public Benefit createBenefit(CreateBenefitCommand createBenefitCommand) {
        String benefitId = idGenerator.nextId();
        Admin admin = adminRoleAdapter.userToAdmin(createBenefitCommand.getUserDetails());
        Benefit benefit = admin.createBenefit(benefitNameIsUnique, benefitId, createBenefitCommand.getBenefitName());
        return benefit;
    }

    public Benefit updateBenefit(UpdateBenefitCommand updateBenefitCommand) {
        String benefitId = updateBenefitCommand.getBenefitId();
        Admin admin = adminRoleAdapter.userToAdmin(updateBenefitCommand.getUserDetails());
        Benefit benefit = getBenefit(benefitId);
        benefit = admin.updateBenefit(benefit, updateBenefitCommand.getBenefitName());
        return benefit;

    }

    public Benefit markBenefitAsUsed(MarkBenefitAsUsedCommand markBenefitAsUsedCommand) {
        String benefitId = markBenefitAsUsedCommand.getBenefitId();
        Benefit benefit = getBenefit(benefitId);
        benefit = benefit.markAsUsed();
        return benefit;
    }

    public Benefit inactivateBenefit(InactivateBenefitStatusCommand inactivateBenefitStatusCommand) {
        String benefitId = inactivateBenefitStatusCommand.getBenefitId();
        Admin admin = adminRoleAdapter.userToAdmin(inactivateBenefitStatusCommand.getUserDetails());
        Benefit benefit = getBenefit(benefitId);
        benefit = admin.inactivateBenefit(benefit);
        return benefit;
    }

    private Benefit getBenefit(String benefitId) {
        CrudRepository<Benefit, String> benefitRepository = jpaRepositoryFactory.getCrudRepository(Benefit.class);
        Benefit benefit = benefitRepository.findOne(benefitId);
        return benefit;
    }
}
