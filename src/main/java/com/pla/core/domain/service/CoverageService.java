package com.pla.core.domain.service;

import com.pla.core.domain.model.Admin;
import com.pla.core.domain.model.Benefit;
import com.pla.core.domain.model.Coverage;
import com.pla.core.domain.model.CoverageName;
import com.pla.core.specification.CoverageCodeIsUnique;
import com.pla.core.specification.CoverageNameIsUnique;
import org.nthdimenzion.ddd.domain.annotations.DomainService;
import org.nthdimenzion.object.utils.IIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: Admin
 * Date: 3/22/15
 * Time: 8:46 PM
 * To change this template use File | Settings | File Templates.
 */
@DomainService
public class CoverageService {

    private AdminRoleAdapter adminRoleAdapter;

    private CoverageNameIsUnique coverageNameIsUnique;

    private CoverageCodeIsUnique  coverageCodeIsUnique;

    private IIdGenerator idGenerator;


    @Autowired
    public CoverageService(AdminRoleAdapter adminRoleAdapter, CoverageNameIsUnique coverageNameIsUnique,CoverageCodeIsUnique coverageCodeIsUnique, IIdGenerator idGenerator) {
        this.adminRoleAdapter = adminRoleAdapter;
        this.coverageNameIsUnique = coverageNameIsUnique;
        this.coverageCodeIsUnique = coverageCodeIsUnique;
        this.idGenerator = idGenerator;
    }

    public Coverage createCoverage(String name,String coverageCode,String description,Set<Benefit> benefitSet, UserDetails userDetails) {
        String coverageId = idGenerator.nextId();
        Admin admin = adminRoleAdapter.userToAdmin(userDetails);
        CoverageName coverageName  = new CoverageName(name);
        boolean isCoverageNameUnique = coverageNameIsUnique.isSatisfiedBy(coverageName);
        boolean isCoverageCodeIsUnique =  coverageCodeIsUnique.isSatisfiedBy(coverageCode);
        Coverage coverage = admin.createCoverage(isCoverageNameUnique,isCoverageCodeIsUnique, coverageId, name,coverageCode,description,benefitSet);
        return coverage;
    }

    public Coverage updateCoverage(Coverage coverage, String newBenefitName,String newCoverageCode,String description,Set<Benefit> benefits, UserDetails userDetails) {
        Admin admin = adminRoleAdapter.userToAdmin(userDetails);
        CoverageName coverageName  = new CoverageName(newBenefitName);
        boolean isCoverageNameUnique = coverageNameIsUnique.isSatisfiedBy(coverageName);
        boolean isCoverageCodeIsUnique = coverageCodeIsUnique.isSatisfiedBy(newCoverageCode);
        Coverage updatedCoverage = admin.updateCoverage(coverage, newBenefitName,newCoverageCode,description, benefits,isCoverageNameUnique,isCoverageCodeIsUnique);
        return updatedCoverage;

    }

    public Coverage markCoverageAsUsed(Coverage coverage) {
        return coverage.markAsUsed();
    }

    public Coverage inactivateCoverage(Coverage coverage, UserDetails userDetails) {
        Admin admin = adminRoleAdapter.userToAdmin(userDetails);
        return admin.inactivateCoverage(coverage);
    }

}

