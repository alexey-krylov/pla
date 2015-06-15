package com.pla.core.domain.model.plan;

import com.google.common.collect.ImmutableSortedSet;
import com.pla.sharedkernel.domain.model.SumAssuredType;
import com.pla.sharedkernel.identifier.CoverageId;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.SortedSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.util.ReflectionTestUtils.invokeGetterMethod;

/**
 * Created by Admin on 4/23/2015.
 */
public class SumAssuredUnitTest {
    SortedSet<BigDecimal> sumAssuredValue;
    private BigDecimal minSumInsured;
    private BigDecimal maxSumInsured;

    @Before
    public void setUp(){
        sumAssuredValue = ImmutableSortedSet.of(new BigDecimal(10),new BigDecimal(20),new BigDecimal(30),new BigDecimal(40),new BigDecimal(50));
        minSumInsured = new BigDecimal(10);
        maxSumInsured = new BigDecimal(50);
    }

    @Test
    public void givenSumAssuredValues_thenItShouldCreateSumAssuredWithSumAssuredValues(){
        SumAssured sumAssured = new SumAssured(sumAssuredValue);
        assertNotNull(sumAssured);
        assertEquals(SumAssuredType.SPECIFIED_VALUES, invokeGetterMethod(sumAssured, "sumAssuredType"));
    }

    @Test
    public void givenMinAndMaxInsured_thenItShouldCreateSumAssuredWithMinAndMaxInsuredValues(){
        int multiplesOf = 10;
        BigDecimal expectedMinSumInsured = new BigDecimal(10);
        BigDecimal expectedMaxSumInsured = new BigDecimal(50);
        SumAssured sumAssured = new SumAssured(minSumInsured,maxSumInsured,multiplesOf);
        assertNotNull(sumAssured);
        assertEquals(expectedMinSumInsured, invokeGetterMethod(sumAssured, "minSumInsured"));
        assertEquals(expectedMaxSumInsured, invokeGetterMethod(sumAssured, "maxSumInsured"));
        assertEquals(SumAssuredType.RANGE, invokeGetterMethod(sumAssured, "sumAssuredType"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void givenMinAndMaxInsured_whenTheMinSumInsuredIsGreaterThanMaxSumInsured_thenItShouldThrowAnException(){
        int multiplesOf = 20;
        BigDecimal minSumInsured = new BigDecimal(40);
        BigDecimal maxSumInsured = new BigDecimal(30);
        SumAssured sumAssured = new SumAssured(minSumInsured,maxSumInsured,multiplesOf);
        assertNotNull(sumAssured);
    }

    @Test(expected = IllegalArgumentException.class)
    public void givenMinAndMaxInsured_whenTheMinSumInsuredOrThanMaxSumInsuredIsZero_thenItShouldThrowAnException(){
        int multiplesOf = 20;
        BigDecimal minSumInsured = BigDecimal.ZERO;
        BigDecimal maxSumInsured = BigDecimal.ZERO;
        SumAssured sumAssured = new SumAssured(minSumInsured,maxSumInsured,multiplesOf);
        assertNotNull(sumAssured);
    }


    @Test
    public void givenCoverageId_thenItShouldCreateSumAssuredWithCoverageId(){
        SumAssured sumAssured = new SumAssured(new CoverageId("C001"),30, BigInteger.ONE);
        assertNotNull(sumAssured);
        assertEquals(new CoverageId("C001"), invokeGetterMethod(sumAssured, "coverageId"));
        assertEquals(SumAssuredType.DERIVED, invokeGetterMethod(sumAssured, "sumAssuredType"));
    }

}
