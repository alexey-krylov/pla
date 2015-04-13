package com.pla.core.domain.service;

import com.pla.core.domain.model.Admin;
import com.pla.core.domain.model.Benefit;
import com.pla.core.domain.model.Coverage;
import com.pla.core.dto.CoverageDto;
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
        CoverageDto coverageDto  = new CoverageDto(coverageId,name,coverageCode);
        boolean isCodeOrNameIsUnique = coverageCodeIsUnique.Or(coverageNameIsUnique).isSatisfiedBy(coverageDto);
        Coverage coverage = admin.createCoverage(isCodeOrNameIsUnique,coverageId, name,coverageCode,description,benefitSet);
        return coverage;
    }

    public Coverage updateCoverage(Coverage coverage, String newCoverageName,String newCoverageCode,String description,Set<Benefit> benefits, UserDetails userDetails) {
        Admin admin = adminRoleAdapter.userToAdmin(userDetails);
        CoverageDto coverageDto  = new CoverageDto(coverage.getCoverageId().getCoverageId(), newCoverageName,newCoverageCode);
        boolean isCodeOrNameIsUnique = coverageCodeIsUnique.Or(coverageNameIsUnique).isSatisfiedBy(coverageDto);
        Coverage updatedCoverage = admin.updateCoverage(coverage, newCoverageName,newCoverageCode,description, benefits,isCodeOrNameIsUnique);
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

