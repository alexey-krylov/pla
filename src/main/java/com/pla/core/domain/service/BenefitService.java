/*
 * Copyright (c) 3/9/15 10:51 AM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.domain.service;

import com.pla.core.domain.exception.BenefitDomainException;
import com.pla.core.domain.model.Admin;
import com.pla.core.domain.model.Benefit;
import com.pla.core.dto.BenefitDto;
import com.pla.core.specification.BenefitIsAssociatedWithCoverage;
import com.pla.core.specification.BenefitNameIsUnique;
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

    private BenefitIsAssociatedWithCoverage benefitIsAssociatedWithCoverage;

    @Autowired
    public BenefitService(AdminRoleAdapter adminRoleAdapter, BenefitNameIsUnique benefitNameIsUnique, IIdGenerator idGenerator, BenefitIsAssociatedWithCoverage benefitIsAssociatedWithCoverage) {
        this.adminRoleAdapter = adminRoleAdapter;
        this.benefitNameIsUnique = benefitNameIsUnique;
        this.idGenerator = idGenerator;
        this.benefitIsAssociatedWithCoverage = benefitIsAssociatedWithCoverage;
    }

    public Benefit createBenefit(String name, UserDetails userDetails) {
        String benefitId = idGenerator.nextId();
        Admin admin = adminRoleAdapter.userToAdmin(userDetails);
        BenefitDto benefitDto = new BenefitDto(benefitId, name);
        boolean isBenefitNameUnique = benefitNameIsUnique.isSatisfiedBy(benefitDto);
        Benefit benefit = admin.createBenefit(isBenefitNameUnique, benefitId, name);
        return benefit;
    }

    public Benefit updateBenefit(Benefit benefit, String newBenefitName, UserDetails userDetails) {
        Admin admin = adminRoleAdapter.userToAdmin(userDetails);
        BenefitDto  benefitDto = new BenefitDto(benefit.getBenefitId().getBenefitId(), newBenefitName);
        boolean isBenefitNameUnique = benefitNameIsUnique.isSatisfiedBy(benefitDto);
        if (!isBenefitNameUnique)
            throw new BenefitDomainException("Benefit already described");
        boolean isBenefitUpdatable = benefitIsAssociatedWithCoverage.And(benefitNameIsUnique).isSatisfiedBy(benefitDto);
        Benefit updatedBenefit = admin.updateBenefit(benefit, newBenefitName, isBenefitUpdatable);
        return updatedBenefit;
    }

    public Benefit markBenefitAsUsed(Benefit benefit) {
        Benefit updatedBenefit = benefit.markAsUsed();
        return updatedBenefit;
    }

    public Benefit inactivateBenefit(Benefit benefit, UserDetails userDetails) {
        Admin admin = adminRoleAdapter.userToAdmin(userDetails);
        BenefitDto benefitDto = new BenefitDto(benefit.getBenefitId().getBenefitId(), benefit.getBenefitName().getBenefitName());
        boolean isBenefitUpdatable = benefitIsAssociatedWithCoverage.isSatisfiedBy(benefitDto);
        Benefit updatedBenefit = admin.inactivateBenefit(benefit,isBenefitUpdatable);
        return updatedBenefit;
    }

}
