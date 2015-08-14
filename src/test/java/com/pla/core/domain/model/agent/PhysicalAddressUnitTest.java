/*
 * Copyright (c) 3/25/15 9:24 AM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.domain.model.agent;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author: Samir
 * @since 1.0 25/03/2015
 */
public class PhysicalAddressUnitTest {

    private PhysicalAddress physicalAddress;

    private GeoDetail geoDetail = new GeoDetail(560068, "India", "Banaglore");

    @Before
    public void setUp() {
        physicalAddress = new PhysicalAddress("Bangalore", geoDetail);
    }

    @Test
    public void itShouldUpdateWithAddressLine2() {
        physicalAddress = physicalAddress.addAddressLine2("Kormangala");
        assertEquals("Kormangala", physicalAddress.getPhysicalAddressLine2());
        assertEquals(geoDetail, physicalAddress.getPhysicalGeoDetail());
        assertEquals("Bangalore", physicalAddress.getPhysicalAddressLine1());
    }
}
