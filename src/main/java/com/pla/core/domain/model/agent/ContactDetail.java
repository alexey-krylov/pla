/*
 * Copyright (c) 3/13/15 8:53 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.domain.model.agent;

import com.pla.sharedkernel.domain.model.EmailAddress;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;

import static com.google.common.base.Preconditions.checkArgument;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * @author: Samir
 * @since 1.0 13/03/2015
 */
@ValueObject
@Immutable
@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class ContactDetail {

    private Integer mobileNumber;

    private Integer homePhoneNumber;

    private Integer workPhoneNumber;

    @Embedded
    private EmailAddress emailAddress;

    private String addressLine1;

    private String addressLine2;

    @Embedded
    private GeoDetail geoDetail;


    ContactDetail(Integer mobileNumber, EmailAddress emailAddress, String addressLine1, GeoDetail geoDetail) {
        checkArgument(emailAddress != null);
        checkArgument(isNotEmpty(addressLine1));
        checkArgument(geoDetail != null);
        this.mobileNumber = mobileNumber;
        this.emailAddress = emailAddress;
        this.addressLine1 = addressLine1;
        this.geoDetail = geoDetail;
    }

    public ContactDetail addHomePhoneNumber(Integer homePhoneNumber) {
        ContactDetail contactDetail = new ContactDetail(this.mobileNumber, this.emailAddress, this.addressLine1, this.geoDetail);
        contactDetail.homePhoneNumber = homePhoneNumber;
        contactDetail.workPhoneNumber = this.workPhoneNumber;
        contactDetail.addressLine2 = this.addressLine2;
        return contactDetail;
    }

    public ContactDetail addWorkPhoneNumber(Integer workPhoneNumber) {
        ContactDetail contactDetail = new ContactDetail(this.mobileNumber, this.emailAddress, this.addressLine1, this.geoDetail);
        contactDetail.homePhoneNumber = this.homePhoneNumber;
        contactDetail.workPhoneNumber = workPhoneNumber;
        contactDetail.addressLine2 = this.addressLine2;
        return contactDetail;
    }

    public ContactDetail addAddressLine2(String addressLine2) {
        ContactDetail contactDetail = new ContactDetail(this.mobileNumber, this.emailAddress, this.addressLine1, this.geoDetail);
        contactDetail.homePhoneNumber = this.homePhoneNumber;
        contactDetail.workPhoneNumber = this.workPhoneNumber;
        contactDetail.addressLine2 = addressLine2;
        return contactDetail;
    }

    public ContactDetail updateContactDetail(Integer mobileNumber, EmailAddress emailAddress, String addressLine1, GeoDetail geoDetail) {
        checkArgument(emailAddress != null);
        checkArgument(isNotEmpty(addressLine1));
        checkArgument(geoDetail != null);
        ContactDetail contactDetail = new ContactDetail(mobileNumber, emailAddress, addressLine1, geoDetail);
        return contactDetail;
    }
}
