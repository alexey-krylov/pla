package com.pla.core.domain.model;

import org.joda.time.LocalDate;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

/**
 * Created by User on 3/24/2015.
 */
public class RegionUnitTest {
    @Test
    public void itShouldCreateNewRegionalManagerGivenRegionCode() {
        Region region = new Region("REG100", "SOUTH", "345");
        RegionalManager regionalManager = new RegionalManager("345", "SOUTH REGIONAL", "MANAGER");
        RegionalManagerFulfillment regionalManagerFulfillment = new RegionalManagerFulfillment(regionalManager, new LocalDate("2015-03-24").plusDays(2));

        Region updatedRegion = region.assignRegionalManager("345", "SOUTH REGIONAL", "MANAGER", new LocalDate("2015-03-24").plusDays(2));
        assertEquals("345", updatedRegion.getRegionalManager());
        assertEquals(regionalManagerFulfillment, updatedRegion.getRegionalManagerFulfillmentForARegionalManager("345"));

    }

    @Test
    public void itShouldUpdateExistingRegionalManagerGivenNewRegionalManagerCode() {
        Region region = new Region("REG100", "SOUTH", "345");
        RegionalManager regionalManager = new RegionalManager("789", "SOUTH REGIONAL", "MANAGER");
        RegionalManagerFulfillment regionalManagerFulfillment = new RegionalManagerFulfillment(regionalManager, new LocalDate().now().plusDays(2));
        region.addRegionalMangerFulfillment("789", regionalManagerFulfillment);
        region.assignRegionalManager("345", "SOUTH REGIONAL", "MANAGER", new LocalDate().now().plusDays(4));
        assertEquals(2, region.getRegionalManagerFulfillments().size());
        assertEquals(new LocalDate().now().plusDays(4).minusDays(1), region.getRegionalManagerFulfillmentForARegionalManager("789").getThruDate());
        assertEquals("345", region.getRegionalManager());
        region.assignRegionalManager("3245", "SOUTH REGIONAL", "MANAGER", new LocalDate().now().plusDays(6));
        assertEquals(3, region.getRegionalManagerFulfillments().size());
        assertEquals("3245", region.getRegionalManager());
    }

    @Test
    public void givenRegionCodeAndManagerName_thenItShouldCreateTheRegion1(){
        Region region = new Region("R001","RName","RManager");
        assertNotNull(region);
        assertThat("R001", is(region.getRegionCode()));
        assertThat("RName", is(region.getRegionName()));
        assertThat("RManager", is(region.getRegionalManager()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void givenRegionCodeAndManagerName_whenRegionIdIsNull_thenItShouldThrowAnException(){
        Region region = new Region(null,"RName","RManager");
        assertNotNull(region);
        assertThat("R001", is(region.getRegionCode()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void givenRegionCodeAndManagerName_whenManagerNameIsNull_thenItShouldThrowAnException(){
        Region region = new Region("R001","RName",null);
        assertNotNull(region);
        assertThat("R001", is(region.getRegionCode()));
    }

    @Test
    public void givenNewRegionalManagerFromDate_whenNewRegionalManagerFromDateIsAfterCurrentRegionalManagerFromDate_thenItShouldReturnTrue(){
        Region region = new Region("R001","RName","RManager");
        boolean isNewRegionalManagerFulfillmentValid = region.isNewRegionalManagerFulfillmentValid(new LocalDate("2015-07-01"), new LocalDate("2015-05-05"));
        assertTrue(isNewRegionalManagerFulfillmentValid);
    }

    @Test
    public void givenNewRegionalManagerFromDate_whenNewRegionalManagerFromDateIsBeforeCurrentRegionalManagerFromDate_thenItShouldReturnFalse(){
        Region region = new Region("R001","RName","RManager");
        boolean isNewRegionalManagerFulfillmentValid = region.isNewRegionalManagerFulfillmentValid(new LocalDate("2015-04-01"), new LocalDate("2015-05-05"));
        assertFalse(isNewRegionalManagerFulfillmentValid);
    }
}
