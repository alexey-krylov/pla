package com.pla.core.domain.query;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.pla.core.query.MandatoryDocumentFinder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by Admin on 5/6/2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:queryTestContext.xml"})
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class})
public class MandatoryDocumentFinderUnitTest {

    @Autowired
    MandatoryDocumentFinder mandatoryDocumentFinder;

    @Test
    @DatabaseSetup(value = "classpath:testdata/endtoend/mandatorydocument/testdataformandatorydocumentwithplan.xml")
    public void givenPlanIdAndProcess_whenMandatoryDocumentDoesNotAssociatedWithAnyPlanAndProcess_thenItShouldReturnZERO(){
        int noOfMandatoryDocument = mandatoryDocumentFinder.getMandatoryDocumentCountBy("P004","CLAIM","C001");
        assertThat(noOfMandatoryDocument, is(0));
    }


    @Test
    @DatabaseSetup(value = "classpath:testdata/endtoend/mandatorydocument/testdataformandatorydocumentwithplan.xml")
    public void givenPlanIdAndProcess_whenMandatoryDocumentIsAssociatedWithAnyPlanAndProcess_thenItShouldReturnONE(){
        int noOfMandatoryDocument = mandatoryDocumentFinder.getMandatoryDocumentCountBy("P001","CLAIM","C001");
        assertThat(noOfMandatoryDocument, is(1));
    }

    @Test
    @DatabaseSetup(value = "classpath:testdata/endtoend/mandatorydocument/testdataformandatorydocumentwithplan.xml")
    public void givenPlanIdAndProcess_whenMandatoryDocumentIsAssociatedWithPlanAndProcessButWithSameCoverage_thenItShouldReturnZERO(){
        int noOfMandatoryDocument = mandatoryDocumentFinder.getMandatoryDocumentCountBy("P001","CLAIM","C002");
        assertThat(noOfMandatoryDocument, is(0));
    }

    @Test
     @DatabaseSetup(value = "classpath:testdata/endtoend/mandatorydocument/testdataformandatorydocumentwithplan.xml")
     public void givenPlanIdAndProcess_whenMandatoryDocumentIsExistedWithSamePlanAndProcess_thenItShouldReturnONE(){
        int noOfMandatoryDocument = mandatoryDocumentFinder.getMandatoryDocumentCountBy("P001","CLAIM",null);
        assertThat(noOfMandatoryDocument, is(1));
    }

    @Test
    @DatabaseSetup(value = "classpath:testdata/endtoend/mandatorydocument/testdataformandatorydocumentwithplan.xml")
    public void givenPlanIdAndProcess_whenMandatoryDocumentDoesNotExistedWithSamePlanAndProcess_thenItShouldReturnONE(){
        int noOfMandatoryDocument = mandatoryDocumentFinder.getMandatoryDocumentCountBy("P004","CLAIM",null);
        assertThat(noOfMandatoryDocument, is(0));
    }
}
