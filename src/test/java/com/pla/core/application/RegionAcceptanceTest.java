/*
 * Copyright (c) 3/5/15 6:28 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.application;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;
import com.pla.core.application.service.RegionService;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import static org.junit.Assert.assertTrue;

/**
 * @author: Nischitha
 * @since 1.0 03/23/2015
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:queryTestContext.xml")
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class})
public class RegionAcceptanceTest {
    private Logger logger = LoggerFactory.getLogger(RegionAcceptanceTest.class);

    @Autowired
    private RegionService regionService;

    @Test
    @DatabaseSetup(value = "classpath:testdata/endtoend/region/testdataforaddregionalmanager.xml")
    @ExpectedDatabase(value = "classpath:testdata/endtoend/region/expecteddataforaddregionalmanager.xml", assertionMode = DatabaseAssertionMode.NON_STRICT)
    public void givenRegionCodeItShouldAssignNewRegionalManager() {
        Boolean isSuccess = Boolean.FALSE;
        try {
            regionService.associateRegionalManager("REGION100", "345", "RM FN", "RM LN", new LocalDate("2015-03-18"));
            isSuccess = Boolean.TRUE;
        } catch (Exception e) {
            logger.error("Error in updating regional manager", e);
        }
        assertTrue(isSuccess);

    }
    @Test
    @DatabaseSetup(value = "classpath:testdata/endtoend/region/testdataforupdateregionalmanager.xml")
    @ExpectedDatabase(value = "classpath:testdata/endtoend/region/expecteddataforupdateregionalmanager.xml", assertionMode = DatabaseAssertionMode.NON_STRICT)
    public void givenRegionCodeItShouldAssignNewRegionalManagerForExistingRegionalManager() {
        Boolean isSuccess = Boolean.FALSE;
        try {
            regionService.associateRegionalManager("REGION100", "745", "RM FN2", "RM LN2", new LocalDate("2015-03-18"));
            isSuccess = Boolean.TRUE;
        } catch (Exception e) {
            logger.error("Error in updating regional manager", e);
        }
        assertTrue(isSuccess);

    }
}