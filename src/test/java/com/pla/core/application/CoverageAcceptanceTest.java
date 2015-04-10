package com.pla.core.application;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;
import com.google.common.collect.Lists;
import com.pla.core.domain.model.BenefitId;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nthdimenzion.security.service.UserLoginDetailDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertTrue;

/**
 * Created by Admin on 3/24/2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:queryTestContext.xml")
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class})
public class CoverageAcceptanceTest {

    private Logger logger = LoggerFactory.getLogger(CoverageAcceptanceTest.class);

    private UserDetails userDetails;

    @Autowired
    private CommandGateway commandGateway;


    @Before
    public void setUp() {
        UserLoginDetailDto userLoginDetailDto = UserLoginDetailDto.createUserLoginDetailDto("", "");
        List<String> permissions = Lists.newArrayList();
        permissions.add("ROLE_ADMIN");
        userDetails = userLoginDetailDto.populateAuthorities(permissions);
    }

    @Test
    @DatabaseSetup(value = "classpath:testdata/endtoend/coverage/testbenefitdataforcreatecoverage.xml",type = DatabaseOperation.CLEAN_INSERT)
    @ExpectedDatabase(value = "classpath:testdata/endtoend/coverage/expectedcoveragedata.xml", assertionMode = DatabaseAssertionMode.NON_STRICT)
//    @DatabaseTearDown(value = "classpath:testdata/endtoend/coverage/testbenefitdataforcreatecoverage.xml",type = DatabaseOperation.TRUNCATE_TABLE)
    public void givenACoverageNameAndSetOfBenefit_whenTheCoverageNameIsUniqueAndUserHasAdminRole_thenCreateACoverage() {

        Set<BenefitId> backendIdSet = new HashSet<>();
        backendIdSet.add(new BenefitId("1"));
        backendIdSet.add(new BenefitId("2"));
        CreateCoverageCommand createCoverageCommand = new CreateCoverageCommand(userDetails,"testing Coverage name","C_ONE","coverage description",backendIdSet);
        Boolean isSuccess = Boolean.FALSE;
        try {
            commandGateway.sendAndWait(createCoverageCommand);
            isSuccess = Boolean.TRUE;
        } catch (Exception e) {
            logger.error("Error in creating coverage", e);
        }
        assertTrue(isSuccess);

    }


    @Test
    @DatabaseSetup(value = "classpath:testdata/endtoend/coverage/testdataforupdatecoverage.xml",type = DatabaseOperation.CLEAN_INSERT)
    @ExpectedDatabase(value = "classpath:testdata/endtoend/coverage/expectedupdatedcoveragedata.xml", assertionMode = DatabaseAssertionMode.NON_STRICT)
    @DatabaseTearDown(value = "classpath:testdata/endtoend/coverage/testdataforupdatecoverage.xml",type = DatabaseOperation.DELETE)
    public void givenACoverageNameCoverageIdAndSetOfBenefitIds_whenTheCoverageNameIsUniqueAndUserHasAdminRole_thenUpdateTheCoverage() {

        Set<BenefitId> backendIdSet = new HashSet<>();
        backendIdSet.add(new BenefitId("1"));
        backendIdSet.add(new BenefitId("4"));
        UpdateCoverageCommand updateCoverageCommand = new UpdateCoverageCommand("C001","testing Coverage name after update","C_ONE","coverage description",backendIdSet,userDetails);
        Boolean isSuccess = Boolean.FALSE;
        try {
            commandGateway.sendAndWait(updateCoverageCommand);
            isSuccess = Boolean.TRUE;
        } catch (Exception e) {
            logger.error("Error in creating coverage", e);
        }
        assertTrue(isSuccess);
    }

    @Test
    @DatabaseSetup(value = "classpath:testdata/endtoend/coverage/testdataforinnactivecoverage.xml",type = DatabaseOperation.CLEAN_INSERT)
    @ExpectedDatabase(value = "classpath:testdata/endtoend/coverage/expectedinactivecoveragedata.xml", assertionMode = DatabaseAssertionMode.NON_STRICT)
    @DatabaseTearDown(value = "classpath:testdata/endtoend/coverage/testdataforinnactivecoverage.xml",type = DatabaseOperation.DELETE)
    public void givenACoverageId_whenTheCoverageStatusIsActiveAndUserHasAdminRole_thenMakeTheCoverageAsInactive() {

        InactivateCoverageCommand inactivateCoverageCommand = new InactivateCoverageCommand("C002",userDetails);
        Boolean isSuccess = Boolean.FALSE;
        try {
            commandGateway.sendAndWait(inactivateCoverageCommand);
            isSuccess = Boolean.TRUE;
        } catch (Exception e) {
            logger.error("Error in inactivating coverage", e);
        }
        assertTrue(isSuccess);

    }

    @Test
    @DatabaseSetup(value = "classpath:testdata/endtoend/coverage/testdataformarkcoverageinuse.xml",type = DatabaseOperation.CLEAN_INSERT)
    @ExpectedDatabase(value = "classpath:testdata/endtoend/coverage/expectedtestdataformakecoverageasinuse.xml", assertionMode = DatabaseAssertionMode.NON_STRICT)
//    @DatabaseTearDown(value = "classpath:testdata/endtoend/coverage/testdataformarkcoverageinuse.xml",type = DatabaseOperation.DELETE)
    public void givenACoverageId_whenTheCoverageStatusActive_thenMarkCoverageAsInUse() {

        MarkCoverageAsUsedCommand markCoverageAsUsedCommand = new MarkCoverageAsUsedCommand("C003");
        Boolean isSuccess = Boolean.FALSE;
        try {
            commandGateway.sendAndWait(markCoverageAsUsedCommand);
            isSuccess = Boolean.TRUE;
        } catch (Exception e) {
            logger.error("Error in marking coverage", e);
        }
        assertTrue(isSuccess);

    }
}
