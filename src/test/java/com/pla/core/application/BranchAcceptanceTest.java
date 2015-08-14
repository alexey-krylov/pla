/*
 * Copyright (c) 3/5/15 6:28 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.application;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;
import com.pla.core.application.service.BranchService;
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
public class BranchAcceptanceTest {
    private Logger logger = LoggerFactory.getLogger(BranchAcceptanceTest.class);

    @Autowired
    private BranchService branchService;

    @Test
    @DatabaseSetup(value = "classpath:testdata/endtoend/branch/testdataforaddbranchmanager.xml", type = DatabaseOperation.CLEAN_INSERT)
    @ExpectedDatabase(value = "classpath:testdata/endtoend/branch/expecteddataforaddbranchmanager.xml", assertionMode = DatabaseAssertionMode.NON_STRICT)
    @DatabaseTearDown(value = "classpath:testdata/endtoend/branch/expecteddataforaddbranchmanager.xml", type = DatabaseOperation.DELETE_ALL)
    public void givenBranchCodeItShouldAssignNewBranchManager() {
        Boolean isSuccess = Boolean.FALSE;
        try {
            branchService.updateBranchManager("BRANCH100", "345", "BRNCH FN", "BRNCH LN", new LocalDate("2015-03-18"));
            isSuccess = Boolean.TRUE;
        } catch (Exception e) {
            logger.error("Error in updating branch manager", e);
        }
        assertTrue(isSuccess);

    }

    @Test
    @DatabaseSetup(value = "classpath:testdata/endtoend/branch/testdataforaddbranchbde.xml", type = DatabaseOperation.CLEAN_INSERT)
    @ExpectedDatabase(value = "classpath:testdata/endtoend/branch/expecteddataforaddbranchbde.xml", assertionMode = DatabaseAssertionMode.NON_STRICT)
    @DatabaseTearDown(value = "classpath:testdata/endtoend/branch/expecteddataforaddbranchbde.xml", type = DatabaseOperation.DELETE_ALL)
    public void givenBranchCodeItShouldAssignNewBranchBde() {
        Boolean isSuccess = Boolean.FALSE;
        try {
            branchService.updateBranchBDE("BRANCHBDE100", "900", "BRNCHBDE FN", "BRNCHBDE LN", new LocalDate("2015-03-18"));
            isSuccess = Boolean.TRUE;
        } catch (Exception e) {
            logger.error("Error in updating branch bde", e);
        }
        assertTrue(isSuccess);

    }

    @Test
    @DatabaseSetup(value = "classpath:testdata/endtoend/branch/testdataforupdatebranchmanager.xml", type = DatabaseOperation.CLEAN_INSERT)
    @ExpectedDatabase(value = "classpath:testdata/endtoend/branch/expecteddataforupdatebranchmanager.xml", assertionMode = DatabaseAssertionMode.NON_STRICT)
    @DatabaseTearDown(value = "classpath:testdata/endtoend/branch/expecteddataforupdatebranchmanager.xml", type = DatabaseOperation.DELETE_ALL)
    public void givenBranchCodeItShouldAssignNewBranchManagerForExistingBranchManager() {
        Boolean isSuccess = Boolean.FALSE;
        try {
            branchService.updateBranchManager("BRANCH1200", "678", "BRNCH FN2", "BRNCH LN2", new LocalDate("2015-03-18"));
            isSuccess = Boolean.TRUE;
        } catch (Exception e) {
            logger.error("Error in updating branch manager", e);
        }
        assertTrue(isSuccess);

    }

    @Test
    @DatabaseSetup(value = "classpath:testdata/endtoend/branch/testdataforupdatebranchbde.xml", type = DatabaseOperation.CLEAN_INSERT)
    @ExpectedDatabase(value = "classpath:testdata/endtoend/branch/expecteddataforupdatebranchbde.xml", assertionMode = DatabaseAssertionMode.NON_STRICT)
    @DatabaseTearDown(value = "classpath:testdata/endtoend/branch/expecteddataforupdatebranchbde.xml", type = DatabaseOperation.DELETE_ALL)
    public void givenBranchCodeItShouldAssignNewBranchManagerForExistingBranchBde() {
        Boolean isSuccess = Boolean.FALSE;
        try {
            branchService.updateBranchBDE("BRANCHBDE100", "800", "BRNCHBDE FN2", "BRNCHBDE LN2", new LocalDate("2015-03-18"));
            isSuccess = Boolean.TRUE;
        } catch (Exception e) {
            logger.error("Error in updating branch bde", e);
        }
        assertTrue(isSuccess);

    }
    @Test
    @DatabaseSetup(value = "classpath:testdata/endtoend/branch/testdataforupdatebranchmanager.xml", type = DatabaseOperation.CLEAN_INSERT)
    @ExpectedDatabase(value = "classpath:testdata/endtoend/branch/expecteddataforupdatebdeonlyrforexistingmanager.xml", assertionMode = DatabaseAssertionMode.NON_STRICT)
    @DatabaseTearDown(value = "classpath:testdata/endtoend/branch/expecteddataforupdatebdeonlyrforexistingmanager.xml", type = DatabaseOperation.DELETE_ALL)

    public void givenBranchBdeEmployeeIdWithoutBranchManagerEmployeeIdItShouldUpdateBdeOnly() {
        Boolean isSuccess = Boolean.FALSE;
        try {
            branchService.updateBranchBDE("BRANCH1200", "000", "BRNCHBDE FN", "BRNCHBDE LN", new LocalDate("2015-03-24"));
            //branchService.updateBranchManager("BRANCH1200", "", "", "", new LocalDate("2015-03-24"));
            isSuccess = Boolean.TRUE;
        } catch (Exception e) {
            logger.error("Error in updating branch bde", e);
        }
        assertTrue(isSuccess);

    }


    @Test
    @DatabaseSetup(value = "classpath:testdata/endtoend/branch/testdataforupdatebranchbde.xml", type = DatabaseOperation.CLEAN_INSERT)
    @ExpectedDatabase(value = "classpath:testdata/endtoend/branch/expecteddataforupdatemanagerforexistingbde.xml", assertionMode = DatabaseAssertionMode.NON_STRICT)
    @DatabaseTearDown(value = "classpath:testdata/endtoend/branch/expecteddataforupdatemanagerforexistingbde.xml", type = DatabaseOperation.DELETE_ALL)
    public void givenBranchManagerEmployeeIdItShouldUpdateBranchManagerForExistingBranchBde() {
        Boolean isSuccess = Boolean.FALSE;
        try {
            branchService.updateBranchBDE("BRANCHBDE100", "900", "BRNCHBDE FN", "BRNCHBDE LN", new LocalDate("2015-03-15"));
            branchService.updateBranchManager("BRANCHBDE100", "475", "BRNCH FN", "BRNCH LN", new LocalDate("2015-03-18"));
            isSuccess = Boolean.TRUE;
        } catch (Exception e) {
            logger.error("Error in updating branch bde", e);
        }
        assertTrue(isSuccess);

    }
    @Test
    @DatabaseSetup(value = "classpath:testdata/endtoend/branch/testdataforupdatebranchmanager.xml", type = DatabaseOperation.CLEAN_INSERT)
    @ExpectedDatabase(value = "classpath:testdata/endtoend/branch/expecteddataforupdatebderforexistingmanager.xml", assertionMode = DatabaseAssertionMode.NON_STRICT)
    @DatabaseTearDown(value = "classpath:testdata/endtoend/branch/expecteddataforupdatebderforexistingmanager.xml", type = DatabaseOperation.DELETE_ALL)
    public void givenBranchBdeEmployeeIdItShouldUpdateBdeForExistingBranchManager() {
        Boolean isSuccess = Boolean.FALSE;
        try {
            branchService.updateBranchBDE("BRANCH1200", "000", "BRNCHBDE FN", "BRNCHBDE LN", new LocalDate("2015-03-18"));
            branchService.updateBranchManager("BRANCH1200", "3425", "BRNCH FN", "BRNCH FN", new LocalDate("2015-03-18"));
            isSuccess = Boolean.TRUE;
        } catch (Exception e) {
            logger.error("Error in updating branch bde", e);
        }
        assertTrue(isSuccess);

    }

    @Test
    @DatabaseSetup(value = "classpath:testdata/endtoend/branch/testdataforupdatebranchbde.xml", type = DatabaseOperation.CLEAN_INSERT)
    @ExpectedDatabase(value = "classpath:testdata/endtoend/branch/expecteddataforupdatemanageronlyrforexistingbde.xml", assertionMode = DatabaseAssertionMode.NON_STRICT)
    @DatabaseTearDown(value = "classpath:testdata/endtoend/branch/expecteddataforupdatemanageronlyrforexistingbde.xml", type = DatabaseOperation.DELETE_ALL)
    public void givenBranchManagerEmployeeIdWithoutBranchBdeEmployeeIdItShouldUpdateManagerOnly() {
        Boolean isSuccess = Boolean.FALSE;
        try {
            //branchService.updateBranchBDE("BRANCHBDE100", "", "", "", new LocalDate("2015-03-29"));
            branchService.updateBranchManager("BRANCHBDE100", "111", "BRNCH FN", "BRNCH LN", new LocalDate("2015-03-29"));
            isSuccess = Boolean.TRUE;
        } catch (Exception e) {
            logger.error("Error in updating branch bde", e);
        }
        assertTrue(isSuccess);

    }

}