package com.pla.core.application;

import com.pla.core.domain.exception.CoverageException;
import com.pla.core.domain.model.Benefit;
import com.pla.core.domain.model.BenefitId;
import com.pla.core.domain.model.Coverage;
import com.pla.core.domain.service.CoverageService;
import com.pla.sharedkernel.identifier.CoverageId;
import org.axonframework.commandhandling.annotation.CommandHandler;
import org.nthdimenzion.common.service.JpaRepositoryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: Admin
 * Date: 3/22/15
 * Time: 8:45 PM
 * To change this template use File | Settings | File Templates.
 */
@Component
public class CoverageCommandHandler {

    private JpaRepositoryFactory jpaRepositoryFactory;

    private CoverageService coverageService;

    private static final Logger LOGGER = LoggerFactory.getLogger(CoverageCommandHandler.class);

    @Autowired
    public CoverageCommandHandler(JpaRepositoryFactory jpaRepositoryFactory, CoverageService coverageService) {
        this.jpaRepositoryFactory = jpaRepositoryFactory;
        this.coverageService = coverageService ;
    }

    @CommandHandler
    public void createCoverageHandler(CreateCoverageCommand createCoverageCommand) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("*****Command Received*****" + createCoverageCommand);
        }
        Set<Benefit> benefitSet= findBenefitById(createCoverageCommand.getBenefitIds());
        JpaRepository<Coverage, String> coverageRepository = jpaRepositoryFactory.getCrudRepository(Coverage.class);
        Coverage coverage = coverageService.createCoverage(createCoverageCommand.getCoverageName(),createCoverageCommand.getDescription(), benefitSet, createCoverageCommand.getUserDetails());
        try {
            coverageRepository.save(coverage);
        } catch (RuntimeException e) {
            LOGGER.error("*****Saving coverage failed*****", e);
            throw new CoverageException(e.getMessage());
        }
    }

    @CommandHandler
    public void updateCoverageHandler(UpdateCoverageCommand updateCoverageCommand) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("*****Command Received*****" + updateCoverageCommand);
        }
        JpaRepository<Coverage, CoverageId> coverageRepository = jpaRepositoryFactory.getCrudRepository(Coverage.class);
        CoverageId coverageId = new CoverageId(updateCoverageCommand.getCoverageId());
        Coverage coverage = coverageRepository.findOne(coverageId);
        Set<Benefit> benefitSet= findBenefitById(updateCoverageCommand.getBenefitIds());
        coverage = coverageService.updateCoverage(coverage, updateCoverageCommand.getCoverageName(),updateCoverageCommand.getDescription(),benefitSet, updateCoverageCommand.getUserDetails());
        try {
            coverageRepository.save(coverage);
        } catch (RuntimeException e) {
            LOGGER.error("*****Updating coverage failed*****", e);
            throw new CoverageException(e.getMessage());
        }
    }

    @CommandHandler
    public void markCoverageAsUsedHandler(MarkCoverageAsUsedCommand markCoverageAsUsedCommand) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("*****Command Received*****" + markCoverageAsUsedCommand);
        }
        CrudRepository<Coverage, CoverageId> coverageRepository = jpaRepositoryFactory.getCrudRepository(Coverage.class);
        CoverageId coverageId = new CoverageId(markCoverageAsUsedCommand.getCoverageId());
        Coverage coverage = coverageRepository.findOne(coverageId);
        coverage = coverageService.markCoverageAsUsed(coverage);
        try {
            coverageRepository.save(coverage);
        } catch (RuntimeException e) {
            LOGGER.error("*****Marking coverage as used failed*****", e);
            throw new CoverageException(e.getMessage());
        }
    }

    @CommandHandler
    public void inactivateCoverageHandler(InactivateCoverageCommand inactivateCoverageCommand) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("*****Inactivate coverage Status Command  Received*****" + inactivateCoverageCommand);
        }
        CrudRepository<Coverage, CoverageId> coverageRepository = jpaRepositoryFactory.getCrudRepository(Coverage.class);
        CoverageId coverageId = new CoverageId(inactivateCoverageCommand.getCoverageId());
        Coverage coverage = coverageRepository.findOne(coverageId);
        coverage = coverageService.inactivateCoverage(coverage, inactivateCoverageCommand.getUserDetails());
        try {
            coverageRepository.save(coverage);
        } catch (RuntimeException e) {
            LOGGER.error("*****Inactivating coverage failed*****", e);
            throw new CoverageException(e.getMessage());
        }
    }

    private Set<Benefit> findBenefitById(Set<BenefitId> benefitIds){
        JpaRepository<Benefit, BenefitId> benefitRepository = jpaRepositoryFactory.getCrudRepository(Benefit.class);
        Set<Benefit> benefitSet = new HashSet<>();
        for (BenefitId benefitId : benefitIds) {
            Benefit benefit = benefitRepository.findOne(benefitId);
            if(benefit!=null) {
                benefitSet.add(benefit);
            }
        }
        return benefitSet;
    }
}
