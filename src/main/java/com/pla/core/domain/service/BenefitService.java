/*
 * Copyright (c) 3/9/15 10:51 AM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.domain.service;

import com.pla.core.domain.model.Admin;
import com.pla.core.domain.model.Benefit;
import com.pla.core.specification.BenefitIsUpdatable;
import com.pla.core.specification.BenefitNameIsUnique;
import org.nthdimenzion.common.service.JpaRepositoryFactory;
import org.nthdimenzion.ddd.domain.annotations.DomainService;
import org.nthdimenzion.object.utils.IIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * @author: Samir
 * @since 1.0 09/03/2015
 */
@DomainService
public class BenefitService {


    private AdminRoleAdapter adminRoleAdapter;

    private BenefitNameIsUnique benefitNameIsUnique;

    private IIdGenerator idGenerator;

    private BenefitIsUpdatable benefitIsUpdatable;

    @Autowired
    public BenefitService(AdminRoleAdapter adminRoleAdapter, BenefitNameIsUnique benefitNameIsUnique,IIdGenerator idGenerator, BenefitIsUpdatable benefitIsUpdatable) {
        this.adminRoleAdapter = adminRoleAdapter;
        this.benefitNameIsUnique = benefitNameIsUnique;
        this.idGenerator = idGenerator;
        this.benefitIsUpdatable = benefitIsUpdatable;
    }

    public Benefit createBenefit(String benefitName, UserDetails userDetails) {
        String benefitId = idGenerator.nextId();
        Admin admin = adminRoleAdapter.userToAdmin(userDetails);
        Benefit benefit = admin.createBenefit(benefitNameIsUnique, benefitId, benefitName);
        return benefit;
    }

    public Benefit updateBenefit(Benefit benefit, String benefitName, UserDetails userDetails) {
        Admin admin = adminRoleAdapter.userToAdmin(userDetails);
        benefit = admin.updateBenefit(benefit, benefitName, benefitNameIsUnique, benefitIsUpdatable);
        return benefit;

    }

    public Benefit markBenefitAsUsed(Benefit benefit) {
        benefit = benefit.markAsUsed();
        return benefit;
    }

    public Benefit inactivateBenefit(Benefit benefit, UserDetails userDetails) {
        Admin admin = adminRoleAdapter.userToAdmin(userDetails);
        benefit = admin.inactivateBenefit(benefit);
        return benefit;
    }

}
