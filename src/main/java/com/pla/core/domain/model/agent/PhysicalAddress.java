/*
 * Copyright (c) 3/13/15 9:24 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.domain.model.agent;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

import javax.persistence.*;

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
public class PhysicalAddress {


    private String physicalAddressLine1;

    private String physicalAddressLine2;

    @Embedded
    @AttributeOverrides({@AttributeOverride(name = "postalCode", column = @Column(name = "physicalAddressPostalCode")),
            @AttributeOverride(name = "province", column = @Column(name = "physicalAddressProvince")), @AttributeOverride(name = "city", column = @Column(name = "physicalAddressCity"))})
    private GeoDetail physicalGeoDetail;


    PhysicalAddress(String addressLine1, GeoDetail physicalGeoDetail) {
        checkArgument(isNotEmpty(addressLine1));
        checkArgument(physicalGeoDetail != null);
        this.physicalAddressLine1 = addressLine1;
        this.physicalGeoDetail = physicalGeoDetail;
    }


    public PhysicalAddress addAddressLine2(String addressLine2) {
        PhysicalAddress contactDetail = new PhysicalAddress(this.physicalAddressLine1, this.physicalGeoDetail);
        contactDetail.physicalAddressLine2 = addressLine2;
        return contactDetail;
    }

}
