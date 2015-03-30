package com.pla.core.application;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.pla.core.domain.model.ProcessType;
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

import java.util.List;
import java.util.Set;

/**
 * Created by Admin on 3/30/2015.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:queryTestContext.xml")
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class})
public class MandatoryDocumentAcceptanceTest {

    private Logger logger = LoggerFactory.getLogger(MandatoryDocumentAcceptanceTest.class);

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
    @ExpectedDatabase(value = "classpath:testdata/endtoend/mandatorydocument/expectedtestdataformandatorydocument.xml", assertionMode = DatabaseAssertionMode.NON_STRICT)
    public void givenProductCoverageAndProcessTypeAndListOfDocuments_whenTheProductIsExisted_thenItShouldCreateAMandatoryDocument(){
        Set<String> documents = Sets.newHashSet();
        documents.add("DOCUMENT_ONE");
        documents.add("DOCUMENT_TWO");
        CreateMandatoryDocumentCommand createMandatoryDocumentCommand = new CreateMandatoryDocumentCommand(userDetails,"P001","C001", ProcessType.CLAIM,documents);
        commandGateway.sendAndWait(createMandatoryDocumentCommand);
    }


    @Test
    @DatabaseSetup(value = "classpath:testdata/endtoend/mandatorydocument/testdataformandatorydocument.xml", type = DatabaseOperation.CLEAN_INSERT)
    @ExpectedDatabase(value = "classpath:testdata/endtoend/mandatorydocument/expectedtestdataforupdatedmandatorydocument.xml",assertionMode = DatabaseAssertionMode.NON_STRICT)
    @DatabaseTearDown(value = "classpath:testdata/endtoend/mandatorydocument/testdataformandatorydocument.xml",type = DatabaseOperation.TRUNCATE_TABLE)
    public void givenProductCoverageAndProcessTypeAndListOfDocuments_whenTheProductIsExisted_thenItShouldUpdateAMandatoryDocument(){
        Set<String> documents = Sets.newHashSet();
        documents.add("DOCUMENT_THREE");
        documents.add("DOCUMENT_FOUR");
        UpdateMandatoryDocumentCommand updateMandatoryDocumentCommand = new UpdateMandatoryDocumentCommand(userDetails,"1000",documents);
        commandGateway.sendAndWait(updateMandatoryDocumentCommand);
    }


}
