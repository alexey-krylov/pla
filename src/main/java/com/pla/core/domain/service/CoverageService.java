package com.pla.core.domain.service;

import com.pla.core.domain.model.Admin;
import com.pla.core.domain.model.Benefit;
import com.pla.core.domain.model.Coverage;
import com.pla.core.domain.model.CoverageName;
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

    private IIdGenerator idGenerator;


    @Autowired
    public CoverageService(AdminRoleAdapter adminRoleAdapter, CoverageNameIsUnique coverageNameIsUnique, IIdGenerator idGenerator) {
        this.adminRoleAdapter = adminRoleAdapter;
        this.coverageNameIsUnique = coverageNameIsUnique;
        this.idGenerator = idGenerator;
    }

    public Coverage createCoverage(String name,String description,Set<Benefit> benefitSet, UserDetails userDetails) {
        String coverageId = idGenerator.nextId();
        Admin admin = adminRoleAdapter.userToAdmin(userDetails);
        CoverageName coverageName  = new CoverageName(name);
        boolean isCoverageNameUnique = coverageNameIsUnique.isSatisfiedBy(coverageName);
        Coverage coverage = admin.createCoverage(isCoverageNameUnique, coverageId, name,description,benefitSet);
        return coverage;
    }

    public Coverage updateCoverage(Coverage coverage, String newBenefitName,String description,Set<Benefit> benefits, UserDetails userDetails) {
        Admin admin = adminRoleAdapter.userToAdmin(userDetails);
        CoverageName coverageName  = new CoverageName(newBenefitName);
        boolean isCoverageNameUnique = coverageNameIsUnique.isSatisfiedBy(coverageName);
        Coverage updatedCoverage = admin.updateCoverage(coverage, newBenefitName,description, benefits,isCoverageNameUnique);
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

