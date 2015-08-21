package com.pla.core.domain.model;

import org.joda.time.LocalDate;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by Nischitha on 3/24/2015.
 */
public class BranchUnitTest {
    @Test
    public void givenBranchEmployeeIdItShouldAssignNewBranchManager() {
        Branch branch = new Branch(new BranchCode("BRANCHCODE12"), new BranchName("LivingStone"), null, null);
        branch.assignBranchManager("213", "BRNCHMNGR FN", "BRNCHMNGR LN", LocalDate.now());
        assertEquals(branch.getBranchManagerFulfillments().size(), 1);
        assertEquals("213", branch.getCurrentBranchManager());
    }

    @Test
    public void givenBranchEmployeeIdItShouldAddNewBranchManagerAndExpireCurrentBranchManager() {
        Branch branch = new Branch(new BranchCode("BRANCHCODE12"), new BranchName("LivingStone"), null, null);
        branch.assignBranchManager("213", "BRNCHMNGR FN", "BRNCHMNGR LN", LocalDate.now());
        branch.assignBranchManager("445", "BRNCHMNGR FN2", "BRNCHMNGR LN2", LocalDate.now().plusDays(1));
        assertEquals(branch.getBranchManagerFulfillments().size(), 2);
        assertEquals("445", branch.getCurrentBranchManager());
        assertEquals(LocalDate.now(), branch.getBranchManagerFulfillmentForABranchManager("213").getThruDate());
    }

    @Test
    public void givenBranchEmployeeIdItShouldAssignNewBranchBDE() {
        Branch branch = new Branch(new BranchCode("BRANCHCODE12"), new BranchName("LivingStone"), "employeeId445", null);
        branch.assignBranchBDE("778", "BRNCHBDE FN", "BRNCHBDE LN", LocalDate.now());
        assertEquals(branch.getBranchBDEFulfillments().size(), 1);
        assertEquals("778", branch.getCurrentBranchBdE());
    }

    @Test
    public void givenBranchEmployeeIdItShouldUpdateExistingBranchBDEAndExpireCurrentBranchBDE() {
        Branch branch = new Branch(new BranchCode("BRANCHCODE12"), new BranchName("LivingStone"), "employeeId445", null);
        branch.assignBranchBDE("778", "BRNCHBDE FN", "BRNCHBDE LN", LocalDate.now());
        branch.assignBranchBDE("889", "BRNCHBDE FN2", "BRNCHBDE LN2", LocalDate.now().plusDays(1));
        assertEquals(branch.getBranchBDEFulfillments().size(), 2);
        assertEquals("889", branch.getCurrentBranchBdE());
        assertEquals(LocalDate.now(), branch.getBranchBDEFulfillmentForABranchBDE("778").getThruDate());
    }

    @Test
    public void givenBranchManagerIdItShouldAddBranchManagerFulFillment() {
        Branch branch = new Branch(new BranchCode("BRANCHCODE12"), new BranchName("LivingStone"), null, null);
        BranchManager branchManager = new BranchManager("90000", "Nischitha", "Kurunji");
        BranchManagerFulfillment branchManagerFulfillment = new BranchManagerFulfillment(branchManager, LocalDate.now().plusDays(1));
        BranchManager branchManager1 = new BranchManager("90001", "Nischitha1", "Kurunji1");
        BranchManagerFulfillment branchManagerFulfillment1 = new BranchManagerFulfillment(branchManager1, LocalDate.now().plusDays(1));
        branch.addBranchManagerFulfillment("90000", branchManagerFulfillment);
        branch.addBranchManagerFulfillment("90001", branchManagerFulfillment1);
        assertEquals(branchManagerFulfillment, branch.getBranchManagerFulfillmentForABranchManager("90000"));
        assertEquals(branchManagerFulfillment1, branch.getBranchManagerFulfillmentForABranchManager("90001"));

    }

    @Test
    public void givenBranchManagerIdAItShouldExpireTheRespectiveBranchManagerFulFillment() {
        Branch branch = new Branch(new BranchCode("BRANCHCODE12"), new BranchName("LivingStone"), null, null);
        BranchManager branchManager = new BranchManager("90000", "Nischitha", "Kurunji");
        BranchManagerFulfillment branchManagerFulfillment = new BranchManagerFulfillment(branchManager, LocalDate.now().plusDays(1));
        BranchManager branchManager1 = new BranchManager("90001", "Nischitha1", "Kurunji1");
        BranchManagerFulfillment branchManagerFulfillment1 = new BranchManagerFulfillment(branchManager1, LocalDate.now().plusDays(1));
        BranchManager branchManager2 = new BranchManager("90002", "Nischitha2", "Kurunji2");
        BranchManagerFulfillment branchManagerFulfillment2 = new BranchManagerFulfillment(branchManager1, LocalDate.now().plusDays(1));
        branch.addBranchManagerFulfillment("90000", branchManagerFulfillment);
        branch.addBranchManagerFulfillment("90001", branchManagerFulfillment1);
        branch.addBranchManagerFulfillment("90002", branchManagerFulfillment2);
        branch.expireBranchManagerFulfillment(branch.getBranchManagerFulfillments(), branchManager, LocalDate.now());
        assertEquals(LocalDate.now(), branch.getBranchManagerFulfillmentForABranchManager("90000").getThruDate());
    }

    @Test
    public void givenBranchManagerIdAItShouldExpireTheRespectiveBranchManager() {
        Branch branch = new Branch(new BranchCode("BRANCHCODE12"), new BranchName("LivingStone"), null, null);
        BranchManager branchManager = new BranchManager("90000", "Nischitha", "Kurunji");
        BranchManagerFulfillment branchManagerFulfillment = new BranchManagerFulfillment(branchManager, LocalDate.now().plusDays(1));
        BranchManager branchManager1 = new BranchManager("90001", "Nischitha1", "Kurunji1");
        BranchManagerFulfillment branchManagerFulfillment1 = new BranchManagerFulfillment(branchManager1, LocalDate.now().plusDays(1));
        BranchManager branchManager2 = new BranchManager("90002", "Nischitha2", "Kurunji2");
        BranchManagerFulfillment branchManagerFulfillment2 = new BranchManagerFulfillment(branchManager2, LocalDate.now().plusDays(1));
        branch.addBranchManagerFulfillment("90000", branchManagerFulfillment);
        branch.addBranchManagerFulfillment("90001", branchManagerFulfillment1);
        branch.addBranchManagerFulfillment("90002", branchManagerFulfillment2);
        branch.expireBranchManager("90000", LocalDate.now());
        assertEquals(LocalDate.now(), branch.getBranchManagerFulfillmentForABranchManager("90000").getThruDate());
    }

    @Test
    public void givenBranchBDEIdItShouldAddBranchBDEFulFillment() {
        Branch branch = new Branch(new BranchCode("BRANCHCODE12"), new BranchName("LivingStone"), null, null);
        BranchBde branchBde = new BranchBde("90000", "Nischitha", "Kurunji");
        BranchBdeFulfillment branchBdeFulfillment = new BranchBdeFulfillment(branchBde, LocalDate.now().plusDays(1));
        BranchBde branchBde1 = new BranchBde("90001", "Nischitha1", "Kurunji1");
        BranchBdeFulfillment branchBdeFulfillment1 = new BranchBdeFulfillment(branchBde1, LocalDate.now().plusDays(1));
        branch.addBranchBDEFulfillment("90000", branchBdeFulfillment);
        branch.addBranchBDEFulfillment("90001", branchBdeFulfillment1);
        assertEquals(branchBdeFulfillment, branch.getBranchBDEFulfillmentForABranchBDE("90000"));
        assertEquals(branchBdeFulfillment1, branch.getBranchBDEFulfillmentForABranchBDE("90001"));

    }

    @Test
    public void givenBranchBDEIdAItShouldExpireTheRespectiveBranchBDEFulFillment() {
        Branch branch = new Branch(new BranchCode("BRANCHCODE12"), new BranchName("LivingStone"), null, null);
        BranchBde branchBde = new BranchBde("90000", "Nischitha", "Kurunji");
        BranchBdeFulfillment branchBdeFulfillment = new BranchBdeFulfillment(branchBde, LocalDate.now().plusDays(1));
        BranchBde branchBde1 = new BranchBde("90001", "Nischitha1", "Kurunji1");
        BranchBdeFulfillment branchBdeFulfillment1 = new BranchBdeFulfillment(branchBde1, LocalDate.now().plusDays(1));
        BranchBde branchBde2 = new BranchBde("90002", "Nischitha2", "Kurunji2");
        BranchBdeFulfillment branchBdeFulfillment2 = new BranchBdeFulfillment(branchBde2, LocalDate.now().plusDays(1));
        branch.addBranchBDEFulfillment("90000", branchBdeFulfillment);
        branch.addBranchBDEFulfillment("90001", branchBdeFulfillment1);
        branch.addBranchBDEFulfillment("90002", branchBdeFulfillment2);
        branch.expireBranchBDEFulfillment(branch.getBranchBDEFulfillments(), branchBde, LocalDate.now());
        assertEquals(LocalDate.now(), branch.getBranchBDEFulfillmentForABranchBDE("90000").getThruDate());
    }

    @Test
    public void givenBranchBDEIdAItShouldExpireTheRespectiveBranchBDE() {
        Branch branch = new Branch(new BranchCode("BRANCHCODE12"), new BranchName("LivingStone"), null, null);
        BranchBde branchBde = new BranchBde("90000", "Nischitha", "Kurunji");
        BranchBdeFulfillment branchBdeFulfillment = new BranchBdeFulfillment(branchBde, LocalDate.now().plusDays(1));
        BranchBde branchBde1 = new BranchBde("90001", "Nischitha1", "Kurunji1");
        BranchBdeFulfillment branchBdeFulfillment1 = new BranchBdeFulfillment(branchBde1, LocalDate.now().plusDays(1));
        BranchBde branchBde2 = new BranchBde("90002", "Nischitha2", "Kurunji2");
        BranchBdeFulfillment branchBdeFulfillment2 = new BranchBdeFulfillment(branchBde2, LocalDate.now().plusDays(1));
        branch.addBranchBDEFulfillment("90000", branchBdeFulfillment);
        branch.addBranchBDEFulfillment("90001", branchBdeFulfillment1);
        branch.addBranchBDEFulfillment("90002", branchBdeFulfillment2);
        branch.expireBranchBDE("90000", LocalDate.now());
        assertEquals(LocalDate.now(), branch.getBranchBDEFulfillmentForABranchBDE("90000").getThruDate());
    }


}
