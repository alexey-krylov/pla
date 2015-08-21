/*
 * Copyright (c) 3/25/15 9:13 AM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.domain.model.agent;

import com.pla.sharedkernel.domain.model.EmailAddress;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author: Samir
 * @since 1.0 25/03/2015
 */
public class ContactDetailUnitTest {

    private ContactDetail contactDetail;

    private GeoDetail geoDetail = new GeoDetail(560068, "India", "Banaglore");

    @Before
    public void setUp() {
        contactDetail = new ContactDetail("9916971271", new EmailAddress("abc@ef.com"), "Bangalore", geoDetail);
    }

    @Test
    public void itShouldUpdateWithHomePhoneNumber() {
        contactDetail = contactDetail.addHomePhoneNumber("08025745700");
        assertEquals("08025745700", contactDetail.getHomePhoneNumber());
        assertEquals("9916971271", contactDetail.getMobileNumber());
        assertEquals(new EmailAddress("abc@ef.com"), contactDetail.getEmailAddress());
        assertEquals(geoDetail, contactDetail.getGeoDetail());
        assertEquals("Bangalore", contactDetail.getAddressLine1());
    }

    @Test
    public void itShouldUpdateWithWorkPhoneNumber() {
        contactDetail = contactDetail.addWorkPhoneNumber("08025745700");
        assertEquals("08025745700", contactDetail.getWorkPhoneNumber());
        assertEquals("9916971271", contactDetail.getMobileNumber());
        assertEquals(new EmailAddress("abc@ef.com"), contactDetail.getEmailAddress());
        assertEquals(geoDetail, contactDetail.getGeoDetail());
        assertEquals("Bangalore", contactDetail.getAddressLine1());
    }


    @Test
    public void itShouldUpdateWithAddressLine2() {
        contactDetail = contactDetail.addAddressLine2("Kormangala");
        assertEquals("Kormangala", contactDetail.getAddressLine2());
        assertEquals("9916971271", contactDetail.getMobileNumber());
        assertEquals(new EmailAddress("abc@ef.com"), contactDetail.getEmailAddress());
        assertEquals(geoDetail, contactDetail.getGeoDetail());
        assertEquals("Bangalore", contactDetail.getAddressLine1());
    }
}
