package com.pla.core.domain.model;

import org.joda.time.LocalDate;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

/**
 * Created by Admin on 4/24/2015.
 */
public class BranchManagerFulfillmentUnitTest {

    @Test
    public void givenBranchManagerAndFromDate_thenItShouldCreateTheBranchManagerFulfillment(){
        BranchManager branchManager = new BranchManager("E001","First Name","Last Name");
        BranchManagerFulfillment branchManagerFulfillment = new BranchManagerFulfillment(branchManager,new LocalDate("2015-01-01"));
        assertNotNull(branchManagerFulfillment);
        assertThat(new LocalDate("2015-01-01"),is(branchManagerFulfillment.getFromDate()));
    }

    @Test
    public void givenTheExpireDate_thenItShouldAssignTheExpireDateToTheBranchManager(){
        BranchManager branchManager = new BranchManager("E001","First Name","Last Name");
        BranchManagerFulfillment branchManagerFulfillment = new BranchManagerFulfillment(branchManager,new LocalDate("2015-01-01"));
        branchManagerFulfillment = branchManagerFulfillment.expireFulfillment(new LocalDate("2016-01-10"));
        LocalDate expireDate  = new LocalDate("2016-01-10");
        assertNotNull(branchManagerFulfillment);
        assertThat(new LocalDate("2015-01-01"),is(branchManagerFulfillment.getFromDate()));
        assertThat(expireDate,is(branchManagerFulfillment.getThruDate()));
    }

}
