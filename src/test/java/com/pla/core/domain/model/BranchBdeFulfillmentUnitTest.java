package com.pla.core.domain.model;

import org.joda.time.LocalDate;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

/**
 * Created by Admin on 4/24/2015.
 */
public class BranchBdeFulfillmentUnitTest {

    @Test
    public void givenBranchBde_thenItShouldCreateTheBranchBdeFulfillment(){
        BranchBde branchBde = new BranchBde("E001","first name","last name");
        BranchBdeFulfillment branchBdeFulfillment = new BranchBdeFulfillment(branchBde,new LocalDate("2015-10-10"));
        assertNotNull(branchBdeFulfillment);
        assertThat(new LocalDate("2015-10-10"),is(branchBdeFulfillment.getFromDate()));
    }

    @Test
    public void givenExpireFulfillmentDate_thenItShouldAssignTheDateToTheBranchBdeFulfillment(){
        BranchBde branchBde = new BranchBde("E001","first name","last name");
        BranchBdeFulfillment branchBdeFulfillment = new BranchBdeFulfillment(branchBde,new LocalDate("2015-10-10"));
        branchBdeFulfillment =   branchBdeFulfillment.expireFulfillment(new LocalDate("2016-01-01"));
        LocalDate expireFulfillmentDate = new LocalDate("2016-01-01");
        assertNotNull(branchBdeFulfillment);
        assertThat(new LocalDate("2015-10-10"),is(branchBdeFulfillment.getFromDate()));
        assertThat(expireFulfillmentDate,is(branchBdeFulfillment.getThruDate()));
    }
}
