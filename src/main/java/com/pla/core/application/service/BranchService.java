package com.pla.core.application.service;

import com.pla.core.domain.model.Branch;
import com.pla.core.domain.model.BranchCode;
import org.joda.time.LocalDate;
import org.nthdimenzion.common.service.JpaRepositoryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * Created by User on 3/20/2015.
 */
@Service
public class BranchService {

    @Autowired
    private JpaRepositoryFactory jpaRepositoryFactory;

    @Autowired
    public BranchService(JpaRepositoryFactory jpaRepositoryFactory) {
        this.jpaRepositoryFactory = jpaRepositoryFactory;
    }

    @Transactional
    public Branch updateBranchManager(String branchId, String branchManagerId, String firstName, String lastName, LocalDate fromDate) {
        JpaRepository<Branch, BranchCode> branchRepository = jpaRepositoryFactory.getCrudRepository(Branch.class);
        BranchCode branchCode = new BranchCode(branchId);
        Branch branch = branchRepository.findOne(branchCode);
        if ((branchManagerId.equals("Unassigned")) && (branch != null)) {
            branch.validateBranchManagerFromDate(branchManagerId, firstName, lastName, fromDate);
            branch = branch.expireBranchManager(branch.getCurrentBranchManager(), fromDate.plusDays(-1));
            branchRepository.save(branch);
            return branch;
        }
        branch = branch.assignBranchManager(branchManagerId, firstName, lastName, fromDate);
        branchRepository.save(branch);
        return branch;

    }

    @Transactional
    public Branch updateBranchBDE(String branchId, String branchBDEId, String firstName, String lastName, LocalDate fromDate) {
        JpaRepository<Branch, BranchCode> branchRepository = jpaRepositoryFactory.getCrudRepository(Branch.class);
        BranchCode branchCode = new BranchCode(branchId);
        Branch branch = branchRepository.findOne(branchCode);
        if ((branchBDEId.equals("Unassigned") && (branch != null))) {
            branch.validateBranchBDEFromDate(branchBDEId, firstName, lastName, fromDate);
            branch = branch.expireBranchBDE(branch.getCurrentBranchBdE(), fromDate.plusDays(-1));
            branchRepository.save(branch);
            return branch;
        }
        branch = branch.assignBranchBDE(branchBDEId, firstName, lastName, fromDate);
        branchRepository.save(branch);
        return branch;
    }
}
