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
import com.pla.core.specification.BenefitCodeIsUnique;
import com.pla.core.specification.BenefitIsAssociatedWithCoverage;
import com.pla.core.specification.BenefitNameIsUnique;
import com.pla.sharedkernel.util.SequenceGenerator;
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

    private BenefitCodeIsUnique benefitCodeIsUnique;

    private IIdGenerator idGenerator;

    private BenefitIsAssociatedWithCoverage benefitIsAssociatedWithCoverage;

    private SequenceGenerator sequenceGenerator;

    @Autowired
    public BenefitService(AdminRoleAdapter adminRoleAdapter, BenefitNameIsUnique benefitNameIsUnique, IIdGenerator idGenerator, BenefitIsAssociatedWithCoverage benefitIsAssociatedWithCoverage, BenefitCodeIsUnique benefitCodeIsUnique, SequenceGenerator sequenceGenerator) {
        this.adminRoleAdapter = adminRoleAdapter;
        this.benefitNameIsUnique = benefitNameIsUnique;
        this.idGenerator = idGenerator;
        this.benefitIsAssociatedWithCoverage = benefitIsAssociatedWithCoverage;
        this.benefitCodeIsUnique = benefitCodeIsUnique;
        this.sequenceGenerator = sequenceGenerator;
    }

    public Benefit createBenefit(String name, UserDetails userDetails, String benefitCode) {
        String benefitId = idGenerator.nextId();
        Admin admin = adminRoleAdapter.userToAdmin(userDetails);
        BenefitDto benefitDto = new BenefitDto(benefitId, name, benefitCode);
        boolean isBenefitNameAndCodeIsUnique = benefitCodeIsUnique.And(benefitNameIsUnique).isSatisfiedBy(benefitDto);
        String benefitCodeSequence = sequenceGenerator.getSequence(Benefit.class);
        Benefit benefit = admin.createBenefit(isBenefitNameAndCodeIsUnique, benefitId, name, benefitCodeSequence);
        return benefit;
    }

    public Benefit updateBenefit(Benefit benefit, String newBenefitName, UserDetails userDetails, String benefitCode) {
        Admin admin = adminRoleAdapter.userToAdmin(userDetails);
        BenefitDto benefitDto = new BenefitDto(benefit.getBenefitId().getBenefitId(), newBenefitName, benefitCode);
        if (!(benefitCodeIsUnique.isSatisfiedBy(benefitDto) && benefitNameIsUnique.isSatisfiedBy(benefitDto)))
            throw new BenefitDomainException("Benefit already described");
        boolean isBenefitUpdatable = benefitIsAssociatedWithCoverage.And(benefitNameIsUnique).isSatisfiedBy(benefitDto);
        Benefit updatedBenefit = admin.updateBenefit(benefit, newBenefitName, isBenefitUpdatable, benefitCode);
        return updatedBenefit;
    }

    public Benefit markBenefitAsUsed(Benefit benefit) {
        Benefit updatedBenefit = benefit.markAsUsed();
        return updatedBenefit;
    }

    public Benefit inactivateBenefit(Benefit benefit, UserDetails userDetails) {
        Admin admin = adminRoleAdapter.userToAdmin(userDetails);
        BenefitDto benefitDto = new BenefitDto(benefit.getBenefitId().getBenefitId(), benefit.getBenefitName().getBenefitName(), benefit.getBenefitCode());
        boolean isBenefitUpdatable = benefitIsAssociatedWithCoverage.isSatisfiedBy(benefitDto);
        Benefit updatedBenefit = admin.inactivateBenefit(benefit, isBenefitUpdatable);
        return updatedBenefit;
    }

}
