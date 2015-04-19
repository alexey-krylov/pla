package com.pla.core.application.service;

import com.pla.core.domain.model.Region;
import org.joda.time.LocalDate;
import org.nthdimenzion.common.service.JpaRepositoryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * Created by User on 3/20/2015.
 */
@Service
public class RegionService {

    @Autowired
    private JpaRepositoryFactory jpaRepositoryFactory;

    private static final Logger LOGGER = LoggerFactory.getLogger(RegionService.class);

    @Autowired
    public RegionService(JpaRepositoryFactory jpaRepositoryFactory) {
        this.jpaRepositoryFactory = jpaRepositoryFactory;
    }

    @Transactional
    public void associateRegionalManager(String regionCode, String employeeId, String firstName, String lastName, LocalDate fromDate) {
        JpaRepository<Region, String> regionRepository = jpaRepositoryFactory.getCrudRepository(Region.class);
        Region region = regionRepository.findOne(regionCode);
        Region updatedRegion = region.assignRegionalManager(employeeId, firstName, lastName, fromDate);
        try {
            regionRepository.save(updatedRegion);
        } catch (RuntimeException e) {
            LOGGER.error("*****Saving Regional Manager failed*****", e);
            throw new RuntimeException(e.getMessage());
        }
    }

}
